package no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller;

import java.util.Objects;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.Behandlinger;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.tjenester.DokumentHendelseTjeneste;
import no.nav.foreldrepenger.fpformidling.vedtak.Vedtaksbrev;
import no.nav.vedtak.exception.FunksjonellException;
import no.nav.vedtak.exception.TekniskException;

@ApplicationScoped
class DokumentMalUtleder {

    private static final String UTVIKLERFEIL_INGEN_ENDRING_SAMMEN = "Utviklerfeil: Det skal ikke være mulig å ha INGEN_ENDRING sammen med andre konsekvenser. BehandlingUuid: ";
    private DomeneobjektProvider domeneobjektProvider;
    private DokumentHendelseTjeneste dokumentHendelseTjeneste;
    private Behandlinger behandlingRestKlient;

    public DokumentMalUtleder() {
        //CDI
    }

    @Inject
    public DokumentMalUtleder(DomeneobjektProvider domeneobjektProvider,
                              DokumentHendelseTjeneste dokumentHendelseTjeneste,
                              Behandlinger behandlingRestKlient) {
        this.domeneobjektProvider = domeneobjektProvider;
        this.dokumentHendelseTjeneste = dokumentHendelseTjeneste;
        this.behandlingRestKlient = behandlingRestKlient;
    }

    private static boolean erKunEndringIFordelingAvYtelsen(Behandlingsresultat behandlingsresultat) {
        return behandlingsresultat.getKonsekvenserForYtelsen().contains(KonsekvensForYtelsen.ENDRING_I_FORDELING_AV_YTELSEN)
            && behandlingsresultat.getKonsekvenserForYtelsen().size() == 1;
    }

    private DokumentMalType mapEngangstønadVedtaksbrev(Behandling behandling) {
        var behandlingsresultat = behandling.getBehandlingsresultat();
        if (behandlingsresultat.erInnvilget()) {
            return DokumentMalType.ENGANGSSTØNAD_INNVILGELSE;
        } else if (behandlingsresultat.erOpphørt() || behandlingsresultat.erAvslått()) {
            return DokumentMalType.ENGANGSSTØNAD_AVSLAG;
        }
        throw ukjentMalException(behandling);
    }

    private DokumentMalType mapForeldrepengerVedtaksbrev(Behandling behandling, boolean opprinneligFritekstBrev) {
        var behandlingsresultat = behandling.getBehandlingsresultat();
        if (behandlingsresultat.erForeldrepengerSenere()) {
            return DokumentMalType.FORELDREPENGER_ANNULLERT;
        } else if (skalBenytteInnvilgelsesbrev(behandlingsresultat)) {
            return DokumentMalType.FORELDREPENGER_INNVILGELSE;
        } else if (behandlingsresultat.erAvslått()) {
            return DokumentMalType.FORELDREPENGER_AVSLAG;
        } else if (behandlingsresultat.erOpphørt()) {
            return DokumentMalType.FORELDREPENGER_OPPHØR;
        } else if (opprinneligFritekstBrev && erVedtakMedEndringIYtelse(behandlingsresultat)) {
            return DokumentMalType.ENDRING_UTBETALING;
        }
        throw ukjentMalException(behandling);
    }

    private DokumentMalType mapSvangerskapspengerVedtaksbrev(Behandling behandling) {
        var behandlingsresultat = behandling.getBehandlingsresultat();
        if (skalBenytteInnvilgelsesbrev(behandlingsresultat)) {
            return DokumentMalType.SVANGERSKAPSPENGER_INNVILGELSE;
        } else if (behandlingsresultat.erOpphørt()) {
            return DokumentMalType.SVANGERSKAPSPENGER_OPPHØR;
        } else if (behandlingsresultat.erAvslått()) {
            return DokumentMalType.SVANGERSKAPSPENGER_AVSLAG;
        }
        throw ukjentMalException(behandling);
    }

    private boolean skalBenytteInnvilgelsesbrev(Behandlingsresultat behandlingsresultat) {
        return behandlingsresultat.erInnvilget() || (behandlingsresultat.erEndretForeldrepenger() && !erKunEndringIFordelingAvYtelsen(
            behandlingsresultat));
    }

    private boolean erVedtakMedEndringIYtelse(Behandlingsresultat behandlingsresultat) {
        return behandlingsresultat.erEndretForeldrepenger() && erKunEndringIFordelingAvYtelsen(behandlingsresultat);
    }

    DokumentMalType utledDokumentmal(Behandling behandling, DokumentHendelse hendelse) {
        if (hendelse.getDokumentMalType() != null) {
            return hendelse.getDokumentMalType();
        }
        if (Boolean.TRUE.equals(hendelse.isGjelderVedtak())) {
            return utledVedtaksbrev(behandling, hendelse);
        }
        throw ukjentMalException(behandling);
    }

    private TekniskException ukjentMalException(Behandling behandling) {
        return new TekniskException("FPFORMIDLING-666915",
            String.format("Ingen brevmal konfigurert for denne type behandlingen %s.", behandling.getUuid().toString()));
    }

    private DokumentMalType utledVedtaksbrev(Behandling behandling, DokumentHendelse hendelse) {
        if (!Objects.equals(Vedtaksbrev.AUTOMATISK, hendelse.getVedtaksbrev())
            && Objects.equals(Vedtaksbrev.FRITEKST, behandling.getBehandlingsresultat().getVedtaksbrev())) {
            return DokumentMalType.FRITEKSTBREV;
        }
        return utledDokumentType(behandling, hendelse.getYtelseType(), false);
    }

    DokumentMalType utledDokumentType(Behandling behandling, FagsakYtelseType ytelseType, boolean opprinneligFritekstBrev) {
        if (BehandlingType.KLAGE.equals(behandling.getBehandlingType())) {
            return mapKlageBrev(behandling);
        } else if (erRevurderingMedUendretUtfall(behandling)) {
            return DokumentMalType.INGEN_ENDRING;
        } else if (FagsakYtelseType.FORELDREPENGER.equals(ytelseType)) {
            return mapForeldrepengerVedtaksbrev(behandling, opprinneligFritekstBrev);
        } else if (FagsakYtelseType.ENGANGSTØNAD.equals(ytelseType)) {
            return mapEngangstønadVedtaksbrev(behandling);
        } else if (FagsakYtelseType.SVANGERSKAPSPENGER.equals(ytelseType)) {
            return mapSvangerskapspengerVedtaksbrev(behandling);
        }
        throw new TekniskException("FPFORMIDLING-666916",
            String.format("Ingen brevmal for ytelse %s for behandling %s.", ytelseType.getKode(), behandling.getUuid().toString()));
    }

    private boolean erRevurderingMedUendretUtfall(Behandling behandling) {
        if (!BehandlingType.REVURDERING.equals(behandling.getBehandlingType())) {
            return false;
        }
        var behandlingsresultat = behandling.getBehandlingsresultat();

        var erRevurderingMedUendretUtfall = behandlingsresultat.erRevurderingMedUendretUtfall();

        var konsekvenserForYtelsen = behandlingsresultat.getKonsekvenserForYtelsen();
        var ingenKonsekvensForYtelsen = konsekvenserForYtelsen.contains(KonsekvensForYtelsen.INGEN_ENDRING);
        if (ingenKonsekvensForYtelsen && konsekvenserForYtelsen.size() > 1) {
            throw new IllegalStateException(UTVIKLERFEIL_INGEN_ENDRING_SAMMEN + behandling.getUuid());
        }
        return erRevurderingMedUendretUtfall || ingenKonsekvensForYtelsen || erKunEndringIFordelingAvYtelsenOgHarSendtVarselOmRevurdering(behandling);
    }

    private boolean erKunEndringIFordelingAvYtelsenOgHarSendtVarselOmRevurdering(Behandling behandling) {
        var behandlingsresultat = behandling.getBehandlingsresultat();
        return behandlingsresultat != null && foreldrepengerErEndret(behandlingsresultat) && erKunEndringIFordelingAvYtelsen(behandlingsresultat)
            && harSendtVarselOmRevurdering(behandling);
    }

    private boolean harSendtVarselOmRevurdering(Behandling behandling) {
        return dokumentHendelseTjeneste.erDokumentHendelseMottatt(behandling.getUuid(), DokumentMalType.VARSEL_OM_REVURDERING) || Boolean.TRUE.equals(
            behandlingRestKlient.harSendtVarselOmRevurdering(behandling.getResourceLinker()).orElse(false));
    }

    private boolean foreldrepengerErEndret(Behandlingsresultat behandlingsresultat) {
        return BehandlingResultatType.FORELDREPENGER_ENDRET.equals(behandlingsresultat.getBehandlingResultatType());
    }

    private DokumentMalType mapKlageBrev(Behandling behandling) {
        var klage = domeneobjektProvider.hentKlagebehandling(behandling);
        var klageVurderingResultat = klage.getGjeldendeKlageVurderingsresultat();
        if (klageVurderingResultat == null) {
            throw new FunksjonellException("FPFORMIDLING-100507",
                String.format("Klagebehandling med id %s mangler resultat av klagevurderingen", behandling.getUuid().toString()),
                "Fortsett saksbehandlingen");
        }

        return switch (klageVurderingResultat.klageVurdering()) {
            case MEDHOLD_I_KLAGE -> DokumentMalType.KLAGE_OMGJORT;
            case AVVIS_KLAGE -> DokumentMalType.KLAGE_AVVIST;
            case UDEFINERT, OPPHEVE_YTELSESVEDTAK, STADFESTE_YTELSESVEDTAK, HJEMSENDE_UTEN_Å_OPPHEVE -> throw ukjentMalException(behandling);
        };
    }
}

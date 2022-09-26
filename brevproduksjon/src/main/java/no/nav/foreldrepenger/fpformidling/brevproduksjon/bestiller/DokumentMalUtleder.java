package no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.Behandlinger;
import no.nav.foreldrepenger.fpformidling.klage.Klage;
import no.nav.foreldrepenger.fpformidling.klage.KlageVurdering;
import no.nav.foreldrepenger.fpformidling.klage.KlageVurderingResultat;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.tjenester.DokumentHendelseTjeneste;
import no.nav.foreldrepenger.fpformidling.vedtak.Vedtaksbrev;
import no.nav.vedtak.exception.FunksjonellException;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.felles.integrasjon.rest.NativeClient;

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
                              @NativeClient Behandlinger behandlingRestKlient) {
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
        throw new TekniskException("FPFORMIDLING-666915",
        String.format("Ingen brevmal konfigurert for denne type behandlingen %s.", behandling.getUuid().toString()));
    }

    private DokumentMalType mapForeldrepengerVedtaksbrev(Behandling behandling) {
        var behandlingsresultat = behandling.getBehandlingsresultat();
        if (behandlingsresultat.erForeldrepengerSenere()) {
            return DokumentMalType.FORELDREPENGER_ANNULLERT;
        } else if (skalBenytteInnvilgelsesbrev(behandlingsresultat)) {
            return DokumentMalType.FORELDREPENGER_INNVILGELSE;
        } else if (behandlingsresultat.erAvslått()) {
            return DokumentMalType.FORELDREPENGER_AVSLAG;
        } else if (behandlingsresultat.erOpphørt()) {
            return DokumentMalType.FORELDREPENGER_OPPHØR;
        }
        throw new TekniskException("FPFORMIDLING-666915",
        String.format("Ingen brevmal konfigurert for denne type behandlingen %s.", behandling.getUuid().toString()));
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
        throw new TekniskException("FPFORMIDLING-666915",
        String.format("Ingen brevmal konfigurert for denne type behandlingen %s.", behandling.getUuid().toString()));
    }

    private boolean skalBenytteInnvilgelsesbrev(Behandlingsresultat behandlingsresultat) {
        return behandlingsresultat.erInnvilget() || (behandlingsresultat.erEndretForeldrepenger() &&
                !erKunEndringIFordelingAvYtelsen(behandlingsresultat));
    }

    DokumentMalType utledDokumentmal(Behandling behandling, DokumentHendelse hendelse) {
        if (hendelse.getDokumentMalType() != null) {
            return hendelse.getDokumentMalType();
        }
        if (Boolean.TRUE.equals(hendelse.isGjelderVedtak())) {
            return utledVedtaksbrev(behandling, hendelse);
        }
        throw new TekniskException("FPFORMIDLING-666915",
        String.format("Ingen brevmal konfigurert for denne type behandlingen %s.", behandling.getUuid().toString()));
    }

    private DokumentMalType utledVedtaksbrev(Behandling behandling, DokumentHendelse hendelse) {
        if (!Objects.equals(hendelse.getVedtaksbrev(), Vedtaksbrev.AUTOMATISK) &&
                Objects.equals(behandling.getBehandlingsresultat().getVedtaksbrev(), Vedtaksbrev.FRITEKST)) {
            return DokumentMalType.FRITEKSTBREV;
        }
        if (BehandlingType.KLAGE.equals(behandling.getBehandlingType())) {
            return mapKlageBrev(behandling);
        } else if (erRevurderingMedUendretUtfall(behandling)) {
            return DokumentMalType.INGEN_ENDRING;
        } else if (FagsakYtelseType.FORELDREPENGER.equals(hendelse.getYtelseType())) {
            return mapForeldrepengerVedtaksbrev(behandling);
        } else if (FagsakYtelseType.ENGANGSTØNAD.equals(hendelse.getYtelseType())) {
            return mapEngangstønadVedtaksbrev(behandling);
        } else if (FagsakYtelseType.SVANGERSKAPSPENGER.equals(hendelse.getYtelseType())) {
            return mapSvangerskapspengerVedtaksbrev(behandling);
        }
        throw new TekniskException("FPFORMIDLING-666915",
        String.format("Ingen brevmal for ytelse %s for behandling %s.", hendelse.getYtelseType().getKode(), behandling.getUuid().toString()));
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
        return erRevurderingMedUendretUtfall || ingenKonsekvensForYtelsen
                || erKunEndringIFordelingAvYtelsenOgHarSendtVarselOmRevurdering(behandling);
    }

    private boolean erKunEndringIFordelingAvYtelsenOgHarSendtVarselOmRevurdering(Behandling behandling) {
        var behandlingsresultat = behandling.getBehandlingsresultat();
        return behandlingsresultat != null &&
                foreldrepengerErEndret(behandlingsresultat)
                && erKunEndringIFordelingAvYtelsen(behandlingsresultat)
                && harSendtVarselOmRevurdering(behandling);
    }

    private boolean harSendtVarselOmRevurdering(Behandling behandling) {
        return dokumentHendelseTjeneste.erDokumentHendelseMottatt(behandling.getUuid(), DokumentMalType.VARSEL_OM_REVURDERING)
                || Boolean.TRUE.equals(behandlingRestKlient.harSendtVarselOmRevurdering(behandling.getResourceLinker()).orElse(false));
    }

    private boolean foreldrepengerErEndret(Behandlingsresultat behandlingsresultat) {
        return BehandlingResultatType.FORELDREPENGER_ENDRET.equals(behandlingsresultat.getBehandlingResultatType());
    }

    private DokumentMalType mapKlageBrev(Behandling behandling) {
        var klage = domeneobjektProvider.hentKlagebehandling(behandling);
        var klageVurderingResultat = klage.getGjeldendeKlageVurderingsresultat();
        if (klageVurderingResultat == null) {
            throw new FunksjonellException("FPFORMIDLING-100507",
            String.format("Klagebehandling med id %s mangler resultat av klagevurderingen", behandling.getUuid().toString()), "Fortsett saksbehandlingen");
        }
        var klagevurdering = klageVurderingResultat.klageVurdering();

        if (KlageVurdering.AVVIS_KLAGE.equals(klagevurdering)) {
            return DokumentMalType.KLAGE_AVVIST;
        } else if (Arrays.asList(KlageVurdering.OPPHEVE_YTELSESVEDTAK, KlageVurdering.HJEMSENDE_UTEN_Å_OPPHEVE).contains(klagevurdering)) {
            return DokumentMalType.KLAGE_HJEMSENDT;
        } else if (KlageVurdering.MEDHOLD_I_KLAGE.equals(klagevurdering)) {
            return DokumentMalType.KLAGE_OMGJORT;
        } else if (KlageVurdering.STADFESTE_YTELSESVEDTAK.equals(klagevurdering)) {
            return DokumentMalType.KLAGE_STADFESTET;
        }

        throw new TekniskException("FPFORMIDLING-666915",
        String.format("Ingen brevmal konfigurert for denne type behandlingen %s.", behandling.getUuid().toString()));
    }
}

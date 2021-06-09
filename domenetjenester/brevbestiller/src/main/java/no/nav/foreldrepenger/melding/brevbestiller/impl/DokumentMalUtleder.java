package no.nav.foreldrepenger.melding.brevbestiller.impl;

import static no.nav.foreldrepenger.melding.brevbestiller.impl.DokgenLanseringTjeneste.overstyrMalHvisNødvendig;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.BehandlingRestKlient;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.historikk.DokumentHistorikkinnslag;
import no.nav.foreldrepenger.melding.historikk.HistorikkRepository;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.klage.KlageVurdering;
import no.nav.foreldrepenger.melding.klage.KlageVurderingResultat;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.melding.vedtak.Vedtaksbrev;
import no.nav.vedtak.exception.FunksjonellException;
import no.nav.vedtak.exception.TekniskException;

@ApplicationScoped
class DokumentMalUtleder {

    private static final String UTVIKLERFEIL_INGEN_ENDRING_SAMMEN = "Utviklerfeil: Det skal ikke være mulig å ha INGEN_ENDRING sammen med andre konsekvenser. BehandlingUuid: ";
    private DomeneobjektProvider domeneobjektProvider;
    private HistorikkRepository historikkRepository;
    private BehandlingRestKlient behandlingRestKlient;
    private DokgenLanseringTjeneste dokgenLanseringTjeneste;

    public DokumentMalUtleder() {
        //CDI
    }

    @Inject
    public DokumentMalUtleder(DomeneobjektProvider domeneobjektProvider,
                              HistorikkRepository historikkRepository,
                              BehandlingRestKlient behandlingRestKlient,
                              DokgenLanseringTjeneste dokgenLanseringTjeneste) {
        this.domeneobjektProvider = domeneobjektProvider;
        this.historikkRepository = historikkRepository;
        this.behandlingRestKlient = behandlingRestKlient;
        this.dokgenLanseringTjeneste = dokgenLanseringTjeneste;
    }

    private static boolean erKunEndringIFordelingAvYtelsen(Behandlingsresultat behandlingsresultat) {
        return behandlingsresultat.getKonsekvenserForYtelsen().contains(KonsekvensForYtelsen.ENDRING_I_FORDELING_AV_YTELSEN)
                && behandlingsresultat.getKonsekvenserForYtelsen().size() == 1;
    }

    private DokumentMalType mapEngangstønadVedtaksbrev(Behandling behandling) {
        Behandlingsresultat behandlingsresultat = behandling.getBehandlingsresultat();
        if (behandlingsresultat.erInnvilget()) {
            return DokumentMalType.INNVILGELSE_ENGANGSSTØNAD;
        } else if (behandlingsresultat.erOpphørt() || behandlingsresultat.erAvslått()) {
                return DokumentMalType.AVSLAG_ENGANGSSTØNAD;
        }
        throw new TekniskException("FPFORMIDLING-666915",
        String.format("Ingen brevmal konfigurert for denne type behandlingen %s.", behandling.getUuid().toString()));
    }

    private DokumentMalType mapForeldrepengerVedtaksbrev(Behandling behandling) {
        Behandlingsresultat behandlingsresultat = behandling.getBehandlingsresultat();
        if (skalBenytteInnvilgelsesbrev(behandlingsresultat)) {
            return dokgenLanseringTjeneste.velgInnvilgelseFpMal(behandling);
        } else if (behandlingsresultat.erAvslått()) {
            return DokumentMalType.AVSLAG_FORELDREPENGER_DOK;
        } else if (behandlingsresultat.erOpphørt()) {
            return DokumentMalType.OPPHØR_DOK;
        }
        throw new TekniskException("FPFORMIDLING-666915",
        String.format("Ingen brevmal konfigurert for denne type behandlingen %s.", behandling.getUuid().toString()));
    }

    private DokumentMalType mapSvangerskapspengerVedtaksbrev(Behandling behandling) {
        Behandlingsresultat behandlingsresultat = behandling.getBehandlingsresultat();
        if (skalBenytteInnvilgelsesbrev(behandlingsresultat)) {
            return DokumentMalType.INNVILGELSE_SVANGERSKAPSPENGER_DOK;
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
            return overstyrMalHvisNødvendig(hendelse.getDokumentMalType());
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
            return DokumentMalType.FRITEKST_DOK;
        }
        if (BehandlingType.KLAGE.equals(behandling.getBehandlingType())) {
            return mapKlageBrev(behandling);
        } else if (erRevurderingMedUendretUtfall(behandling)) {
            return DokumentMalType.UENDRETUTFALL_DOK;
        } else if (FagsakYtelseType.FORELDREPENGER.equals(hendelse.getYtelseType())) {
            return mapForeldrepengerVedtaksbrev(behandling);
        } else if (FagsakYtelseType.ENGANGSTØNAD.equals(hendelse.getYtelseType())) {
            return mapEngangstønadVedtaksbrev(behandling);
        } else if (FagsakYtelseType.SVANGERSKAPSPENGER.equals(hendelse.getYtelseType())) {
            return mapSvangerskapspengerVedtaksbrev(behandling);
        }
        throw new TekniskException("FPFORMIDLING-666915",
        String.format("Kjenner ikke igjen ytelse %s for behandling %s.", hendelse.getYtelseType().getKode(), behandling.getUuid().toString()));
    }

    private boolean erRevurderingMedUendretUtfall(Behandling behandling) {
        if (!BehandlingType.REVURDERING.equals(behandling.getBehandlingType())) {
            return false;
        }
        Behandlingsresultat behandlingsresultat = behandling.getBehandlingsresultat();

        Boolean erRevurderingMedUendretUtfall = behandlingsresultat.erRevurderingMedUendretUtfall();

        List<KonsekvensForYtelsen> konsekvenserForYtelsen = behandlingsresultat.getKonsekvenserForYtelsen();
        boolean ingenKonsekvensForYtelsen = konsekvenserForYtelsen.contains(KonsekvensForYtelsen.INGEN_ENDRING);
        if (ingenKonsekvensForYtelsen && konsekvenserForYtelsen.size() > 1) {
            throw new IllegalStateException(UTVIKLERFEIL_INGEN_ENDRING_SAMMEN + behandling.getUuid());
        }
        return erRevurderingMedUendretUtfall || ingenKonsekvensForYtelsen
                || erKunEndringIFordelingAvYtelsenOgHarSendtVarselOmRevurdering(behandling);
    }

    private boolean erKunEndringIFordelingAvYtelsenOgHarSendtVarselOmRevurdering(Behandling behandling) {
        Behandlingsresultat behandlingsresultat = behandling.getBehandlingsresultat();
        return behandlingsresultat != null &&
                foreldrepengerErEndret(behandlingsresultat)
                && erKunEndringIFordelingAvYtelsen(behandlingsresultat)
                && harSendtVarselOmRevurdering(behandling);
    }

    private boolean harSendtVarselOmRevurdering(Behandling behandling) {
        return historikkRepository.hentInnslagForBehandling(behandling.getUuid())
                .stream().map(DokumentHistorikkinnslag::getDokumentMalType)
                .anyMatch(d -> DokumentMalType.REVURDERING_DOK.equals(d) || DokumentMalType.VARSEL_OM_REVURDERING.equals(d))
                || Boolean.TRUE.equals(behandlingRestKlient.harSendtVarselOmRevurdering(behandling.getResourceLinker()).orElse(false));
    }

    private boolean foreldrepengerErEndret(Behandlingsresultat behandlingsresultat) {
        return BehandlingResultatType.FORELDREPENGER_ENDRET.equals(behandlingsresultat.getBehandlingResultatType());
    }

    private DokumentMalType mapKlageBrev(Behandling behandling) {
        Klage klage = domeneobjektProvider.hentKlagebehandling(behandling);
        KlageVurderingResultat klageVurderingResultat = klage.getGjeldendeKlageVurderingsresultat();
        if (klageVurderingResultat == null) {
            throw new FunksjonellException("FPFORMIDLING-100507",
            String.format("Klagebehandling med id %s mangler resultat av klagevurderingen", behandling.getUuid().toString()), "Fortsett saksbehandlingen");
        }
        KlageVurdering klagevurdering = klageVurderingResultat.klageVurdering();

        if (KlageVurdering.AVVIS_KLAGE.equals(klagevurdering)) {
            return DokumentMalType.KLAGE_AVVIST;
        } else if (Arrays.asList(KlageVurdering.OPPHEVE_YTELSESVEDTAK, KlageVurdering.HJEMSENDE_UTEN_Å_OPPHEVE).contains(klagevurdering)) {
            return DokumentMalType.KLAGE_HJEMSENDT;
        } else if (KlageVurdering.MEDHOLD_I_KLAGE.equals(klagevurdering)) {
            return DokumentMalType.KLAGE_OMGJØRING;
        } else if (KlageVurdering.STADFESTE_YTELSESVEDTAK.equals(klagevurdering)) {
            return DokumentMalType.KLAGE_STADFESTET;
        }

        throw new TekniskException("FPFORMIDLING-666915",
        String.format("Ingen brevmal konfigurert for denne type behandlingen %s.", behandling.getUuid().toString()));
    }
}

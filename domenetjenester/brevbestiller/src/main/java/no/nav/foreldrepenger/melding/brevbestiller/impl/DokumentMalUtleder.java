package no.nav.foreldrepenger.melding.brevbestiller.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.BehandlingRestKlient;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingResultatType;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.melding.datamapper.DokumentBestillerFeil;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.historikk.DokumentHistorikkinnslag;
import no.nav.foreldrepenger.melding.historikk.HistorikkRepository;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.klage.KlageVurdering;
import no.nav.foreldrepenger.melding.klage.KlageVurderingResultat;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkTabellRepository;
import no.nav.foreldrepenger.melding.vedtak.Vedtaksbrev;

@ApplicationScoped
class DokumentMalUtleder {

    private static final String UTVIKLERFEIL_INGEN_ENDRING_SAMMEN = "Utviklerfeil: Det skal ikke være mulig å ha INGEN_ENDRING sammen med andre konsekvenser. BehandlingUuid: ";

    private KodeverkTabellRepository kodeverkTabellRepository;
    private DomeneobjektProvider domeneobjektProvider;
    private HistorikkRepository historikkRepository;
    private BehandlingRestKlient behandlingRestKlient;

    public DokumentMalUtleder() {
        //CDI
    }

    @Inject
    public DokumentMalUtleder(KodeverkTabellRepository kodeverkTabellRepository,
                              DomeneobjektProvider domeneobjektProvider,
                              HistorikkRepository historikkRepository,
                              BehandlingRestKlient behandlingRestKlient) {
        this.kodeverkTabellRepository = kodeverkTabellRepository;
        this.domeneobjektProvider = domeneobjektProvider;
        this.historikkRepository = historikkRepository;
        this.behandlingRestKlient = behandlingRestKlient;
    }

    private static boolean erKunEndringIFordelingAvYtelsen(Behandlingsresultat behandlingsresultat) {
        return behandlingsresultat.getKonsekvenserForYtelsen().contains(KonsekvensForYtelsen.ENDRING_I_FORDELING_AV_YTELSEN)
                && behandlingsresultat.getKonsekvenserForYtelsen().size() == 1;
    }

    private DokumentMalType mapEngangstønadVedtaksbrev(Behandling behandling) {
        Behandlingsresultat behandlingsresultat = behandling.getBehandlingsresultat();
        if (behandlingsresultat.erInnvilget()) {
            return kodeverkTabellRepository.finnDokumentMalType(DokumentMalType.POSITIVT_VEDTAK_DOK);
        } else if (behandlingsresultat.erOpphørt() || behandlingsresultat.erAvslått()) {
            return kodeverkTabellRepository.finnDokumentMalType(DokumentMalType.AVSLAGSVEDTAK_DOK);
        }
        throw DokumentBestillerFeil.FACTORY.ingenBrevmalKonfigurert(behandling.getUuid().toString()).toException();
    }

    private DokumentMalType mapForeldrepengerVedtaksbrev(Behandling behandling) {
        Behandlingsresultat behandlingsresultat = behandling.getBehandlingsresultat();
        if (skalBenytteInnvilgelsesbrev(behandlingsresultat)) {
            return kodeverkTabellRepository.finnDokumentMalType(DokumentMalType.INNVILGELSE_FORELDREPENGER_DOK);
        } else if (behandlingsresultat.erAvslått()) {
            return kodeverkTabellRepository.finnDokumentMalType(DokumentMalType.AVSLAG_FORELDREPENGER_DOK);
        } else if (behandlingsresultat.erOpphørt()) {
            return kodeverkTabellRepository.finnDokumentMalType(DokumentMalType.OPPHØR_DOK);
        }
        throw DokumentBestillerFeil.FACTORY.ingenBrevmalKonfigurert(behandling.getUuid().toString()).toException();
    }

    private DokumentMalType mapSvangerskapspengerVedtaksbrev(Behandling behandling) {
        Behandlingsresultat behandlingsresultat = behandling.getBehandlingsresultat();
        if (skalBenytteInnvilgelsesbrev(behandlingsresultat)) {
            return kodeverkTabellRepository.finnDokumentMalType(DokumentMalType.INNVILGELSE_SVANGERSKAPSPENGER_DOK);
        }
        throw DokumentBestillerFeil.FACTORY.ingenBrevmalKonfigurert(behandling.getUuid().toString()).toException();
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
        throw DokumentBestillerFeil.FACTORY.ingenBrevmalKonfigurert(behandling.getUuid().toString()).toException();
    }

    private DokumentMalType utledVedtaksbrev(Behandling behandling, DokumentHendelse hendelse) {
        if (Objects.equals(behandling.getBehandlingsresultat().getVedtaksbrev(), Vedtaksbrev.FRITEKST)) {
            return kodeverkTabellRepository.finnDokumentMalType(DokumentMalType.FRITEKST_DOK);
        }
        if (BehandlingType.KLAGE.equals(behandling.getBehandlingType())) {
            return mapKlageBrev(behandling);
        } else if (erRevurderingMedUendretUtfall(behandling)) {
            return kodeverkTabellRepository.finnDokumentMalType(DokumentMalType.UENDRETUTFALL_DOK);
        } else if (FagsakYtelseType.FORELDREPENGER.equals(hendelse.getYtelseType())) {
            return mapForeldrepengerVedtaksbrev(behandling);
        } else if (FagsakYtelseType.ENGANGSTØNAD.equals(hendelse.getYtelseType())) {
            return mapEngangstønadVedtaksbrev(behandling);
        } else if (FagsakYtelseType.SVANGERSKAPSPENGER.equals(hendelse.getYtelseType())) {
            return mapSvangerskapspengerVedtaksbrev(behandling);
        }
        throw DokumentBestillerFeil.FACTORY.kjennerIkkeYtelse(hendelse.getYtelseType().getKode(), behandling.getUuid().toString()).toException();
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
                .anyMatch(mal -> mal.getKode().equals(DokumentMalType.REVURDERING_DOK))
                || Boolean.TRUE.equals(behandlingRestKlient.harSendtVarselOmRevurdering(behandling.getResourceLinker()).orElse(false));
    }

    private boolean foreldrepengerErEndret(Behandlingsresultat behandlingsresultat) {
        return BehandlingResultatType.FORELDREPENGER_ENDRET.equals(behandlingsresultat.getBehandlingResultatType());
    }

    private DokumentMalType mapKlageBrev(Behandling behandling) {
        Klage klage = domeneobjektProvider.hentKlagebehandling(behandling);
        KlageVurderingResultat klageVurderingResultat = klage.getGjeldendeKlageVurderingsresultat();
        if (klageVurderingResultat == null) {
            throw DokumentBestillerFeil.FACTORY.behandlingManglerKlageVurderingResultat(behandling.getUuid().toString()).toException();
        }
        KlageVurdering klagevurdering = klageVurderingResultat.getKlageVurdering();
        if (KlageVurdering.AVVIS_KLAGE.equals(klagevurdering)) {
            return kodeverkTabellRepository.finnDokumentMalType(DokumentMalType.KLAGE_AVVIST_DOK);
        } else if (Arrays.asList(KlageVurdering.OPPHEVE_YTELSESVEDTAK, KlageVurdering.HJEMSENDE_UTEN_Å_OPPHEVE).contains(klagevurdering)) {
            return kodeverkTabellRepository.finnDokumentMalType(DokumentMalType.KLAGE_YTELSESVEDTAK_OPPHEVET_DOK);
        } else if (KlageVurdering.MEDHOLD_I_KLAGE.equals(klagevurdering)) {
            return kodeverkTabellRepository.finnDokumentMalType(DokumentMalType.VEDTAK_MEDHOLD);
        } else if (KlageVurdering.STADFESTE_YTELSESVEDTAK.equals(klagevurdering)) {
            return kodeverkTabellRepository.finnDokumentMalType(DokumentMalType.KLAGE_YTELSESVEDTAK_STADFESTET_DOK);
        }
        throw DokumentBestillerFeil.FACTORY.ingenBrevmalKonfigurert(behandling.getUuid().toString()).toException();
    }
}

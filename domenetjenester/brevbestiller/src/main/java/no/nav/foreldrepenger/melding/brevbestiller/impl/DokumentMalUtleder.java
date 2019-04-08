package no.nav.foreldrepenger.melding.brevbestiller.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.melding.brevbestiller.api.dto.klage.Klage;
import no.nav.foreldrepenger.melding.brevbestiller.api.dto.klage.KlageVurderingResultat;
import no.nav.foreldrepenger.melding.datamapper.DokumentBestillerFeil;
import no.nav.foreldrepenger.melding.datamapper.domene.KlageMapper;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.klage.KlageVurdering;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkTabellRepository;
import no.nav.foreldrepenger.melding.vedtak.Vedtaksbrev;

@ApplicationScoped
class DokumentMalUtleder {

    private static final String UTVIKLERFEIL_INGEN_ENDRING_SAMMEN = "Utviklerfeil: Det skal ikke være mulig å ha INGEN_ENDRING sammen med andre konsekvenser. BehandlingId: ";

    private KodeverkTabellRepository kodeverkTabellRepository;
    private KlageMapper klageMapper;

    public DokumentMalUtleder() {
        //CDI
    }

    @Inject
    public DokumentMalUtleder(KodeverkTabellRepository kodeverkTabellRepository,
                              KlageMapper klageMapper) {
        this.kodeverkTabellRepository = kodeverkTabellRepository;
        this.klageMapper = klageMapper;
    }

    private static boolean erKunEndringIFordelingAvYtelsen(Behandlingsresultat behandlingsresultat) {
        return behandlingsresultat.getKonsekvenserForYtelsen().contains(KonsekvensForYtelsen.ENDRING_I_FORDELING_AV_YTELSEN.getKode())
                && behandlingsresultat.getKonsekvenserForYtelsen().size() == 1;
    }

    private DokumentMalType mapEngangstønadVedtaksbrev(Behandling behandling) {
        Behandlingsresultat behandlingsresultat = behandling.getBehandlingsresultat();
        if (behandlingsresultat.erInnvilget()) {
            return kodeverkTabellRepository.finnDokumentMalType(DokumentMalType.POSITIVT_VEDTAK_DOK);
        } else if (behandlingsresultat.erOpphørt() || behandlingsresultat.erAvslått()) {
            return kodeverkTabellRepository.finnDokumentMalType(DokumentMalType.AVSLAGSVEDTAK_DOK);
        }
        throw DokumentBestillerFeil.FACTORY.ingenBrevmalKonfigurert(behandling.getId()).toException();
    }

    private DokumentMalType mapForeldrepengerVedtaksbrev(Behandling behandling) {
        Behandlingsresultat behandlingsresultat = behandling.getBehandlingsresultat();
        if (innvilgetForeldrepenger(behandlingsresultat)) {
            return kodeverkTabellRepository.finnDokumentMalType(DokumentMalType.INNVILGELSE_FORELDREPENGER_DOK);
        } else if (behandlingsresultat.erAvslått()) {
            return kodeverkTabellRepository.finnDokumentMalType(DokumentMalType.AVSLAG_FORELDREPENGER_DOK);
        } else if (behandlingsresultat.erOpphørt()) {
            return kodeverkTabellRepository.finnDokumentMalType(DokumentMalType.OPPHØR_DOK);
        }
        throw DokumentBestillerFeil.FACTORY.ingenBrevmalKonfigurert(behandling.getId()).toException();
    }

    private boolean innvilgetForeldrepenger(Behandlingsresultat behandlingsresultat) {
        return behandlingsresultat.erInnvilget() || skalBenytteInnvilgelsesbrev(behandlingsresultat);
    }

    private boolean skalBenytteInnvilgelsesbrev(Behandlingsresultat behandlingsresultat) {
        return behandlingsresultat.erEndretForeldrepenger() && !erKunEndringIFordelingAvYtelsen(behandlingsresultat);
    }

    DokumentMalType utledDokumentmal(Behandling behandling, DokumentHendelse hendelse) {
        if (hendelse.getDokumentMalType() != null) {
            return hendelse.getDokumentMalType();
        }
        if (Boolean.TRUE.equals(hendelse.isGjelderVedtak())) {
            return utledVedtaksbrev(behandling, hendelse);
        }
        throw DokumentBestillerFeil.FACTORY.ingenBrevmalKonfigurert(behandling.getId()).toException();
    }

    private DokumentMalType utledVedtaksbrev(Behandling behandling, DokumentHendelse hendelse) {
        //TODO aleksander - Fpsak kan sannsynligvis selv utlede hvilket vedtadsbrev som bestilles
        if (Objects.equals(behandling.getBehandlingsresultat().getVedtaksbrev(), Vedtaksbrev.FRITEKST.getKode())) {
            return kodeverkTabellRepository.finnDokumentMalType(DokumentMalType.FRITEKST_DOK);
        }
        if (erKlagebehandling(BehandlingType.KLAGE.getKode(), behandling.getBehandlingType())) {
            return mapKlageBrev(behandling);
        } else if (erRevurderingMedUendretUtfall(behandling)) {
            return kodeverkTabellRepository.finnDokumentMalType(DokumentMalType.UENDRETUTFALL_DOK);
        } else if (FagsakYtelseType.FORELDREPENGER.equals(hendelse.getYtelseType())) {
            return mapForeldrepengerVedtaksbrev(behandling);
        } else if (FagsakYtelseType.ENGANGSTØNAD.equals(hendelse.getYtelseType())) {
            return mapEngangstønadVedtaksbrev(behandling);
        }
        throw DokumentBestillerFeil.FACTORY.kjennerIkkeYtelse(hendelse.getYtelseType().getKode(), behandling.getId()).toException();
    }

    private boolean erRevurderingMedUendretUtfall(Behandling behandling) {
        if (!BehandlingType.REVURDERING.getKode().equals(behandling.getBehandlingType())) {
            return false;
        }
        Behandlingsresultat behandlingsresultat = behandling.getBehandlingsresultat();
        List<String> konsekvenserForYtelsen = behandlingsresultat.getKonsekvenserForYtelsen();
        boolean ingenKonsekvensForYtelsen = konsekvenserForYtelsen.contains(KonsekvensForYtelsen.INGEN_ENDRING.getKode());
        if (ingenKonsekvensForYtelsen && konsekvenserForYtelsen.size() > 1) {
            throw new IllegalStateException(UTVIKLERFEIL_INGEN_ENDRING_SAMMEN + behandling.getId());
        }
        return ingenKonsekvensForYtelsen;
    }

    private boolean erKlagebehandling(String kode, String behandlingType) {
        return kode.equals(behandlingType);
    }

    private DokumentMalType mapKlageBrev(Behandling behandling) {
        Klage klage = klageMapper.hentKlagebehandling(behandling);
        KlageVurderingResultat klageVurderingResultat = klage.getGjeldendeKlageVurderingsresultat();
        if (klageVurderingResultat == null) {
            throw DokumentBestillerFeil.FACTORY.behandlingManglerKlageVurderingResultat(behandling.getId()).toException();
        }
        String klagevurdering = klageVurderingResultat.getKlageVurdering();
        if (KlageVurdering.AVVIS_KLAGE.getKode().equals(klagevurdering)) {
            return kodeverkTabellRepository.finnDokumentMalType(DokumentMalType.KLAGE_AVVIST_DOK);
        } else if (Arrays.asList(KlageVurdering.OPPHEVE_YTELSESVEDTAK.getKode(), KlageVurdering.HJEMSENDE_UTEN_Å_OPPHEVE.getKode()).contains(klagevurdering)) {
            return kodeverkTabellRepository.finnDokumentMalType(DokumentMalType.KLAGE_YTELSESVEDTAK_OPPHEVET_DOK);
        } else if (KlageVurdering.MEDHOLD_I_KLAGE.getKode().equals(klagevurdering)) {
            return kodeverkTabellRepository.finnDokumentMalType(DokumentMalType.VEDTAK_MEDHOLD);
        } else if (KlageVurdering.STADFESTE_YTELSESVEDTAK.getKode().equals(klagevurdering)) {
            return kodeverkTabellRepository.finnDokumentMalType(DokumentMalType.KLAGE_YTELSESVEDTAK_STADFESTET_DOK);
        }
        //TODO aleksander
        throw DokumentBestillerFeil.FACTORY.ingenBrevmalKonfigurert(behandling.getId()).toException();
    }

}

package no.nav.foreldrepenger.melding.brevbestiller.impl;

import java.util.Arrays;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.melding.brevbestiller.api.dto.klage.Klage;
import no.nav.foreldrepenger.melding.brevbestiller.api.dto.klage.KlageVurderingResultat;
import no.nav.foreldrepenger.melding.datamapper.domene.KlageMapper;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.klage.KlageVurdering;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkTabellRepository;
import no.nav.foreldrepenger.melding.vedtak.Vedtaksbrev;

@ApplicationScoped
class DokumentMalUtreder {

    private KodeverkTabellRepository kodeverkTabellRepository;
    private KlageMapper klageMapper;

    public DokumentMalUtreder() {
        //CDI
    }

    @Inject
    public DokumentMalUtreder(KodeverkTabellRepository kodeverkTabellRepository,
                              KlageMapper klageMapper) {
        this.kodeverkTabellRepository = kodeverkTabellRepository;
        this.klageMapper = klageMapper;
    }

    private static boolean erKunEndringIFordelingAvYtelsen(Behandlingsresultat behandlingsresultat) {
        return behandlingsresultat.getKonsekvenserForYtelsen().contains(KonsekvensForYtelsen.ENDRING_I_FORDELING_AV_YTELSEN.getKode())
                && behandlingsresultat.getKonsekvenserForYtelsen().size() == 1;
    }

    private DokumentMalType mapEngangstønadVedtaksbrev(Behandling behandling, DokumentHendelse hendelse) {
        if (behandling.getBehandlingsresultat().erInnvilget()) {
            return kodeverkTabellRepository.finnDokumentMalType(DokumentMalType.POSITIVT_VEDTAK_DOK);
        }
        return null;
    }

    private DokumentMalType mapForeldrepengerVedtaksbrev(Behandling behandling, DokumentHendelse hendelse) {
        if (innvilgetForeldrepenger(behandling.getBehandlingsresultat())) {
            return kodeverkTabellRepository.finnDokumentMalType(DokumentMalType.INNVILGELSE_FORELDREPENGER_DOK);
        }
        //TODO
        return null;
    }

    private boolean innvilgetForeldrepenger(Behandlingsresultat behandlingsresultat) {
        return behandlingsresultat.erInnvilget() || skalBenytteInnvilgelsesbrev(behandlingsresultat);
    }

    private boolean skalBenytteInnvilgelsesbrev(Behandlingsresultat behandlingsresultat) {
        return behandlingsresultat.erEndretForeldrepenger() && !erKunEndringIFordelingAvYtelsen(behandlingsresultat);
    }

    DokumentMalType utredDokumentmal(Behandling behandling, DokumentHendelse hendelse) {
        if (hendelse.getDokumentMalType() != null) {
            return hendelse.getDokumentMalType();
        }
        if (hendelse.isGjelderVedtak()) {
            //TODO     public Long lagDokumentData(Behandlingsresultat behandlingsresultat, i FPSAK, LagDokumentData.java:35
            return utledVedtaksbrev(behandling, hendelse);
        }
        throw new IllegalStateException("Klarer ikke utlede dokumentmal for behandling: " + behandling.getId());
    }

    private DokumentMalType utledVedtaksbrev(Behandling behandling, DokumentHendelse hendelse) {
        //TODO aleksander - Fpsak kan sannsynligvis selv utlede hvilket vedtadsbrev som bestilles
        //TODO aleksander - mange revurdering uendret utfall
        if (behandling.getBehandlingsresultat().getVedtaksbrev().equals(Vedtaksbrev.FRITEKST.getKode())) {
            return kodeverkTabellRepository.finnDokumentMalType(DokumentMalType.FRITEKST_DOK);
        }
        if (erKlagebehandling(BehandlingType.KLAGE.getKode(), behandling.getBehandlingType())) {
            return mapKlageBrev(behandling);
        } else if (FagsakYtelseType.FORELDREPENGER.equals(hendelse.getYtelseType())) {
            return mapForeldrepengerVedtaksbrev(behandling, hendelse);
        } else if (FagsakYtelseType.ENGANGSTØNAD.equals(hendelse.getYtelseType())) {
            return mapEngangstønadVedtaksbrev(behandling, hendelse);
        }
        throw new IllegalStateException("Finner ikke ytelse: " + hendelse.getYtelseType());
    }

    private boolean erKlagebehandling(String kode, String behandlingType) {
        return kode.equals(behandlingType);
    }

    private DokumentMalType mapKlageBrev(Behandling behandling) {
        Klage klage = klageMapper.hentKlagebehandling(behandling);
        KlageVurderingResultat klageVurderingResultat = klage.getGjeldendeKlageVurderingsresultat();
        if (klageVurderingResultat == null) {
            //TODO aleksander
            throw new IllegalStateException();
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
        throw new IllegalStateException();
    }

}

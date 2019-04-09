package no.nav.foreldrepenger.melding.brevbestiller.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingResultatType;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.melding.datamapper.domene.KlageMapper;
import no.nav.foreldrepenger.melding.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepositoryImpl;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.klage.KlageVurdering;
import no.nav.foreldrepenger.melding.klage.KlageVurderingResultat;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkTabellRepository;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkTabellRepositoryImpl;
import no.nav.foreldrepenger.melding.vedtak.Vedtaksbrev;
import no.nav.vedtak.exception.VLException;
public class DokumentMalUtlederTest {
    private final long BEHANDLING_ID = 123L;
    @Rule
    public UnittestRepositoryRule repositoryRule = new UnittestRepositoryRule();

    private KodeverkTabellRepository kodeverkTabellRepository = new KodeverkTabellRepositoryImpl(repositoryRule.getEntityManager());
    private DokumentRepository dokumentRepository;

    private KlageMapper klageMapper = Mockito.mock(KlageMapper.class);
    private DokumentMalUtleder dokumentMalUtleder;
    private DokumentHendelse hendelse;

    @Before
    public void setup() {
        dokumentRepository = new DokumentRepositoryImpl(repositoryRule.getEntityManager());
        dokumentMalUtleder = new DokumentMalUtleder(kodeverkTabellRepository, klageMapper);
    }

    @Test
    public void utledfra_input_mal() {
        sjekkAtVelgerValgtMal(DokumentMalType.UENDRETUTFALL_DOK);
        sjekkAtVelgerValgtMal(DokumentMalType.FRITEKST_DOK);
        sjekkAtVelgerValgtMal(DokumentMalType.KLAGE_AVVIST_DOK);
        sjekkAtVelgerValgtMal(DokumentMalType.FORLENGET_DOK);
        sjekkAtVelgerValgtMal(DokumentMalType.AVSLAGSVEDTAK_DOK);
    }

    private void sjekkAtVelgerValgtMal(String malType) {
        hendelse = standardBuilder()
                .medDokumentMalType(dokumentRepository.hentDokumentMalType(malType))
                .build();
        assertThat(dokumentMalUtleder.utledDokumentmal(null, hendelse).getKode()).isEqualTo(malType);
    }

    @Test
    public void utled_uendret_utfall() {
        hendelse = standardBuilder()
                .medGjelderVedtak(true)
                .build();
        Behandling behandling = Behandling.builder()
                .medBehandlingType(BehandlingType.REVURDERING)
                .medBehandlingsresultat(Behandlingsresultat.builder().medKonsekvenserForYtelsen(List.of(KonsekvensForYtelsen.INGEN_ENDRING.getKode())).build())
                .build();
        assertThat(dokumentMalUtleder.utledDokumentmal(behandling, hendelse).getKode()).isEqualTo(DokumentMalType.UENDRETUTFALL_DOK);
    }

    @Test
    public void utled_innvilget_fp() {
        hendelse = standardBuilder()
                .medGjelderVedtak(true)
                .build();
        Behandling behandling = Behandling.builder()
                .medBehandlingsresultat(Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.INNVILGET).build())
                .build();
        assertThat(dokumentMalUtleder.utledDokumentmal(behandling, hendelse).getKode()).isEqualTo(DokumentMalType.INNVILGELSE_FORELDREPENGER_DOK);
    }

    @Test
    public void utled_innvilget_es() {
        hendelse = DokumentHendelse.builder()
                .medBehandlingId(BEHANDLING_ID)
                .medYtelseType(FagsakYtelseType.ENGANGSTØNAD)
                .medGjelderVedtak(true)
                .build();
        Behandling behandling = Behandling.builder()
                .medBehandlingsresultat(Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.INNVILGET).build())
                .build();
        assertThat(dokumentMalUtleder.utledDokumentmal(behandling, hendelse).getKode()).isEqualTo(DokumentMalType.POSITIVT_VEDTAK_DOK);
    }

    @Test
    public void utled_avslått_es() {
        hendelse = DokumentHendelse.builder()
                .medBehandlingId(BEHANDLING_ID)
                .medYtelseType(FagsakYtelseType.ENGANGSTØNAD)
                .medGjelderVedtak(true)
                .build();
        Behandling behandling = Behandling.builder()
                .medBehandlingsresultat(Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.AVSLÅTT).build())
                .build();
        assertThat(dokumentMalUtleder.utledDokumentmal(behandling, hendelse).getKode()).isEqualTo(DokumentMalType.AVSLAGSVEDTAK_DOK);
    }

    @Test
    public void utled_avslått_opphør_es() {
        hendelse = DokumentHendelse.builder()
                .medBehandlingId(BEHANDLING_ID)
                .medYtelseType(FagsakYtelseType.ENGANGSTØNAD)
                .medGjelderVedtak(true)
                .build();
        Behandling behandling = Behandling.builder()
                .medBehandlingsresultat(Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.OPPHØR).build())
                .build();
        assertThat(dokumentMalUtleder.utledDokumentmal(behandling, hendelse).getKode()).isEqualTo(DokumentMalType.AVSLAGSVEDTAK_DOK);
    }

    @Test
    public void kast_exception_hvis_ugyldig_es() {
        hendelse = DokumentHendelse.builder()
                .medBehandlingId(BEHANDLING_ID)
                .medYtelseType(FagsakYtelseType.ENGANGSTØNAD)
                .medGjelderVedtak(true)
                .build();
        Behandling behandling = Behandling.builder()
                .medBehandlingsresultat(Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.FORELDREPENGER_ENDRET).build())
                .build();
        assertThatThrownBy(() -> dokumentMalUtleder.utledDokumentmal(behandling, hendelse)).isInstanceOf(VLException.class);
    }

    @Test
    public void utled_avslått_fp() {
        hendelse = standardBuilder()
                .medGjelderVedtak(true)
                .build();
        Behandling behandling = Behandling.builder()
                .medBehandlingsresultat(Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.AVSLÅTT).build())
                .build();
        assertThat(dokumentMalUtleder.utledDokumentmal(behandling, hendelse).getKode()).isEqualTo(DokumentMalType.AVSLAG_FORELDREPENGER_DOK);
    }

    @Test
    public void utled_opphør_fp() {
        hendelse = standardBuilder()
                .medGjelderVedtak(true)
                .build();
        Behandling behandling = Behandling.builder()
                .medBehandlingsresultat(Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.OPPHØR).build())
                .build();
        assertThat(dokumentMalUtleder.utledDokumentmal(behandling, hendelse).getKode()).isEqualTo(DokumentMalType.OPPHØR_DOK);
    }


    @Test
    public void utled_innvilget_fp_endret() {
        hendelse = standardBuilder()
                .medGjelderVedtak(true)
                .build();
        Behandling behandling = Behandling.builder()
                .medBehandlingsresultat(
                        Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.FORELDREPENGER_ENDRET)
                                .build())
                .build();
        assertThat(dokumentMalUtleder.utledDokumentmal(behandling, hendelse).getKode()).isEqualTo(DokumentMalType.INNVILGELSE_FORELDREPENGER_DOK);
    }

    @Test
    public void ikke_innvilget_fp_hvis_kun_endring_av_ytelsen() {
        hendelse = standardBuilder()
                .medGjelderVedtak(true)
                .build();
        Behandling behandling = Behandling.builder()
                .medBehandlingsresultat(
                        Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.FORELDREPENGER_ENDRET)
                                .medKonsekvenserForYtelsen(List.of(KonsekvensForYtelsen.ENDRING_I_FORDELING_AV_YTELSEN.getKode()))
                                .build())
                .build();
        assertThatThrownBy(() -> dokumentMalUtleder.utledDokumentmal(behandling, hendelse)).isInstanceOf(VLException.class);
    }

    @Test
    public void utledfritekst() {
        hendelse = standardBuilder()
                .medGjelderVedtak(true)
                .build();
        Behandling behandling = Behandling.builder()
                .medBehandlingType(BehandlingType.REVURDERING)
                .medBehandlingsresultat(Behandlingsresultat.builder().medVedtaksbrev(Vedtaksbrev.FRITEKST.getKode()).build())
                .build();
        assertThat(dokumentMalUtleder.utledDokumentmal(behandling, hendelse).getKode()).isEqualTo(DokumentMalType.FRITEKST_DOK);
    }

    @Test
    public void kast_error_hvis_ugyldig() {
        hendelse = standardBuilder()
                .build();
        Behandling behandling = Behandling.builder()
                .medId(123)
                .build();
        assertThatThrownBy(() -> dokumentMalUtleder.utledDokumentmal(behandling, hendelse)).isInstanceOf(VLException.class);
    }

    @Test
    public void utled_klage() {
        sjekkKlageDokument(KlageVurdering.AVVIS_KLAGE, DokumentMalType.KLAGE_AVVIST_DOK);
        sjekkKlageDokument(KlageVurdering.STADFESTE_YTELSESVEDTAK, DokumentMalType.KLAGE_YTELSESVEDTAK_STADFESTET_DOK);
        sjekkKlageDokument(KlageVurdering.MEDHOLD_I_KLAGE, DokumentMalType.VEDTAK_MEDHOLD);
        sjekkKlageDokument(KlageVurdering.OPPHEVE_YTELSESVEDTAK, DokumentMalType.KLAGE_YTELSESVEDTAK_OPPHEVET_DOK);
        sjekkKlageDokument(KlageVurdering.HJEMSENDE_UTEN_Å_OPPHEVE, DokumentMalType.KLAGE_YTELSESVEDTAK_OPPHEVET_DOK);
        assertThatThrownBy(() -> sjekkKlageDokument(KlageVurdering.UDEFINERT, DokumentMalType.KLAGE_AVVIST_DOK)).isInstanceOf(VLException.class);
    }

    private void sjekkKlageDokument(KlageVurdering klageVurdering, String dokumentmal) {
        hendelse = standardBuilder()
                .medGjelderVedtak(true)
                .build();
        Behandling behandling = Behandling.builder()
                .medBehandlingType(BehandlingType.KLAGE)
                .medBehandlingsresultat(Behandlingsresultat.builder().build())
                .build();

        Klage klage = Klage.ny()
                .medKlageVurderingResultatNK(KlageVurderingResultat.ny().medKlageVurdering(klageVurdering).build())
                .build();
        Mockito.doReturn(klage).when(klageMapper).hentKlagebehandling(behandling);
        assertThat(dokumentMalUtleder.utledDokumentmal(behandling, hendelse).getKode()).isEqualTo(dokumentmal);
    }

    private DokumentHendelse.Builder standardBuilder() {
        return DokumentHendelse.builder()
                .medBehandlingId(123L)
                .medYtelseType(FagsakYtelseType.FORELDREPENGER);
    }
}

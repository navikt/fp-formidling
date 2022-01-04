package no.nav.foreldrepenger.melding.brevbestiller.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.klage.KlageVurdering;
import no.nav.foreldrepenger.melding.klage.KlageVurderingResultat;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.melding.vedtak.Vedtaksbrev;
import no.nav.vedtak.exception.VLException;

public class DokumentMalUtlederTest {

    private final DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);
    private DokumentMalUtleder dokumentMalUtleder;
    private DokumentHendelse hendelse;

    @BeforeEach
    public void setup() {
        dokumentMalUtleder = new DokumentMalUtleder(domeneobjektProvider, null, null);
    }

    @Test
    public void utledfra_input_mal() {
        sjekkAtVelgerValgtMal(DokumentMalType.INGEN_ENDRING);
        sjekkAtVelgerValgtMal(DokumentMalType.FRITEKSTBREV);
        sjekkAtVelgerValgtMal(DokumentMalType.KLAGE_AVVIST);
        sjekkAtVelgerValgtMal(DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_MEDL);
        sjekkAtVelgerValgtMal(DokumentMalType.ENGANGSSTØNAD_AVSLAG);
    }

    private void sjekkAtVelgerValgtMal(DokumentMalType malType) {
        hendelse = standardBuilder()
                .medDokumentMalType((malType))
                .build();
        assertThat(dokumentMalUtleder.utledDokumentmal(null, hendelse).getKode()).isEqualTo(malType.getKode());
    }

    @Test
    public void utled_ingen_endring() {
        hendelse = standardBuilder()
                .medGjelderVedtak(true)
                .build();
        Behandling behandling = Behandling.builder()
                .medUuid(UUID.randomUUID())
                .medBehandlingType(BehandlingType.REVURDERING)
                .medBehandlingsresultat(Behandlingsresultat.builder().medKonsekvenserForYtelsen(List.of(KonsekvensForYtelsen.INGEN_ENDRING)).build())
                .build();
        assertThat(dokumentMalUtleder.utledDokumentmal(behandling, hendelse).getKode()).isEqualTo(DokumentMalType.INGEN_ENDRING.getKode());
    }

    @Test
    public void utled_innvilget_fp() {
        hendelse = standardBuilder()
                .medGjelderVedtak(true)
                .build();
        Behandling behandling = Behandling.builder()
                .medUuid(UUID.randomUUID())
                .medBehandlingsresultat(Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.INNVILGET).build())
                .build();
        assertThat(dokumentMalUtleder.utledDokumentmal(behandling, hendelse).getKode())
                .isEqualTo(DokumentMalType.FORELDREPENGER_INNVILGELSE.getKode());
    }

    @Test
    public void utled_innvilget_es() {
        hendelse = DokumentHendelse.builder()
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .medYtelseType(FagsakYtelseType.ENGANGSTØNAD)
                .medGjelderVedtak(true)
                .build();
        Behandling behandling = Behandling.builder()
                .medUuid(UUID.randomUUID())
                .medBehandlingsresultat(Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.INNVILGET).build())
                .build();
        assertThat(dokumentMalUtleder.utledDokumentmal(behandling, hendelse).getKode())
                .isEqualTo(DokumentMalType.ENGANGSSTØNAD_INNVILGELSE.getKode());
    }

    @Test
    public void utled_avslått_es() {
        hendelse = DokumentHendelse.builder()
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .medYtelseType(FagsakYtelseType.ENGANGSTØNAD)
                .medGjelderVedtak(true)
                .build();
        Behandling behandling = Behandling.builder()
                .medUuid(UUID.randomUUID())
                .medBehandlingsresultat(Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.AVSLÅTT).build())
                .build();
        assertThat(dokumentMalUtleder.utledDokumentmal(behandling, hendelse).getKode()).isEqualTo(DokumentMalType.ENGANGSSTØNAD_AVSLAG.getKode());
    }

    @Test
    public void utled_avslått_opphør_es() {
        hendelse = DokumentHendelse.builder()
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .medYtelseType(FagsakYtelseType.ENGANGSTØNAD)
                .medGjelderVedtak(true)
                .build();
        Behandling behandling = Behandling.builder()
                .medUuid(UUID.randomUUID())
                .medBehandlingsresultat(Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.OPPHØR).build())
                .build();
        assertThat(dokumentMalUtleder.utledDokumentmal(behandling, hendelse).getKode()).isEqualTo(DokumentMalType.ENGANGSSTØNAD_AVSLAG.getKode());
    }

    @Test
    public void kast_exception_hvis_ugyldig_es() {
        hendelse = DokumentHendelse.builder()
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .medYtelseType(FagsakYtelseType.ENGANGSTØNAD)
                .medGjelderVedtak(true)
                .build();
        Behandling behandling = Behandling.builder()
                .medUuid(UUID.randomUUID())
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
                .medUuid(UUID.randomUUID())
                .medBehandlingsresultat(Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.AVSLÅTT).build())
                .build();
        assertThat(dokumentMalUtleder.utledDokumentmal(behandling, hendelse).getKode())
                .isEqualTo(DokumentMalType.FORELDREPENGER_AVSLAG.getKode());
    }

    @Test
    public void utled_opphør_fp() {
        hendelse = standardBuilder()
                .medGjelderVedtak(true)
                .build();
        Behandling behandling = Behandling.builder()
                .medUuid(UUID.randomUUID())
                .medBehandlingsresultat(Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.OPPHØR).build())
                .build();
        assertThat(dokumentMalUtleder.utledDokumentmal(behandling, hendelse).getKode()).isEqualTo(DokumentMalType.FORELDREPENGER_OPPHØR.getKode());
    }

    @Test
    public void utled_innvilget_fp_endret() {
        hendelse = standardBuilder()
                .medGjelderVedtak(true)
                .build();
        Behandling behandling = Behandling.builder()
                .medUuid(UUID.randomUUID())
                .medBehandlingsresultat(
                        Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.FORELDREPENGER_ENDRET)
                                .build())
                .build();
        assertThat(dokumentMalUtleder.utledDokumentmal(behandling, hendelse).getKode())
                .isEqualTo(DokumentMalType.FORELDREPENGER_INNVILGELSE.getKode());
    }

    @Test
    public void ikke_innvilget_fp_hvis_kun_endring_av_ytelsen() {
        hendelse = standardBuilder()
                .medGjelderVedtak(true)
                .build();
        Behandling behandling = Behandling.builder()
                .medUuid(UUID.randomUUID())
                .medBehandlingsresultat(
                        Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.FORELDREPENGER_ENDRET)
                                .medKonsekvenserForYtelsen(List.of(KonsekvensForYtelsen.ENDRING_I_FORDELING_AV_YTELSEN))
                                .build())
                .build();
        assertThatThrownBy(() -> dokumentMalUtleder.utledDokumentmal(behandling, hendelse)).isInstanceOf(VLException.class);
    }

    @Test
    public void utled_annullert_fp() {
        hendelse = standardBuilder()
                .medGjelderVedtak(true)
                .build();
        Behandling behandling = Behandling.builder()
                .medUuid(UUID.randomUUID())
                .medBehandlingsresultat(Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.FORELDREPENGER_SENERE).build())
                .build();
        assertThat(dokumentMalUtleder.utledDokumentmal(behandling, hendelse).getKode())
                .isEqualTo(DokumentMalType.FORELDREPENGER_ANNULLERT.getKode());
    }

    @Test
    public void utled_fritekstbrev() {
        hendelse = standardBuilder()
                .medGjelderVedtak(true)
                .build();
        Behandling behandling = Behandling.builder()
                .medUuid(UUID.randomUUID())
                .medBehandlingType(BehandlingType.REVURDERING)
                .medBehandlingsresultat(Behandlingsresultat.builder().medVedtaksbrev(Vedtaksbrev.FRITEKST).build())
                .build();
        assertThat(dokumentMalUtleder.utledDokumentmal(behandling, hendelse).getKode()).isEqualTo(DokumentMalType.FRITEKSTBREV.getKode());
    }

    @Test
    public void kast_error_hvis_ugyldig() {
        hendelse = standardBuilder()
                .build();
        Behandling behandling = Behandling.builder()
                .medUuid(UUID.randomUUID())
                .build();
        assertThatThrownBy(() -> dokumentMalUtleder.utledDokumentmal(behandling, hendelse)).isInstanceOf(VLException.class);
    }

    @Test
    public void utled_klage() {
        sjekkKlageDokument(KlageVurdering.AVVIS_KLAGE, DokumentMalType.KLAGE_AVVIST);
        sjekkKlageDokument(KlageVurdering.STADFESTE_YTELSESVEDTAK, DokumentMalType.KLAGE_STADFESTET);
        sjekkKlageDokument(KlageVurdering.MEDHOLD_I_KLAGE, DokumentMalType.KLAGE_OMGJORT);
        sjekkKlageDokument(KlageVurdering.OPPHEVE_YTELSESVEDTAK, DokumentMalType.KLAGE_HJEMSENDT);
        sjekkKlageDokument(KlageVurdering.HJEMSENDE_UTEN_Å_OPPHEVE, DokumentMalType.KLAGE_HJEMSENDT);
        assertThatThrownBy(() -> sjekkKlageDokument(KlageVurdering.UDEFINERT, DokumentMalType.KLAGE_AVVIST)).isInstanceOf(VLException.class);
    }

    @Test
    public void utled_SVP_revurdering_med_endring() {
        hendelse = standardBuilder()
                .medGjelderVedtak(true)
                .medYtelseType(FagsakYtelseType.SVANGERSKAPSPENGER)
                .build();
        Behandlingsresultat behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.FORELDREPENGER_ENDRET)
                .medKonsekvenserForYtelsen(List.of(KonsekvensForYtelsen.ENDRING_I_UTTAK))
                .build();
        Behandling behandling = Behandling.builder()
                .medUuid(UUID.randomUUID())
                .medBehandlingType(BehandlingType.REVURDERING)
                .medBehandlingsresultat(behandlingsresultat)
                .build();
        assertThat(dokumentMalUtleder.utledDokumentmal(behandling, hendelse).getKode())
                .isEqualTo(DokumentMalType.SVANGERSKAPSPENGER_INNVILGELSE.getKode());
    }

    @Test
    public void utled_SVP_førstegangsbehandling_innvilget() {
        hendelse = standardBuilder()
                .medGjelderVedtak(true)
                .medYtelseType(FagsakYtelseType.SVANGERSKAPSPENGER)
                .build();
        Behandlingsresultat behandlingsresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                .build();
        Behandling behandling = Behandling.builder()
                .medUuid(UUID.randomUUID())
                .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medBehandlingsresultat(behandlingsresultat)
                .build();
        assertThat(dokumentMalUtleder.utledDokumentmal(behandling, hendelse).getKode())
                .isEqualTo(DokumentMalType.SVANGERSKAPSPENGER_INNVILGELSE.getKode());
    }

    private void sjekkKlageDokument(KlageVurdering klageVurdering, DokumentMalType dokumentmal) {
        hendelse = standardBuilder()
                .medGjelderVedtak(true)
                .build();
        Behandling behandling = Behandling.builder()
                .medUuid(UUID.randomUUID())
                .medBehandlingType(BehandlingType.KLAGE)
                .medBehandlingsresultat(Behandlingsresultat.builder().build())
                .build();

        Klage klage = Klage.ny()
                .medKlageVurderingResultatNK(new KlageVurderingResultat(klageVurdering, null))
                .build();
        when(domeneobjektProvider.hentKlagebehandling(behandling)).thenReturn(klage);
        assertThat(dokumentMalUtleder.utledDokumentmal(behandling, hendelse).getKode()).isEqualTo(dokumentmal.getKode());
    }

    private DokumentHendelse.Builder standardBuilder() {
        return DokumentHendelse.builder()
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .medYtelseType(FagsakYtelseType.FORELDREPENGER);
    }
}

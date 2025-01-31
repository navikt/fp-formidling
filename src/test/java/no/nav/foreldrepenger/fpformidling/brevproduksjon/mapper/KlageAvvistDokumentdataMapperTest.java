package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentData;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.klage.Klage;
import no.nav.foreldrepenger.fpformidling.domene.klage.KlageAvvistÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.klage.KlageFormkravResultat;
import no.nav.foreldrepenger.fpformidling.domene.klage.KlageVurderingResultat;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;


@ExtendWith(MockitoExtension.class)
class KlageAvvistDokumentdataMapperTest {

    @Mock
    private BrevParametere brevParametere;

    @Mock
    private DomeneobjektProvider domeneobjektProvider;

    private DokumentData dokumentData;

    private KlageAvvistDokumentdataMapper dokumentdataMapper;

    private static final String FRITEKST_TIL_BREV = "FRITEKST";

    @BeforeEach
    void before() {
        dokumentData = DatamapperTestUtil.lagStandardDokumentData(DokumentMalType.KLAGE_AVVIST);
        when(brevParametere.getKlagefristUker()).thenReturn(6);
        dokumentdataMapper = new KlageAvvistDokumentdataMapper(brevParametere, domeneobjektProvider);
    }

    @Test
    void skal_mappe_felter_for_vanlig_behandling() {
        // Arrange
        var behandling = DatamapperTestUtil.standardForeldrepengerBehandling();
        var dokumentFelles = DatamapperTestUtil.lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        var dokumentHendelse = DatamapperTestUtil.lagStandardHendelseBuilder().build();
        mockKlage(behandling, BehandlingType.FØRSTEGANGSSØKNAD, of(KlageAvvistÅrsak.KLAGER_IKKE_PART));

        // Act
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getFelles()).isNotNull();
        assertThat(dokumentdata.getFelles().getSøkerNavn()).isEqualTo(DatamapperTestUtil.SØKERS_NAVN);
        assertThat(dokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(DatamapperTestUtil.SØKERS_FNR));
        assertThat(dokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(dokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoNorsk(LocalDate.now()));
        assertThat(dokumentdata.getFelles().getHarVerge()).isTrue();
        assertThat(dokumentdata.getFelles().getErKopi()).isTrue();
        assertThat(dokumentdata.getFelles().getSaksnummer()).isEqualTo(DatamapperTestUtil.SAKSNUMMER);
        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo("FP");
        assertThat(dokumentdata.getFelles().getErUtkast()).isFalse();
        assertThat(dokumentdata.getFelles().getFritekst()).isEqualTo(FritekstDto.fra(FRITEKST_TIL_BREV));

        assertThat(dokumentdata.getGjelderTilbakekreving()).isFalse();
        assertThat(dokumentdata.getLovhjemler()).isEqualTo("forvaltningsloven §§ 28 og 33");
        assertThat(dokumentdata.getKlagefristUker()).isEqualTo(6);
        assertThat(dokumentdata.getAvvistGrunner()).containsAll(of(KlageAvvistÅrsak.KLAGER_IKKE_PART.getKode()));
        assertThat(dokumentdata.isPåklagdVedtak()).isTrue();
    }

    @Test
    void skal_mappe_felter_for_tilbakekreving_med_alle_avvist_grunner_og_spesialhåndtere_for_sent_og_ikke_signert() {
        // Arrange
        var behandling = DatamapperTestUtil.standardForeldrepengerBehandling();
        var dokumentFelles = DatamapperTestUtil.lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        var dokumentHendelse = DatamapperTestUtil.lagStandardHendelseBuilder().build();
        mockKlage(behandling, BehandlingType.TILBAKEKREVING,
            of(KlageAvvistÅrsak.KLAGET_FOR_SENT, KlageAvvistÅrsak.KLAGER_IKKE_PART, KlageAvvistÅrsak.KLAGE_UGYLDIG, KlageAvvistÅrsak.IKKE_KONKRET,
                KlageAvvistÅrsak.IKKE_PAKLAGD_VEDTAK, KlageAvvistÅrsak.IKKE_SIGNERT));

        // Act
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getGjelderTilbakekreving()).isTrue();
        assertThat(dokumentdata.getLovhjemler()).isEqualTo("folketrygdloven § 21-12 og forvaltningsloven §§ 28, 31, 32 og 33");
        assertThat(dokumentdata.getAvvistGrunner()).containsAll(
            of(KlageAvvistÅrsak.KLAGET_FOR_SENT.getKode(), KlageAvvistÅrsak.KLAGER_IKKE_PART.getKode(), KlageAvvistÅrsak.KLAGE_UGYLDIG.getKode(),
                KlageAvvistÅrsak.IKKE_KONKRET.getKode(), KlageAvvistÅrsak.IKKE_PAKLAGD_VEDTAK.getKode()));
        assertThat(dokumentdata.isPåklagdVedtak()).isFalse();
    }

    private void mockKlage(Behandling behandling, BehandlingType behandlingType, List<KlageAvvistÅrsak> avvistÅrsaker) {
        var klageVurderingResultat = new KlageVurderingResultat(null, FRITEKST_TIL_BREV);
        var klageFormkravResultat = new KlageFormkravResultat(avvistÅrsaker);
        var klage = Klage.ny()
            .medPåklagdBehandlingType(behandlingType)
            .medKlageVurderingResultatNK(klageVurderingResultat)
            .medFormkravNFP(klageFormkravResultat)
            .build();
        when(domeneobjektProvider.hentKlagebehandling(behandling)).thenReturn(klage);
    }
}

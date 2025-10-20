package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.defaultBuilder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.klageBehandling;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.klageFormkravResultat;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.klageVurderingResultat;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.Period;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;

@ExtendWith(MockitoExtension.class)
class KlageAvvistDokumentdataMapperTest {

    private KlageAvvistDokumentdataMapper dokumentdataMapper;

    private static final String FRITEKST_TIL_BREV = "FRITEKST";

    @BeforeEach
    void before() {
        var brevParametere = new BrevParametere(6, 2, Period.ZERO, Period.ZERO);
        dokumentdataMapper = new KlageAvvistDokumentdataMapper(brevParametere);
    }

    @Test
    void skal_mappe_felter_for_vanlig_behandling() {
        // Arrange
        var klageFormkrav = klageFormkravResultat()
            .påklagdBehandlingType(BrevGrunnlagDto.BehandlingType.FØRSTEGANGSSØKNAD)
            .avvistÅrsaker(of(BrevGrunnlagDto.KlageBehandling.KlageAvvistÅrsak.KLAGER_IKKE_PART))
            .build();

        var klageVurdering = klageVurderingResultat()
            .fritekstTilBrev(FRITEKST_TIL_BREV)
            .build();

        var klage = klageBehandling()
            .klageFormkravResultatNFP(klageFormkrav)
            .klageVurderingResultatNK(klageVurdering)
            .build();

        var behandling = defaultBuilder()
            .fagsakYtelseType(BrevGrunnlagDto.FagsakYtelseType.FORELDREPENGER)
            .klageBehandling(klage)
            .build();

        var dokumentFelles = lagStandardDokumentFelles(FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = lagStandardHendelseBuilder().build();

        // Act
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getFelles()).isNotNull();
        assertThat(dokumentdata.getFelles().getSøkerNavn()).isEqualTo(DatamapperTestUtil.SØKERS_NAVN);
        assertThat(dokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(DatamapperTestUtil.SØKERS_FNR));
        assertThat(dokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(dokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoNorsk(LocalDate.now()));
        assertThat(dokumentdata.getFelles().getHarVerge()).isFalse();
        assertThat(dokumentdata.getFelles().getErKopi()).isFalse();
        assertThat(dokumentdata.getFelles().getSaksnummer()).isEqualTo(DatamapperTestUtil.SAKSNUMMER);
        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo("FP");
        assertThat(dokumentdata.getFelles().getErUtkast()).isFalse();
        assertThat(dokumentdata.getFelles().getFritekst()).isEqualTo(FritekstDto.fra(FRITEKST_TIL_BREV));

        assertThat(dokumentdata.getGjelderTilbakekreving()).isFalse();
        assertThat(dokumentdata.getLovhjemler()).isEqualTo("forvaltningsloven §§ 28 og 33");
        assertThat(dokumentdata.getKlagefristUker()).isEqualTo(6);
        assertThat(dokumentdata.getAvvistGrunner()).containsAll(of("KLAGER_IKKE_PART"));
        assertThat(dokumentdata.isPåklagdVedtak()).isTrue();
    }

    @Test
    void skal_mappe_felter_for_tilbakekreving_med_alle_avvist_grunner_og_spesialhåndtere_for_sent_og_ikke_signert() {
        // Arrange
        var klageFormkrav = klageFormkravResultat()
            .påklagdBehandlingType(BrevGrunnlagDto.BehandlingType.TILBAKEKREVING)
            .avvistÅrsaker(of(
                BrevGrunnlagDto.KlageBehandling.KlageAvvistÅrsak.KLAGET_FOR_SENT,
                BrevGrunnlagDto.KlageBehandling.KlageAvvistÅrsak.KLAGER_IKKE_PART,
                BrevGrunnlagDto.KlageBehandling.KlageAvvistÅrsak.KLAGE_UGYLDIG,
                BrevGrunnlagDto.KlageBehandling.KlageAvvistÅrsak.IKKE_KONKRET,
                BrevGrunnlagDto.KlageBehandling.KlageAvvistÅrsak.IKKE_PÅKLAGD_VEDTAK,
                BrevGrunnlagDto.KlageBehandling.KlageAvvistÅrsak.IKKE_SIGNERT
            ))
            .build();

        var klageVurdering = klageVurderingResultat()
            .fritekstTilBrev(FRITEKST_TIL_BREV)
            .build();

        var klage = klageBehandling()
            .klageFormkravResultatNFP(klageFormkrav)
            .klageVurderingResultatNK(klageVurdering)
            .build();

        var behandling = defaultBuilder()
            .fagsakYtelseType(BrevGrunnlagDto.FagsakYtelseType.FORELDREPENGER)
            .klageBehandling(klage)
            .build();

        var dokumentFelles = lagStandardDokumentFelles(FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = lagStandardHendelseBuilder().build();

        // Act
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getGjelderTilbakekreving()).isTrue();
        assertThat(dokumentdata.getLovhjemler()).isEqualTo("folketrygdloven § 21-12 og forvaltningsloven §§ 28, 31, 32 og 33");
        assertThat(dokumentdata.getAvvistGrunner()).containsAll(
            of("KLAGET_FOR_SENT", "KLAGER_IKKE_PART", "KLAGE_UGYLDIG", "IKKE_KONKRET", "IKKE_PAKLAGD_VEDTAK"));
        assertThat(dokumentdata.isPåklagdVedtak()).isFalse();
    }
}

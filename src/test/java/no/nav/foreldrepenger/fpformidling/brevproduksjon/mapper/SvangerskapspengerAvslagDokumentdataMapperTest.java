package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.defaultBuilder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.behandlingsresultat;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.svangerskapspenger;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.svangerskapspengerUttakArbeidsforhold;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.svangerskapspengerUttakPeriode;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.arbeidsgiver.ArbeidsgiverTjeneste;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.uttak.svp.PeriodeIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FellesDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.AktivitetStatusDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.HjemmelDto;

class SvangerskapspengerAvslagDokumentdataMapperTest {
    private static final int KLAGEFRIST = 6;
    private static final int GRUNNBELØP = 100000;
    private static final LocalDate PERIODE1_FOM = LocalDate.now().plusDays(2);
    private static final LocalDate PERIODE1_TOM = LocalDate.now().plusDays(3);
    private static final String ARBEIDSGIVER_1 = "Arbeidsgiver_1";
    private static final String ARBEIDSGIVER_1_REF = "123456";
    private static final Språkkode SPRÅKKODE_NB = Språkkode.NB;

    private final ArbeidsgiverTjeneste arbeidsgiverTjeneste = mock(ArbeidsgiverTjeneste.class);
    private SvangerskapspengerAvslagDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    void before() {
        var brevParametere = new BrevParametere(KLAGEFRIST, 2, Period.ZERO, Period.ZERO);
        when(arbeidsgiverTjeneste.hentArbeidsgiverNavn(any())).thenReturn(ARBEIDSGIVER_1);
        dokumentdataMapper = new SvangerskapspengerAvslagDokumentdataMapper(brevParametere, arbeidsgiverTjeneste);
    }

    @Test
    void skal_mappe_felter_for_brev_med_årsak_fra_behandling() {
        // Arrange
        var behandling = opprettBehandling(Avslagsårsak.ARBEIDSTAKER_KAN_OMPLASSERES);
        var dokumentFelles = lagStandardDokumentFelles(FagsakYtelseType.SVANGERSKAPSPENGER);
        var dokumentHendelse = lagStandardHendelseBuilder().build();

        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getFelles()).isNotNull();
        assertThat(dokumentdata.getFelles().getSøkerNavn()).isEqualTo(DatamapperTestUtil.SØKERS_NAVN);
        assertThat(dokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(DatamapperTestUtil.SØKERS_FNR));
        assertThat(dokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(dokumentdata.getFelles().getHarVerge()).isFalse();
        assertThat(dokumentdata.getFelles().getErKopi()).isFalse();
        assertThat(dokumentdata.getFelles().getSaksnummer()).isEqualTo(DatamapperTestUtil.SAKSNUMMER);
        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo(FellesDokumentdata.YtelseType.SVP);
        assertThat(dokumentdata.getFelles().getErUtkast()).isFalse();

        assertThat(dokumentdata.getÅrsak()).isEqualTo(Årsak.of(Avslagsårsak.ARBEIDSTAKER_KAN_OMPLASSERES.getKode()));
        assertThat(dokumentdata.getMottattDato()).isEqualTo(formaterDato(LocalDate.now(), SPRÅKKODE_NB));
        assertThat(dokumentdata.getAntallArbeidsgivere()).isEqualTo(1);
        assertThat(dokumentdata.getErSøkerDød()).isFalse();
        assertThat(dokumentdata.getHalvG()).isEqualTo(GRUNNBELØP / 2);
        assertThat(dokumentdata.getLovhjemmel()).isEqualTo("§ 14-4");
        assertThat(dokumentdata.getStønadsdatoFom()).isEqualTo(formaterDato(PERIODE1_FOM, SPRÅKKODE_NB));
        assertThat(dokumentdata.getKlagefristUker()).isEqualTo(6);
    }

    @Test
    void skal_mappe_felter_for_brev_med_årsak_fra_uttaket() {
        // Arrange
        var behandling = opprettBehandling(Avslagsårsak.UDEFINERT);
        var dokumentFelles = lagStandardDokumentFelles(FagsakYtelseType.SVANGERSKAPSPENGER);
        var dokumentHendelse = lagStandardHendelseBuilder().build();

        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getFelles()).isNotNull();
        assertThat(dokumentdata.getFelles().getSøkerNavn()).isEqualTo(DatamapperTestUtil.SØKERS_NAVN);
        assertThat(dokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(DatamapperTestUtil.SØKERS_FNR));
        assertThat(dokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(dokumentdata.getFelles().getHarVerge()).isFalse();
        assertThat(dokumentdata.getFelles().getErKopi()).isFalse();
        assertThat(dokumentdata.getFelles().getSaksnummer()).isEqualTo(DatamapperTestUtil.SAKSNUMMER);
        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo(FellesDokumentdata.YtelseType.SVP);
        assertThat(dokumentdata.getFelles().getErUtkast()).isFalse();

        assertThat(dokumentdata.getÅrsak().getKode()).isEqualTo(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT.getKode());
        assertThat(dokumentdata.getMottattDato()).isEqualTo(formaterDato(LocalDate.now(), SPRÅKKODE_NB));
        assertThat(dokumentdata.getAntallArbeidsgivere()).isEqualTo(1);
        assertThat(dokumentdata.getErSøkerDød()).isFalse();
        assertThat(dokumentdata.getHalvG()).isEqualTo(GRUNNBELØP / 2);
        assertThat(dokumentdata.getLovhjemmel()).isEqualTo("§ 14-4");
        assertThat(dokumentdata.getStønadsdatoFom()).isEqualTo(formaterDato(PERIODE1_FOM, SPRÅKKODE_NB));
        assertThat(dokumentdata.getKlagefristUker()).isEqualTo(6);
    }

    private BeregningsgrunnlagDto opprettBeregningsgrunnlag() {
        var periode = new BeregningsgrunnlagPeriodeDto((long) GRUNNBELØP, BigDecimal.valueOf(GRUNNBELØP), BigDecimal.valueOf(GRUNNBELØP), null, null,
            null, null);
        return new BeregningsgrunnlagDto(List.of(AktivitetStatusDto.ARBEIDSTAKER), HjemmelDto.F_14_7, BigDecimal.valueOf(GRUNNBELØP),
            List.of(periode), true, false);
    }

    private BrevGrunnlagDto.Svangerskapspenger opprettUttaksresultat(PeriodeIkkeOppfyltÅrsak periodeÅrsakKode) {
        var periode1 = svangerskapspengerUttakPeriode().fom(PERIODE1_FOM)
            .tom(PERIODE1_TOM)
            .periodeResultatType(BrevGrunnlagDto.PeriodeResultatType.AVSLÅTT)
            .periodeIkkeOppfyltÅrsak(periodeÅrsakKode.getKode())
            .build();

        var arbeidsforhold = svangerskapspengerUttakArbeidsforhold().arbeidsgiverReferanse(ARBEIDSGIVER_1_REF)
            .arbeidType(BrevGrunnlagDto.UttakArbeidType.ORDINÆRT_ARBEID)
            .perioder(List.of(periode1))
            .build();

        return svangerskapspenger().uttakArbeidsforhold(List.of(arbeidsforhold)).build();
    }

    private BrevGrunnlagDto opprettBehandling(Avslagsårsak avslagsårsak) {
        var behandlingsres = behandlingsresultat().behandlingResultatType(BrevGrunnlagDto.Behandlingsresultat.BehandlingResultatType.AVSLÅTT)
            .avslagsårsak(avslagsårsak.getKode())
            .vilkårTyper(List.of(BrevGrunnlagDto.Behandlingsresultat.VilkårType.SVANGERSKAPSPENGERVILKÅR))
            .build();

        return defaultBuilder().fagsakYtelseType(BrevGrunnlagDto.FagsakYtelseType.SVANGERSKAPSPENGER)
            .behandlingsresultat(behandlingsres)
            .behandlingType(BrevGrunnlagDto.BehandlingType.FØRSTEGANGSSØKNAD)
            .språkkode(BrevGrunnlagDto.Språkkode.BOKMÅL)
            .førsteSøknadMottattDato(LocalDate.now())
            .beregningsgrunnlag(opprettBeregningsgrunnlag())
            .svangerskapspenger(opprettUttaksresultat(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT))
            .build();
    }
}

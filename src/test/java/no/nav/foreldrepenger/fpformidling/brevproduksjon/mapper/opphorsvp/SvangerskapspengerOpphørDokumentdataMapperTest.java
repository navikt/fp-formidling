package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.opphorsvp;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.defaultBuilder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.barn;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.behandlingsresultat;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.familieHendelse;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.svangerskapspenger;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.svangerskapspengerUttakArbeidsforhold;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.svangerskapspengerUttakPeriode;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.arbeidsgiver.ArbeidsgiverTjeneste;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.uttak.svp.PeriodeIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FellesDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.AktivitetStatusDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.HjemmelDto;

@ExtendWith(MockitoExtension.class)
class SvangerskapspengerOpphørDokumentdataMapperTest {
    private static final int KLAGEFRIST = 6;
    private static final int GRUNNBELØP = 100000;
    private static final LocalDate PERIODE1_FOM = LocalDate.now().plusDays(2);
    private static final LocalDate PERIODE1_TOM = LocalDate.now().plusDays(3);
    private static final String ARBEIDSGIVER_1_NAVN = "Arbeidsgiver_1";
    private static final String ARBEIDSGIVER_1_REFERANSE = "123456";

    private SvangerskapspengerOpphørDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    void before() {
        var brevParametere = new BrevParametere(KLAGEFRIST, 2, Period.ZERO, Period.ZERO);
        var arbeidsgiverTjeneste = mock(ArbeidsgiverTjeneste.class);
        when(arbeidsgiverTjeneste.hentArbeidsgiverNavn(any())).thenReturn(ARBEIDSGIVER_1_NAVN);
        dokumentdataMapper = new SvangerskapspengerOpphørDokumentdataMapper(brevParametere, arbeidsgiverTjeneste);
    }

    @Test
    void skal_mappe_felter_for_brev() {
        // Arrange
        var opphørsdato = PERIODE1_TOM.plusDays(1);
        var behandling = opprettBehandling(opphørsdato);
        var dokumentFelles = lagStandardDokumentFelles(FagsakYtelseType.SVANGERSKAPSPENGER);
        var dokumentHendelse = lagStandardHendelseBuilder().build();

        // Act
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getFelles()).isNotNull();
        assertThat(dokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(dokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(dokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(dokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoNorsk(LocalDate.now()));
        assertThat(dokumentdata.getFelles().getHarVerge()).isFalse();
        assertThat(dokumentdata.getFelles().getErKopi()).isFalse();
        assertThat(dokumentdata.getFelles().getSaksnummer()).isEqualTo(SAKSNUMMER);
        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo(FellesDokumentdata.YtelseType.SVP);
        assertThat(dokumentdata.getFelles().getErUtkast()).isFalse();

        assertThat(dokumentdata.getOpphørsdato()).isEqualTo(formaterDato(opphørsdato, Språkkode.NB));
        assertThat(dokumentdata.getDødsdatoBarn()).isNull();
        assertThat(dokumentdata.getFødselsdato()).isEqualTo(formaterDato(behandling.familieHendelse().barn().getFirst().fødselsdato(), Språkkode.NB));
        assertThat(dokumentdata.getErSøkerDød()).isFalse();
        assertThat(dokumentdata.getHalvG()).isEqualTo(GRUNNBELØP / 2);
        assertThat(dokumentdata.getLovhjemmel()).isEqualTo("§ 14-4 og forvaltningsloven § 35");
        assertThat(dokumentdata.getOpphørtPeriode().getÅrsak()).isEqualTo(Årsak.of(PeriodeIkkeOppfyltÅrsak.PERIODEN_ER_IKKE_FØR_FØDSEL.getKode()));
        assertThat(dokumentdata.getOpphørtPeriode().getStønadsperiodeFom()).isEqualTo(formaterDato(PERIODE1_FOM, Språkkode.NB));
        assertThat(dokumentdata.getOpphørtPeriode().getStønadsperiodeTom()).isEqualTo(formaterDato(PERIODE1_TOM, Språkkode.NB));
        assertThat(dokumentdata.getOpphørtPeriode().getAntallArbeidsgivere()).isEqualTo(1);
    }

    private BeregningsgrunnlagDto opprettBeregningsgrunnlag() {
        return new BeregningsgrunnlagDto(List.of(AktivitetStatusDto.ARBEIDSTAKER), HjemmelDto.F_14_7, BigDecimal.valueOf(GRUNNBELØP), List.of(), true,
            true);
    }

    private BrevGrunnlagDto.Svangerskapspenger opprettUttaksresultat() {
        var uttakResultatPeriode1 = svangerskapspengerUttakPeriode().fom(PERIODE1_FOM)
            .tom(PERIODE1_TOM)
            .periodeResultatType(BrevGrunnlagDto.PeriodeResultatType.INNVILGET)
            .build();
        var uttakResultatPeriode2 = svangerskapspengerUttakPeriode().fom(PERIODE1_FOM)
            .tom(PERIODE1_TOM)
            .periodeResultatType(BrevGrunnlagDto.PeriodeResultatType.AVSLÅTT)
            .periodeIkkeOppfyltÅrsak(PeriodeIkkeOppfyltÅrsak.PERIODEN_ER_IKKE_FØR_FØDSEL.getKode())
            .build();

        var uttakArbeidsforhold = svangerskapspengerUttakArbeidsforhold().arbeidsgiverReferanse(ARBEIDSGIVER_1_REFERANSE)
            .arbeidType(BrevGrunnlagDto.UttakArbeidType.ORDINÆRT_ARBEID)
            .perioder(List.of(uttakResultatPeriode1, uttakResultatPeriode2))
            .build();

        return svangerskapspenger().uttakArbeidsforhold(List.of(uttakArbeidsforhold)).build();
    }

    private BrevGrunnlagDto opprettBehandling(LocalDate opphørsdato) {
        var fhBarn = barn().fødselsdato(LocalDate.now()).build();

        var fh = familieHendelse().barn(List.of(fhBarn)).termindato(LocalDate.now()).antallBarn(1).build();

        var behandlingsres = behandlingsresultat()
            .behandlingResultatType(BrevGrunnlagDto.Behandlingsresultat.BehandlingResultatType.OPPHØR)
            .opphørsdato(opphørsdato)
            .build();

        return defaultBuilder().fagsakYtelseType(BrevGrunnlagDto.FagsakYtelseType.SVANGERSKAPSPENGER)
            .behandlingType(BrevGrunnlagDto.BehandlingType.REVURDERING)
            .familieHendelse(fh)
            .behandlingsresultat(behandlingsres)
            .beregningsgrunnlag(opprettBeregningsgrunnlag())
            .svangerskapspenger(opprettUttaksresultat())
            .build();
    }
}

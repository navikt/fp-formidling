package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.opphorsvp;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.behandlingsresultat;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.brevGrunnlag;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.svangerskapspengerUttakArbeidsforhold;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.svangerskapspengerUttakPeriode;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.uttak.svp.ArbeidsforholdIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.uttak.svp.PeriodeIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto.TilkjentYtelseAndelDto;
import no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto;

class OpphørPeriodeMapperTest {
    private static final LocalDate PERIODE1_FOM = LocalDate.now().minusDays(10);
    private static final LocalDate PERIODE1_TOM = LocalDate.now().plusDays(5);
    private static final LocalDate PERIODE2_FOM = LocalDate.now().plusDays(6);
    private static final LocalDate PERIODE2_TOM = LocalDate.now().plusDays(10);
    private static final String ARBGIVER_2_NAVN = "Arbeidsgiver2";
    private static final String ARBGIVER_1_NAVN = "Arbeidsgiver1";
    private static final String ARBEIDSGIVER_1_REF = "123456";
    private static final String ARBEIDSGIVER_2_REF = "123457";
    private static final String LOVHJEMLER = "§ 14-4 og forvaltningsloven § 35";
    private static final UnaryOperator<String> HENT_NAVN = ref -> {
        if (ARBEIDSGIVER_1_REF.equals(ref)) {
            return ARBGIVER_1_NAVN;
        } else if (ARBEIDSGIVER_2_REF.equals(ref)) {
            return ARBGIVER_2_NAVN;
        }
        return "Ukjent arbeidsgiver";
    };

    @Test
    void opphør_uten_uttak_og_tilkjent_ytelse() {
        //Arrange
        var svpUttakResultatArbeidsforholdList = List.of(
            opprettUttakArbeidsforhold(ArbeidsforholdIkkeOppfyltÅrsak.INGEN, ARBEIDSGIVER_1_REF, Collections.emptyList()));

        var behandling = brevGrunnlag().uuid(UUID.randomUUID())
            .behandlingsresultat(behandlingsresultat().avslagsårsak(Avslagsårsak.SØKER_SKULLE_IKKE_SØKT_SVP.getKode())
                .vilkårTyper(List.of(BrevGrunnlagDto.Behandlingsresultat.VilkårType.SVANGERSKAPSPENGERVILKÅR))
                .build())
            .build();

        //Act
        var opphørtePerioderOgLovhjemmel = OpphørPeriodeMapper.mapOpphørtePerioderOgLovhjemmel(behandling, svpUttakResultatArbeidsforholdList,
            Språkkode.NB, Collections.emptyList(), Collections.emptyList(), HENT_NAVN);

        //Assert
        var opphørtPeriode = opphørtePerioderOgLovhjemmel.element1();
        var lovhjemler = opphørtePerioderOgLovhjemmel.element2();

        assertThat(opphørtPeriode).isNotNull();
        assertThat(opphørtPeriode.getAntallArbeidsgivere()).isEqualTo(1);
        assertThat(opphørtPeriode.getÅrsak()).isEqualTo(Årsak.of(Avslagsårsak.SØKER_SKULLE_IKKE_SØKT_SVP.getKode()));
        assertThat(lovhjemler).isEqualTo(LOVHJEMLER);
    }


    @Test
    void en_opphørt_Periode_med_en_arbeidsgiver_fra_tikjent_ytelse() {
        //Arrange
        var behandling = brevGrunnlag().uuid(UUID.randomUUID()).behandlingsresultat(behandlingsresultat().build()).build();

        var uttakArbeidsforhold = List.of(opprettUttakArbeidsforhold(ArbeidsforholdIkkeOppfyltÅrsak.INGEN, ARBEIDSGIVER_1_REF,
            List.of(opprettUttaksperiode(BrevGrunnlagDto.PeriodeResultatType.INNVILGET, null, PERIODE1_FOM, PERIODE1_TOM),
                opprettUttaksperiode(BrevGrunnlagDto.PeriodeResultatType.AVSLÅTT, PeriodeIkkeOppfyltÅrsak.PERIODEN_ER_IKKE_FØR_FØDSEL, PERIODE2_FOM,
                    PERIODE2_TOM))));

        var tilkjentYtelsePerioder = opprettTilkjentYtelse(1);

        //Act
        var opphørtePerioderOgLovhjemmel = OpphørPeriodeMapper.mapOpphørtePerioderOgLovhjemmel(behandling, uttakArbeidsforhold, Språkkode.NB,
            Collections.emptyList(), tilkjentYtelsePerioder, HENT_NAVN);

        //Assert
        var opphørtPeriode = opphørtePerioderOgLovhjemmel.element1();
        var lovhjemler = opphørtePerioderOgLovhjemmel.element2();
        assertThat(lovhjemler).isEqualTo(LOVHJEMLER);
        assertThat(opphørtPeriode).isNotNull();
        assertThat(opphørtPeriode.getÅrsak()).isEqualTo(Årsak.of(PeriodeIkkeOppfyltÅrsak.PERIODEN_ER_IKKE_FØR_FØDSEL.getKode()));
        assertThat(opphørtPeriode.getAntallArbeidsgivere()).isEqualTo(1);
        assertThat(opphørtPeriode.getStønadsperiodeFom()).isEqualTo(formaterDato(PERIODE1_FOM, Språkkode.NB));
        assertThat(opphørtPeriode.getStønadsperiodeTom()).isEqualTo(formaterDato(PERIODE2_TOM, Språkkode.NB));
    }

    @Test
    void en_opphørt_periode_med_2_arbeidsgivere_fra_uttak_innvilget() {
        //Arrange
        var behandling = brevGrunnlag().uuid(UUID.randomUUID())
            .behandlingsresultat(behandlingsresultat().avslagsårsak(Avslagsårsak.SØKER_ER_IKKE_BOSATT.getKode())
                .vilkårTyper(List.of(BrevGrunnlagDto.Behandlingsresultat.VilkårType.SVANGERSKAPSPENGERVILKÅR,
                    BrevGrunnlagDto.Behandlingsresultat.VilkårType.MEDLEMSKAPSVILKÅRET))
                .build())
            .build();

        var uttakArbeidsforhold = List.of(opprettUttakArbeidsforhold(ArbeidsforholdIkkeOppfyltÅrsak.INGEN, ARBEIDSGIVER_1_REF,
                List.of(opprettUttaksperiode(BrevGrunnlagDto.PeriodeResultatType.INNVILGET, null, PERIODE1_FOM, PERIODE1_TOM),
                    opprettUttaksperiode(BrevGrunnlagDto.PeriodeResultatType.INNVILGET, null, PERIODE1_FOM, PERIODE1_TOM))),
            opprettUttakArbeidsforhold(ArbeidsforholdIkkeOppfyltÅrsak.INGEN, ARBEIDSGIVER_2_REF,
                List.of(opprettUttaksperiode(BrevGrunnlagDto.PeriodeResultatType.INNVILGET, null, PERIODE2_FOM, PERIODE2_TOM),
                    opprettUttaksperiode(BrevGrunnlagDto.PeriodeResultatType.INNVILGET, null, PERIODE2_FOM, PERIODE2_TOM))));

        List<TilkjentYtelsePeriodeDto> tilkjentYtelsePerioder = Collections.emptyList();

        //Act
        var opphørtePerioderOgLovhjemmel = OpphørPeriodeMapper.mapOpphørtePerioderOgLovhjemmel(behandling, uttakArbeidsforhold, Språkkode.NB,
            Collections.emptyList(), tilkjentYtelsePerioder, HENT_NAVN);

        //Assert
        var opphørtPeriode = opphørtePerioderOgLovhjemmel.element1();
        var lovhjemler = opphørtePerioderOgLovhjemmel.element2();

        assertThat(opphørtPeriode).isNotNull();
        assertThat(lovhjemler).isEqualTo("§ 14-2 og forvaltningsloven § 35");
        assertThat(opphørtPeriode.getÅrsak()).isEqualTo(Årsak.of("1025"));
        assertThat(opphørtPeriode.getAntallArbeidsgivere()).isEqualTo(2);
        assertThat(opphørtPeriode.getStønadsperiodeFom()).isEqualTo(formaterDato(PERIODE1_FOM, Språkkode.NB));
        assertThat(opphørtPeriode.getStønadsperiodeTom()).isEqualTo(formaterDato(PERIODE2_TOM, Språkkode.NB));
    }

    @Test
    void opphør_død_før_uten_tilkjent_ytelse_ingen_stønadsdatoer() {
        //Arrange
        var behandling = brevGrunnlag().uuid(UUID.randomUUID())
            .behandlingsresultat(behandlingsresultat().avslagsårsak(Avslagsårsak.SØKER_ER_IKKE_BOSATT.getKode()).build())
            .build();

        var uttakArbeidsforhold = List.of(opprettUttakArbeidsforhold(ArbeidsforholdIkkeOppfyltÅrsak.INGEN, ARBEIDSGIVER_1_REF,
                List.of(opprettUttaksperiode(BrevGrunnlagDto.PeriodeResultatType.INNVILGET, null, PERIODE1_FOM, PERIODE1_TOM),
                    opprettUttaksperiode(BrevGrunnlagDto.PeriodeResultatType.AVSLÅTT, PeriodeIkkeOppfyltÅrsak.BRUKER_ER_DØD, PERIODE1_FOM, PERIODE1_TOM))),
            opprettUttakArbeidsforhold(ArbeidsforholdIkkeOppfyltÅrsak.INGEN, ARBEIDSGIVER_2_REF,
                List.of(opprettUttaksperiode(BrevGrunnlagDto.PeriodeResultatType.INNVILGET, null, PERIODE2_FOM, PERIODE2_TOM),
                    opprettUttaksperiode(BrevGrunnlagDto.PeriodeResultatType.AVSLÅTT, PeriodeIkkeOppfyltÅrsak.BRUKER_ER_DØD, PERIODE2_FOM,
                        PERIODE2_TOM))));

        List<TilkjentYtelsePeriodeDto> tilkjentYtelsePerioder = Collections.emptyList();

        //Act
        var opphørtePerioderOgLovhjemmel = OpphørPeriodeMapper.mapOpphørtePerioderOgLovhjemmel(behandling, uttakArbeidsforhold, Språkkode.NB,
            Collections.emptyList(), tilkjentYtelsePerioder, HENT_NAVN);

        //Assert
        var opphørtPeriode = opphørtePerioderOgLovhjemmel.element1();
        var lovhjemler = opphørtePerioderOgLovhjemmel.element2();

        assertThat(opphørtPeriode).isNotNull();
        assertThat(lovhjemler).isEqualTo(LOVHJEMLER);
        assertThat(opphørtPeriode.getÅrsak()).isEqualTo(Årsak.of("8304"));
        assertThat(opphørtPeriode.getAntallArbeidsgivere()).isEqualTo(2);
        assertThat(opphørtPeriode.getStønadsperiodeFom()).isBlank();
        assertThat(opphørtPeriode.getStønadsperiodeTom()).isBlank();
    }

    @Test
    void en_opphørsperiode_pga_avslag_på_arbeidsforholdet() {
        //Arrange
        var uttakArbeidsforhold = List.of(
            opprettUttakArbeidsforhold(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE, ARBEIDSGIVER_1_REF, Collections.emptyList()));

        var behandling = brevGrunnlag().uuid(UUID.randomUUID()).behandlingsresultat(behandlingsresultat().build()).build();

        //Act
        var opphørtePerioderOgLovhjemmel = OpphørPeriodeMapper.mapOpphørtePerioderOgLovhjemmel(behandling, uttakArbeidsforhold, Språkkode.NB,
            Collections.emptyList(), Collections.emptyList(), HENT_NAVN);

        //Assert
        var opphørtPeriode = opphørtePerioderOgLovhjemmel.element1();
        var lovhjemler = opphørtePerioderOgLovhjemmel.element2();

        assertThat(lovhjemler).isEqualTo(LOVHJEMLER);
        assertThat(opphørtPeriode).isNotNull();
        assertThat(opphørtPeriode.getÅrsak()).isEqualTo(Årsak.of(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE.getKode()));
        assertThat(opphørtPeriode.getAntallArbeidsgivere()).isEqualTo(1);
    }

    private List<TilkjentYtelsePeriodeDto> opprettTilkjentYtelse(int antallArbeidsgivere) {
        var andel1 = new TilkjentYtelseAndelDto(ARBEIDSGIVER_1_REF, 500, 100, null, null, null, null);
        var andel2 = new TilkjentYtelseAndelDto(antallArbeidsgivere == 1 ? ARBEIDSGIVER_1_REF : ARBEIDSGIVER_2_REF, 300, 100, null, null, null, null);
        var andel3 = new TilkjentYtelseAndelDto(antallArbeidsgivere == 1 ? ARBEIDSGIVER_1_REF : ARBEIDSGIVER_2_REF, 0, 0, null, null, null, null);

        return of(new TilkjentYtelsePeriodeDto(PERIODE1_FOM, PERIODE1_TOM, 500, of(andel1)),
            new TilkjentYtelsePeriodeDto(PERIODE2_FOM, PERIODE2_TOM, 300, of(andel2)),
            new TilkjentYtelsePeriodeDto(LocalDate.now().plusDays(8), LocalDate.now().plusDays(12), 0, of(andel3)));
    }

    private BrevGrunnlagDto.Svangerskapspenger.UttakArbeidsforhold opprettUttakArbeidsforhold(ArbeidsforholdIkkeOppfyltÅrsak arbeidsforholdIkkeOppfyltÅrsak,
                                                                                              String arbeidsgiverRef,
                                                                                              List<BrevGrunnlagDto.Svangerskapspenger.Uttaksperiode> perioder) {

        return svangerskapspengerUttakArbeidsforhold().arbeidsforholdIkkeOppfyltÅrsak(arbeidsforholdIkkeOppfyltÅrsak.getKode())
            .arbeidsgiverReferanse(arbeidsgiverRef)
            .arbeidType(BrevGrunnlagDto.UttakArbeidType.ORDINÆRT_ARBEID)
            .perioder(perioder)
            .build();
    }

    private BrevGrunnlagDto.Svangerskapspenger.Uttaksperiode opprettUttaksperiode(BrevGrunnlagDto.PeriodeResultatType periodeResultatType,
                                                                                  PeriodeIkkeOppfyltÅrsak periodeIkkeOppfyltÅrsak,
                                                                                  LocalDate fom,
                                                                                  LocalDate tom) {

        return svangerskapspengerUttakPeriode().fom(fom)
            .tom(tom)
            .periodeResultatType(periodeResultatType)
            .periodeIkkeOppfyltÅrsak(periodeIkkeOppfyltÅrsak != null ? periodeIkkeOppfyltÅrsak.getKode() : null)
            .build();
    }
}

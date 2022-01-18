package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.opphørsvp;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;
import static no.nav.foreldrepenger.fpformidling.typer.DatoIntervall.fraOgMedTilOgMed;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.Tuple;
import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.opphørsvp.OpphørPeriode;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseAndel;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseForeldrepenger;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelsePeriode;
import no.nav.foreldrepenger.fpformidling.typer.DatoIntervall;
import no.nav.foreldrepenger.fpformidling.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.fpformidling.uttak.UttakArbeidType;
import no.nav.foreldrepenger.fpformidling.uttak.svp.ArbeidsforholdIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.fpformidling.uttak.svp.PeriodeIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvpUttakResultatArbeidsforhold;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvpUttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.fpformidling.virksomhet.Arbeidsgiver;

class OpphørPeriodeMapperTest {
    private static final LocalDate PERIODE1_FOM = LocalDate.now().minusDays(10);
    private static final LocalDate PERIODE1_TOM = LocalDate.now().plusDays(5);
    private static final LocalDate PERIODE2_FOM = LocalDate.now().plusDays(6);
    private static final LocalDate PERIODE2_TOM = LocalDate.now().plusDays(10);
    private static final String ARBGIVER_2_NAVN = "Arbeidsgiver2";
    private static final String ARBGIVER_1_NAVN = "Arbeidsgiver1";
    private static final Arbeidsgiver ARBEIDSGIVER_1 = new Arbeidsgiver("123456", ARBGIVER_1_NAVN );
    private static final Arbeidsgiver ARBEIDSGIVER_2 = new Arbeidsgiver("123457", ARBGIVER_2_NAVN );
    private static final String LOVHJEMLER = "§ 14-4 og forvaltningsloven § 35";

    @Test
    public void  opphør_uten_uttak_og_tilkjent_ytelse() {
        //Arrange
        List<SvpUttakResultatArbeidsforhold> svpUttakResultatArbeidsforholdList = List.of(opprettUttakArbeidsforhold(ArbeidsforholdIkkeOppfyltÅrsak.INGEN, ARBEIDSGIVER_1, Collections.emptyList()));

        Behandling behandling  = standardBehandlingBuilder()
                .medBehandlingsresultat(Behandlingsresultat.builder()
                        .medAvslagsårsak(Avslagsårsak.SØKER_SKULLE_IKKE_SØKT_SVP)
                        .build())
                .build();

        //Act
        Tuple<OpphørPeriode, String> opphørtePerioderOgLovhjemmel = OpphørPeriodeMapper.mapOpphørtePerioderOgLovhjemmel(behandling, svpUttakResultatArbeidsforholdList, Språkkode.NB, null, Collections.emptyList());

        //Assert
        OpphørPeriode opphørtPeriode = opphørtePerioderOgLovhjemmel.element1();
        String lovhjemler = opphørtePerioderOgLovhjemmel.element2();

        assertThat(opphørtPeriode).isNotNull();
        assertThat(opphørtPeriode.getAntallArbeidsgivere()).isEqualTo(1);
        assertThat(opphørtPeriode.getÅrsak()).isEqualTo(Årsak.of(Avslagsårsak.SØKER_SKULLE_IKKE_SØKT_SVP.getKode()));
        assertThat(lovhjemler).isEqualTo(LOVHJEMLER);

    }


    @Test
    public void  en_opphørt_Periode_med_en_arbeidsgiver_fra_tikjent_ytelse() {
        //Arrange
        Behandling behandling  = standardBehandlingBuilder()
                .medBehandlingsresultat(Behandlingsresultat.builder()
                        .build())
                .build();

        List<SvpUttakResultatArbeidsforhold> uttakArbeidsforhold = List.of(opprettUttakArbeidsforhold(ArbeidsforholdIkkeOppfyltÅrsak.INGEN, ARBEIDSGIVER_1,
                List.of(opprettUttaksperiode(PeriodeResultatType.INNVILGET, null, ARBGIVER_1_NAVN, DatoIntervall.fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM )),
                        opprettUttaksperiode(PeriodeResultatType.AVSLÅTT, PeriodeIkkeOppfyltÅrsak._8309, ARBGIVER_1_NAVN, DatoIntervall.fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM )))));

        List<TilkjentYtelsePeriode> tilkjentYtelsePerioder = opprettTilkjentYtelse(1).getPerioder();

        //Act
        Tuple<OpphørPeriode, String> opphørtePerioderOgLovhjemmel = OpphørPeriodeMapper.mapOpphørtePerioderOgLovhjemmel(behandling, uttakArbeidsforhold, Språkkode.NB, null, tilkjentYtelsePerioder);

        //Assert
        OpphørPeriode opphørtPeriode = opphørtePerioderOgLovhjemmel.element1();
        String lovhjemler = opphørtePerioderOgLovhjemmel.element2();
        assertThat(lovhjemler).isEqualTo(LOVHJEMLER);
        assertThat(opphørtPeriode).isNotNull();
        assertThat(opphørtPeriode.getÅrsak()).isEqualTo(Årsak.of(PeriodeIkkeOppfyltÅrsak._8309.getKode()));
        assertThat(opphørtPeriode.getAntallArbeidsgivere()).isEqualTo(1);
        assertThat(opphørtPeriode.getStønadsperiodeFom()).isEqualTo(formaterDato(PERIODE1_FOM, Språkkode.NB));
        assertThat(opphørtPeriode.getStønadsperiodeTom()).isEqualTo(formaterDato(PERIODE2_TOM, Språkkode.NB));

    }

    @Test
    public void  en_opphørt_periode_med_2_arbeidsgivere_fra_uttak_innvilget() {
        //Arrange
        Behandling behandling  = standardBehandlingBuilder()
                .medBehandlingsresultat(Behandlingsresultat.builder()
                        .medAvslagsårsak(Avslagsårsak.SØKER_ER_IKKE_BOSATT)
                        .build())
                .build();

        List<SvpUttakResultatArbeidsforhold> uttakArbeidsforhold = List.of(opprettUttakArbeidsforhold(ArbeidsforholdIkkeOppfyltÅrsak.INGEN, ARBEIDSGIVER_1,
                List.of(opprettUttaksperiode(PeriodeResultatType.INNVILGET, null, ARBGIVER_1_NAVN, DatoIntervall.fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM )),
                        opprettUttaksperiode(PeriodeResultatType.INNVILGET, null, ARBGIVER_1_NAVN, DatoIntervall.fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM )))
        ), opprettUttakArbeidsforhold(ArbeidsforholdIkkeOppfyltÅrsak.INGEN, ARBEIDSGIVER_2,
                        List.of(opprettUttaksperiode(PeriodeResultatType.INNVILGET, null, ARBGIVER_2_NAVN, DatoIntervall.fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM )),
                                opprettUttaksperiode(PeriodeResultatType.INNVILGET, null, ARBGIVER_2_NAVN, DatoIntervall.fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM )))));

        List<TilkjentYtelsePeriode> tilkjentYtelsePerioder = Collections.emptyList();

        //Act
        Tuple<OpphørPeriode, String> opphørtePerioderOgLovhjemmel = OpphørPeriodeMapper.mapOpphørtePerioderOgLovhjemmel(behandling, uttakArbeidsforhold, Språkkode.NB, null, tilkjentYtelsePerioder);

        //Assert
        OpphørPeriode opphørtPeriode = opphørtePerioderOgLovhjemmel.element1();
        String lovhjemler = opphørtePerioderOgLovhjemmel.element2();

        assertThat(opphørtPeriode).isNotNull();
        assertThat(lovhjemler).isEqualTo("§ 14-2 og forvaltningsloven § 35");
        assertThat(opphørtPeriode.getÅrsak()).isEqualTo(Årsak.of("1025"));
        assertThat(opphørtPeriode.getAntallArbeidsgivere()).isEqualTo(2);
        assertThat(opphørtPeriode.getStønadsperiodeFom()).isEqualTo(formaterDato(PERIODE1_FOM, Språkkode.NB));
        assertThat(opphørtPeriode.getStønadsperiodeTom()).isEqualTo(formaterDato(PERIODE2_TOM, Språkkode.NB));
    }

    @Test
    public void  opphør_død_før_uten_tilkjent_ytelse_ingen_stønadsdatoer() {
        //Arrange
        Behandling behandling  = standardBehandlingBuilder()
                .medBehandlingsresultat(Behandlingsresultat.builder()
                        .medAvslagsårsak(Avslagsårsak.SØKER_ER_IKKE_BOSATT)
                        .build())
                .build();

        List<SvpUttakResultatArbeidsforhold> uttakArbeidsforhold = List.of(opprettUttakArbeidsforhold(ArbeidsforholdIkkeOppfyltÅrsak.INGEN, ARBEIDSGIVER_1,
                List.of(opprettUttaksperiode(PeriodeResultatType.INNVILGET, null, ARBGIVER_1_NAVN, DatoIntervall.fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM )),
                        opprettUttaksperiode(PeriodeResultatType.AVSLÅTT, PeriodeIkkeOppfyltÅrsak._8304, ARBGIVER_1_NAVN, DatoIntervall.fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM )))
        ), opprettUttakArbeidsforhold(ArbeidsforholdIkkeOppfyltÅrsak.INGEN, ARBEIDSGIVER_2,
                List.of(opprettUttaksperiode(PeriodeResultatType.INNVILGET, null, ARBGIVER_2_NAVN, DatoIntervall.fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM )),
                        opprettUttaksperiode(PeriodeResultatType.AVSLÅTT, PeriodeIkkeOppfyltÅrsak._8304, ARBGIVER_2_NAVN, DatoIntervall.fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM )))));

        List<TilkjentYtelsePeriode> tilkjentYtelsePerioder = Collections.emptyList();

        //Act
        Tuple<OpphørPeriode, String> opphørtePerioderOgLovhjemmel = OpphørPeriodeMapper.mapOpphørtePerioderOgLovhjemmel(behandling, uttakArbeidsforhold, Språkkode.NB, null, tilkjentYtelsePerioder);

        //Assert
        OpphørPeriode opphørtPeriode = opphørtePerioderOgLovhjemmel.element1();
        String lovhjemler = opphørtePerioderOgLovhjemmel.element2();

        assertThat(opphørtPeriode).isNotNull();
        assertThat(lovhjemler).isEqualTo(LOVHJEMLER);
        assertThat(opphørtPeriode.getÅrsak()).isEqualTo(Årsak.of("8304"));
        assertThat(opphørtPeriode.getAntallArbeidsgivere()).isEqualTo(2);
        assertThat(opphørtPeriode.getStønadsperiodeFom()).isBlank();
        assertThat(opphørtPeriode.getStønadsperiodeTom()).isBlank();
    }

    @Test
    public void  en_opphørsperiode_pga_avslag_på_arbeidsforholdet() {
        //Arrange
        List<SvpUttakResultatArbeidsforhold> uttakArbeidsforhold = List.of(opprettUttakArbeidsforhold(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE, ARBEIDSGIVER_1, Collections.emptyList()));

        Behandling behandling  = standardBehandlingBuilder()
                .medBehandlingsresultat(Behandlingsresultat.builder()
                        .build())
                .build();
        //Act
        Tuple<OpphørPeriode, String> opphørtePerioderOgLovhjemmel = OpphørPeriodeMapper.mapOpphørtePerioderOgLovhjemmel(behandling, uttakArbeidsforhold, Språkkode.NB, null, Collections.emptyList());

        //Assert
        OpphørPeriode opphørtPeriode = opphørtePerioderOgLovhjemmel.element1();
        String lovhjemler = opphørtePerioderOgLovhjemmel.element2();

        assertThat(lovhjemler).isEqualTo(LOVHJEMLER);
        assertThat(opphørtPeriode).isNotNull();
        assertThat(opphørtPeriode.getÅrsak()).isEqualTo(Årsak.of(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE.getKode()));
        assertThat(opphørtPeriode.getAntallArbeidsgivere()).isEqualTo(1);

    }

    private TilkjentYtelseForeldrepenger opprettTilkjentYtelse(int antallArbeidsgivere) {
        return TilkjentYtelseForeldrepenger.ny()
                .leggTilPerioder(of(
                        TilkjentYtelsePeriode.ny()
                                .medDagsats(500L)
                                .medPeriode(fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
                                .medAndeler(of(TilkjentYtelseAndel.ny()
                                        .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                        .medArbeidsgiver(ARBEIDSGIVER_1)
                                        .medStillingsprosent(BigDecimal.valueOf(50))
                                        .build()))
                                .build(),
                        TilkjentYtelsePeriode.ny()
                                .medDagsats(300L)
                                .medPeriode(fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM))
                                .medAndeler(of(TilkjentYtelseAndel.ny()
                                        .medArbeidsgiver(antallArbeidsgivere == 1 ? ARBEIDSGIVER_1 : ARBEIDSGIVER_2)
                                        .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                        .medStillingsprosent(BigDecimal.valueOf(50))
                                        .build()))
                                .build(),
                        TilkjentYtelsePeriode.ny()
                                .medDagsats(0L)
                                .medPeriode(fraOgMedTilOgMed(LocalDate.now().plusDays(8), LocalDate.now().plusDays(12)))
                                .medAndeler(of(TilkjentYtelseAndel.ny()
                                        .medArbeidsgiver(antallArbeidsgivere == 1 ? ARBEIDSGIVER_1 : ARBEIDSGIVER_2)
                                        .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                        .medStillingsprosent(BigDecimal.valueOf(50))
                                        .build()))
                                .build()))
                .build();
    }

    private SvpUttakResultatArbeidsforhold opprettUttakArbeidsforhold(ArbeidsforholdIkkeOppfyltÅrsak arbeidsforholdIkkeOppfyltÅrsak, Arbeidsgiver arbeidsgiver, List<SvpUttakResultatPeriode> perioder) {

        return SvpUttakResultatArbeidsforhold.Builder.ny()
                .medArbeidsforholdIkkeOppfyltÅrsak(arbeidsforholdIkkeOppfyltÅrsak)
                .medUttakArbeidType(UttakArbeidType.ORDINÆRT_ARBEID)
                .medArbeidsgiver(arbeidsgiver)
                .leggTilPerioder(perioder)
                .build();
    }

    private SvpUttakResultatPeriode opprettUttaksperiode(PeriodeResultatType periodeResultatType, PeriodeIkkeOppfyltÅrsak periodeIkkeOppfyltÅrsak, String arbeidsgiverNavn, DatoIntervall tidsperiode) {
        return SvpUttakResultatPeriode.Builder.ny()
                .medPeriodeResultatType(periodeResultatType)
                .medPeriodeIkkeOppfyltÅrsak(periodeIkkeOppfyltÅrsak)
                .medArbeidsgiverNavn(arbeidsgiverNavn)
                .medTidsperiode(tidsperiode)
                .build();

    }

    private Behandling.Builder standardBehandlingBuilder() {
        return Behandling.builder().medUuid(UUID.randomUUID());
    }
}
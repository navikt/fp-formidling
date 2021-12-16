package no.nav.foreldrepenger.melding.brevmapper.brev.opphørsvp;

import no.nav.foreldrepenger.melding.Tuple;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatAndel;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.opphørsvp.OpphørPeriode;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.UttakArbeidType;
import no.nav.foreldrepenger.melding.uttak.svp.ArbeidsforholdIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.melding.uttak.svp.PeriodeIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatArbeidsforhold;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatPeriode;
import no.nav.foreldrepenger.melding.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.List.of;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDato;
import static no.nav.foreldrepenger.melding.typer.DatoIntervall.fraOgMedTilOgMed;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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
    public void  en_opphørt_Periode_med_en_arbeidsgiver() {
        //Arrange
        Behandling behandling  = standardBehandlingBuilder()
                .medBehandlingsresultat(Behandlingsresultat.builder()
                        .build())
                .build();

        List<SvpUttakResultatArbeidsforhold> uttakArbeidsforhold = List.of(opprettUttakArbeidsforhold(ArbeidsforholdIkkeOppfyltÅrsak.INGEN, ARBEIDSGIVER_1,
                List.of(opprettUttaksperiode(PeriodeResultatType.INNVILGET, null, ARBGIVER_1_NAVN, DatoIntervall.fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM )),
                        opprettUttaksperiode(PeriodeResultatType.AVSLÅTT, PeriodeIkkeOppfyltÅrsak._8309, ARBGIVER_1_NAVN, DatoIntervall.fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM )))));

        List<BeregningsresultatPeriode> tilkjentYtelsePerioder = opprettBeregningsresultat(1).getBeregningsresultatPerioder();

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
    public void  en_opphørt_periode_med_2_arbeidsgivere() {
        //Arrange
        Behandling behandling  = standardBehandlingBuilder()
                .medBehandlingsresultat(Behandlingsresultat.builder()
                        .build())
                .build();

        List<SvpUttakResultatArbeidsforhold> uttakArbeidsforhold = List.of(opprettUttakArbeidsforhold(ArbeidsforholdIkkeOppfyltÅrsak.INGEN, ARBEIDSGIVER_1,
                List.of(opprettUttaksperiode(PeriodeResultatType.INNVILGET, null, ARBGIVER_1_NAVN, DatoIntervall.fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM )),
                        opprettUttaksperiode(PeriodeResultatType.AVSLÅTT, PeriodeIkkeOppfyltÅrsak._8304, ARBGIVER_1_NAVN, DatoIntervall.fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM )))
        ), opprettUttakArbeidsforhold(ArbeidsforholdIkkeOppfyltÅrsak.INGEN, ARBEIDSGIVER_2,
                        List.of(opprettUttaksperiode(PeriodeResultatType.INNVILGET, null, ARBGIVER_2_NAVN, DatoIntervall.fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM )),
                                opprettUttaksperiode(PeriodeResultatType.AVSLÅTT, PeriodeIkkeOppfyltÅrsak._8304, ARBGIVER_2_NAVN, DatoIntervall.fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM )))));

        List<BeregningsresultatPeriode> tilkjentYtelsePerioder = opprettBeregningsresultat(2).getBeregningsresultatPerioder();

        //Act
        Tuple<OpphørPeriode, String> opphørtePerioderOgLovhjemmel = OpphørPeriodeMapper.mapOpphørtePerioderOgLovhjemmel(behandling, uttakArbeidsforhold, Språkkode.NB, null, tilkjentYtelsePerioder);

        //Assert
        OpphørPeriode opphørtPeriode = opphørtePerioderOgLovhjemmel.element1();
        String lovhjemler = opphørtePerioderOgLovhjemmel.element2();

        assertThat(opphørtPeriode).isNotNull();
        assertThat(lovhjemler).isEqualTo(LOVHJEMLER);
        assertThat(opphørtPeriode.getÅrsak()).isEqualTo(Årsak.of(PeriodeIkkeOppfyltÅrsak._8304.getKode()));
        assertThat(opphørtPeriode.getAntallArbeidsgivere()).isEqualTo(2);
        assertThat(opphørtPeriode.getStønadsperiodeFom()).isEqualTo(formaterDato(PERIODE1_FOM, Språkkode.NB));
        assertThat(opphørtPeriode.getStønadsperiodeTom()).isEqualTo(formaterDato(PERIODE2_TOM, Språkkode.NB));
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

    private BeregningsresultatFP opprettBeregningsresultat(int antallArbeidsgivere) {
        return BeregningsresultatFP.ny()
                .leggTilBeregningsresultatPerioder(of(
                        BeregningsresultatPeriode.ny()
                                .medDagsats(500L)
                                .medPeriode(fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
                                .medBeregningsresultatAndel(of(BeregningsresultatAndel.ny()
                                        .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                        .medArbeidsgiver(ARBEIDSGIVER_1)
                                        .medStillingsprosent(BigDecimal.valueOf(50))
                                        .build()))
                                .build(),
                        BeregningsresultatPeriode.ny()
                                .medDagsats(300L)
                                .medPeriode(fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM))
                                .medBeregningsresultatAndel(of(BeregningsresultatAndel.ny()
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
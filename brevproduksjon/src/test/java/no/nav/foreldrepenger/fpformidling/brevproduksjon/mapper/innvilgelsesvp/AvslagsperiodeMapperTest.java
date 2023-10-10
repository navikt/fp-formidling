package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.fpformidling.uttak.fp.UttakArbeidType;
import no.nav.foreldrepenger.fpformidling.virksomhet.Arbeidsgiver;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.typer.DatoIntervall;
import no.nav.foreldrepenger.fpformidling.uttak.fp.PeriodeResultatType;
import no.nav.foreldrepenger.fpformidling.uttak.svp.PeriodeIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvpUttakResultatArbeidsforhold;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvpUttakResultatPeriode;

class AvslagsperiodeMapperTest {

    private static final String ARBEIDSGIVER1_NAVN = "Arbeidsgiver1 AS";
    private static final LocalDate PERIODE1_FOM = LocalDate.now().minusDays(10); //Slås sammen med periode 2 og 6
    private static final LocalDate PERIODE1_TOM = LocalDate.now().minusDays(1);
    private static final LocalDate PERIODE2_FOM = LocalDate.now();               //Slås sammen med periode 1 og 6
    private static final LocalDate PERIODE2_TOM = LocalDate.now().plusDays(10);

    private static final String ARBEIDSGIVER2_NAVN = "Arbeidsgiver2 AS";
    private static final LocalDate PERIODE3_FOM = LocalDate.now().plusDays(11); //Slås sammen med periode 7 (overlapp)
    private static final LocalDate PERIODE3_TOM = LocalDate.now().plusDays(14);
    private static final LocalDate PERIODE4_FOM = LocalDate.now().plusDays(19); //Slås ikke sammen med noen pga datoene
    private static final LocalDate PERIODE4_TOM = LocalDate.now().plusDays(20);
    private static final LocalDate PERIODE5_FOM = LocalDate.now().plusDays(21); //Utelates helt pga årsaken
    private static final LocalDate PERIODE5_TOM = LocalDate.now().plusDays(22);

    private static final String ARBEIDSGIVER3_NAVN = "Arbeidsgiver3 AS";
    private static final LocalDate PERIODE6_FOM = LocalDate.now().plusDays(11); //Slås sammen med periode 1 og 2
    private static final LocalDate PERIODE6_TOM = LocalDate.now().plusDays(12);
    private static final LocalDate PERIODE7_FOM = LocalDate.now().plusDays(13); //Slås sammen med periode 3 (overlapp)
    private static final LocalDate PERIODE7_TOM = LocalDate.now().plusDays(14);

    @Test
    void skal_mappe_perioder_med_riktig_årsak_og_slå_sammen_sammenhengende_perioder_med_samme_årsak_på_tvers_av_arbeidsgivere() {
        // Arrange
        var svpUttakResultatArbeidsforhold = getSvpUttakResultatArbeidsforhold();

        // Act
        var resultat = AvslagsperiodeMapper.mapAvslagsperioder(svpUttakResultatArbeidsforhold, Språkkode.NB);

        // Assert
        assertThat(resultat).hasSize(4);
        assertThat(resultat.get(0).getPeriodeFom()).isEqualTo(PERIODE1_FOM);
        assertThat(resultat.get(0).getPeriodeTom()).isEqualTo(PERIODE6_TOM);
        assertThat(resultat.get(0).getÅrsak()).isEqualTo(Årsak.of("8308"));
        assertThat(resultat.get(0).getArbeidsforholdInformasjon()).isNull();
        assertThat(resultat.get(1).getPeriodeFom()).isEqualTo(PERIODE3_FOM);
        assertThat(resultat.get(1).getPeriodeTom()).isEqualTo(PERIODE7_TOM);
        assertThat(resultat.get(1).getÅrsak()).isEqualTo(Årsak.of("8311"));
        assertThat(resultat.get(1).getArbeidsforholdInformasjon().arbeidsgivernavn()).isEqualTo(ARBEIDSGIVER2_NAVN);
        assertThat(resultat.get(2).getPeriodeFom()).isEqualTo(PERIODE7_FOM);
        assertThat(resultat.get(2).getPeriodeTom()).isEqualTo(PERIODE7_TOM);
        assertThat(resultat.get(2).getÅrsak()).isEqualTo(Årsak.of("8311"));
        assertThat(resultat.get(2).getArbeidsforholdInformasjon().arbeidsgivernavn()).isEqualTo(ARBEIDSGIVER3_NAVN);
        assertThat(resultat.get(3).getPeriodeFom()).isEqualTo(PERIODE4_FOM);
        assertThat(resultat.get(3).getPeriodeTom()).isEqualTo(PERIODE4_TOM);
        assertThat(resultat.get(3).getÅrsak()).isEqualTo(Årsak.of("8311"));
        assertThat(resultat.get(2).getArbeidsforholdInformasjon().arbeidsgivernavn()).isEqualTo(ARBEIDSGIVER3_NAVN);
    }

    private List<SvpUttakResultatArbeidsforhold> getSvpUttakResultatArbeidsforhold() {
        var uttakPeriode1 = SvpUttakResultatPeriode.Builder.ny()
            .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
            .medArbeidsgiverNavn(ARBEIDSGIVER1_NAVN)
            .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
            .medPeriodeIkkeOppfyltÅrsak(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT)
            .build();
        var uttakPeriode2 = SvpUttakResultatPeriode.Builder.ny()
            .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM))
            .medArbeidsgiverNavn(ARBEIDSGIVER1_NAVN)
            .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
            .medPeriodeIkkeOppfyltÅrsak(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT)
            .build();
        var arbeidsforhold1 = SvpUttakResultatArbeidsforhold.Builder.ny()
            .medArbeidsgiver(new Arbeidsgiver("123456789", ARBEIDSGIVER1_NAVN))
            .medUttakArbeidType(UttakArbeidType.ORDINÆRT_ARBEID)
            .leggTilPerioder(of(uttakPeriode1, uttakPeriode2)).build();

        var uttakPeriode3 = SvpUttakResultatPeriode.Builder.ny()
            .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE3_FOM, PERIODE3_TOM))
            .medArbeidsgiverNavn(ARBEIDSGIVER2_NAVN)
            .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
            .medPeriodeIkkeOppfyltÅrsak(PeriodeIkkeOppfyltÅrsak.PERIODE_SAMTIDIG_SOM_FERIE)
            .build();
        var uttakPeriode4 = SvpUttakResultatPeriode.Builder.ny()
            .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE4_FOM, PERIODE4_TOM))
            .medArbeidsgiverNavn(ARBEIDSGIVER2_NAVN)
            .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
            .medPeriodeIkkeOppfyltÅrsak(PeriodeIkkeOppfyltÅrsak.PERIODE_SAMTIDIG_SOM_FERIE)
            .build();
        var uttakPeriode5 = SvpUttakResultatPeriode.Builder.ny()
            .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE5_FOM, PERIODE5_TOM))
            .medArbeidsgiverNavn(ARBEIDSGIVER2_NAVN)
            .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
            .medPeriodeIkkeOppfyltÅrsak(PeriodeIkkeOppfyltÅrsak.OPPTJENINGSVILKÅRET_IKKE_OPPFYLT) //Mappes ikke
            .build();

        var arbeidsforhold2 = SvpUttakResultatArbeidsforhold.Builder.ny()
            .medArbeidsgiver(new Arbeidsgiver("988765432", ARBEIDSGIVER2_NAVN))
            .medUttakArbeidType(UttakArbeidType.ORDINÆRT_ARBEID)
            .leggTilPerioder(of(uttakPeriode3, uttakPeriode4, uttakPeriode5)).build();

        var uttakPeriode6 = SvpUttakResultatPeriode.Builder.ny()
            .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE6_FOM, PERIODE6_TOM))
            .medArbeidsgiverNavn(ARBEIDSGIVER3_NAVN)
            .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
            .medPeriodeIkkeOppfyltÅrsak(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT)
            .build();
        var uttakPeriode7 = SvpUttakResultatPeriode.Builder.ny()
            .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE7_FOM, PERIODE7_TOM))
            .medArbeidsgiverNavn(ARBEIDSGIVER3_NAVN)
            .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
            .medPeriodeIkkeOppfyltÅrsak(PeriodeIkkeOppfyltÅrsak.PERIODE_SAMTIDIG_SOM_FERIE)
            .build();

        var arbeidsforhold3 = SvpUttakResultatArbeidsforhold.Builder.ny()
            .medArbeidsgiver(new Arbeidsgiver("56432198", ARBEIDSGIVER3_NAVN))
            .medUttakArbeidType(UttakArbeidType.ORDINÆRT_ARBEID)
            .leggTilPerioder(of(uttakPeriode6, uttakPeriode7)).build();


        return of(arbeidsforhold1, arbeidsforhold2, arbeidsforhold3);
    }
}

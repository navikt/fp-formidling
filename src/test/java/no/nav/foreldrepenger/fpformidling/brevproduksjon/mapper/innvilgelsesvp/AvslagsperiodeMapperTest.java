package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.svangerskapspengerUttakArbeidsforhold;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.svangerskapspengerUttakPeriode;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;

class AvslagsperiodeMapperTest {

    private static final String ARBEIDSGIVER1_NAVN = "Arbeidsgiver1 AS";
    private static final String ARBEIDSGIVER1_REF = "123456789";
    private static final LocalDate PERIODE1_FOM = LocalDate.now().minusDays(10); //Slås sammen med periode 2 og 6
    private static final LocalDate PERIODE1_TOM = LocalDate.now().minusDays(1);
    private static final LocalDate PERIODE2_FOM = LocalDate.now();               //Slås sammen med periode 1 og 6
    private static final LocalDate PERIODE2_TOM = LocalDate.now().plusDays(10);

    private static final String ARBEIDSGIVER2_NAVN = "Arbeidsgiver2 AS";
    private static final String ARBEIDSGIVER2_REF = "11111111";
    private static final LocalDate PERIODE3_FOM = LocalDate.now().plusDays(11); //Slås sammen med periode 7 (overlapp)
    private static final LocalDate PERIODE3_TOM = LocalDate.now().plusDays(14);
    private static final LocalDate PERIODE4_FOM = LocalDate.now().plusDays(19); //Slås ikke sammen med noen pga datoene
    private static final LocalDate PERIODE4_TOM = LocalDate.now().plusDays(20);
    private static final LocalDate PERIODE5_FOM = LocalDate.now().plusDays(21); //Utelates helt pga årsaken
    private static final LocalDate PERIODE5_TOM = LocalDate.now().plusDays(22);

    private static final String ARBEIDSGIVER3_NAVN = "Arbeidsgiver3 AS";
    private static final String ARBEIDSGIVER3_REF = "22222222";
    private static final LocalDate PERIODE6_FOM = LocalDate.now().plusDays(11); //Slås sammen med periode 1 og 2
    private static final LocalDate PERIODE6_TOM = LocalDate.now().plusDays(12);
    private static final LocalDate PERIODE7_FOM = LocalDate.now().plusDays(13); //Slås sammen med periode 3 (overlapp)
    private static final LocalDate PERIODE7_TOM = LocalDate.now().plusDays(14);

    @Test
    void skal_mappe_perioder_med_riktig_årsak_og_slå_sammen_sammenhengende_perioder_med_samme_årsak_på_tvers_av_arbeidsgivere() {
        // Arrange
        var svpUttakResultatArbeidsforhold = getSvpUttakResultatArbeidsforhold();

        // Act
        var resultat = AvslagsperiodeMapper.mapAvslagsperioder(svpUttakResultatArbeidsforhold, Språkkode.NB, this::hentArbeidsgiverNavn);

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
        assertThat(resultat.get(3).getArbeidsforholdInformasjon().arbeidsgivernavn()).isEqualTo(ARBEIDSGIVER2_NAVN);
    }

    private String hentArbeidsgiverNavn(String arbeidsgiverRef) {
        return switch (arbeidsgiverRef) {
            case ARBEIDSGIVER1_REF -> ARBEIDSGIVER1_NAVN;
            case ARBEIDSGIVER2_REF -> ARBEIDSGIVER2_NAVN;
            case ARBEIDSGIVER3_REF -> ARBEIDSGIVER3_NAVN;
            default -> "Ukjent arbeidsgiver";
        };
    }

    private List<BrevGrunnlagDto.Svangerskapspenger.UttakArbeidsforhold> getSvpUttakResultatArbeidsforhold() {
        var uttakPeriode1 = svangerskapspengerUttakPeriode().fom(PERIODE1_FOM)
            .tom(PERIODE1_TOM)
            .periodeResultatType(BrevGrunnlagDto.PeriodeResultatType.AVSLÅTT)
            .periodeIkkeOppfyltÅrsak("8308") // SØKT_FOR_SENT
            .build();
        var uttakPeriode2 = svangerskapspengerUttakPeriode().fom(PERIODE2_FOM)
            .tom(PERIODE2_TOM)
            .periodeResultatType(BrevGrunnlagDto.PeriodeResultatType.AVSLÅTT)
            .periodeIkkeOppfyltÅrsak("8308") // SØKT_FOR_SENT
            .build();
        var arbeidsforhold1 = svangerskapspengerUttakArbeidsforhold().arbeidsgiverReferanse(ARBEIDSGIVER1_REF)
            .arbeidType(BrevGrunnlagDto.UttakArbeidType.ORDINÆRT_ARBEID)
            .perioder(of(uttakPeriode1, uttakPeriode2))
            .build();

        var uttakPeriode3 = svangerskapspengerUttakPeriode().fom(PERIODE3_FOM)
            .tom(PERIODE3_TOM)
            .periodeResultatType(BrevGrunnlagDto.PeriodeResultatType.AVSLÅTT)
            .periodeIkkeOppfyltÅrsak("8311") // PERIODE_SAMTIDIG_SOM_FERIE
            .build();
        var uttakPeriode4 = svangerskapspengerUttakPeriode().fom(PERIODE4_FOM)
            .tom(PERIODE4_TOM)
            .periodeResultatType(BrevGrunnlagDto.PeriodeResultatType.AVSLÅTT)
            .periodeIkkeOppfyltÅrsak("8311") // PERIODE_SAMTIDIG_SOM_FERIE
            .build();
        var uttakPeriode5 = svangerskapspengerUttakPeriode().fom(PERIODE5_FOM)
            .tom(PERIODE5_TOM)
            .periodeResultatType(BrevGrunnlagDto.PeriodeResultatType.AVSLÅTT)
            .periodeIkkeOppfyltÅrsak("4002") // OPPTJENINGSVILKÅRET_IKKE_OPPFYLT - Mappes ikke
            .build();

        var arbeidsforhold2 = svangerskapspengerUttakArbeidsforhold().arbeidsgiverReferanse(ARBEIDSGIVER2_REF)
            .arbeidType(BrevGrunnlagDto.UttakArbeidType.ORDINÆRT_ARBEID)
            .perioder(of(uttakPeriode3, uttakPeriode4, uttakPeriode5))
            .build();

        var uttakPeriode6 = svangerskapspengerUttakPeriode().fom(PERIODE6_FOM)
            .tom(PERIODE6_TOM)
            .periodeResultatType(BrevGrunnlagDto.PeriodeResultatType.AVSLÅTT)
            .periodeIkkeOppfyltÅrsak("8308") // SØKT_FOR_SENT
            .build();
        var uttakPeriode7 = svangerskapspengerUttakPeriode().fom(PERIODE7_FOM)
            .tom(PERIODE7_TOM)
            .periodeResultatType(BrevGrunnlagDto.PeriodeResultatType.AVSLÅTT)
            .periodeIkkeOppfyltÅrsak("8311") // PERIODE_SAMTIDIG_SOM_FERIE
            .build();

        var arbeidsforhold3 = svangerskapspengerUttakArbeidsforhold().arbeidsgiverReferanse(ARBEIDSGIVER3_REF)
            .arbeidType(BrevGrunnlagDto.UttakArbeidType.ORDINÆRT_ARBEID)
            .perioder(of(uttakPeriode6, uttakPeriode7))
            .build();

        return of(arbeidsforhold1, arbeidsforhold2, arbeidsforhold3);
    }
}

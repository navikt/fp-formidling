package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;

import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.svangerskapspengerUttakArbeidsforhold;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.domene.uttak.svp.ArbeidsforholdIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;

class AvslåttAktivitetMapperTest {

    private static final String ARBEIDSGIVER1_NAVN = "Arbeidsgiver1 AS";
    private static final String ARBEIDSGIVER2_NAVN = "Arbeidsgiver2 AS";
    private static final String ARBEIDSGIVER3_NAVN = "Arbeidsgiver3 AS";
    private static final String ARBEIDSGIVER1_REF = "ref1";
    private static final String ARBEIDSGIVER2_REF = "ref2";
    private static final String ARBEIDSGIVER3_REF = "ref3";

    @Test
    void skal_mappe_aktiviteter_med_riktig_årsak() {
        // Arrange
        var svpUttakResultatArbeidsforhold = getSvpUttakResultatArbeidsforhold();
        var arbeidsgiverNavnMap = Map.of(ARBEIDSGIVER1_REF, ARBEIDSGIVER1_NAVN, ARBEIDSGIVER2_REF, ARBEIDSGIVER2_NAVN, ARBEIDSGIVER3_REF,
            ARBEIDSGIVER3_NAVN);

        // Act
        var resultat = AvslåttAktivitetMapper.mapAvslåtteAktiviteter(svpUttakResultatArbeidsforhold, arbeidsgiverNavnMap::get);

        // Assert
        assertThat(resultat).hasSize(4);
        assertThat(resultat.get(0).getÅrsak()).isEqualTo(Årsak.of(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE.getKode()));
        assertThat(resultat.get(0).getArbeidsgiverNavn()).isEqualTo(ARBEIDSGIVER1_NAVN);
        assertThat(resultat.get(0).getErFL()).isFalse();
        assertThat(resultat.get(0).getErSN()).isFalse();
        assertThat(resultat.get(1).getÅrsak()).isEqualTo(
            Årsak.of(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN.getKode()));
        assertThat(resultat.get(1).getArbeidsgiverNavn()).isNull();
        assertThat(resultat.get(1).getErFL()).isFalse();
        assertThat(resultat.get(1).getErSN()).isTrue();
        assertThat(resultat.get(2).getÅrsak()).isEqualTo(Årsak.of(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE.getKode()));
        assertThat(resultat.get(2).getArbeidsgiverNavn()).isNull();
        assertThat(resultat.get(2).getErFL()).isTrue();
        assertThat(resultat.get(2).getErSN()).isFalse();
        assertThat(resultat.get(3).getÅrsak()).isEqualTo(
            Årsak.of(ArbeidsforholdIkkeOppfyltÅrsak.HELE_UTTAKET_ER_ETTER_3_UKER_FØR_TERMINDATO.getKode()));
        assertThat(resultat.get(3).getArbeidsgiverNavn()).isNull();
        assertThat(resultat.get(3).getErFL()).isFalse();
        assertThat(resultat.get(3).getErSN()).isFalse();
    }

    private List<BrevGrunnlagDto.Svangerskapspenger.UttakArbeidsforhold> getSvpUttakResultatArbeidsforhold() {
        var arbeidsforhold1 = svangerskapspengerUttakArbeidsforhold().arbeidsforholdIkkeOppfyltÅrsak(
                ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE.getKode())
            .arbeidType(BrevGrunnlagDto.UttakArbeidType.ORDINÆRT_ARBEID)
            .arbeidsgiverReferanse(ARBEIDSGIVER1_REF)
            .build();

        var arbeidsforhold2 = svangerskapspengerUttakArbeidsforhold().arbeidsforholdIkkeOppfyltÅrsak(
                ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN.getKode())
            .arbeidType(BrevGrunnlagDto.UttakArbeidType.SELVSTENDIG_NÆRINGSDRIVENDE)
            .build();

        var arbeidsforhold3 = svangerskapspengerUttakArbeidsforhold().arbeidsforholdIkkeOppfyltÅrsak(
            ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE.getKode()).arbeidType(BrevGrunnlagDto.UttakArbeidType.FRILANS).build();

        //Årsak som tilsier at arbeidsgiverNavn/SN/FL ikke trengs da arbeidsgiver ikke kan tilrettelegge
        var arbeidsforhold4 = svangerskapspengerUttakArbeidsforhold().arbeidsforholdIkkeOppfyltÅrsak(
                ArbeidsforholdIkkeOppfyltÅrsak.HELE_UTTAKET_ER_ETTER_3_UKER_FØR_TERMINDATO.getKode())
            .arbeidType(BrevGrunnlagDto.UttakArbeidType.ORDINÆRT_ARBEID)
            .arbeidsgiverReferanse(ARBEIDSGIVER2_REF)
            .build();

        //Årsak som ikke skal mappes
        var arbeidsforhold5 = svangerskapspengerUttakArbeidsforhold().arbeidsforholdIkkeOppfyltÅrsak(
                ArbeidsforholdIkkeOppfyltÅrsak.UTTAK_KUN_PÅ_HELG.getKode())
            .arbeidType(BrevGrunnlagDto.UttakArbeidType.ORDINÆRT_ARBEID)
            .arbeidsgiverReferanse(ARBEIDSGIVER3_REF)
            .build();

        return List.of(arbeidsforhold1, arbeidsforhold2, arbeidsforhold3, arbeidsforhold4, arbeidsforhold5);
    }
}

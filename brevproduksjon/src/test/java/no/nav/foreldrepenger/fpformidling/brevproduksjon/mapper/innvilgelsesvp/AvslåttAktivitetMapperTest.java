package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.uttak.UttakArbeidType;
import no.nav.foreldrepenger.fpformidling.uttak.svp.ArbeidsforholdIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvpUttakResultatArbeidsforhold;
import no.nav.foreldrepenger.fpformidling.virksomhet.Arbeidsgiver;

public class AvslåttAktivitetMapperTest {

    private static final String ARBEIDSGIVER1_NAVN = "Arbeidsgiver1 AS";
    private static final String ARBEIDSGIVER2_NAVN = "Arbeidsgiver2 AS";
    private static final String ARBEIDSGIVER3_NAVN = "Arbeidsgiver3 AS";

    @Test
    void skal_mappe_aktiviteter_med_riktig_årsak() {
        // Arrange
        var svpUttakResultatArbeidsforhold = getSvpUttakResultatArbeidsforhold();

        // Act
        var resultat = AvslåttAktivitetMapper.mapAvslåtteAktiviteter(svpUttakResultatArbeidsforhold);

        // Assert
        assertThat(resultat).hasSize(4);
        assertThat(resultat.get(0).getÅrsak()).isEqualTo(Årsak.of(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE.getKode()));
        assertThat(resultat.get(0).getArbeidsgiverNavn()).isEqualTo(ARBEIDSGIVER1_NAVN);
        assertThat(resultat.get(0).getErFL()).isFalse();
        assertThat(resultat.get(0).getErSN()).isFalse();
        assertThat(resultat.get(1).getÅrsak()).isEqualTo(Årsak.of(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN.getKode()));
        assertThat(resultat.get(1).getArbeidsgiverNavn()).isNull();
        assertThat(resultat.get(1).getErFL()).isFalse();
        assertThat(resultat.get(1).getErSN()).isTrue();
        assertThat(resultat.get(2).getÅrsak()).isEqualTo(Årsak.of(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE.getKode()));
        assertThat(resultat.get(2).getArbeidsgiverNavn()).isNull();
        assertThat(resultat.get(2).getErFL()).isTrue();
        assertThat(resultat.get(2).getErSN()).isFalse();
        assertThat(resultat.get(3).getÅrsak()).isEqualTo(Årsak.of(ArbeidsforholdIkkeOppfyltÅrsak.HELE_UTTAKET_ER_ETTER_3_UKER_FØR_TERMINDATO.getKode()));
        assertThat(resultat.get(3).getArbeidsgiverNavn()).isNull();
        assertThat(resultat.get(3).getErFL()).isFalse();
        assertThat(resultat.get(3).getErSN()).isFalse();
    }

    private List<SvpUttakResultatArbeidsforhold> getSvpUttakResultatArbeidsforhold() {
        var arbeidsgiver1 = new Arbeidsgiver("ref1", ARBEIDSGIVER1_NAVN);
        var arbeidsforhold1 = SvpUttakResultatArbeidsforhold.Builder.ny()
                .medArbeidsforholdIkkeOppfyltÅrsak(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE)
                .medUttakArbeidType(UttakArbeidType.ORDINÆRT_ARBEID)
                .medArbeidsgiver(arbeidsgiver1)
                .build();

        var arbeidsforhold2 = SvpUttakResultatArbeidsforhold.Builder.ny()
                .medArbeidsforholdIkkeOppfyltÅrsak(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN)
                .medUttakArbeidType(UttakArbeidType.SELVSTENDIG_NÆRINGSDRIVENDE)
                .build();

        var arbeidsforhold3 = SvpUttakResultatArbeidsforhold.Builder.ny()
                .medArbeidsforholdIkkeOppfyltÅrsak(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE)
                .medUttakArbeidType(UttakArbeidType.FRILANS)
                .build();

        //Årsak som tilsier at arbeidsgiverNavn/SN/FL ikke trengs da arbeidsgiver ikke kan tilrettelegge
        var arbeidsgiver2 = new Arbeidsgiver("ref2", ARBEIDSGIVER2_NAVN);
        var arbeidsforhold4 = SvpUttakResultatArbeidsforhold.Builder.ny()
                .medArbeidsforholdIkkeOppfyltÅrsak(ArbeidsforholdIkkeOppfyltÅrsak.HELE_UTTAKET_ER_ETTER_3_UKER_FØR_TERMINDATO)
                .medUttakArbeidType(UttakArbeidType.ORDINÆRT_ARBEID)
                .medArbeidsgiver(arbeidsgiver2)
                .build();

        //Årsak som ikke skal mappes
        var arbeidsgiver3 = new Arbeidsgiver("ref3", ARBEIDSGIVER3_NAVN);
        var arbeidsforhold5 = SvpUttakResultatArbeidsforhold.Builder.ny()
                .medArbeidsforholdIkkeOppfyltÅrsak(ArbeidsforholdIkkeOppfyltÅrsak.UTTAK_KUN_PÅ_HELG)
                .medUttakArbeidType(UttakArbeidType.ORDINÆRT_ARBEID)
                .medArbeidsgiver(arbeidsgiver3)
                .build();

        return of(arbeidsforhold1, arbeidsforhold2, arbeidsforhold3, arbeidsforhold4, arbeidsforhold5);
    }
}

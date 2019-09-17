package no.nav.foreldrepenger.melding.datamapper.domene.svp;

import static org.assertj.core.api.Java6Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import no.nav.foreldrepenger.melding.uttak.UttakArbeidType;
import no.nav.foreldrepenger.melding.uttak.svp.ArbeidsforholdIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatArbeidsforhold;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;
import no.nav.foreldrepenger.melding.virksomhet.Virksomhet;

public class SvpUtledAvslagArbeidsforholdTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private static final String VIRKSOMHET_NAVN = "VIRKSOMHET";
    private static final String ARBEIDSGIVER_NAVN = "ARBEIDSGIVER";
    private static final String ORGNR = "123";

    private static final Virksomhet VIRKSOMHET = new Virksomhet(VIRKSOMHET_NAVN, ORGNR);
    private static final Arbeidsgiver ARBEIDSGIVER = new Arbeidsgiver(ARBEIDSGIVER_NAVN, VIRKSOMHET, null);

    @Test
    public void skal_filtrere_bort_URAer_med_urelevant_årsak() {

        // Arrange
        SvpUttakResultatArbeidsforhold ura = SvpUttakResultatArbeidsforhold.Builder.ny()
                .medArbeidsforholdIkkeOppfyltÅrsak(ArbeidsforholdIkkeOppfyltÅrsak.INGEN)
                .medArbeidsgiver(ARBEIDSGIVER)
                .build();

        List<SvpUttakResultatArbeidsforhold> uraList = List.of(ura);

        // Act
        Set<SvpAvslagArbeidsforhold> avslagArbeidsforhold = SvpUtledAvslagArbeidsforhold.utled(uraList);

        // Assert
        assertThat(avslagArbeidsforhold).isEmpty();

    }

    @Test
    public void skal_utlede_avslag_periode_for_årsak_ARBEIDSGIVER_KAN_TILRETTELEGGE_som_AT() {

        // Arrange
        SvpUttakResultatArbeidsforhold ura = SvpUttakResultatArbeidsforhold.Builder.ny()
                .medArbeidsforholdIkkeOppfyltÅrsak(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE)
                .medArbeidsgiver(ARBEIDSGIVER)
                .build();

        List<SvpUttakResultatArbeidsforhold> uraList = List.of(ura);

        // Act
        Set<SvpAvslagArbeidsforhold> avslagArbeidsforhold = SvpUtledAvslagArbeidsforhold.utled(uraList);

        // Assert
        assertThat(avslagArbeidsforhold).hasSize(1);
        assert_AT(avslagArbeidsforhold);

    }

    @Test
    public void skal_utlede_avslag_periode_for_årsak_ARBEIDSGIVER_KAN_TILRETTELEGGE_som_FL() {

        // Arrange
        SvpUttakResultatArbeidsforhold ura = SvpUttakResultatArbeidsforhold.Builder.ny()
                .medArbeidsforholdIkkeOppfyltÅrsak(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE)
                .medUttakArbeidType(UttakArbeidType.FRILANS)
                .build();

        List<SvpUttakResultatArbeidsforhold> uraList = List.of(ura);

        // Act
        Set<SvpAvslagArbeidsforhold> avslagArbeidsforhold = SvpUtledAvslagArbeidsforhold.utled(uraList);

        // Assert
        assertThat(avslagArbeidsforhold).hasSize(1);
        assert_FL(avslagArbeidsforhold);

    }

    @Test
    public void skal_utlede_avslag_periode_for_årsak_ARBEIDSGIVER_KAN_TILRETTELEGGE_som_SN() {

        // Arrange
        SvpUttakResultatArbeidsforhold ura = SvpUttakResultatArbeidsforhold.Builder.ny()
                .medArbeidsforholdIkkeOppfyltÅrsak(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE)
                .medUttakArbeidType(UttakArbeidType.SELVSTENDIG_NÆRINGSDRIVENDE)
                .build();

        List<SvpUttakResultatArbeidsforhold> uraList = List.of(ura);

        // Act
        Set<SvpAvslagArbeidsforhold> avslagArbeidsforhold = SvpUtledAvslagArbeidsforhold.utled(uraList);

        // Assert
        assertThat(avslagArbeidsforhold).hasSize(1);
        assert_SN(avslagArbeidsforhold);

    }

    @Test
    public void skal_utlede_avslag_periode_for_årsak_HELE_UTTAKET_ER_ETTER_3_UKER_FØR_TERMINDATO() {

        // Arrange
        SvpUttakResultatArbeidsforhold ura = SvpUttakResultatArbeidsforhold.Builder.ny()
                .medArbeidsforholdIkkeOppfyltÅrsak(ArbeidsforholdIkkeOppfyltÅrsak.HELE_UTTAKET_ER_ETTER_3_UKER_FØR_TERMINDATO)
                .build();

        List<SvpUttakResultatArbeidsforhold> uraList = List.of(ura);

        // Act
        Set<SvpAvslagArbeidsforhold> avslagArbeidsforhold = SvpUtledAvslagArbeidsforhold.utled(uraList);

        // Assert
        assertThat(avslagArbeidsforhold).hasSize(1);
        assertThat(avslagArbeidsforhold).anySatisfy(aa -> {
            assertThat(aa.getArbeidsgiverNavn()).isNull();
            assertThat(aa.getErFL()).isNull();
            assertThat(aa.getErSN()).isNull();
            assertThat(aa.getAarsakskode()).isEqualTo(
                    Integer.valueOf(ArbeidsforholdIkkeOppfyltÅrsak.HELE_UTTAKET_ER_ETTER_3_UKER_FØR_TERMINDATO.getKode()));
        });

    }

    @Test
    public void skal_utlede_avslag_periode_for_årsak_ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN() {

        // Arrange
        SvpUttakResultatArbeidsforhold ura = SvpUttakResultatArbeidsforhold.Builder.ny()
                .medArbeidsforholdIkkeOppfyltÅrsak(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN)
                .medArbeidsgiver(ARBEIDSGIVER)
                .build();

        List<SvpUttakResultatArbeidsforhold> uraList = List.of(ura);

        // Act
        Set<SvpAvslagArbeidsforhold> avslagArbeidsforhold = SvpUtledAvslagArbeidsforhold.utled(uraList);

        // Assert
        assertThat(avslagArbeidsforhold).hasSize(1);
        assertThat(avslagArbeidsforhold).anySatisfy(aa -> {
            assertThat(aa.getArbeidsgiverNavn()).isEqualTo(ARBEIDSGIVER_NAVN);
            assertThat(aa.getErFL()).isNull();
            assertThat(aa.getErSN()).isNull();
            assertThat(aa.getAarsakskode()).isEqualTo(
                    Integer.valueOf(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN.getKode()));
        });

    }

    @Test
    public void skal_kaste_exception_når_årsak_ARBEIDSGIVER_KAN_TILRETTELEGGE_uten_AT_FL_eller_SN() {

        // Arrange
        SvpUttakResultatArbeidsforhold ura = SvpUttakResultatArbeidsforhold.Builder.ny()
                .medArbeidsforholdIkkeOppfyltÅrsak(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE)
                .build();

        List<SvpUttakResultatArbeidsforhold> uraList = List.of(ura);

        // Expect
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(
                "Kan ikke opprette avslått arbeidsforhold for ukjent arbeidsgiver og/eller UttakArbeidType");

        // Act
        SvpUtledAvslagArbeidsforhold.utled(uraList);

    }

    @Test
    public void skal_lage_tre_avslag_arbeidsforhold_for_AT_FL_og_SN() {

        // Arrange
        SvpUttakResultatArbeidsforhold ura_1 = SvpUttakResultatArbeidsforhold.Builder.ny()
                .medArbeidsforholdIkkeOppfyltÅrsak(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE)
                .medArbeidsgiver(ARBEIDSGIVER)
                .build();

        SvpUttakResultatArbeidsforhold ura_2 = SvpUttakResultatArbeidsforhold.Builder.ny()
                .medArbeidsforholdIkkeOppfyltÅrsak(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE)
                .medUttakArbeidType(UttakArbeidType.SELVSTENDIG_NÆRINGSDRIVENDE)
                .build();

        SvpUttakResultatArbeidsforhold ura_3 = SvpUttakResultatArbeidsforhold.Builder.ny()
                .medArbeidsforholdIkkeOppfyltÅrsak(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE)
                .medUttakArbeidType(UttakArbeidType.FRILANS)
                .build();

        List<SvpUttakResultatArbeidsforhold> uraList = List.of(ura_1, ura_2, ura_3);

        // Act
        Set<SvpAvslagArbeidsforhold> avslagArbeidsforhold = SvpUtledAvslagArbeidsforhold.utled(uraList);

        // Assert
        assertThat(avslagArbeidsforhold).hasSize(3);
        assert_AT(avslagArbeidsforhold);
        assert_SN(avslagArbeidsforhold);
        assert_FL(avslagArbeidsforhold);

    }

    private void assert_FL(Set<SvpAvslagArbeidsforhold> avslagArbeidsforhold) {
        assertThat(avslagArbeidsforhold).anySatisfy(aa -> {
            assertThat(aa.getArbeidsgiverNavn()).isNull();
            assertThat(aa.getErFL()).isTrue();
            assertThat(aa.getErSN()).isNull();
            assertThat(aa.getAarsakskode()).isEqualTo(
                    Integer.valueOf(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE.getKode()));
        });
    }

    private void assert_SN(Set<SvpAvslagArbeidsforhold> avslagArbeidsforhold) {
        assertThat(avslagArbeidsforhold).anySatisfy(aa -> {
            assertThat(aa.getArbeidsgiverNavn()).isNull();
            assertThat(aa.getErFL()).isNull();
            assertThat(aa.getErSN()).isTrue();
            assertThat(aa.getAarsakskode()).isEqualTo(
                    Integer.valueOf(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE.getKode()));
        });
    }

    private void assert_AT(Set<SvpAvslagArbeidsforhold> avslagArbeidsforhold) {
        assertThat(avslagArbeidsforhold).anySatisfy(aa -> {
            assertThat(aa.getArbeidsgiverNavn()).isEqualTo(ARBEIDSGIVER_NAVN);
            assertThat(aa.getErFL()).isNull();
            assertThat(aa.getErSN()).isNull();
            assertThat(aa.getAarsakskode()).isEqualTo(
                    Integer.valueOf(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE.getKode()));
        });
    }

}

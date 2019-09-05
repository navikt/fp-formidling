package no.nav.foreldrepenger.melding.datamapper.domene.svp;

import static org.assertj.core.api.Java6Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.helper.ConditionalHelpers;
import com.github.jknack.handlebars.helper.I18nHelper;

import no.nav.foreldrepenger.melding.datamapper.mal.fritekst.BrevmalKilder;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.uttak.svp.ArbeidsforholdIkkeOppfyltÅrsak;

public class SvpHandlebarAvslagArbeidsforholdTest {

    private static final String ARBEIDSGIVER_NAVN = "NAV Pluto";
    private static final String SVP_BUNDLE = "dokumentmal/innvilgelsesvangerskapspenger/innvilgelsesvangerskapspenger";
    private static final String HANDLEBAR_FILE_LOCATION = "/dokumentmal/innvilgelsesvangerskapspenger/avslagArbeidsforhold";

    private Handlebars handlebars;

    @Before
    public void oppsett() {
        handlebars = new Handlebars();
        handlebars.setCharset(Charset.forName("latin1"));
        handlebars.registerHelpers(ConditionalHelpers.class);
    }

    @Test
    public void skal_compile_for_AT_med_årsak_ARBEIDSGIVER_KAN_TILRETTELEGGE_og_språkkode_NB() throws IOException {

        // Arrange
        SvpAvslagArbeidsforhold arb = SvpAvslagArbeidsforhold.Builder.ny()
                .medAarsakskode(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE)
                .medArbeidsgiverNavn(ARBEIDSGIVER_NAVN)
                .medErFL(true) // Kun for å teste if, else if, else logikk
                .medErSN(true) // Kun for å teste if, else if, else logikk
                .build();

        Map<String, Object> map = new HashMap<>();
        Set<SvpAvslagArbeidsforhold> avslagArbeidsforhold = Set.of(arb);
        map.put("avslagArbeidsforhold", avslagArbeidsforhold);

        registrerTestSpesifikkeHandlebars(avslagArbeidsforhold, Språkkode.nb);

        // Act
        String templateString = handlebars.compile(HANDLEBAR_FILE_LOCATION).apply(map);

        // Assert
        assertThat(templateString).contains(String.format(
                "Du har ikke rett til svangerskapspenger hos %s fordi du kan gjøre arbeidsoppgavene uten at det " +
                "er fare for å skade barnet eller barna du venter.",
                ARBEIDSGIVER_NAVN));

    }

    @Test
    public void skal_compile_for_AT_med_årsak_ARBEIDSGIVER_KAN_TILRETTELEGGE_og_språkkode_NN() throws IOException {

        // Arrange
        SvpAvslagArbeidsforhold arb = SvpAvslagArbeidsforhold.Builder.ny()
                .medAarsakskode(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE)
                .medArbeidsgiverNavn(ARBEIDSGIVER_NAVN)
                .medErFL(true) // Kun for å teste if, else if, else logikk
                .medErSN(true) // Kun for å teste if, else if, else logikk
                .build();

        Map<String, Object> map = new HashMap<>();
        Set<SvpAvslagArbeidsforhold> avslagArbeidsforhold = Set.of(arb);
        map.put("avslagArbeidsforhold", avslagArbeidsforhold);

        registrerTestSpesifikkeHandlebars(avslagArbeidsforhold, Språkkode.nn);

        // Act
        String templateString = handlebars.compile(HANDLEBAR_FILE_LOCATION).apply(map);

        // Assert
        assertThat(templateString).contains(String.format(
                "Du har ikkje rett til svangerskapspengar hos %s fordi du kan gjere arbeidsoppgåvene utan at det " +
                "er fare for å skade barnet eller barna du ventar",
                ARBEIDSGIVER_NAVN));

    }

    @Test
    public void skal_compile_for_FL_med_årsak_ARBEIDSGIVER_KAN_TILRETTELEGGE_og_språkkode_NB() throws IOException {

        // Arrange
        SvpAvslagArbeidsforhold arb = SvpAvslagArbeidsforhold.Builder.ny()
                .medAarsakskode(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE)
                .medErFL(true)
                .medErSN(true) // Kun for å teste if, else if, else logikk
                .build();

        Map<String, Object> map = new HashMap<>();
        Set<SvpAvslagArbeidsforhold> avslagArbeidsforhold = Set.of(arb);
        map.put("avslagArbeidsforhold", avslagArbeidsforhold);

        registrerTestSpesifikkeHandlebars(avslagArbeidsforhold, Språkkode.nb);

        // Act
        String templateString = handlebars.compile(HANDLEBAR_FILE_LOCATION).apply(map);

        // Assert
        assertThat(templateString).contains(
                "Du har ikke rett til svangerskapspenger som frilanser fordi du kan gjøre arbeidsoppgavene uten at det " +
                "er fare for å skade barnet eller barna du venter.");

    }

    @Test
    public void skal_compile_for_FL_med_årsak_ARBEIDSGIVER_KAN_TILRETTELEGGE_og_språkkode_NN() throws IOException {

        // Arrange
        SvpAvslagArbeidsforhold arb = SvpAvslagArbeidsforhold.Builder.ny()
                .medAarsakskode(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE)
                .medErFL(true)
                .medErSN(true) // Kun for å teste if, else if, else logikk
                .build();

        Map<String, Object> map = new HashMap<>();
        Set<SvpAvslagArbeidsforhold> avslagArbeidsforhold = Set.of(arb);
        map.put("avslagArbeidsforhold", avslagArbeidsforhold);

        registrerTestSpesifikkeHandlebars(avslagArbeidsforhold, Språkkode.nn);

        // Act
        String templateString = handlebars.compile(HANDLEBAR_FILE_LOCATION).apply(map);

        // Assert
        assertThat(templateString).contains(
                "Du har ikkje rett til svangerskapspengar som frilansar fordi du kan gjere arbeidsoppgåvene utan at det " +
                "er fare for å skade barnet eller barna du ventar.");

    }

    @Test
    public void skal_compile_for_SN_med_årsak_ARBEIDSGIVER_KAN_TILRETTELEGGE_og_språkkode_NB() throws IOException {

        // Arrange
        SvpAvslagArbeidsforhold arb = SvpAvslagArbeidsforhold.Builder.ny()
                .medAarsakskode(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE)
                .medErSN(true)
                .build();

        Map<String, Object> map = new HashMap<>();
        Set<SvpAvslagArbeidsforhold> avslagArbeidsforhold = Set.of(arb);
        map.put("avslagArbeidsforhold", avslagArbeidsforhold);

        registrerTestSpesifikkeHandlebars(avslagArbeidsforhold, Språkkode.nb);

        // Act
        String templateString = handlebars.compile(HANDLEBAR_FILE_LOCATION).apply(map);

        // Assert
        assertThat(templateString).contains(
                "Du har ikke rett til svangerskapspenger som selvstendig næringsdrivende fordi du kan gjøre " +
                "arbeidsoppgavene uten at det er fare for å skade barnet eller barna du venter.");

    }

    @Test
    public void skal_compile_for_SN_med_årsak_ARBEIDSGIVER_KAN_TILRETTELEGGE_og_språkkode_NN() throws IOException {

        // Arrange
        SvpAvslagArbeidsforhold arb = SvpAvslagArbeidsforhold.Builder.ny()
                .medAarsakskode(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE)
                .medErSN(true)
                .build();

        Map<String, Object> map = new HashMap<>();
        Set<SvpAvslagArbeidsforhold> avslagArbeidsforhold = Set.of(arb);
        map.put("avslagArbeidsforhold", avslagArbeidsforhold);

        registrerTestSpesifikkeHandlebars(avslagArbeidsforhold, Språkkode.nn);

        // Act
        String templateString = handlebars.compile(HANDLEBAR_FILE_LOCATION).apply(map);

        // Assert
        assertThat(templateString).contains(
                "Du har ikkje rett til svangerskapspengar som sjølvstendig næringsdrivande fordi du kan gjere " +
                "arbeidsoppgåvene utan at det er fare for å skade barnet eller barna du ventar.");

    }

    @Test
    public void skal_compile_for_årsak_HELE_UTTAKET_ER_ETTER_3_UKER_FØR_TERMINDATO_og_språkkode_NB() throws IOException {

        // Arrange
        SvpAvslagArbeidsforhold arb = SvpAvslagArbeidsforhold.Builder.ny()
                .medAarsakskode(ArbeidsforholdIkkeOppfyltÅrsak.HELE_UTTAKET_ER_ETTER_3_UKER_FØR_TERMINDATO)
                .build();

        Map<String, Object> map = new HashMap<>();
        Set<SvpAvslagArbeidsforhold> avslagArbeidsforhold = Set.of(arb);
        map.put("avslagArbeidsforhold", avslagArbeidsforhold);

        registrerTestSpesifikkeHandlebars(avslagArbeidsforhold, Språkkode.nb);

        // Act
        String templateString = handlebars.compile(HANDLEBAR_FILE_LOCATION).apply(map);

        // Assert
        assertThat(templateString).contains(
                "Du har ikke rett til svangerskapspenger fordi det er mindre enn tre uker til du har termin.");
        assertThat(templateString).contains(
                "Du kan ha rett til foreldrepenger eller engangsstønad. Se nav.no/foreldrepenger for mer informasjon og hvordan du kan søke.");

    }

    @Test
    public void skal_compile_for_årsak_HELE_UTTAKET_ER_ETTER_3_UKER_FØR_TERMINDATO_og_språkkode_NN() throws IOException {

        // Arrange
        SvpAvslagArbeidsforhold arb = SvpAvslagArbeidsforhold.Builder.ny()
                .medAarsakskode(ArbeidsforholdIkkeOppfyltÅrsak.HELE_UTTAKET_ER_ETTER_3_UKER_FØR_TERMINDATO)
                .build();

        Map<String, Object> map = new HashMap<>();
        Set<SvpAvslagArbeidsforhold> avslagArbeidsforhold = Set.of(arb);
        map.put("avslagArbeidsforhold", avslagArbeidsforhold);

        registrerTestSpesifikkeHandlebars(avslagArbeidsforhold, Språkkode.nn);

        // Act
        String templateString = handlebars.compile(HANDLEBAR_FILE_LOCATION).apply(map);

        // Assert
        assertThat(templateString).contains(
                "Du har ikkje rett til svangerskapspengar fordi det er mindre enn tre veker til du har termin.");
        assertThat(templateString).contains(
                "Du kan ha rett til foreldrepengar eller eingongsstønad. Sjå nav.no/foreldrepenger for meir informasjon og korleis du kan søkje.");

    }


    @Test
    public void skal_compile_for_AT_med_årsak_ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN_og_språkkode_NB() throws IOException {

        // Arrange
        SvpAvslagArbeidsforhold arb = SvpAvslagArbeidsforhold.Builder.ny()
                .medAarsakskode(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN)
                .medArbeidsgiverNavn(ARBEIDSGIVER_NAVN)
                .medErFL(true) // Kun for å teste if, else if, else logikk
                .medErSN(true) // Kun for å teste if, else if, else logikk
                .build();

        Map<String, Object> map = new HashMap<>();
        Set<SvpAvslagArbeidsforhold> avslagArbeidsforhold = Set.of(arb);
        map.put("avslagArbeidsforhold", avslagArbeidsforhold);

        registrerTestSpesifikkeHandlebars(avslagArbeidsforhold, Språkkode.nb);

        // Act
        String templateString = handlebars.compile(HANDLEBAR_FILE_LOCATION).apply(map);

        // Assert
        assertThat(templateString).contains(String.format(
                "Du har ikke rett til svangerskapspenger hos %s fordi du kan gjøre arbeidsoppgavene uten at det " +
                "er fare for å skade barnet eller barna du venter frem til tre uker før termin.",
                ARBEIDSGIVER_NAVN));

    }

    @Test
    public void skal_compile_for_AT_med_årsak_ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN_og_språkkode_NN() throws IOException {

        // Arrange
        SvpAvslagArbeidsforhold arb = SvpAvslagArbeidsforhold.Builder.ny()
                .medAarsakskode(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN)
                .medArbeidsgiverNavn(ARBEIDSGIVER_NAVN)
                .medErFL(true) // Kun for å teste if, else if, else logikk
                .medErSN(true) // Kun for å teste if, else if, else logikk
                .build();

        Map<String, Object> map = new HashMap<>();
        Set<SvpAvslagArbeidsforhold> avslagArbeidsforhold = Set.of(arb);
        map.put("avslagArbeidsforhold", avslagArbeidsforhold);

        registrerTestSpesifikkeHandlebars(avslagArbeidsforhold, Språkkode.nn);

        // Act
        String templateString = handlebars.compile(HANDLEBAR_FILE_LOCATION).apply(map);

        // Assert
        assertThat(templateString).contains(String.format(
                "Du har ikkje rett til svangerskapspengar hos %s fordi du kan gjere arbeidsoppgåvene utan at det " +
                "er fare for å skade barnet eller barna du ventar fram til tre veker før termin.",
                ARBEIDSGIVER_NAVN));

    }

    @Test
    public void skal_compile_for_FL_med_årsak_ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN_og_språkkode_NB() throws IOException {

        // Arrange
        SvpAvslagArbeidsforhold arb = SvpAvslagArbeidsforhold.Builder.ny()
                .medAarsakskode(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN)
                .medErFL(true)
                .medErSN(true) // Kun for å teste if, else if, else logikk
                .build();

        Map<String, Object> map = new HashMap<>();
        Set<SvpAvslagArbeidsforhold> avslagArbeidsforhold = Set.of(arb);
        map.put("avslagArbeidsforhold", avslagArbeidsforhold);

        registrerTestSpesifikkeHandlebars(avslagArbeidsforhold, Språkkode.nb);

        // Act
        String templateString = handlebars.compile(HANDLEBAR_FILE_LOCATION).apply(map);

        // Assert
        assertThat(templateString).contains(
                "Du har ikke rett til svangerskapspenger som frilanser fordi du kan gjøre arbeidsoppgavene uten at det " +
                "er fare for å skade barnet eller barna du venter frem til tre uker før termin.");

    }

    @Test
    public void skal_compile_for_FL_med_årsak_ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN_og_språkkode_NN() throws IOException {

        // Arrange
        SvpAvslagArbeidsforhold arb = SvpAvslagArbeidsforhold.Builder.ny()
                .medAarsakskode(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN)
                .medErFL(true)
                .medErSN(true) // Kun for å teste if, else if, else logikk
                .build();

        Map<String, Object> map = new HashMap<>();
        Set<SvpAvslagArbeidsforhold> avslagArbeidsforhold = Set.of(arb);
        map.put("avslagArbeidsforhold", avslagArbeidsforhold);

        registrerTestSpesifikkeHandlebars(avslagArbeidsforhold, Språkkode.nn);

        // Act
        String templateString = handlebars.compile(HANDLEBAR_FILE_LOCATION).apply(map);

        // Assert
        assertThat(templateString).contains(
                "Du har ikkje rett til svangerskapspengar som frilansar fordi du kan gjere arbeidsoppgåvene utan at det " +
                "er fare for å skade barnet eller barna du ventar fram til tre veker før termin.");

    }

    @Test
    public void skal_compile_for_SN_med_årsak_ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN_og_språkkode_NB() throws IOException {

        // Arrange
        SvpAvslagArbeidsforhold arb = SvpAvslagArbeidsforhold.Builder.ny()
                .medAarsakskode(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN)
                .medErSN(true)
                .build();

        Map<String, Object> map = new HashMap<>();
        Set<SvpAvslagArbeidsforhold> avslagArbeidsforhold = Set.of(arb);
        map.put("avslagArbeidsforhold", avslagArbeidsforhold);

        registrerTestSpesifikkeHandlebars(avslagArbeidsforhold, Språkkode.nb);

        // Act
        String templateString = handlebars.compile(HANDLEBAR_FILE_LOCATION).apply(map);

        // Assert
        assertThat(templateString).contains(
                "Du har ikke rett til svangerskapspenger som selvstendig næringsdrivende fordi du kan gjøre arbeidsoppgavene uten at det " +
                        "er fare for å skade barnet eller barna du venter frem til tre uker før termin.");

    }

    @Test
    public void skal_compile_for_SN_med_årsak_ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN_og_språkkode_NN() throws IOException {

        // Arrange
        SvpAvslagArbeidsforhold arb = SvpAvslagArbeidsforhold.Builder.ny()
                .medAarsakskode(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN)
                .medErSN(true)
                .build();

        Map<String, Object> map = new HashMap<>();
        Set<SvpAvslagArbeidsforhold> avslagArbeidsforhold = Set.of(arb);
        map.put("avslagArbeidsforhold", avslagArbeidsforhold);

        registrerTestSpesifikkeHandlebars(avslagArbeidsforhold, Språkkode.nn);

        // Act
        String templateString = handlebars.compile(HANDLEBAR_FILE_LOCATION).apply(map);

        // Assert
        assertThat(templateString).contains(
                "Du har ikkje rett til svangerskapspengar som sjølvstendig næringsdrivande fordi du kan gjere arbeidsoppgåvene utan at det " +
                        "er fare for å skade barnet eller barna du ventar fram til tre veker før termin.");

    }

    @Test
    public void skal_compile_for_flere_ikke_oppfylte_arbeidsforhold_med_ulike_årsaker() throws IOException {

        // Arrange
        SvpAvslagArbeidsforhold arb1 = SvpAvslagArbeidsforhold.Builder.ny()
                .medAarsakskode(ArbeidsforholdIkkeOppfyltÅrsak.HELE_UTTAKET_ER_ETTER_3_UKER_FØR_TERMINDATO)
                .build();

        SvpAvslagArbeidsforhold arb2 = SvpAvslagArbeidsforhold.Builder.ny()
                .medAarsakskode(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE)
                .medArbeidsgiverNavn(ARBEIDSGIVER_NAVN)
                .build();

        SvpAvslagArbeidsforhold arb3 = SvpAvslagArbeidsforhold.Builder.ny()
                .medAarsakskode(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE)
                .medErFL(true)
                .build();

        SvpAvslagArbeidsforhold arb4 = SvpAvslagArbeidsforhold.Builder.ny()
                .medAarsakskode(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE)
                .medErSN(true)
                .build();

        SvpAvslagArbeidsforhold arb5 = SvpAvslagArbeidsforhold.Builder.ny()
                .medAarsakskode(ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN)
                .medArbeidsgiverNavn(ARBEIDSGIVER_NAVN)
                .build();

        Map<String, Object> map = new HashMap<>();
        Set<SvpAvslagArbeidsforhold> avslagArbeidsforhold = Set.of(arb1, arb2, arb3, arb4, arb5);
        map.put("avslagArbeidsforhold", avslagArbeidsforhold);

        registrerTestSpesifikkeHandlebars(avslagArbeidsforhold, Språkkode.nb);

        // Act
        String templateString = handlebars.compile(HANDLEBAR_FILE_LOCATION).apply(map);

        // Assert
        assertThat(templateString).contains(
                "Du har ikke rett til svangerskapspenger fordi det er mindre enn tre uker til du har termin.");
        assertThat(templateString).contains(
                "Du kan ha rett til foreldrepenger eller engangsstønad. Se nav.no/foreldrepenger for mer informasjon og hvordan du kan søke.");

        assertThat(templateString).contains(String.format(
                "Du har ikke rett til svangerskapspenger hos %s fordi du kan gjøre arbeidsoppgavene uten at det " +
                        "er fare for å skade barnet eller barna du venter.",
                ARBEIDSGIVER_NAVN));

        assertThat(templateString).contains(
                "Du har ikke rett til svangerskapspenger som frilanser fordi du kan gjøre arbeidsoppgavene uten at det " +
                        "er fare for å skade barnet eller barna du venter.");

        assertThat(templateString).contains(
                "Du har ikke rett til svangerskapspenger som selvstendig næringsdrivende fordi du kan gjøre " +
                        "arbeidsoppgavene uten at det er fare for å skade barnet eller barna du venter.");

        assertThat(templateString).contains(String.format(
                "Du har ikke rett til svangerskapspenger hos %s fordi du kan gjøre arbeidsoppgavene uten at det " +
                        "er fare for å skade barnet eller barna du venter frem til tre uker før termin.",
                ARBEIDSGIVER_NAVN));

    }

    private void registrerTestSpesifikkeHandlebars(Set<SvpAvslagArbeidsforhold> avslagArbeidsforhold, Språkkode språkkode) {
        handlebars.registerHelper("size", (o, options) -> avslagArbeidsforhold.size());
        handlebars.registerHelper(I18nHelper.i18n.name(), (resource, options) -> {
            options.hash.put("bundle", SVP_BUNDLE);
            options.hash.put("locale", BrevmalKilder.getLocaleSuffixFor(språkkode));
            return I18nHelper.i18n.apply((String) resource, options);
        });
    }

}

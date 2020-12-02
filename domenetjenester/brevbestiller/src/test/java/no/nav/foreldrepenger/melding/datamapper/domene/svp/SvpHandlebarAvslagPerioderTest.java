package no.nav.foreldrepenger.melding.datamapper.domene.svp;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.helper.ConditionalHelpers;
import com.github.jknack.handlebars.helper.I18nHelper;

import no.nav.foreldrepenger.melding.datamapper.mal.fritekst.BrevmalKilder;
import no.nav.foreldrepenger.melding.dbstoette.JpaExtension;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.typer.Dato;
import no.nav.foreldrepenger.melding.uttak.svp.PeriodeIkkeOppfyltÅrsak;

@ExtendWith(JpaExtension.class)
public class SvpHandlebarAvslagPerioderTest {

    private static final LocalDate AUGUST_1 = LocalDate.of(2019, 8, 1);
    private static final LocalDate OKTOBER_1 = LocalDate.of(2019, 10, 1);
    private static final String SVP_BUNDLE = "dokumentmal/innvilgelsesvangerskapspenger/innvilgelsesvangerskapspenger";
    private static final String HANDLEBAR_FILE_LOCATION = "/dokumentmal/innvilgelsesvangerskapspenger/avslagPerioder";

    private Handlebars handlebars;

    @BeforeEach
    public void oppsett() {
        handlebars = new Handlebars();
        handlebars.setCharset(Charset.forName("latin1"));
        handlebars.registerHelpers(ConditionalHelpers.class);
    }

    @Test
    public void skal_compile_for_en_periode_på_språkkode_NB() throws IOException {

        // Arrange
        Dato periodeFom = new Dato(AUGUST_1.plusWeeks(1));
        Dato periodeTom = new Dato(AUGUST_1.plusWeeks(2));
        SvpAvslagPeriode periode = SvpAvslagPeriode.Builder.ny()
                .medFom(periodeFom)
                .medTom(periodeTom)
                .medPeriodeÅrsak(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT)
                .build();
        Set<SvpAvslagPeriode> perioder = Set.of(periode);

        registrerTestSpesifikkeHandlebars(perioder, Språkkode.nb);

        String mottattDato = new Dato(OKTOBER_1).toString();
        Map<String, Object> map = new HashMap<>();
        map.put("avslagPerioder", perioder);
        map.put("mottattDato", mottattDato);

        // Act
        String templateString = handlebars.compile(HANDLEBAR_FILE_LOCATION).apply(map);

        // assert
        assertThat(templateString).contains("_I denne perioden får du ikke svangerskapspenger");
        assertThat(templateString).contains(
                String.format("Fra og med %s til og med %s", periodeFom.toString(), periodeTom.toString()));
        assertThat(templateString).contains("har du ikke rett til svangerskapspenger fordi du har søkt for sent.");
        assertThat(templateString).contains("Du må søke senest tre måneder etter at perioden med svangerskapspenger har startet.");
        assertThat(templateString).contains(
                String.format("Du søkte %s.", mottattDato));

    }

    @Test
    public void skal_compile_for_to_perioder_på_språkkode_NB() throws IOException {

        // Arrange
        Dato periodeFom_1 = new Dato(AUGUST_1.plusWeeks(1));
        Dato periodeTom_1 = new Dato(AUGUST_1.plusWeeks(2));
        SvpAvslagPeriode periode_1 = SvpAvslagPeriode.Builder.ny()
                .medFom(periodeFom_1)
                .medTom(periodeTom_1)
                .medPeriodeÅrsak(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT)
                .build();

        Dato periodeFom_2 = new Dato(AUGUST_1.plusWeeks(1).minusDays(1));
        Dato periodeTom_2 = new Dato(AUGUST_1.plusWeeks(2).plusDays(1));
        SvpAvslagPeriode periode_2 = SvpAvslagPeriode.Builder.ny()
                .medFom(periodeFom_2)
                .medTom(periodeTom_2)
                .medPeriodeÅrsak(PeriodeIkkeOppfyltÅrsak.PERIODE_SAMTIDIG_SOM_FERIE)
                .build();

        Set<SvpAvslagPeriode> perioder = Set.of(periode_1, periode_2);

        registrerTestSpesifikkeHandlebars(perioder, Språkkode.nb);

        String mottattDato = new Dato(OKTOBER_1).toString();
        Map<String, Object> map = new HashMap<>();
        map.put("avslagPerioder", perioder);
        map.put("mottattDato", mottattDato);

        // Act
        String templateString = handlebars.compile(HANDLEBAR_FILE_LOCATION).apply(map);

        // assert
        assertThat(templateString).contains("_I disse periodene får du ikke svangerskapspenger");
        assertThat(templateString).contains(String.format(
                "Fra og med %s til og med %s har du ikke rett til svangerskapspenger fordi du har søkt for sent.",
                periodeFom_1.toString(),
                periodeTom_1.toString()));
        assertThat(templateString).contains(String.format(
                "Fra og med %s til og med %s får du ikke svangerskapspenger fordi du har ferie.",
                periodeFom_2.toString(),
                periodeTom_2.toString()));
        assertThat(templateString).contains(String.format(
                "Du må søke senest tre måneder etter at perioden med svangerskapspenger har startet. Du søkte %s.",
                mottattDato));

    }

    @Test
    public void skal_compile_for_en_periode_på_språkkode_NN() throws IOException {

        // Arrange
        Dato periodeFom = new Dato(AUGUST_1.plusWeeks(1));
        Dato periodeTom = new Dato(AUGUST_1.plusWeeks(2));
        SvpAvslagPeriode periode = SvpAvslagPeriode.Builder.ny()
                .medFom(periodeFom)
                .medTom(periodeTom)
                .medPeriodeÅrsak(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT)
                .build();

        Set<SvpAvslagPeriode> perioder = Set.of(periode);

        registrerTestSpesifikkeHandlebars(perioder, Språkkode.nn);

        String mottattDato = new Dato(OKTOBER_1).toString();
        Map<String, Object> map = new HashMap<>();
        map.put("avslagPerioder", perioder);
        map.put("mottattDato", mottattDato);

        // Act
        String templateString = handlebars.compile(HANDLEBAR_FILE_LOCATION).apply(map);

        // assert
        assertThat(templateString).contains("_I denne perioden får du svangerskapspengar");
        assertThat(templateString).contains(String.format(
                "Frå og med %s til og med %s", periodeFom.toString(), periodeTom.toString()));
        assertThat(templateString).contains("har du ikkje rett til svangerskapspengar fordi du har søkt for seint.");
        assertThat(templateString).contains("Du må søkje seinast tre månader etter at perioden med svangerskapspengar har starta.");
        assertThat(templateString).contains(String.format(
                "Du søkte %s.", mottattDato));

    }

    @Test
    public void skal_compile_for_to_perioder_på_språkkode_NN() throws IOException {

        // Arrange
        Dato periodeFom_1 = new Dato(AUGUST_1.plusWeeks(1));
        Dato periodeTom_1 = new Dato(AUGUST_1.plusWeeks(2));
        SvpAvslagPeriode periode_1 = SvpAvslagPeriode.Builder.ny()
                .medFom(periodeFom_1)
                .medTom(periodeTom_1)
                .medPeriodeÅrsak(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT)
                .build();

        Dato periodeFom_2 = new Dato(AUGUST_1.plusWeeks(1).minusDays(1));
        Dato periodeTom_2 = new Dato(AUGUST_1.plusWeeks(2).plusDays(1));
        SvpAvslagPeriode periode_2 = SvpAvslagPeriode.Builder.ny()
                .medFom(periodeFom_2)
                .medTom(periodeTom_2)
                .medPeriodeÅrsak(PeriodeIkkeOppfyltÅrsak.PERIODE_SAMTIDIG_SOM_FERIE)
                .build();

        Set<SvpAvslagPeriode> perioder = Set.of(periode_1, periode_2);

        registrerTestSpesifikkeHandlebars(perioder, Språkkode.nn);

        String mottattDato = new Dato(OKTOBER_1).toString();
        Map<String, Object> map = new HashMap<>();
        map.put("avslagPerioder", perioder);
        map.put("mottattDato", mottattDato);

        // Act
        String templateString = handlebars.compile(HANDLEBAR_FILE_LOCATION).apply(map);

        // assert
        assertThat(templateString).contains("_I desse periodane får du svangerskapspengar");
        assertThat(templateString).contains(String.format(
                "Frå og med %s til og med %s har du ikkje rett til svangerskapspengar fordi du har søkt for seint.",
                periodeFom_1.toString(),
                periodeTom_1.toString()));
        assertThat(templateString).contains(String.format(
                "Frå og med %s til og med %s får du ikkje svangerskapspengar fordi du har ferie.",
                periodeFom_2.toString(),
                periodeTom_2.toString()));
        assertThat(templateString).contains(String.format(
                "Du må søkje seinast tre månader etter at perioden med svangerskapspengar har starta. Du søkte %s.",
                mottattDato));

    }

    private void registrerTestSpesifikkeHandlebars(Set<SvpAvslagPeriode> perioder, Språkkode språkkode) {
        handlebars.registerHelper("size", (o, options) -> perioder.size());
        handlebars.registerHelper(I18nHelper.i18n.name(), (resource, options) -> {
            options.hash.put("bundle", SVP_BUNDLE);
            options.hash.put("locale", BrevmalKilder.getLocaleSuffixFor(språkkode));
            return I18nHelper.i18n.apply((String) resource, options);
        });
    }

}

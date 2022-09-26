package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LovhjemmelComparatorTest {

    List<String> lovhjemler = new ArrayList();

    @BeforeEach
    public void setup() {
        lovhjemler.add("1-1");
        lovhjemler.add("1-3");
        lovhjemler.add("1-2");
        lovhjemler.add("14-6");
        lovhjemler.add("14-16");
        lovhjemler.add("14-9");
        lovhjemler.add("14-10 b"); // En bokstav - skal inkluderes
        lovhjemler.add("13-0");
        lovhjemler.add("13-0"); // Duplikat - skal fjernes
        lovhjemler.add("13-1");
        lovhjemler.add("14-10");
        lovhjemler.add("13-9");
        lovhjemler.add("99-56");
        lovhjemler.add("420-33");
        lovhjemler.add("3-1");
        lovhjemler.add("2-1");
        lovhjemler.add("14-10 ab"); // To bokstaver / ugyldig - skal legges først
        lovhjemler.add("33-2");
        lovhjemler.add("14-test"); // Ugyldig - skal legges først
        lovhjemler.add("14");
        lovhjemler.add("14-10 a"); // En bokstav - skal inkluderes
        lovhjemler.add("14-10 a"); // Duplikat - skal fjernes
    }

    @Test
    public void skal_sortere_stigende_etter_kapittel_og_paragraf_som_kan_inkludere_en_bokstav_tilslutt_og_legge_ugyldige_først_og_fjerne_duplikater() {
        var set = new TreeSet<>(new LovhjemmelComparator());
        set.addAll(lovhjemler);
        var iterator = set.iterator();
        assertThat(iterator.next()).isEqualTo("14-test");
        assertThat(iterator.next()).isEqualTo("14-10 ab");
        assertThat(iterator.next()).isEqualTo("1-1");
        assertThat(iterator.next()).isEqualTo("1-2");
        assertThat(iterator.next()).isEqualTo("1-3");
        assertThat(iterator.next()).isEqualTo("2-1");
        assertThat(iterator.next()).isEqualTo("3-1");
        assertThat(iterator.next()).isEqualTo("13-0");
        assertThat(iterator.next()).isEqualTo("13-1");
        assertThat(iterator.next()).isEqualTo("13-9");
        assertThat(iterator.next()).isEqualTo("14");
        assertThat(iterator.next()).isEqualTo("14-6");
        assertThat(iterator.next()).isEqualTo("14-9");
        assertThat(iterator.next()).isEqualTo("14-10");
        assertThat(iterator.next()).isEqualTo("14-10 a");
        assertThat(iterator.next()).isEqualTo("14-10 b");
        assertThat(iterator.next()).isEqualTo("14-16");
        assertThat(iterator.next()).isEqualTo("33-2");
        assertThat(iterator.next()).isEqualTo("99-56");
        assertThat(iterator.next()).isEqualTo("420-33");
    }
}

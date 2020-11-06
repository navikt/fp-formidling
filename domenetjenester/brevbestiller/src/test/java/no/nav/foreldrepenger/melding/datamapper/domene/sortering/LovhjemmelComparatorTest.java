package no.nav.foreldrepenger.melding.datamapper.domene.sortering;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LovhjemmelComparatorTest {

    Set<String> lovhjemler = new HashSet<>();

    @BeforeEach
    public void setup() {
        lovhjemler.add("1-1");
        lovhjemler.add("1-3");
        lovhjemler.add("1-2");
        lovhjemler.add("14-6");
        lovhjemler.add("14-16");
        lovhjemler.add("14-9");
        lovhjemler.add("13-0");
        lovhjemler.add("13-0");
        lovhjemler.add("13-1");
        lovhjemler.add("13-9");
        lovhjemler.add("99-56");
        lovhjemler.add("420-33");
        lovhjemler.add("3-1");
        lovhjemler.add("2-1");
        lovhjemler.add("33-2");
        lovhjemler.add("14-test");
        lovhjemler.add("14");
    }

    @Test
    public void skal_sortere_stigende_etter_to_tall() {
        TreeSet<String> set = new TreeSet<>(new LovhjemmelComparator());
        set.addAll(lovhjemler);
        Iterator<String> iterator = set.iterator();
        assertThat(iterator.next()).isEqualTo("14-test");
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
        assertThat(iterator.next()).isEqualTo("14-16");
        assertThat(iterator.next()).isEqualTo("33-2");
        assertThat(iterator.next()).isEqualTo("99-56");
        assertThat(iterator.next()).isEqualTo("420-33");
    }


}

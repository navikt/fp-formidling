package no.nav.foreldrepenger.fpformidling.web.app.validering;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;

public class KodeverkValidatorTest {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testSkalFeilePåTomInput() {
        var kl = new TestKodeverdi("", "");
        Set<ConstraintViolation<TestKodeverdi>> violations = validator.validate(kl);
        assertThat(violations.size()).isEqualTo(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("kodeverk kode feilet validering");
    }

    @Test
    public void testSkalFeilePåUgyldigeTegnIKodeverk() {
        var kl = new TestKodeverdi("#¤#2aS", "RE-MF");
        Set<ConstraintViolation<TestKodeverdi>> violations = validator.validate(kl);
        assertThat(violations.size()).isEqualTo(1);
    }

    @Test
    public void testSkalFeilePåUgyldigKodeLengde() {
        var kl = new TestKodeverdi("",
                "asdfghjklqwertyuiasdfgdsfasjjjfhsjhkjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj" +
                        "jjjjjjjjjjjjjasdfghjklqwertyuiasdfgdsfasjjjfhsjhkjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj"
        );
        Set<ConstraintViolation<TestKodeverdi>> violations = validator.validate(kl);
        assertThat(violations.size()).isEqualTo(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("kodeverk kode feilet validering");
    }

    static class KodeverdiValidatorTest implements Kodeverdi {

        private String kodeverk;
        private String kode;

        public KodeverdiValidatorTest(String kodeverk, String kode) {
            this.kodeverk = kodeverk;
            this.kode = kode;
        }

        @Override
        public String getKodeverk() {
            return kodeverk;
        }

        @Override
        public String getKode() {
            return kode;
        }
    }

    static class TestKodeverdi {
        @ValidKodeverk
        private KodeverdiValidatorTest k;

        public TestKodeverdi(String kodeverk, String kode) {
            k = new KodeverdiValidatorTest(kodeverk, kode);
        }

        public KodeverdiValidatorTest getK() {
            return k;
        }

        public void setK(KodeverdiValidatorTest k) {
            this.k = k;
        }
    }
}

package no.nav.foreldrepenger.melding.web.app.validering;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.BeforeClass;
import org.junit.Test;

import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.Kodeverdi;

public class KodeverkValidatorTest {

    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testSkalFeilePåTomInput() {
        var kl = new TestKodeverdi("", "");
        Set<ConstraintViolation<TestKodeverdi>> violations = validator.validate(kl);
        assertEquals(1, violations.size());
        assertEquals("kodeverk kode feilet validering", violations.iterator().next().getMessage());
    }

    @Test
    public void testSkalFeilePåUgyldigeTegnIKodeverk() {
        var kl = new TestKodeverdi("#¤#2aS", "RE-MF");
        Set<ConstraintViolation<TestKodeverdi>> violations = validator.validate(kl);
        assertEquals(1, violations.size());
    }

    @Test
    public void testSkalFeilePåUgyldigKodeLengde() {
        var kl = new TestKodeverdi("",
                "asdfghjklqwertyuiasdfgdsfasjjjfhsjhkjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj" +
                        "jjjjjjjjjjjjjasdfghjklqwertyuiasdfgdsfasjjjfhsjhkjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj"
        );
        Set<ConstraintViolation<TestKodeverdi>> violations = validator.validate(kl);
        assertEquals(1, violations.size());
        assertEquals("kodeverk kode feilet validering", violations.iterator().next().getMessage());
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

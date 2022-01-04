package no.nav.foreldrepenger.fpformidling.web.app.validering;

import java.util.Objects;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;
import no.nav.vedtak.util.InputValideringRegex;

public class KodeverdiValidator implements ConstraintValidator<ValidKodeverk, Kodeverdi> {

    static final String invKode = "kodeverks kode feilet validering";
    static final String invNavn = "kodeverks navn feilet validering";

    Pattern kodeverkPattern = Pattern.compile(InputValideringRegex.KODEVERK);
    Pattern navnPattern = Pattern.compile(InputValideringRegex.NAVN);

    @Override
    public void initialize(ValidKodeverk validKodeverk) {
        // ikke noe å gjøre
    }

    boolean erTomEllerNull(String str) {
        return (Objects.equals(null, str) || str.isEmpty());
    }

    boolean gyldigKode(String kode) {
        return (!erTomEllerNull(kode) && gyldigLengde(kode, 1, 100) && kodeverkPattern.matcher(kode).matches());
    }

    boolean gyldigKodeverk(String kodeverk) {
        return (!erTomEllerNull(kodeverk) && gyldigLengde(kodeverk, 0, 256) && kodeverkPattern.matcher(kodeverk).matches());
    }

    boolean gyldigNavn(String str) {
        return (!erTomEllerNull(str) && gyldigLengde(str, 0, 256) && navnPattern.matcher(str).matches());
    }

    boolean gyldigLengde(String str, int min, int max) {
        return (str.length() >= min && str.length() <= max);
    }

    @Override
    public boolean isValid(Kodeverdi kodeliste, ConstraintValidatorContext context) {
        if (Objects.equals(null, kodeliste)) {
            return true;
        }
        boolean ok = true;

        if (!gyldigKode(kodeliste.getKode())) {
            context.buildConstraintViolationWithTemplate(invKode);
            ok = false;
        }

        if (!gyldigKodeverk(kodeliste.getKodeverk())) {
            context.buildConstraintViolationWithTemplate(invNavn);
            ok = false;
        }

        return ok;
    }
}

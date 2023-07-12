package no.nav.foreldrepenger.fpformidling.web.app.validering;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;
import no.nav.vedtak.util.InputValideringRegex;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;
import java.util.regex.Pattern;

public class KodeverdiValidator implements ConstraintValidator<ValidKodeverk, Kodeverdi> {

    private static final String INV_KODE = "kodeverks kode feilet validering";

    private static final Pattern KODEVERK_PATTERN = Pattern.compile(InputValideringRegex.KODEVERK);

    @Override
    public void initialize(ValidKodeverk validKodeverk) {
        // ikke noe å gjøre
    }

    boolean erTomEllerNull(String str) {
        return (Objects.equals(null, str) || str.isEmpty());
    }

    boolean gyldigKode(String kode) {
        return (!erTomEllerNull(kode) && gyldigLengde(kode, 1, 100) && KODEVERK_PATTERN.matcher(kode).matches());
    }

    @Override
    public boolean isValid(Kodeverdi kodeliste, ConstraintValidatorContext context) {
        if (Objects.equals(null, kodeliste)) {
            return true;
        }
        var ok = true;

        if (!gyldigKode(kodeliste.getKode())) {
            context.buildConstraintViolationWithTemplate(INV_KODE);
            ok = false;
        }

        return ok;
    }

    private static boolean gyldigLengde(String str, int min, int max) {
        return (str.length() >= min && str.length() <= max);
    }
}

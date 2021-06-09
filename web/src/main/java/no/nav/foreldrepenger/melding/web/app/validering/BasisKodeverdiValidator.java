package no.nav.foreldrepenger.melding.web.app.validering;

import java.util.Objects;

import javax.validation.ConstraintValidatorContext;

import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BasisKodeverdi;

public class BasisKodeverdiValidator extends KodeverkValidator<BasisKodeverdi> {

    @Override
    public boolean isValid(BasisKodeverdi kodeliste, ConstraintValidatorContext context) {
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

package no.nav.foreldrepenger.melding.web.app.jackson;

import javax.validation.constraints.Pattern;

class Patternklasse {

    @Pattern(regexp = "[Aa]")
    private String fritekst;
}

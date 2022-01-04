package no.nav.foreldrepenger.fpformidling.web.app.jackson;

import javax.validation.constraints.Pattern;

class Patternklasse {

    @Pattern(regexp = "[Aa]")
    private String fritekst;
}

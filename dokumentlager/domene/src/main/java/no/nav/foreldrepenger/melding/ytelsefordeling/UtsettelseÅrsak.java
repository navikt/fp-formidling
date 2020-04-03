package no.nav.foreldrepenger.melding.ytelsefordeling;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "UtsettelseÅrsak")
@DiscriminatorValue(UtsettelseÅrsak.DISCRIMINATOR)
public class UtsettelseÅrsak extends Årsak {
    public static final String DISCRIMINATOR = "UTSETTELSE_AARSAK_TYPE";

    public static final UtsettelseÅrsak ARBEID = new UtsettelseÅrsak("ARBEID");
    public static final UtsettelseÅrsak FERIE = new UtsettelseÅrsak("LOVBESTEMT_FERIE");
    public static final UtsettelseÅrsak SYKDOM = new UtsettelseÅrsak("SYKDOM");
    public static final UtsettelseÅrsak INSTITUSJON_SØKER = new UtsettelseÅrsak("INSTITUSJONSOPPHOLD_SØKER");
    public static final UtsettelseÅrsak INSTITUSJON_BARN = new UtsettelseÅrsak("INSTITUSJONSOPPHOLD_BARNET");
    public static final UtsettelseÅrsak HV_OVELSE = new UtsettelseÅrsak("HV_OVELSE");
    public static final UtsettelseÅrsak NAV_TILTAK = new UtsettelseÅrsak("NAV_TILTAK");
    public static final UtsettelseÅrsak UDEFINERT = new UtsettelseÅrsak("-");

    UtsettelseÅrsak(String kode) {
        super(kode, DISCRIMINATOR);
    }

    UtsettelseÅrsak() {
    }
}

package no.nav.foreldrepenger.melding.personopplysning;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "RelasjonsRolleType")
@DiscriminatorValue(RelasjonsRolleType.DISCRIMINATOR)
public class RelasjonsRolleType extends Kodeliste {
    public static final String DISCRIMINATOR = "RELASJONSROLLE_TYPE";

    public static final RelasjonsRolleType EKTE = new RelasjonsRolleType("EKTE");  //$NON-NLS-1$
    public static final RelasjonsRolleType BARN = new RelasjonsRolleType("BARN");  //$NON-NLS-1$
    public static final RelasjonsRolleType FARA = new RelasjonsRolleType("FARA");  //$NON-NLS-1$
    public static final RelasjonsRolleType MORA = new RelasjonsRolleType("MORA");  //$NON-NLS-1$
    public static final RelasjonsRolleType REGISTRERT_PARTNER = new RelasjonsRolleType("REPA");  //$NON-NLS-1$
    public static final RelasjonsRolleType SAMBOER = new RelasjonsRolleType("SAMB");  //$NON-NLS-1$
    public static final RelasjonsRolleType MEDMOR = new RelasjonsRolleType("MMOR");  //$NON-NLS-1$

    public static final RelasjonsRolleType UDEFINERT = new RelasjonsRolleType("-");  //$NON-NLS-1$
    private static final Set<RelasjonsRolleType> FORELDRE_ROLLER = Stream.of(RelasjonsRolleType.MORA, RelasjonsRolleType.FARA, RelasjonsRolleType.MEDMOR).collect(Collectors.toCollection(LinkedHashSet::new));

    public RelasjonsRolleType() {
    }

    public RelasjonsRolleType(String kode) {
        super(kode, DISCRIMINATOR);
    }

    public static boolean erFar(RelasjonsRolleType relasjon) {
        return FARA.getKode().equals(relasjon.getKode());
    }

    public static boolean erMedmor(RelasjonsRolleType relasjon) {
        return MEDMOR.getKode().equals(relasjon.getKode());
    }

    public static boolean erFarEllerMedmor(RelasjonsRolleType relasjon) {
        return erFar(relasjon) || erMedmor(relasjon);
    }

    public static boolean erMor(RelasjonsRolleType relasjon) {
        return MORA.getKode().equals(relasjon.getKode());
    }

    public static boolean erRegistrertForeldre(RelasjonsRolleType type) {
        return FORELDRE_ROLLER.contains(type);
    }
}

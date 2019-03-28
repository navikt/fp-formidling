package no.nav.foreldrepenger.melding.inntektarbeidytelse;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "RelatertYtelseType")
@DiscriminatorValue(RelatertYtelseType.DISCRIMINATOR)
public class RelatertYtelseType extends Kodeliste {

    public static final String DISCRIMINATOR = "RELATERT_YTELSE_TYPE";
    public static final RelatertYtelseType ENSLIG_FORSØRGER = new RelatertYtelseType("ENSLIG_FORSØRGER");
    public static final RelatertYtelseType SYKEPENGER = new RelatertYtelseType("SYKEPENGER");
    public static final RelatertYtelseType SVANGERSKAPSPENGER = new RelatertYtelseType("SVANGERSKAPSPENGER");
    public static final RelatertYtelseType FORELDREPENGER = new RelatertYtelseType("FORELDREPENGER");
    public static final RelatertYtelseType ENGANGSSTØNAD = new RelatertYtelseType("ENGANGSSTØNAD");
    public static final RelatertYtelseType PÅRØRENDESYKDOM = new RelatertYtelseType("PÅRØRENDESYKDOM");

    public static final RelatertYtelseType ARBEIDSAVKLARINGSPENGER = new RelatertYtelseType("ARBEIDSAVKLARINGSPENGER");
    public static final RelatertYtelseType DAGPENGER = new RelatertYtelseType("DAGPENGER");

    public static final RelatertYtelseType UDEFINERT = new RelatertYtelseType("-");

    public RelatertYtelseType() {
        // Hibernate trenger den
    }

    private RelatertYtelseType(String kode) {
        super(kode, DISCRIMINATOR);
    }
}

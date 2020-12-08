package no.nav.foreldrepenger.melding.typer;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Pattern.Flag;

import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.melding.kodeverk.diff.IndexKey;

/**
 * Id som genereres fra NAV Aktør Register. Denne iden benyttes til interne forhold i Nav og vil ikke endres f.eks. dersom bruker går fra
 * DNR til FNR i Folkeregisteret. Tilsvarende vil den kunne referere personer som har ident fra et utenlandsk system.
 */
@Embeddable
public class AktørId implements Serializable, Comparable<AktørId>, IndexKey {
    private static final String CHARS = "a-z0-9_:-";

    private static final String VALID_REGEXP = "^(-?[1-9]|[a-z0])[" + CHARS + "]*$";
    private static final String INVALID_REGEXP = "[^" + CHARS + "]+";

    private static final Pattern VALID = Pattern.compile(VALID_REGEXP, Pattern.CASE_INSENSITIVE);
    private static final Pattern INVALID = Pattern.compile(INVALID_REGEXP, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    @JsonValue
    @javax.validation.constraints.Pattern(regexp = VALID_REGEXP, flags = {Flag.CASE_INSENSITIVE})
    @Column(name = "aktoer_id", updatable = false, length = 50)
    private String aktørId;  // NOSONAR

    protected AktørId() {
        // for hibernate
    }

    public AktørId(Long aktørId) {
        Objects.requireNonNull(aktørId, "aktørId");
        this.aktørId = aktørId.toString();
    }

    public AktørId(String aktørId) {
        Objects.requireNonNull(aktørId, "aktørId");
        if (!VALID.matcher(aktørId).matches()) {
            // skal ikke skje, funksjonelle feilmeldinger håndteres ikke her.
            throw new IllegalArgumentException("Ugyldig aktørId, støtter kun A-Z/0-9/:/-/_ tegn. Var: " + aktørId.replaceAll(INVALID.pattern(), "?") + " (vasket)");
        }
        this.aktørId = aktørId;
    }

    @Override
    public String getIndexKey() { //NOSONAR
        return aktørId;
    }

    public String getId() { //NOSONAR
        return aktørId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || !getClass().equals(obj.getClass())) {
            return false;
        }
        AktørId other = (AktørId) obj;
        return Objects.equals(aktørId, other.aktørId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aktørId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "<" + aktørId + ">";
    }

    @Override
    public int compareTo(AktørId o) {
        return aktørId.compareTo(o.aktørId);
    }
}

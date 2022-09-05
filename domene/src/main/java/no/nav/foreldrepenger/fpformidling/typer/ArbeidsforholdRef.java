package no.nav.foreldrepenger.fpformidling.typer;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Arbeidsforhold id fra AA-registeret. (ikke intern id)
 */

@Embeddable
public class ArbeidsforholdRef implements Serializable {

    @Column(name = "arbeidsforhold_id")
    private String referanse;

    ArbeidsforholdRef() {
    }

    private ArbeidsforholdRef(String referanse) {
        this.referanse = referanse;
    }

    public static ArbeidsforholdRef ref(String referanse) {
        return new ArbeidsforholdRef(referanse);
    }

    public String getReferanse() {
        return referanse;
    }

    public boolean gjelderForSpesifiktArbeidsforhold() {
        return referanse != null && !referanse.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null && this.referanse == null) {
            return true;
        }
        if (o == null || getClass() != o.getClass())
            return false;
        ArbeidsforholdRef that = (ArbeidsforholdRef) o;
        return Objects.equals(referanse, that.referanse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referanse);
    }

    @Override
    public String toString() {
        return referanse;
    }
}

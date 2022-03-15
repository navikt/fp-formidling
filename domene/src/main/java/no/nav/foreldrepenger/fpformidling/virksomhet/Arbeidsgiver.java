package no.nav.foreldrepenger.fpformidling.virksomhet;

import java.util.Objects;

public record Arbeidsgiver(String arbeidsgiverReferanse, String navn) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arbeidsgiver that = (Arbeidsgiver) o;
        return Objects.equals(arbeidsgiverReferanse, that.arbeidsgiverReferanse) && Objects.equals(navn, that.navn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arbeidsgiverReferanse, navn);
    }

    @Override
    public String toString() {
        return "Arbeidsgiver{" +
                "arbeidsgiverReferanse='" + arbeidsgiverReferanse + '\'' +
                '}';
    }
}

package no.nav.foreldrepenger.fpformidling.domene.virksomhet;

import java.util.Objects;

public record Arbeidsgiver(String arbeidsgiverReferanse, String navn) {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        var that = (Arbeidsgiver) o;
        return Objects.equals(arbeidsgiverReferanse, that.arbeidsgiverReferanse) && Objects.equals(navn, that.navn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arbeidsgiverReferanse, navn);
    }

    @Override
    public String toString() {
        return "Arbeidsgiver{" + "arbeidsgiverReferanse='" + tilMaskertNummer(arbeidsgiverReferanse) + '\'' + '}';
    }

    public static String tilMaskertNummer(String orgNummer) {
        if (orgNummer == null) {
            return null;
        }
        var length = orgNummer.length();
        if (length <= 4) {
            return "*".repeat(length);
        }
        return "*".repeat(length - 4) + orgNummer.substring(length - 4);
    }

}

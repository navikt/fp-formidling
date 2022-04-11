package no.nav.foreldrepenger.fpformidling.integrasjon.journal.dto;

public record Bruker(String id, BrukerIdType idType) {
    @Override
    public String toString() {
        return "Bruker{id=" + id.substring(0,6) + ", idType=" + idType + '}';
    }
}

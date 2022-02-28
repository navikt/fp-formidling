package no.nav.foreldrepenger.fpformidling.integrasjon.journal.dto;

public record AvsenderMottaker(String id, String navn, AvsenderMottakerIdType idType) {
    @Override
    public String toString() {
        return "AvsenderMottaker{idType=" + idType + '}';
    }
}

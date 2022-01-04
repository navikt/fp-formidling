package no.nav.foreldrepenger.fpformidling.uttak;

public record Stønadskonto(Integer maxDager, StønadskontoType stønadskontoType, int saldo, int prematurDager, int flerbarnsDager) {
}

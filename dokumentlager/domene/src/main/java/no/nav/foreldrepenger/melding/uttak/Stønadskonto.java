package no.nav.foreldrepenger.melding.uttak;

public record Stønadskonto(Integer maxDager, StønadskontoType stønadskontoType, int saldo, int prematurDager, int flerbarnsDager) {
}

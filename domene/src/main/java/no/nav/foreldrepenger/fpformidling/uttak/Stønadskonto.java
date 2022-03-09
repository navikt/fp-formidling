package no.nav.foreldrepenger.fpformidling.uttak;

public record Stønadskonto(Integer maxDager, SaldoVisningStønadskontoType stønadskontoType, int saldo, int prematurDager, int flerbarnsDager) {
}

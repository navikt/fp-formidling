package no.nav.foreldrepenger.fpformidling.uttak.fp;

public record Stønadskonto(Integer maxDager, SaldoVisningStønadskontoType stønadskontoType, int saldo, int prematurDager, int flerbarnsDager) {
}

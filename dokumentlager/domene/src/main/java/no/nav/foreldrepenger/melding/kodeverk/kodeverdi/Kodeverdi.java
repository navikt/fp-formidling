package no.nav.foreldrepenger.melding.kodeverk.kodeverdi;

/** Kodeverk som er portet til java. */
public interface Kodeverdi extends BasisKodeverdi {
    @Override
    String getKode();

    @Override
    String getKodeverk();
}

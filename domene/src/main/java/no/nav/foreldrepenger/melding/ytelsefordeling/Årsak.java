package no.nav.foreldrepenger.melding.ytelsefordeling;


import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.Kodeverdi;

public interface Årsak extends Kodeverdi {

    Årsak UKJENT = new Årsak() {

        @Override
        public String getKodeverk() {
            return "AARSAK_TYPE";
        }

        @Override
        public String getKode() {
            return "-";
        }

    };
}

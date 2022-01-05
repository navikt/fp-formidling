package no.nav.foreldrepenger.fpformidling.ytelsefordeling;


import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;

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

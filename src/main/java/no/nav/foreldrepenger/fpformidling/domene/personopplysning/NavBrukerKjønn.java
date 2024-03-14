package no.nav.foreldrepenger.fpformidling.domene.personopplysning;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;

public enum NavBrukerKjønn implements Kodeverdi {

    KVINNE("K"),
    MANN("M"),
    UDEFINERT("-"),
    ;

    private String kode;

    NavBrukerKjønn() {
    }

    NavBrukerKjønn(String kode) {
        this.kode = kode;
    }

    @Override
    public String getKode() {
        return kode;
    }

}

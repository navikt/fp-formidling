package no.nav.foreldrepenger.fpformidling.domene.kodeverk.kodeverdi;

public enum Fagsystem implements Kodeverdi, KodeverdiMedOffisiellKode {

    FPSAK("FPSAK", "FS36"),
    GOSYS("GOSYS", "FS22"),

    /**
     * Alle kodeverk må ha en verdi, det kan ikke være null i databasen. Denne koden gjør samme nytten.
     */
    UDEFINERT("-", null),
    ;

    private String offisiellKode;

    private String kode;

    Fagsystem() {
        // Hibernate trenger den
    }

    private Fagsystem(String kode, String offisiellKode) {
        this.kode = kode;
        this.offisiellKode = offisiellKode;
    }

    @Override
    public String getOffisiellKode() {
        return offisiellKode;
    }

    @Override
    public String getKode() {
        return kode;
    }

}

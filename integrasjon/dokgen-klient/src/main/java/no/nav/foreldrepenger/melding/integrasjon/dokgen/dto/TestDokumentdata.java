package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto;

public class TestDokumentdata extends Dokumentdata {
    private boolean revurdering;
    private boolean førstegangsbehandling;
    private boolean medhold;
    private double innvilgetBeløp;
    private int klagefristUker;
    private boolean død;
    private String søkersNavn;
    private boolean fbEllerMedhold;
    private String kontaktTelefonnummer;
    private double endretSats;

    public TestDokumentdata(FellesDokumentdata felles, boolean revurdering, boolean førstegangsbehandling, boolean medhold, double innvilgetBeløp, int klagefristUker,
                            boolean død, String søkersNavn, boolean fbEllerMedhold, String kontaktTelefonnummer, double endretSats) {
        this.felles = felles;
        this.revurdering = revurdering;
        this.førstegangsbehandling =førstegangsbehandling;
        this.medhold = medhold;
        this.innvilgetBeløp = innvilgetBeløp;
        this.klagefristUker = klagefristUker;
        this.død = død;
        this.søkersNavn = søkersNavn;
        this.fbEllerMedhold = fbEllerMedhold;
        this.kontaktTelefonnummer = kontaktTelefonnummer;
        this.endretSats = endretSats;
    }

    public boolean getRevurdering() { return revurdering; }

    public boolean getFørstegangsbehandling() { return førstegangsbehandling;  }

    public boolean getMedhold() { return medhold; }

    public double getInnvilgetBeløp() {
        return innvilgetBeløp;
    }

    public int getKlagefristUker() { return klagefristUker; }

    public boolean getDød() { return død; }

    public String getSøkersNavn() { return søkersNavn; }

    public boolean getFbEllerMedhold() { return fbEllerMedhold; }

    public String getKontaktTelefonnummer() { return kontaktTelefonnummer; }

    public double getEndretSats() { return endretSats; }
}

package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto;

public class EngangsstønadInnvilgelseDokumentdata extends Dokumentdata {
    private boolean revurdering;
    private boolean førstegangsbehandling;
    private boolean medhold;
    private double innvilgetBeløp;
    private int klagefristUker;
    private boolean død;
    private boolean fbEllerMedhold;
    private String kontaktTelefonnummer;
    private double endretSats;

    private EngangsstønadInnvilgelseDokumentdata(Builder builder) {
        this.felles = builder.felles;
        this.revurdering = builder.revurdering;
        this.førstegangsbehandling = builder.førstegangsbehandling;
        this.medhold = builder.medhold;
        this.innvilgetBeløp = builder.innvilgetBeløp;
        this.klagefristUker = builder.klagefristUker;
        this.død = builder.død;
        this.fbEllerMedhold = builder.fbEllerMedhold;
        this.kontaktTelefonnummer = builder.kontaktTelefonnummer;
        this.endretSats = builder.endretSats;
    }

    public boolean getRevurdering() { return revurdering; }

    public boolean getFørstegangsbehandling() { return førstegangsbehandling;  }

    public boolean getMedhold() { return medhold; }

    public double getInnvilgetBeløp() {
        return innvilgetBeløp;
    }

    public int getKlagefristUker() { return klagefristUker; }

    public boolean getDød() { return død; }

    public boolean getFbEllerMedhold() { return fbEllerMedhold; }

    public String getKontaktTelefonnummer() { return kontaktTelefonnummer; }

    public double getEndretSats() { return endretSats; }

    public static class Builder {
        public FellesDokumentdata felles;
        private boolean revurdering;
        private boolean førstegangsbehandling;
        private boolean medhold;
        private double innvilgetBeløp;
        private int klagefristUker;
        private boolean død;
        private boolean fbEllerMedhold;
        private String kontaktTelefonnummer;
        private double endretSats;

        public Builder felles(FellesDokumentdata felles) {
            this.felles = felles;
            return this;
        }

        public Builder revurdering(boolean revurdering) {
            this.revurdering = revurdering;
            return this;
        }

        public Builder førstegangsbehandling(boolean førstegangsbehandling) {
            this.førstegangsbehandling = førstegangsbehandling;
            return this;
        }

        public Builder medhold(boolean medhold) {
            this.medhold = medhold;
            return this;
        }

        public Builder innvilgetBeløp(double innvilgetBeløp) {
            this.innvilgetBeløp = innvilgetBeløp;
            return this;
        }

        public Builder klagefristUker(int klagefristUker) {
            this.klagefristUker = klagefristUker;
            return this;
        }

        public Builder død(boolean død) {
            this.død = død;
            return this;
        }

        public Builder fbEllerMedhold(boolean fbEllerMedhold) {
            this.fbEllerMedhold = fbEllerMedhold;
            return this;
        }

        public Builder kontaktTelefonnummer(String kontaktTelefonnummer) {
            this.kontaktTelefonnummer = kontaktTelefonnummer;
            return this;
        }

        public Builder endretSats(double endretSats) {
            this.endretSats = endretSats;
            return this;
        }

        public EngangsstønadInnvilgelseDokumentdata build() {
            return new EngangsstønadInnvilgelseDokumentdata(this);
        }
    }
}

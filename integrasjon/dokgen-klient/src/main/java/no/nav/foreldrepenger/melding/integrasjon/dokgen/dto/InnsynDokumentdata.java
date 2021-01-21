package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto;

public class InnsynDokumentdata extends Dokumentdata{
    private String innsynResultat;
    private int klagefrist;

    public static Builder ny() { return new Builder(); }

    public String getInnsynResultat() { return innsynResultat; }

    public int getKlagefrist() { return klagefrist; }

    public static class Builder {
        private InnsynDokumentdata kladd;

        private Builder() {
            this.kladd = new InnsynDokumentdata();
        }

        public Builder medFellesDokumentData(FellesDokumentdata fellesDokumentdata) {
            this.kladd.felles = fellesDokumentdata;
            return this;
        }

        public Builder medInnsynResultat(String innsynResultat) {
            this.kladd.innsynResultat = innsynResultat;
            return this;
        }

        public Builder medKlagefrist(int klagefrist) {
            this.kladd.klagefrist = klagefrist;
            return this;
        }

        public InnsynDokumentdata build() {
            return this.kladd;
        }
    }
}

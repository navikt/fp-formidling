package no.nav.foreldrepenger.fpformidling.domene.dokumentdata;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.typer.PersonIdent;
import no.nav.foreldrepenger.fpformidling.domene.typer.Saksnummer;

public class DokumentFelles {

    public enum PersonStatus {
        DOD,
        ANNET;
    }

    public enum Kopi {
        JA,
        NEI;
    }

    public enum MottakerType {
        PERSON,
        ORGANISASJON;
    }
    private Språkkode språkkode;
    private Saksnummer saksnummer;
    private Boolean automatiskBehandlet;
    private String sakspartId;
    private String sakspartNavn;
    private String mottakerId;
    private String mottakerNavn;
    private DokumentData dokumentData;
    private LocalDate dokumentDato;
    private PersonStatus sakspartPersonStatus;
    private Kopi erKopi;
    private MottakerType mottakerType;

    public DokumentFelles() {
        // Cdi
    }

    public DokumentFelles(DokumentData dokumentData) {
        this.dokumentData = dokumentData;
        dokumentData.addDokumentFelles(this);
    }

    public static Builder builder(DokumentData dokumentData) {
        return new DokumentFelles.Builder(dokumentData);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Språkkode getSpråkkode() {
        return språkkode;
    }

    public Saksnummer getSaksnummer() {
        return saksnummer;
    }

    public Boolean getAutomatiskBehandlet() {
        return automatiskBehandlet;
    }

    public String getSakspartId() {
        return sakspartId;
    }

    public String getSakspartNavn() {
        return sakspartNavn;
    }

    public String getMottakerId() {
        return mottakerId;
    }

    public String getMottakerNavn() {
        return mottakerNavn;
    }

    public DokumentData getDokumentData() {
        return dokumentData;
    }

    public LocalDate getDokumentDato() {
        return dokumentDato;
    }

    public PersonStatus getSakspartPersonStatus() {
        return sakspartPersonStatus;
    }

    public Optional<Kopi> getErKopi() {
        return Optional.ofNullable(erKopi);
    }

    public MottakerType getMottakerType() {
        return mottakerType;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof DokumentFelles dokFelles)) {
            return false;
        }
        return Objects.equals(getSpråkkode(), dokFelles.getSpråkkode()) && Objects.equals(saksnummer, dokFelles.getSaksnummer()) && Objects.equals(automatiskBehandlet,
            dokFelles.getAutomatiskBehandlet()) && Objects.equals(sakspartId, dokFelles.getSakspartId()) && Objects.equals(sakspartNavn,
            dokFelles.getSakspartNavn())  && Objects.equals(mottakerId, dokFelles.getMottakerId()) && Objects.equals(mottakerNavn, dokFelles.getMottakerNavn()) && Objects.equals(dokumentDato,
            dokFelles.getDokumentDato()) && Objects.equals(sakspartPersonStatus, dokFelles.getSakspartPersonStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSpråkkode(), saksnummer, automatiskBehandlet, sakspartId, sakspartNavn, mottakerId, mottakerNavn, dokumentDato, sakspartPersonStatus);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "<>";
    }

    public static class Builder {

        private DokumentFelles dokumentFelles;

        public Builder() {
            this.dokumentFelles = new DokumentFelles();
        }

        public Builder(DokumentData dokumentData) {
            this.dokumentFelles = new DokumentFelles(dokumentData);
        }

        public Builder medSpråkkode(Språkkode språkkode) {
            this.dokumentFelles.språkkode = språkkode;
            return this;
        }

        public Builder medSaksnummer(Saksnummer saksnummer) {
            this.dokumentFelles.saksnummer = saksnummer;
            return this;
        }

        public Builder medAutomatiskBehandlet(Boolean automatiskBehandlet) {
            this.dokumentFelles.automatiskBehandlet = automatiskBehandlet;
            return this;
        }

        public Builder medSakspartId(PersonIdent sakspartIdent) {
            this.dokumentFelles.sakspartId = sakspartIdent == null ? null : sakspartIdent.getIdent();
            return this;
        }

        public Builder medSakspartNavn(String sakspartNavn) {
            this.dokumentFelles.sakspartNavn = sakspartNavn;
            return this;
        }

        public Builder medMottakerId(String mottakerId) {
            medMottakerId(mottakerId == null ? null : new PersonIdent(mottakerId));
            return this;
        }

        public Builder medMottakerId(PersonIdent personIdent) {
            this.dokumentFelles.mottakerId = personIdent == null ? null : personIdent.getIdent();
            return this;
        }

        public Builder medMottakerNavn(String mottakerNavn) {
            this.dokumentFelles.mottakerNavn = mottakerNavn;
            return this;
        }

        public Builder medDokumentDato(LocalDate dokumentDato) {
            this.dokumentFelles.dokumentDato = dokumentDato;
            return this;
        }

        public Builder medSakspartPersonStatus(PersonStatus sakspartPersonStatus) {
            this.dokumentFelles.sakspartPersonStatus = sakspartPersonStatus;
            return this;
        }

        public Builder medErKopi(Kopi kopi) {
            this.dokumentFelles.erKopi = kopi;
            return this;
        }

        public Builder medMottakerType(MottakerType mottakerType) {
            this.dokumentFelles.mottakerType = mottakerType;
            return this;
        }

        public DokumentFelles build() {
            verifyStateForBuild();
            return dokumentFelles;
        }

        private void verifyStateForBuild() {
            Objects.requireNonNull(dokumentFelles.språkkode, "språkkode");
            Objects.requireNonNull(dokumentFelles.saksnummer, "saksnummer");
            Objects.requireNonNull(dokumentFelles.automatiskBehandlet, "automatiskBehandlet");
            Objects.requireNonNull(dokumentFelles.sakspartId, "sakspartId");
            Objects.requireNonNull(dokumentFelles.sakspartNavn, "sakspartNavn");
            Objects.requireNonNull(dokumentFelles.mottakerId, "mottakerId");
            Objects.requireNonNull(dokumentFelles.mottakerNavn, "mottakerNavn");
            Objects.requireNonNull(dokumentFelles.dokumentDato, "dokumentDato");
            Objects.requireNonNull(dokumentFelles.dokumentData, "dokumentData");
        }
    }
}

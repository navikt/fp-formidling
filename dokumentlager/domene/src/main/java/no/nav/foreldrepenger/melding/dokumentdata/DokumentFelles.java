package no.nav.foreldrepenger.melding.dokumentdata;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinFormula;

import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.typer.PersonIdent;
import no.nav.foreldrepenger.melding.typer.Saksnummer;
import no.nav.vedtak.felles.jpa.converters.BooleanToStringConverter;

@Entity(name = "DokumentFelles")
@Table(name = "DOKUMENT_FELLES")
public class DokumentFelles extends BaseEntitet {

    public enum Kopi
    {
        JA,
        NEI;
    }

    public enum MottakerType {
        PERSON,
        ORGANISASJON;
    }



        @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DOKUMENT_FELLES")
    private Long id;

    @Version
    @Column(name = "versjon", nullable = false)
    private long versjon;

    @ManyToOne(optional = false)
    @JoinColumnOrFormula(column = @JoinColumn(name = "sprak_kode", referencedColumnName = "kode", nullable = false))
    @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "kodeverk", value = "'" + Språkkode.DISCRIMINATOR + "'"))
    private Språkkode språkkode = Språkkode.UDEFINERT;

    /**
     * Offisielt tildelt saksnummer fra GSAK.
     */
    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "saksnummer", column = @Column(name = "saksnummer", nullable = false, updatable = false)))
    private Saksnummer saksnummer;

    @Column(name = "sign_saksbehandler_navn")
    private String signerendeSaksbehandlerNavn;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "automatisk_behandlet", nullable = false)
    private Boolean automatiskBehandlet;

    /**
     * Dokumentbestillingsinformasjon fra dokumentproduksjon tjenesten bruker sakspartId for å sette identen til bruker.
     * Det er derfor ønsket å lagre ned denne for å spore hva slags data som er blitt sendt.
     **/
    @Column(name = "sakspart_id", nullable = false)
    private String sakspartId;

    @Column(name = "sakspart_navn", nullable = false)
    private String sakspartNavn;

    @Column(name = "sign_beslutter_navn")
    private String signerendeBeslutterNavn;

    /**
     * Dokumentbestillingsinformasjon fra dokumentproduksjon tjenesten bruker mottakerId for å sette identen til
     * mottaker.
     * Det er derfor ønsket å lagre ned denne for å spore hva slags data som er blitt sendt.
     **/
    @Column(name = "mottaker_id", nullable = false)
    private String mottakerId;

    @Column(name = "mottaker_navn", nullable = false)
    private String mottakerNavn;

    @Transient
    private DokumentAdresse mottakerAdresse;

    @Column(name = "navn_avsender_enhet", nullable = false)
    private String navnAvsenderEnhet;

    @Column(name = "kontakt_tlf", nullable = false)
    private String kontaktTlf;

    @Transient
    private DokumentAdresse returadresse;

    @Transient
    private DokumentAdresse postadresse;

    @ManyToOne(optional = false)
    @JoinColumn(name = "dokument_data_id", nullable = false, updatable = false)
    private DokumentData dokumentData;

    @Column(name = "dokument_dato", nullable = false)
    private LocalDate dokumentDato;

    @Column(name = "sakspart_person_status", nullable = false)
    private String sakspartPersonStatus;

    @Column
    private String xml;

    @Transient
    private Optional<Kopi> erKopi;

    @Transient
    private MottakerType mottakerType;

    public DokumentFelles() {
        // Hibernate
    }

    private DokumentFelles(DokumentData dokumentData) {
        this.dokumentData = dokumentData;
        dokumentData.addDokumentFelles(this);
    }

    public static DokumentFelles.Builder builder(DokumentData dokumentData) {
        return new DokumentFelles.Builder(dokumentData);
    }

    public static DokumentFelles.Builder builder() {
        return new DokumentFelles.Builder();
    }

    public Long getId() {
        return id;
    }

    public Språkkode getSpråkkode() {
        return språkkode;
    }

    public Saksnummer getSaksnummer() {
        return saksnummer;
    }

    public String getSignerendeSaksbehandlerNavn() {
        return signerendeSaksbehandlerNavn;
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

    public String getSignerendeBeslutterNavn() {
        return signerendeBeslutterNavn;
    }

    public String getMottakerId() {
        return mottakerId;
    }

    public String getMottakerNavn() {
        return mottakerNavn;
    }

    public DokumentAdresse getMottakerAdresse() {
        return mottakerAdresse;
    }

    public String getNavnAvsenderEnhet() {
        return navnAvsenderEnhet;
    }

    public String getKontaktTlf() {
        return kontaktTlf;
    }

    public DokumentData getDokumentData() {
        return dokumentData;
    }

    public DokumentAdresse getReturadresse() {
        return returadresse;
    }

    public DokumentAdresse getPostadresse() {
        return postadresse;
    }

    public LocalDate getDokumentDato() {
        return dokumentDato;
    }

    public String getSakspartPersonStatus() {
        return sakspartPersonStatus;
    }

    public Optional<Kopi> getErKopi() { return erKopi; }

    public MottakerType getMottakerType() { return mottakerType; }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof DokumentFelles)) {
            return false;
        }
        DokumentFelles dokFelles = (DokumentFelles) object;
        return Objects.equals(getSpråkkode(), dokFelles.getSpråkkode())
                && Objects.equals(saksnummer, dokFelles.getSaksnummer())
                && Objects.equals(signerendeSaksbehandlerNavn, dokFelles.getSignerendeSaksbehandlerNavn())
                && Objects.equals(automatiskBehandlet, dokFelles.getAutomatiskBehandlet())
                && Objects.equals(sakspartId, dokFelles.getSakspartId())
                && Objects.equals(sakspartNavn, dokFelles.getSakspartNavn())
                && Objects.equals(signerendeBeslutterNavn, dokFelles.getSignerendeBeslutterNavn())
                && Objects.equals(mottakerId, dokFelles.getMottakerId())
                && Objects.equals(mottakerNavn, dokFelles.getMottakerNavn())
                && Objects.equals(navnAvsenderEnhet, dokFelles.getNavnAvsenderEnhet())
                && Objects.equals(kontaktTlf, dokFelles.getKontaktTlf())
                && Objects.equals(dokumentDato, dokFelles.getDokumentDato())
                && Objects.equals(sakspartPersonStatus, dokFelles.getSakspartPersonStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSpråkkode(), saksnummer, signerendeSaksbehandlerNavn, automatiskBehandlet, sakspartId, sakspartNavn,
                signerendeBeslutterNavn, mottakerId, mottakerNavn, navnAvsenderEnhet,
                kontaktTlf, dokumentDato, sakspartPersonStatus);
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

        public DokumentFelles.Builder medSpråkkode(Språkkode språkkode) {
            this.dokumentFelles.språkkode = språkkode == null ? Språkkode.UDEFINERT : språkkode;
            return this;
        }

        public DokumentFelles.Builder medSaksnummer(Saksnummer saksnummer) {
            this.dokumentFelles.saksnummer = saksnummer;
            return this;
        }

        public DokumentFelles.Builder medSignerendeBeslutterNavn(String signerendeBeslutterNavn) {
            this.dokumentFelles.signerendeBeslutterNavn = signerendeBeslutterNavn;
            return this;
        }

        public DokumentFelles.Builder medSignerendeSaksbehandlerNavn(String signerendeSaksbehandlerNavn) {
            this.dokumentFelles.signerendeSaksbehandlerNavn = signerendeSaksbehandlerNavn;
            return this;
        }

        public DokumentFelles.Builder medAutomatiskBehandlet(Boolean automatiskBehandlet) {
            this.dokumentFelles.automatiskBehandlet = automatiskBehandlet;
            return this;
        }

        public DokumentFelles.Builder medSakspartId(String sakspartId) {
            medSakspartId(sakspartId == null ? null : new PersonIdent(sakspartId));
            return this;
        }

        public Builder medSakspartId(PersonIdent sakspartIdent) {
            this.dokumentFelles.sakspartId = sakspartIdent == null ? null : sakspartIdent.getIdent();
            return this;
        }

        public DokumentFelles.Builder medSakspartNavn(String sakspartNavn) {
            this.dokumentFelles.sakspartNavn = sakspartNavn;
            return this;
        }

        public DokumentFelles.Builder medMottakerId(String mottakerId) {
            medMottakerId(mottakerId == null ? null : new PersonIdent(mottakerId));
            return this;
        }

        public Builder medMottakerId(PersonIdent personIdent) {
            this.dokumentFelles.mottakerId = personIdent == null ? null : personIdent.getIdent();
            return this;
        }

        public DokumentFelles.Builder medMottakerNavn(String mottakerNavn) {
            this.dokumentFelles.mottakerNavn = mottakerNavn;
            return this;
        }

        public DokumentFelles.Builder medMottakerAdresse(DokumentAdresse mottakerAdresse) {
            this.dokumentFelles.mottakerAdresse = mottakerAdresse;
            return this;
        }

        public DokumentFelles.Builder medNavnAvsenderEnhet(String navnAvsenderEnhet) {
            this.dokumentFelles.navnAvsenderEnhet = navnAvsenderEnhet;
            return this;
        }

        public DokumentFelles.Builder medKontaktTelefonNummer(String kontaktTlf) {
            this.dokumentFelles.kontaktTlf = kontaktTlf;
            return this;
        }

        public DokumentFelles.Builder medReturadresse(DokumentAdresse returadresse) {
            this.dokumentFelles.returadresse = returadresse;
            return this;
        }

        public DokumentFelles.Builder medPostadresse(DokumentAdresse postadresse) {
            this.dokumentFelles.postadresse = postadresse;
            return this;
        }

        public DokumentFelles.Builder medDokumentDato(LocalDate dokumentDato) {
            this.dokumentFelles.dokumentDato = dokumentDato;
            return this;
        }

        public DokumentFelles.Builder medSakspartPersonStatus(String sakspartPersonStatus) {
            this.dokumentFelles.sakspartPersonStatus = sakspartPersonStatus;
            return this;
        }

        public DokumentFelles.Builder medXml(String xml) {
            this.dokumentFelles.xml = xml;
            return this;
        }

        public DokumentFelles.Builder medErKopi(Optional<Kopi> kopi) {
            this.dokumentFelles.erKopi = kopi;
            return this;
        }

        public DokumentFelles.Builder medMottakerType(MottakerType mottakerType) {
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
            Objects.requireNonNull(dokumentFelles.mottakerAdresse, "mottakerAdresse");
            Objects.requireNonNull(dokumentFelles.navnAvsenderEnhet, "navnAvsenderEnhet");
            Objects.requireNonNull(dokumentFelles.kontaktTlf, "kontaktTelefonNummer");
            Objects.requireNonNull(dokumentFelles.returadresse, "returadresse");
            Objects.requireNonNull(dokumentFelles.postadresse, "behandling");
            Objects.requireNonNull(dokumentFelles.dokumentDato, "dokumentDato");
            Objects.requireNonNull(dokumentFelles.dokumentData, "dokumentData");
        }
    }
}

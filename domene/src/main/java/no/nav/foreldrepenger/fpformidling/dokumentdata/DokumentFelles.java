package no.nav.foreldrepenger.fpformidling.dokumentdata;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.AttributeConverter;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.jpa.BaseEntitet;
import no.nav.foreldrepenger.fpformidling.typer.PersonIdent;
import no.nav.foreldrepenger.fpformidling.typer.Saksnummer;
import no.nav.vedtak.felles.jpa.converters.BooleanToStringConverter;

@Entity(name = "DokumentFelles")
@Table(name = "DOKUMENT_FELLES")
public class DokumentFelles extends BaseEntitet {

    public enum PersonStatus {
        DOD,
        ANNET;

        @Converter(autoApply = true)
        public static class KodeverdiConverter implements AttributeConverter<PersonStatus, String> {
            @Override
            public String convertToDatabaseColumn(PersonStatus status) {
                return status == null ? null : status.name();
            }

            @Override
            public PersonStatus convertToEntityAttribute(String status) {
                return status == null ? null : PersonStatus.valueOf(status);
            }
        }
    }

    public enum Kopi {
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

    @Enumerated(EnumType.STRING)
    @Column(name = "sprak_kode", nullable = false)
    private Språkkode språkkode;

    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "saksnummer", column = @Column(name = "saksnummer", nullable = false, updatable = false)))
    private Saksnummer saksnummer;

    @Column(name = "sign_saksbehandler_navn")
    private String signerendeSaksbehandlerNavn;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "automatisk_behandlet", nullable = false)
    private Boolean automatiskBehandlet;

    @Column(name = "sakspart_id", nullable = false)
    private String sakspartId;

    @Column(name = "sakspart_navn", nullable = false)
    private String sakspartNavn;

    @Column(name = "sign_beslutter_navn")
    private String signerendeBeslutterNavn;

    @Column(name = "mottaker_id", nullable = false)
    private String mottakerId;

    @Column(name = "mottaker_navn", nullable = false)
    private String mottakerNavn;

    @ManyToOne(optional = false)
    @JoinColumn(name = "dokument_data_id", nullable = false, updatable = false)
    private DokumentData dokumentData;

    @Column(name = "dokument_dato", nullable = false)
    private LocalDate dokumentDato;

    @Convert(converter = PersonStatus.KodeverdiConverter.class)
    @Column(name = "sakspart_person_status", nullable = false)
    private PersonStatus sakspartPersonStatus;

    @Column(name = "brev_data")
    private String brevData;

    @Transient
    private Kopi erKopi;

    @Transient
    private MottakerType mottakerType;

    public DokumentFelles() {
        // Hibernate
    }

    private DokumentFelles(DokumentData dokumentData) {
        this.dokumentData = dokumentData;
        dokumentData.addDokumentFelles(this);
    }

    public static Builder builder(DokumentData dokumentData) {
        return new DokumentFelles.Builder(dokumentData);
    }

    public static Builder builder() {
        return new Builder();
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

    public void setBrevData(String brevData) {
        this.brevData = brevData;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof DokumentFelles)) {
            return false;
        }
        var dokFelles = (DokumentFelles) object;
        return Objects.equals(getSpråkkode(), dokFelles.getSpråkkode())
                && Objects.equals(saksnummer, dokFelles.getSaksnummer())
                && Objects.equals(signerendeSaksbehandlerNavn, dokFelles.getSignerendeSaksbehandlerNavn())
                && Objects.equals(automatiskBehandlet, dokFelles.getAutomatiskBehandlet())
                && Objects.equals(sakspartId, dokFelles.getSakspartId())
                && Objects.equals(sakspartNavn, dokFelles.getSakspartNavn())
                && Objects.equals(signerendeBeslutterNavn, dokFelles.getSignerendeBeslutterNavn())
                && Objects.equals(mottakerId, dokFelles.getMottakerId())
                && Objects.equals(mottakerNavn, dokFelles.getMottakerNavn())
                && Objects.equals(dokumentDato, dokFelles.getDokumentDato())
                && Objects.equals(sakspartPersonStatus, dokFelles.getSakspartPersonStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSpråkkode(), saksnummer, signerendeSaksbehandlerNavn, automatiskBehandlet, sakspartId, sakspartNavn,
                signerendeBeslutterNavn, mottakerId, mottakerNavn, dokumentDato, sakspartPersonStatus);
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

        public Builder medSignerendeBeslutterNavn(String signerendeBeslutterNavn) {
            this.dokumentFelles.signerendeBeslutterNavn = signerendeBeslutterNavn;
            return this;
        }

        public Builder medSignerendeSaksbehandlerNavn(String signerendeSaksbehandlerNavn) {
            this.dokumentFelles.signerendeSaksbehandlerNavn = signerendeSaksbehandlerNavn;
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

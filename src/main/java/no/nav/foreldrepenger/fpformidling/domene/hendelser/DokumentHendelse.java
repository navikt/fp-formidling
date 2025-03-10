package no.nav.foreldrepenger.fpformidling.domene.hendelser;

import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import no.nav.foreldrepenger.fpformidling.felles.BaseEntitet;
import no.nav.foreldrepenger.fpformidling.typer.DokumentMal;
import no.nav.foreldrepenger.fpformidling.typer.RevurderingÅrsak;

@Entity(name = "DokumentHendelse")
@Table(name = "DOKUMENT_HENDELSE")
public class DokumentHendelse extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DOKUMENT_HENDELSE")
    private Long id;

    @Column(name = "behandling_uuid")
    private UUID behandlingUuid;

    @Column(name = "bestilling_uuid")
    private UUID bestillingUuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "dokument_mal")
    private DokumentMal dokumentMal;

    @Enumerated(EnumType.STRING)
    @Column(name = "journalfoer_som")
    private DokumentMal journalførSom;

    @Transient
    private String tittel;

    @Column
    private String fritekst;

    @Enumerated(EnumType.STRING)
    @Column(name = "revurdering_aarsak")
    private RevurderingÅrsak revurderingÅrsak;

    public DokumentHendelse() {
        //For Hibernate
    }

    public static DokumentHendelse.Builder builder() {
        return new DokumentHendelse.Builder();
    }

    public Long getId() {
        return id;
    }

    public UUID getBehandlingUuid() {
        return behandlingUuid;
    }

    public UUID getBestillingUuid() {
        return bestillingUuid;
    }

    public String getTittel() {
        return tittel;
    }

    public String getFritekst() {
        return fritekst;
    }

    public DokumentMal getDokumentMal() {
        return dokumentMal;
    }

    public DokumentMal getJournalførSom() {
        return journalførSom;
    }

    public RevurderingÅrsak getRevurderingÅrsak() {
        return revurderingÅrsak;
    }

    @Override
    public String toString() {
        return "DokumentHendelse{" + "id=" + id + ", behandlingUuid=" + behandlingUuid + ", bestillingUuid=" + bestillingUuid + ", dokumentMal="
                + dokumentMal + ", tittel='" + tittel + '\'' + ", fritekst='" + (
                fritekst != null ? "****** fritekst ***** " : "null") + '\'' + ", revurderingÅrsak=" + revurderingÅrsak
                + ", journalførSom=" + journalførSom +'}';
    }


    public static class Builder {
        private DokumentHendelse kladd;

        Builder() {
            this.kladd = new DokumentHendelse();
        }

        public DokumentHendelse.Builder medDokumentMal(DokumentMal dokumentMal) {
            this.kladd.dokumentMal = dokumentMal;
            return this;
        }

        public DokumentHendelse.Builder medJournalførSom(DokumentMal medJournalførSom) {
            this.kladd.journalførSom = medJournalførSom;
            return this;
        }

        public DokumentHendelse.Builder medBehandlingUuid(UUID behandlingUuid) {
            this.kladd.behandlingUuid = behandlingUuid;
            return this;
        }

        public DokumentHendelse.Builder medBestillingUuid(UUID bestillingUuid) {
            this.kladd.bestillingUuid = bestillingUuid;
            return this;
        }

        public DokumentHendelse.Builder medTittel(String tittel) {
            this.kladd.tittel = tittel;
            return this;
        }

        public DokumentHendelse.Builder medFritekst(String fritekst) {
            this.kladd.fritekst = fritekst;
            return this;
        }

        public DokumentHendelse.Builder medRevurderingÅrsak(RevurderingÅrsak revurderingÅrsak) {
            this.kladd.revurderingÅrsak = revurderingÅrsak;
            return this;
        }

        public DokumentHendelse build() {
            verifyStateForBuild();
            return kladd;
        }

        private void verifyStateForBuild() {
            Objects.requireNonNull(kladd.dokumentMal, "dokumentMal");
            Objects.requireNonNull(kladd.behandlingUuid, "behandlingUuid");
            Objects.requireNonNull(kladd.bestillingUuid, "bestillingUuid");
        }

    }

    // TEST USE ONLY
    public void setId(Long id) {
        this.id = id;
    }
}

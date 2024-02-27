package no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller;

import jakarta.persistence.*;
import no.nav.foreldrepenger.fpformidling.felles.BaseEntitet;
import no.nav.foreldrepenger.fpformidling.typer.DokumentMalEnum;
import no.nav.foreldrepenger.fpformidling.typer.RevurderingÅrsakEnum;

import java.util.Objects;
import java.util.UUID;

@Entity(name = "DokumentHendelse")
@Table(name = "DOKUMENT_HENDELSE")
private class DokumentHendelseEntitet extends BaseEntitet {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DOKUMENT_HENDELSE")
    private Long id;

    @Column(name = "behandling_uuid")
    private UUID behandlingUuid;

    @Column(name = "bestilling_uuid")
    private UUID bestillingUuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "dokument_mal")
    private DokumentMalEnum dokumentMal;

    @Enumerated(EnumType.STRING)
    @Column(name = "journalfoer_som")
    private DokumentMalEnum journalførSom;

    @Enumerated(EnumType.STRING)
    @Column(name = "revurdering_aarsak")
    private RevurderingÅrsakEnum revurderingÅrsak;

    @Column
    private String fritekst;

    @Transient
    private String tittel;

    DokumentHendelseEntitet() {
        // For Hibernate
    }

    public static DokumentHendelseEntitet.Builder builder() {
        return new DokumentHendelseEntitet.Builder();
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

    public DokumentMalEnum getDokumentMal() {
        return dokumentMal;
    }

    public DokumentMalEnum getJournalførSom() {
        return journalførSom;
    }

    public String getFritekst() {
        return fritekst;
    }

    public String getTittel() {
        return tittel;
    }

    public RevurderingÅrsakEnum getRevurderingÅrsak() {
        return revurderingÅrsak;
    }

    @Override
    public String toString() {
        return "DokumentHendelse{" + "id=" + id + ", behandlingUuid=" + behandlingUuid + ", bestillingUuid=" + bestillingUuid + ", dokumentMal="
                + dokumentMal + '\'' + ", fritekst='" + (fritekst != null ? "****** fritekst ***** " : "null") + '\'' + ", revurderingÅrsak=" + revurderingÅrsak
                + ", journalførSom=" + journalførSom +'}';
    }


    public static class Builder {
        private DokumentHendelseEntitet kladd;

        Builder() {
            this.kladd = new DokumentHendelseEntitet();
        }

        public DokumentHendelseEntitet.Builder medBehandlingUuid(UUID behandlingUuid) {
            this.kladd.behandlingUuid = behandlingUuid;
            return this;
        }

        public DokumentHendelseEntitet.Builder medBestillingUuid(UUID bestillingUuid) {
            this.kladd.bestillingUuid = bestillingUuid;
            return this;
        }

        public DokumentHendelseEntitet.Builder medDokumentMal(DokumentMalEnum dokumentMal) {
            this.kladd.dokumentMal = dokumentMal;
            return this;
        }

        public DokumentHendelseEntitet.Builder medJournalførSom(DokumentMalEnum medJournalførSom) {
            this.kladd.journalførSom = medJournalførSom;
            return this;
        }

        public DokumentHendelseEntitet.Builder medFritekst(String fritekst) {
            this.kladd.fritekst = fritekst;
            return this;
        }

        public DokumentHendelseEntitet.Builder medTittel(String tittel) {
            this.kladd.tittel = tittel;
            return this;
        }

        public DokumentHendelseEntitet.Builder medRevurderingÅrsak(RevurderingÅrsakEnum revurderingÅrsak) {
            this.kladd.revurderingÅrsak = revurderingÅrsak;
            return this;
        }

        public DokumentHendelseEntitet build() {
            verifyStateForBuild();
            return kladd;
        }

        private void verifyStateForBuild() {
            Objects.requireNonNull(kladd.behandlingUuid, "behandlingUuid");
            Objects.requireNonNull(kladd.bestillingUuid, "bestillingUuid");
            Objects.requireNonNull(kladd.dokumentMal, "dokumentMal");
        }

    }

    // TEST USE ONLY
    public void setId(Long id) {
        this.id = id;
    }
}

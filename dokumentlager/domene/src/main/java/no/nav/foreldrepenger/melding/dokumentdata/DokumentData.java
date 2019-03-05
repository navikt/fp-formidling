package no.nav.foreldrepenger.melding.dokumentdata;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import no.nav.foreldrepenger.melding.kodeverk.diff.IndexKey;

@Entity(name = "DokumentData")
@Table(name = "DOKUMENT_DATA")
public class DokumentData extends BaseEntitet implements IndexKey {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DOKUMENT_DATA")
    private Long id;

    @Version
    @Column(name = "versjon", nullable = false)
    private long versjon;

    @ManyToOne(optional = false)
    @JoinColumn(name = "dokument_mal_navn", nullable = false)
    private DokumentMalType dokumentMalType;

    @Column(name = "forhaandsvist_tid")
    private LocalDateTime forhåndsvistTid;

    @Column(name = "sendt_tid")
    private LocalDateTime sendtTid;

    @OneToMany(mappedBy = "dokumentData")
    private Set<DokumentFelles> dokumentFelles = new HashSet<>(1);

    @Column(name = "behandling_id", nullable = false)
    private Long behandling;

    @Column(name = "bestilt_tid")
    private LocalDateTime bestiltTid;

    @SuppressWarnings("unused")
    private DokumentData() {
    }

    public DokumentData(DokumentMalType dokumentMalType, Long behandling) {
        this.dokumentMalType = dokumentMalType;
        this.behandling = behandling;
    }

    @Override
    public String getIndexKey() {
        return IndexKey.createKey(dokumentMalType, bestiltTid);
    }

    public Long getId() {
        return id;
    }

    public DokumentMalType getDokumentMalType() {
        return dokumentMalType;
    }

    public LocalDateTime getForhåndsvistTid() {
        return forhåndsvistTid;
    }

    public void setForhåndsvistTid(LocalDateTime forhåndsvistTid) {
        this.forhåndsvistTid = forhåndsvistTid;
    }

    public LocalDateTime getSendtTid() {
        return sendtTid;
    }

    public void setSendtTid(LocalDateTime sendtTid) {
        this.sendtTid = sendtTid;
    }

    public Set<DokumentFelles> getDokumentFelles() {
        return dokumentFelles;
    }

    public void addDokumentFelles(DokumentFelles dokumentFelles) {
        this.dokumentFelles.add(dokumentFelles);
    }

    public DokumentFelles getFørsteDokumentFelles() {
        return dokumentFelles.iterator().next();
    }

    public Long getBehandling() {
        return behandling;
    }

    public LocalDateTime getBestiltTid() {
        return bestiltTid;
    }

    public void setBestiltTid(LocalDateTime bestiltTid) {
        this.bestiltTid = bestiltTid;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof DokumentData)) {
            return false;
        }
        DokumentData dokData = (DokumentData) object;
        return Objects.equals(dokumentMalType, dokData.getDokumentMalType())
                && Objects.equals(forhåndsvistTid, dokData.getForhåndsvistTid())
                && Objects.equals(sendtTid, dokData.getSendtTid())
                && Objects.equals(bestiltTid, dokData.getBestiltTid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(dokumentMalType, forhåndsvistTid, sendtTid, bestiltTid);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "<>";
    }

    public static DokumentData opprettNy(DokumentMalType dokumentMalType, Long behandling) {
        return new DokumentData(dokumentMalType, behandling);
    }

    public static DokumentData.Builder builder() {
        return new DokumentData.Builder();
    }

    public static class Builder {
        private DokumentMalType dokumentMalType;
        private LocalDateTime forhåndsvistTid;
        private LocalDateTime sendtTid;
        private LocalDateTime bestiltTid;
        private Long behandling;

        public DokumentData.Builder medDokumentMalType(DokumentMalType dokumentMalType) {
            this.dokumentMalType = dokumentMalType;
            return this;
        }

        public DokumentData.Builder medForhåndsvistTid(LocalDateTime forhåndsvistTid) {
            this.forhåndsvistTid = forhåndsvistTid;
            return this;
        }

        public DokumentData.Builder medSendtTid(LocalDateTime sendtTid) {
            this.sendtTid = sendtTid;
            return this;
        }

        public DokumentData.Builder medBestiltTid(LocalDateTime bestiltTid) {
            this.bestiltTid = bestiltTid;
            return this;
        }

        public DokumentData.Builder medBehandling(Long behandling) {
            this.behandling = behandling;
            return this;
        }

        public DokumentData build() {
            verifyStateForBuild();
            DokumentData dokData = new DokumentData(dokumentMalType, behandling);
            dokData.forhåndsvistTid = forhåndsvistTid;
            dokData.sendtTid = sendtTid;
            dokData.bestiltTid = bestiltTid;

            return dokData;
        }

        public void verifyStateForBuild() {
            Objects.requireNonNull(dokumentMalType, "dokumentMalType");
            Objects.requireNonNull(behandling, "behandling");
        }
    }
}

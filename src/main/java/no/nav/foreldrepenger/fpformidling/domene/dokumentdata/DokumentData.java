package no.nav.foreldrepenger.fpformidling.domene.dokumentdata;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

public class DokumentData {

    private DokumentMalType dokumentMalType = DokumentMalType.UDEFINERT;

    private final Set<DokumentFelles> dokumentFelles = HashSet.newHashSet(1);

    private UUID behandlingUuid;

    private LocalDateTime bestiltTid;

    private String bestillingType;

    @SuppressWarnings("unused")
    public DokumentData() {
        // CDI
    }

    private DokumentData(Builder builder) {
        dokumentMalType = builder.dokumentMalType;
        behandlingUuid = builder.behandlingUuid;
        bestiltTid = builder.bestiltTid;
        bestillingType = builder.bestillingType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public DokumentMalType getDokumentMalType() {
        return dokumentMalType;
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

    public UUID getBehandlingUuid() {
        return behandlingUuid;
    }

    public LocalDateTime getBestiltTid() {
        return bestiltTid;
    }

    public String getBestillingType() {
        return bestillingType;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof DokumentData)) {
            return false;
        }
        var dokData = (DokumentData) object;
        return Objects.equals(dokumentMalType, dokData.getDokumentMalType()) && Objects.equals(behandlingUuid, dokData.getBehandlingUuid())
            && Objects.equals(bestiltTid, dokData.getBestiltTid()) && Objects.equals(bestillingType, dokData.getBestillingType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(dokumentMalType, behandlingUuid, bestiltTid, bestillingType);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "<>";
    }

    public static final class Builder {

        private DokumentMalType dokumentMalType;
        private UUID behandlingUuid;
        private LocalDateTime bestiltTid;
        private String bestillingType;

        private Builder() {
        }

        public Builder medDokumentMalType(DokumentMalType dokumentMalType) {
            this.dokumentMalType = dokumentMalType;
            return this;
        }

        public Builder medBehandlingUuid(UUID behandlingUuid) {
            this.behandlingUuid = behandlingUuid;
            return this;
        }

        public Builder medBestiltTid(LocalDateTime bestiltTid) {
            this.bestiltTid = bestiltTid;
            return this;
        }

        public Builder medBestillingType(String bestillingType) {
            this.bestillingType = bestillingType;
            return this;
        }

        public DokumentData build() {
            verifyStateForBuild();
            return new DokumentData(this);
        }

        private void verifyStateForBuild() {
            Objects.requireNonNull(dokumentMalType, "dokumentMalType");
            Objects.requireNonNull(behandlingUuid, "behandlingUuid");
            Objects.requireNonNull(bestiltTid, "bestiltTid");
            Objects.requireNonNull(bestillingType, "bestillingType");
        }
    }
}

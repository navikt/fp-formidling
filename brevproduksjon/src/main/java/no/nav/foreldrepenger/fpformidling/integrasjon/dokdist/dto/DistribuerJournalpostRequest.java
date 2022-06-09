package no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Fagsystem;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;

public record DistribuerJournalpostRequest(String journalpostId,
                                           String batchId,
                                           String bestillendeFagsystem,
                                           String dokumentProdApp,
                                           @JsonInclude(Include.NON_NULL) Distribusjonstype distribusjonstype,
                                           @JsonInclude(Include.NON_NULL) Distribusjonstidspunkt distribusjonstidspunkt) {
    public DistribuerJournalpostRequest(JournalpostId id,
                                        Fagsystem fagsystem,
                                        String bestillingId,
                                        Distribusjonstype distribusjonstype) {
        this(id.getVerdi(),
                bestillingId,
                fagsystem.getOffisiellKode(),
                fagsystem.getKode(),
                distribusjonstype,
                Distribusjonstidspunkt.KJERNETID);
    }
}
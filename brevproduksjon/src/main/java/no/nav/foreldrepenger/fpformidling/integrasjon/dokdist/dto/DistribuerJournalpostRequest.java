package no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Fagsystem;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;

public record DistribuerJournalpostRequest(String journalpostId,
                                           String batchId,
                                           String bestillendeFagsystem,
                                           String dokumentProdApp,
                                           Distribusjonstype distribusjonstype,
                                           Distribusjonstidspunkt distribusjonstidspunkt) {
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
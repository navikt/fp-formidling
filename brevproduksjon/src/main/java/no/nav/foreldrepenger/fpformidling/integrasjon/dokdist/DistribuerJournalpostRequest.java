package no.nav.foreldrepenger.fpformidling.integrasjon.dokdist;

import javax.validation.constraints.NotNull;

public record DistribuerJournalpostRequest(@NotNull String journalpostId,
                                           @NotNull String batchId,
                                           @NotNull String bestillendeFagsystem,
                                           @NotNull String dokumentProdApp,
                                           @NotNull Distribusjonstype distribusjonstype,
                                           @NotNull Distribusjonstidspunkt distribusjonstidspunkt) {}

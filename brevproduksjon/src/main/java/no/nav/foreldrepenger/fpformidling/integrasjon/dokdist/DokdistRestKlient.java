package no.nav.foreldrepenger.fpformidling.integrasjon.dokdist;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto.DistribuerJournalpostRequest;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto.DistribuerJournalpostResponse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto.Distribusjonstidspunkt;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto.Distribusjonstype;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Fagsystem;
import no.nav.foreldrepenger.fpformidling.typer.JournalpostId;
import no.nav.foreldrepenger.konfig.KonfigVerdi;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.felles.integrasjon.rest.OidcRestClient;

@Dependent
/*
 *
 */
public class DokdistRestKlient implements Dokdist {
    private static final Logger LOG = LoggerFactory.getLogger(DokdistRestKlient.class);

    private final OidcRestClient oidcRestClient;
    private final String endpointDokdistRestBase;

    @Inject
    public DokdistRestKlient(OidcRestClient oidcRestClient,
            @KonfigVerdi("dokdist.rest.base.url") String endpointDokdistRestBase) {
        this.oidcRestClient = oidcRestClient;
        this.endpointDokdistRestBase = endpointDokdistRestBase;
    }

    @Override
    public Dokdist.Resultat distribuerJournalpost(JournalpostId journalpostId, String bestillingId, Distribusjonstype distribusjonstype) {
        DistribuerJournalpostRequest request = lagRequest(journalpostId, bestillingId, distribusjonstype);
        try {
            URIBuilder uriBuilder = new URIBuilder(endpointDokdistRestBase + "/distribuerjournalpost");
            var response = oidcRestClient.postAcceptConflict(uriBuilder.build(), request,
                    DistribuerJournalpostResponse.class);
            if (LOG.isInfoEnabled()) {
                LOG.info("Distribuert {} med bestillingsId {}", journalpostId, response.bestillingsId());
            }
        } catch (Exception e) {
            throw new TekniskException("FPFORMIDLING-647353", String.format("Fikk feil ved kall til dokdist for %s.", journalpostId), e);
        }
        return Resultat.OK;
    }

    private DistribuerJournalpostRequest lagRequest(JournalpostId journalpostId, String bestillingId, Distribusjonstype distribusjonstype) {
        LOG.info("Bestiller distribusjon av {} med batchId {}", journalpostId, bestillingId);
        return new DistribuerJournalpostRequest(journalpostId.getVerdi(),
                bestillingId,
                Fagsystem.FPSAK.getOffisiellKode(),
                Fagsystem.FPSAK.getKode(),
                distribusjonstype,
                Distribusjonstidspunkt.KJERNETID);
    }
}

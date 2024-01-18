package no.nav.foreldrepenger.fpformidling.integrasjon.journal;

import jakarta.enterprise.context.Dependent;

import no.nav.vedtak.felles.integrasjon.dokarkiv.AbstractDokArkivKlient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;

@Dependent
@RestClientConfig(tokenConfig = TokenFlow.ADAPTIVE, endpointProperty = "journalpost.rest.v1.url", endpointDefault = "http://dokarkiv.teamdokumenthandtering/rest/journalpostapi/v1/journalpost", scopesProperty = "journalpost.scopes", scopesDefault = "api://prod-fss.teamdokumenthandtering.dokarkiv/.default")
public class DokArkivKlient extends AbstractDokArkivKlient {

    public static final String AUTOMATISK_JOURNALFØRENDE_ENHET = "9999";
    public static final String TEMA_FORELDREPENGER = "FOR";

    protected DokArkivKlient() {
        super();
    }
}

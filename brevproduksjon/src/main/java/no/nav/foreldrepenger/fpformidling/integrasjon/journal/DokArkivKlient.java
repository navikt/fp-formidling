package no.nav.foreldrepenger.fpformidling.integrasjon.journal;

import javax.enterprise.context.Dependent;

import no.nav.vedtak.felles.integrasjon.dokarkiv.AbstractDokArkivKlient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;

@Dependent
@RestClientConfig(tokenConfig = TokenFlow.STS_CC, endpointProperty = "journalpost.rest.v1.url", endpointDefault = "http://dokarkiv.default/rest/journalpostapi/v1/journalpost")
public class DokArkivKlient extends AbstractDokArkivKlient {

    public static final String AUTOMATISK_JOURNALFØRENDE_ENHET = "9999";
    public static final String TEMA_FORELDREPENGER = "FOR";

    protected DokArkivKlient() {
        super();
    }
}

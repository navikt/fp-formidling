package no.nav.foreldrepenger.fpformidling.integrasjon.organisasjon;

import no.nav.vedtak.felles.integrasjon.organisasjon.AbstractOrganisasjonKlient;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;

import jakarta.enterprise.context.Dependent;

@Dependent
@RestClientConfig(tokenConfig = TokenFlow.NO_AUTH_NEEDED, endpointProperty = "organisasjon.rs.url", endpointDefault = "https://ereg-services.intern.nav.no/api/v2/organisasjon")
public class OrganisasjonKlient extends AbstractOrganisasjonKlient {

    public OrganisasjonKlient() {
        super();
    }
}

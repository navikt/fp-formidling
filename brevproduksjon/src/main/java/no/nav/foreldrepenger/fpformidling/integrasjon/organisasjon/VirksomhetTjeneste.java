package no.nav.foreldrepenger.fpformidling.integrasjon.organisasjon;

import no.nav.vedtak.felles.integrasjon.organisasjon.OrgInfo;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;

@ApplicationScoped
public class VirksomhetTjeneste {

    private OrgInfo eregRestKlient;

    public VirksomhetTjeneste() {
        // CDI
    }

    @Inject
    public VirksomhetTjeneste(OrgInfo eregRestKlient) {
        this.eregRestKlient = eregRestKlient;
    }

    public Virksomhet getOrganisasjon(String orgNummer) {
        var response = eregRestKlient.hentOrganisasjonNavn(orgNummer);
        return new Virksomhet.Builder().medOrgnr(orgNummer).medNavn(response).build();
    }

    public Optional<String> getNavnFor(String orgNummer) {
        var response = eregRestKlient.hentOrganisasjonNavn(orgNummer);
        return Optional.ofNullable(response);
    }
}

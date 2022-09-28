package no.nav.foreldrepenger.fpformidling.integrasjon.organisasjon;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.vedtak.felles.integrasjon.organisasjon.OrgInfo;

@ApplicationScoped
public class VirksomhetTjeneste {

    private OrgInfo eregRestKlient;

    public VirksomhetTjeneste() {
        // CDI
    }

    @Inject
    public VirksomhetTjeneste(@NativeClient OrgInfo eregRestKlient) {
        this.eregRestKlient = eregRestKlient;
    }

    public Virksomhet getOrganisasjon(String orgNummer)  {
        var response = eregRestKlient.hentOrganisasjonAdresse(orgNummer);
        return new Virksomhet.Builder()
                .medOrgnr(orgNummer)
                .medNavn(response.getNavn())
                .build();
    }

    public Optional<String> getNavnFor(String orgNummer)  {
        var response = eregRestKlient.hentOrganisasjonAdresse(orgNummer);
        return Optional.ofNullable(response.getNavn());
    }
}

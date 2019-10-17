package no.nav.foreldrepenger.organisasjon;


import no.nav.tjeneste.virksomhet.organisasjon.v4.binding.HentOrganisasjonOrganisasjonIkkeFunnet;
import no.nav.tjeneste.virksomhet.organisasjon.v4.binding.HentOrganisasjonUgyldigInput;

public interface VirksomhetTjeneste {

    public Virksomhet getOrganisasjon(String orgNummer) throws HentOrganisasjonOrganisasjonIkkeFunnet, HentOrganisasjonUgyldigInput;

}

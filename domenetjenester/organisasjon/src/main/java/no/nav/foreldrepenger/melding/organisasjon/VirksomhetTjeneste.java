package no.nav.foreldrepenger.melding.organisasjon;

import java.util.Optional;
import java.util.function.UnaryOperator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.geografisk.Landkoder;
import no.nav.foreldrepenger.melding.geografisk.Poststed;
import no.nav.foreldrepenger.melding.geografisk.PoststedKodeverkRepository;
import no.nav.vedtak.felles.integrasjon.organisasjon.OrganisasjonRestKlient;

@ApplicationScoped
public class VirksomhetTjeneste {

    private OrganisasjonRestKlient eregRestKlient;
    private PoststedKodeverkRepository kodeverkRepository;

    public VirksomhetTjeneste() {
        // CDI
    }

    @Inject
    public VirksomhetTjeneste(OrganisasjonRestKlient eregRestKlient,
                              PoststedKodeverkRepository kodeverkRepository) {
        this.eregRestKlient = eregRestKlient;
        this.kodeverkRepository = kodeverkRepository;
    }

    public Virksomhet getOrganisasjon(String orgNummer, UnaryOperator<String> landKodeOversetter)  {
        var response = eregRestKlient.hentOrganisasjonAdresse(orgNummer);
        var adresse = response.getKorrespondanseadresse();
        var antaNorsk = adresse.getLandkode() == null || Landkoder.NOR.getKode().equals(adresse.getLandkode()) || "NO".equals(adresse.getLandkode());
        var oversattLandkode = antaNorsk ? Landkoder.NOR.getKode() : landKodeOversetter.apply(adresse.getLandkode());
        var builder = new Virksomhet.Builder()
                .medOrgnr(orgNummer)
                .medNavn(response.getNavn())
                .medAdresselinje1(adresse.getAdresselinje1())
                .medAdresselinje2(adresse.getAdresselinje2())
                .medAdresselinje3(adresse.getAdresselinje3())
                .medLandkode(oversattLandkode)
                .medPostNr(adresse.getPostnummer())
                .medPoststed(adresse.getPoststed());

        if (antaNorsk && adresse.getPostnummer() != null) {
            kodeverkRepository.finnPostnummer(adresse.getPostnummer()).map(Poststed::getPoststednavn).ifPresent(builder::medPoststed);
        }
        return builder.build();
    }

    public Optional<String> getNavnFor(String orgNummer)  {
        var response = eregRestKlient.hentOrganisasjonAdresse(orgNummer);
        return Optional.ofNullable(response.getNavn());
    }
}

package no.nav.foreldrepenger.organisasjon.impl;

import java.util.List;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.geografisk.Poststed;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.organisasjon.Virksomhet;
import no.nav.foreldrepenger.organisasjon.VirksomhetTjeneste;
import no.nav.tjeneste.virksomhet.organisasjon.v4.binding.HentOrganisasjonOrganisasjonIkkeFunnet;
import no.nav.tjeneste.virksomhet.organisasjon.v4.binding.HentOrganisasjonUgyldigInput;
import no.nav.tjeneste.virksomhet.organisasjon.v4.informasjon.GeografiskAdresse;
import no.nav.tjeneste.virksomhet.organisasjon.v4.informasjon.NoekkelVerdiAdresse;
import no.nav.tjeneste.virksomhet.organisasjon.v4.informasjon.Organisasjon;
import no.nav.tjeneste.virksomhet.organisasjon.v4.informasjon.UstrukturertNavn;
import no.nav.tjeneste.virksomhet.organisasjon.v4.meldinger.HentOrganisasjonResponse;
import no.nav.vedtak.felles.integrasjon.organisasjon.OrganisasjonConsumer;
import no.nav.vedtak.felles.integrasjon.organisasjon.hent.HentOrganisasjonRequest;

@ApplicationScoped
public class VirksomhetTjenesteImpl implements VirksomhetTjeneste {

    private OrganisasjonConsumer organisasjonConsumer;
    private KodeverkRepository kodeverkRepository;

    public VirksomhetTjenesteImpl() {
        // CDI
    }

    @Inject
    public VirksomhetTjenesteImpl(OrganisasjonConsumer organisasjonConsumer, KodeverkRepository kodeverkRepository) {
        this.organisasjonConsumer = organisasjonConsumer;
        this.kodeverkRepository = kodeverkRepository;
    }

    public Virksomhet getOrganisasjon(String orgNummer) throws HentOrganisasjonOrganisasjonIkkeFunnet, HentOrganisasjonUgyldigInput {
        HentOrganisasjonResponse response = hentOrganisasjon(orgNummer);
        final Virksomhet virksomhet = mapOrganisasjonResponseToVirksomhet(response.getOrganisasjon());
        return virksomhet;
    }

    private HentOrganisasjonResponse hentOrganisasjon(String orgNummer) throws HentOrganisasjonOrganisasjonIkkeFunnet, HentOrganisasjonUgyldigInput {
        Objects.requireNonNull(orgNummer, "orgNummer"); // NOSONAR
        HentOrganisasjonRequest request = new HentOrganisasjonRequest(orgNummer);
        return organisasjonConsumer.hentOrganisasjon(request);
    }

    private Virksomhet mapOrganisasjonResponseToVirksomhet(Organisasjon responsOrganisasjon) {
        var builder = new Virksomhet.Builder();
        builder.medNavn(((UstrukturertNavn) responsOrganisasjon.getNavn()).getNavnelinje().stream().filter(it -> !it.isEmpty())
                .reduce("", (a, b) -> a + " " + b).trim());

        builder.medOrgnr(responsOrganisasjon.getOrgnummer());

        // TODO (OJR || MG): ikke helt heldig å sjekke med instanceof - alltid rom for feil og vanskelig å søke opp tilfeller, men fins annen måte her?  dårlig kontrakt
        if (responsOrganisasjon instanceof no.nav.tjeneste.virksomhet.organisasjon.v4.informasjon.Virksomhet) {
            no.nav.tjeneste.virksomhet.organisasjon.v4.informasjon.Virksomhet virksomhet = (no.nav.tjeneste.virksomhet.organisasjon.v4.informasjon.Virksomhet) responsOrganisasjon;

            List<GeografiskAdresse> adresser = virksomhet.getOrganisasjonDetaljer().getForretningsadresse();

            GeografiskAdresse qa = adresser.get(0);
            if (qa instanceof no.nav.tjeneste.virksomhet.organisasjon.v4.informasjon.SemistrukturertAdresse) {
                no.nav.tjeneste.virksomhet.organisasjon.v4.informasjon.SemistrukturertAdresse sa = (no.nav.tjeneste.virksomhet.organisasjon.v4.informasjon.SemistrukturertAdresse) qa;

                builder.medLandkode(sa.getLandkode().getKodeRef());

                List<NoekkelVerdiAdresse> al = sa.getAdresseledd();
                for (NoekkelVerdiAdresse noekkelVerdiAdresse : al) {
                    String verdi = noekkelVerdiAdresse.getVerdi();
                    if (noekkelVerdiAdresse.getNoekkel().getKodeRef().equals("adresselinje1")) {
                        builder.medAdresselinje1(verdi);
                    } else if (noekkelVerdiAdresse.getNoekkel().getKodeRef().equals("adresselinje2")) {
                        builder.medAdresselinje2(verdi);
                    } else if (noekkelVerdiAdresse.getNoekkel().getKodeRef().equals("adresselinje3")) {
                        builder.medAdresselinje3(verdi);
                    } else if (noekkelVerdiAdresse.getNoekkel().getKodeRef().equals("adresselinje4")) {
                        builder.medAdresselinje4(verdi);
                    } else if (noekkelVerdiAdresse.getNoekkel().getKodeRef().equals("postnr")) {
                        builder.medPostNr(verdi);
                    } else if (noekkelVerdiAdresse.getNoekkel().getKodeRef().equals("kommunenr")) {

                        Poststed postSted = kodeverkRepository.finn(Poststed.class, verdi);

                        builder.medPoststed(postSted.getNavn());
                    }
                }
            }
        }
        return builder.build();
    }
}

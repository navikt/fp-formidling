package no.nav.foreldrepenger.melding.brevbestiller.impl;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.PersonAdapter;
import no.nav.foreldrepenger.melding.aktør.Adresseinfo;
import no.nav.foreldrepenger.melding.aktør.Personinfo;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevbestiller.LandkodeOversetter;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentAdresse;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakBackend;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.organisasjon.Virksomhet;
import no.nav.foreldrepenger.melding.organisasjon.VirksomhetTjeneste;
import no.nav.foreldrepenger.melding.typer.AktørId;
import no.nav.foreldrepenger.melding.typer.Saksnummer;
import no.nav.foreldrepenger.melding.verge.Verge;
import no.nav.vedtak.exception.TekniskException;

@ApplicationScoped
public class DokumentFellesDataMapper {
    private static final String DOD_PERSON_STATUS = "DOD";
    private static final String DEFAULT_PERSON_STATUS = "ANNET";
    private NavKontaktKonfigurasjon navKontaktKonfigurasjon;
    private DomeneobjektProvider domeneobjektProvider;
    private PersonAdapter personAdapter;
    private VirksomhetTjeneste virksomhetTjeneste;

    public DokumentFellesDataMapper() {
        //CDI
    }

    @Inject
    public DokumentFellesDataMapper(PersonAdapter personAdapter,
                                    DomeneobjektProvider domeneobjektProvider,
                                    NavKontaktKonfigurasjon navKontaktKonfigurasjon,
                                    VirksomhetTjeneste virksomhetTjeneste) {
        this.personAdapter = personAdapter;
        this.domeneobjektProvider = domeneobjektProvider;
        this.navKontaktKonfigurasjon = navKontaktKonfigurasjon;
        this.virksomhetTjeneste = virksomhetTjeneste;
    }

    void opprettDokumentDataForBehandling(Behandling behandling, DokumentData dokumentData, DokumentHendelse dokumentHendelse) {

        final AktørId søkersAktørId = domeneobjektProvider.hentFagsakBackend(behandling).getAktørId();

        Optional<Verge> vergeOpt = domeneobjektProvider.hentVerge(behandling);
        if (vergeOpt.isEmpty()) {
            opprettDokumentDataForMottaker(behandling, dokumentData, dokumentHendelse, søkersAktørId, søkersAktørId, Optional.empty());
            return;
        }

        // til søker
        opprettDokumentDataForMottaker(behandling, dokumentData, dokumentHendelse, søkersAktørId, søkersAktørId,Optional.of( DokumentFelles.Kopi.JA)); // kopien går til søker

        Verge verge = vergeOpt.get();


        if (verge.aktoerId() != null) {
            AktørId vergesAktørId = new AktørId(verge.aktoerId());
            opprettDokumentDataForMottaker(behandling, dokumentData, dokumentHendelse, vergesAktørId, søkersAktørId, Optional.of( DokumentFelles.Kopi.NEI)); // orginalen går til verge
        } else if (verge.organisasjonsnummer() != null) {
            opprettDokumentDataForOrganisasjonsMottaker(behandling, dokumentData, dokumentHendelse, verge, søkersAktørId, Optional.of( DokumentFelles.Kopi.NEI));// orginalen går til verge
        }
    }

    private void opprettDokumentDataForOrganisasjonsMottaker(Behandling behandling,
                                                             DokumentData dokumentData,
                                                             DokumentHendelse dokumentHendelse,
                                                             Verge verge,
                                                             AktørId aktørIdBruker,
                                                             Optional<DokumentFelles.Kopi> erKopi) {
        Virksomhet virksomhet = getVirksomhet(verge);

        Personinfo personinfoBruker = personAdapter.hentBrukerForAktør(aktørIdBruker)
                .orElseThrow(() -> new TekniskException("FPFORMIDLING-109013",
                String.format("Fant ikke fødselsnummer for aktørId: %s. Kan ikke bestille dokument", aktørIdBruker)));

        String avsenderEnhet = dokumentHendelse.getBehandlendeEnhetNavn() != null ?
                dokumentHendelse.getBehandlendeEnhetNavn() : behandling.getBehandlendeEnhetNavn();

        buildDokumentFellesVirksomhet(behandling,
                dokumentData,
                personinfoBruker,
                virksomhet,
                verge.navn(),
                avsenderEnhet,
                erKopi);
    }

    private Virksomhet getVirksomhet(Verge verge) {
        return virksomhetTjeneste.getOrganisasjon(verge.organisasjonsnummer(), LandkodeOversetter::tilLandkoderToBokstav);
    }

    private void opprettDokumentDataForMottaker(Behandling behandling,
                                                DokumentData dokumentData,
                                                DokumentHendelse dokumentHendelse,
                                                AktørId aktørIdMottaker,
                                                AktørId aktørIdBruker,Optional<DokumentFelles.Kopi> erKopi) {

        Adresseinfo adresseinfo = innhentAdresseopplysningerForDokumentsending(aktørIdMottaker)
                .orElseThrow(() -> new TekniskException("FPFORMIDLING-119013", String.format("Fant ikke adresse for aktørId: %s. Kan ikke bestille dokument", aktørIdMottaker)));

        DokumentAdresse adresse = fra(adresseinfo);

        var personinfoBruker = Objects.equals(aktørIdMottaker, aktørIdBruker) ? fraAdresseinfo(aktørIdBruker, adresseinfo) :
                personAdapter.hentBrukerForAktør(aktørIdBruker).orElseThrow(() -> new TekniskException("FPFORMIDLING-109013",
                String.format("Fant ikke fødselsnummer for aktørId: %s. Kan ikke bestille dokument", aktørIdBruker)));

        String avsenderEnhet = dokumentHendelse.getBehandlendeEnhetNavn() != null ?
                dokumentHendelse.getBehandlendeEnhetNavn() : behandling.getBehandlendeEnhetNavn();

        buildDokumentFellesPerson(behandling,
                dokumentData,
                adresse,
                personinfoBruker,
                adresseinfo,
                avsenderEnhet,
                erKopi);
    }

    private void buildDokumentFellesVirksomhet(Behandling behandling,
                                     DokumentData dokumentData,
                                     Personinfo personinfoBruker,
                                     Virksomhet virksomhet,
                                     String vergeNavn,
                                     String avsenderEnhet,
                                     Optional<DokumentFelles.Kopi> erKopi) {

        FagsakBackend fagsak = domeneobjektProvider.hentFagsakBackend(behandling);

        DokumentFelles.Builder builder = DokumentFelles.builder(dokumentData)
                .medAutomatiskBehandlet(Boolean.TRUE)
                .medDokumentDato(LocalDate.now())
                .medKontaktTelefonNummer(norg2KontaktTelefonnummer(avsenderEnhet))
                .medMottakerAdresse(fra(virksomhet))
                .medNavnAvsenderEnhet(norg2NavnAvsenderEnhet(avsenderEnhet))
                .medPostadresse(norg2Postadresse())
                .medReturadresse(norg2Returadresse())
                .medMottakerId(virksomhet.getOrgnr())
                .medMottakerNavn(virksomhet.getNavn() + (vergeNavn == null || "".equals(vergeNavn) ? "" : " c/o " + vergeNavn))
                .medSaksnummer(new Saksnummer(fagsak.getSaksnummer().getVerdi()))
                .medSakspartId(personinfoBruker.getPersonIdent())
                .medSakspartNavn(personinfoBruker.getNavn())
                .medErKopi(erKopi)
                .medMottakerType(DokumentFelles.MottakerType.ORGANISASJON)
                .medSpråkkode(behandling.getSpråkkode())
                .medSakspartPersonStatus(getPersonstatusVerdi(personinfoBruker));


        if (behandling.isToTrinnsBehandling() || behandling.erKlage()) {
            builder.medAutomatiskBehandlet(Boolean.FALSE)
                    .medSignerendeSaksbehandlerNavn(behandling.getAnsvarligSaksbehandler())
                    .medSignerendeBeslutterNavn(behandling.getAnsvarligBeslutter());
        }
        builder.build();
    }

    private void buildDokumentFellesPerson(Behandling behandling,
                                     DokumentData dokumentData,
                                     DokumentAdresse adresse,
                                     Personinfo personinfoBruker,
                                     Adresseinfo adresseinfo,
                                     String avsenderEnhet,
                                     Optional<DokumentFelles.Kopi> erKopi) {

        FagsakBackend fagsak = domeneobjektProvider.hentFagsakBackend(behandling);

        DokumentFelles.Builder builder = DokumentFelles.builder(dokumentData)
                .medAutomatiskBehandlet(Boolean.TRUE)
                .medDokumentDato(LocalDate.now())
                .medKontaktTelefonNummer(norg2KontaktTelefonnummer(avsenderEnhet))
                .medMottakerAdresse(adresse)
                .medNavnAvsenderEnhet(norg2NavnAvsenderEnhet(avsenderEnhet))
                .medPostadresse(norg2Postadresse())
                .medReturadresse(norg2Returadresse())
                .medMottakerId(adresseinfo.getPersonIdent())
                .medMottakerNavn(adresseinfo.getMottakerNavn())
                .medSaksnummer(new Saksnummer(fagsak.getSaksnummer().getVerdi()))
                .medSakspartId(personinfoBruker.getPersonIdent())
                .medSakspartNavn(personinfoBruker.getNavn())
                .medErKopi(erKopi)
                .medMottakerType(DokumentFelles.MottakerType.PERSON)
                .medSpråkkode(behandling.getSpråkkode())
                .medSakspartPersonStatus(getPersonstatusVerdi(personinfoBruker));

        if (behandling.isToTrinnsBehandling() || behandling.erKlage()) {
            builder.medAutomatiskBehandlet(Boolean.FALSE)
                    .medSignerendeSaksbehandlerNavn(behandling.getAnsvarligSaksbehandler())
                    .medSignerendeBeslutterNavn(behandling.getAnsvarligBeslutter());
        }
        builder.build();
    }

//Todo når vi har koblet oss fra team CCM bør denne bruke faktiske koder og ikke gjøre om
    private String getPersonstatusVerdi(Personinfo personinfo) {
        return personinfo.isRegistrertDød() ? DOD_PERSON_STATUS : DEFAULT_PERSON_STATUS;
    }

    private Optional<Adresseinfo> innhentAdresseopplysningerForDokumentsending(AktørId aktørId) {
        return personAdapter.hentAdresseinformasjon(aktørId);
    }

    private DokumentAdresse fra(Adresseinfo adresseinfo) {
        return new DokumentAdresse.Builder()
                .medAdresselinje1(adresseinfo.getAdresselinje1())
                .medAdresselinje2(adresseinfo.getAdresselinje2())
                .medAdresselinje3(adresseinfo.getAdresselinje3())
                .medLand(adresseinfo.getLand())
                .medPostNummer(adresseinfo.getPostNr())
                .medPoststed(adresseinfo.getPoststed())
                .build();
    }

    private DokumentAdresse fra(Virksomhet virksomhet) { //NOSONAR - denne er i bruk...
        return new DokumentAdresse.Builder()
                .medAdresselinje1(virksomhet.getAdresselinje1())
                .medAdresselinje2(virksomhet.getAdresselinje2())
                .medAdresselinje3(virksomhet.getAdresselinje3())
                .medLand(virksomhet.getLandkode())
                .medPostNummer(virksomhet.getPostNr())
                .medPoststed(virksomhet.getPoststed())
                .build();
    }


    private String norg2KontaktTelefonnummer(String behandlendeEnhetNavn) {
        if (behandlendeEnhetNavn == null) {
            return navKontaktKonfigurasjon.getNorg2KontaktTelefonNummer();
        }
        return behandlendeEnhetNavn.contains(navKontaktKonfigurasjon.getBrevAvsenderKlageEnhet())
                ? navKontaktKonfigurasjon.getNorg2NavKlageinstansTelefon() : navKontaktKonfigurasjon.getNorg2KontaktTelefonNummer();
    }

    private String norg2NavnAvsenderEnhet(String behandlendeEnhetNavn) {
        if (behandlendeEnhetNavn == null) {
            return navKontaktKonfigurasjon.getBrevAvsenderEnhetNavn();
        }
        return behandlendeEnhetNavn.contains(navKontaktKonfigurasjon.getBrevAvsenderKlageEnhet())
                ? navKontaktKonfigurasjon.getBrevAvsenderKlageEnhet() : navKontaktKonfigurasjon.getBrevAvsenderEnhetNavn();
    }

    private DokumentAdresse norg2Returadresse() {
        return opprettAdresse();
    }

    private DokumentAdresse norg2Postadresse() {
        return opprettAdresse();
    }

    private DokumentAdresse opprettAdresse() {
        return new DokumentAdresse.Builder()
                .medAdresselinje1(navKontaktKonfigurasjon.getBrevReturadresseAdresselinje1())
                .medPostNummer(navKontaktKonfigurasjon.getBrevReturadressePostnummer())
                .medPoststed(navKontaktKonfigurasjon.getBrevReturadressePoststed())
                .build();
    }

    private static Personinfo fraAdresseinfo(AktørId aktørId, Adresseinfo adresseinfo) {
        return Personinfo.getbuilder(aktørId)
                .medPersonIdent(adresseinfo.getPersonIdent())
                .medNavn(adresseinfo.getMottakerNavn())
                .medRegistrertDød(adresseinfo.isRegistrertDød())
                .build();
    }
}

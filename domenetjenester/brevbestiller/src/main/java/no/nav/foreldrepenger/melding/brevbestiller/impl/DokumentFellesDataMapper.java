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
import no.nav.foreldrepenger.melding.datamapper.DokumentBestillerFeil;
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

@ApplicationScoped
public class DokumentFellesDataMapper {
    private static final String DOD_PERSON_STATUS = "DOD";
    private static final String DEFAULT_PERSON_STATUS = "ANNET";
    private NavKontaktKonfigurasjon navKontaktKonfigurasjon;
    private DomeneobjektProvider domeneobjektProvider;
    private PersonAdapter tpsTjeneste;
    private VirksomhetTjeneste virksomhetTjeneste;

    public DokumentFellesDataMapper() {
        //CDI
    }

    @Inject
    public DokumentFellesDataMapper(PersonAdapter tpsTjeneste,
                                    DomeneobjektProvider domeneobjektProvider,
                                    NavKontaktKonfigurasjon navKontaktKonfigurasjon,
                                    VirksomhetTjeneste virksomhetTjeneste) {
        this.tpsTjeneste = tpsTjeneste;
        this.domeneobjektProvider = domeneobjektProvider;
        this.navKontaktKonfigurasjon = navKontaktKonfigurasjon;
        this.virksomhetTjeneste = virksomhetTjeneste;
    }

    void opprettDokumentDataForBehandling(Behandling behandling, DokumentData dokumentData, DokumentHendelse dokumentHendelse) {

        final AktørId søkersAktørId = domeneobjektProvider.hentFagsakBackend(behandling).getAktørId();

        if (!harLenkeForVerge(behandling)) {
            opprettDokumentDataForMottaker(behandling, dokumentData, dokumentHendelse, søkersAktørId, søkersAktørId, Optional.empty());
            return;
        }

        Optional<Verge> vergeOpt = domeneobjektProvider.hentVerge(behandling);
        if (vergeOpt.isEmpty()) {
            opprettDokumentDataForMottaker(behandling, dokumentData, dokumentHendelse, søkersAktørId, søkersAktørId, Optional.empty());
            return;
        }

        // til søker
        opprettDokumentDataForMottaker(behandling, dokumentData, dokumentHendelse, søkersAktørId, søkersAktørId,Optional.of( DokumentFelles.Kopi.JA)); // kopien går til søker

        Verge verge = vergeOpt.get();


        if (verge.getAktoerId() != null) {
            AktørId vergesAktørId = new AktørId(verge.getAktoerId());
            opprettDokumentDataForMottaker(behandling, dokumentData, dokumentHendelse, vergesAktørId, søkersAktørId, Optional.of( DokumentFelles.Kopi.NEI)); // orginalen går til verge
        } else if (verge.getOrganisasjonsnummer() != null) {
            opprettDokumentDataForOrganisasjonsMottaker(behandling, dokumentData, dokumentHendelse, verge, søkersAktørId, Optional.of( DokumentFelles.Kopi.NEI));// orginalen går til verge
        }
    }

    private boolean harLenkeForVerge(Behandling behandling) {
        return behandling.getResourceLinker().stream()
                .anyMatch(link -> "soeker-verge".equals(link.getRel()));
    }


    private void opprettDokumentDataForOrganisasjonsMottaker(Behandling behandling,
                                                             DokumentData dokumentData,
                                                             DokumentHendelse dokumentHendelse,
                                                             Verge verge,
                                                             AktørId aktørIdBruker,
                                                             Optional<DokumentFelles.Kopi> erKopi) {

        Virksomhet virksomhet = getVirksomhet(verge);

        DokumentAdresse adresseVerge = fra(virksomhet);

        Personinfo personinfoBruker = tpsTjeneste.hentBrukerForAktør(aktørIdBruker)
                .orElseThrow(() -> DokumentBestillerFeil.FACTORY.fantIkkeFnrForAktørId(aktørIdBruker).toException());


        String avsenderEnhet = dokumentHendelse.getBehandlendeEnhetNavn() != null ?
                dokumentHendelse.getBehandlendeEnhetNavn() : behandling.getBehandlendeEnhetNavn();

        buildDokumentFellesVirksomhet(behandling,
                dokumentData,
                adresseVerge,
                personinfoBruker,
                virksomhet,
                verge.getNavn(),
                avsenderEnhet,
                erKopi);
    }

    private Virksomhet getVirksomhet(Verge verge) {
        return virksomhetTjeneste.getOrganisasjon(verge.getOrganisasjonsnummer(), LandkodeOversetter::tilLandkoderToBokstav);
    }

    private void opprettDokumentDataForMottaker(Behandling behandling,
                                                DokumentData dokumentData,
                                                DokumentHendelse dokumentHendelse,
                                                AktørId aktørIdMottaker,
                                                AktørId aktørIdBruker,Optional<DokumentFelles.Kopi> erKopi) {

        Adresseinfo adresseinfo = innhentAdresseopplysningerForDokumentsending(aktørIdMottaker)
                .orElseThrow(() -> DokumentBestillerFeil.FACTORY.fantIkkeAdresse(aktørIdMottaker).toException());

        DokumentAdresse adresse = fra(adresseinfo);

        var personinfoBruker = Objects.equals(aktørIdMottaker, aktørIdBruker) ? fraAdresseinfo(aktørIdBruker, adresseinfo) :
                tpsTjeneste.hentBrukerForAktør(aktørIdBruker).orElseThrow(() -> DokumentBestillerFeil.FACTORY.fantIkkeFnrForAktørId(aktørIdBruker).toException());

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
                                     DokumentAdresse adresse,
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
                .medMottakerAdresse(adresse)
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
        return tpsTjeneste.hentAdresseinformasjon(aktørId);
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

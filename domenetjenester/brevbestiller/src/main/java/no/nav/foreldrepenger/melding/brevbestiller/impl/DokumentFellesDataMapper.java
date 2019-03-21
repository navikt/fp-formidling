package no.nav.foreldrepenger.melding.brevbestiller.impl;

import java.util.Objects;
import java.util.Optional;

import no.nav.foreldrepenger.fpsak.BehandlingRestKlient;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingIdDto;
import no.nav.foreldrepenger.fpsak.dto.personopplysning.VergeDto;
import no.nav.foreldrepenger.melding.aktør.Adresseinfo;
import no.nav.foreldrepenger.melding.aktør.Personinfo;
import no.nav.foreldrepenger.melding.aktør.PersonstatusType;
import no.nav.foreldrepenger.melding.behandling.verge.BrevMottaker;
import no.nav.foreldrepenger.melding.brevbestiller.api.dto.Address;
import no.nav.foreldrepenger.melding.brevbestiller.api.dto.Behandling;
import no.nav.foreldrepenger.melding.brevbestiller.api.dto.Personopplysning;
import no.nav.foreldrepenger.melding.brevbestiller.api.dto.Verge;
import no.nav.foreldrepenger.melding.datamapper.DokumentBestillerFeil;
import no.nav.foreldrepenger.melding.datamapper.mal.DokumentType;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentAdresse;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.typer.AktørId;
import no.nav.foreldrepenger.melding.typer.PersonIdent;
import no.nav.foreldrepenger.melding.typer.Saksnummer;
import no.nav.foreldrepenger.tps.TpsTjeneste;
import no.nav.vedtak.util.FPDateUtil;

public class DokumentFellesDataMapper {
    private DokumentRepository dokumentRepository;
    private NavKontaktKonfigurasjon navKontaktKonfigurasjon;
    private BehandlingRestKlient behandlingRestKlient;
    private TpsTjeneste tpsTjeneste;

    DokumentData opprettDokumentDataForBehandling(BehandlingDto behandlingDto,
                                                  DokumentType dokumentType) {
        //Hent dokumentmal type
//        DokumentMalType dokumentMalType = dokumentRepository.hentDokumentMalType(dokumentType.getDokumentMalType());
        DokumentMalType dokumentMalType = dokumentRepository.hentDokumentMalType(DokumentMalType.UENDRETUTFALL_DOK);

        //Data for mapping
        Behandling behandling = new Behandling(behandlingDto);
        Personopplysning personopplysning = new Personopplysning(behandlingDto.getPersonopplysningDto());
        //TODO: Sjekk hvis mulighet for felere addresser
        Address address = new Address(behandlingDto.getPersonopplysningDto().getAdresser().get(0));

        final Optional<VergeDto> vergeDto = behandlingRestKlient.hentVerge(new BehandlingIdDto(behandlingDto.getId()), behandlingDto.getLinks());
        Verge verge = new Verge(vergeDto.get());

        final AktørId aktørId = new AktørId(personopplysning.getAktoerId());
        DokumentData dokumentData = DokumentData.opprettNy(dokumentMalType, behandling.getId());

        if (!vergeDto.isPresent()) {
            opprettDokumentDataForMottaker(behandling, dokumentData, aktørId, dokumentType);
        } else {
//            Verge aggregat = verge.get();
            //TODO: REST tjeneste VergeDto mangler brevmottaker
//            BrevMottaker brevMottaker = aggregat.getBrevMottaker();
            BrevMottaker brevMottaker = BrevMottaker.SØKER;
            if (BrevMottaker.SØKER.equals(brevMottaker)) {
                opprettDokumentDataForMottaker(behandling, dokumentData, aktørId, dokumentType);
            } else if (BrevMottaker.VERGE.equals(brevMottaker)) {
//                opprettDokumentDataForMottaker(behandling, dokumentData, aggregat.getAktørId(), dokumentType);
            } else if (BrevMottaker.BEGGE.equals(brevMottaker)) {
                opprettDokumentDataForMottaker(behandling, dokumentData, aktørId, dokumentType);
//                opprettDokumentDataForMottaker(behandling, dokumentData, aggregat.getAktørId(), dokumentType);
            }
        }
        return dokumentData;
    }

    private void opprettDokumentDataForMottaker(Behandling behandling,
                                                DokumentData dokumentData,
                                                AktørId aktørId,
                                                DokumentType dokumentType) {

        Adresseinfo adresseinfo = innhentAdresseopplysningerForDokumentsending(aktørId)
                .orElseThrow(() -> DokumentBestillerFeil.FACTORY.fantIkkeAdresse(aktørId).toException());

        final Personopplysning personopplysning = hentPersonopplysning();

        DokumentAdresse adresse = fra(adresseinfo);
        AktørId aktørIdBruker = new AktørId(personopplysning.getAktoerId());
        PersonIdent fnrBruker;
        String navnBruker;
        PersonstatusType personstatusBruker;

        //TODO: Hvis bruker aktørid erstattet med Verge
        if (Objects.equals(aktørId, aktørIdBruker)) {
            fnrBruker = adresseinfo.getPersonIdent();
            navnBruker = adresseinfo.getMottakerNavn();
            personstatusBruker = adresseinfo.getPersonstatus();
        } else {
            Personinfo personinfo = tpsTjeneste.hentBrukerForAktør(aktørIdBruker)
                    .orElseThrow(() -> DokumentBestillerFeil.FACTORY.fantIkkeFnrForAktørId(aktørIdBruker).toException());
            fnrBruker = personinfo.getPersonIdent();
            navnBruker = personinfo.getNavn();
            personstatusBruker = personinfo.getPersonstatus();
        }

        buildDokumentFelles(behandling,
                dokumentData,
                dokumentType,
                adresse,
                fnrBruker,
                navnBruker,
                personstatusBruker,
                adresseinfo);
    }

    private void buildDokumentFelles(Behandling behandling,
                                     DokumentData dokumentData,
                                     DokumentType dokumentType,
                                     DokumentAdresse adresse,
                                     PersonIdent fnrBruker,
                                     String navnBruker,
                                     PersonstatusType personstatusBruker,
                                     Adresseinfo adresseinfo) {

        String avsenderEnhet = behandling.getBehandlendeEnhetNavn();

        DokumentFelles.Builder builder = DokumentFelles.builder(dokumentData)
                .medAutomatiskBehandlet(Boolean.TRUE)
                .medDokumentDato(FPDateUtil.iDag())
                .medKontaktTelefonNummer(norg2KontaktTelefonnummer(avsenderEnhet))
                .medMottakerAdresse(adresse)
                .medMottakerId(adresseinfo.getPersonIdent())
                .medMottakerNavn(adresseinfo.getMottakerNavn())
                .medNavnAvsenderEnhet(norg2NavnAvsenderEnhet(avsenderEnhet))
                .medPostadresse(norg2Postadresse())
                .medReturadresse(norg2Returadresse())
                .medSaksnummer(new Saksnummer(String.valueOf(behandling.getSaksnummer())))
                .medSakspartId(fnrBruker)
                .medSakspartNavn(navnBruker)
                //TODO: Hent språk preferanse fra selvbetjeningløsning
//                .medSpråkkode(fagsak.getNavBruker().getSpråkkode())
                .medSpråkkode(Språkkode.nb)
                .medSakspartPersonStatus(dokumentType.getPersonstatusVerdi(personstatusBruker));

        if (behandling.isToTrinnsBehandling()) {
            builder
                    .medAutomatiskBehandlet(Boolean.FALSE)
                    .medSignerendeSaksbehandlerNavn(behandling.getAnsvarligSaksbehandler())
                    .medSignerendeBeslutterNavn(behandling.getAnsvarligBeslutter())
                    .medSignerendeBeslutterGeografiskEnhet("N/A");  // FIXME SOMMERFUGL Denne skal vel ikke hardkodes?
        }
        builder.build();
    }

    private Optional<Adresseinfo> innhentAdresseopplysningerForDokumentsending(AktørId aktørId) {
        Optional<Personinfo> optFnr = tpsTjeneste.hentBrukerForAktør(aktørId);
        return optFnr.map(s -> tpsTjeneste.hentAdresseinformasjon(s.getPersonIdent()));
    }

    private DokumentAdresse fra(Adresseinfo adresseinfo) {
        DokumentAdresse adresse = new DokumentAdresse.Builder()
                .medAdresselinje1(adresseinfo.getAdresselinje1())
                .medAdresselinje2(adresseinfo.getAdresselinje2())
                .medAdresselinje3(adresseinfo.getAdresselinje3())
                .medLand(adresseinfo.getLand())
                .medPostNummer(adresseinfo.getPostNr())
                .medPoststed(adresseinfo.getPoststed())
                .build();

        dokumentRepository.lagre(adresse);
        return adresse;
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

    private Behandling hentBehandling(Long behandlingId) {
        return new Behandling(null);
    }

    private Optional<Verge> hentVerge() {
        return Optional.empty();
    }

    private Personopplysning hentPersonopplysning() {
        return null;
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
}

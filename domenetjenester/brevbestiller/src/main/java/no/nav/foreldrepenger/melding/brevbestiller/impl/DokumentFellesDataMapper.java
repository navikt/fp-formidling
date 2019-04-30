package no.nav.foreldrepenger.melding.brevbestiller.impl;

import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.BehandlingRestKlient;
import no.nav.foreldrepenger.fpsak.dto.personopplysning.VergeDto;
import no.nav.foreldrepenger.melding.aktør.Adresseinfo;
import no.nav.foreldrepenger.melding.aktør.Personinfo;
import no.nav.foreldrepenger.melding.aktør.PersonstatusType;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DokumentBestillerFeil;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentAdresse;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;
import no.nav.foreldrepenger.melding.dtomapper.VergeDtoMapper;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.typer.AktørId;
import no.nav.foreldrepenger.melding.typer.PersonIdent;
import no.nav.foreldrepenger.melding.typer.Saksnummer;
import no.nav.foreldrepenger.melding.verge.Verge;
import no.nav.foreldrepenger.tps.TpsTjeneste;
import no.nav.vedtak.util.FPDateUtil;

@ApplicationScoped
public class DokumentFellesDataMapper {
    private final String DOD_PERSON_STATUS = "DOD";
    private final String DEFAULT_PERSON_STATUS = "ANNET";
    private DokumentRepository dokumentRepository;
    private NavKontaktKonfigurasjon navKontaktKonfigurasjon;
    private BehandlingRestKlient behandlingRestKlient;
    private TpsTjeneste tpsTjeneste;

    public DokumentFellesDataMapper() {
        //CDI
    }

    @Inject
    public DokumentFellesDataMapper(TpsTjeneste tpsTjeneste,
                                    DokumentRepository dokumentRepository,
                                    BehandlingRestKlient behandlingRestKlient,
                                    NavKontaktKonfigurasjon navKontaktKonfigurasjon) {
        this.tpsTjeneste = tpsTjeneste;
        this.dokumentRepository = dokumentRepository;
        this.behandlingRestKlient = behandlingRestKlient;
        this.navKontaktKonfigurasjon = navKontaktKonfigurasjon;
    }

    DokumentData opprettDokumentDataForBehandling(Behandling behandling, DokumentMalType dokumentMalType) {
        //Data for mapping
        Personinfo personinfo = behandling.getFagsak().getPersoninfo();
        DokumentData dokumentData = DokumentData.opprettNy(dokumentMalType, behandling.getId());
        final AktørId søkersAktørId = personinfo.getAktørId();

        if (!harLenkeForVerge(behandling)) {
            opprettDokumentDataForMottaker(behandling, dokumentData, søkersAktørId, søkersAktørId);
            return dokumentData;
        }

        final VergeDto vergeDto = behandlingRestKlient.hentVerge(behandling.getResourceLinker());

        Verge verge = VergeDtoMapper.mapVergeFraDto(vergeDto);
        AktørId vergesAktørId = tpsTjeneste.hentAktørForFnr(PersonIdent.fra(verge.getFnr())).orElseThrow(IllegalStateException::new);
        if (verge.brevTilBegge()) {
            opprettDokumentDataForMottaker(behandling, dokumentData, søkersAktørId, søkersAktørId);
            opprettDokumentDataForMottaker(behandling, dokumentData, vergesAktørId, søkersAktørId);
        } else if (verge.isBrevTilSøker()) {
            opprettDokumentDataForMottaker(behandling, dokumentData, søkersAktørId, søkersAktørId);
        } else if (verge.isBrevTilVerge()) {
            opprettDokumentDataForMottaker(behandling, dokumentData, vergesAktørId, søkersAktørId);
        }
        return dokumentData;
    }

    private boolean harLenkeForVerge(Behandling behandling) {
        return behandling.getResourceLinker().stream()
                .anyMatch(link -> "soeker-verge".equals(link.getRel()));
    }

    private void opprettDokumentDataForMottaker(Behandling behandling,
                                                DokumentData dokumentData,
                                                AktørId aktørId,
                                                AktørId aktørIdBruker) {

        Adresseinfo adresseinfo = innhentAdresseopplysningerForDokumentsending(aktørId)
                .orElseThrow(() -> DokumentBestillerFeil.FACTORY.fantIkkeAdresse(aktørId).toException());

        DokumentAdresse adresse = fra(adresseinfo);
        PersonIdent fnrBruker;
        String navnBruker;
        PersonstatusType personstatusBruker;

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
                adresse,
                fnrBruker,
                navnBruker,
                personstatusBruker,
                adresseinfo);
    }

    private void buildDokumentFelles(Behandling behandling,
                                     DokumentData dokumentData,
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
                .medSaksnummer(new Saksnummer(behandling.getSaksnummer().getVerdi()))
                .medSakspartId(fnrBruker)
                .medSakspartNavn(navnBruker)
                //TODO ramesh: Hent språk preferanse fra selvbetjeningløsning
//                .medSpråkkode(fagsak.getNavBruker().getSpråkkode())
                .medSpråkkode(Språkkode.nb)
                .medSakspartPersonStatus(getPersonstatusVerdi(personstatusBruker));

        if (Boolean.TRUE.equals(behandling.isToTrinnsBehandling())) {
            builder
                    .medAutomatiskBehandlet(Boolean.FALSE)
                    .medSignerendeSaksbehandlerNavn(behandling.getAnsvarligSaksbehandler())
                    .medSignerendeBeslutterNavn(behandling.getAnsvarligBeslutter())
                    .medSignerendeBeslutterGeografiskEnhet("N/A");  // FIXME SOMMERFUGL Denne skal vel ikke hardkodes?
        }
        builder.build();
    }

    private String getPersonstatusVerdi(PersonstatusType personstatus) {
        return PersonstatusType.erDød(personstatus) ? DOD_PERSON_STATUS : DEFAULT_PERSON_STATUS;
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

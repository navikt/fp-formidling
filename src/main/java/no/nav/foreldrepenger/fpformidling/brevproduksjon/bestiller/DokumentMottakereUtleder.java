package no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller;

import java.time.LocalDate;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.domene.aktør.Personinfo;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMottakere;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.BrevGrunnlag;
import no.nav.foreldrepenger.fpformidling.integrasjon.organisasjon.Virksomhet;
import no.nav.foreldrepenger.fpformidling.integrasjon.organisasjon.VirksomhetTjeneste;
import no.nav.foreldrepenger.fpformidling.integrasjon.pdl.PersonAdapter;
import no.nav.foreldrepenger.fpformidling.typer.AktørId;
import no.nav.foreldrepenger.fpformidling.typer.Saksnummer;
import no.nav.vedtak.exception.TekniskException;

@ApplicationScoped
public class DokumentMottakereUtleder {
    private static final String FANT_IKKE_BRUKER = "Fant ikke bruker for aktørId: %s. Kan ikke bestille dokument";

    private PersonAdapter personAdapter;
    private VirksomhetTjeneste virksomhetTjeneste;

    public DokumentMottakereUtleder() {
        //CDI
    }

    @Inject
    public DokumentMottakereUtleder(PersonAdapter personAdapter, VirksomhetTjeneste virksomhetTjeneste) {
        this.personAdapter = personAdapter;
        this.virksomhetTjeneste = virksomhetTjeneste;
    }

    DokumentMottakere utledDokumentMottakereForBehandling(BrevGrunnlag behandling) {

        var søkersAktørId = new AktørId(behandling.aktørId());
        var ytelseType = mapFagsakYtelseType(behandling.fagsakYtelseType());

        var brevDato = LocalDate.now();

        var gyldigVerge = Optional.ofNullable(behandling.verge())
            .filter(verge -> brevDato.isAfter(verge.gyldigFom()) && (brevDato.isBefore(verge.gyldigTom()) || brevDato.equals(verge.gyldigTom())))
            .orElse(null);

        if (gyldigVerge != null) {
            // kopien går til søker
            var søker = opprettDokumentFellesForMottaker(behandling, ytelseType, søkersAktørId, søkersAktørId, DokumentFelles.Kopi.JA);
            // orginalen går til verge
            var verge = opprettDokumentFellesTilVerge(behandling, gyldigVerge, ytelseType, søkersAktørId);
            return new DokumentMottakere(søker, verge);
        }
        //ingen verge
        return new DokumentMottakere(opprettDokumentFellesForMottaker(behandling, ytelseType, søkersAktørId, søkersAktørId), null);
    }

    private DokumentFelles opprettDokumentFellesTilVerge(BrevGrunnlag behandling, BrevGrunnlag.Verge gyldigVerge, FagsakYtelseType ytelseType, AktørId søkersAktørId) {
        if (gyldigVerge.aktørId() != null) {
            var vergesAktørId = new AktørId(gyldigVerge.aktørId());
            return opprettDokumentFellesForMottaker(behandling, ytelseType, vergesAktørId, søkersAktørId, DokumentFelles.Kopi.NEI);
        } else if (gyldigVerge.organisasjonsnummer() != null) {
            return opprettDokumentFellesForOrganisasjonsMottaker(behandling, ytelseType, gyldigVerge, søkersAktørId, DokumentFelles.Kopi.NEI);
        } else {
            throw new IllegalStateException("Verken person eller org er oppgitt som verge for behandling " + behandling.uuid());
        }
    }

    private DokumentFelles opprettDokumentFellesForOrganisasjonsMottaker(BrevGrunnlag behandling,
                                                                         FagsakYtelseType ytelseType,
                                                                         BrevGrunnlag.Verge verge,
                                                                         AktørId aktørIdBruker,
                                                                         DokumentFelles.Kopi erKopi) {
        var virksomhet = virksomhetTjeneste.getOrganisasjon(verge.organisasjonsnummer());

        var personinfoBruker = personAdapter.hentBrukerForAktør(ytelseType, aktørIdBruker)
            .orElseThrow(() -> new TekniskException("FPFORMIDLING-109013",
                String.format("Fant ikke fødselsnummer for aktørId: %s. Kan ikke bestille dokument", aktørIdBruker)));

        return buildDokumentFellesVirksomhet(behandling, personinfoBruker, virksomhet, verge.navn(), erKopi);
    }

    private DokumentFelles opprettDokumentFellesForMottaker(BrevGrunnlag behandling,
                                                            FagsakYtelseType ytelseType,
                                                            AktørId aktørIdMottaker,
                                                            AktørId aktørIdBruker) {
        return opprettDokumentFellesForMottaker(behandling, ytelseType, aktørIdMottaker, aktørIdBruker, null);
    }

    private DokumentFelles opprettDokumentFellesForMottaker(BrevGrunnlag behandling,
                                                            FagsakYtelseType ytelseType,
                                                            AktørId aktørIdMottaker,
                                                            AktørId aktørIdBruker,
                                                            DokumentFelles.Kopi erKopi) {

        var personinfoMottaker = personAdapter.hentBrukerForAktør(ytelseType, aktørIdMottaker)
            .orElseThrow(() -> new TekniskException("FPFORMIDLING-119013", String.format(FANT_IKKE_BRUKER, aktørIdMottaker)));
        var personinfoBruker = personAdapter.hentBrukerForAktør(ytelseType, aktørIdBruker)
            .orElseThrow(() -> new TekniskException("FPFORMIDLING-109013", String.format(FANT_IKKE_BRUKER, aktørIdBruker)));

        return buildDokumentFellesPerson(behandling, personinfoBruker, personinfoMottaker, erKopi);
    }

    private DokumentFelles buildDokumentFellesVirksomhet(BrevGrunnlag behandling,
                                                         Personinfo personinfoBruker,
                                                         Virksomhet virksomhet,
                                                         String vergeNavn,
                                                         DokumentFelles.Kopi erKopi) {
        return DokumentFelles.builder()
            .medAutomatiskBehandlet(Boolean.TRUE)
            .medDokumentDato(LocalDate.now())
            .medMottakerId(virksomhet.getOrgnr())
            .medMottakerNavn(virksomhet.getNavn() + (vergeNavn == null || vergeNavn.isEmpty() ? "" : " c/o " + vergeNavn))
            .medSaksnummer(new Saksnummer(behandling.saksnummer()))
            .medSakspartId(personinfoBruker.getPersonIdent())
            .medSakspartNavn(personinfoBruker.getNavn())
            .medErKopi(erKopi)
            .medMottakerType(DokumentFelles.MottakerType.ORGANISASJON)
            .medSpråkkode(mapSpråkkode(behandling.språkkode()))
            .medSakspartPersonStatus(getPersonstatusVerdi(personinfoBruker))
            .medAutomatiskBehandlet(behandling.automatiskBehandlet()).build();
    }

    private DokumentFelles buildDokumentFellesPerson(BrevGrunnlag behandling,
                                                     Personinfo personinfoBruker,
                                                     Personinfo personinfoMottaker,
                                                     DokumentFelles.Kopi erKopi) {
        //TODO TFP-6069 slå sammen med metoden over? Felles basic builder?
        return DokumentFelles.builder()
            .medAutomatiskBehandlet(Boolean.TRUE)
            .medDokumentDato(LocalDate.now())
            .medMottakerId(personinfoMottaker.getPersonIdent())
            .medMottakerNavn(personinfoMottaker.getNavn())
            .medSaksnummer(new Saksnummer(behandling.saksnummer()))
            .medSakspartId(personinfoBruker.getPersonIdent())
            .medSakspartNavn(personinfoBruker.getNavn())
            .medErKopi(erKopi)
            .medMottakerType(DokumentFelles.MottakerType.PERSON)
            .medSpråkkode(mapSpråkkode(behandling.språkkode()))
            .medYtelseType(mapFagsakYtelseType(behandling.fagsakYtelseType()))
            .medSakspartPersonStatus(getPersonstatusVerdi(personinfoBruker))
            .medAutomatiskBehandlet(behandling.automatiskBehandlet()).build();
    }

    private static FagsakYtelseType mapFagsakYtelseType(BrevGrunnlag.FagsakYtelseType fagsakYtelseType) {
        return switch (fagsakYtelseType) {
            case ENGANGSTØNAD -> FagsakYtelseType.ENGANGSTØNAD;
            case FORELDREPENGER -> FagsakYtelseType.FORELDREPENGER;
            case SVANGERSKAPSPENGER -> FagsakYtelseType.SVANGERSKAPSPENGER;
        };
    }

    private static Språkkode mapSpråkkode(BrevGrunnlag.Språkkode språkkode) {
        return switch (språkkode) {
            case BOKMÅL -> Språkkode.NB;
            case NYNORSK -> Språkkode.NN;
            case ENGELSK -> Språkkode.EN;
        };
    }


    private DokumentFelles.PersonStatus getPersonstatusVerdi(Personinfo personinfo) {
        return personinfo.isRegistrertDød() ? DokumentFelles.PersonStatus.DOD : DokumentFelles.PersonStatus.ANNET;
    }
}

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
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
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

    DokumentMottakere utledDokumentMottakereForBehandling(BrevGrunnlagDto behandling) {

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

    private DokumentFelles opprettDokumentFellesTilVerge(BrevGrunnlagDto behandling, BrevGrunnlagDto.Verge gyldigVerge, FagsakYtelseType ytelseType, AktørId søkersAktørId) {
        if (gyldigVerge.aktørId() != null) {
            var vergesAktørId = new AktørId(gyldigVerge.aktørId());
            return opprettDokumentFellesForMottaker(behandling, ytelseType, vergesAktørId, søkersAktørId, DokumentFelles.Kopi.NEI);
        } else if (gyldigVerge.organisasjonsnummer() != null) {
            return opprettDokumentFellesForOrganisasjonsMottaker(behandling, ytelseType, gyldigVerge, søkersAktørId, DokumentFelles.Kopi.NEI);
        } else {
            throw new IllegalStateException("Verken person eller org er oppgitt som verge for behandling " + behandling.uuid());
        }
    }

    private DokumentFelles opprettDokumentFellesForOrganisasjonsMottaker(BrevGrunnlagDto behandling,
                                                                         FagsakYtelseType ytelseType,
                                                                         BrevGrunnlagDto.Verge verge,
                                                                         AktørId aktørIdBruker,
                                                                         DokumentFelles.Kopi erKopi) {
        var virksomhet = virksomhetTjeneste.getOrganisasjon(verge.organisasjonsnummer());

        var personinfoBruker = personAdapter.hentBrukerForAktør(ytelseType, aktørIdBruker)
            .orElseThrow(() -> new TekniskException("FPFORMIDLING-109013",
                String.format("Fant ikke fødselsnummer for aktørId: %s. Kan ikke bestille dokument", aktørIdBruker)));

        return buildDokumentFellesVirksomhet(behandling, personinfoBruker, virksomhet, verge.navn(), erKopi);
    }

    private DokumentFelles opprettDokumentFellesForMottaker(BrevGrunnlagDto behandling,
                                                            FagsakYtelseType ytelseType,
                                                            AktørId aktørIdMottaker,
                                                            AktørId aktørIdBruker) {
        return opprettDokumentFellesForMottaker(behandling, ytelseType, aktørIdMottaker, aktørIdBruker, null);
    }

    private DokumentFelles opprettDokumentFellesForMottaker(BrevGrunnlagDto behandling,
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

    private DokumentFelles buildDokumentFellesVirksomhet(BrevGrunnlagDto behandling,
                                                         Personinfo personinfoBruker,
                                                         Virksomhet virksomhet,
                                                         String vergeNavn,
                                                         DokumentFelles.Kopi erKopi) {
        return fellesBuilder(behandling, personinfoBruker, erKopi)
            .medMottakerType(DokumentFelles.MottakerType.ORGANISASJON)
            .medMottakerId(virksomhet.getOrgnr())
            .medMottakerNavn(virksomhet.getNavn() + (vergeNavn == null || vergeNavn.isEmpty() ? "" : " c/o " + vergeNavn))
            .build();
    }

    private DokumentFelles buildDokumentFellesPerson(BrevGrunnlagDto behandling,
                                                     Personinfo personinfoBruker,
                                                     Personinfo personinfoMottaker,
                                                     DokumentFelles.Kopi erKopi) {
        return fellesBuilder(behandling, personinfoBruker, erKopi)
            .medMottakerType(DokumentFelles.MottakerType.PERSON)
            .medMottakerId(personinfoMottaker.getPersonIdent())
            .medMottakerNavn(personinfoMottaker.getNavn())
            .build();
    }

    private DokumentFelles.Builder fellesBuilder(BrevGrunnlagDto behandling, Personinfo personinfoBruker, DokumentFelles.Kopi erKopi) {
        return DokumentFelles.builder()
            .medYtelseType(mapFagsakYtelseType(behandling.fagsakYtelseType()))
            .medDokumentDato(LocalDate.now())
            .medSaksnummer(new Saksnummer(behandling.saksnummer()))
            .medSakspartId(personinfoBruker.getPersonIdent())
            .medSakspartNavn(personinfoBruker.getNavn())
            .medErKopi(erKopi)
            .medSpråkkode(mapSpråkkode(behandling.språkkode()))
            .medSakspartPersonStatus(getPersonstatusVerdi(personinfoBruker))
            .medAutomatiskBehandlet(behandling.automatiskBehandlet());
    }

    private static FagsakYtelseType mapFagsakYtelseType(BrevGrunnlagDto.FagsakYtelseType fagsakYtelseType) {
        return switch (fagsakYtelseType) {
            case ENGANGSTØNAD -> FagsakYtelseType.ENGANGSTØNAD;
            case FORELDREPENGER -> FagsakYtelseType.FORELDREPENGER;
            case SVANGERSKAPSPENGER -> FagsakYtelseType.SVANGERSKAPSPENGER;
        };
    }

    private static Språkkode mapSpråkkode(BrevGrunnlagDto.Språkkode språkkode) {
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

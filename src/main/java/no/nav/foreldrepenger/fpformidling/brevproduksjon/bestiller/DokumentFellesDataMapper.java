package no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller;

import java.time.LocalDate;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.domene.aktør.Personinfo;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentData;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.verge.Verge;
import no.nav.foreldrepenger.fpformidling.integrasjon.organisasjon.Virksomhet;
import no.nav.foreldrepenger.fpformidling.integrasjon.organisasjon.VirksomhetTjeneste;
import no.nav.foreldrepenger.fpformidling.integrasjon.pdl.PersonAdapter;
import no.nav.foreldrepenger.fpformidling.typer.AktørId;
import no.nav.foreldrepenger.fpformidling.typer.Saksnummer;
import no.nav.vedtak.exception.TekniskException;

@ApplicationScoped
public class DokumentFellesDataMapper {
    private static final String FANT_IKKE_BRUKER = "Fant ikke bruker for aktørId: %s. Kan ikke bestille dokument";

    private DomeneobjektProvider domeneobjektProvider;
    private PersonAdapter personAdapter;
    private VirksomhetTjeneste virksomhetTjeneste;

    public DokumentFellesDataMapper() {
        //CDI
    }

    @Inject
    public DokumentFellesDataMapper(PersonAdapter personAdapter, DomeneobjektProvider domeneobjektProvider, VirksomhetTjeneste virksomhetTjeneste) {
        this.personAdapter = personAdapter;
        this.domeneobjektProvider = domeneobjektProvider;
        this.virksomhetTjeneste = virksomhetTjeneste;
    }

    void opprettDokumentDataForBehandling(Behandling behandling, DokumentData dokumentData) {

        final var fagsak = domeneobjektProvider.hentFagsakBackend(behandling);
        final var søkersAktørId = fagsak.getAktørId();
        final var ytelseType = fagsak.getYtelseType();

        var brevDato = LocalDate.now();

        var gyldigVerge = domeneobjektProvider.hentVerge(behandling)
            .filter(verge -> brevDato.isAfter(verge.gyldigFom()) && (brevDato.isBefore(verge.gyldigTom()) || brevDato.equals(verge.gyldigTom())))
            .orElse(null);

        if (gyldigVerge != null) {
            // kopien går til søker
            opprettDokumentDataForMottaker(behandling, dokumentData, ytelseType, søkersAktørId, søkersAktørId, DokumentFelles.Kopi.JA);

            // orginalen går til verge
            if (gyldigVerge.aktoerId() != null) {
                var vergesAktørId = new AktørId(gyldigVerge.aktoerId());
                opprettDokumentDataForMottaker(behandling, dokumentData, ytelseType, vergesAktørId, søkersAktørId, DokumentFelles.Kopi.NEI);
            } else if (gyldigVerge.organisasjonsnummer() != null) {
                opprettDokumentDataForOrganisasjonsMottaker(behandling, dokumentData, ytelseType, gyldigVerge, søkersAktørId, DokumentFelles.Kopi.NEI);
            }
        } else {
            //ingen verge
            opprettDokumentDataForMottaker(behandling, dokumentData, ytelseType, søkersAktørId, søkersAktørId);
        }
    }

    private void opprettDokumentDataForOrganisasjonsMottaker(Behandling behandling,
                                                             DokumentData dokumentData,
                                                             FagsakYtelseType ytelseType,
                                                             Verge verge,
                                                             AktørId aktørIdBruker,
                                                             DokumentFelles.Kopi erKopi) {
        var virksomhet = getVirksomhet(verge);

        var personinfoBruker = personAdapter.hentBrukerForAktør(ytelseType, aktørIdBruker)
            .orElseThrow(() -> new TekniskException("FPFORMIDLING-109013",
                String.format("Fant ikke fødselsnummer for aktørId: %s. Kan ikke bestille dokument", aktørIdBruker)));

        buildDokumentFellesVirksomhet(behandling, dokumentData, personinfoBruker, virksomhet, verge.navn(), erKopi);
    }

    private Virksomhet getVirksomhet(Verge verge) {
        return virksomhetTjeneste.getOrganisasjon(verge.organisasjonsnummer());
    }

    private void opprettDokumentDataForMottaker(Behandling behandling, DokumentData dokumentData, FagsakYtelseType ytelseType,
                                                AktørId aktørIdMottaker, AktørId aktørIdBruker) {
        opprettDokumentDataForMottaker(behandling, dokumentData, ytelseType, aktørIdMottaker, aktørIdBruker, null);
    }

    private void opprettDokumentDataForMottaker(Behandling behandling,
                                                DokumentData dokumentData,
                                                FagsakYtelseType ytelseType,
                                                AktørId aktørIdMottaker,
                                                AktørId aktørIdBruker,
                                                DokumentFelles.Kopi erKopi) {

        var personinfoMottaker = personAdapter.hentBrukerForAktør(ytelseType, aktørIdMottaker)
            .orElseThrow(() -> new TekniskException("FPFORMIDLING-119013", String.format(FANT_IKKE_BRUKER, aktørIdMottaker)));
        var personinfoBruker = personAdapter.hentBrukerForAktør(ytelseType, aktørIdBruker)
            .orElseThrow(() -> new TekniskException("FPFORMIDLING-109013", String.format(FANT_IKKE_BRUKER, aktørIdBruker)));

        buildDokumentFellesPerson(behandling, dokumentData, personinfoBruker, personinfoMottaker, erKopi);
    }

    private void buildDokumentFellesVirksomhet(Behandling behandling,
                                               DokumentData dokumentData,
                                               Personinfo personinfoBruker,
                                               Virksomhet virksomhet,
                                               String vergeNavn,
                                               DokumentFelles.Kopi erKopi) {

        var fagsak = domeneobjektProvider.hentFagsakBackend(behandling);

        var builder = DokumentFelles.builder(dokumentData)
            .medAutomatiskBehandlet(Boolean.TRUE)
            .medDokumentDato(LocalDate.now())
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
            builder.medAutomatiskBehandlet(Boolean.FALSE);
        }
        builder.build();
    }

    private void buildDokumentFellesPerson(Behandling behandling,
                                           DokumentData dokumentData,
                                           Personinfo personinfoBruker,
                                           Personinfo personinfoMottaker,
                                           DokumentFelles.Kopi erKopi) {

        var fagsak = domeneobjektProvider.hentFagsakBackend(behandling);

        var builder = DokumentFelles.builder(dokumentData)
            .medAutomatiskBehandlet(Boolean.TRUE)
            .medDokumentDato(LocalDate.now())
            .medMottakerId(personinfoMottaker.getPersonIdent())
            .medMottakerNavn(personinfoMottaker.getNavn())
            .medSaksnummer(new Saksnummer(fagsak.getSaksnummer().getVerdi()))
            .medSakspartId(personinfoBruker.getPersonIdent())
            .medSakspartNavn(personinfoBruker.getNavn())
            .medErKopi(erKopi)
            .medMottakerType(DokumentFelles.MottakerType.PERSON)
            .medSpråkkode(behandling.getSpråkkode())
            .medSakspartPersonStatus(getPersonstatusVerdi(personinfoBruker));

        if (behandling.isToTrinnsBehandling() || behandling.erKlage()) {
            builder.medAutomatiskBehandlet(Boolean.FALSE);
        }
        builder.build();
    }


    private DokumentFelles.PersonStatus getPersonstatusVerdi(Personinfo personinfo) {
        return personinfo.isRegistrertDød() ? DokumentFelles.PersonStatus.DOD : DokumentFelles.PersonStatus.ANNET;
    }
}

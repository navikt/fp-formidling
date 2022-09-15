package no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller;

import java.time.LocalDate;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpformidling.aktør.Personinfo;
import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentData;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.fagsak.FagsakBackend;
import no.nav.foreldrepenger.fpformidling.integrasjon.organisasjon.Virksomhet;
import no.nav.foreldrepenger.fpformidling.integrasjon.organisasjon.VirksomhetTjeneste;
import no.nav.foreldrepenger.fpformidling.integrasjon.pdl.PersonAdapter;
import no.nav.foreldrepenger.fpformidling.typer.AktørId;
import no.nav.foreldrepenger.fpformidling.typer.Saksnummer;
import no.nav.foreldrepenger.fpformidling.verge.Verge;
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
    public DokumentFellesDataMapper(PersonAdapter personAdapter,
                                    DomeneobjektProvider domeneobjektProvider,
                                    VirksomhetTjeneste virksomhetTjeneste) {
        this.personAdapter = personAdapter;
        this.domeneobjektProvider = domeneobjektProvider;
        this.virksomhetTjeneste = virksomhetTjeneste;
    }

    void opprettDokumentDataForBehandling(Behandling behandling, DokumentData dokumentData) {

        final AktørId søkersAktørId = domeneobjektProvider.hentFagsakBackend(behandling).getAktørId();

        Optional<Verge> vergeOpt = domeneobjektProvider.hentVerge(behandling);
        if (vergeOpt.isEmpty()) {
            opprettDokumentDataForMottaker(behandling, dokumentData, søkersAktørId, søkersAktørId, Optional.empty());
            return;
        }

        // kopien går til søker
        opprettDokumentDataForMottaker(behandling, dokumentData, søkersAktørId, søkersAktørId,Optional.of( DokumentFelles.Kopi.JA));

        // orginalen går til verge
        Verge verge = vergeOpt.get();
        if (verge.aktoerId() != null) {
            AktørId vergesAktørId = new AktørId(verge.aktoerId());
            opprettDokumentDataForMottaker(behandling, dokumentData, vergesAktørId, søkersAktørId, Optional.of( DokumentFelles.Kopi.NEI));
        } else if (verge.organisasjonsnummer() != null) {
            opprettDokumentDataForOrganisasjonsMottaker(behandling, dokumentData, verge, søkersAktørId, Optional.of( DokumentFelles.Kopi.NEI));
        }
    }

    private void opprettDokumentDataForOrganisasjonsMottaker(Behandling behandling,
                                                             DokumentData dokumentData,
                                                             Verge verge,
                                                             AktørId aktørIdBruker,
                                                             Optional<DokumentFelles.Kopi> erKopi) {
        Virksomhet virksomhet = getVirksomhet(verge);

        Personinfo personinfoBruker = personAdapter.hentBrukerForAktør(aktørIdBruker)
                .orElseThrow(() -> new TekniskException("FPFORMIDLING-109013",
                String.format("Fant ikke fødselsnummer for aktørId: %s. Kan ikke bestille dokument", aktørIdBruker)));

        buildDokumentFellesVirksomhet(behandling,
                dokumentData,
                personinfoBruker,
                virksomhet,
                verge.navn(),
                erKopi);
    }

    private Virksomhet getVirksomhet(Verge verge) {
        return virksomhetTjeneste.getOrganisasjon(verge.organisasjonsnummer());
    }

    private void opprettDokumentDataForMottaker(Behandling behandling,
                                                DokumentData dokumentData,
                                                AktørId aktørIdMottaker,
                                                AktørId aktørIdBruker,
                                                Optional<DokumentFelles.Kopi> erKopi) {

        var personinfoMottaker = personAdapter.hentBrukerForAktør(aktørIdMottaker)
                .orElseThrow(() -> new TekniskException("FPFORMIDLING-119013", String.format(FANT_IKKE_BRUKER, aktørIdMottaker)));
        var personinfoBruker = personAdapter.hentBrukerForAktør(aktørIdBruker)
                .orElseThrow(() -> new TekniskException("FPFORMIDLING-109013", String.format(FANT_IKKE_BRUKER, aktørIdBruker)));

        buildDokumentFellesPerson(behandling,
                dokumentData,
                personinfoBruker,
                personinfoMottaker,
                erKopi);
    }

    private void buildDokumentFellesVirksomhet(Behandling behandling,
                                               DokumentData dokumentData,
                                               Personinfo personinfoBruker,
                                               Virksomhet virksomhet,
                                               String vergeNavn,
                                               Optional<DokumentFelles.Kopi> erKopi) {

        FagsakBackend fagsak = domeneobjektProvider.hentFagsakBackend(behandling);

        DokumentFelles.Builder builder = DokumentFelles.builder(dokumentData)
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
            builder.medAutomatiskBehandlet(Boolean.FALSE)
                    .medSignerendeSaksbehandlerNavn(behandling.getAnsvarligSaksbehandler())
                    .medSignerendeBeslutterNavn(behandling.getAnsvarligBeslutter());
        }
        builder.build();
    }

    private void buildDokumentFellesPerson(Behandling behandling,
                                           DokumentData dokumentData,
                                           Personinfo personinfoBruker,
                                           Personinfo personinfoMottaker,
                                           Optional<DokumentFelles.Kopi> erKopi) {

        FagsakBackend fagsak = domeneobjektProvider.hentFagsakBackend(behandling);

        DokumentFelles.Builder builder = DokumentFelles.builder(dokumentData)
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
            builder.medAutomatiskBehandlet(Boolean.FALSE)
                    .medSignerendeSaksbehandlerNavn(behandling.getAnsvarligSaksbehandler())
                    .medSignerendeBeslutterNavn(behandling.getAnsvarligBeslutter());
        }
        builder.build();
    }

//Todo når vi har koblet oss fra team CCM bør denne bruke faktiske koder og ikke gjøre om
    private DokumentFelles.PersonStatus getPersonstatusVerdi(Personinfo personinfo) {
        return personinfo.isRegistrertDød() ? DokumentFelles.PersonStatus.DOD : DokumentFelles.PersonStatus.ANNET;
    }
}

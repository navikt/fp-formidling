package no.nav.foreldrepenger.melding.brevbestiller;

import no.nav.foreldrepenger.melding.datamapper.DokumentBestillerTjenesteUtil;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.Fagsystem;
import no.nav.foreldrepenger.melding.typer.Saksnummer;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.informasjon.Adresse;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.informasjon.Dokumentbestillingsinformasjon;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.informasjon.Fagomraader;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.informasjon.Fagsystemer;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.informasjon.Organisasjon;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.informasjon.Person;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.informasjon.UtenlandskPostadresse;

public class DokumentbestillingMapper {
    private static final String FAGOMRÅDE_KODE = "FOR";
    private static final String JOURNALFØRENDE_ENHET_KODE = "9999";
    private static final String UKJENT_ADRESSE = "Ukjent adresse";

    public static Dokumentbestillingsinformasjon mapFraBehandling(DokumentMalType dokumentMal, DokumentFelles dokumentFelles, Saksnummer saksnummer, boolean harVedlegg) {
        final Dokumentbestillingsinformasjon dokumentbestillingsinformasjon = new Dokumentbestillingsinformasjon();
        dokumentbestillingsinformasjon.setDokumenttypeId(dokumentMal.getDokSysKode().getKode());
        Fagsystemer vlfp = new Fagsystemer();
        vlfp.setKodeRef(Fagsystem.FPSAK.getOffisiellKode());
        vlfp.setValue(Fagsystem.FPSAK.getOffisiellKode());
        dokumentbestillingsinformasjon.setBestillendeFagsystem(vlfp);
        setPostadresse(dokumentFelles, dokumentbestillingsinformasjon);
        Person bruker = new Person();
        bruker.setIdent(dokumentFelles.getSakspartId());
        bruker.setNavn(dokumentFelles.getSakspartNavn());
        dokumentbestillingsinformasjon.setBruker(bruker);
        Fagomraader dokumenttilhørendeFagområde = new Fagomraader();
        dokumenttilhørendeFagområde.setKodeRef(FAGOMRÅDE_KODE);
        dokumenttilhørendeFagområde.setValue(FAGOMRÅDE_KODE);
        dokumentbestillingsinformasjon.setDokumenttilhoerendeFagomraade(dokumenttilhørendeFagområde);
        dokumentbestillingsinformasjon.setFerdigstillForsendelse(!harVedlegg);
        dokumentbestillingsinformasjon.setInkludererEksterneVedlegg(harVedlegg);

        // TODO: RS - Dette felt er ikke brukt, venter at team dokument for å selette dette felt.
        // (bts) bruke 9999 hvis automatisk, ellers pålogget saksbehandlers enhetskode
        dokumentbestillingsinformasjon.setJournalfoerendeEnhet(JOURNALFØRENDE_ENHET_KODE);

        dokumentbestillingsinformasjon.setJournalsakId(saksnummer.getVerdi());


        if(dokumentFelles.getMottakerType()==DokumentFelles.MottakerType.PERSON) {
            Person mottaker = new Person();
            mottaker.setIdent(dokumentFelles.getMottakerId());
            mottaker.setNavn(dokumentFelles.getMottakerNavn());
            dokumentbestillingsinformasjon.setMottaker(mottaker);

        }
        else{
            Organisasjon mottaker = new Organisasjon();
            mottaker.setOrgnummer(dokumentFelles.getMottakerId());
            mottaker.setNavn(dokumentFelles.getMottakerNavn());
            dokumentbestillingsinformasjon.setMottaker(mottaker);
        }

        dokumentbestillingsinformasjon.setSaksbehandlernavn(dokumentFelles.getSignerendeBeslutterNavn() == null ? "Vedtaksløsning Prosess" : dokumentFelles.getSignerendeBeslutterNavn());
        Fagsystemer gsak = new Fagsystemer();
        gsak.setKodeRef(Fagsystem.GOSYS.getOffisiellKode());
        gsak.setValue(Fagsystem.GOSYS.getOffisiellKode());
        dokumentbestillingsinformasjon.setSakstilhoerendeFagsystem(gsak);
        return dokumentbestillingsinformasjon;
    }

    private static void setPostadresse(DokumentFelles dokumentFelles, Dokumentbestillingsinformasjon dokumentbestillingsinformasjon) {
        Adresse adresse;
        if (DokumentBestillerTjenesteUtil.erNorskAdresse(dokumentFelles.getMottakerAdresse())) {
            adresse = DokumentBestillerTjenesteUtil.lagNorskPostadresse(dokumentFelles);
        } else {
            adresse = lagUtenlandskPostadresse(dokumentFelles);
        }
        dokumentbestillingsinformasjon.setAdresse(adresse);
    }

    private static UtenlandskPostadresse lagUtenlandskPostadresse(DokumentFelles dokumentFelles) {
        UtenlandskPostadresse adresse = new UtenlandskPostadresse();
        adresse.setAdresselinje1(dokumentFelles.getMottakerAdresse().getAdresselinje1() == null ? UKJENT_ADRESSE : dokumentFelles.getMottakerAdresse().getAdresselinje1());
        adresse.setAdresselinje2(dokumentFelles.getMottakerAdresse().getAdresselinje2());
        adresse.setAdresselinje3(dokumentFelles.getMottakerAdresse().getAdresselinje3());
        no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.informasjon.Landkoder landkode = new no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.informasjon.Landkoder();
        landkode.setValue(LandkodeOversetter.tilLandkoderToBokstav(dokumentFelles.getMottakerAdresse().getLand()));
        adresse.setLand(landkode);
        return adresse;
    }
}

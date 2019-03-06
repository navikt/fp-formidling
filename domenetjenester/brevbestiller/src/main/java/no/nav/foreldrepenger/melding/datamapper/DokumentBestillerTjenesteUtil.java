package no.nav.foreldrepenger.melding.datamapper;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentAdresse;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.AvsenderAdresseType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.IdKodeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.KontaktInformasjonType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.SakspartType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.SignerendeBeslutterType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.SignerendeSaksbehandlerType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.SpraakkodeType;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.informasjon.NorskPostadresse;

public class DokumentBestillerTjenesteUtil {

    public static final String LANDSKODE_NORGE = "NOR";

    private DokumentBestillerTjenesteUtil() {

    }

    static SignerendeSaksbehandlerType lageSignerendeSaksbehandlerType(DokumentFelles dokumentFelles) {
        SignerendeSaksbehandlerType signerendeSaksbehandlerType = new SignerendeSaksbehandlerType();
        signerendeSaksbehandlerType.setSignerendeSaksbehandlerNavn(dokumentFelles.getSignerendeSaksbehandlerNavn());
        return signerendeSaksbehandlerType;
    }

    static SpraakkodeType mapSpråkkode(Språkkode språkkode) {
        return SpraakkodeType.fromValue(språkkode.getKode());
    }

    static SignerendeBeslutterType lageSignerendeBeslutterType(DokumentFelles dokumentFelles) {
        SignerendeBeslutterType signerendeBeslutterType = new SignerendeBeslutterType();
        signerendeBeslutterType.setSignerendeBeslutterNavn(dokumentFelles.getSignerendeBeslutterNavn());
        signerendeBeslutterType.setGeografiskEnhet(dokumentFelles.getSignerendeBeslutterGeografiskEnhet());
        return signerendeBeslutterType;
    }

    static SakspartType lageSakspartType(DokumentFelles dokumentFelles) {
        SakspartType sakspartType = new SakspartType();
        sakspartType.setSakspartId(dokumentFelles.getSakspartId());
        sakspartType.setSakspartTypeKode(IdKodeType.PERSON);
        sakspartType.setSakspartNavn(dokumentFelles.getSakspartNavn());
        return sakspartType;
    }

    static KontaktInformasjonType lageKontaktInformasjonType(DokumentFelles dokumentFelles) {
        KontaktInformasjonType kontaktInformasjonType = new KontaktInformasjonType();
        kontaktInformasjonType.setKontaktTelefonnummer(dokumentFelles.getKontaktTlf());
        //Adressen skal benyttes ved tilfeller der dokumenter må sendes i retur per post.
        AvsenderAdresseType avsenderadresse = new AvsenderAdresseType();
        avsenderadresse.setAdresselinje(dokumentFelles.getPostadresse().getAdresselinje1());
        avsenderadresse.setNavEnhetsNavn(dokumentFelles.getNavnAvsenderEnhet());
        avsenderadresse.setPostNr(dokumentFelles.getPostadresse().getPostnummer());
        avsenderadresse.setPoststed(dokumentFelles.getPostadresse().getPoststed());

        kontaktInformasjonType.setPostadresse(avsenderadresse);
        //Adressen skal benyttes dersom bruker/mottaker har behov for å kontakte NAV per post.
        kontaktInformasjonType.setReturadresse(avsenderadresse);
        return kontaktInformasjonType;
    }

    public static boolean erNorskAdresse(DokumentAdresse adresse) {
        return adresse.getLand() != null && LANDSKODE_NORGE.equalsIgnoreCase(adresse.getLand()) && adresse.getPostnummer() != null;
    }

    public static NorskPostadresse lagNorskPostadresse(DokumentFelles dokumentFelles) {
        NorskPostadresse adresse = new NorskPostadresse();
        adresse.setAdresselinje1(dokumentFelles.getMottakerAdresse().getAdresselinje1());
        adresse.setAdresselinje2(dokumentFelles.getMottakerAdresse().getAdresselinje2());
        adresse.setAdresselinje3(dokumentFelles.getMottakerAdresse().getAdresselinje3());
        no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.informasjon.Landkoder landkode = new no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.informasjon.Landkoder();
        landkode.setKodeRef("NO");
        landkode.setValue("NO");
        adresse.setLand(landkode);
        adresse.setPostnummer(dokumentFelles.getMottakerAdresse().getPostnummer());
        adresse.setPoststed(dokumentFelles.getMottakerAdresse().getPoststed());
        return adresse;
    }

}

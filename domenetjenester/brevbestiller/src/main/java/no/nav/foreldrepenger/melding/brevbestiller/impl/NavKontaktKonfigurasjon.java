package no.nav.foreldrepenger.melding.brevbestiller.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.vedtak.konfig.KonfigVerdi;

@ApplicationScoped
public class NavKontaktKonfigurasjon {
    private String norg2KontaktTelefonNummer = "55553333";
    private String norg2NavKlageinstansTelefon = "21071730";
    private String brevAvsenderEnhetNavn = "NAV Familie- og pensjonsytelser";
    private String brevAvsenderKlageEnhet = "NAV Klageinstans";
    private String brevReturadresseAdresselinje1 = "Postboks 6600 Etterstad";
    private String brevReturadressePostnummer = "0607";
    private String brevReturadressePoststed = "OSLO";

    public NavKontaktKonfigurasjon() {
        // for CDI proxy
    }

    @Inject
    public NavKontaktKonfigurasjon(
            @KonfigVerdi(value = "norg2.kontakt.telefonnummer") String norg2KontaktTelefonNummer,
            @KonfigVerdi(value = "norg2.kontakt.klageinstans.telefonnummer") String norg2NavKlageinstansTelefon,
            @KonfigVerdi("brev.avsender.enhet.navn") String brevAvsenderEnhetNavn,
            @KonfigVerdi("brev.avsender.klage.enhet.navn") String brevAvsenderKlageEnhet,
            @KonfigVerdi("brev.returadresse.adresselinje1") String brevReturadresseAdresselinje1,
            @KonfigVerdi("brev.returadresse.postnummer") String brevReturadressePostnummer,
            @KonfigVerdi("brev.returadresse.poststed") String brevReturadressePoststed) {
        this.norg2KontaktTelefonNummer = norg2KontaktTelefonNummer;
        this.norg2NavKlageinstansTelefon = norg2NavKlageinstansTelefon;
        this.brevAvsenderEnhetNavn = brevAvsenderEnhetNavn;
        this.brevAvsenderKlageEnhet = brevAvsenderKlageEnhet;
        this.brevReturadresseAdresselinje1 = brevReturadresseAdresselinje1;
        this.brevReturadressePostnummer = brevReturadressePostnummer;
        this.brevReturadressePoststed = brevReturadressePoststed;
    }

    public String getNorg2KontaktTelefonNummer() {
        return norg2KontaktTelefonNummer;
    }

    public String getNorg2NavKlageinstansTelefon() {
        return norg2NavKlageinstansTelefon;
    }

    public String getBrevAvsenderEnhetNavn() {
        return brevAvsenderEnhetNavn;
    }

    public String getBrevAvsenderKlageEnhet() {
        return brevAvsenderKlageEnhet;
    }

    public String getBrevReturadresseAdresselinje1() {
        return brevReturadresseAdresselinje1;
    }

    public String getBrevReturadressePostnummer() {
        return brevReturadressePostnummer;
    }

    public String getBrevReturadressePoststed() {
        return brevReturadressePoststed;
    }
}

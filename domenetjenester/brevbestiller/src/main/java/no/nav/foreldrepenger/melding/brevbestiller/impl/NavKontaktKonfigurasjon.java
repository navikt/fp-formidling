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
            @KonfigVerdi(value = "norg2.kontakt.telefonnummer", defaultVerdi = "55553333") String norg2KontaktTelefonNummer,
            @KonfigVerdi(value = "norg2.kontakt.klageinstans.telefonnummer", defaultVerdi = "21 07 17 30") String norg2NavKlageinstansTelefon,
            @KonfigVerdi(value = "brev.avsender.enhet.navn", defaultVerdi = "NAV Familie- og pensjonsytelser") String brevAvsenderEnhetNavn,
            @KonfigVerdi(value = "brev.avsender.klage.enhet.navn", defaultVerdi = "NAV Klageinstans") String brevAvsenderKlageEnhet,
            @KonfigVerdi(value = "brev.returadresse.adresselinje1", defaultVerdi = "Postboks 6600 Etterstad") String brevReturadresseAdresselinje1,
            @KonfigVerdi(value = "brev.returadresse.postnummer", defaultVerdi = "0607") String brevReturadressePostnummer,
            @KonfigVerdi(value = "brev.returadresse.poststed", defaultVerdi = "OSLO") String brevReturadressePoststed) {
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

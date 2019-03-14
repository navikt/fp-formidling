package no.nav.foreldrepenger.tps.impl;

import no.nav.tjeneste.virksomhet.person.v3.informasjon.Aktoer;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Bruker;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.NorskIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Person;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Personidenter;

public final class TpsUtil {

    private TpsUtil() {
        //for å hindre instanser av util klasse
    }

    static String getFnr(Person person) {
        Aktoer aktor = person.getAktoer();
        return getFnr(aktor);
    }

    private static String getFnr(Aktoer aktor) {
        if (!(aktor instanceof PersonIdent)) {
            throw new IllegalArgumentException("Krever at person inneholder en " + PersonIdent.class + " men fikk " + (aktor != null ? aktor.getClass().getName() : null));
        }
        PersonIdent pi = (PersonIdent) aktor;
        return pi.getIdent().getIdent();
    }

    public static PersonIdent lagPersonIdent(String fnr) {
        if (fnr == null || fnr.isEmpty()) {
            throw new IllegalArgumentException("Fødselsnummer kan ikke være null eller tomt");
        }

        PersonIdent personIdent = new PersonIdent();
        NorskIdent norskIdent = new NorskIdent();
        norskIdent.setIdent(fnr);

        Personidenter type = new Personidenter();
        type.setValue(erDNr(fnr) ? "DNR" : "FNR");
        norskIdent.setType(type);

        personIdent.setIdent(norskIdent);
        return personIdent;
    }

    private static boolean erDNr(String fnr) {
        //D-nummer kan indentifiseres ved at første siffer er 4 større enn hva som finnes i fødselsnumre
        char førsteTegn = fnr.charAt(0);
        return førsteTegn >= '4' && førsteTegn <= '7';
    }

    static no.nav.foreldrepenger.melding.typer.PersonIdent getPersonIdent(Bruker bruker) {
        return getPersonIdent(bruker.getAktoer());
    }

    static no.nav.foreldrepenger.melding.typer.PersonIdent getPersonIdent(Aktoer aktor) {
        if (!(aktor instanceof PersonIdent)) {
            throw new IllegalArgumentException("Krever at person inneholder en " + PersonIdent.class + " men fikk " + (aktor != null ? aktor.getClass().getName() : null));
        }
        PersonIdent pi = (PersonIdent) aktor;
        String fnr = pi.getIdent().getIdent();
        return no.nav.foreldrepenger.melding.typer.PersonIdent.fra(fnr);
    }

    public static String getPersonnavn(Person person) {
        return person.getPersonnavn().getSammensattNavn();
    }
}

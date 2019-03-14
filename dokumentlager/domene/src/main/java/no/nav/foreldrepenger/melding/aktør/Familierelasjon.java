package no.nav.foreldrepenger.melding.aktør;

import java.time.LocalDate;

import no.nav.foreldrepenger.melding.behandling.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.typer.PersonIdent;

public class Familierelasjon {
    private PersonIdent personIdent;
    private RelasjonsRolleType relasjonsrolle;
    private LocalDate fødselsdato;
    private String adresse;
    private Boolean harSammeBosted;

    /**
     * @deprecated bruk ctor med PersonIdent
     */
    @Deprecated
    public Familierelasjon(String fnr, RelasjonsRolleType relasjonsrolle, LocalDate fødselsdato,
                           String adresse, Boolean harSammeBosted) {

        this(PersonIdent.fra(fnr), relasjonsrolle, fødselsdato, adresse, harSammeBosted);
    }

    public Familierelasjon(PersonIdent personIdent, RelasjonsRolleType relasjonsrolle, LocalDate fødselsdato,
                           String adresse, Boolean harSammeBosted) {
        this.personIdent = personIdent;
        this.relasjonsrolle = relasjonsrolle;
        this.fødselsdato = fødselsdato;
        this.adresse = adresse;
        this.harSammeBosted = harSammeBosted;
    }

    public LocalDate getFødselsdato() {
        return fødselsdato;
    }

    /**
     * @deprecated bruk {@link #getPersonIdent()}
     */
    @Deprecated
    public String getFnr() {
        return personIdent.getIdent();
    }

    public PersonIdent getPersonIdent() {
        return personIdent;
    }

    public RelasjonsRolleType getRelasjonsrolle() {
        return relasjonsrolle;
    }

    public String getAdresse() {
        return adresse;
    }

    public Boolean getHarSammeBosted() {
        return harSammeBosted;
    }

    @Override
    public String toString() {
        // tar ikke med personIdent i toString så det ikke lekkeri logger etc.
        return getClass().getSimpleName()
                + "<relasjon=" + relasjonsrolle  //$NON-NLS-1$
                + ", fødselsdato=" + fødselsdato //$NON-NLS-1$
                + ">"; //$NON-NLS-1$
    }
}

package no.nav.foreldrepenger.melding.familiehendelse;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Optional;

public class FamilieHendelse {

    private FamilieHendelseType familieHendelseType; //Kodeliste.FamilieHendelseType
    private BigInteger antallBarn;
    private String barna;
    private String UidentifisertBarn;
    private String terminbekreftelse;

    private Optional<LocalDate> skjæringstidspunkt;
    private Optional<LocalDate> termindato;
    private Optional<LocalDate> fødselsdato;
    private Optional<LocalDate> dødsdato;
    private boolean barnErFødt;
    private boolean gjelderFødsel;

    public FamilieHendelse(BigInteger antallBarn,
                           Optional<LocalDate> skjæringstidspunkt,
                           Optional<LocalDate> termindato,
                           boolean barnErFødt,
                           boolean gjelderFødsel,
                           FamilieHendelseType familieHendelseType,
                           Optional<LocalDate> fødselsdato,
                           Optional<LocalDate> dødsdato) {
        this.antallBarn = antallBarn;
        this.skjæringstidspunkt = skjæringstidspunkt;
        this.termindato = termindato;
        this.barnErFødt = barnErFødt;
        this.gjelderFødsel = gjelderFødsel;
        this.familieHendelseType = familieHendelseType;
        this.fødselsdato = fødselsdato;
        this.dødsdato = dødsdato;
    }

    public boolean isGjelderFødsel() {
        return gjelderFødsel;
    }

    public boolean isBarnErFødt() {
        return barnErFødt;
    }

    public Optional<LocalDate> getSkjæringstidspunkt() {
        return skjæringstidspunkt;
    }

    public Optional<LocalDate> getTermindato() {
        return termindato;
    }

    public Optional<LocalDate> getFødselsdato() {
        return fødselsdato;
    }

    public Optional<LocalDate> getDødsdato() {
        return dødsdato;
    }

    public FamilieHendelseType getFamilieHendelseType() {
        return familieHendelseType;
    }

    public BigInteger getAntallBarn() {
        return antallBarn;
    }

    public String getBarna() {
        return barna;
    }

    public String getUidentifisertBarn() {
        return UidentifisertBarn;
    }

    public String getTerminbekreftelse() {
        return terminbekreftelse;
    }
}

package no.nav.foreldrepenger.fpformidling.familiehendelse;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Optional;

public class FamilieHendelse {

    private FamilieHendelseType familieHendelseType; //Kodeliste.FamilieHendelseType
    private BigInteger antallBarn;
    private int antallDødeBarn;

    private Optional<LocalDate> skjæringstidspunkt;
    private Optional<LocalDate> termindato;
    private Optional<LocalDate> fødselsdato;
    private Optional<LocalDate> dødsdato;
    private boolean barnErFødt;
    private boolean gjelderFødsel;

    public FamilieHendelse(BigInteger antallBarn,
                           int antallDødeBarn,
                           boolean barnErFødt,
                           boolean gjelderFødsel,
                           FamilieHendelseType familieHendelseType,
                           OptionalDatoer optionalDato) {
        this.antallBarn = antallBarn;
        this.antallDødeBarn = antallDødeBarn;
        this.skjæringstidspunkt = optionalDato.skjæringstidspunkt;
        this.termindato = optionalDato.termindato;
        this.barnErFødt = barnErFødt;
        this.gjelderFødsel = gjelderFødsel;
        this.familieHendelseType = familieHendelseType;
        this.fødselsdato = optionalDato.fødselsdato;
        this.dødsdato = optionalDato.dødsdato;
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

    public int getAntallDødeBarn() {
        return antallDødeBarn;
    }


    public static final class OptionalDatoer {
        private Optional<LocalDate> skjæringstidspunkt;
        private Optional<LocalDate> termindato;
        private Optional<LocalDate> fødselsdato;
        private Optional<LocalDate> dødsdato;

        public OptionalDatoer(Optional<LocalDate> skjæringstidspunkt, Optional<LocalDate> termindato,
                              Optional<LocalDate> fødselsdato, Optional<LocalDate> dødsdato) {
            this.skjæringstidspunkt = skjæringstidspunkt;
            this.termindato = termindato;
            this.fødselsdato = fødselsdato;
            this.dødsdato = dødsdato;
        }
    }
}

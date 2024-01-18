package no.nav.foreldrepenger.fpformidling.domene.familiehendelse;

import java.time.LocalDate;
import java.util.Optional;

public class FamilieHendelse {

    private final FamilieHendelseType familieHendelseType;
    private final int antallBarn;
    private final int antallDødeBarn;
    private final LocalDate skjæringstidspunkt;
    private final LocalDate termindato;
    private final LocalDate fødselsdato;
    private final LocalDate dødsdato;
    private final boolean barnErFødt;
    private final boolean gjelderFødsel;

    public FamilieHendelse(FamilieHendelseType familieHendelseType,
                           int antallBarn,
                           int antallDødeBarn,
                           LocalDate skjæringstidspunkt,
                           LocalDate termindato,
                           LocalDate fødselsdato,
                           LocalDate dødsdato,
                           boolean barnErFødt,
                           boolean gjelderFødsel) {
        this.familieHendelseType = familieHendelseType;
        this.antallBarn = antallBarn;
        this.antallDødeBarn = antallDødeBarn;
        this.skjæringstidspunkt = skjæringstidspunkt;
        this.termindato = termindato;
        this.fødselsdato = fødselsdato;
        this.dødsdato = dødsdato;
        this.barnErFødt = barnErFødt;
        this.gjelderFødsel = gjelderFødsel;
    }

    public boolean gjelderFødsel() {
        return gjelderFødsel;
    }

    public boolean barnErFødt() {
        return barnErFødt;
    }

    public Optional<LocalDate> skjæringstidspunkt() {
        return Optional.ofNullable(skjæringstidspunkt);
    }

    public Optional<LocalDate> termindato() {
        return Optional.ofNullable(termindato);
    }

    public Optional<LocalDate> fødselsdato() {
        return Optional.ofNullable(fødselsdato);
    }

    public Optional<LocalDate> dødsdato() {
        return Optional.ofNullable(dødsdato);
    }

    public FamilieHendelseType familieHendelseType() {
        return familieHendelseType;
    }

    public int antallBarn() {
        return antallBarn;
    }

    public int antallDødeBarn() {
        return antallDødeBarn;
    }
}

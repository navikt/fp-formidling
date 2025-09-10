package no.nav.foreldrepenger.fpformidling.domene.familiehendelse;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record FamilieHendelse(List<Barn> barn, LocalDate termindato, int antallBarn, LocalDate omsorgsovertakelse) {
    public boolean barnErFødt() {
        return !barn.isEmpty();
    }

    public boolean gjelderFødsel() {
        return omsorgsovertakelse == null;
    }

    public Optional<LocalDate> tidligstDødsdato() {
        return barn.stream().map(FamilieHendelse.Barn::dødsdato).filter(Objects::nonNull).min(LocalDate::compareTo);
    }

    public Optional<LocalDate> tidligstFødselsdato() {
        return barn.stream().map(FamilieHendelse.Barn::fødselsdato).filter(Objects::nonNull).min(LocalDate::compareTo);
    }

    public record Barn(LocalDate fødselsdato, LocalDate dødsdato) {
    }
}

package no.nav.foreldrepenger.fpsak.dto.uttak.saldo;

import java.util.Objects;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.ArbeidsgiverDto;

public class AktivitetIdentifikatorDto {

    private KodeDto uttakArbeidType;
    private ArbeidsgiverDto arbeidsgiver;
    private String arbeidsforholdId;

    public AktivitetIdentifikatorDto() {
    }

    public KodeDto getUttakArbeidType() {
        return uttakArbeidType;
    }

    public ArbeidsgiverDto getArbeidsgiver() {
        return arbeidsgiver;
    }

    public String getArbeidsforholdId() {
        return arbeidsforholdId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AktivitetIdentifikatorDto that = (AktivitetIdentifikatorDto) o;
        return Objects.equals(uttakArbeidType, that.uttakArbeidType) &&
                Objects.equals(arbeidsgiver, that.arbeidsgiver) &&
                Objects.equals(arbeidsforholdId, that.arbeidsforholdId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uttakArbeidType, arbeidsgiver, arbeidsforholdId);
    }
}

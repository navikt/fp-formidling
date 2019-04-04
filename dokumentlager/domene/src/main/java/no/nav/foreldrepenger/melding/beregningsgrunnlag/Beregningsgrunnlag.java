package no.nav.foreldrepenger.melding.beregningsgrunnlag;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag.BeregningsgrunnlagDto;
import no.nav.foreldrepenger.melding.typer.Beløp;

public class Beregningsgrunnlag {
    private Beløp grunnbeløp;
    private List<BeregningsgrunnlagPeriode> beregningsgrunnlagPerioder = new ArrayList<>();
    private List<BeregningsgrunnlagAktivitetStatus> aktivitetStatuser = new ArrayList<>();

    private Beregningsgrunnlag() {

    }

    public static Beregningsgrunnlag fraDto(BeregningsgrunnlagDto dto) {
        Builder builder = Beregningsgrunnlag.builder();
        //TODO - vi burde ikke trenge å gange
        builder.medGrunnbeløp(new Beløp(BigDecimal.valueOf(dto.getHalvG()).multiply(BigDecimal.valueOf(2))));
        dto.getAktivitetStatus().stream().map(BeregningsgrunnlagAktivitetStatus::fraDto).forEach(builder::leggTilBeregningsgrunnlagAktivitetStatus);
        return null;
    }

    public static Beregningsgrunnlag.Builder builder() {
        return new Beregningsgrunnlag.Builder();
    }

    public Beløp getGrunnbeløp() {
        return grunnbeløp;
    }

    public List<BeregningsgrunnlagPeriode> getBeregningsgrunnlagPerioder() {
        return beregningsgrunnlagPerioder;
    }

    public List<BeregningsgrunnlagAktivitetStatus> getAktivitetStatuser() {
        return Collections.unmodifiableList(aktivitetStatuser);
    }

    public Hjemmel getHjemmel() {
        if (aktivitetStatuser.isEmpty()) {
            return Hjemmel.UDEFINERT;
        }
        if (aktivitetStatuser.size() == 1) {
            return aktivitetStatuser.get(0).getHjemmel();
        }
        Optional<BeregningsgrunnlagAktivitetStatus> dagpenger = aktivitetStatuser.stream()
                .filter(as -> Hjemmel.F_14_7_8_49.equals(as.getHjemmel()))
                .findFirst();
        if (dagpenger.isPresent()) {
            return dagpenger.get().getHjemmel();
        }
        Optional<BeregningsgrunnlagAktivitetStatus> gjelder = aktivitetStatuser.stream()
                .filter(as -> !Hjemmel.F_14_7.equals(as.getHjemmel()))
                .findFirst();
        return gjelder.isPresent() ? gjelder.get().getHjemmel() : Hjemmel.F_14_7;
    }

    public static class Builder {
        private Beløp grunnbeløp;
        private List<BeregningsgrunnlagPeriode> beregningsgrunnlagPerioder = new ArrayList<>();
        private List<BeregningsgrunnlagAktivitetStatus> aktivitetStatuser = new ArrayList<>();

        public Builder medGrunnbeløp(Beløp grunnbeløp) {
            this.grunnbeløp = grunnbeløp;
            return this;
        }

        public Builder leggTilBeregningsgrunnlagPeriode(BeregningsgrunnlagPeriode beregningsgrunnlagPeriode) {
            this.beregningsgrunnlagPerioder.add(beregningsgrunnlagPeriode);
            return this;
        }

        public Builder leggTilBeregningsgrunnlagAktivitetStatus(BeregningsgrunnlagAktivitetStatus beregningsgrunnlagAktivitetStatus) {
            this.aktivitetStatuser.add(beregningsgrunnlagAktivitetStatus);
            return this;
        }

        public Beregningsgrunnlag build() {
            Beregningsgrunnlag beregningsgrunnlag = new Beregningsgrunnlag();
            beregningsgrunnlag.grunnbeløp = grunnbeløp;
            beregningsgrunnlag.beregningsgrunnlagPerioder = beregningsgrunnlagPerioder;
            beregningsgrunnlag.aktivitetStatuser = aktivitetStatuser;
            return beregningsgrunnlag;
        }

    }

}

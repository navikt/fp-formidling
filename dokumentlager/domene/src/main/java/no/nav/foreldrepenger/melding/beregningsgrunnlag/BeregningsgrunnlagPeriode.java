package no.nav.foreldrepenger.melding.beregningsgrunnlag;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;

public class BeregningsgrunnlagPeriode {
    private Long dagsats;
    private BigDecimal bruttoPrÅr;
    private List<String> periodeÅrsaker; //Kode beregningsgrunnlagperiodeÅrsakerer.periodeÅrsaker
    private DatoIntervall periode;
    private List<BeregningsgrunnlagPrStatusOgAndel> beregningsgrunnlagPrStatusOgAndelList = new ArrayList<>();

    private BeregningsgrunnlagPeriode() {
    }

    private BeregningsgrunnlagPeriode(Builder builder) {
        dagsats = builder.dagsats;
        bruttoPrÅr = builder.bruttoPrÅr;
        periodeÅrsaker = builder.periodeÅrsaker;
        periode = builder.periode;
        beregningsgrunnlagPrStatusOgAndelList = builder.beregningsgrunnlagPrStatusOgAndelList;
    }

    public static BeregningsgrunnlagPeriode fraDto(BeregningsgrunnlagPeriodeDto dto) {
        DatoIntervall intervall = dto.getBeregningsgrunnlagPeriodeTom() != null ?
                DatoIntervall.fraOgMedTilOgMed(dto.getBeregningsgrunnlagPeriodeFom(), dto.getBeregningsgrunnlagPeriodeTom()) :
                DatoIntervall.fraOgMed(dto.getBeregningsgrunnlagPeriodeFom());
        return ny()
                .medBruttoPrÅr(dto.getBruttoPrAar())
                .medDagsats(dto.getDagsats())
                .medPeriode(intervall)
                .medperiodeÅrsaker(dto.getPeriodeAarsaker().stream().map(KodeDto::getKode).collect(Collectors.toList()))
                .medBeregningsgrunnlagPrStatusOgAndelList(dto.getBeregningsgrunnlagPrStatusOgAndel().stream().map(BeregningsgrunnlagPrStatusOgAndel::fraDto).collect(Collectors.toList()))
                .build();
    }

    public static Builder ny() {
        return new Builder();
    }

    public Long getDagsats() {
        return dagsats;
    }

    public BigDecimal getBruttoPrÅr() {
        return bruttoPrÅr;
    }

    public List<String> getperiodeÅrsaker() {
        return periodeÅrsaker;
    }

    public LocalDate getBeregningsgrunnlagPeriodeFom() {
        return periode.getFomDato();
    }

    public LocalDate getBeregningsgrunnlagPeriodeTom() {
        return periode.getTomDato();
    }

    public List<BeregningsgrunnlagPrStatusOgAndel> getBeregningsgrunnlagPrStatusOgAndelList() {
        return Collections.unmodifiableList(beregningsgrunnlagPrStatusOgAndelList);
    }

    public static final class Builder {
        private Long dagsats;
        private BigDecimal bruttoPrÅr;
        private List<String> periodeÅrsaker;
        private DatoIntervall periode;
        private List<BeregningsgrunnlagPrStatusOgAndel> beregningsgrunnlagPrStatusOgAndelList;

        private Builder() {
        }

        public Builder medDagsats(Long val) {
            dagsats = val;
            return this;
        }

        public Builder medBruttoPrÅr(BigDecimal val) {
            bruttoPrÅr = val;
            return this;
        }

        public Builder medperiodeÅrsaker(List<String> val) {
            periodeÅrsaker = val;
            return this;
        }

        public Builder medPeriode(DatoIntervall val) {
            periode = val;
            return this;
        }

        public Builder medBeregningsgrunnlagPrStatusOgAndelList(List<BeregningsgrunnlagPrStatusOgAndel> val) {
            beregningsgrunnlagPrStatusOgAndelList = val;
            return this;
        }

        public BeregningsgrunnlagPeriode build() {
            return new BeregningsgrunnlagPeriode(this);
        }
    }
}

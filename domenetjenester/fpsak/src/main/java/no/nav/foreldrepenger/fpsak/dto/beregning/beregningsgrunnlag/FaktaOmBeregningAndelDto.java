package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class FaktaOmBeregningAndelDto {


    @JsonProperty("andelsnr")
    private Long andelsnr;

    @JsonProperty("arbeidsforhold")
    private BeregningsgrunnlagArbeidsforholdDto arbeidsforhold;

    @JsonProperty("inntektskategori")
    private KodeDto inntektskategori;

    @JsonProperty("aktivitetStatus")
    private KodeDto aktivitetStatus;

    @JsonProperty("lagtTilAvSaksbehandler")
    private Boolean lagtTilAvSaksbehandler = false;

    @JsonProperty("fastsattAvSaksbehandler")
    private Boolean fastsattAvSaksbehandler = false;

    @JsonProperty("andelIArbeid")
    private List<BigDecimal> andelIArbeid = new ArrayList<>();

    FaktaOmBeregningAndelDto(Long andelsnr, BeregningsgrunnlagArbeidsforholdDto arbeidsforhold, KodeDto inntektskategori, KodeDto aktivitetStatus, Boolean lagtTilAvSaksbehandler, Boolean fastsattAvSaksbehandler, List<BigDecimal> andelIArbeid) {
        this.andelsnr = andelsnr;
        this.arbeidsforhold = arbeidsforhold;
        this.inntektskategori = inntektskategori;
        this.aktivitetStatus = aktivitetStatus;
        this.lagtTilAvSaksbehandler = lagtTilAvSaksbehandler;
        this.fastsattAvSaksbehandler = fastsattAvSaksbehandler;
        this.andelIArbeid = andelIArbeid;
    }

    public FaktaOmBeregningAndelDto() {
        // Hibernate
    }

    public Long getAndelsnr() {
        return andelsnr;
    }

    public void setAndelsnr(Long andelsnr) {
        this.andelsnr = andelsnr;
    }

    public BeregningsgrunnlagArbeidsforholdDto getArbeidsforhold() {
        return arbeidsforhold;
    }

    public void setArbeidsforhold(BeregningsgrunnlagArbeidsforholdDto arbeidsforhold) {
        this.arbeidsforhold = arbeidsforhold;
    }

    public KodeDto getInntektskategori() {
        return inntektskategori;
    }

    public void setInntektskategori(KodeDto inntektskategori) {
        this.inntektskategori = inntektskategori;
    }

    public KodeDto getAktivitetStatus() {
        return aktivitetStatus;
    }

    public void setAktivitetStatus(KodeDto aktivitetStatus) {
        this.aktivitetStatus = aktivitetStatus;
    }

    public Boolean getLagtTilAvSaksbehandler() {
        return lagtTilAvSaksbehandler;
    }

    public void setLagtTilAvSaksbehandler(Boolean lagtTilAvSaksbehandler) {
        this.lagtTilAvSaksbehandler = lagtTilAvSaksbehandler;
    }

    public Boolean getFastsattAvSaksbehandler() {
        return fastsattAvSaksbehandler;
    }

    public void setFastsattAvSaksbehandler(Boolean fastsattAvSaksbehandler) {
        this.fastsattAvSaksbehandler = fastsattAvSaksbehandler;
    }

    public List<BigDecimal> getAndelIArbeid() {
        return andelIArbeid;
    }

    public void setAndelIArbeid(List<BigDecimal> andelIArbeid) {
        this.andelIArbeid = andelIArbeid;
    }

    public void leggTilAndelIArbeid(BigDecimal andelIArbeid) {
        this.andelIArbeid.add(andelIArbeid);
    }
}

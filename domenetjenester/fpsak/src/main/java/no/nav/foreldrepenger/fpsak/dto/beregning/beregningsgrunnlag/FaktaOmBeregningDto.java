package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag;

import java.util.List;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class FaktaOmBeregningDto {

    private List<KortvarigeArbeidsforholdDto> kortvarigeArbeidsforhold;
    private TilstøtendeYtelseDto tilstøtendeYtelse;
    private FaktaOmBeregningAndelDto frilansAndel;
    private EndringBeregningsgrunnlagDto endringBeregningsgrunnlag;
    private KunYtelseDto kunYtelse;
    private List<KodeDto> faktaOmBeregningTilfeller;
    private List<ATogFLISammeOrganisasjonDto> arbeidstakerOgFrilanserISammeOrganisasjonListe;
    private List<FaktaOmBeregningAndelDto> arbeidsforholdMedLønnsendringUtenIM;
    private List<TilstøtendeYtelseAndelDto> besteberegningAndeler;
    private VurderMottarYtelseDto vurderMottarYtelse;
    private AvklarAktiviteterDto avklarAktiviteter;

    public FaktaOmBeregningDto() {
        // Hibernate
    }

    public KunYtelseDto getKunYtelse() {
        return kunYtelse;
    }

    public void setKunYtelse(KunYtelseDto kunYtelse) {
        this.kunYtelse = kunYtelse;
    }

    public EndringBeregningsgrunnlagDto getEndringBeregningsgrunnlag() {
        return endringBeregningsgrunnlag;
    }

    public void setEndringBeregningsgrunnlag(EndringBeregningsgrunnlagDto endringBeregningsgrunnlag) {
        this.endringBeregningsgrunnlag = endringBeregningsgrunnlag;
    }

    public List<KortvarigeArbeidsforholdDto> getKortvarigeArbeidsforhold() {
        return kortvarigeArbeidsforhold;
    }

    public TilstøtendeYtelseDto getTilstøtendeYtelse() {
        return tilstøtendeYtelse;
    }

    public void setKortvarigeArbeidsforhold(List<KortvarigeArbeidsforholdDto> kortvarigeArbeidsforhold) {
        this.kortvarigeArbeidsforhold = kortvarigeArbeidsforhold;
    }

    public void setTilstøtendeYtelse(TilstøtendeYtelseDto tilstøtendeYtelse) {
        this.tilstøtendeYtelse = tilstøtendeYtelse;
    }

    public FaktaOmBeregningAndelDto getFrilansAndel() {
        return frilansAndel;
    }

    public void setFrilansAndel(FaktaOmBeregningAndelDto frilansAndel) {
        this.frilansAndel = frilansAndel;
    }

    public List<FaktaOmBeregningAndelDto> getArbeidsforholdMedLønnsendringUtenIM() {
        return arbeidsforholdMedLønnsendringUtenIM;
    }

    public void setArbeidsforholdMedLønnsendringUtenIM(List<FaktaOmBeregningAndelDto> arbeidsforholdMedLønnsendringUtenIM) {
        this.arbeidsforholdMedLønnsendringUtenIM = arbeidsforholdMedLønnsendringUtenIM;
    }

    public List<KodeDto> getFaktaOmBeregningTilfeller() {
        return faktaOmBeregningTilfeller;
    }

    public void setFaktaOmBeregningTilfeller(List<KodeDto> faktaOmBeregningTilfeller) {
        this.faktaOmBeregningTilfeller = faktaOmBeregningTilfeller;
    }

    public List<ATogFLISammeOrganisasjonDto> getArbeidstakerOgFrilanserISammeOrganisasjonListe() {
        return arbeidstakerOgFrilanserISammeOrganisasjonListe;
    }

    public void setArbeidstakerOgFrilanserISammeOrganisasjonListe(List<ATogFLISammeOrganisasjonDto> aTogFLISammeOrganisasjonListe) {
        this.arbeidstakerOgFrilanserISammeOrganisasjonListe = aTogFLISammeOrganisasjonListe;
    }

    public List<TilstøtendeYtelseAndelDto> getBesteberegningAndeler() {
        return besteberegningAndeler;
    }

    public void setBesteberegningAndeler(List<TilstøtendeYtelseAndelDto> besteberegningAndeler) {
        this.besteberegningAndeler = besteberegningAndeler;
    }

    public VurderMottarYtelseDto getVurderMottarYtelse() {
        return vurderMottarYtelse;
    }

    public void setVurderMottarYtelse(VurderMottarYtelseDto vurderMottarYtelse) {
        this.vurderMottarYtelse = vurderMottarYtelse;
    }

    public AvklarAktiviteterDto getAvklarAktiviteter() {
        return avklarAktiviteter;
    }

    public void setAvklarAktiviteter(AvklarAktiviteterDto avklarAktiviteter) {
        this.avklarAktiviteter = avklarAktiviteter;
    }
}

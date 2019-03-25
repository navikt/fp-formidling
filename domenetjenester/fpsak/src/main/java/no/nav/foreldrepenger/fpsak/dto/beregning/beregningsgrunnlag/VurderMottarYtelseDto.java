package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class VurderMottarYtelseDto {

    private boolean erFrilans;
    private Boolean frilansMottarYtelse;
    private BigDecimal frilansInntektPrMnd;
    private List<ArbeidstakerUtenInntektsmeldingAndelDto> arbeidstakerAndelerUtenIM = new ArrayList<>();

    public boolean getErFrilans() {
        return erFrilans;
    }

    public void setErFrilans(boolean erFrilans) {
        this.erFrilans = erFrilans;
    }

    public Boolean getFrilansMottarYtelse() {
        return frilansMottarYtelse;
    }

    public BigDecimal getFrilansInntektPrMnd() {
        return frilansInntektPrMnd;
    }

    public void setFrilansInntektPrMnd(BigDecimal frilansInntektPrMnd) {
        this.frilansInntektPrMnd = frilansInntektPrMnd;
    }

    public void setFrilansMottarYtelse(Boolean frilansMottarYtelse) {
        this.frilansMottarYtelse = frilansMottarYtelse;
    }

    public List<ArbeidstakerUtenInntektsmeldingAndelDto> getArbeidstakerAndelerUtenIM() {
        return arbeidstakerAndelerUtenIM;
    }

    public void leggTilArbeidstakerAndelUtenInntektsmelding(ArbeidstakerUtenInntektsmeldingAndelDto arbeidstakerAndelUtenInnteksmelding) {
        this.arbeidstakerAndelerUtenIM.add(arbeidstakerAndelUtenInnteksmelding);
    }
}

package no.nav.foreldrepenger.fpsak.dto.inntektarbeidytelse;

import java.util.Collections;
import java.util.List;

public class InntektArbeidYtelseDto {

    private List<InntektsmeldingDto> inntektsmeldinger = Collections.emptyList();
    private List<RelaterteYtelserDto> relatertTilgrensendeYtelserForSoker = Collections.emptyList();
    private List<RelaterteYtelserDto> relatertTilgrensendeYtelserForAnnenForelder = Collections.emptyList();
    private List<RelaterteYtelserDto> innvilgetRelatertTilgrensendeYtelserForAnnenForelder = Collections.emptyList();
    private List<ArbeidsforholdDto> arbeidsforhold = Collections.emptyList();
    private boolean skalKunneLeggeTilNyeArbeidsforhold = false;

    public void setInntektsmeldinger(List<InntektsmeldingDto> inntektsmeldinger) {
        this.inntektsmeldinger = inntektsmeldinger;
    }

    public List<InntektsmeldingDto> getInntektsmeldinger() {
        return inntektsmeldinger;
    }

    void setRelatertTilgrensendeYtelserForSoker(List<RelaterteYtelserDto> relatertTilgrensendeYtelserForSoker) {
        this.relatertTilgrensendeYtelserForSoker = relatertTilgrensendeYtelserForSoker;
    }

    void setRelatertTilgrensendeYtelserForAnnenForelder(List<RelaterteYtelserDto> relatertTilgrensendeYtelserForAnnenForelder) {
        this.relatertTilgrensendeYtelserForAnnenForelder = relatertTilgrensendeYtelserForAnnenForelder;
    }

    void setInnvilgetRelatertTilgrensendeYtelserForAnnenForelder(List<RelaterteYtelserDto> innvilgetRelatertTilgrensendeYtelserForAnnenForelder) {
        this.innvilgetRelatertTilgrensendeYtelserForAnnenForelder = innvilgetRelatertTilgrensendeYtelserForAnnenForelder;
    }

    public List<RelaterteYtelserDto> getRelatertTilgrensendeYtelserForSoker() {
        return relatertTilgrensendeYtelserForSoker;
    }

    public List<RelaterteYtelserDto> getRelatertTilgrensendeYtelserForAnnenForelder() {
        return relatertTilgrensendeYtelserForAnnenForelder;
    }

    public List<RelaterteYtelserDto> getInnvilgetRelatertTilgrensendeYtelserForAnnenForelder() {
        return innvilgetRelatertTilgrensendeYtelserForAnnenForelder;
    }

    public List<ArbeidsforholdDto> getArbeidsforhold() {
        return arbeidsforhold;
    }

    public void setArbeidsforhold(List<ArbeidsforholdDto> arbeidsforhold) {
        this.arbeidsforhold = arbeidsforhold;
    }

    public boolean isSkalKunneLeggeTilNyeArbeidsforhold() {
        return skalKunneLeggeTilNyeArbeidsforhold;
    }

    public void setSkalKunneLeggeTilNyeArbeidsforhold(boolean skalKunneLeggeTilNyeArbeidsforhold) {
        this.skalKunneLeggeTilNyeArbeidsforhold = skalKunneLeggeTilNyeArbeidsforhold;
    }

}

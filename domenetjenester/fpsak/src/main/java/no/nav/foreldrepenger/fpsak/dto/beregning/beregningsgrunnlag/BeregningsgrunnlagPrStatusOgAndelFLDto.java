package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag;

public class BeregningsgrunnlagPrStatusOgAndelFLDto extends BeregningsgrunnlagPrStatusOgAndelDto {
    private Boolean erNyoppstartetEllerSammeOrganisasjon;
    private Boolean erNyoppstartet;

    public BeregningsgrunnlagPrStatusOgAndelFLDto() {
        super();
        // trengs for deserialisering av JSON
    }

    public Boolean getErNyoppstartet() {
        return erNyoppstartet;
    }

    public void setErNyoppstartet(Boolean erNyoppstartet) {
        this.erNyoppstartet = erNyoppstartet;
    }

    public Boolean getErNyoppstartetEllerSammeOrganisasjon() {
        return erNyoppstartetEllerSammeOrganisasjon;
    }

    public void setErNyoppstartetEllerSammeOrganisasjon(Boolean erNyoppstartetEllerSammeOrganisasjon) {
        this.erNyoppstartetEllerSammeOrganisasjon = erNyoppstartetEllerSammeOrganisasjon;
    }
}

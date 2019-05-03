package no.nav.foreldrepenger.melding.beregningsgrunnlag;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.beregning.Sats;
import no.nav.foreldrepenger.melding.beregning.SatsType;
import no.nav.foreldrepenger.melding.beregning.repository.BeregningRepository;
import no.nav.foreldrepenger.melding.typer.Beløp;

@ApplicationScoped
public class GrunnbeløpTjeneste {

    private BeregningRepository beregningRepository;

    GrunnbeløpTjeneste() {
        // for CDI proxy
    }

    @Inject
    public GrunnbeløpTjeneste(BeregningRepository beregningRepository) {
        this.beregningRepository = beregningRepository;
    }

    public Beløp grunnbeløpPå(LocalDate dato) {
        Sats g = beregningRepository.finnEksaktSats(SatsType.GRUNNBELØP, dato);
        return new Beløp(BigDecimal.valueOf(g.getVerdi()));
    }

    public long finnHalvGPå(LocalDate dato) {
        return grunnbeløpPå(dato).getVerdi().divide(BigDecimal.valueOf(2), RoundingMode.HALF_EVEN).longValue();
    }

}

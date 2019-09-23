package no.nav.foreldrepenger.melding.datamapper.domene;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.Test;

import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;

public class UttakMapperTest {


    private final static LocalDate FØRSTE_JANUAR = LocalDate.of(2019, 1, 1);
    private final static LocalDate TREDJE_JANUAR = LocalDate.of(2019, 1, 3);
    private final static LocalDate FJERDE_JANUAR = LocalDate.of(2019, 1, 4);

    @Test
    public void skal_finne_siste_dag_fra_uttak_begge_innvilget() {
        UttakResultatPerioder uttakResultatPerioder = settOppUttaksperioder(PeriodeResultatType.INNVILGET, PeriodeResultatType.INNVILGET,
                DatoIntervall.fraOgMedTilOgMed(FØRSTE_JANUAR, TREDJE_JANUAR), DatoIntervall.fraOgMedTilOgMed(TREDJE_JANUAR, FJERDE_JANUAR));
        assertThat(UttakMapper.finnSisteDagAvSistePeriode(uttakResultatPerioder)).isEqualTo(XmlUtil.finnDatoVerdiAvUtenTidSone(FJERDE_JANUAR));
    }


    @Test
    public void skal_finne_siste_dag_fra_uttak_begge_siste_avslått() {
        UttakResultatPerioder uttakResultatPerioder = settOppUttaksperioder(PeriodeResultatType.INNVILGET, PeriodeResultatType.AVSLÅTT,
                DatoIntervall.fraOgMedTilOgMed(FØRSTE_JANUAR, TREDJE_JANUAR), DatoIntervall.fraOgMedTilOgMed(TREDJE_JANUAR, FJERDE_JANUAR));
        assertThat(UttakMapper.finnSisteDagAvSistePeriode(uttakResultatPerioder)).isEqualTo(XmlUtil.finnDatoVerdiAvUtenTidSone(TREDJE_JANUAR));
    }

    private UttakResultatPerioder settOppUttaksperioder(PeriodeResultatType periodeResultatFørste, PeriodeResultatType periodeResultatAndre, DatoIntervall tidsperiodeEn, DatoIntervall tidsperiodeTo) {
        return UttakResultatPerioder.ny().medPerioder(
                List.of(UttakResultatPeriode.ny()
                        .medTidsperiode(tidsperiodeEn)
                        .medPeriodeResultatType(periodeResultatFørste)
                        .build()))
                .medPerioderAnnenPart(
                        List.of(UttakResultatPeriode.ny()
                                .medTidsperiode(tidsperiodeTo)
                                .medPeriodeResultatType(periodeResultatAndre)
                                .build()))
                .build();
    }
}

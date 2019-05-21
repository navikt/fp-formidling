package no.nav.foreldrepenger.melding.datamapper.domene;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.VurderingsstatusKode;
import no.nav.foreldrepenger.melding.søknad.Søknad;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.melding.ytelsefordeling.OppgittRettighet;

public class UttakMapperTest {


    private final static LocalDate FØRSTE_JANUAR = LocalDate.of(2019, 1, 1);
    private final static LocalDate TREDJE_JANUAR = LocalDate.of(2019, 1, 3);
    private final static LocalDate FJERDE_JANUAR = LocalDate.of(2019, 1, 4);
    private static UttakResultatPerioder uttakResultatPerioder;

    @Before
    public void setup() {
        settOppUttaksperioder(PeriodeResultatType.INNVILGET, PeriodeResultatType.INNVILGET, DatoIntervall.fraOgMedTilOgMed(FØRSTE_JANUAR, TREDJE_JANUAR), DatoIntervall.fraOgMedTilOgMed(TREDJE_JANUAR, FJERDE_JANUAR));
    }

    @Test
    public void skal_finne_siste_dag_fra_uttak_begge_innvilget() {
        assertThat(UttakMapper.finnSisteDagAvSistePeriode(uttakResultatPerioder)).isEqualTo(XmlUtil.finnDatoVerdiAvUtenTidSone(FJERDE_JANUAR));
    }


    @Test
    public void skal_finne_siste_dag_fra_uttak_begge_siste_avslått() {
        settOppUttaksperioder(PeriodeResultatType.INNVILGET, PeriodeResultatType.AVSLÅTT, DatoIntervall.fraOgMedTilOgMed(FØRSTE_JANUAR, TREDJE_JANUAR), DatoIntervall.fraOgMedTilOgMed(TREDJE_JANUAR, FJERDE_JANUAR));
        assertThat(UttakMapper.finnSisteDagAvSistePeriode(uttakResultatPerioder)).isEqualTo(XmlUtil.finnDatoVerdiAvUtenTidSone(TREDJE_JANUAR));
    }

    private void settOppUttaksperioder(PeriodeResultatType periodeResultatFørste, PeriodeResultatType periodeResultatAndre, DatoIntervall tidsperiodeEn, DatoIntervall tidsperiodeTo) {
        uttakResultatPerioder = UttakResultatPerioder.ny().medPerioder(
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

    Søknad søknad;
    UttakResultatPerioder uttakresultatPerioder;
    private LocalDate nå = LocalDate.now();

    @Test
    public void skal_oppdage_aleneomsorg() {
        søknad = new Søknad(nå, nå, new OppgittRettighet(true));
        uttakresultatPerioder = UttakResultatPerioder.ny().medAleneomsorg(true).build();
        assertThat(UttakMapper.harSøkerAleneomsorg(søknad, uttakresultatPerioder)).isEqualTo(VurderingsstatusKode.JA);
        assertThat(UttakMapper.harSøkerAleneomsorgBoolean(søknad, uttakresultatPerioder)).isTrue();
    }

    @Test
    public void skal_oppdage_ikke_søkt_om_aleneomsorg() {
        søknad = new Søknad(nå, nå, new OppgittRettighet(false));
        uttakresultatPerioder = UttakResultatPerioder.ny().medAleneomsorg(false).build();
        assertThat(UttakMapper.harSøkerAleneomsorg(søknad, uttakresultatPerioder)).isEqualTo(VurderingsstatusKode.IKKE_VURDERT);
        assertThat(UttakMapper.harSøkerAleneomsorgBoolean(søknad, uttakresultatPerioder)).isFalse();
    }


    @Test
    public void skal_oppdage_avslått_aleneomsorg() {
        søknad = new Søknad(nå, nå, new OppgittRettighet(true));
        uttakresultatPerioder = UttakResultatPerioder.ny().build();
        assertThat(UttakMapper.harSøkerAleneomsorg(søknad, uttakresultatPerioder)).isEqualTo(VurderingsstatusKode.NEI);
        assertThat(UttakMapper.harSøkerAleneomsorgBoolean(søknad, uttakresultatPerioder)).isFalse();
    }

}

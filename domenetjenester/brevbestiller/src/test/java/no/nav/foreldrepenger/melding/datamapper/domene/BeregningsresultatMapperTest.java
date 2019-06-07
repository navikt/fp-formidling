package no.nav.foreldrepenger.melding.datamapper.domene;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.PeriodeListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.PeriodeType;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;

public class BeregningsresultatMapperTest {

    private ObjectFactory objectFactory = new ObjectFactory();
    private PeriodeListeType periodeListeType = objectFactory.createPeriodeListeType();
    private static final long HUNDRE = 100L;
    private BeregningsresultatFP beregningsresultat;

    @Before
    public void setup() {
        DatoIntervall ubetydeligPeriode = DatoIntervall.fraOgMedTilOgMed(LocalDate.now(), LocalDate.now().plusDays(1));
        beregningsresultat = BeregningsresultatFP.ny()
                .leggTilBeregningsresultatPerioder(List.of(BeregningsresultatPeriode.ny()
                                .medDagsats(HUNDRE)
                                .medPeriode(ubetydeligPeriode)
                                .build(),
                        BeregningsresultatPeriode.ny()
                                .medDagsats(HUNDRE * 2)
                                .medPeriode(ubetydeligPeriode)
                                .build()))
                .build();
    }


    @Test
    public void skal_finne_første_stønadsdato_null_ikke_satt() {
        assertThat(BeregningsresultatMapper.finnStønadsperiodeFomHvisFinnes(periodeListeType)).isEmpty();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void skal_finne_første_og_siste_stønadsdato_og_håndtere_null() {
        LocalDate førsteJanuarTjueAtten = LocalDate.of(2018, 1, 1);
        LocalDate TrettiendeAprilTjueAtten = LocalDate.of(2018, 4, 30);
        leggtilPeriode(LocalDate.of(2017, 1, 1), LocalDate.of(2017, 1, 30), false);
        leggtilPeriode(førsteJanuarTjueAtten, LocalDate.of(2018, 1, 30), true);
        leggtilPeriode(LocalDate.of(2018, 2, 1), LocalDate.of(2018, 2, 25), true);
        leggtilPeriode(LocalDate.of(2018, 3, 1), LocalDate.of(2018, 3, 30), null);
        leggtilPeriode(LocalDate.of(2018, 4, 1), TrettiendeAprilTjueAtten, true);
        leggtilPeriode(LocalDate.of(2019, 3, 1), LocalDate.of(2019, 3, 30), false);
        assertThat(BeregningsresultatMapper.finnStønadsperiodeFomHvisFinnes(periodeListeType).get()).isEqualTo(XmlUtil.finnDatoVerdiAvUtenTidSone(førsteJanuarTjueAtten));
        assertThat(BeregningsresultatMapper.finnStønadsperiodeTomHvisFinnes(periodeListeType).get()).isEqualTo(XmlUtil.finnDatoVerdiAvUtenTidSone(TrettiendeAprilTjueAtten));
    }

    @Test
    public void skal_finne_dagsats() {
        assertThat(BeregningsresultatMapper.finnDagsats(beregningsresultat)).isEqualTo(HUNDRE);
    }

    @Test
    public void skal_finne_månedsbeløp() {
        assertThat(BeregningsresultatMapper.finnMånedsbeløp(beregningsresultat)).isEqualTo(2166);
    }

    private boolean leggtilPeriode(LocalDate fom, LocalDate tom, Boolean innvilget) {
        return periodeListeType.getPeriode().add(lagPeriode(fom, tom, innvilget));
    }

    PeriodeType lagPeriode(LocalDate fom, LocalDate tom, Boolean innvilget) {
        PeriodeType periodeType = objectFactory.createPeriodeType();
        periodeType.setInnvilget(innvilget);
        periodeType.setPeriodeFom(XmlUtil.finnDatoVerdiAvUtenTidSone(fom));
        periodeType.setPeriodeTom(XmlUtil.finnDatoVerdiAvUtenTidSone(tom));
        return periodeType;
    }

}

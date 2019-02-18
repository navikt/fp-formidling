package no.nav.foreldrepenger.melding.web.app.metrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ProsessTaskGaugesCacheTest {

    private ProsessTaskGaugesCache cache; // objektet vi tester

    private MetricRepository mockMetricRepository;

    private List<Object[]> typiskRowSet;

    private List<Object[]> typiskRowSetStatus;

    @Before
    public void setup() {
        cache = new ProsessTaskGaugesCache();
        mockMetricRepository = mock(MetricRepository.class);
        cache.setMetricRepository(mockMetricRepository);

        Object[] row1 = {"fordeling.hentFraJoark", "KLAR", BigDecimal.valueOf(2)};
        Object[] row2 = {"fordeling.opprettSak", "KLAR", BigDecimal.valueOf(3)};
        Object[] row3 = {"fordeling.opprettSak", "VENTER_SVAR", BigDecimal.valueOf(7)};
        Object[] row4 = {"fordeling.opprettSak", "SUSPENDERT", BigDecimal.valueOf(19)};
        Object[] row5 = {"fordeling.opprettSak", "FEILET", BigDecimal.valueOf(11)};
        Object[] row6 = {"fordeling.opprettSak", "FERDIG", BigDecimal.valueOf(23)};
        Object[] row7 = {"innhentsaksopplysninger.relaterteYtelser", "KLAR", BigDecimal.valueOf(111)};
        Object[] row8 = {"innhentsaksopplysninger.relaterteYtelser", "FEILET", BigDecimal.valueOf(1331)};
        typiskRowSet = Arrays.asList(row1, row2, row3, row4, row5, row6, row7, row8);

        Object[] row11 = {"KLAR", BigDecimal.valueOf(2)};
        Object[] row13 = {"VENTER_SVAR", BigDecimal.valueOf(7)};
        Object[] row15 = {"FEILET", BigDecimal.valueOf(11)};
        Object[] row16 = {"FERDIG", BigDecimal.valueOf(23)};
        ;
        typiskRowSetStatus = Arrays.asList(row11, row13, row15, row16);
    }

    @Test
    public void skalGiAntallProsessTaskerKøetForStatus() {
        when(mockMetricRepository.tellAntallProsessTaskerPerStatus()).thenReturn(typiskRowSetStatus);

        assertThat(cache.antallProsessTaskerKøet()).isEqualTo(BigDecimal.valueOf(9));
    }

    @Test
    public void skalGiAntallProsessTaskerFeiletForStatus() {
        when(mockMetricRepository.tellAntallProsessTaskerPerStatus()).thenReturn(typiskRowSetStatus);

        assertThat(cache.antallProsessTaskerFeilet()).isEqualTo(BigDecimal.valueOf(11));
    }

    @Test
    public void skalGiAntallProsessTaskerKøetForTypeOgStatus() {
        when(mockMetricRepository.tellAntallProsessTaskerPerTypeOgStatus()).thenReturn(typiskRowSet);

        assertThat(cache.antallProsessTaskerKøet("fordeling.hentFraJoark")).isEqualTo(BigDecimal.valueOf(2));
        assertThat(cache.antallProsessTaskerKøet("fordeling.opprettSak")).isEqualTo(BigDecimal.valueOf(29)); // 3 + 7 + 19
        assertThat(cache.antallProsessTaskerKøet("innhentsaksopplysninger.relaterteYtelser")).isEqualTo(BigDecimal.valueOf(111));
        assertThat(cache.antallProsessTaskerKøet("foobar.bollocks")).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    public void skalGiAntallProsessTaskerFeiletForTypeOgStatus() {
        when(mockMetricRepository.tellAntallProsessTaskerPerTypeOgStatus()).thenReturn(typiskRowSet);

        assertThat(cache.antallProsessTaskerFeilet("fordeling.hentFraJoark")).isEqualTo(BigDecimal.ZERO);
        assertThat(cache.antallProsessTaskerFeilet("fordeling.opprettSak")).isEqualTo(BigDecimal.valueOf(11));
        assertThat(cache.antallProsessTaskerFeilet("innhentsaksopplysninger.relaterteYtelser")).isEqualTo(BigDecimal.valueOf(1331));
        assertThat(cache.antallProsessTaskerFeilet("foobar.bollocks")).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    public void skalGiAntallProsessTaskerMedTypePrefixKøet() {
        when(mockMetricRepository.tellAntallProsessTaskerPerTypeOgStatus()).thenReturn(typiskRowSet);

        assertThat(cache.antallProsessTaskerMedTypePrefixKøet("fordeling")).isEqualTo(BigDecimal.valueOf(31)); // 2 + 3 + 7 +19
        assertThat(cache.antallProsessTaskerMedTypePrefixKøet("innhentsaksopplysninger")).isEqualTo(BigDecimal.valueOf(111));
        assertThat(cache.antallProsessTaskerMedTypePrefixKøet("foobar")).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    public void metodeneSkalFungereMedTomtDatasett() {
        when(mockMetricRepository.tellAntallProsessTaskerPerTypeOgStatus()).thenReturn(Collections.emptyList());

        BigDecimal antallKøet = cache.antallProsessTaskerKøet("fordeling.hentFraJoark");
        assertThat(antallKøet).isEqualTo(BigDecimal.ZERO);

        BigDecimal antallFeilet = cache.antallProsessTaskerFeilet("fordeling.hentFraJoark");
        assertThat(antallFeilet).isEqualTo(BigDecimal.ZERO);

        BigDecimal antallMedTypePrefixKøet = cache.antallProsessTaskerMedTypePrefixKøet("fordeling");
        assertThat(antallMedTypePrefixKøet).isEqualTo(BigDecimal.ZERO);

        verify(mockMetricRepository).tellAntallProsessTaskerPerTypeOgStatus();
    }

    @Test
    public void skalGjenbrukeNyligCachetDatasett() {
        when(mockMetricRepository.tellAntallProsessTaskerPerTypeOgStatus()).thenReturn(Collections.emptyList());

        cache.antallProsessTaskerKøet("fordeling.hentFraJoark");
        cache.antallProsessTaskerFeilet("fordeling.hentFraJoark");
        cache.antallProsessTaskerMedTypePrefixKøet("fordeling");

        verify(mockMetricRepository, times(1)).tellAntallProsessTaskerPerTypeOgStatus();
    }

}

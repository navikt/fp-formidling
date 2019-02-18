package no.nav.foreldrepenger.melding.web.app.metrics;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.vedtak.util.Tuple;

@ApplicationScoped
class ProsessTaskGaugesCache {

    private static final long MAX_DATA_ALDER_MS = 10000;
    private final Set<String> statusKøetSet;
    private final Set<String> statusFeiletSet;
    private MetricRepository metricRepository;
    private Map<Tuple<String, String>, BigDecimal> antallPrTypeOgStatus;
    private Map<String, BigDecimal> antallPrStatus;
    private long antallLestTidspunktMs = 0;

    public ProsessTaskGaugesCache() {
        this.statusKøetSet = new HashSet<>(Arrays.asList("KLAR", "VENTER_SVAR", "SUSPENDERT"));
        this.statusFeiletSet = new HashSet<>(Collections.singletonList("FEILET"));
    }

    @Inject
    public void setMetricRepository(MetricRepository metricRepository) {
        this.metricRepository = metricRepository;
    }

    BigDecimal antallProsessTaskerKøet() {
        return antallProsessTasker(statusKøetSet);
    }

    BigDecimal antallProsessTaskerFeilet() {
        return antallProsessTasker(statusFeiletSet);
    }

    BigDecimal antallProsessTaskerKøet(String type) {
        return antallProsessTasker(type, statusKøetSet);
    }

    BigDecimal antallProsessTaskerFeilet(String type) {
        return antallProsessTasker(type, statusFeiletSet);
    }

    BigDecimal antallProsessTaskerMedTypePrefixKøet(String typePrefix) {
        refreshDataIfNeeded();

        BigDecimal sumAntall = BigDecimal.ZERO;
        for (Map.Entry<Tuple<String, String>, BigDecimal> entry : antallPrTypeOgStatus.entrySet()) {
            Tuple<String, String> key = entry.getKey();
            if (key.getElement1().startsWith(typePrefix)) {
                String status = key.getElement2();
                if (statusKøetSet.contains(status)) {
                    BigDecimal antall = entry.getValue();
                    sumAntall = sumAntall.add(antall);
                }
            }
        }
        return sumAntall;
    }

    private BigDecimal antallProsessTasker(String type, Set<String> statusSet) {
        refreshDataIfNeeded();

        BigDecimal sumAntall = BigDecimal.ZERO;
        for (String status : statusSet) {
            Tuple<String, String> key = new Tuple<>(type, status);
            BigDecimal antall = antallPrTypeOgStatus.get(key);
            if (antall != null) {
                sumAntall = sumAntall.add(antall);
            }
        }
        return sumAntall;
    }

    private BigDecimal antallProsessTasker(Set<String> statusSet) {
        refreshDataIfNeeded();

        BigDecimal sumAntall = BigDecimal.ZERO;
        for (String status : statusSet) {
            BigDecimal antall = antallPrStatus.get(status);
            if (antall != null) {
                sumAntall = sumAntall.add(antall);
            }
        }
        return sumAntall;
    }


    private void refreshDataIfNeeded() {
        long naaMs = System.currentTimeMillis();
        long alderMs = naaMs - antallLestTidspunktMs;
        if (alderMs >= MAX_DATA_ALDER_MS) {
            refreshData();
            antallLestTidspunktMs = System.currentTimeMillis();
        }
    }

    private void refreshData() {
        List<Object[]> rowList = metricRepository.tellAntallProsessTaskerPerTypeOgStatus();

        antallPrTypeOgStatus = new HashMap<>();
        for (Object[] row : rowList) {
            String type = (String) row[0]; //NOSONAR
            String status = (String) row[1]; //NOSONAR
            Tuple<String, String> typeOgStatus = new Tuple<>(type, status);
            BigDecimal antall = (BigDecimal) row[2]; //NOSONAR
            antallPrTypeOgStatus.put(typeOgStatus, antall);
        }

        List<Object[]> rowList2 = metricRepository.tellAntallProsessTaskerPerStatus();
        antallPrStatus = new HashMap<>();
        for (Object[] row : rowList2) {
            String status = (String) row[0]; //NOSONAR
            BigDecimal antall = (BigDecimal) row[1]; //NOSONAR
            antallPrStatus.put(status, antall);
        }

    }
}

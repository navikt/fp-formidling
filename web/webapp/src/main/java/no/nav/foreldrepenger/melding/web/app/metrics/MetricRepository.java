package no.nav.foreldrepenger.melding.web.app.metrics;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import no.nav.vedtak.felles.jpa.OracleVersionChecker;
import no.nav.vedtak.felles.jpa.VLPersistenceUnit;

@ApplicationScoped
public class MetricRepository {

    private EntityManager entityManager;
    private OracleVersionChecker oracleVersionChecker;

    MetricRepository() {
        // for CDI proxy
    }

    @Inject
    public MetricRepository(@VLPersistenceUnit EntityManager entityManager, OracleVersionChecker oracleVersionChecker) {
        Objects.requireNonNull(entityManager, "entityManager"); //$NON-NLS-1$
        Objects.requireNonNull(oracleVersionChecker, "oracleVersionChecker"); //$NON-NLS-1$
        this.entityManager = entityManager;
        this.oracleVersionChecker = oracleVersionChecker;
    }

    List<String> hentProsessTaskTyperMedPrefixer(List<String> prefixer) {
        Query query = entityManager.createNativeQuery("SELECT KODE FROM PROSESS_TASK_TYPE");
        @SuppressWarnings("unchecked")
        List<String> alleProsessTaskTyper = query.getResultList();

        List<String> ønskedeProsessTaskTyper = alleProsessTaskTyper.stream().
                filter(ptType -> stringHarEtAvPrefixer(ptType, prefixer)).
                collect(Collectors.toList());

        return ønskedeProsessTaskTyper;
    }

    /**
     * @return Liste av [type/String, status/String, antall/BigDecimal]
     * <p>
     * Tasks med status FERDIG telles ikke.
     */
    List<Object[]> tellAntallProsessTaskerPerTypeOgStatus() {
        final String queryTemplate =
                " select task_type, status, count(*) " +
                        "from %s " +
                        "group by task_type, status ";
        String queryStr;
        if (oracleVersionChecker.isRunningOnExpressEdition()) {
            // Express Edition mangler støtte for spørring på partition
            queryStr = String.format(queryTemplate, " (select * from prosess_task where status <> 'FERDIG') ");
        } else {
            queryStr =
                    String.format(queryTemplate, " prosess_task partition (STATUS_KLAR) ") +
                            " UNION " +
                            String.format(queryTemplate, " prosess_task partition (STATUS_FEILET) ");
        }
        Query query = entityManager.createNativeQuery(queryStr);
        @SuppressWarnings("unchecked")
        List<Object[]> rowList = query.getResultList();
        return rowList;
    }

    /**
     * @return Liste av [status/String, antall/BigDecimal]
     * <p>
     * Tasks med status FERDIG telles ikke.
     */
    List<Object[]> tellAntallProsessTaskerPerStatus() {
        final String queryTemplate =
                " select status, count(*) " +
                        "from %s " +
                        "group by status ";
        String queryStr;
        if (oracleVersionChecker.isRunningOnExpressEdition()) {
            // Express Edition mangler støtte for spørring på partition
            queryStr = String.format(queryTemplate, " (select * from prosess_task where status <> 'FERDIG') ");
        } else {
            queryStr =
                    String.format(queryTemplate, " prosess_task partition (STATUS_KLAR) ") +
                            " UNION " +
                            String.format(queryTemplate, " prosess_task partition (STATUS_FEILET) ");
        }
        Query query = entityManager.createNativeQuery(queryStr);
        @SuppressWarnings("unchecked")
        List<Object[]> rowList = query.getResultList();
        return rowList;
    }

    public Long tellAntallDokumentTypeIdForTaskType(String taskType, String dokumentTypeIdKode, LocalDate dag) {
        final Query query;
        if (dag != null) {
            query = entityManager.createNativeQuery("SELECT count(*) " +
                    "FROM PROSESS_TASK " +
                    "WHERE TASK_TYPE = :taskType " +
                    "AND TASK_PARAMETERE LIKE :dokTypeId " +
                    "AND OPPRETTET_TID > to_date( :idag , 'YYYY-MM-DD') " +
                    "AND  OPPRETTET_TID < to_date( :imorgen , 'YYYY-MM-DD')");
            query.setParameter("idag", dag.toString());
            query.setParameter("imorgen", dag.plusDays(1).toString());
        } else {
            query = entityManager.createNativeQuery("SELECT count(*) " +
                    "FROM PROSESS_TASK " +
                    "WHERE TASK_TYPE = :taskType " +
                    "AND TASK_PARAMETERE LIKE :dokTypeId");
        }
        query.setParameter("taskType", taskType);
        query.setParameter("dokTypeId", "%dokumentTypeId=" + dokumentTypeIdKode + "%");
        @SuppressWarnings("unchecked")
        List<BigDecimal> alleSvar = query.getResultList();
        return alleSvar.get(0).longValue();
    }


    private boolean stringHarEtAvPrefixer(String s, List<String> prefixer) {
        boolean res = prefixer.stream().
                anyMatch(prefix -> s.startsWith(prefix));
        return res;
    }
}

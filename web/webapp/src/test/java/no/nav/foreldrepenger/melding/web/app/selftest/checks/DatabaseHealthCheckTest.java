package no.nav.foreldrepenger.melding.web.app.selftest.checks;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import no.nav.foreldrepenger.melding.dbstoette.UnittestRepositoryRule;

@Ignore
public class DatabaseHealthCheckTest {
    @Rule
    public final UnittestRepositoryRule repositoryRule = new UnittestRepositoryRule();


    @Test
    public void test_check_healthy() {
        DatabaseHealthCheck dbCheck = new DatabaseHealthCheck();

        boolean result = dbCheck.isReady();

        assertThat(result).isTrue();
    }

    @Test
    public void skal_feile_pga_ukjent_jndi_name() {
        DatabaseHealthCheck dbCheck = new DatabaseHealthCheck("jndi/ukjent");

        boolean result = dbCheck.isReady();

        assertThat(result).isFalse();
    }

}

package no.nav.foreldrepenger.melding.feed.poller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Iterator;

import javax.enterprise.inject.Instance;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.qos.logback.classic.Level;
import no.nav.foreldrepenger.melding.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.melding.sikkerhet.LogSniffer;
import no.nav.vedtak.felles.testutilities.cdi.CdiRunner;

@RunWith(CdiRunner.class)
public class FeedPollerManagerTest {

    @Rule
    public LogSniffer logSniffer = new LogSniffer(Level.ALL);

    @Rule
    public UnittestRepositoryRule repoRule = new UnittestRepositoryRule();

    private FeedPollerManager manager;

    @Before
    public void setUp() {
        Instance<FeedPoller> feedPollers = mock(Instance.class);
        Iterator<FeedPoller> iterator = mock(Iterator.class);

        when(feedPollers.get()).thenReturn(new TestFeedPoller());
        when(feedPollers.iterator()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true, false);
        when(iterator.next()).thenReturn(new TestFeedPoller());
        manager = new FeedPollerManager(repoRule.getEntityManager(), feedPollers);
    }

    @After
    public void tearDown() {
        logSniffer.clearLog();
    }

    @Test
    public void skal_legge_til_poller() {
        manager.start();
        logSniffer.assertHasWarnMessage("Created thread for JSON feed polling FeedPollerManager-UnitTestPoller-poller");
        Assertions.assertThat(logSniffer.countEntries("Lagt til ny poller til pollingtjeneste. poller=UnitTestPoller, delayBetweenPollingMillis=500")).isEqualTo(1);
    }


    private class TestFeedPoller implements FeedPoller {

        @Override
        public String getName() {
            return "UnitTestPoller";
        }

        @Override
        public void poll() {
            System.out.println("FOO");

        }
    }
}

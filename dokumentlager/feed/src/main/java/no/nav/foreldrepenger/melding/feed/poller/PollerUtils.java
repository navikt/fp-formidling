package no.nav.foreldrepenger.melding.feed.poller;

import java.util.concurrent.ThreadFactory;

class PollerUtils {

    private PollerUtils() {
    }

    static class NamedThreadFactory implements ThreadFactory {
        private final String name;

        public NamedThreadFactory(String name) {
            this.name = name;
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, name);
        }
    }
}

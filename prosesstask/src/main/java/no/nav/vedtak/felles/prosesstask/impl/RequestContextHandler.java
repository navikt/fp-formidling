package no.nav.vedtak.felles.prosesstask.impl;

import java.util.function.Supplier;

import javax.enterprise.inject.spi.CDI;

import org.jboss.weld.context.RequestContext;
import org.jboss.weld.context.unbound.UnboundLiteral;

/**
 * Kjør angitt funksjon med RequestScope aktivt.
 */
final class RequestContextHandler {

    private RequestContextHandler() {
        // hidden ctor
    }

    public static <V> V doWithRequestContext(Supplier<V> supplier) {

        RequestContext requestContext = CDI.current().select(RequestContext.class, UnboundLiteral.INSTANCE).get();
        if (requestContext.isActive()) {
            return supplier.get();
        } else {

            try {
                requestContext.activate();
                return supplier.get();
            } finally {
                requestContext.invalidate();
                requestContext.deactivate();
            }
        }
    }

}

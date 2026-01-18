package no.nav.foreldrepenger.fpformidling.konfig;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import no.nav.vedtak.sikkerhet.kontekst.AnsattGruppe;
import no.nav.vedtak.sikkerhet.kontekst.KontekstHolder;
import no.nav.vedtak.sikkerhet.kontekst.RequestKontekst;

@Provider
@Priority(Priorities.AUTHORIZATION)
public class ForvaltningAuthorizationFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceinfo;

    public ForvaltningAuthorizationFilter() {
        // Ingenting
    }

    @Override
    public void filter(ContainerRequestContext req) {
        if (!KontekstHolder.harKontekst() || !KontekstHolder.getKontekst().erAutentisert() || !(KontekstHolder.getKontekst() instanceof RequestKontekst kontekst)) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }
        if (!kontekst.harGruppe(AnsattGruppe.DRIFT)) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        }

    }

}

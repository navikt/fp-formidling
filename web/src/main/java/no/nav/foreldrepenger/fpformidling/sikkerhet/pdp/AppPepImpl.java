package no.nav.foreldrepenger.fpformidling.sikkerhet.pdp;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Named;
import javax.interceptor.Interceptor;

import no.nav.foreldrepenger.sikkerhet.abac.LegacyTokenProvider;
import no.nav.vedtak.sikkerhet.abac.AbacAuditlogger;
import no.nav.vedtak.sikkerhet.abac.PdpKlient;
import no.nav.vedtak.sikkerhet.abac.PdpRequestBuilder;
import no.nav.vedtak.sikkerhet.abac.PepImpl;

@Default
@Alternative
@Priority(Interceptor.Priority.APPLICATION + 1)
public class AppPepImpl extends PepImpl {

    AppPepImpl() {
    }

    @Inject
    public AppPepImpl(@Named("pdp2") PdpKlient pdpKlient,
                      PdpRequestBuilder pdpRequestBuilder,
                      AbacAuditlogger sporingslogg) {
        super(null, pdpKlient, new LegacyTokenProvider() ,pdpRequestBuilder, sporingslogg, null);
    }

}

package no.nav.foreldrepenger.melding.web.server.jetty.sikkerhet;

import static javax.security.auth.message.AuthStatus.SUCCESS;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.servlet.http.HttpServletRequest;

import no.nav.vedtak.sikkerhet.jaspic.OidcAuthModule;

public class JettyOidcAuthModule extends OidcAuthModule {

    private static final Map<HttpServletRequest, HttpServletRequest> REQUEST_MAP = Collections.synchronizedMap(new IdentityHashMap<>());
    private static final String ORIGINAL_REQUEST_KEY = "originalRequest";

    /**
     * Jetty ignores the fact that a SAM can wrap the HTTP request/response objects so we need to put it in a map and retrieve it again from a Filter.
     *
     * @param request the request that we need the wrapped version of
     * @return the wrapped version of the <tt>request</tt>, or
     * <tt>null</tt> if there was no mapping for <tt>request</tt>.
     */
    private static HttpServletRequest getWrappedRequest(HttpServletRequest request) {
         /* Since we only need to get this once (in the filter), we remove it from the map to keep the map as small as
            possible */
        return REQUEST_MAP.remove(request);
    }

    @Override
    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject) throws AuthException {
        HttpServletRequest originalRequest = (HttpServletRequest) messageInfo.getRequestMessage();
        AuthStatus authStatus = super.validateRequest(messageInfo, clientSubject, serviceSubject);

        if (SUCCESS.equals(authStatus)) {
            exposeWrappedRequestToJetty(originalRequest, messageInfo);
        }

        return authStatus;
    }

    @Override
    public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {
        AuthStatus authStatus = super.secureResponse(messageInfo, serviceSubject);

        /* Make absolutely sure that the mapping is removed from the map */
        HttpServletRequest originalRequest = (HttpServletRequest) messageInfo.getMap().remove(ORIGINAL_REQUEST_KEY);
        if (originalRequest != null) {
            getWrappedRequest(originalRequest);
        }

        return authStatus;
    }

    /**
     * Must be called after the last call to @{@link MessageInfo#setRequestMessage(Object)}
     *
     * @param originalRequest the original request into the SAM
     * @param messageInfo     contains the final request from this SAM
     * @see JettyOidcAuthModule#getWrappedRequest(HttpServletRequest)
     */
    @SuppressWarnings("unchecked")
    private void exposeWrappedRequestToJetty(HttpServletRequest originalRequest, MessageInfo messageInfo) {
        HttpServletRequest finalRequest = (HttpServletRequest) messageInfo.getRequestMessage();
        REQUEST_MAP.put(originalRequest, finalRequest);
        messageInfo.getMap().put(ORIGINAL_REQUEST_KEY, originalRequest);
    }

}

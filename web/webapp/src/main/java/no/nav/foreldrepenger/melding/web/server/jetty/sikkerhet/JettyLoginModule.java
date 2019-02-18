package no.nav.foreldrepenger.melding.web.server.jetty.sikkerhet;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.jboss.weld.exceptions.IllegalArgumentException;

import no.nav.vedtak.sikkerhet.context.SubjectHandler;

public class JettyLoginModule implements LoginModule {
    private Subject subject;

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
    }

    @Override
    public boolean login() throws LoginException {
        return true;
    }

    @Override
    public boolean commit() throws LoginException {
        getSubjectHandler().setSubject(subject);
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        getSubjectHandler().removeSubject();
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        getSubjectHandler().removeSubject();
        return true;
    }

    private JettySubjectHandler getSubjectHandler() {
        SubjectHandler subjectHandler = SubjectHandler.getSubjectHandler();
        if (!(subjectHandler instanceof JettySubjectHandler)) {
            throw new IllegalArgumentException(JettyLoginModule.class.getSimpleName() + " krever subject handler av klasse "
                    + JettySubjectHandler.class + ", men fikk istedet: " + subjectHandler);
        }
        return (JettySubjectHandler) subjectHandler;
    }
}

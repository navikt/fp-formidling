package no.nav.foreldrepenger.melding.felles;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import no.nav.vedtak.sikkerhet.loginmodule.ContainerLogin;

@ApplicationScoped
public class ContainerLoginProducer {

    @Produces
    @ContainerLoginCDI
    public ContainerLogin getContainerLogin() {
        return new ContainerLogin();
    }

}

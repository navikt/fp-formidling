package no.nav.vedtak.felles.prosesstask.impl.feilhåndtering;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DefaultFeilhånderingsalgoritme extends SimpelFeilhåndteringsalgoritme {

    public DefaultFeilhånderingsalgoritme() {
        super(new BackoffFeilhåndteringStrategi());
    }

    @Override
    public String kode() {
        return "DEFAULT";
    }

}

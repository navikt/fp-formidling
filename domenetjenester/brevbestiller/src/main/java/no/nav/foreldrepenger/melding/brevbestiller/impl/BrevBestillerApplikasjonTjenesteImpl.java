package no.nav.foreldrepenger.melding.brevbestiller.impl;

import javax.enterprise.context.ApplicationScoped;

import no.nav.foreldrepenger.melding.brevbestiller.api.BrevBestillerApplikasjonTjeneste;

@ApplicationScoped
public class BrevBestillerApplikasjonTjenesteImpl implements BrevBestillerApplikasjonTjeneste {

    public BrevBestillerApplikasjonTjenesteImpl() {
        // for cdi proxy
    }

    @Override
    public byte[] forhandsvisBrev() {
        return new byte[0];
    }
}

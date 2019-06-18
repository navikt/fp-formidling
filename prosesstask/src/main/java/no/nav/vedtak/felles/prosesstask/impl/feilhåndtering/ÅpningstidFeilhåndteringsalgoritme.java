package no.nav.vedtak.felles.prosesstask.impl.feilhåndtering;

import javax.enterprise.context.ApplicationScoped;

/**
 * Algoritmen sjekker om neste forsøk er innenfor åpningstid. Hvis den er utenfor åpningstida, flyttes neste forsøk til den første timen etter åpningstid. Åpningstid regnes som hverdager mellom åpningstid og stengetid. *
 * Algoritmen tar inn to verdier En for åpningstid og stengetid.
 */
@ApplicationScoped
public class ÅpningstidFeilhåndteringsalgoritme extends SimpelFeilhåndteringsalgoritme {

    public ÅpningstidFeilhåndteringsalgoritme() {
        super(new ÅpningstidForsinkelseStrategi());
    }

    @Override
    public String kode() {
        return "ÅPNINGSTID";
    }

}

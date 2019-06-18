package no.nav.vedtak.felles.prosesstask.impl.feilhåndtering;

import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskTypeInfo;
import no.nav.vedtak.felles.prosesstask.spi.ForsinkelseStrategi;
import no.nav.vedtak.felles.prosesstask.spi.ProsessTaskFeilhåndteringAlgoritme;

public abstract class SimpelFeilhåndteringsalgoritme implements ProsessTaskFeilhåndteringAlgoritme {

    private ForsinkelseStrategi forsinkelseStrategi;

    protected SimpelFeilhåndteringsalgoritme(ForsinkelseStrategi forsinkelseStrategi) {
        this.forsinkelseStrategi = forsinkelseStrategi;
    }

    @Override
    public ForsinkelseStrategi getForsinkelseStrategi() {
        return forsinkelseStrategi;
    }

    @Override
    public boolean skalKjørePåNytt(ProsessTaskTypeInfo taskType, int antallFeilet, Exception exception) {
        return taskType.getMaksForsøk() > antallFeilet;
    }

    @Override
    public Feil hendelserNårIkkeKjøresPåNytt(Exception exception, ProsessTaskData prosessTaskData) {
        return null;
    }
}

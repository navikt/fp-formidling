package no.nav.vedtak.felles.prosesstask.spi;

public interface ForsinkelseStrategi {
    int sekunderTilNesteForsøk(int runde, ProsessTaskFeilHåndteringParametere feilhåndteringAlgoritme);
}

package no.nav.vedtak.felles.prosesstask.api;

public interface ProsessTaskHendelseMottak {

    void mottaHendelse(Long taskId, ProsessTaskHendelse hendelse);

}

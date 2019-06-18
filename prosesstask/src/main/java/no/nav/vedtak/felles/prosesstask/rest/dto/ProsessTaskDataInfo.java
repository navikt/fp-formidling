package no.nav.vedtak.felles.prosesstask.rest.dto;

import java.util.Optional;

import no.nav.vedtak.log.sporingslogg.Sporingsdata;

public interface ProsessTaskDataInfo {
    Optional<Sporingsdata> lagSporingsloggData();
}

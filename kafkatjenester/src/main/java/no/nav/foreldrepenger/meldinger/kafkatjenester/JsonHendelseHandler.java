package no.nav.foreldrepenger.meldinger.kafkatjenester;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.meldinger.kafkatjenester.jsondokumenthendelse.JsonDokumentHendelse;

@ApplicationScoped
public class JsonHendelseHandler {

    private static final Logger log = LoggerFactory.getLogger(JsonHendelseHandler.class);


    public JsonHendelseHandler() {
        //CDI
    }

    public void prosesser(JsonDokumentHendelse jsonHendelse) {
        log.info("Prossessert hendelse: {} for behandling: {} OK", jsonHendelse.getHendelse(), jsonHendelse.getBehandlingId());
    }

}

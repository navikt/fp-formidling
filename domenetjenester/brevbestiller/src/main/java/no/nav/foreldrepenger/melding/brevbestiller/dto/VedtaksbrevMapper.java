package no.nav.foreldrepenger.melding.brevbestiller.dto;

import no.nav.foreldrepenger.melding.vedtak.Vedtaksbrev;

public class VedtaksbrevMapper {

    public static Vedtaksbrev tilEntitet(no.nav.foreldrepenger.kontrakter.formidling.kodeverk.Vedtaksbrev vedtaksbrev) {
        if (vedtaksbrev == null) {
            return Vedtaksbrev.UDEFINERT;
        } else if (Vedtaksbrev.INGEN.getKode().equals(vedtaksbrev.getKode())) {
            return Vedtaksbrev.INGEN;
        } else if (Vedtaksbrev.AUTOMATISK.getKode().equals(vedtaksbrev.getKode())) {
            return Vedtaksbrev.AUTOMATISK;
        } else if (Vedtaksbrev.FRITEKST.getKode().equals(vedtaksbrev.getKode())) {
            return Vedtaksbrev.FRITEKST;
        } else {
            return Vedtaksbrev.UDEFINERT;
        }
    }
}

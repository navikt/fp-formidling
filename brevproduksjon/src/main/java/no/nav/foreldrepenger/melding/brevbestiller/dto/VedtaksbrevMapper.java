package no.nav.foreldrepenger.melding.brevbestiller.dto;

import no.nav.foreldrepenger.melding.vedtak.Vedtaksbrev;

class VedtaksbrevMapper {

    private VedtaksbrevMapper() {}

    public static Vedtaksbrev tilEntitet(no.nav.foreldrepenger.kontrakter.formidling.kodeverk.Vedtaksbrev vedtaksbrev) {
        if (vedtaksbrev == null) {
            return Vedtaksbrev.UDEFINERT;
        }
        if (Vedtaksbrev.INGEN.getKode().equals(vedtaksbrev.getKode())) {
            return Vedtaksbrev.INGEN;
        }
        if (Vedtaksbrev.AUTOMATISK.getKode().equals(vedtaksbrev.getKode())) {
            return Vedtaksbrev.AUTOMATISK;
        }
        if (Vedtaksbrev.FRITEKST.getKode().equals(vedtaksbrev.getKode())) {
            return Vedtaksbrev.FRITEKST;
        }
        return Vedtaksbrev.UDEFINERT;

    }
}

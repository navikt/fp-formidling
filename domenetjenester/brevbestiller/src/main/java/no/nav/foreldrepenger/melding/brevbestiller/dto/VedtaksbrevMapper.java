package no.nav.foreldrepenger.melding.brevbestiller.dto;

import no.nav.foreldrepenger.melding.vedtak.Vedtaksbrev;

public class VedtaksbrevMapper {

    public static no.nav.foreldrepenger.kontrakter.formidling.kodeverk.Vedtaksbrev fraEntitet(Vedtaksbrev vedtaksbrev) {
        if (Vedtaksbrev.INGEN.equals(vedtaksbrev)) {
            return no.nav.foreldrepenger.kontrakter.formidling.kodeverk.Vedtaksbrev.INGEN;
        } else if (Vedtaksbrev.AUTOMATISK.equals(vedtaksbrev)) {
            return no.nav.foreldrepenger.kontrakter.formidling.kodeverk.Vedtaksbrev.AUTOMATISK;
        } else if (Vedtaksbrev.FRITEKST.equals(vedtaksbrev)) {
            return no.nav.foreldrepenger.kontrakter.formidling.kodeverk.Vedtaksbrev.FRITEKST;
        } else {
            return no.nav.foreldrepenger.kontrakter.formidling.kodeverk.Vedtaksbrev.UDEFINERT;
        }
    }

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

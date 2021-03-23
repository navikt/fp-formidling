package no.nav.foreldrepenger.melding.brevbestiller;

import no.nav.vedtak.exception.TekniskException;

public class BrevbestillerFeil {

    public static TekniskException klarteIkkeÅForhåndsviseMal(String dokumentMal, String behandlingId) {
        return new TekniskException("FPFORMIDLING-221005", String.format("Klarte ikke hente forhåndvise mal %s for behandling %s.", dokumentMal, behandlingId));
    }

    public static TekniskException klarteIkkeForhåndvise(String dokumentMal, String behandlingId, Exception e) {
        return new TekniskException("FPFORMIDLING-221006", String.format("Klarte ikke hente forhåndvise mal %s for behandling %s.", dokumentMal, behandlingId), e);
    }

    public static TekniskException feilFraDokProd(Exception e) {
        return new TekniskException("FPFORMIDLING-210631", String.format("Feilmelding fra DokProd."), e);
    }

    public static TekniskException feilFraSak(Exception e) {
        return new TekniskException("FPFORMIDLING-210632", String.format("Feilmelding fra Sak."), e);
    }

}


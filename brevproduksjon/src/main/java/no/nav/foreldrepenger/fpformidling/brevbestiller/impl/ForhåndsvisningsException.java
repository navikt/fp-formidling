package no.nav.foreldrepenger.fpformidling.brevbestiller.impl;

import no.nav.vedtak.exception.FunksjonellException;

public class ForhåndsvisningsException extends FunksjonellException {

    public ForhåndsvisningsException(String kode, String message, String løsning) {
        super(kode, message, løsning);
    }

    public ForhåndsvisningsException(String kode, String message, String løsning, Throwable cause) {
        super(kode, message, løsning, cause);
    }
}

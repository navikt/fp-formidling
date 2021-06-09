package no.nav.foreldrepenger.melding.web.app.exceptions;

import no.nav.vedtak.exception.FunksjonellException;

import java.util.List;

public class FeltValideringFeil {

    static FunksjonellException feltverdiKanIkkeValideres(List<String> feltnavn) {
        return new FunksjonellException("FPFORMIDLING-328673", String.format("Det oppstod en valideringsfeil på felt %s. Vennligst kontroller at alle feltverdier er korrekte.", feltnavn), "Kontroller at alle feltverdier er korrekte");
    }
}

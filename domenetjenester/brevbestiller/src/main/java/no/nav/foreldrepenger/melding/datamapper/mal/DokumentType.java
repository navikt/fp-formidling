package no.nav.foreldrepenger.melding.datamapper.mal;

import java.util.List;

public interface DokumentType {
    String getDokumentMalType();

    List<Flettefelt> getFlettefelter(DokumentType dokumentType);
}

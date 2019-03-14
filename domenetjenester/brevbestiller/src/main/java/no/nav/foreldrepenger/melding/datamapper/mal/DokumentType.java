package no.nav.foreldrepenger.melding.datamapper.mal;

import java.util.List;

import no.nav.foreldrepenger.melding.aktør.PersonstatusType;

public interface DokumentType {
    String DEFAULT_PERSON_STATUS = "ANNET";
    String DOD_PERSON_STATUS = "DOD";

    String getDokumentMalType();

    List<Flettefelt> getFlettefelter(DokumentType dokumentType);

    default String getPersonstatusVerdi(@SuppressWarnings("unused") PersonstatusType personstatus) {
        return PersonstatusType.erDød(personstatus) ? DOD_PERSON_STATUS : DEFAULT_PERSON_STATUS;
    }
}

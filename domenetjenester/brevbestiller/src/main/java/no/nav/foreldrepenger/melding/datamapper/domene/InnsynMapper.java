package no.nav.foreldrepenger.melding.datamapper.domene;

import java.util.Objects;

import no.nav.foreldrepenger.melding.behandling.InnsynResultatType;
import no.nav.foreldrepenger.melding.datamapper.DokumentMapperFeil;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innsyn.InnsynResultatTypeKode;

public class InnsynMapper {

    public static InnsynResultatTypeKode mapInnsynResultatKode(String internResultatTypeKode) {
        if (Objects.equals(internResultatTypeKode, InnsynResultatType.INNVILGET.getKode())) {
            return InnsynResultatTypeKode.INNVILGET;
        } else if (Objects.equals(internResultatTypeKode, InnsynResultatType.DELVIS_INNVILGET.getKode())) {
            return InnsynResultatTypeKode.DELVISINNVILGET;
        } else if (Objects.equals(internResultatTypeKode, InnsynResultatType.AVVIST.getKode())) {
            return InnsynResultatTypeKode.AVVIST;
        }
        throw DokumentMapperFeil.FACTORY.innsynskravSvarHarUkjentResultatType(internResultatTypeKode).toException();
    }


}

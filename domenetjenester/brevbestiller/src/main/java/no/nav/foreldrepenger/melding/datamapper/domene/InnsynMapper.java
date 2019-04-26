package no.nav.foreldrepenger.melding.datamapper.domene;

import java.util.Objects;

import no.nav.foreldrepenger.melding.behandling.InnsynResultatType;
import no.nav.foreldrepenger.melding.datamapper.DokumentMapperFeil;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innsyn.InnsynResultatTypeKode;

public class InnsynMapper {

    public static InnsynResultatTypeKode mapInnsynResultatKode(InnsynResultatType internResultatTypeKode) {
        if (Objects.equals(internResultatTypeKode, InnsynResultatType.INNVILGET)) {
            return InnsynResultatTypeKode.INNVILGET;
        } else if (Objects.equals(internResultatTypeKode, InnsynResultatType.DELVIS_INNVILGET)) {
            return InnsynResultatTypeKode.DELVISINNVILGET;
        } else if (Objects.equals(internResultatTypeKode, InnsynResultatType.AVVIST)) {
            return InnsynResultatTypeKode.AVVIST;
        }
        throw DokumentMapperFeil.FACTORY.innsynskravSvarHarUkjentResultatType(internResultatTypeKode.getKode()).toException();
    }


}

package no.nav.foreldrepenger.melding.datamapper.domene;

import no.nav.foreldrepenger.melding.behandling.innsyn.InnsynResultatType;
import no.nav.foreldrepenger.melding.datamapper.DokumentMapperFeil;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innsyn.InnsynResultatTypeKode;

import java.util.Objects;

public class InnsynMapper {

    public static InnsynResultatTypeKode mapInnsynResultatKode(InnsynResultatType internResultatTypeKode) {
        if (Objects.equals(internResultatTypeKode, InnsynResultatType.INNVILGET)) {
            return InnsynResultatTypeKode.INNVILGET;
        } else if (Objects.equals(internResultatTypeKode, InnsynResultatType.DELVIS_INNVILGET)) {
            return InnsynResultatTypeKode.DELVISINNVILGET;
        } else if (Objects.equals(internResultatTypeKode, InnsynResultatType.AVVIST)) {
            return InnsynResultatTypeKode.AVVIST;
        }
        throw DokumentMapperFeil.innsynskravSvarHarUkjentResultatType(internResultatTypeKode.getKode());
    }


}

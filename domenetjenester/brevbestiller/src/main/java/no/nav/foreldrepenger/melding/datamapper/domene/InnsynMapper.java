package no.nav.foreldrepenger.melding.datamapper.domene;

import java.util.Objects;

import no.nav.foreldrepenger.melding.behandling.innsyn.InnsynResultatType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innsyn.InnsynResultatTypeKode;
import no.nav.vedtak.exception.TekniskException;

public class InnsynMapper {

    public static InnsynResultatTypeKode mapInnsynResultatKode(InnsynResultatType internResultatTypeKode) {
        if (Objects.equals(internResultatTypeKode, InnsynResultatType.INNVILGET)) {
            return InnsynResultatTypeKode.INNVILGET;
        }
        if (Objects.equals(internResultatTypeKode, InnsynResultatType.DELVIS_INNVILGET)) {
            return InnsynResultatTypeKode.DELVISINNVILGET;
        }
        if (Objects.equals(internResultatTypeKode, InnsynResultatType.AVVIST)) {
            return InnsynResultatTypeKode.AVVIST;
        }
        throw new TekniskException("FPFORMIDLING-729430", String.format("Ugyldig innsynsresultattype %s", internResultatTypeKode.getKode()));
    }

}

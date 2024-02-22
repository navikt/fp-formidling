package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FellesDokumentdata;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class FeilPraksisUtsettelseInfobrevDokumentdata extends Dokumentdata {

    public FeilPraksisUtsettelseInfobrevDokumentdata(FellesDokumentdata felles) {
        this.felles = felles;
    }
}

package no.nav.foreldrepenger.melding.behandling.repository;

import java.util.List;

import no.nav.foreldrepenger.melding.aktør.NavBrukerKjønn;
import no.nav.foreldrepenger.melding.aktør.PersonstatusType;
import no.nav.foreldrepenger.melding.behandling.personopplysning.SivilstandType;
import no.nav.foreldrepenger.melding.geografisk.Landkoder;
import no.nav.foreldrepenger.melding.geografisk.Region;

public interface PersonInfoKodeverkRepository {

    SivilstandType finnSivilstandType(String kode);

    Landkoder finnLandkode(String kode);

    Region finnHøyestRangertRegion(List<String> statsborgerskap);

    List<Region> finnRegioner(String kode);

    NavBrukerKjønn finnBrukerKjønn(String kode);

    PersonstatusType finnPersonstatus(String kode);
}

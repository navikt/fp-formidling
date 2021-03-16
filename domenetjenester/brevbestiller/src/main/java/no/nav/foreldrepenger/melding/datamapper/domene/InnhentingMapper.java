package no.nav.foreldrepenger.melding.datamapper.domene;

import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.datamapper.DokumentMapperFeil;
import no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innhentopplysninger.BehandlingsTypeKode;

import java.util.Objects;

public class InnhentingMapper {

    private InnhentingMapper() {
    }

    public static BehandlingsTypeKode mapToXmlBehandlingsType(String vlKode) {
        if (Objects.equals(vlKode, BehandlingType.FØRSTEGANGSSØKNAD.getKode())) {
            return BehandlingsTypeKode.FOERSTEGANGSBEHANDLING;
        } else if (Objects.equals(vlKode, BehandlingType.KLAGE.getKode())) {
            return BehandlingsTypeKode.KLAGE;
        } else if (Objects.equals(vlKode, BehandlingType.REVURDERING.getKode())) {
            return BehandlingsTypeKode.REVURDERING;
        } else if (Objects.equals(vlKode, BehandlingTypeKonstanter.ENDRINGSSØKNAD)) {
            return BehandlingsTypeKode.ENDRINGSSØKNAD;
        }
        throw DokumentMapperFeil.innhentDokumentasjonKreverGyldigBehandlingstype(vlKode);
    }
}

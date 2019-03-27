package no.nav.foreldrepenger.melding.datamapper.domene;

import java.util.Objects;

import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.datamapper.DokumentMapperFeil;
import no.nav.foreldrepenger.melding.datamapper.DokumentMapperKonstanter;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innhentopplysninger.BehandlingsTypeKode;

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
        } else if (Objects.equals(vlKode, DokumentMapperKonstanter.ENDRINGSSØKNAD)) {
            return BehandlingsTypeKode.ENDRINGSSØKNAD;
        }
        throw DokumentMapperFeil.FACTORY.innhentDokumentasjonKreverGyldigBehandlingstype(vlKode).toException();
    }
}

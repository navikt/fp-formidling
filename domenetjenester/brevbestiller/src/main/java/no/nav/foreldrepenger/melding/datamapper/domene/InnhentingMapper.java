package no.nav.foreldrepenger.melding.datamapper.domene;

import java.util.Objects;

import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innhentopplysninger.BehandlingsTypeKode;
import no.nav.vedtak.exception.TekniskException;

public class InnhentingMapper {

    private InnhentingMapper() {
    }

    public static BehandlingsTypeKode mapToXmlBehandlingsType(String vlKode) {
        if (Objects.equals(vlKode, BehandlingType.FØRSTEGANGSSØKNAD.getKode())) {
            return BehandlingsTypeKode.FOERSTEGANGSBEHANDLING;
        }
        if (Objects.equals(vlKode, BehandlingType.KLAGE.getKode())) {
            return BehandlingsTypeKode.KLAGE;
        }
        if (Objects.equals(vlKode, BehandlingType.REVURDERING.getKode())) {
            return BehandlingsTypeKode.REVURDERING;
        }
        if (Objects.equals(vlKode, BehandlingTypeKonstanter.ENDRINGSSØKNAD)) {
            return BehandlingsTypeKode.ENDRINGSSØKNAD;
        }
        throw new TekniskException("FPFORMIDLING-875839", String.format("Ugyldig behandlingstype %s for brev med malkode INNHEN.", vlKode));
    }
}

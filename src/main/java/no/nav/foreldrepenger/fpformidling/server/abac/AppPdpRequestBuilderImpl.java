package no.nav.foreldrepenger.fpformidling.server.abac;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.PdpRequestBuilder;
import no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType;
import no.nav.vedtak.sikkerhet.abac.pdp.AppRessursData;


/**
 * Implementasjon av PDP request for denne applikasjonen.
 */
@Dependent
public class AppPdpRequestBuilderImpl implements PdpRequestBuilder {

    public static final String ABAC_DOMAIN = "foreldrepenger";

    private final PipRestKlient pipRestKlient;

    @Inject
    public AppPdpRequestBuilderImpl(PipRestKlient pipRestKlient) {
        this.pipRestKlient = pipRestKlient;
    }

    @Override
    public AppRessursData lagAppRessursData(AbacDataAttributter dataAttributter) {
        Set<UUID> uuids = dataAttributter.getVerdier(StandardAbacAttributtType.BEHANDLING_UUID);
        var behandlingUuids = uuids.stream().distinct().toList();
        if (behandlingUuids.size() > 1) {
            throw new IllegalArgumentException("Støtter ikke request med to ulike behandlinger. Må utvides");
        }

        var appRessursData = AppRessursData.builder();
        behandlingUuids.stream().findFirst().ifPresent(b -> {
            var dto = pipRestKlient.hentPipdataForBehandling(b);
            appRessursData.leggTilAbacAktørIdSet(dto.aktørIder());
            Optional.ofNullable(dto.fagsakStatus()).ifPresent(appRessursData::medFagsakStatus);
            Optional.ofNullable(dto.behandlingStatus()).ifPresent(appRessursData::medBehandlingStatus);
        });
        return appRessursData.build();
    }
}


package no.nav.foreldrepenger.fpformidling.web.app.pdp;

import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.RESOURCE_FELLES_DOMENE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.RESOURCE_FELLES_RESOURCE_TYPE;
import static no.nav.vedtak.sikkerhet.abac.NavAbacCommonAttributter.XACML10_ACTION_ACTION_ID;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.sikkerhet.pdp.AppAbacAttributtType;
import no.nav.foreldrepenger.fpformidling.typer.AktørId;
import no.nav.vedtak.sikkerhet.abac.AbacAttributtSamling;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.PdpKlient;
import no.nav.vedtak.sikkerhet.abac.PdpRequest;
import no.nav.vedtak.sikkerhet.abac.PdpRequestBuilder;
import no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType;
import no.nav.vedtak.sikkerhet.abac.pdp.AppRessursData;
import no.nav.vedtak.sikkerhet.abac.pdp.BehandlingStatus;
import no.nav.vedtak.sikkerhet.abac.pdp.FagsakStatus;


/**
 * Implementasjon av PDP request for denne applikasjonen.
 */
@Dependent
public class AppPdpRequestBuilderImpl implements PdpRequestBuilder {

    public static final String ABAC_DOMAIN = "foreldrepenger";

    private final DomeneobjektProvider domeneobjektProvider;
    private final PipRestKlient pipRestKlient;

    @Inject
    public AppPdpRequestBuilderImpl(DomeneobjektProvider domeneobjektProvider,
                                 PipRestKlient pipRestKlient) {
        this.domeneobjektProvider = domeneobjektProvider;
        this.pipRestKlient = pipRestKlient;
    }

    @Override
    public PdpRequest lagPdpRequest(AbacAttributtSamling attributter) {
        var pdpRequest = new PdpRequest();
        Set<UUID> uuids = attributter.getVerdier(StandardAbacAttributtType.BEHANDLING_UUID);
        var behandlingUuids = uuids.stream().distinct().toList();
        if (behandlingUuids.size() > 1) {
            throw new IllegalArgumentException("Støtter ikke request med to ulike behandlinger. Må utvides");
        }
        var behandling = behandlingUuids.stream().findFirst().map(domeneobjektProvider::hentBehandling);

        List<String> aktørIder = new ArrayList<>();
        behandling.ifPresent(b -> {
                    var dto = pipRestKlient.hentPipdataForBehandling(b.getUuid().toString());
                    aktørIder.addAll(dto.aktørIder().stream().map(AktørId::getId).toList());
                    pdpRequest.put(AppAbacAttributtType.RESOURCE_FORELDREPENGER_SAK_SAKSSTATUS, dto.fagsakStatus());
                    pdpRequest.put(AppAbacAttributtType.RESOURCE_FORELDREPENGER_SAK_BEHANDLINGSSTATUS, dto.behandlingStatus());
                }
        );

        pdpRequest.put(PdpKlient.ENVIRONMENT_AUTH_TOKEN, attributter.getIdToken());
        pdpRequest.put(XACML10_ACTION_ACTION_ID, attributter.getActionType().getEksternKode());
        pdpRequest.put(RESOURCE_FELLES_DOMENE, ABAC_DOMAIN);
        pdpRequest.put(RESOURCE_FELLES_RESOURCE_TYPE, attributter.getResource());
        pdpRequest.put(RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE, aktørIder);
        return pdpRequest;
    }

    @Override
    public boolean nyttAbacGrensesnitt() {
        return true;
    }

    @Override
    public AppRessursData lagAppRessursData(AbacDataAttributter dataAttributter) {
        Set<UUID> uuids = dataAttributter.getVerdier(StandardAbacAttributtType.BEHANDLING_UUID);
        var behandlingUuids = uuids.stream().distinct().toList();
        if (behandlingUuids.size() > 1) {
            throw new IllegalArgumentException("Støtter ikke request med to ulike behandlinger. Må utvides");
        }

        var appRessursData = AppRessursData.builder();
        behandlingUuids.stream().findFirst()
                .map(domeneobjektProvider::hentBehandling).ifPresent(b -> {
                    var dto = pipRestKlient.hentPipdataForBehandling(b.getUuid().toString());
                    appRessursData.leggTilAktørIdSet(dto.aktørIder().stream().map(AktørId::getId).collect(Collectors.toSet()));
                    Optional.ofNullable(dto.fagsakStatus()).map(FagsakStatus::valueOf).ifPresent(appRessursData::medFagsakStatus);
                    Optional.ofNullable(dto.behandlingStatus()).map(BehandlingStatus::valueOf).ifPresent(appRessursData::medBehandlingStatus);
                }
        );
        return appRessursData.build();
    }
}


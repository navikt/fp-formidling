package no.nav.foreldrepenger.melding.web.app.pdp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import no.nav.abac.xacml.NavAttributter;
import no.nav.abac.xacml.StandardAttributter;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingStatus;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.fagsak.Fagsak;
import no.nav.foreldrepenger.melding.fagsak.FagsakStatus;
import no.nav.vedtak.sikkerhet.abac.AbacAttributtSamling;
import no.nav.vedtak.sikkerhet.abac.AbacBehandlingStatus;
import no.nav.vedtak.sikkerhet.abac.AbacFagsakStatus;
import no.nav.vedtak.sikkerhet.abac.PdpKlient;
import no.nav.vedtak.sikkerhet.abac.PdpRequest;
import no.nav.vedtak.sikkerhet.abac.PdpRequestBuilder;
import no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType;

/**
 * Implementasjon av PDP request for denne applikasjonen.
 */
@Dependent
@Alternative
@Priority(2)
public class PdpRequestBuilderImpl implements PdpRequestBuilder {

    public static final String ABAC_DOMAIN = "foreldrepenger";

    private DomeneobjektProvider domeneobjektProvider;

    @Inject
    public PdpRequestBuilderImpl(DomeneobjektProvider domeneobjektProvider) {
        this.domeneobjektProvider = domeneobjektProvider;
    }

    private static Optional<AbacBehandlingStatus> oversettBehandlingStatus(String kode) {
        if (kode.equals(BehandlingStatus.OPPRETTET.getKode())) {
            return Optional.of(AbacBehandlingStatus.OPPRETTET);
        } else if (kode.equals(BehandlingStatus.UTREDES.getKode())) {
            return Optional.of(AbacBehandlingStatus.UTREDES);
        } else if (kode.equals(BehandlingStatus.FATTER_VEDTAK.getKode())) {
            return Optional.of(AbacBehandlingStatus.FATTE_VEDTAK);
        } else {
            return Optional.empty();
        }
    }

    private static Optional<AbacFagsakStatus> oversettFagstatus(String kode) {
        if (kode.equals(FagsakStatus.OPPRETTET.getKode())) {
            return Optional.of(AbacFagsakStatus.OPPRETTET);
        } else if (kode.equals(FagsakStatus.UNDER_BEHANDLING.getKode())) {
            return Optional.of(AbacFagsakStatus.UNDER_BEHANDLING);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public PdpRequest lagPdpRequest(AbacAttributtSamling attributter) {
        PdpRequest pdpRequest = new PdpRequest();
        List<String> aktørIder = new ArrayList<>();

        Optional<Behandling> behandling = attributter.getVerdier(StandardAbacAttributtType.BEHANDLING_UUID).stream()
                .findFirst().map(UUID::fromString)
                .map(domeneobjektProvider::hentBehandling);
        behandling.map(Behandling::getFagsak).ifPresent(fagsak -> aktørIder.add(fagsak.getPersoninfo().getAktørId().getId()));
        pdpRequest.put(PdpKlient.ENVIRONMENT_AUTH_TOKEN, attributter.getIdToken());
        pdpRequest.put(StandardAttributter.ACTION_ID, attributter.getActionType().getEksternKode());
        pdpRequest.put(NavAttributter.RESOURCE_FELLES_DOMENE, ABAC_DOMAIN);
        pdpRequest.put(NavAttributter.RESOURCE_FELLES_RESOURCE_TYPE, attributter.getResource().getEksternKode());
        pdpRequest.put(NavAttributter.RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE, aktørIder);
        behandling.map(Behandling::getStatus).map(BehandlingStatus::getKode).map(PdpRequestBuilderImpl::oversettBehandlingStatus).map(Optional::get).ifPresent(
                status -> pdpRequest.put(NavAttributter.RESOURCE_FORELDREPENGER_SAK_BEHANDLINGSSTATUS, status.getEksternKode()));
        behandling.map(Behandling::getFagsak).map(Fagsak::getFagsakStatus).map(PdpRequestBuilderImpl::oversettFagstatus).map(Optional::get).ifPresent(
                status -> pdpRequest.put(NavAttributter.RESOURCE_FORELDREPENGER_SAK_SAKSSTATUS, status.getEksternKode()));
        pdpRequest.put(NavAttributter.RESOURCE_FORELDREPENGER_SAK_AKSJONSPUNKT_TYPE, Collections.emptySet()); //TODO
        return pdpRequest;
    }

}

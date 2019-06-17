package no.nav.foreldrepenger.melding.web.app.pdp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import no.nav.abac.common.xacml.CommonAttributter;
import no.nav.abac.foreldrepenger.xacml.ForeldrepengerAttributter;
import no.nav.abac.xacml.StandardAttributter;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingStatus;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.fagsak.FagsakStatus;
import no.nav.foreldrepenger.melding.typer.AktørId;
import no.nav.foreldrepenger.melding.web.app.pdp.dto.PipDto;
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
    private PipRestKlient pipRestKlient;

    @Inject
    public PdpRequestBuilderImpl(DomeneobjektProvider domeneobjektProvider,
                                 PipRestKlient pipRestKlient) {
        this.domeneobjektProvider = domeneobjektProvider;
        this.pipRestKlient = pipRestKlient;
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
        behandling.ifPresent(b -> {
                    PipDto dto = pipRestKlient.hentPipdataForBehandling(b.getUuid().toString());
                    aktørIder.addAll(dto.getAktørIder().stream().map(AktørId::getId).collect(Collectors.toList()));
                    pdpRequest.put(ForeldrepengerAttributter.RESOURCE_FORELDREPENGER_SAK_SAKSSTATUS, dto.getFagsakStatus());
                    pdpRequest.put(ForeldrepengerAttributter.RESOURCE_FORELDREPENGER_SAK_BEHANDLINGSSTATUS, dto.getBehandlingStatus());
                }
        );
        pdpRequest.put(PdpKlient.ENVIRONMENT_AUTH_TOKEN, attributter.getIdToken());
        pdpRequest.put(StandardAttributter.ACTION_ID, attributter.getActionType().getEksternKode());
        pdpRequest.put(CommonAttributter.RESOURCE_FELLES_DOMENE, ABAC_DOMAIN);
        pdpRequest.put(CommonAttributter.RESOURCE_FELLES_RESOURCE_TYPE, attributter.getResource().getEksternKode());
        pdpRequest.put(CommonAttributter.RESOURCE_FELLES_PERSON_AKTOERID_RESOURCE, aktørIder);
        return pdpRequest;
    }

}

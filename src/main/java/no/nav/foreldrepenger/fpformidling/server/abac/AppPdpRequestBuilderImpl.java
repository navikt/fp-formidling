package no.nav.foreldrepenger.fpformidling.server.abac;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.enterprise.context.Dependent;
import no.nav.vedtak.log.mdc.MdcExtendedLogContext;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.PdpRequestBuilder;
import no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType;
import no.nav.vedtak.sikkerhet.abac.pdp.AppRessursData;
import no.nav.vedtak.sikkerhet.abac.pipdata.PipBehandlingStatus;
import no.nav.vedtak.sikkerhet.abac.pipdata.PipFagsakStatus;


/**
 * Implementasjon av PDP request for denne applikasjonen.
 */
@Dependent
public class AppPdpRequestBuilderImpl implements PdpRequestBuilder {

    private static final MdcExtendedLogContext MDC_EXTENDED_LOG_CONTEXT = MdcExtendedLogContext.getContext("prosess");

    @Override
    public AppRessursData lagAppRessursData(AbacDataAttributter dataAttributter) {
        Set<String> saksnummer = new HashSet<>(dataAttributter.getVerdier(StandardAbacAttributtType.SAKSNUMMER));
        Set<UUID> uuids = dataAttributter.getVerdier(StandardAbacAttributtType.BEHANDLING_UUID);
        var behandlingUuids = new HashSet<>(uuids);
        if (behandlingUuids.size() > 1) {
            throw new IllegalArgumentException("Støtter ikke request med to ulike behandlinger. Må utvides");
        }
        setLogContext(saksnummer, behandlingUuids);

        var builder = minimalbuilder();
        behandlingUuids.stream().findFirst().ifPresent(builder::medBehandling);
        return builder.build();
    }

    @Override
    public AppRessursData lagAppRessursDataForSystembruker(AbacDataAttributter dataAttributter) {
        Set<String> saksnummer = new HashSet<>(dataAttributter.getVerdier(StandardAbacAttributtType.SAKSNUMMER));
        Set<UUID> uuids = dataAttributter.getVerdier(StandardAbacAttributtType.BEHANDLING_UUID);
        setLogContext(saksnummer, uuids);
        return minimalbuilder().build();
    }

    private static void setLogContext(Set<String> saksnummer, Set<UUID> behandlinger) {
        MDC_EXTENDED_LOG_CONTEXT.add("fagsak", saksnummer.stream().findFirst().orElse(null));
        MDC_EXTENDED_LOG_CONTEXT.add("behandling", behandlinger.stream().findFirst().map(UUID::toString).orElse(null));
    }

    private static AppRessursData.Builder minimalbuilder() {
        return AppRessursData.builder()
            .medFagsakStatus(PipFagsakStatus.UNDER_BEHANDLING)
            .medBehandlingStatus(PipBehandlingStatus.UTREDES);
    }
}


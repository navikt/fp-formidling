package no.nav.foreldrepenger.fpformidling.server.abac;

import static no.nav.vedtak.sikkerhet.abac.pdp.ForeldrepengerDataKeys.BEHANDLING_STATUS;
import static no.nav.vedtak.sikkerhet.abac.pdp.ForeldrepengerDataKeys.FAGSAK_STATUS;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
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

    public static final String ABAC_DOMAIN = "foreldrepenger";

    private final PipRestKlient pipRestKlient;

    @Inject
    public AppPdpRequestBuilderImpl(PipRestKlient pipRestKlient) {
        this.pipRestKlient = pipRestKlient;
    }

    @Override
    public AppRessursData lagAppRessursData(AbacDataAttributter dataAttributter) {
        Set<String> saksnummer = new HashSet<>(dataAttributter.getVerdier(StandardAbacAttributtType.SAKSNUMMER));
        MDC_EXTENDED_LOG_CONTEXT.add("fagsak", saksnummer.stream().findFirst().orElse(null));
        Set<UUID> uuids = dataAttributter.getVerdier(StandardAbacAttributtType.BEHANDLING_UUID);
        var behandlingUuids = uuids.stream().distinct().toList();
        if (behandlingUuids.size() > 1) {
            throw new IllegalArgumentException("Støtter ikke request med to ulike behandlinger. Må utvides");
        }

        var appRessursData = AppRessursData.builder();
        behandlingUuids.stream().findFirst().ifPresent(b -> {
            MDC_EXTENDED_LOG_CONTEXT.add("behandling", b);
            var dto = pipRestKlient.hentPipdataForBehandling(b);
            appRessursData.leggTilAbacAktørIdSet(dto)
                .leggTilRessurs(FAGSAK_STATUS, PipFagsakStatus.UNDER_BEHANDLING)
                .leggTilRessurs(BEHANDLING_STATUS, PipBehandlingStatus.UTREDES);
        });
        return appRessursData.build();
    }
}


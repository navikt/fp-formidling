package no.nav.foreldrepenger.melding.brevbestiller.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.aksjonspunkt.Aksjonspunkt;
import no.nav.foreldrepenger.melding.aksjonspunkt.AksjonspunktDefinisjon;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevbestiller.api.DokumentBehandlingTjeneste;
import no.nav.foreldrepenger.melding.brevbestiller.dto.BrevmalDto;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalRestriksjon;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;

@ApplicationScoped
public class DokumentBehandlingTjenesteImpl implements DokumentBehandlingTjeneste {
    private DokumentRepository dokumentRepository;
    private DomeneobjektProvider domeneobjektProvider;

    public DokumentBehandlingTjenesteImpl() {
        // for cdi proxy
    }

    @Inject
    public DokumentBehandlingTjenesteImpl(DokumentRepository dokumentRepository,
                                          DomeneobjektProvider domeneobjektProvider) {
        this.dokumentRepository = dokumentRepository;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public List<BrevmalDto> hentBrevmalerFor(UUID behandlingUuid) {
        Behandling behandling = domeneobjektProvider.hentBehandling(behandlingUuid);
        final List<Aksjonspunkt> aksjonspunkter = domeneobjektProvider.hentAksjonspunkter(behandling);

        List<DokumentMalType> kandidater = new ArrayList<>(dokumentRepository.hentAlleDokumentMalTyper());
        List<DokumentMalType> fjernes = filtrerUtilgjengeligBrevmaler(behandling, kandidater, automatiskOpprettet(behandling), aksjonspunkter);
        kandidater.removeAll(fjernes);
        return tilBrevmalDto(behandling, sorterte(kandidater));
    }

    private boolean automatiskOpprettet(Behandling behandling) {
        return !behandling.erManueltOpprettet();
    }

    private List<DokumentMalType> sorterte(List<DokumentMalType> kandidater) {
        List<DokumentMalType> sorterte = new ArrayList<>();
        kandidater.stream()
                .filter(dm -> DokumentMalRestriksjon.INGEN.equals(dm.getDokumentMalRestriksjon()))
                .forEach(sorterte::add);
        kandidater.stream()
                .filter(dm -> !(DokumentMalRestriksjon.INGEN.equals(dm.getDokumentMalRestriksjon())))
                .forEach(sorterte::add);
        return sorterte;
    }

    //TODO - Denne gjør ingenting?
    @Override
    public boolean erDokumentProdusert(Long behandlingId, String dokumentMalTypeKode) {
        return new SjekkDokumentTilgjengelig(dokumentRepository)
                .erDokumentProdusert(behandlingId, dokumentMalTypeKode);
    }

    // Fjerner dokumentmaler som aldri er relevante for denne behandlingstypen
    private List<DokumentMalType> filtrerUtilgjengeligBrevmaler(Behandling behandling, List<DokumentMalType> kandidater, boolean automatiskOpprettet, List<Aksjonspunkt> aksjonspunkter) {
        List<DokumentMalType> fjernes = kandidater.stream()
                .filter(dm -> !dm.erTilgjengeligForManuellUtsendelse())
                .collect(Collectors.toList());
        if (harBehandledeAksjonspunktVarselOmRevurdering(aksjonspunkter)) {
            fjernes.add(dokumentRepository.hentDokumentMalType(DokumentMalType.FORLENGET_DOK));
            fjernes.add(dokumentRepository.hentDokumentMalType(DokumentMalType.FORLENGET_MEDL_DOK));
            fjernes.add(dokumentRepository.hentDokumentMalType(DokumentMalType.REVURDERING_DOK));
        } else if (behandling.erKlage()) {
            fjernes.add(dokumentRepository.hentDokumentMalType(DokumentMalType.FORLENGET_MEDL_DOK));
            fjernes.add(dokumentRepository.hentDokumentMalType(DokumentMalType.REVURDERING_DOK));
        } else if (behandling.erRevurdering()) {
            if (!automatiskOpprettet) {
                fjernes.add(dokumentRepository.hentDokumentMalType(DokumentMalType.FORLENGET_DOK));
                fjernes.add(dokumentRepository.hentDokumentMalType(DokumentMalType.FORLENGET_MEDL_DOK));
            }
        } else {
            fjernes.add(dokumentRepository.hentDokumentMalType(DokumentMalType.REVURDERING_DOK));
        }
        return fjernes;
    }

    private static boolean harBehandledeAksjonspunktVarselOmRevurdering(List<Aksjonspunkt> aksjonspunkter) {
        return aksjonspunkter.stream()
                .anyMatch(ap -> ap.getAksjonspunktDefinisjon().equals(AksjonspunktDefinisjon.VARSEL_REVURDERING_ETTERKONTROLL) ||
                        ap.getAksjonspunktDefinisjon().equals(AksjonspunktDefinisjon.VARSEL_REVURDERING_MANUELL));
    }

    // Markerer som ikke tilgjengelige de brevmaler som ikke er aktuelle i denne behandlingen
    private List<BrevmalDto> tilBrevmalDto(Behandling behandling, List<DokumentMalType> dmtList) {
        List<BrevmalDto> brevmalDtoList = new ArrayList<>(dmtList.size());
        for (DokumentMalType dmt : dmtList) {
            boolean tilgjengelig = new SjekkDokumentTilgjengelig(dokumentRepository).sjekkOmTilgjengelig(behandling, dmt);
            brevmalDtoList.add(new BrevmalDto(dmt.getKode(), dmt.getNavn(), dmt.getDokumentMalRestriksjon(), tilgjengelig));
        }
        return brevmalDtoList;
    }
}

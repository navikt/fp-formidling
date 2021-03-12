package no.nav.foreldrepenger.melding.brevbestiller.impl;

import static no.nav.foreldrepenger.melding.brevbestiller.impl.DokgenLanseringTjeneste.malSkalIkkeTilgjengeliggjøresForManuellUtsendelse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.kontrakter.formidling.v1.BrevmalDto;
import no.nav.foreldrepenger.melding.aksjonspunkt.Aksjonspunkt;
import no.nav.foreldrepenger.melding.aksjonspunkt.AksjonspunktDefinisjon;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevbestiller.api.DokumentBehandlingTjeneste;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalRestriksjon;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;

@ApplicationScoped
public class DokumentBehandlingTjenesteImpl implements DokumentBehandlingTjeneste {
    private DomeneobjektProvider domeneobjektProvider;
    private SjekkDokumentTilgjengelig sjekkDokumentTilgjengelig;

    public DokumentBehandlingTjenesteImpl() {
        // for cdi proxy
    }

    @Inject
    public DokumentBehandlingTjenesteImpl(DomeneobjektProvider domeneobjektProvider,
                                          SjekkDokumentTilgjengelig sjekkDokumentTilgjengelig) {
        this.domeneobjektProvider = domeneobjektProvider;
        this.sjekkDokumentTilgjengelig = sjekkDokumentTilgjengelig;
    }

    @Override
    public List<BrevmalDto> hentBrevmalerFor(UUID behandlingUuid) {
        Behandling behandling = domeneobjektProvider.hentBehandling(behandlingUuid);
        final List<Aksjonspunkt> aksjonspunkter = domeneobjektProvider.hentAksjonspunkter(behandling);

        List<DokumentMalType> kandidater = new ArrayList<>(DokumentMalType.hentAlleGyldige());
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
                .filter(dm -> DokumentMalRestriksjon.INGEN.equals(dm.getDokumentmalRestriksjon()))
                .forEach(sorterte::add);
        kandidater.stream()
                .filter(dm -> !(DokumentMalRestriksjon.INGEN.equals(dm.getDokumentmalRestriksjon())))
                .forEach(sorterte::add);
        return sorterte;
    }

    // Fjerner dokumentmaler som ikke er tilgjengelig for manuell utsendelse, og for ulike behandlingstyper
    //Todo Dokumentmaltype Etterlys inntekstmelding skal kun være tilgjengelig for manuell utsendelse når foreldrepenger - men vi vet ikke at en behandling er FP, SVP, eller ES
    private List<DokumentMalType> filtrerUtilgjengeligBrevmaler(Behandling behandling, List<DokumentMalType> kandidater, boolean automatiskOpprettet, List<Aksjonspunkt> aksjonspunkter) {
        List<DokumentMalType> fjernes = kandidater.stream()
                .filter(dm -> !dm.erTilgjengeligForManuellUtsendelse() || malSkalIkkeTilgjengeliggjøresForManuellUtsendelse(dm))
                .collect(Collectors.toList());
        if (harAksjonspunktVarselOmRevurdering(aksjonspunkter)) {
            fjernes.add(DokumentMalType.FORLENGET_DOK);
            fjernes.add(DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID);
            fjernes.add(DokumentMalType.FORLENGET_MEDL_DOK);
            fjernes.add(DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_MEDL);
            fjernes.add(DokumentMalType.REVURDERING_DOK);
            fjernes.add(DokumentMalType.VARSEL_OM_REVURDERING);
        } else if (behandling.erKlage()) {
            fjernes.add(DokumentMalType.FORLENGET_MEDL_DOK);
            fjernes.add(DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_MEDL);
            fjernes.add(DokumentMalType.REVURDERING_DOK);
            fjernes.add(DokumentMalType.VARSEL_OM_REVURDERING);
        } else if (behandling.erRevurdering()) {
            if (!automatiskOpprettet) {
                fjernes.add(DokumentMalType.FORLENGET_DOK);
                fjernes.add(DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID);
                fjernes.add(DokumentMalType.FORLENGET_MEDL_DOK);
                fjernes.add(DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_MEDL);
            }
        } else {
            fjernes.add(DokumentMalType.REVURDERING_DOK);
            fjernes.add(DokumentMalType.VARSEL_OM_REVURDERING);
        }
        return fjernes;
    }

    private static boolean harAksjonspunktVarselOmRevurdering(List<Aksjonspunkt> aksjonspunkter) {
        return aksjonspunkter.stream()
                .anyMatch(ap -> ap.getAksjonspunktDefinisjon().equals(AksjonspunktDefinisjon.VARSEL_REVURDERING_ETTERKONTROLL) ||
                        ap.getAksjonspunktDefinisjon().equals(AksjonspunktDefinisjon.VARSEL_REVURDERING_MANUELL));
    }

    // Markerer som ikke tilgjengelige de brevmaler som ikke er aktuelle i denne behandlingen
    private List<BrevmalDto> tilBrevmalDto(Behandling behandling, List<DokumentMalType> dmtList) {
        List<BrevmalDto> brevmalDtoList = new ArrayList<>(dmtList.size());
        for (DokumentMalType dmt : dmtList) {
            boolean tilgjengelig = sjekkDokumentTilgjengelig.sjekkOmTilgjengelig(behandling, dmt);
            brevmalDtoList.add(new BrevmalDto(dmt.getKode(), dmt.getNavn(), mapKontraktDokumentMalRestriksjon(dmt.getDokumentmalRestriksjon()), tilgjengelig));
        }
        return brevmalDtoList;
    }

    private no.nav.foreldrepenger.kontrakter.formidling.kodeverk.DokumentMalRestriksjon mapKontraktDokumentMalRestriksjon(DokumentMalRestriksjon dokumentMalRestriksjon) {
        if (no.nav.foreldrepenger.kontrakter.formidling.kodeverk.DokumentMalRestriksjon.REVURDERING.getKode().equals(dokumentMalRestriksjon.getKode())) {
            return no.nav.foreldrepenger.kontrakter.formidling.kodeverk.DokumentMalRestriksjon.REVURDERING;
        } else if (no.nav.foreldrepenger.kontrakter.formidling.kodeverk.DokumentMalRestriksjon.ÅPEN_BEHANDLING.getKode().equals(dokumentMalRestriksjon.getKode())) {
            return no.nav.foreldrepenger.kontrakter.formidling.kodeverk.DokumentMalRestriksjon.ÅPEN_BEHANDLING;
        } else if (no.nav.foreldrepenger.kontrakter.formidling.kodeverk.DokumentMalRestriksjon.ÅPEN_BEHANDLING_IKKE_SENDT.getKode().equals(dokumentMalRestriksjon.getKode())) {
            return no.nav.foreldrepenger.kontrakter.formidling.kodeverk.DokumentMalRestriksjon.ÅPEN_BEHANDLING_IKKE_SENDT;
        } else {
            return no.nav.foreldrepenger.kontrakter.formidling.kodeverk.DokumentMalRestriksjon.INGEN;
        }
    }
}

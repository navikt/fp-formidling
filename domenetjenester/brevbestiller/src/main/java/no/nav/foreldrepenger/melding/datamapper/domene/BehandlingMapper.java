package no.nav.foreldrepenger.melding.datamapper.domene;

import static no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter.ENDRINGSSØKNAD;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.BehandlingRestKlient;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingIdDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingÅrsakDto;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.BehandlingÅrsak;
import no.nav.foreldrepenger.melding.behandling.BehandlingÅrsakType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.avslag.BehandlingstypeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.BehandlingsTypeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.BehandlingsTypeKode;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.vedtak.util.StringUtils;

@ApplicationScoped
public class BehandlingMapper {
    private KodeverkRepository kodeverkRepository;
    private BehandlingRestKlient behandlingRestKlient;
    private BehandlingsresultatMapper behandlingsresultatMapper;

    public BehandlingMapper() {
        //CDI
    }

    @Inject
    public BehandlingMapper(KodeverkRepository kodeverkRepository,
                            BehandlingRestKlient behandlingRestKlient,
                            BehandlingsresultatMapper behandlingsresultatMapper) {
        this.kodeverkRepository = kodeverkRepository;
        this.behandlingRestKlient = behandlingRestKlient;
        this.behandlingsresultatMapper = behandlingsresultatMapper;
    }

    public Behandling hentBehandling(long behandlingId) {
        return mapBehandlingFraDto(behandlingRestKlient.hentBehandling(new BehandlingIdDto(behandlingId)));
    }

    public Behandling mapBehandlingFraDto(BehandlingDto dto) {
        Behandling.Builder builder = Behandling.builder();
        builder.medId(dto.getId())
                .medBehandlingType(finnBehandlingType(dto.getType().getKode()))
                .medOpprettetDato(dto.getOpprettet())
                .medOriginalBehandling(dto.getOriginalBehandlingId())
                .medAnsvarligSaksbehandler(dto.getAnsvarligSaksbehandler())
                .medToTrinnsBehandling(dto.getToTrinnsBehandling())
                .medBehandlendeEnhetNavn(dto.getBehandlendeEnhetNavn())
                .medSaksnummer(dto.getFagsakId())
                .medBehandlingÅrsaker(mapBehandlingÅrsakListe(dto.getBehandlingArsaker()));

        dto.getLinks().forEach(builder::leggTilResourceLink);

        if (dto.getBehandlingsresultat() != null) {
            builder.medBehandlingsresultat(behandlingsresultatMapper.mapBehandlingsresultatFraDto(dto.getBehandlingsresultat()));
        }

        return builder.build();
    }

    private List<BehandlingÅrsak> mapBehandlingÅrsakListe(List<BehandlingÅrsakDto> behandlingÅrsakDtoer) {
        if (!behandlingÅrsakDtoer.isEmpty()) {
            return behandlingÅrsakDtoer.stream()
                    .map(this::mapBehandlingÅrsakFraDto)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private BehandlingÅrsak mapBehandlingÅrsakFraDto(BehandlingÅrsakDto dto) {
        return BehandlingÅrsak.builder()
                .medBehandlingÅrsakType(kodeverkRepository.finn(BehandlingÅrsakType.class, dto.getBehandlingArsakType().getKode()))
                .medManueltOpprettet(dto.getManueltOpprettet())
                .build();
    }

    public static Optional<String> avklarFritekst(DokumentHendelse dokumentHendelse, Behandling behandling) {
        if (!StringUtils.nullOrEmpty(dokumentHendelse.getFritekst())) {
            return Optional.of(dokumentHendelse.getFritekst());
        } else if (behandling.getBehandlingsresultat() != null &&
                !StringUtils.nullOrEmpty(behandling.getBehandlingsresultat().getAvslagarsakFritekst())) {
            return Optional.of(behandling.getBehandlingsresultat().getAvslagarsakFritekst());
        }
        return Optional.empty();
    }

    private BehandlingType finnBehandlingType(String behandlingType) {
        return kodeverkRepository.finn(BehandlingType.class, behandlingType);
    }

    public int finnAntallUkerBehandlingsfrist(BehandlingType behandlingType) {
        return behandlingType.getBehandlingstidFristUker();
    }

    public String finnBehandlingTypeForDokument(Behandling behandling) {
        return gjelderEndringsøknad(behandling) ?
                ENDRINGSSØKNAD :
                behandling.getBehandlingType().getKode();
    }

    static boolean gjelderEndringsøknad(Behandling behandling) {
        return getBehandlingÅrsakStringListe(behandling)
                .contains(BehandlingÅrsakType.RE_ENDRING_FRA_BRUKER);
    }

    public static boolean erRevurderingPgaFødselshendelse(Behandling behandling) {
        return getBehandlingÅrsakStringListe(behandling)
                .contains(BehandlingÅrsakType.RE_HENDELSE_FØDSEL);
    }

    static List<BehandlingÅrsakType> getBehandlingÅrsakStringListe(Behandling behandling) {
        return behandling.getBehandlingÅrsaker().stream()
                .map(BehandlingÅrsak::getBehandlingÅrsakType)
                .collect(Collectors.toList());
    }

    public BehandlingsTypeKode utledBehandlingsTypeInnvilgetFP(Behandling behandling) {
        return BehandlingType.REVURDERING.equals(behandling.getBehandlingType()) ?
                BehandlingsTypeKode.REVURDERING : BehandlingsTypeKode.FOERSTEGANGSBEHANDLING;
    }

    public BehandlingsTypeType utledBehandlingsTypeInnvilgetES(Behandling behandling) {
        Stream<BehandlingÅrsakType> årsaker = behandling.getBehandlingÅrsaker().stream()
                .map(BehandlingÅrsak::getBehandlingÅrsakType).map(kode -> kodeverkRepository.finn(BehandlingÅrsakType.class, kode));
        boolean etterKlage = årsaker.anyMatch(BehandlingÅrsakType.årsakerEtterKlageBehandling()::contains);
        if (etterKlage) {
            return BehandlingsTypeType.MEDHOLD;
        }
        return BehandlingType.REVURDERING.equals(behandling.getBehandlingType()) ? BehandlingsTypeType.REVURDERING : BehandlingsTypeType.FOERSTEGANGSBEHANDLING;
    }

    public BehandlingstypeType utledBehandlingsTypeAvslagES(Behandling behandling) {
        boolean erRevurdering = BehandlingType.REVURDERING.equals(behandling.getBehandlingType());
        return erRevurdering ? BehandlingstypeType.REVURDERING : BehandlingstypeType.SØKNAD;
    }
}

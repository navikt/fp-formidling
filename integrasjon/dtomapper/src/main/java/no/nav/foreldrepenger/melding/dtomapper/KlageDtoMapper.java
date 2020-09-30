package no.nav.foreldrepenger.melding.dtomapper;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.dto.klage.KlageFormkravResultatDto;
import no.nav.foreldrepenger.fpsak.dto.klage.KlageVurderingResultatDto;
import no.nav.foreldrepenger.fpsak.dto.klage.KlagebehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.klage.MottattKlagedokumentDto;
import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.klage.KlageAvvistÅrsak;
import no.nav.foreldrepenger.melding.klage.KlageDokument;
import no.nav.foreldrepenger.melding.klage.KlageFormkravResultat;
import no.nav.foreldrepenger.melding.klage.KlageVurdering;
import no.nav.foreldrepenger.melding.klage.KlageVurderingResultat;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;

@ApplicationScoped
public class KlageDtoMapper {

    private KodeverkRepository kodeverkRepository;

    @Inject
    public KlageDtoMapper(KodeverkRepository kodeverkRepository) {
        this.kodeverkRepository = kodeverkRepository;
    }

    public KlageDtoMapper() {
        //CDI
    }

    public KlageDokument mapKlagedokumentFraDto(MottattKlagedokumentDto dto) {
        return new KlageDokument(dto.getMottattDato());
    }

    public Klage mapKlagefraDto(KlagebehandlingDto dto) {
        Klage.Builder builder = Klage.ny();
        if (dto.getKlageFormkravResultatNFP() != null) {
            builder.medFormkravNFP(mapKlageFormkravResultatfraDto(dto.getKlageFormkravResultatNFP()));
            leggTilPåklagdBehandlingType(builder, dto.getKlageFormkravResultatNFP());
        }
        if (dto.getKlageFormkravResultatKA() != null) {
            builder.medFormkravKA(mapKlageFormkravResultatfraDto(dto.getKlageFormkravResultatKA()));
            leggTilPåklagdBehandlingType(builder, dto.getKlageFormkravResultatKA());
        }
        if (dto.getKlageVurderingResultatNFP() != null) {
            builder.medKlageVurderingResultatNFP(mapKlageVurderingResultatfraDto(dto.getKlageVurderingResultatNFP()));
        }
        if (dto.getKlageVurderingResultatNK() != null) {
            builder.medKlageVurderingResultatNK(mapKlageVurderingResultatfraDto(dto.getKlageVurderingResultatNK()));
        }
        return builder.build();
    }

    private void leggTilPåklagdBehandlingType(Klage.Builder builder, KlageFormkravResultatDto klageFormkravResultat) {
        KodeDto paklagdBehandlingType = klageFormkravResultat.getPaklagdBehandlingType();
        if (paklagdBehandlingType != null) {
            builder.medPåklagdBehandlingType(finnBehandlingType(paklagdBehandlingType.getKode()));
        }
    }


    private KlageFormkravResultat mapKlageFormkravResultatfraDto(KlageFormkravResultatDto dto) {
        List<KlageAvvistÅrsak> avvistÅrsaker = dto.getAvvistArsaker().stream()
                .map(KodeDto::getKode)
                .map(KlageAvvistÅrsak::fraKode)
                .collect(Collectors.toList());
        return new KlageFormkravResultat(avvistÅrsaker);
    }

    private KlageVurderingResultat mapKlageVurderingResultatfraDto(KlageVurderingResultatDto dto) {
        return KlageVurderingResultat.ny()
                .medKlageVurdering(KlageVurdering.fraKode(dto.getKlageVurdering().getKode()))
                .medFritekstTilbrev(dto.getFritekstTilBrev())
                .build();
    }

    private BehandlingType finnBehandlingType(String behandlingType) {
        return kodeverkRepository.finn(BehandlingType.class, behandlingType);
    }
}

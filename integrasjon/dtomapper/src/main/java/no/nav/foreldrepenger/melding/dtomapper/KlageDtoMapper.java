package no.nav.foreldrepenger.melding.dtomapper;

import java.util.ArrayList;
import java.util.List;

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
        KlageFormkravResultat.Builder builder = KlageFormkravResultat.ny();
        builder.medBegrunnelse(dto.getBegrunnelse());
        List<KlageAvvistÅrsak> avvistÅrsaker = new ArrayList<>();
        dto.getAvvistArsaker().forEach(årsak -> avvistÅrsaker.add(KlageAvvistÅrsak.fraKode(årsak.getKode())));
        builder.medAvvistÅrsaker(avvistÅrsaker);
        return builder.build();
    }

    private KlageVurderingResultat mapKlageVurderingResultatfraDto(KlageVurderingResultatDto dto) {
        KlageVurderingResultat.Builder builder = KlageVurderingResultat.ny();
        builder.medKlageVurdering(KlageVurdering.fraKode(dto.getKlageVurdering()));
        builder.medFritekstTilbrev(dto.getFritekstTilBrev());
        return builder.build();
    }

    private BehandlingType finnBehandlingType(String behandlingType) {
        return kodeverkRepository.finn(BehandlingType.class, behandlingType);
    }
}

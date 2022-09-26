package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import no.nav.foreldrepenger.fpformidling.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.klage.KlageFormkravResultatDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.klage.KlageVurderingResultatDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.klage.KlagebehandlingDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.klage.MottattKlagedokumentDto;
import no.nav.foreldrepenger.fpformidling.klage.Klage;
import no.nav.foreldrepenger.fpformidling.klage.KlageDokument;
import no.nav.foreldrepenger.fpformidling.klage.KlageFormkravResultat;
import no.nav.foreldrepenger.fpformidling.klage.KlageVurderingResultat;

public class KlageDtoMapper {

    public static KlageDokument mapKlagedokumentFraDto(MottattKlagedokumentDto dto) {
        return new KlageDokument(dto.getMottattDato());
    }

    public static Klage mapKlagefraDto(KlagebehandlingDto dto) {
        var builder = Klage.ny();
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

    private static void leggTilPåklagdBehandlingType(Klage.Builder builder, KlageFormkravResultatDto klageFormkravResultat) {
        var paklagdBehandlingType = klageFormkravResultat.getPaklagdBehandlingType();
        if (paklagdBehandlingType != null) {
            builder.medPåklagdBehandlingType(paklagdBehandlingType);
        }
    }

    private static KlageFormkravResultat mapKlageFormkravResultatfraDto(KlageFormkravResultatDto dto) {
        return new KlageFormkravResultat(dto.getAvvistArsaker());
    }

    private static KlageVurderingResultat mapKlageVurderingResultatfraDto(KlageVurderingResultatDto dto) {
        return new KlageVurderingResultat(dto.getKlageVurdering(), dto.getFritekstTilBrev());
    }
}

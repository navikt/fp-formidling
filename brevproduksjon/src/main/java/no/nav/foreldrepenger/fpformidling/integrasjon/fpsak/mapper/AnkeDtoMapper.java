package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import java.util.Optional;

import no.nav.foreldrepenger.fpformidling.anke.Anke;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.anke.AnkebehandlingDto;

public class AnkeDtoMapper {

    public static Optional<Anke> mapAnkeFraDto(AnkebehandlingDto dto) {

        if (dto.getAnkeVurderingResultat() == null) {
            return Optional.empty();
        }

        var ankeResultat = dto.getAnkeVurderingResultat();

        var builder = Anke.ny()
                .medAnkeVurdering(ankeResultat.getAnkeVurdering())
                .medFritekstTilBrev(ankeResultat.getFritekstTilBrev())
                .medAnkeVurderingOmgjoer(ankeResultat.getAnkeVurderingOmgjoer())
                .medPaAnketBehandlingUuid(ankeResultat.getPåAnketKlageBehandlingUuid());

        return Optional.of(builder.build());
    }
}

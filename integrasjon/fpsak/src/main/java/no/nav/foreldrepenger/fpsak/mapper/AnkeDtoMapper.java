package no.nav.foreldrepenger.fpsak.mapper;

import java.util.Optional;

import no.nav.foreldrepenger.fpsak.dto.anke.AnkeVurderingResultatDto;
import no.nav.foreldrepenger.fpsak.dto.anke.AnkebehandlingDto;
import no.nav.foreldrepenger.melding.anke.Anke;
import no.nav.foreldrepenger.melding.anke.AnkeVurdering;
import no.nav.foreldrepenger.melding.anke.AnkeVurderingOmgjør;

public class AnkeDtoMapper {

    public static Optional<Anke> mapAnkeFraDto(AnkebehandlingDto dto) {

        if (dto.getAnkeVurderingResultat() == null) {
            return Optional.empty();
        }

        AnkeVurderingResultatDto ankeResultat = dto.getAnkeVurderingResultat();

        var builder = Anke.ny()
                .medAnkeVurdering(AnkeVurdering.fraKode(ankeResultat.getAnkeVurdering().getKode()))
                .medFritekstTilBrev(ankeResultat.getFritekstTilBrev())
                .medAnkeVurderingOmgjoer(AnkeVurderingOmgjør.fraKode(ankeResultat.getAnkeVurderingOmgjoer().getKode()))
                .medPaAnketBehandlingUuid(ankeResultat.getPåAnketKlageBehandlingUuid());

        return Optional.of(builder.build());
    }
}

package no.nav.foreldrepenger.melding.dtomapper;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import no.nav.foreldrepenger.fpsak.dto.anke.AnkeVurderingResultatDto;
import no.nav.foreldrepenger.fpsak.dto.anke.AnkebehandlingDto;
import no.nav.foreldrepenger.melding.anke.Anke;
import no.nav.foreldrepenger.melding.anke.AnkeVurdering;
import no.nav.foreldrepenger.melding.anke.AnkeVurderingOmgjør;

@ApplicationScoped
public class AnkeDtoMapper {


    public AnkeDtoMapper() {
        //CDI
    }

    public Optional<Anke> mapAnkeFraDto(AnkebehandlingDto dto) {

        if (dto.getAnkeVurderingResultat() == null) {
            return Optional.empty();
        }

        AnkeVurderingResultatDto ankeResultat = dto.getAnkeVurderingResultat();

        var builder = Anke.ny()
                .medAnkeVurdering(AnkeVurdering.fraKode(ankeResultat.getAnkeVurdering().getKode()))
                .medFritekstTilBrev(ankeResultat.getFritekstTilBrev())
                .medAnkeVurderingOmgjoer(AnkeVurderingOmgjør.fraKode(ankeResultat.getAnkeVurderingOmgjoer().getKode()))
                .medPaAnketBehandlingUuid(ankeResultat.getPaAnketBehandlingUuid());

        return Optional.of(builder.build());
    }
}

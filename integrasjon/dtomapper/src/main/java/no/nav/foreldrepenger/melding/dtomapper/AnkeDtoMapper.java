package no.nav.foreldrepenger.melding.dtomapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.dto.anke.AnkeVurderingResultatDto;
import no.nav.foreldrepenger.fpsak.dto.anke.AnkebehandlingDto;
import no.nav.foreldrepenger.melding.anke.Anke;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;

@ApplicationScoped
public class AnkeDtoMapper {


    public AnkeDtoMapper() {
        //CDI
    }

    public Anke mapAnkeFraDto(AnkebehandlingDto dto) {
        Anke.Builder builder = Anke.ny();

        AnkeVurderingResultatDto ankeResultat = dto.getAnkeVurderingResultat();

        builder.medFritekstTilBrev(ankeResultat.getFritekstTilBrev())
                .medAnkeOmgjoerArsak(ankeResultat.getAnkeOmgjoerArsak())
                .medAnkeOmgjoerArsakNavn(ankeResultat.getAnkeOmgjoerArsakNavn())
                .medAnkeVurdering(ankeResultat.getAnkeVurdering())
                .medAnkeVurderingOmgjoer(ankeResultat.getAnkeVurderingOmgjoer())
                .medBegrunnelse(ankeResultat.getBegrunnelse())
                .medErAnkerIkkePart(ankeResultat.isErAnkerIkkePart())
                .medErFristIkkeOverholdt(ankeResultat.isErFristIkkeOverholdt())
                .medErIkkeKonkret(ankeResultat.isErIkkeKonkret())
                .medErIkkeSignert(ankeResultat.isErIkkeSignert())
                .medErSubsidiartRealitetsbehandles(ankeResultat.isErSubsidiartRealitetsbehandles())
                .medGodkjentAvMedunderskriver(ankeResultat.isGodkjentAvMedunderskriver())
                .medPaAnketBehandlingId(ankeResultat.getPaAnketBehandlingId());

        return builder.build();
    }
}

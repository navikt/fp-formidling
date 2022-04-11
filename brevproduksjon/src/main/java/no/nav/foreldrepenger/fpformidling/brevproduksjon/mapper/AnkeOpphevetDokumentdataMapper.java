package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpformidling.anke.Anke;
import no.nav.foreldrepenger.fpformidling.anke.AnkeVurdering;
import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.AnkeOpphevetDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalTypeKode;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.ANKE_OPPHEVET)
public class AnkeOpphevetDokumentdataMapper implements DokumentdataMapper {

    private DomeneobjektProvider domeneobjektProvider;

    AnkeOpphevetDokumentdataMapper() {
        //CDI
    }

    @Inject
    public AnkeOpphevetDokumentdataMapper(DomeneobjektProvider domeneobjektProvider) {
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String getTemplateNavn() {
        return "anke-opphevet";
    }

    @Override
    public AnkeOpphevetDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse,
                                                       Behandling behandling, boolean erUtkast) {
        Optional<Anke> anke = domeneobjektProvider.hentAnkebehandling(behandling);

        var fellesBuilder = opprettFellesBuilder(dokumentFelles, hendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDatoNorsk(dokumentFelles.getDokumentDato()) : null);
        fellesBuilder.medFritekst(FritekstDto.fra(anke.map(Anke::getFritekstTilBrev).orElse(hendelse.getFritekst())));

        return AnkeOpphevetDokumentdata.ny()
                .medFelles(fellesBuilder.build())
                .medOppheve(erOpphevet(anke))
                .build();
    }

    private boolean erOpphevet(Optional<Anke> anke) {
        AnkeVurdering ankeVurdering = anke.map(Anke::getAnkeVurdering).orElse(AnkeVurdering.UDEFINERT);
        return !AnkeVurdering.ANKE_HJEMSEND_UTEN_OPPHEV.equals(ankeVurdering);
    }
}

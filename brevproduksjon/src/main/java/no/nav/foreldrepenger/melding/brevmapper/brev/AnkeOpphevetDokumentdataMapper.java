package no.nav.foreldrepenger.melding.brevmapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDatoNorsk;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.anke.Anke;
import no.nav.foreldrepenger.melding.anke.AnkeVurdering;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.AnkeOpphevetDokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Fritekst;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;

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
        fellesBuilder.medFritekst(Fritekst.fra(anke.map(Anke::getFritekstTilBrev).orElse(hendelse.getFritekst())));

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

package no.nav.foreldrepenger.fpformidling.brevmapper.brev;

import static no.nav.foreldrepenger.fpformidling.datamapper.util.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.behandling.innsyn.Innsyn;
import no.nav.foreldrepenger.fpformidling.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.InnsynDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Fritekst;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalTypeKode;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.INNSYN_SVAR)
public class InnsynDokumentdataMapper implements DokumentdataMapper {
    private BrevParametere brevParametere;
    private DomeneobjektProvider domeneobjektProvider;

    @Inject
    public InnsynDokumentdataMapper(BrevParametere brevParametere, DomeneobjektProvider domeneobjektProvider) {
        this.brevParametere = brevParametere;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String getTemplateNavn() {
        return "innsyn";
    }

    @Override
    public InnsynDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse,
                                                 Behandling behandling, boolean erUtkast) {
        Innsyn innsynsBehandling = domeneobjektProvider.hentInnsyn(behandling);

        var fellesBuilder = opprettFellesBuilder(dokumentFelles, hendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), behandling.getSpråkkode()) : null);
        Fritekst.fra(hendelse, behandling).ifPresent(fellesBuilder::medFritekst);

        return InnsynDokumentdata.ny()
                .medFelles(fellesBuilder.build())
                .medKlagefrist(brevParametere.getKlagefristUker())
                .medInnsynResultat(innsynsBehandling.getInnsynResultatType().getKode())
                .build();
    }
}

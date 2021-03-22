package no.nav.foreldrepenger.melding.brevmapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper.avklarFritekst;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.opprettFellesDokumentdataBuilder;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDato;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.innsyn.Innsyn;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.InnsynDokumentdata;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;

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
    public InnsynDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse, Behandling behandling) {
        Innsyn innsynsBehandling = domeneobjektProvider.hentInnsyn(behandling);

        var fellesBuilder = opprettFellesDokumentdataBuilder(dokumentFelles, hendelse);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), behandling.getSpråkkode()) : null);
        avklarFritekst(hendelse, behandling).ifPresent(fellesBuilder::medFritekst);

        return InnsynDokumentdata.ny()
                .medFellesDokumentData(fellesBuilder.build())
                .medKlagefrist(brevParametere.getKlagefristUker())
                .medInnsynResultat(innsynsBehandling.getInnsynResultatType().getKode())
                .build();
    }
}

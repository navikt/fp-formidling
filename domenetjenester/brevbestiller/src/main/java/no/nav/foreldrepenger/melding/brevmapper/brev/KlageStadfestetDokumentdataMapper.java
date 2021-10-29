package no.nav.foreldrepenger.melding.brevmapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDatoNorsk;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.KlageMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.KlageStadfestetDokumentdata;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.KLAGE_STADFESTET)
public class KlageStadfestetDokumentdataMapper implements DokumentdataMapper {

    private BrevParametere brevParametere;
    private DomeneobjektProvider domeneobjektProvider;

    KlageStadfestetDokumentdataMapper() {
        //CDI
    }

    @Inject
    public KlageStadfestetDokumentdataMapper(BrevParametere brevParametere, DomeneobjektProvider domeneobjektProvider) {
        this.brevParametere = brevParametere;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String getTemplateNavn() {
        return "klage-stadfestet";
    }

    @Override
    public KlageStadfestetDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse,
                                                       Behandling behandling, boolean erUtkast) {
        Klage klage = domeneobjektProvider.hentKlagebehandling(behandling);

        var fellesBuilder = opprettFellesBuilder(dokumentFelles, hendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDatoNorsk(dokumentFelles.getDokumentDato()) : null);
        KlageMapper.avklarFritekstKlage(hendelse, klage).ifPresent(fellesBuilder::medFritekst);

        var dokumentdataBuilder = KlageStadfestetDokumentdata.ny()
                .medFelles(fellesBuilder.build())
                .medGjelderTilbakekreving(klage.getPåklagdBehandlingType().erTilbakekrevingBehandlingType())
                .medKlagefristUker(brevParametere.getKlagefristUker());

        return dokumentdataBuilder.build();
    }
}
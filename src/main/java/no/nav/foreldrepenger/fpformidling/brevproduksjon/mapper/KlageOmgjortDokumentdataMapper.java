package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto.fra;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.KlageOmgjortDokumentdata;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalTypeKode;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.KLAGE_OMGJORT)
public class KlageOmgjortDokumentdataMapper implements DokumentdataMapper {

    private BrevParametere brevParametere;
    private DomeneobjektProvider domeneobjektProvider;

    KlageOmgjortDokumentdataMapper() {
        //CDI
    }

    @Inject
    public KlageOmgjortDokumentdataMapper(BrevParametere brevParametere, DomeneobjektProvider domeneobjektProvider) {
        this.brevParametere = brevParametere;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String getTemplateNavn() {
        return "klage-omgjort";
    }

    @Override
    public KlageOmgjortDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                       DokumentHendelse hendelse,
                                                       Behandling behandling,
                                                       boolean erUtkast) {
        var klage = domeneobjektProvider.hentKlagebehandling(behandling);

        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, hendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDatoNorsk(dokumentFelles.getDokumentDato()) : null);
        fra(hendelse, klage).ifPresent(fellesBuilder::medFritekst);

        var dokumentdataBuilder = KlageOmgjortDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medGjelderTilbakekreving(klage.getPåklagdBehandlingType().erTilbakekrevingBehandlingType())
            .medKlagefristUker(brevParametere.getKlagefristUker());

        return dokumentdataBuilder.build();
    }
}

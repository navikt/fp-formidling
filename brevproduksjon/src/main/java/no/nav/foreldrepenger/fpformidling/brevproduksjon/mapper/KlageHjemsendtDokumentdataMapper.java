package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto.fra;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.KlageMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.KlageHjemsendtDokumentdata;
import no.nav.foreldrepenger.fpformidling.klage.Klage;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalTypeKode;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.KLAGE_HJEMSENDT)
public class KlageHjemsendtDokumentdataMapper implements DokumentdataMapper {

    private BrevMapperUtil brevMapperUtil;
    private DomeneobjektProvider domeneobjektProvider;

    KlageHjemsendtDokumentdataMapper() {
        //CDI
    }

    @Inject
    public KlageHjemsendtDokumentdataMapper(BrevMapperUtil brevMapperUtil, DomeneobjektProvider domeneobjektProvider) {
        this.brevMapperUtil = brevMapperUtil;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String getTemplateNavn() {
        return "klage-hjemsendt";
    }

    @Override
    public KlageHjemsendtDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse,
                                                         Behandling behandling, boolean erUtkast) {
        Klage klage = domeneobjektProvider.hentKlagebehandling(behandling);

        var fellesBuilder = opprettFellesBuilder(dokumentFelles, hendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDatoNorsk(dokumentFelles.getDokumentDato()) : null);
        fra(hendelse, klage).ifPresent(fellesBuilder::medFritekst);

        var dokumentdataBuilder = KlageHjemsendtDokumentdata.ny()
                .medFelles(fellesBuilder.build())
                .medGjelderTilbakekreving(klage.getPåklagdBehandlingType().erTilbakekrevingBehandlingType())
                .medOpphevet(KlageMapper.erOpphevet(klage, hendelse))
                .medEttersendelsesfrist(formaterDatoNorsk(brevMapperUtil.getSvarFrist()));

        return dokumentdataBuilder.build();
    }
}

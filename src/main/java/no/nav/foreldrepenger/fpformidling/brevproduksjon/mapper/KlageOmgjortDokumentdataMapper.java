package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.KlageMapper.gjelderTilbakekreving;
import static no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto.fra;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.KlageOmgjortDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.KLAGE_OMGJORT)
public class KlageOmgjortDokumentdataMapper implements DokumentdataMapper {

    private BrevParametere brevParametere;

    KlageOmgjortDokumentdataMapper() {
        //CDI
    }

    @Inject
    public KlageOmgjortDokumentdataMapper(BrevParametere brevParametere) {
        this.brevParametere = brevParametere;
    }

    @Override
    public String getTemplateNavn() {
        return "klage-omgjort";
    }

    @Override
    public KlageOmgjortDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                       DokumentHendelse hendelse,
                                                       BrevGrunnlagDto behandling,
                                                       boolean erUtkast) {
        var klage = behandling.klageBehandling();

        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDatoNorsk(dokumentFelles.getDokumentDato()) : null);
        fra(hendelse, klage).ifPresent(fellesBuilder::medFritekst);

        var dokumentdataBuilder = KlageOmgjortDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medGjelderTilbakekreving(gjelderTilbakekreving(klage))
            .medKlagefristUker(brevParametere.getKlagefristUker());

        return dokumentdataBuilder.build();
    }
}

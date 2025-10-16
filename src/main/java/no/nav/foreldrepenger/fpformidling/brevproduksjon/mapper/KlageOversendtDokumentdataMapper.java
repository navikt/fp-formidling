package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.KlageMapper.gjelderTilbakekreving;
import static no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto.fra;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;

import java.time.LocalDate;

import jakarta.enterprise.context.ApplicationScoped;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.KlageOversendtDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.KLAGE_OVERSENDT)
public class KlageOversendtDokumentdataMapper implements DokumentdataMapper {

    @Override
    public String getTemplateNavn() {
        return "klage-oversendt";
    }

    @Override
    public KlageOversendtDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                         DokumentHendelse hendelse,
                                                         BrevGrunnlagDto behandling,
                                                         boolean erUtkast) {
        var klage = behandling.klageBehandling();

        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, erUtkast);

        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDatoNorsk(dokumentFelles.getDokumentDato()) : null);
        fra(hendelse, klage).ifPresent(fellesBuilder::medFritekst);

        var dokumentdataBuilder = KlageOversendtDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medGjelderTilbakekreving(gjelderTilbakekreving(klage))
            .medMottattDato(formaterDatoNorsk(utledMottattDato(behandling, klage.mottattDato())));

        return dokumentdataBuilder.build();
    }

    private LocalDate utledMottattDato(BrevGrunnlagDto behandling, LocalDate klageMottattDato) {
        return klageMottattDato != null ? klageMottattDato : behandling.opprettet().toLocalDate();
    }
}

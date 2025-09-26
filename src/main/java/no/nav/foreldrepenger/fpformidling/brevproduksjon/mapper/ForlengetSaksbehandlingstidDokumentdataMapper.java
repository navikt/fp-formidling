package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import jakarta.enterprise.context.ApplicationScoped;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.ForlengetSaksbehandlingstidDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.ForlengetSaksbehandlingstidDokumentdata.VariantType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.typer.DokumentMal;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID)
public class ForlengetSaksbehandlingstidDokumentdataMapper implements DokumentdataMapper {

    ForlengetSaksbehandlingstidDokumentdataMapper() {
        //CDI
    }

    @Override
    public String getTemplateNavn() {
        return "forlenget-saksbehandlingstid";
    }

    @Override
    public ForlengetSaksbehandlingstidDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                                      DokumentHendelse hendelse,
                                                                      Behandling behandling,
                                                                      boolean erUtkast) {

        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, behandling, erUtkast);
        fellesBuilder.medBrevDato(
            dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), behandling.getSpråkkode()) : null);

        return ForlengetSaksbehandlingstidDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medVariantType(mapVariantType(behandling.getBehandlingType(), hendelse.getDokumentMal()))
            .medDød(BrevMapperUtil.erDød(dokumentFelles))
            .medBehandlingsfristUker(behandling.getBehandlingType().getBehandlingstidFristUker())
            .build();
    }

    private static VariantType mapVariantType(BehandlingType behandlingType, DokumentMal dokumentMal) {
        if (BehandlingType.KLAGE.equals(behandlingType)) {
            return VariantType.KLAGE;
        }
        return mapForlengetSaksbehandlingstidVariant(dokumentMal);
    }

    private static VariantType mapForlengetSaksbehandlingstidVariant(DokumentMal dokumentMal) {
        return switch (dokumentMal) {
            case FORLENGET_SAKSBEHANDLINGSTID -> VariantType.FORLENGET;
            case FORLENGET_SAKSBEHANDLINGSTID_MEDL -> VariantType.MEDLEM;
            case FORLENGET_SAKSBEHANDLINGSTID_MEDL_FORUTGÅENDE -> VariantType.MEDLEM_FORUTGÅENDE;
            case FORLENGET_SAKSBEHANDLINGSTID_TIDLIG -> VariantType.FORTIDLIG;
            default -> VariantType.FORLENGET;
        };
    }

}

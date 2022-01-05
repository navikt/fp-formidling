package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.erDød;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.ForlengetSaksbehandlingstidDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.ForlengetSaksbehandlingstidDokumentdata.VariantType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalTypeKode;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.FORLENGET_SAKSBEHANDLINGSTID)
public class ForlengetSaksbehandlingstidDokumentdataMapper implements DokumentdataMapper {

    private static final Map<DokumentMalType, VariantType> MAL_TIL_VARIANT_MAP = Map.of(
            DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID, VariantType.FORLENGET,
            DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_MEDL, VariantType.MEDLEM,
            DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_TIDLIG, VariantType.FORTIDLIG
    );

    private DomeneobjektProvider domeneobjektProvider;

    ForlengetSaksbehandlingstidDokumentdataMapper() {
        //CDI
    }

    @Inject
    public ForlengetSaksbehandlingstidDokumentdataMapper(DomeneobjektProvider domeneobjektProvider) {
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String getTemplateNavn() {
        return "forlenget-saksbehandlingstid";
    }

    @Override
    public ForlengetSaksbehandlingstidDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse,
                                                                      Behandling behandling, boolean erUtkast) {

        var fellesBuilder = opprettFellesBuilder(dokumentFelles, hendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), behandling.getSpråkkode()) : null);

        return ForlengetSaksbehandlingstidDokumentdata.ny()
                .medFelles(fellesBuilder.build())
                .medVariantType(mapVariantType(hendelse.getDokumentMalType(), behandling))
                .medDød(erDød(dokumentFelles))
                .medBehandlingsfristUker(behandling.getBehandlingType().getBehandlingstidFristUker())
                .medAntallBarn(getAntallBarn(behandling))
                .build();
    }

    private VariantType mapVariantType(DokumentMalType dokumentMalType, Behandling behandling) {
        if (BehandlingType.KLAGE.equals(behandling.getBehandlingType())) {
            return VariantType.KLAGE;
        }
        if (MAL_TIL_VARIANT_MAP.containsKey(dokumentMalType)) {
            return MAL_TIL_VARIANT_MAP.get(dokumentMalType);
        }
        return VariantType.FORLENGET;
    }

    private int getAntallBarn(Behandling behandling) {
        Optional<FamilieHendelse> familieHendelse = domeneobjektProvider.hentFamiliehendelseHvisFinnes(behandling);
        return familieHendelse.map(FamilieHendelse::getAntallBarn)
                .filter(antall -> antall.intValue() > 0)
                .orElse(BigInteger.ONE).intValue();
    }
}

package no.nav.foreldrepenger.melding.brevmapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.erDød;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.opprettFellesDokumentdataBuilder;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDato;

import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.ForlengetSaksbehandlingstidDokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.ForlengetSaksbehandlingstidDokumentdata.VariantType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;

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
    public ForlengetSaksbehandlingstidDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse, Behandling behandling) {

        var fellesBuilder = opprettFellesDokumentdataBuilder(dokumentFelles, hendelse, behandling);
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

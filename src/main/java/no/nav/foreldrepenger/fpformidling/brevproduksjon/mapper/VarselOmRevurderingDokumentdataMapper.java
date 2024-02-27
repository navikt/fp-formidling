package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.behandling.RevurderingVarslingÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller.DokumentHendelseEntitet;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.VarselOmRevurderingDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.VARSEL_OM_REVURDERING)
public class VarselOmRevurderingDokumentdataMapper implements DokumentdataMapper {

    private BrevMapperUtil brevMapperUtil;
    private DomeneobjektProvider domeneobjektProvider;

    VarselOmRevurderingDokumentdataMapper() {
        //CDI
    }

    @Inject
    public VarselOmRevurderingDokumentdataMapper(BrevMapperUtil brevMapperUtil, DomeneobjektProvider domeneobjektProvider) {
        this.brevMapperUtil = brevMapperUtil;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String getTemplateNavn() {
        return "varsel-revurdering";
    }

    @Override
    public VarselOmRevurderingDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                              DokumentHendelseEntitet hendelse,
                                                              Behandling behandling,
                                                              boolean erUtkast) {

        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, behandling, erUtkast);
        fellesBuilder.medBrevDato(
            dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), behandling.getSpråkkode()) : null);
        fellesBuilder.medFritekst(FritekstDto.fra(hendelse.getFritekst()));

        var familieHendelse = domeneobjektProvider.hentFamiliehendelse(behandling);

        var advarselKode = utledAdvarselkode(hendelse);
        var dokumentdataBuilder = VarselOmRevurderingDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medTerminDato(finnTermindato(familieHendelse, behandling.getSpråkkode()).orElse(null))
            .medFristDato(formaterDato(brevMapperUtil.getSvarFrist(), behandling.getSpråkkode()))
            .medAntallBarn(familieHendelse.antallBarn())
            .medAdvarselKode(advarselKode)
            .medFlereOpplysninger(utledFlereOpplysninger(hendelse, advarselKode, behandling.getFagsakBackend().getYtelseType()))
            .medKreverSammenhengendeUttak(behandling.kreverSammenhengendeUttakFraBehandlingen());

        return dokumentdataBuilder.build();
    }

    private Optional<String> finnTermindato(FamilieHendelse familieHendelse, Språkkode språkkode) {
        return familieHendelse.termindato().map(termindato -> formaterDato(termindato, språkkode));
    }

    private String utledAdvarselkode(DokumentHendelseEntitet hendelse) {
        if (hendelse.getRevurderingVarslingÅrsak().equals(RevurderingVarslingÅrsak.UDEFINERT)) {
            if (harFritekst(hendelse)) {
                return RevurderingVarslingÅrsak.ANNET.getKode();
            }
            return null;
        }
        return hendelse.getRevurderingVarslingÅrsak().getKode();
    }

    private boolean utledFlereOpplysninger(DokumentHendelseEntitet hendelse, String advarselKode, FagsakYtelseType ytelseType) {
        return !RevurderingVarslingÅrsak.ARBEIDS_I_STØNADSPERIODEN.getKode().equals(advarselKode) && (harFritekst(hendelse)
            || !FagsakYtelseType.ENGANGSTØNAD.equals(ytelseType));
    }

    private boolean harFritekst(DokumentHendelseEntitet hendelse) {
        return hendelse.getFritekst() != null && !hendelse.getFritekst().isEmpty();
    }
}

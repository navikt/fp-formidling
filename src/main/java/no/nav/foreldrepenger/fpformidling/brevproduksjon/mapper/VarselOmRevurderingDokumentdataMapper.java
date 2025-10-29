package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.VarselOmRevurderingDokumentdata.RevurderingVarslingÅrsak;
import static no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.VarselOmRevurderingDokumentdata.ny;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.VarselOmRevurderingDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.VARSEL_OM_REVURDERING)
public class VarselOmRevurderingDokumentdataMapper implements DokumentdataMapper {

    private final BrevMapperUtil brevMapperUtil;

    @Inject
    public VarselOmRevurderingDokumentdataMapper(BrevMapperUtil brevMapperUtil) {
        this.brevMapperUtil = brevMapperUtil;
    }

    @Override
    public String getTemplateNavn() {
        return "varsel-revurdering";
    }

    @Override
    public VarselOmRevurderingDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                              DokumentHendelse hendelse,
                                                              BrevGrunnlagDto behandling,
                                                              boolean erUtkast) {

        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, erUtkast);
        var språkkode = dokumentFelles.getSpråkkode();
        fellesBuilder.medBrevDato(
            dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), språkkode) : null);
        fellesBuilder.medFritekst(FritekstDto.fra(hendelse.getFritekst()));

        var familieHendelse = behandling.familieHendelse();

        var advarselKode = utledAdvarselkode(hendelse);

        return ny()
            .medFelles(fellesBuilder.build())
            .medTerminDato(finnTermindato(familieHendelse, språkkode).orElse(null))
            .medFristDato(formaterDato(brevMapperUtil.getSvarFrist(), språkkode))
            .medAntallBarn(familieHendelse.antallBarn())
            .medAdvarselKode(advarselKode)
            .medFlereOpplysninger(utledFlereOpplysninger(hendelse, advarselKode, behandling.fagsakYtelseType())).build();
    }

    private Optional<String> finnTermindato(BrevGrunnlagDto.FamilieHendelse familieHendelse, Språkkode språkkode) {
        return Optional.ofNullable(familieHendelse.termindato()).map(termindato -> formaterDato(termindato, språkkode));
    }

    private RevurderingVarslingÅrsak utledAdvarselkode(DokumentHendelse hendelse) {
        if (hendelse.getRevurderingÅrsak() == null) {
            if (harFritekst(hendelse)) {
                return RevurderingVarslingÅrsak.ANNET;
            }
            return null;
        }
        return switch (hendelse.getRevurderingÅrsak()) {
            case BARN_IKKE_REGISTRERT_FOLKEREGISTER -> RevurderingVarslingÅrsak.BARNIKKEREG;
            case ARBEIDS_I_STØNADSPERIODEN -> RevurderingVarslingÅrsak.JOBBFULLTID;
            case BEREGNINGSGRUNNLAG_UNDER_HALV_G -> RevurderingVarslingÅrsak.IKKEOPPTJENT;
            case BRUKER_REGISTRERT_UTVANDRET -> RevurderingVarslingÅrsak.UTVANDRET;
            case ARBEID_I_UTLANDET -> RevurderingVarslingÅrsak.JOBBUTLAND;
            case IKKE_LOVLIG_OPPHOLD -> RevurderingVarslingÅrsak.IKKEOPPHOLD;
            case OPPTJENING_IKKE_OPPFYLT -> RevurderingVarslingÅrsak.JOBB6MND;
            case MOR_AKTIVITET_IKKE_OPPFYLT -> RevurderingVarslingÅrsak.AKTIVITET;
            case ANNET -> RevurderingVarslingÅrsak.ANNET;
        };
    }

    private boolean utledFlereOpplysninger(DokumentHendelse hendelse, RevurderingVarslingÅrsak advarselKode, FagsakYtelseType ytelseType) {
        return !RevurderingVarslingÅrsak.JOBBFULLTID.equals(advarselKode) && (harFritekst(hendelse)
            || !FagsakYtelseType.ENGANGSTØNAD.equals(ytelseType));
    }

    private boolean harFritekst(DokumentHendelse hendelse) {
        return hendelse.getFritekst() != null && !hendelse.getFritekst().isEmpty();
    }
}

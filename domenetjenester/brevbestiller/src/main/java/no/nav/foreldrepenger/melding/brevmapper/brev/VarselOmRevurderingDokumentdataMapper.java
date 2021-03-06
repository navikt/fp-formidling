package no.nav.foreldrepenger.melding.brevmapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.opprettFellesDokumentdataBuilder;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDato;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.RevurderingVarslingÅrsak;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.VarselOmRevurderingDokumentdata;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.VARSEL_OM_REVURDERING)
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
    public VarselOmRevurderingDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse, Behandling behandling) {

        var fellesBuilder = opprettFellesDokumentdataBuilder(dokumentFelles, hendelse);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), behandling.getSpråkkode()) : null);
        fellesBuilder.medFritekst(hendelse.getFritekst());

        FamilieHendelse familieHendelse = domeneobjektProvider.hentFamiliehendelse(behandling);

        String advarselKode = utledAdvarselkode(hendelse);
        var dokumentdataBuilder = VarselOmRevurderingDokumentdata.ny()
                .medFelles(fellesBuilder.build())
                .medTerminDato(finnTermindato(familieHendelse, behandling.getSpråkkode()))
                .medFristDato(formaterDato(brevMapperUtil.getSvarFrist(), behandling.getSpråkkode()))
                .medAntallBarn(familieHendelse.getAntallBarn().intValue())
                .medAdvarselKode(advarselKode)
                .medFlereOpplysninger(utledFlereOpplysninger(hendelse, advarselKode));

        return dokumentdataBuilder.build();
    }

    private String finnTermindato(FamilieHendelse familieHendelse, Språkkode språkkode) {
        if (familieHendelse.getTermindato() != null && familieHendelse.getTermindato().isPresent()) {
            return formaterDato(familieHendelse.getTermindato().get(), språkkode);
        }
        return null;
    }

    private String utledAdvarselkode(DokumentHendelse hendelse) {
        if (hendelse.getRevurderingVarslingÅrsak().equals(RevurderingVarslingÅrsak.UDEFINERT)) {
            if (harFritekst(hendelse)) {
                return RevurderingVarslingÅrsak.ANNET.getKode();
            }
            return null;
        }
        return hendelse.getRevurderingVarslingÅrsak().getKode();
    }

    private boolean utledFlereOpplysninger(DokumentHendelse hendelse, String advarselKode) {
        return !RevurderingVarslingÅrsak.ARBEIDS_I_STØNADSPERIODEN.getKode().equals(advarselKode) &&
                (harFritekst(hendelse) || !FagsakYtelseType.ENGANGSTØNAD.equals(hendelse.getYtelseType()));
    }

    private boolean harFritekst(DokumentHendelse hendelse) {
        return hendelse.getFritekst() != null && !hendelse.getFritekst().isEmpty();
    }
}

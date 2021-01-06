package no.nav.foreldrepenger.melding.brevmapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.brevSendesTilVerge;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.erKopi;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDatoNorsk;

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
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.FellesDokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.VarselOmRevurderingDokumentdata;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;
import no.nav.vedtak.util.StringUtils;

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

        var felles = FellesDokumentdata.ny()
                .medSøkerNavn(dokumentFelles.getSakspartNavn())
                .medSøkerPersonnummer(formaterPersonnummer(dokumentFelles.getSakspartId()))
                .medBrevDato(dokumentFelles.getDokumentDato()!= null ? formaterDatoNorsk(dokumentFelles.getDokumentDato()) : null)
                .medHarVerge(dokumentFelles.getErKopi() != null && dokumentFelles.getErKopi().isPresent())
                .medErKopi(dokumentFelles.getErKopi() != null && dokumentFelles.getErKopi().isPresent() && erKopi(dokumentFelles.getErKopi().get()))
                .medSaksnummer(dokumentFelles.getSaksnummer().getVerdi())
                .medYtelseType(hendelse.getYtelseType().getKode())
                .medFritekst(hendelse.getFritekst());

        if (brevSendesTilVerge(dokumentFelles)) {
            felles.medMottakerNavn(dokumentFelles.getMottakerNavn());
        }

        FamilieHendelse familieHendelse = domeneobjektProvider.hentFamiliehendelse(behandling);

        String advarselKode = utledAdvarselkode(hendelse);
        var varselOmRevurderingDokumentdataBuilder = VarselOmRevurderingDokumentdata.ny()
                .medFelles(felles.build())
                .medTerminDato(finnTermindato(familieHendelse))
                .medFristDato(formaterDatoNorsk(brevMapperUtil.getSvarFrist()))
                .medAntallBarn(familieHendelse.getAntallBarn().intValue())
                .medAdvarselKode(advarselKode)
                .medFlereOpplysninger(utledFlereOpplysninger(hendelse, advarselKode));

        return varselOmRevurderingDokumentdataBuilder.build();
    }

    private String finnTermindato(FamilieHendelse familieHendelse) {
        if (familieHendelse.getTermindato() != null && familieHendelse.getTermindato().isPresent()) {
            return formaterDatoNorsk(familieHendelse.getTermindato().get());
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
        return !StringUtils.nullOrEmpty(hendelse.getFritekst());
    }
}

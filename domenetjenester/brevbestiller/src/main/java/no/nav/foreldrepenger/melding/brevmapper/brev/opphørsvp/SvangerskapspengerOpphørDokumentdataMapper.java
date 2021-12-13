package no.nav.foreldrepenger.melding.brevmapper.brev.opphørsvp;

import no.nav.foreldrepenger.melding.Tuple;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.melding.brevmapper.brev.felles.SvpMapperUtil;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.BeregningsgrunnlagMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.InntektArbeidYtelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.opphørsvp.OpphørPeriode;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.opphørsvp.SvangerskapspengerOpphørDokumentdata;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatArbeidsforhold;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttaksresultat;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.erDød;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDato;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.SVANGERSKAPSPENGER_OPPHØR)
public class SvangerskapspengerOpphørDokumentdataMapper implements DokumentdataMapper {

    private BrevParametere brevParametere;
    private DomeneobjektProvider domeneobjektProvider;

    SvangerskapspengerOpphørDokumentdataMapper() {
        //CDI
    }

    @Inject
    public SvangerskapspengerOpphørDokumentdataMapper(BrevParametere brevParametere, DomeneobjektProvider domeneobjektProvider) {
        this.brevParametere = brevParametere;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String getTemplateNavn() {
        return "svangerskapspenger-opphor";
    }

    @Override
    public SvangerskapspengerOpphørDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse,
                                                                  Behandling behandling, boolean erUtkast) {
        var beregningsgrunnlag = domeneobjektProvider.hentBeregningsgrunnlagHvisFinnes(behandling);
        var svpUttaksresultat = domeneobjektProvider.hentUttaksresultatSvpHvisFinnes(behandling);
        var familieHendelse = domeneobjektProvider.hentFamiliehendelse(behandling);
        var iay = domeneobjektProvider.hentInntektArbeidYtelse(behandling);

        Språkkode språkkode = behandling.getSpråkkode();

        var fellesBuilder = opprettFellesBuilder(dokumentFelles, hendelse, behandling, erUtkast);
            fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), språkkode) : null);
            fellesBuilder.medErAutomatiskBehandlet(dokumentFelles.getAutomatiskBehandlet());

        var uttaksperioder = SvpMapperUtil.hentUttaksperioder(svpUttaksresultat);

         var dokumentdatabuilder = SvangerskapspengerOpphørDokumentdata.ny()
                .medFelles(fellesBuilder.build())
                .medHalvG(BeregningsgrunnlagMapper.getHalvGOrElseZero(beregningsgrunnlag))
                .medErSøkerDød(erDød(dokumentFelles))
                .medKlagefristUker(brevParametere.getKlagefristUker());

         mapOpphørtPeriodeOgLovhjemmel(dokumentdatabuilder, behandling,
                svpUttaksresultat.map(SvpUttaksresultat::getUttakResultatArbeidsforhold).orElse(Collections.emptyList()),
                 språkkode, iay);

        SvpMapperUtil.finnFørsteUttakssdato(uttaksperioder, behandling.getBehandlingsresultat())
                 .ifPresent(d -> dokumentdatabuilder.medOpphørsdato(formaterDato(d, språkkode)));

        familieHendelse.getDødsdato().ifPresent(d-> dokumentdatabuilder.medDødsdatoBarn(formaterDato(d, språkkode)));

        familieHendelse.getFødselsdato().ifPresent(d -> dokumentdatabuilder.medFødselsdato(formaterDato(d, språkkode)));

        return dokumentdatabuilder.build();
    }

    private void mapOpphørtPeriodeOgLovhjemmel(SvangerskapspengerOpphørDokumentdata.Builder dokumentdataBuilder, Behandling behandling,
                                               List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold, Språkkode språkKode, InntektArbeidYtelse iay) {

        Tuple <OpphørPeriode, String> opphørtePerioderOgLovhjemmel = OpphørPeriodeMapper.mapOpphørtePerioderOgLovhjemmel(behandling, uttakResultatArbeidsforhold, språkKode, iay);

        dokumentdataBuilder.medLovhjemmel(opphørtePerioderOgLovhjemmel.element2());
        dokumentdataBuilder.medOpphørPerioder(opphørtePerioderOgLovhjemmel.element1());
    }

}
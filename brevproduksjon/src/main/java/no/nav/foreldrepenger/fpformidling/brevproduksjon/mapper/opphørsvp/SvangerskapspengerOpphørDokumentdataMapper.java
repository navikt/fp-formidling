package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.opphørsvp;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.erDød;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BeregningsgrunnlagMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.SvpMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.Tuple;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.inntektarbeidytelse.Inntektsmeldinger;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.opphørsvp.OpphørPeriode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.opphørsvp.SvangerskapspengerOpphørDokumentdata;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalTypeKode;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseForeldrepenger;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelsePeriode;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvpUttakResultatArbeidsforhold;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvpUttaksresultat;

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
        var iay = domeneobjektProvider.hentInntektsmeldinger(behandling);
        var tilkjentYtelsePerioder = domeneobjektProvider.hentTilkjentYtelseFPHvisFinnes(behandling).map(TilkjentYtelseForeldrepenger::getPerioder).orElse(Collections.emptyList());

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
                 språkkode, iay, tilkjentYtelsePerioder);

        SvpMapperUtil.finnFørsteUttakssdato(uttaksperioder, behandling.getBehandlingsresultat())
                 .ifPresent(d -> dokumentdatabuilder.medOpphørsdato(formaterDato(d, språkkode)));

        familieHendelse.getDødsdato().ifPresent(d-> dokumentdatabuilder.medDødsdatoBarn(formaterDato(d, språkkode)));

        familieHendelse.getFødselsdato().ifPresent(d -> dokumentdatabuilder.medFødselsdato(formaterDato(d, språkkode)));

        return dokumentdatabuilder.build();
    }

    private void mapOpphørtPeriodeOgLovhjemmel(SvangerskapspengerOpphørDokumentdata.Builder dokumentdataBuilder, Behandling behandling, List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold, Språkkode språkKode, Inntektsmeldinger iay, List <TilkjentYtelsePeriode> tilkjentYtelsePerioder) {

        Tuple <OpphørPeriode, String> opphørtePerioderOgLovhjemmel = OpphørPeriodeMapper.mapOpphørtePerioderOgLovhjemmel(behandling, uttakResultatArbeidsforhold, språkKode, iay, tilkjentYtelsePerioder);

        dokumentdataBuilder.medLovhjemmel(opphørtePerioderOgLovhjemmel.element2());
        dokumentdataBuilder.medOpphørPerioder(opphørtePerioderOgLovhjemmel.element1());
    }

}
package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.opphorsvp;

import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BeregningsgrunnlagMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.opphørsvp.SvangerskapspengerOpphørDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.BrevGrunnlag;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.SVANGERSKAPSPENGER_OPPHØR)
public class SvangerskapspengerOpphørDokumentdataMapper implements DokumentdataMapper {

    private final BrevParametere brevParametere;

    @Inject
    public SvangerskapspengerOpphørDokumentdataMapper(BrevParametere brevParametere) {
        this.brevParametere = brevParametere;
    }

    @Override
    public String getTemplateNavn() {
        return "svangerskapspenger-opphor";
    }

    @Override
    public SvangerskapspengerOpphørDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                                   DokumentHendelse hendelse,
                                                                   BrevGrunnlag behandling,
                                                                   boolean erUtkast) {
        var beregningsgrunnlag = Optional.ofNullable(behandling.beregningsgrunnlag());
        var svpUttaksresultat = Optional.ofNullable(behandling.svangerskapspengerUttak());
        var familieHendelse = behandling.familieHendelse();
        var inntektsmeldinger = behandling.inntektsmeldinger();
        var tilkjentYtelsePerioder = Optional.ofNullable(behandling.tilkentYtelse()).map(BrevGrunnlag.TilkjentYtelse::dagytelse);

        var språkkode = dokumentFelles.getSpråkkode();

        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), språkkode) : null);
        fellesBuilder.medErAutomatiskBehandlet(dokumentFelles.getAutomatiskBehandlet());

        var dokumentdatabuilder = SvangerskapspengerOpphørDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medHalvG(BeregningsgrunnlagMapper.getHalvGOrElseZero(beregningsgrunnlag))
            .medErSøkerDød(BrevMapperUtil.erDød(dokumentFelles))
            .medKlagefristUker(brevParametere.getKlagefristUker());

        mapOpphørtPeriodeOgLovhjemmel(dokumentdatabuilder, behandling,
            svpUttaksresultat.map(BrevGrunnlag.SvangerskapspengerUttak::uttakArbeidsforhold).orElse(Collections.emptyList()), språkkode, inntektsmeldinger,
            tilkjentYtelsePerioder);

        var opphørsdato = behandling.getBehandlingsresultat().getOpphørsdato();

        opphørsdato.ifPresent(d -> dokumentdatabuilder.medOpphørsdato(formaterDato(d, språkkode)));

        familieHendelse.tidligstDødsdato().ifPresent(d -> dokumentdatabuilder.medDødsdatoBarn(formaterDato(d, språkkode)));

        familieHendelse.tidligstFødselsdato().ifPresent(d -> dokumentdatabuilder.medFødselsdato(formaterDato(d, språkkode)));

        return dokumentdatabuilder.build();
    }

    private void mapOpphørtPeriodeOgLovhjemmel(SvangerskapspengerOpphørDokumentdata.Builder dokumentdataBuilder,
                                               BrevGrunnlag behandling,
                                               List<BrevGrunnlag.SvangerskapspengerUttak.UttakArbeidsforhold> uttakResultatArbeidsforhold,
                                               Språkkode språkKode,
                                               List<BrevGrunnlag.Inntektsmelding> inntektsmeldinger,
                                               Optional<TilkjentYtelseDagytelseDto> tilkjentYtelsePerioder) {

        var opphørtePerioderOgLovhjemmel = OpphørPeriodeMapper.mapOpphørtePerioderOgLovhjemmel(behandling, uttakResultatArbeidsforhold, språkKode,
            inntektsmeldinger, tilkjentYtelsePerioder);

        dokumentdataBuilder.medLovhjemmel(opphørtePerioderOgLovhjemmel.element2());
        dokumentdataBuilder.medOpphørPerioder(opphørtePerioderOgLovhjemmel.element1());
    }

}

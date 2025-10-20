package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.opphorsvp;

import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BeregningsgrunnlagMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.arbeidsgiver.ArbeidsgiverTjeneste;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.opphørsvp.SvangerskapspengerOpphørDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.SVANGERSKAPSPENGER_OPPHØR)
public class SvangerskapspengerOpphørDokumentdataMapper implements DokumentdataMapper {

    private final BrevParametere brevParametere;
    private final ArbeidsgiverTjeneste arbeidsgiverTjeneste;

    @Inject
    public SvangerskapspengerOpphørDokumentdataMapper(BrevParametere brevParametere, ArbeidsgiverTjeneste arbeidsgiverTjeneste) {
        this.brevParametere = brevParametere;
        this.arbeidsgiverTjeneste = arbeidsgiverTjeneste;
    }

    @Override
    public String getTemplateNavn() {
        return "svangerskapspenger-opphor";
    }

    @Override
    public SvangerskapspengerOpphørDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                                   DokumentHendelse hendelse,
                                                                   BrevGrunnlagDto behandling,
                                                                   boolean erUtkast) {
        var beregningsgrunnlag = Optional.ofNullable(behandling.beregningsgrunnlag());
        var svpUttaksresultat = Optional.ofNullable(behandling.svangerskapspenger());
        var familieHendelse = behandling.familieHendelse();
        var inntektsmeldinger = behandling.inntektsmeldinger();
        var tilkjentYtelse = Optional.ofNullable(behandling.tilkjentYtelse()).map(BrevGrunnlagDto.TilkjentYtelse::dagytelse);

        var språkkode = dokumentFelles.getSpråkkode();

        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), språkkode) : null);
        fellesBuilder.medErAutomatiskBehandlet(dokumentFelles.getAutomatiskBehandlet());

        var dokumentdatabuilder = SvangerskapspengerOpphørDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medHalvG(BeregningsgrunnlagMapper.getHalvGOrElseZero(beregningsgrunnlag))
            .medErSøkerDød(BrevMapperUtil.erDød(dokumentFelles))
            .medKlagefristUker(brevParametere.getKlagefristUker());

        var tilkjentPerioder = tilkjentYtelse.map(TilkjentYtelseDagytelseDto::perioder).orElse(List.of());
        mapOpphørtPeriodeOgLovhjemmel(dokumentdatabuilder, behandling,
            svpUttaksresultat.map(BrevGrunnlagDto.Svangerskapspenger::uttakArbeidsforhold).orElse(Collections.emptyList()), språkkode, inntektsmeldinger, tilkjentPerioder);

        var opphørsdato = Optional.ofNullable(behandling.behandlingsresultat().opphørsdato());

        opphørsdato.ifPresent(d -> dokumentdatabuilder.medOpphørsdato(formaterDato(d, språkkode)));

        finnTidligstDødsdato(familieHendelse).ifPresent(d -> dokumentdatabuilder.medDødsdatoBarn(formaterDato(d, språkkode)));

        finnTidligstFødselsdato(familieHendelse).ifPresent(d -> dokumentdatabuilder.medFødselsdato(formaterDato(d, språkkode)));

        return dokumentdatabuilder.build();
    }

    private Optional<LocalDate> finnTidligstFødselsdato(BrevGrunnlagDto.FamilieHendelse familieHendelse) {
        return familieHendelse.barn().stream().map(BrevGrunnlagDto.Barn::fødselsdato).filter(Objects::nonNull).min(LocalDate::compareTo);
    }

    private static Optional<LocalDate> finnTidligstDødsdato(BrevGrunnlagDto.FamilieHendelse familieHendelse) {
        return familieHendelse.barn().stream().map(BrevGrunnlagDto.Barn::dødsdato).filter(Objects::nonNull).min(LocalDate::compareTo);
    }

    private void mapOpphørtPeriodeOgLovhjemmel(SvangerskapspengerOpphørDokumentdata.Builder dokumentdataBuilder,
                                               BrevGrunnlagDto behandling,
                                               List<BrevGrunnlagDto.Svangerskapspenger.UttakArbeidsforhold> uttakResultatArbeidsforhold,
                                               Språkkode språkKode,
                                               List<BrevGrunnlagDto.Inntektsmelding> inntektsmeldinger,
                                               List<TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto> tilkjentYtelsePerioder) {

        var opphørtePerioderOgLovhjemmel = OpphørPeriodeMapper.mapOpphørtePerioderOgLovhjemmel(behandling, uttakResultatArbeidsforhold, språkKode,
            inntektsmeldinger, tilkjentYtelsePerioder, arbeidsgiverTjeneste::hentArbeidsgiverNavn);

        dokumentdataBuilder.medLovhjemmel(opphørtePerioderOgLovhjemmel.element2());
        dokumentdataBuilder.medOpphørPerioder(opphørtePerioderOgLovhjemmel.element1());
    }

}

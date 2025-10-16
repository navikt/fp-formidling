package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BehandlingMapper.erRevurderingPgaEndretBeregningsgrunnlag;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BehandlingMapper.erTermindatoEndret;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.TilkjentYtelseMapper.finnAntallRefusjonerTilArbeidsgivere;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.TilkjentYtelseMapper.finnMånedsbeløp;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.TilkjentYtelseMapper.harBrukerAndel;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp.AvslagsperiodeMapper.mapAvslagsperioder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp.AvslåttAktivitetMapper.mapAvslåtteAktiviteter;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.util.Collection;
import java.util.List;
import java.util.function.UnaryOperator;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.arbeidsgiver.ArbeidsgiverTjeneste;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Beløp;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.AktiviteterOgUtbetalingsperioder;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.SvangerskapspengerInnvilgelseDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.Utbetalingsperiode;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.SVANGERSKAPSPENGER_INNVILGELSE)
public class SvangerskapspengerInnvilgelseDokumentdataMapper implements DokumentdataMapper {

    private final BrevParametere brevParametere;
    private final ArbeidsgiverTjeneste arbeidsgiverTjeneste;

    @Inject
    public SvangerskapspengerInnvilgelseDokumentdataMapper(BrevParametere brevParametere, ArbeidsgiverTjeneste arbeidsgiverTjeneste) {
        this.brevParametere = brevParametere;
        this.arbeidsgiverTjeneste = arbeidsgiverTjeneste;
    }

    private static boolean erNyEllerEndretBeregning(BrevGrunnlagDto behandling) {
        return behandling.erFørstegangssøknad() || behandling.behandlingsresultat().konsekvenserForYtelsen().contains(
            Behandlingsresultat.KonsekvensForYtelsen.ENDRING_I_BEREGNING);
    }

    @Override
    public String getTemplateNavn() {
        return "svangerskapspenger-innvilgelse";
    }

    @Override
    public SvangerskapspengerInnvilgelseDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                                        DokumentHendelse hendelse,
                                                                        BrevGrunnlagDto behandling,
                                                                        boolean erUtkast) {
        var beregningsgrunnlag = behandling.beregningsgrunnlag();
        var tilkjentYtelse = behandling.tilkjentYtelse().dagytelse();
        var uttaksresultatSvp = behandling.svangerskapspenger();
        var mottattDatoSøknad = behandling.søknadMottattDato();


        var språkkode = dokumentFelles.getSpråkkode();
        var fellesBuilder = opprettFellesBuilder(dokumentFelles, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), språkkode) : null);
        FritekstDto.fraFritekst(hendelse, behandling.behandlingsresultat().fritekst()).ifPresent(fellesBuilder::medFritekst);

        UnaryOperator<String> hentArbeidsgiverNavn = arbeidsgiverTjeneste::hentArbeidsgiverNavn;
        var utbetalingsPerioderPerAktvivitet = UtbetalingsperiodeMapper.mapUtbetalingsperioderPerAktivitet(tilkjentYtelse.perioder(), språkkode,
            hentArbeidsgiverNavn);
        var inkludereBeregning = erNyEllerEndretBeregning(behandling);
        var alleUtbetalingsperioder = utledAlleUtbetalingsperioder(utbetalingsPerioderPerAktvivitet);

        var dokumentdataBuilder = SvangerskapspengerInnvilgelseDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medRevurdering(behandling.erRevurdering())
            .medRefusjonTilBruker(harBrukerAndel(tilkjentYtelse))
            .medAntallRefusjonerTilArbeidsgivere(finnAntallRefusjonerTilArbeidsgivere(tilkjentYtelse))
            .medStønadsperiodeTom(formaterDato(UtbetalingsperiodeMapper.finnSisteStønadsdato(alleUtbetalingsperioder), språkkode))
            .medMånedsbeløp(finnMånedsbeløp(tilkjentYtelse))
            .medMottattDato(formaterDato(mottattDatoSøknad, språkkode))
            .medKlagefristUker(brevParametere.getKlagefristUker())
            .medAntallUtbetalingsperioder(alleUtbetalingsperioder.size())
            .medAktiviteterOgUtbetalingsperioder(utbetalingsPerioderPerAktvivitet)
            .medAvslagsperioder(mapAvslagsperioder(uttaksresultatSvp.uttakArbeidsforhold(), språkkode, hentArbeidsgiverNavn))
            .medAvslåtteAktiviteter(mapAvslåtteAktiviteter(uttaksresultatSvp.uttakArbeidsforhold(), hentArbeidsgiverNavn))
            .medInkludereBeregning(inkludereBeregning);

        if (behandling.erRevurdering()) {
            var originalBehandling = behandling.originalBehandling();
            var originalFamiliehendelse = originalBehandling.familieHendelse();
            var familieHendelse = behandling.familieHendelse();

            dokumentdataBuilder.medEndretFraAvslag(originalBehandling.behandlingResultatType() == Behandlingsresultat.BehandlingResultatType.AVSLÅTT);
            dokumentdataBuilder.medUtbetalingEndret(erRevurderingPgaEndretBeregningsgrunnlag(behandling));
            dokumentdataBuilder.medTermindatoEndret(erTermindatoEndret(familieHendelse, originalFamiliehendelse));
        }

        if (inkludereBeregning) {
            //Spesielhåndtering av militærstatus. Dersom personen har militærstatus med dagsats er beregningsgrunnlaget satt til
            //3G. I disse tilfellene skal andre statuser ignoreres (Beregning fjerner ikke andre statuser). Gjelder både FP og SVP.
            if (BeregningMapper.erMilitærSivil(beregningsgrunnlag)) {
                dokumentdataBuilder.medMilitærSivil(true);
            } else {
                dokumentdataBuilder.medArbeidsforhold(BeregningMapper.mapArbeidsforhold(beregningsgrunnlag, hentArbeidsgiverNavn));
                dokumentdataBuilder.medSelvstendigNæringsdrivende(BeregningMapper.mapSelvstendigNæringsdrivende(beregningsgrunnlag));
                dokumentdataBuilder.medFrilanser(BeregningMapper.mapFrilanser(beregningsgrunnlag));
                dokumentdataBuilder.medMilitærSivil(false);
            }
            dokumentdataBuilder.medNaturalytelser(NaturalytelseMapper.mapNaturalytelser(tilkjentYtelse, beregningsgrunnlag, språkkode,
                hentArbeidsgiverNavn));
            dokumentdataBuilder.medBruttoBeregningsgrunnlag(Beløp.of(BeregningMapper.getAvkortetPrÅrSVP(beregningsgrunnlag)));
            dokumentdataBuilder.medInntektOver6G(BeregningMapper.inntektOverSeksG(beregningsgrunnlag));
            dokumentdataBuilder.medSeksG(BeregningMapper.finnSeksG(beregningsgrunnlag).longValue());
            dokumentdataBuilder.medLovhjemmel(BeregningMapper.utledLovhjemmelForBeregning(beregningsgrunnlag, behandling));
        }

        return dokumentdataBuilder.build();
    }

    private List<Utbetalingsperiode> utledAlleUtbetalingsperioder(List<AktiviteterOgUtbetalingsperioder> utbetalingsPerioderPerAktvivitet) {
        return  utbetalingsPerioderPerAktvivitet.stream()
            .map(AktiviteterOgUtbetalingsperioder::utbetalingsperioder)
            .flatMap(Collection::stream)
            .toList();
    }
}

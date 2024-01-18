package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;

import static no.nav.foreldrepenger.fpformidling.domene.behandling.KonsekvensForYtelsen.ENDRING_I_BEREGNING;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BehandlingMapper.erEndretFraAvslått;
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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Beløp;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.AktiviteterOgUtbetalingsperioder;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.SvangerskapspengerInnvilgelseDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.Utbetalingsperiode;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalTypeKode;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.SVANGERSKAPSPENGER_INNVILGELSE)
public class SvangerskapspengerInnvilgelseDokumentdataMapper implements DokumentdataMapper {

    private DomeneobjektProvider domeneobjektProvider;
    private BrevParametere brevParametere;

    SvangerskapspengerInnvilgelseDokumentdataMapper() {
        //CDI
    }

    @Inject
    public SvangerskapspengerInnvilgelseDokumentdataMapper(DomeneobjektProvider domeneobjektProvider, BrevParametere brevParametere) {
        this.domeneobjektProvider = domeneobjektProvider;
        this.brevParametere = brevParametere;
    }

    private static boolean erNyEllerEndretBeregning(Behandling behandling) {
        return behandling.erFørstegangssøknad() || behandling.getBehandlingsresultat().getKonsekvenserForYtelsen().contains(ENDRING_I_BEREGNING);
    }

    @Override
    public String getTemplateNavn() {
        return "svangerskapspenger-innvilgelse";
    }

    @Override
    public SvangerskapspengerInnvilgelseDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                                        DokumentHendelse hendelse,
                                                                        Behandling behandling,
                                                                        boolean erUtkast) {
        var språkkode = behandling.getSpråkkode();
        var beregningsgrunnlag = domeneobjektProvider.hentBeregningsgrunnlag(behandling);
        var tilkjentYtelse = domeneobjektProvider.hentTilkjentYtelseForeldrepenger(behandling);
        var uttaksresultatSvp = domeneobjektProvider.hentSvangerskapspengerUttak(behandling);
        var mottattDatoSøknad = domeneobjektProvider.hentMottattDatoSøknad(behandling);


        var fellesBuilder = opprettFellesBuilder(dokumentFelles, hendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), språkkode) : null);
        FritekstDto.fra(hendelse, behandling).ifPresent(fellesBuilder::medFritekst);

        var utbetalingsPerioderPerAktvivitet = UtbetalingsperiodeMapper.mapUtbetalingsperioderPerAktivitet(tilkjentYtelse.getPerioder(), språkkode);
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
            .medAvslagsperioder(mapAvslagsperioder(uttaksresultatSvp.getUttakResultatArbeidsforhold(), språkkode))
            .medAvslåtteAktiviteter(mapAvslåtteAktiviteter(uttaksresultatSvp.getUttakResultatArbeidsforhold()))
            .medInkludereBeregning(inkludereBeregning);

        if (behandling.erRevurdering()) {
            var orginalBehandling = domeneobjektProvider.hentOriginalBehandlingHvisFinnes(behandling);
            var originalFamiliehendelse = orginalBehandling.map(domeneobjektProvider::hentFamiliehendelse);
            var familieHendelse = domeneobjektProvider.hentFamiliehendelse(behandling);

            dokumentdataBuilder.medEndretFraAvslag(erEndretFraAvslått(orginalBehandling));
            dokumentdataBuilder.medUtbetalingEndret(erRevurderingPgaEndretBeregningsgrunnlag(behandling));
            dokumentdataBuilder.medTermindatoEndret(erTermindatoEndret(familieHendelse, originalFamiliehendelse));
        }

        if (inkludereBeregning) {
            //Spesielhåndtering av militærstatus. Dersom personen har militærstatus med dagsats er beregningsgrunnlaget satt til
            //3G. I disse tilfellene skal andre statuser ignoreres (Beregning fjerner ikke andre statuser). Gjelder både FP og SVP.
            if (BeregningMapper.erMilitærSivil(beregningsgrunnlag)) {
                dokumentdataBuilder.medMilitærSivil(true);
            } else {
                dokumentdataBuilder.medArbeidsforhold(BeregningMapper.mapArbeidsforhold(beregningsgrunnlag));
                dokumentdataBuilder.medSelvstendigNæringsdrivende(BeregningMapper.mapSelvstendigNæringsdrivende(beregningsgrunnlag));
                dokumentdataBuilder.medFrilanser(BeregningMapper.mapFrilanser(beregningsgrunnlag));
                dokumentdataBuilder.medMilitærSivil(false);
            }
            dokumentdataBuilder.medNaturalytelser(NaturalytelseMapper.mapNaturalytelser(tilkjentYtelse, beregningsgrunnlag, språkkode));
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

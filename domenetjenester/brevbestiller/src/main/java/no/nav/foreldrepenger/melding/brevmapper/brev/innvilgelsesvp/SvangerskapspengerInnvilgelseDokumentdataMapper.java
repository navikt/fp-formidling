package no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsesvp;

import static no.nav.foreldrepenger.melding.behandling.KonsekvensForYtelsen.ENDRING_I_BEREGNING;
import static no.nav.foreldrepenger.melding.brevmapper.brev.felles.BeregningsresultatMapper.finnAntallRefusjonerTilArbeidsgivere;
import static no.nav.foreldrepenger.melding.brevmapper.brev.felles.BeregningsresultatMapper.finnMånedsbeløp;
import static no.nav.foreldrepenger.melding.brevmapper.brev.felles.BeregningsresultatMapper.harBrukerAndel;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsesvp.AvslagsperiodeMapper.mapAvslagsperioder;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsesvp.AvslåttAktivitetMapper.mapAvslåtteAktiviteter;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsesvp.BeregningMapper.erMilitærSivil;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsesvp.BeregningMapper.finnSeksG;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsesvp.BeregningMapper.getAvkortetPrÅrSVP;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsesvp.BeregningMapper.inntektOverSeksG;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsesvp.BeregningMapper.mapArbeidsforhold;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsesvp.BeregningMapper.mapFrilanser;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsesvp.BeregningMapper.mapSelvstendigNæringsdrivende;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsesvp.BeregningMapper.utledLovhjemmelForBeregning;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsesvp.NaturalytelseMapper.mapNaturalytelser;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsesvp.UtbetalingsperiodeMapper.finnStønadsperiodeTom;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsesvp.UtbetalingsperiodeMapper.mapUtbetalingsperioder;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsesvp.UttaksperiodeMapper.mapUttaksaktivteterMedPerioder;
import static no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper.erEndretFraAvslått;
import static no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper.erRevurderingPgaEndretBeregningsgrunnlag;
import static no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper.erTermindatoEndret;
import static no.nav.foreldrepenger.melding.datamapper.domene.MottattdokumentMapper.finnSøknadsdatoFraMottatteDokumenter;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDato;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Fritekst;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsesvp.SvangerskapspengerInnvilgelseDokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsesvp.Utbetalingsperiode;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsesvp.Uttaksaktivitet;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.SVANGERSKAPSPENGER_INNVILGELSE)
public class SvangerskapspengerInnvilgelseDokumentdataMapper implements DokumentdataMapper {

    private DomeneobjektProvider domeneobjektProvider;
    private BrevParametere brevParametere;

    SvangerskapspengerInnvilgelseDokumentdataMapper() {
        //CDI
    }

    @Inject
    public SvangerskapspengerInnvilgelseDokumentdataMapper(DomeneobjektProvider domeneobjektProvider,
                                                           BrevParametere brevParametere) {
        this.domeneobjektProvider = domeneobjektProvider;
        this.brevParametere = brevParametere;
    }

    @Override
    public String getTemplateNavn() {
        return "svangerskapspenger-innvilgelse";
    }

    @Override
    public SvangerskapspengerInnvilgelseDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse,
                                                                        Behandling behandling, boolean erUtkast) {
        var språkkode = behandling.getSpråkkode();
        var mottatteDokumenter = domeneobjektProvider.hentMottatteDokumenter(behandling);
        var beregningsgrunnlag = domeneobjektProvider.hentBeregningsgrunnlag(behandling);
        var beregningsresultat = domeneobjektProvider.hentBeregningsresultatFP(behandling);
        var uttaksresultatSvp = domeneobjektProvider.hentUttaksresultatSvp(behandling);

        var fellesBuilder = opprettFellesBuilder(dokumentFelles, hendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), språkkode) : null);
        Fritekst.fra(hendelse, behandling).ifPresent(fellesBuilder::medFritekst);

        List<Utbetalingsperiode> utbetalingsperioder = mapUtbetalingsperioder(beregningsresultat.getBeregningsresultatPerioder(), språkkode);
        List<Uttaksaktivitet> uttaksaktiviteter = mapUttaksaktivteterMedPerioder(uttaksresultatSvp, beregningsresultat, språkkode);
        boolean inkludereBeregning = erNyEllerEndretBeregning(behandling);

        var dokumentdataBuilder = SvangerskapspengerInnvilgelseDokumentdata.ny()
                .medFelles(fellesBuilder.build())
                .medRevurdering(behandling.erRevurdering())
                .medRefusjonTilBruker(harBrukerAndel(beregningsresultat))
                .medAntallRefusjonerTilArbeidsgivere(finnAntallRefusjonerTilArbeidsgivere(beregningsresultat))
                .medStønadsperiodeTom(formaterDato(finnStønadsperiodeTom(utbetalingsperioder), språkkode))
                .medMånedsbeløp(finnMånedsbeløp(beregningsresultat))
                .medMottattDato(formaterDato(finnSøknadsdatoFraMottatteDokumenter(behandling, mottatteDokumenter), språkkode))
                .medKlagefristUker(brevParametere.getKlagefristUker())
                .medAntallUttaksperioder(tellAntallUttaksperioder(uttaksaktiviteter))
                .medUttaksaktiviteter(uttaksaktiviteter)
                .medUtbetalingsperioder(utbetalingsperioder)
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
            dokumentdataBuilder.medArbeidsforhold(mapArbeidsforhold(beregningsgrunnlag));
            dokumentdataBuilder.medSelvstendigNæringsdrivende(mapSelvstendigNæringsdrivende(beregningsgrunnlag));
            dokumentdataBuilder.medFrilanser(mapFrilanser(beregningsgrunnlag));
            dokumentdataBuilder.medNaturalytelser(mapNaturalytelser(beregningsresultat, beregningsgrunnlag, språkkode));
            dokumentdataBuilder.medBruttoBeregningsgrunnlag(getAvkortetPrÅrSVP(beregningsgrunnlag));
            dokumentdataBuilder.medMilitærSivil(erMilitærSivil(beregningsgrunnlag));
            dokumentdataBuilder.medInntektOver6G(inntektOverSeksG(beregningsgrunnlag));
            dokumentdataBuilder.medSeksG(finnSeksG(beregningsgrunnlag).longValue());
            dokumentdataBuilder.medLovhjemmel(utledLovhjemmelForBeregning(beregningsgrunnlag, behandling));
        }

        return dokumentdataBuilder.build();
    }

    private int tellAntallUttaksperioder(List<Uttaksaktivitet> uttaksaktiviteter) {
        return uttaksaktiviteter.stream().mapToInt(aktivitet -> aktivitet.getUttaksperioder().size()).sum();
    }

    private static boolean erNyEllerEndretBeregning(Behandling behandling) {
        return behandling.erFørstegangssøknad() ||
                behandling.getBehandlingsresultat().getKonsekvenserForYtelsen().contains(ENDRING_I_BEREGNING);
    }
}
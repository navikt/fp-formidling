package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp;

import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.Dokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.FellesDokumentdata;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingResultatType;

public class InnvilgelseForeldrepengerDokumentdata extends Dokumentdata {
    private BehandlingType behandlingType;
    private BehandlingResultatType behandlingResultatType;
    private KonsekvensForInnvilgetYtelse konsekvensForInnvilgetYtelse;
    private String søknadsdato;
    private int dekningsgrad;
    private long dagsats;
    private long månedsbeløp;
    private long seksG;
    private boolean inntektOverSeksG;
    private ForMyeUtbetalt forMyeUtbetalt;
    private boolean inntektMottattArbeidsgiver;
    private boolean annenForelderHarRett;
    private boolean annenForelderHarRettVurdert;
    private AleneomsorgKode aleneomsorgKode;
    private boolean ikkeOmsorg;
    private boolean barnErFødt;
    private boolean årsakErFødselshendelse;
    private boolean gjelderMor;
    private boolean gjelderFødsel;
    private boolean erBesteberegning;
    private boolean harBrukerAndel;
    private boolean harArbeidsgiverAndel;

    private int antallPerioder;
    private boolean harInnvilgedePerioder;
    private int antallArbeidsgivere;
    private int dagerTaptFørTermin;
    private int disponibleDager;
    private int disponibleFellesDager;
    private String sisteDagAvSistePeriode;
    private String stønadsperiodeFom;
    private String stønadsperiodeTom;
    private int foreldrepengeperiodenUtvidetUker;
    private int antallBarn;
    private int prematurDager;

    private List<Utbetalingsperiode> perioder = new ArrayList<>();

    private long bruttoBeregningsgrunnlag;
    private boolean harBruktBruttoBeregningsgrunnlag;
    private List<BeregningsgrunnlagRegel> beregningsgrunnlagregler;

    private boolean inkludereInfoOmUtbetaling;
    private boolean inkludereUtbetaling;
    private boolean inkludereInnvilget;
    private boolean inkludereAvslag;

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private InnvilgelseForeldrepengerDokumentdata kladd;

        private Builder() {
            this.kladd = new InnvilgelseForeldrepengerDokumentdata();
        }

        public Builder medFellesDokumentData(FellesDokumentdata fellesDokumentdata) {
            this.kladd.felles = fellesDokumentdata;
            return this;
        }

        public Builder medBehandlingType(BehandlingType behandlingType) {
            this.kladd.behandlingType = behandlingType;
            return this;
        }

        public Builder medBehandlingResultatType(BehandlingResultatType behandlingResultatType) {
            this.kladd.behandlingResultatType = behandlingResultatType;
            return this;
        }

        public Builder medKonsekvensForInnvilgetYtelse(KonsekvensForInnvilgetYtelse konsekvensForInnvilgetYtelse) {
            this.kladd.konsekvensForInnvilgetYtelse = konsekvensForInnvilgetYtelse;
            return this;
        }

        public Builder medDekningsgrad(int dekningsgrad) {
            this.kladd.dekningsgrad = dekningsgrad;
            return this;
        }

        public Builder medSøknadsdato(String søknadsdato) {
            this.kladd.søknadsdato = søknadsdato;
            return this;
        }

        public Builder medDagsats(long dagsats) {
            this.kladd.dagsats = dagsats;
            return this;
        }

        public Builder medMånedsbeløp(long månedsbeløp) {
            this.kladd.månedsbeløp = månedsbeløp;
            return this;
        }

        public Builder medSekgG(long seksG) {
            this.kladd.seksG = seksG;
            return this;
        }

        public Builder medInntektOverSekgG(boolean inntektOverSeksG) {
            this.kladd.inntektOverSeksG = inntektOverSeksG;
            return this;
        }

        public Builder medForMyeUtbetalt(ForMyeUtbetalt forMyeUtbetalt) {
            this.kladd.forMyeUtbetalt = forMyeUtbetalt;
            return this;
        }

        public Builder medInntektMottattArbeidsgiver(boolean inntektMottattArbeidsgiver) {
            this.kladd.inntektMottattArbeidsgiver = inntektMottattArbeidsgiver;
            return this;
        }

        public Builder medAnnenForelderHarRett(boolean annenForelderHarRett) {
            this.kladd.annenForelderHarRett = annenForelderHarRett;
            return this;
        }

        public Builder medAnnenForelderHarRettVurdert(boolean annenForelderHarRettVurdert) {
            this.kladd.annenForelderHarRettVurdert = annenForelderHarRettVurdert;
            return this;
        }

        public Builder medAleneomsorgKode(AleneomsorgKode aleneomsorgKode) {
            this.kladd.aleneomsorgKode = aleneomsorgKode;
            return this;
        }

        public Builder medIkkeOmsorg(boolean ikkeOmsorg) {
            this.kladd.ikkeOmsorg = ikkeOmsorg;
            return this;
        }

        public Builder medBarnErFødt(boolean barnErFødt) {
            this.kladd.barnErFødt = barnErFødt;
            return this;
        }

        public Builder medÅrsakErFødselshendelse(boolean årsakErFødselshendelse) {
            this.kladd.årsakErFødselshendelse = årsakErFødselshendelse;
            return this;
        }

        public Builder medGjelderMor(boolean gjelderMor) {
            this.kladd.gjelderMor = gjelderMor;
            return this;
        }

        public Builder medGjelderFødsel(boolean gjelderFødsel) {
            this.kladd.gjelderFødsel = gjelderFødsel;
            return this;
        }

        public Builder medErBesteberegning(boolean erBesteberegning) {
            this.kladd.erBesteberegning = erBesteberegning;
            return this;
        }

        public Builder medHarBrukerAndel(boolean harBrukerAndel) {
            this.kladd.harBrukerAndel = harBrukerAndel;
            return this;
        }

        public Builder medHarArbeidsgiverAndel(boolean harArbeidsgiverAndel) {
            this.kladd.harArbeidsgiverAndel = harArbeidsgiverAndel;
            return this;
        }

        public Builder medHarInnvilgedePerioder(boolean harInnvilgedePerioder) {
            this.kladd.harInnvilgedePerioder = harInnvilgedePerioder;
            return this;
        }

        public Builder medAntallPerioder(int antallPerioder) {
            this.kladd.antallPerioder = antallPerioder;
            return this;
        }

        public Builder medAntallArbeidsgivere(int antallArbeidsgivere) {
            this.kladd.antallArbeidsgivere = antallArbeidsgivere;
            return this;
        }

        public Builder medDagerTaptFørTermin(int dagerTaptFørTermin) {
            this.kladd.dagerTaptFørTermin = dagerTaptFørTermin;
            return this;
        }

        public Builder medDisponibleDager(int disponibleDager) {
            this.kladd.disponibleDager = disponibleDager;
            return this;
        }

        public Builder medDisponibleFellesDager(int disponibleFellesDager) {
            this.kladd.disponibleFellesDager = disponibleFellesDager;
            return this;
        }

        public Builder medSisteDagAvSistePeriode(String sisteDagAvSistePeriode) {
            this.kladd.sisteDagAvSistePeriode = sisteDagAvSistePeriode;
            return this;
        }

        public Builder medStønadsperiodeFom(String stønadsperiodeFom) {
            this.kladd.stønadsperiodeFom = stønadsperiodeFom;
            return this;
        }

        public Builder medStønadsperiodeTom(String stønadsperiodeTom) {
            this.kladd.stønadsperiodeTom = stønadsperiodeTom;
            return this;
        }

        public Builder medForeldrepengeperiodenUtvidetUker(int foreldrepengeperiodenUtvidetUker) {
            this.kladd.foreldrepengeperiodenUtvidetUker = foreldrepengeperiodenUtvidetUker;
            return this;
        }

        public Builder medAntallBarn(int antallBarn) {
            this.kladd.antallBarn = antallBarn;
            return this;
        }

        public Builder medPrematurDager(int prematurDager) {
            this.kladd.prematurDager = prematurDager;
            return this;
        }

        public Builder medUtbetalingsperioder(List<Utbetalingsperiode> utbetalingsperioder) {
            this.kladd.perioder = utbetalingsperioder;
            return this;
        }

        public Builder medBruttoBeregningsgrunnlag(long bruttoBeregningsgrunnlag) {
            this.kladd.bruttoBeregningsgrunnlag = bruttoBeregningsgrunnlag;
            return this;
        }

        public Builder medHarBruktBruttoBeregningsgrunnlag(boolean harBruktBruttoBeregningsgrunnlag) {
            this.kladd.harBruktBruttoBeregningsgrunnlag = harBruktBruttoBeregningsgrunnlag;
            return this;
        }

        public Builder medBeregningsgrunnlagregler(List<BeregningsgrunnlagRegel> beregningsgrunnlagregler) {
            this.kladd.beregningsgrunnlagregler = beregningsgrunnlagregler;
            return this;
        }

        public Builder medInkludereInfoOmUtbetaling(boolean inkludereInfoOmUtbetaling) {
            this.kladd.inkludereInfoOmUtbetaling = inkludereInfoOmUtbetaling;
            return this;
        }

        public Builder medInkludereUtbetaling(boolean inkludereUtbetaling) {
            this.kladd.inkludereUtbetaling = inkludereUtbetaling;
            return this;
        }

        public Builder medInkludereInnvilget(boolean inkludereInnvilget) {
            this.kladd.inkludereInnvilget = inkludereInnvilget;
            return this;
        }

        public Builder medInkludereAvslag(boolean inkludereAvslag) {
            this.kladd.inkludereAvslag = inkludereAvslag;
            return this;
        }

        public InnvilgelseForeldrepengerDokumentdata build() {
            return this.kladd;
        }
    }
}

package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.Dokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.FellesDokumentdata;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class InnvilgelseForeldrepengerDokumentdata extends Dokumentdata {
    private String behandlingType;
    private String behandlingResultatType;
    private String konsekvensForInnvilgetYtelse;
    private String søknadsdato;
    private int dekningsgrad;
    private long dagsats;
    private long månedsbeløp;
    private long seksG;
    private boolean inntektOverSeksG;
    private ForMyeUtbetalt forMyeUtbetalt;
    private boolean inntektMottattArbeidsgiver;
    private boolean annenForelderHarRett;
    private VurderingsKode annenForelderHarRettVurdert;
    private VurderingsKode aleneomsorgKode;
    private boolean ikkeOmsorg;
    private boolean barnErFødt;
    private boolean årsakErFødselshendelse;
    private boolean gjelderMor;
    private boolean gjelderFødsel;
    private boolean erBesteberegning;
    private boolean ingenRefusjon;
    private boolean delvisRefusjon;
    private boolean fullRefusjon;
    private boolean fbEllerRvInnvilget;

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

    private int klagefristUker;
    private String lovhjemlerUttak;
    private String lovhjemlerBeregning;

    private boolean inkludereUtbetaling;
    private boolean inkludereUtbetNårGradering;
    private boolean inkludereInnvilget;
    private boolean inkludereAvslag;
    private boolean inkludereNyeOpplysningerUtbet;

    public String getBehandlingType() {
        return behandlingType;
    }

    public String getBehandlingResultatType() {
        return behandlingResultatType;
    }

    public String getKonsekvensForInnvilgetYtelse() {
        return konsekvensForInnvilgetYtelse;
    }

    public String getSøknadsdato() {
        return søknadsdato;
    }

    public int getDekningsgrad() {
        return dekningsgrad;
    }

    public long getDagsats() {
        return dagsats;
    }

    public long getMånedsbeløp() {
        return månedsbeløp;
    }

    public long getSeksG() {
        return seksG;
    }

    public boolean getInntektOverSeksG() {
        return inntektOverSeksG;
    }

    public ForMyeUtbetalt getForMyeUtbetalt() {
        return forMyeUtbetalt;
    }

    public boolean getInntektMottattArbeidsgiver() {
        return inntektMottattArbeidsgiver;
    }

    public boolean getAnnenForelderHarRett() {
        return annenForelderHarRett;
    }

    public VurderingsKode getAnnenForelderHarRettVurdert() {
        return annenForelderHarRettVurdert;
    }

    public VurderingsKode getAleneomsorgKode() {
        return aleneomsorgKode;
    }

    public boolean getIkkeOmsorg() {
        return ikkeOmsorg;
    }

    public boolean getBarnErFødt() {
        return barnErFødt;
    }

    public boolean getÅrsakErFødselshendelse() {
        return årsakErFødselshendelse;
    }

    public boolean getGjelderMor() {
        return gjelderMor;
    }

    public boolean getGjelderFødsel() {
        return gjelderFødsel;
    }

    public boolean getErBesteberegning() {
        return erBesteberegning;
    }

    public boolean getIngenRefusjon() {
        return ingenRefusjon;
    }

    public boolean getDelvisRefusjon() {
        return delvisRefusjon;
    }

    public boolean getFullRefusjon() {
        return fullRefusjon;
    }

    public boolean getFbEllerRvInnvilget() {
        return fbEllerRvInnvilget;
    }

    public int getAntallPerioder() {
        return antallPerioder;
    }

    public boolean getHarInnvilgedePerioder() {
        return harInnvilgedePerioder;
    }

    public int getAntallArbeidsgivere() {
        return antallArbeidsgivere;
    }

    public int getDagerTaptFørTermin() {
        return dagerTaptFørTermin;
    }

    public int getDisponibleDager() {
        return disponibleDager;
    }

    public int getDisponibleFellesDager() {
        return disponibleFellesDager;
    }

    public String getSisteDagAvSistePeriode() {
        return sisteDagAvSistePeriode;
    }

    public String getStønadsperiodeFom() {
        return stønadsperiodeFom;
    }

    public String getStønadsperiodeTom() {
        return stønadsperiodeTom;
    }

    public int getForeldrepengeperiodenUtvidetUker() {
        return foreldrepengeperiodenUtvidetUker;
    }

    public int getAntallBarn() {
        return antallBarn;
    }

    public int getPrematurDager() {
        return prematurDager;
    }

    public List<Utbetalingsperiode> getPerioder() {
        return perioder;
    }

    public long getBruttoBeregningsgrunnlag() {
        return bruttoBeregningsgrunnlag;
    }

    public boolean getHarBruktBruttoBeregningsgrunnlag() {
        return harBruktBruttoBeregningsgrunnlag;
    }

    public List<BeregningsgrunnlagRegel> getBeregningsgrunnlagregler() {
        return beregningsgrunnlagregler;
    }

    public int getKlagefristUker() {
        return klagefristUker;
    }

    public String getLovhjemlerUttak() { return lovhjemlerUttak; }

    public String getLovhjemlerBeregning() { return lovhjemlerBeregning; }

    public boolean getInkludereUtbetaling() {
        return inkludereUtbetaling;
    }

    public boolean getInkludereUtbetNårGradering() {
        return inkludereUtbetNårGradering;
    }

    public boolean getInkludereInnvilget() {
        return inkludereInnvilget;
    }

    public boolean getInkludereAvslag() {
        return inkludereAvslag;
    }

    public boolean getInkludereNyeOpplysningerUtbet() { return inkludereNyeOpplysningerUtbet; }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (InnvilgelseForeldrepengerDokumentdata) object;
        return Objects.equals(felles, that.felles)
                && Objects.equals(behandlingType, that.behandlingType)
                && Objects.equals(behandlingResultatType, that.behandlingResultatType)
                && Objects.equals(konsekvensForInnvilgetYtelse, that.konsekvensForInnvilgetYtelse)
                && Objects.equals(søknadsdato, that.søknadsdato)
                && Objects.equals(dekningsgrad, that.dekningsgrad)
                && Objects.equals(dagsats, that.dagsats)
                && Objects.equals(månedsbeløp, that.månedsbeløp)
                && Objects.equals(seksG, that.seksG)
                && Objects.equals(inntektOverSeksG, that.inntektOverSeksG)
                && Objects.equals(forMyeUtbetalt, that.forMyeUtbetalt)
                && Objects.equals(inntektMottattArbeidsgiver, that.inntektMottattArbeidsgiver)
                && Objects.equals(annenForelderHarRett, that.annenForelderHarRett)
                && Objects.equals(annenForelderHarRettVurdert, that.annenForelderHarRettVurdert)
                && Objects.equals(aleneomsorgKode, that.aleneomsorgKode)
                && Objects.equals(ikkeOmsorg, that.ikkeOmsorg)
                && Objects.equals(barnErFødt, that.barnErFødt)
                && Objects.equals(årsakErFødselshendelse, that.årsakErFødselshendelse)
                && Objects.equals(gjelderMor, that.gjelderMor)
                && Objects.equals(gjelderFødsel, that.gjelderFødsel)
                && Objects.equals(erBesteberegning, that.erBesteberegning)
                && Objects.equals(ingenRefusjon, that.ingenRefusjon)
                && Objects.equals(delvisRefusjon, that.delvisRefusjon)
                && Objects.equals(fullRefusjon, that.fullRefusjon)
                && Objects.equals(fbEllerRvInnvilget, that.fbEllerRvInnvilget)
                && Objects.equals(antallPerioder, that.antallPerioder)
                && Objects.equals(harInnvilgedePerioder, that.harInnvilgedePerioder)
                && Objects.equals(antallArbeidsgivere, that.antallArbeidsgivere)
                && Objects.equals(dagerTaptFørTermin, that.dagerTaptFørTermin)
                && Objects.equals(disponibleDager, that.disponibleDager)
                && Objects.equals(disponibleFellesDager, that.disponibleFellesDager)
                && Objects.equals(sisteDagAvSistePeriode, that.sisteDagAvSistePeriode)
                && Objects.equals(stønadsperiodeFom, that.stønadsperiodeFom)
                && Objects.equals(stønadsperiodeTom, that.stønadsperiodeTom)
                && Objects.equals(foreldrepengeperiodenUtvidetUker, that.foreldrepengeperiodenUtvidetUker)
                && Objects.equals(antallBarn, that.antallBarn)
                && Objects.equals(prematurDager, that.prematurDager)
                && Objects.equals(perioder, that.perioder)
                && Objects.equals(bruttoBeregningsgrunnlag, that.bruttoBeregningsgrunnlag)
                && Objects.equals(harBruktBruttoBeregningsgrunnlag, that.harBruktBruttoBeregningsgrunnlag)
                && Objects.equals(beregningsgrunnlagregler, that.beregningsgrunnlagregler)
                && Objects.equals(klagefristUker, that.klagefristUker)
                && Objects.equals(lovhjemlerUttak, that.lovhjemlerUttak)
                && Objects.equals(lovhjemlerBeregning, that.lovhjemlerBeregning)
                && Objects.equals(inkludereUtbetaling, that.inkludereUtbetaling)
                && Objects.equals(inkludereUtbetNårGradering, that.inkludereUtbetNårGradering)
                && Objects.equals(inkludereInnvilget, that.inkludereInnvilget)
                && Objects.equals(inkludereAvslag, that.inkludereAvslag)
                && Objects.equals(inkludereNyeOpplysningerUtbet, that.inkludereNyeOpplysningerUtbet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(felles, behandlingType, behandlingResultatType, konsekvensForInnvilgetYtelse, søknadsdato,
                dekningsgrad, dagsats, månedsbeløp, seksG, inntektOverSeksG, forMyeUtbetalt, inntektMottattArbeidsgiver,
                annenForelderHarRett, annenForelderHarRettVurdert, aleneomsorgKode, ikkeOmsorg, barnErFødt, årsakErFødselshendelse,
                gjelderMor, gjelderFødsel, erBesteberegning, ingenRefusjon, delvisRefusjon, fullRefusjon, fbEllerRvInnvilget,
                antallPerioder, harInnvilgedePerioder, antallArbeidsgivere, dagerTaptFørTermin, disponibleDager, disponibleFellesDager,
                sisteDagAvSistePeriode, stønadsperiodeFom, stønadsperiodeTom, foreldrepengeperiodenUtvidetUker, antallBarn, prematurDager,
                perioder, bruttoBeregningsgrunnlag, harBruktBruttoBeregningsgrunnlag, beregningsgrunnlagregler, klagefristUker,
                lovhjemlerUttak, lovhjemlerBeregning, inkludereUtbetaling, inkludereUtbetNårGradering, inkludereInnvilget, inkludereAvslag,
                inkludereNyeOpplysningerUtbet);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private InnvilgelseForeldrepengerDokumentdata kladd;

        private Builder() {
            this.kladd = new InnvilgelseForeldrepengerDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata fellesDokumentdata) {
            this.kladd.felles = fellesDokumentdata;
            return this;
        }

        public Builder medBehandlingType(String behandlingType) {
            this.kladd.behandlingType = behandlingType;
            return this;
        }

        public Builder medBehandlingResultatType(String behandlingResultatType) {
            this.kladd.behandlingResultatType = behandlingResultatType;
            return this;
        }

        public Builder medKonsekvensForInnvilgetYtelse(String konsekvensForInnvilgetYtelse) {
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

        public Builder medAnnenForelderHarRettVurdert(VurderingsKode annenForelderHarRettVurdert) {
            this.kladd.annenForelderHarRettVurdert = annenForelderHarRettVurdert;
            return this;
        }

        public Builder medAleneomsorgKode(VurderingsKode aleneomsorgKode) {
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

        public Builder medIngenRefusjon(boolean ingenRefusjon) {
            this.kladd.ingenRefusjon = ingenRefusjon;
            return this;
        }

        public Builder medDelvisRefusjon(boolean delvisRefusjon) {
            this.kladd.delvisRefusjon = delvisRefusjon;
            return this;
        }

        public Builder medFullRefusjon(boolean fullRefusjon) {
            this.kladd.fullRefusjon = fullRefusjon;
            return this;
        }

        public Builder medFbEllerRvInnvilget(boolean fbEllerRvInnvilget) {
            this.kladd.fbEllerRvInnvilget = fbEllerRvInnvilget;
            return this;
        }

        public Builder medAntallPerioder(int antallPerioder) {
            this.kladd.antallPerioder = antallPerioder;
            return this;
        }

        public Builder medHarInnvilgedePerioder(boolean harInnvilgedePerioder) {
            this.kladd.harInnvilgedePerioder = harInnvilgedePerioder;
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

        public Builder medKlagefristUker(int klagefristUker) {
            this.kladd.klagefristUker = klagefristUker;
            return this;
        }

        public Builder medLovhjemlerUttak(String lovhjemlerUttak) {
            this.kladd.lovhjemlerUttak = lovhjemlerUttak;
            return this;
        }

        public Builder medLovhjemlerBeregning(String lovhjemlerBeregning) {
            this.kladd.lovhjemlerBeregning = lovhjemlerBeregning;
            return this;
        }

        public Builder medInkludereUtbetaling(boolean inkludereUtbetaling) {
            this.kladd.inkludereUtbetaling = inkludereUtbetaling;
            return this;
        }

        public Builder medInkludereUtbetNårGradering(boolean inkludereUtbetNårGradering) {
            this.kladd.inkludereUtbetNårGradering = inkludereUtbetNårGradering;
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

        public Builder medInkludereNyeOpplysningerUtbet(boolean inkludereNyeOpplysningerUtbet) {
            this.kladd.inkludereNyeOpplysningerUtbet = inkludereNyeOpplysningerUtbet;
            return this;
        }

        public InnvilgelseForeldrepengerDokumentdata build() {
            return this.kladd;
        }
    }
}

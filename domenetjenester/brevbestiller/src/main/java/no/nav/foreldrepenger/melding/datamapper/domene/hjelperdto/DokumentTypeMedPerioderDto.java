package no.nav.foreldrepenger.melding.datamapper.domene.hjelperdto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import no.nav.foreldrepenger.melding.datamapper.domene.sortering.LovhjemmelComparator;

@Deprecated
public class DokumentTypeMedPerioderDto extends DokumentTypeDto {
    private LocalDate stønadsperiodeFom;
    private LocalDate stønadsperiodeTom;
    private String arbeidsgiversNavn;
    private Boolean annenForelderHarRett;
    private Boolean fødselsHendelse;
    private String aleneomsorg;
    private Long bruttoBeregningsgrunnlag = 0L;
    private Integer dagerTaptFørTermin;
    private Integer antallPerioder;
    private Integer antallAvslag;
    private LocalDate sisteDagIFellesPeriode;
    private LocalDate sisteUtbetalingsdag;
    private LocalDate sisteDagMedUtsettelse;
    private Set<PeriodeDto> periode = new TreeSet<>();
    private Integer disponibleDager;
    private Integer disponibleFellesDager;
    private Set<String> lovhjemmelVurdering = new TreeSet<>(new LovhjemmelComparator());
    private Integer foreldrepengeperiodenUtvidetUker;
    private int antallTapteDager = 0;
    private LocalDate mottattInntektsmelding;
    private Integer antallInnvilget;
    private boolean overstyrtBeløpBeregning;
    private boolean inntektMottattArbgiver;
    private boolean innvilgetGraderingFinnes;
    private LocalDate sisteDagAvSistePeriode;
    private List<FeriePeriodeDto> feriePerioder = new ArrayList<>();
    private String forMyeUtbetalt;

    public DokumentTypeMedPerioderDto(Long behandlingId) {
        super(behandlingId);
    }

    public int getAntallTapteDager() {
        return antallTapteDager;
    }

    public boolean isInnvilgetGraderingFinnes() {
        return innvilgetGraderingFinnes;
    }

    public boolean isOverstyrtBeløpBeregning() {
        return overstyrtBeløpBeregning;
    }

    public void setOverstyrtBeløpBeregning(boolean overstyrtBeløpBeregning) {
        this.overstyrtBeløpBeregning = overstyrtBeløpBeregning;
    }

    public LocalDate getStønadsperiodeFom() {
        return stønadsperiodeFom;
    }

    public void setStønadsperiodeFom(LocalDate stønadsperiodeFom) {
        this.stønadsperiodeFom = stønadsperiodeFom;
    }

    public LocalDate getStønadsperiodeTom() {
        return stønadsperiodeTom;
    }

    public void setStønadsperiodeTom(LocalDate stønadsperiodeTom) {
        this.stønadsperiodeTom = stønadsperiodeTom;
    }

    public Boolean getAnnenForelderHarRett() {
        return annenForelderHarRett;
    }

    public void setAnnenForelderHarRett(Boolean annenForelderHarRett) {
        this.annenForelderHarRett = annenForelderHarRett;
    }

    public Boolean getFødselsHendelse() {
        return fødselsHendelse;
    }

    public void setFødselsHendelse(Boolean fødselsHendelse) {
        this.fødselsHendelse = fødselsHendelse;
    }

    public String getAleneomsorg() {
        return aleneomsorg;
    }

    public void setAleneomsorg(String aleneomsorg) {
        this.aleneomsorg = aleneomsorg;
    }

    public Integer getDagerTaptFørTermin() {
        return dagerTaptFørTermin;
    }

    public void setDagerTaptFørTermin(Integer dagerTaptFørTermin) {
        this.dagerTaptFørTermin = dagerTaptFørTermin;
    }

    public Integer getAntallPerioder() {
        return antallPerioder;
    }

    public void setAntallPerioder(Integer antallPerioder) {
        this.antallPerioder = antallPerioder;
    }

    public Integer getAntallAvslag() {
        return antallAvslag;
    }

    public void setAntallAvslag(Integer antallAvslag) {
        this.antallAvslag = antallAvslag;
    }

    @Override
    public LocalDate getSisteDagIFellesPeriode() {
        return sisteDagIFellesPeriode;
    }

    @Override
    public void setSisteDagIFellesPeriode(LocalDate sisteDagIFellesPeriode) {
        this.sisteDagIFellesPeriode = sisteDagIFellesPeriode;
    }

    public LocalDate getSisteUtbetalingsdag() {
        return sisteUtbetalingsdag;
    }

    public void setSisteUtbetalingsdag(LocalDate sisteUtbetalingsdag) {
        this.sisteUtbetalingsdag = sisteUtbetalingsdag;
    }

    public LocalDate getSisteDagMedUtsettelse() {
        return sisteDagMedUtsettelse;
    }

    public void setSisteDagMedUtsettelse(LocalDate sisteDagMedUtsettelse) {
        this.sisteDagMedUtsettelse = sisteDagMedUtsettelse;
    }

    public Set<PeriodeDto> getPeriode() {
        return periode;
    }

    public void addPeriode(PeriodeDto periode) {
        this.periode.add(periode);
        antallTapteDager += periode.getAntallTapteDager();
        if (periode.isGraderingFinnes() && periode.getInnvilget()) innvilgetGraderingFinnes = true;
    }

    public Integer getDisponibleDager() {
        return disponibleDager;
    }

    public void setDisponibleDager(Integer disponibleDager) {
        this.disponibleDager = disponibleDager;
    }

    public Integer getDisponibleFellesDager() {
        return disponibleFellesDager;
    }

    public void setDisponibleFellesDager(Integer disponibleFellesDager) {
        this.disponibleFellesDager = disponibleFellesDager;
    }

    public Set<String> getLovhjemmelVurdering() {
        return lovhjemmelVurdering;
    }

    public void leggTilLovhjemmelVurdering(String lovhjemmelVurdering) {
        this.lovhjemmelVurdering.add(lovhjemmelVurdering);
    }

    public void addLovhjemler(Set<String> lovhjemler) {
        this.lovhjemmelVurdering.addAll(lovhjemler);
    }

    public boolean isInntektMottattArbgiver() {
        return inntektMottattArbgiver;
    }

    public void setInntektMottattArbgiver(boolean inntektMottattArbgiver) {
        this.inntektMottattArbgiver = inntektMottattArbgiver;
    }

    public Integer getForeldrepengeperiodenUtvidetUker() {
        return foreldrepengeperiodenUtvidetUker;
    }

    public void setForeldrepengeperiodenUtvidetUker(Integer foreldrepengeperiodenUtvidetUker) {
        this.foreldrepengeperiodenUtvidetUker = foreldrepengeperiodenUtvidetUker;
    }

    public Integer getAntallInnvilget() {
        return antallInnvilget;
    }

    public void setAntallInnvilget(Integer antallInnvilget) {
        this.antallInnvilget = antallInnvilget;
    }

    public LocalDate getSisteDagAvSistePeriode() {
        return sisteDagAvSistePeriode;
    }

    public void setSisteDagAvSistePeriode(LocalDate sisteDagAvSistePeriode) {
        this.sisteDagAvSistePeriode = sisteDagAvSistePeriode;
    }

    public String getArbeidsgiversNavn() {
        return arbeidsgiversNavn;
    }

    public void setArbeidsgiversNavn(String arbeidsgiversNavn) {
        this.arbeidsgiversNavn = arbeidsgiversNavn;
    }

    public LocalDate getMottattInntektsmelding() {
        return mottattInntektsmelding;
    }

    public void setMottattInntektsmelding(LocalDate mottattInntektsmelding) {
        this.mottattInntektsmelding = mottattInntektsmelding;
    }

    public List<FeriePeriodeDto> getFeriePerioder() {
        return feriePerioder;
    }

    public void setFeriePerioder(List<FeriePeriodeDto> feriePerioder) {
        this.feriePerioder = feriePerioder;
    }

    public Long getBruttoBeregningsgrunnlag() {
        return bruttoBeregningsgrunnlag;
    }

    public void addToBruttoBeregningsgrunnlag(Long bruttoAndel) {
        this.bruttoBeregningsgrunnlag += bruttoAndel;
    }

    public String getForMyeUtbetalt() {
        return forMyeUtbetalt;
    }

    public void setForMyeUtbetalt(String utbetaltKode) {
        this.forMyeUtbetalt = utbetaltKode;
    }

    @Override
    public String toString() {
        return "DokumentTypeMedPerioderDto{" +
                "stønadsperiodeFom=" + stønadsperiodeFom +
                ", stønadsperiodeTom=" + stønadsperiodeTom +
                ", arbeidsgiversNavn='" + arbeidsgiversNavn + '\'' +
                ", annenForelderHarRett=" + annenForelderHarRett +
                ", fødselsHendelse=" + fødselsHendelse +
                ", aleneomsorg='" + aleneomsorg + '\'' +
                ", bruttoBeregningsgrunnlag=" + bruttoBeregningsgrunnlag +
                ", dagerTaptFørTermin=" + dagerTaptFørTermin +
                ", antallPerioder=" + antallPerioder +
                ", antallAvslag=" + antallAvslag +
                ", sisteDagIFellesPeriode=" + sisteDagIFellesPeriode +
                ", sisteUtbetalingsdag=" + sisteUtbetalingsdag +
                ", sisteDagMedUtsettelse=" + sisteDagMedUtsettelse +
                ", periode=" + periode +
                ", disponibleDager=" + disponibleDager +
                ", lovhjemmelVurdering=" + lovhjemmelVurdering +
                ", foreldrepengeperiodenUtvidetUker=" + foreldrepengeperiodenUtvidetUker +
                ", mottattInntektsmelding=" + mottattInntektsmelding +
                ", antallInnvilget=" + antallInnvilget +
                ", overstyrtBeløpBeregning=" + overstyrtBeløpBeregning +
                ", inntektMottattArbgiver=" + inntektMottattArbgiver +
                ", sisteDagAvSistePeriode=" + sisteDagAvSistePeriode +
                ", feriePerioder=" + feriePerioder +
                ", forMyeUtbetalt=" + forMyeUtbetalt +
                '}';
    }
}

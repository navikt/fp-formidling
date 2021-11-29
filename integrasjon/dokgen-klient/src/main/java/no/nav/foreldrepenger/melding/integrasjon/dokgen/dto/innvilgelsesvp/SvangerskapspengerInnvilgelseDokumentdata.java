package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsesvp;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Dokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.FellesDokumentdata;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class SvangerskapspengerInnvilgelseDokumentdata extends Dokumentdata {
    private boolean revurdering;
    private boolean endretFraAvslag;
    private boolean utbetalingEndret;
    private boolean termindatoEndret;
    private boolean refusjonTilBruker;
    private int antallRefusjonerTilArbeidsgivere;
    private String stønadsperiodeTom;
    private long månedsbeløp;
    private String mottattDato;
    private int klagefristUker;
    private int antallUttaksperioder;
    private List<Uttaksaktivitet> uttaksaktiviteter;
    private List<Utbetalingsperiode> utbetalingsperioder;
    private List<Avslagsperiode> avslagsperioder;
    private List<AvslåttAktivitet> avslåtteAktiviteter;
    private boolean inkludereBeregning;
    private List<Arbeidsforhold> arbeidsforhold;
    private SelvstendigNæringsdrivende selvstendigNæringsdrivende;
    private Frilanser frilanser;
    private List<Naturalytelse> naturalytelser;
    private long bruttoBeregningsgrunnlag;
    private boolean militærSivil;
    private boolean inntektOver6G;
    private long seksG;
    private String lovhjemmel;

    public boolean getRevurdering() {
        return revurdering;
    }

    public boolean getEndretFraAvslag() {
        return endretFraAvslag;
    }

    public boolean getUtbetalingEndret() {
        return utbetalingEndret;
    }

    public boolean getTermindatoEndret() {
        return termindatoEndret;
    }

    public boolean getRefusjonTilBruker() {
        return refusjonTilBruker;
    }

    public int getAntallRefusjonerTilArbeidsgivere() {
        return antallRefusjonerTilArbeidsgivere;
    }

    public String getStønadsperiodeTom() {
        return stønadsperiodeTom;
    }

    public long getMånedsbeløp() {
        return månedsbeløp;
    }

    public String getMottattDato() {
        return mottattDato;
    }

    public int getKlagefristUker() {
        return klagefristUker;
    }

    public int getAntallUttaksperioder() {
        return antallUttaksperioder;
    }

    public List<Uttaksaktivitet> getUttaksaktiviteter() {
        return uttaksaktiviteter;
    }

    public List<Utbetalingsperiode> getUtbetalingsperioder() {
        return utbetalingsperioder;
    }

    public List<Avslagsperiode> getAvslagsperioder() {
        return avslagsperioder;
    }

    public List<AvslåttAktivitet> getAvslåtteAktiviteter() {
        return avslåtteAktiviteter;
    }

    public boolean getInkludereBeregning() {
        return inkludereBeregning;
    }

    public List<Arbeidsforhold> getArbeidsforhold() {
        return arbeidsforhold;
    }

    public SelvstendigNæringsdrivende getSelvstendigNæringsdrivende() {
        return selvstendigNæringsdrivende;
    }

    public Frilanser getFrilanser() {
        return frilanser;
    }

    public List<Naturalytelse> getNaturalytelser() {
        return naturalytelser;
    }

    public long getBruttoBeregningsgrunnlag() {
        return bruttoBeregningsgrunnlag;
    }

    public boolean getMilitærSivil() {
        return militærSivil;
    }

    public boolean getInntektOver6G() {
        return inntektOver6G;
    }

    public long getSeksG() {
        return seksG;
    }

    public String getLovhjemmel() {
        return lovhjemmel;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (SvangerskapspengerInnvilgelseDokumentdata) object;
        return Objects.equals(felles, that.felles)
                && Objects.equals(revurdering, that.revurdering)
                && Objects.equals(endretFraAvslag, that.endretFraAvslag)
                && Objects.equals(utbetalingEndret, that.utbetalingEndret)
                && Objects.equals(termindatoEndret, that.termindatoEndret)
                && Objects.equals(refusjonTilBruker, that.refusjonTilBruker)
                && Objects.equals(antallRefusjonerTilArbeidsgivere, that.antallRefusjonerTilArbeidsgivere)
                && Objects.equals(stønadsperiodeTom, that.stønadsperiodeTom)
                && Objects.equals(månedsbeløp, that.månedsbeløp)
                && Objects.equals(mottattDato, that.mottattDato)
                && Objects.equals(klagefristUker, that.klagefristUker)
                && Objects.equals(antallUttaksperioder, that.antallUttaksperioder)
                && Objects.equals(uttaksaktiviteter, that.uttaksaktiviteter)
                && Objects.equals(utbetalingsperioder, that.utbetalingsperioder)
                && Objects.equals(avslagsperioder, that.avslagsperioder)
                && Objects.equals(avslåtteAktiviteter, that.avslåtteAktiviteter)
                && Objects.equals(inkludereBeregning, that.inkludereBeregning)
                && Objects.equals(arbeidsforhold, that.arbeidsforhold)
                && Objects.equals(selvstendigNæringsdrivende, that.selvstendigNæringsdrivende)
                && Objects.equals(frilanser, that.frilanser)
                && Objects.equals(naturalytelser, that.naturalytelser)
                && Objects.equals(bruttoBeregningsgrunnlag, that.bruttoBeregningsgrunnlag)
                && Objects.equals(militærSivil, that.militærSivil)
                && Objects.equals(inntektOver6G, that.inntektOver6G)
                && Objects.equals(seksG, that.seksG)
                && Objects.equals(lovhjemmel, that.lovhjemmel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(felles, revurdering, endretFraAvslag, utbetalingEndret, termindatoEndret, refusjonTilBruker,
                antallRefusjonerTilArbeidsgivere, stønadsperiodeTom, månedsbeløp, mottattDato, klagefristUker, antallUttaksperioder,
                uttaksaktiviteter, utbetalingsperioder, avslagsperioder, avslåtteAktiviteter, inkludereBeregning,
                arbeidsforhold, selvstendigNæringsdrivende, frilanser, naturalytelser, bruttoBeregningsgrunnlag, militærSivil,
                inntektOver6G, seksG, lovhjemmel);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private SvangerskapspengerInnvilgelseDokumentdata kladd;

        private Builder() {
            this.kladd = new SvangerskapspengerInnvilgelseDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata fellesDokumentdata) {
            this.kladd.felles = fellesDokumentdata;
            return this;
        }

        public Builder medRevurdering(boolean revurdering) {
            this.kladd.revurdering = revurdering;
            return this;
        }

        public Builder medEndretFraAvslag(boolean endretFraAvslag) {
            this.kladd.endretFraAvslag = endretFraAvslag;
            return this;
        }

        public Builder medUtbetalingEndret(boolean utbetalingEndret) {
            this.kladd.utbetalingEndret = utbetalingEndret;
            return this;
        }

        public Builder medTermindatoEndret(boolean termindatoEndret) {
            this.kladd.termindatoEndret = termindatoEndret;
            return this;
        }

        public Builder medRefusjonTilBruker(boolean refusjonTilBruker) {
            this.kladd.refusjonTilBruker = refusjonTilBruker;
            return this;
        }

        public Builder medAntallRefusjonerTilArbeidsgivere(int antallRefusjonerTilArbeidsgivere) {
            this.kladd.antallRefusjonerTilArbeidsgivere = antallRefusjonerTilArbeidsgivere;
            return this;
        }

        public Builder medStønadsperiodeTom(String stønadsperiodeTom) {
            this.kladd.stønadsperiodeTom = stønadsperiodeTom;
            return this;
        }

        public Builder medMånedsbeløp(long månedsbeløp) {
            this.kladd.månedsbeløp = månedsbeløp;
            return this;
        }

        public Builder medMottattDato(String mottattDato) {
            this.kladd.mottattDato = mottattDato;
            return this;
        }

        public Builder medKlagefristUker(int klagefristUker) {
            this.kladd.klagefristUker = klagefristUker;
            return this;
        }

        public Builder medAntallUttaksperioder(int antallUttaksperioder) {
            this.kladd.antallUttaksperioder = antallUttaksperioder;
            return this;
        }

        public Builder medUttaksaktiviteter(List<Uttaksaktivitet> uttaksaktiviteter) {
            this.kladd.uttaksaktiviteter = uttaksaktiviteter;
            return this;
        }

        public Builder medUtbetalingsperioder(List<Utbetalingsperiode> utbetalingsperioder) {
            this.kladd.utbetalingsperioder = utbetalingsperioder;
            return this;
        }

        public Builder medAvslagsperioder(List<Avslagsperiode> avslagsperioder) {
            this.kladd.avslagsperioder = avslagsperioder;
            return this;
        }

        public Builder medAvslåtteAktiviteter(List<AvslåttAktivitet> avslåtteAktiviteter) {
            this.kladd.avslåtteAktiviteter = avslåtteAktiviteter;
            return this;
        }

        public Builder medInkludereBeregning(boolean inkludereBeregning) {
            this.kladd.inkludereBeregning = inkludereBeregning;
            return this;
        }

        public Builder medArbeidsforhold(List<Arbeidsforhold> arbeidsforhold) {
            this.kladd.arbeidsforhold = arbeidsforhold;
            return this;
        }

        public Builder medSelvstendigNæringsdrivende(SelvstendigNæringsdrivende selvstendigNæringsdrivende) {
            this.kladd.selvstendigNæringsdrivende = selvstendigNæringsdrivende;
            return this;
        }

        public Builder medFrilanser(Frilanser frilanser) {
            this.kladd.frilanser = frilanser;
            return this;
        }

        public Builder medNaturalytelser(List<Naturalytelse> naturalytelser) {
            this.kladd.naturalytelser = naturalytelser;
            return this;
        }

        public Builder medBruttoBeregningsgrunnlag(long bruttoBeregningsgrunnlag) {
            this.kladd.bruttoBeregningsgrunnlag = bruttoBeregningsgrunnlag;
            return this;
        }

        public Builder medMilitærSivil(boolean militærSivil) {
            this.kladd.militærSivil = militærSivil;
            return this;
        }

        public Builder medInntektOver6G(boolean inntektOver6G) {
            this.kladd.inntektOver6G = inntektOver6G;
            return this;
        }

        public Builder medSeksG(long seksG) {
            this.kladd.seksG = seksG;
            return this;
        }

        public Builder medLovhjemmel(String lovhjemmel) {
            this.kladd.lovhjemmel = lovhjemmel;
            return this;
        }

        public SvangerskapspengerInnvilgelseDokumentdata build() {
            return this.kladd;
        }
    }
}

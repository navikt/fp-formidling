package no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.AnnenAktivitetListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.AnnenAktivitetType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.ArbeidsforholdListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.ArbeidsforholdType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.NæringListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.NæringType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.PeriodeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.StatusTypeKode;

public class PeriodeMergerVerktøyTest {

    ObjectFactory objectFactory = new ObjectFactory();
    PeriodeType periodeEn = objectFactory.createPeriodeType();
    PeriodeType periodeTo = objectFactory.createPeriodeType();

    @Test
    public void skal_finne_like_aktiviteter_når_ikke_satt() {
        assertThat(PeriodeMergerVerktøy.likeAktiviteter(periodeEn, periodeTo)).isTrue();
    }

    @Test
    public void skal_finne_at_aktiviteter_er_ulike() {
        periodeTo.setNæringListe(objectFactory.createNæringListeType());
        assertThat(PeriodeMergerVerktøy.likeAktiviteter(periodeEn, periodeTo)).isFalse();
        periodeTo = objectFactory.createPeriodeType();
        periodeTo.setArbeidsforholdListe(objectFactory.createArbeidsforholdListeType());
        assertThat(PeriodeMergerVerktøy.likeAktiviteter(periodeEn, periodeTo)).isFalse();
        periodeTo = objectFactory.createPeriodeType();
        periodeTo.setAnnenAktivitetListe(objectFactory.createAnnenAktivitetListeType());
        assertThat(PeriodeMergerVerktøy.likeAktiviteter(periodeEn, periodeTo)).isFalse();
    }

    @Test
    public void skal_finne_like_aktiviteter_kompleks_næring() {
        NæringListeType næringListeEn = objectFactory.createNæringListeType();
        NæringListeType næringListeTo = objectFactory.createNæringListeType();

        periodeEn.setNæringListe(næringListeEn);
        periodeTo.setNæringListe(næringListeTo);
        assertThat(PeriodeMergerVerktøy.likeAktiviteter(periodeEn, periodeTo)).isTrue();

        NæringType næringEn = objectFactory.createNæringType();
        periodeEn.getNæringListe().setNæring(næringEn);
        assertThat(PeriodeMergerVerktøy.likeAktiviteter(periodeEn, periodeTo)).isFalse();

        NæringType næringTo = objectFactory.createNæringType();
        næringTo.setInntekt1(200L);
        periodeTo.getNæringListe().setNæring(næringTo);
        assertThat(PeriodeMergerVerktøy.likeAktiviteter(periodeEn, periodeTo)).isFalse();

        periodeEn.getNæringListe().getNæring().setInntekt1(200L);
        assertThat(PeriodeMergerVerktøy.likeAktiviteter(periodeEn, periodeTo)).isTrue();

        periodeEn.getNæringListe().getNæring().setUttaksgrad(BigInteger.ONE);
        assertThat(PeriodeMergerVerktøy.likeAktiviteter(periodeEn, periodeTo)).isTrue();

        periodeEn.getNæringListe().getNæring().setSistLignedeÅr(BigInteger.valueOf(2019));
        assertThat(PeriodeMergerVerktøy.likeAktiviteter(periodeEn, periodeTo)).isFalse();

        periodeTo.getNæringListe().getNæring().setSistLignedeÅr(BigInteger.valueOf(2019));
        assertThat(PeriodeMergerVerktøy.likeAktiviteter(periodeEn, periodeTo)).isTrue();
    }

    @Test
    public void skal_finne_like_aktiviteter_kompleks_annen_aktivitet() {
        AnnenAktivitetListeType annenAktivitetListeEn = objectFactory.createAnnenAktivitetListeType();
        AnnenAktivitetListeType annenAktivitetListeTo = objectFactory.createAnnenAktivitetListeType();
        periodeEn.setAnnenAktivitetListe(annenAktivitetListeEn);
        periodeTo.setAnnenAktivitetListe(annenAktivitetListeTo);

        assertThat(PeriodeMergerVerktøy.likeAktiviteter(periodeEn, periodeTo)).isTrue();

        periodeEn.getAnnenAktivitetListe().getAnnenAktivitet().add(opprettAnnenAktivitet(100L, null));
        assertThat(PeriodeMergerVerktøy.likeAktiviteter(periodeEn, periodeTo)).isFalse();

        periodeTo.getAnnenAktivitetListe().getAnnenAktivitet().add(opprettAnnenAktivitet(200L, null));
        assertThat(PeriodeMergerVerktøy.likeAktiviteter(periodeEn, periodeTo)).isFalse();

        annenAktivitetListeTo = objectFactory.createAnnenAktivitetListeType();
        periodeTo.setAnnenAktivitetListe(annenAktivitetListeTo);
        periodeTo.getAnnenAktivitetListe().getAnnenAktivitet().add(opprettAnnenAktivitet(100L, null));
        assertThat(PeriodeMergerVerktøy.likeAktiviteter(periodeEn, periodeTo)).isTrue();

        periodeTo.getAnnenAktivitetListe().getAnnenAktivitet().add(opprettAnnenAktivitet(100L, null));
        assertThat(PeriodeMergerVerktøy.likeAktiviteter(periodeEn, periodeTo)).isFalse();

        periodeEn.getAnnenAktivitetListe().getAnnenAktivitet().add(opprettAnnenAktivitet(100L, StatusTypeKode.ARBEIDSTAKER));
        assertThat(PeriodeMergerVerktøy.likeAktiviteter(periodeEn, periodeTo)).isFalse();
    }

    @Test
    public void skal_finne_like_aktiviteter_kompleks_arbeidsforhold() {
        ArbeidsforholdListeType arbeidsforholdListeEn = objectFactory.createArbeidsforholdListeType();
        ArbeidsforholdListeType arbeidsforholdListeTo = objectFactory.createArbeidsforholdListeType();
        periodeEn.setArbeidsforholdListe(arbeidsforholdListeEn);
        periodeTo.setArbeidsforholdListe(arbeidsforholdListeTo);

        assertThat(PeriodeMergerVerktøy.likeAktiviteter(periodeEn, periodeTo)).isTrue();

        periodeEn.getArbeidsforholdListe().getArbeidsforhold().add(opprettArbeidsforhold(false, 100L, null));
        assertThat(PeriodeMergerVerktøy.likeAktiviteter(periodeEn, periodeTo)).isFalse();

        periodeTo.getArbeidsforholdListe().getArbeidsforhold().add(opprettArbeidsforhold(true, 100L, null));
        assertThat(PeriodeMergerVerktøy.likeAktiviteter(periodeEn, periodeTo)).isTrue();

        periodeTo.getArbeidsforholdListe().getArbeidsforhold().add(opprettArbeidsforhold(false, 200L, null));
        periodeEn.getArbeidsforholdListe().getArbeidsforhold().add(opprettArbeidsforhold(false, 200L, 200L));
        assertThat(PeriodeMergerVerktøy.likeAktiviteter(periodeEn, periodeTo)).isFalse();
    }

    private ArbeidsforholdType opprettArbeidsforhold(boolean gradering, long dagsats, Long naturalytelseNyDagsats) {
        ArbeidsforholdType arbeidsforhold = objectFactory.createArbeidsforholdType();
        arbeidsforhold.setAktivitetDagsats(dagsats);
        arbeidsforhold.setGradering(gradering);
        arbeidsforhold.setNaturalytelseNyDagsats(naturalytelseNyDagsats);
        return arbeidsforhold;
    }

    private AnnenAktivitetType opprettAnnenAktivitet(long dagsats, StatusTypeKode aktivitetType) {
        AnnenAktivitetType annenAktivitet = objectFactory.createAnnenAktivitetType();
        annenAktivitet.setAktivitetDagsats(dagsats);
        annenAktivitet.setAktivitetType(aktivitetType);
        return annenAktivitet;
    }

}

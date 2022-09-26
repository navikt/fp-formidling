package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import static no.nav.foreldrepenger.fpformidling.uttak.kodeliste.PeriodeResultatÅrsak.ARBEIDER_I_UTTAKSPERIODEN_MER_ENN_0_PROSENT;
import static no.nav.foreldrepenger.fpformidling.uttak.kodeliste.PeriodeResultatÅrsak.AVSLAG_GRADERING_PÅ_GRUNN_AV_FOR_SEN_SØKNAD;
import static no.nav.foreldrepenger.fpformidling.uttak.kodeliste.PeriodeResultatÅrsak.FOR_SEN_SØKNAD;
import static no.nav.foreldrepenger.fpformidling.uttak.kodeliste.PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_PGA_100_PROSENT_ARBEID;
import static no.nav.foreldrepenger.fpformidling.uttak.kodeliste.PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_PGA_ARBEID_KUN_FAR_HAR_RETT;
import static no.nav.foreldrepenger.fpformidling.uttak.kodeliste.PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_PGA_FERIE;
import static no.nav.foreldrepenger.fpformidling.uttak.kodeliste.PeriodeResultatÅrsak.UTSETTELSE_GYLDIG_PGA_FERIE_KUN_FAR_HAR_RETT;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.AnnenAktivitet;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Arbeidsforhold;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.ForMyeUtbetalt;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Næring;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Utbetalingsperiode;

public final class ForMyeUtbetaltMapper {
    private static final List<Årsak> manglendeEllerForSenSøknadOmGraderingÅrsaker = List.of(
            Årsak.of(ARBEIDER_I_UTTAKSPERIODEN_MER_ENN_0_PROSENT.getKode()),
            Årsak.of(AVSLAG_GRADERING_PÅ_GRUNN_AV_FOR_SEN_SØKNAD.getKode()),
            Årsak.of(FOR_SEN_SØKNAD.getKode()));

    private static final List<Årsak> innvilgetUtsettelsePgaFerieÅrsaker = List.of(
            Årsak.of(UTSETTELSE_GYLDIG_PGA_FERIE.getKode()),
            Årsak.of(UTSETTELSE_GYLDIG_PGA_FERIE_KUN_FAR_HAR_RETT.getKode()));

    private static final List<Årsak> innvilgetUtsettelsePgaArbeidÅrsaker = List.of(
            Årsak.of(UTSETTELSE_GYLDIG_PGA_100_PROSENT_ARBEID.getKode()),
            Årsak.of(UTSETTELSE_GYLDIG_PGA_ARBEID_KUN_FAR_HAR_RETT.getKode()));

    private ForMyeUtbetaltMapper() {
    }

    public static ForMyeUtbetalt forMyeUtbetalt(List<Utbetalingsperiode> periodeListe, Behandling behandling) {
        if (!behandling.erRevurdering()) {
            return null;
        }

        var vedtaksdato = behandling.getAvsluttet() != null ? behandling.getAvsluttet().toLocalDate() : null;
        LocalDate innvilgetUtsettelseFOM = null;

        var generell = false;
        var ferie = false;
        var jobb = false;

        for (var periode : periodeListe) {
            if (periodeHarGradering(periode) || manglendeEllerForSenSøknadOmGraderingÅrsaker.contains(periode.getÅrsak())) {
                generell = true;
                break;
            }
            if (innvilgetUtsettelsePgaFerieÅrsaker.contains(periode.getÅrsak())) {
                ferie = true;
                innvilgetUtsettelseFOM = tidligsteAv(innvilgetUtsettelseFOM, periode.getPeriodeFom());
            }
            if (innvilgetUtsettelsePgaArbeidÅrsaker.contains(periode.getÅrsak())) {
                jobb = true;
                innvilgetUtsettelseFOM = tidligsteAv(innvilgetUtsettelseFOM, periode.getPeriodeFom());
            }
        }
        return forMyeUtbetaltKode(generell, ferie, jobb, innvilgetUtsettelseFOM, vedtaksdato);
    }

    private static ForMyeUtbetalt forMyeUtbetaltKode(boolean generell, boolean ferie, boolean jobb,
                                                     LocalDate innvilgetUtsettelseFOM, LocalDate vedtaksdato) {
        if (generell) {
            return ForMyeUtbetalt.GENERELL;
        }
        if ((ferie || jobb) && erInnvilgetUtsettelseInneværendeMånedEllerTidligere(innvilgetUtsettelseFOM, vedtaksdato)) {
            if (ferie && jobb) {
                return ForMyeUtbetalt.GENERELL;
            }
            return ferie ? ForMyeUtbetalt.FERIE : ForMyeUtbetalt.JOBB;
        }
        return null;
    }

    private static boolean erInnvilgetUtsettelseInneværendeMånedEllerTidligere(LocalDate innvilgetUtsettelseFOM, LocalDate vedtaksdato) {
        var iDag = vedtaksdato != null ? vedtaksdato : LocalDate.now();
        return innvilgetUtsettelseFOM.isBefore(iDag.plusMonths(1).withDayOfMonth(1));
    }

    private static LocalDate tidligsteAv(LocalDate innvilgetUtsettelseFOM, LocalDate periodeFOM) {
        if (innvilgetUtsettelseFOM == null || periodeFOM.isBefore(innvilgetUtsettelseFOM)) {
            return periodeFOM;
        }
        return innvilgetUtsettelseFOM;
    }

    private static boolean periodeHarGradering(Utbetalingsperiode periodeType) {
        return periodeStreamInneholderGradering(List.of(periodeType));
    }

    private static Boolean periodeStreamInneholderGradering(List<Utbetalingsperiode> innvilgedePerioder) {
        return arbeidsforholdMedGraderingFinnes(innvilgedePerioder.stream()) ||
                annenAktivtitetMedGraderingFinnes(innvilgedePerioder.stream()) ||
                næringMedGraderingFinnes(innvilgedePerioder.stream());
    }

    private static boolean annenAktivtitetMedGraderingFinnes(Stream<Utbetalingsperiode> periodeStream) {
        return periodeStream
                .map(Utbetalingsperiode::getAnnenAktivitetsliste)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .anyMatch(AnnenAktivitet::isGradering);
    }

    private static boolean næringMedGraderingFinnes(Stream<Utbetalingsperiode> periodeStream) {
        return periodeStream
                .map(Utbetalingsperiode::getNæring)
                .filter(Objects::nonNull)
                .anyMatch(Næring::isGradering);
    }

    private static boolean arbeidsforholdMedGraderingFinnes(Stream<Utbetalingsperiode> periodeStream) {
        return periodeStream
                .map(Utbetalingsperiode::getArbeidsforholdsliste)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .anyMatch(Arbeidsforhold::isGradering);
    }
}

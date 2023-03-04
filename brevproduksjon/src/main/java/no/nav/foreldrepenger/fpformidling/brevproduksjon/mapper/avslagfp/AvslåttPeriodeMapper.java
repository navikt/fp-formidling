package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.avslagfp;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.PeriodeBeregner.alleAktiviteterHarNullUtbetaling;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.behandling.ÅrsakMedLovReferanse;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BehandlingMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.FellesMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.LovhjemmelComparator;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.LovhjemmelUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.PeriodeBeregner;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.Tuple;
import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.avslagfp.AvslåttPeriode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelsePeriode;
import no.nav.foreldrepenger.fpformidling.uttak.ForeldrepengerUttak;
import no.nav.foreldrepenger.fpformidling.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.fpformidling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.uttak.UttakResultatPeriodeAktivitet;

public final class AvslåttPeriodeMapper {

    private AvslåttPeriodeMapper() {
    }

    public static Tuple<List<AvslåttPeriode>, String> mapAvslåttePerioderOgLovhjemmel(Behandling behandling,
                                                                                      List<TilkjentYtelsePeriode> tilkjentYtelsePerioder,
                                                                                      Optional<ForeldrepengerUttak> uttakResultatPerioder) {
        var lovReferanser = new TreeSet<>(new LovhjemmelComparator());
        var behandlingsresultat = behandling.getBehandlingsresultat();

        var avslåttePerioder = finnAvslåttePerioder(tilkjentYtelsePerioder, uttakResultatPerioder, behandling.getSpråkkode(), lovReferanser);
        var lovhjemmelForAvslag = FellesMapper.formaterLovhjemlerUttak(lovReferanser,
            BehandlingMapper.kodeFra(behandlingsresultat.getKonsekvenserForYtelsen()), false);

        if (avslåttePerioder.isEmpty()) {
            avslåttePerioder = finnAvslåttePerioder(behandlingsresultat, uttakResultatPerioder, lovReferanser);
            lovhjemmelForAvslag = FellesMapper.formaterLovhjemlerUttak(lovReferanser);
        }
        avslåttePerioder = AvslåttPeriodeMerger.mergePerioder(avslåttePerioder);

        return new Tuple<>(avslåttePerioder, lovhjemmelForAvslag);
    }

    private static List<AvslåttPeriode> finnAvslåttePerioder(List<TilkjentYtelsePeriode> tilkjentYtelsePerioder,
                                                             Optional<ForeldrepengerUttak> uttakResultatPerioder,
                                                             Språkkode språkkode,
                                                             TreeSet<String> lovReferanser) {
        List<AvslåttPeriode> avslåttePerioder = new ArrayList<>();
        for (var tilkjentYtelsePeriode : tilkjentYtelsePerioder) {
            var matchetUttaksperiode = finnUttakResultatPeriode(uttakResultatPerioder, tilkjentYtelsePeriode);

            var avslåttPeriode = mapAvslåttPeriode(tilkjentYtelsePeriode, tilkjentYtelsePerioder, matchetUttaksperiode, språkkode, lovReferanser);
            avslåttePerioder.add(avslåttPeriode);
        }
        return avslåttePerioder;
    }

    private static List<AvslåttPeriode> finnAvslåttePerioder(Behandlingsresultat behandlingsresultat,
                                                             Optional<ForeldrepengerUttak> uttakResultatPerioder,
                                                             TreeSet<String> lovReferanser) {
        List<AvslåttPeriode> avslagsAarsaker = new ArrayList<>();

        var avslagsårsak = behandlingsresultat.getAvslagsårsak();
        if (avslagsårsak != null) {
            avslagsAarsaker.add(årsaktypeFra(avslagsårsak, lovReferanser));
        }
        for (var periode : uttakResultatPerioder.map(ForeldrepengerUttak::perioder).orElse(Collections.emptyList())) {
            var periodeResultatÅrsak = periode.getPeriodeResultatÅrsak();
            if (PeriodeResultatType.AVSLÅTT.equals(periode.getPeriodeResultatType()) && periodeResultatÅrsak != null) {
                avslagsAarsaker.add(årsaktypeFra(periodeResultatÅrsak, lovReferanser));
            }
        }
        return avslagsAarsaker;
    }

    private static AvslåttPeriode årsaktypeFra(ÅrsakMedLovReferanse årsakKode, TreeSet<String> lovReferanser) {
        var avslåttPeriode = AvslåttPeriode.ny().medAvslagsårsak(Årsak.of(årsakKode.getKode())).build();
        lovReferanser.addAll(mapLovReferanse(årsakKode));
        return avslåttPeriode;
    }

    private static AvslåttPeriode mapAvslåttPeriode(TilkjentYtelsePeriode tilkjentYtelsePeriode,
                                                    List<TilkjentYtelsePeriode> tilkjentPeriodeListe,
                                                    UttakResultatPeriode uttakResultatPeriode,
                                                    Språkkode språkkode,
                                                    TreeSet<String> lovReferanser) {
        //TFP-5200 Hack for å forhindre dobbelt antall tapte dager om det er flere tilkjentperioder for en uttaksperiode
        var antallTilkjentPerioderForUttaksperioden = PeriodeBeregner.finnAntallTilkjentePerioderForUttaksperioden(tilkjentPeriodeListe,
            uttakResultatPeriode);
        var antallTapteDager = mapAntallTapteDagerFra(uttakResultatPeriode.getAktiviteter());
        var tapteDagerHvisFlereTilkjentPerioder = BigDecimal.ZERO;
        if (antallTilkjentPerioderForUttaksperioden > 1) {
            tapteDagerHvisFlereTilkjentPerioder = BigDecimal.valueOf(antallTapteDager)
                .divide(BigDecimal.valueOf(antallTilkjentPerioderForUttaksperioden), 2, RoundingMode.HALF_UP);
        }
        var avslåttPeriode = AvslåttPeriode.ny()
            .medAvslagsårsak(Årsak.of(uttakResultatPeriode.getPeriodeResultatÅrsak().getKode()))
            .medPeriodeFom(tilkjentYtelsePeriode.getPeriodeFom(), språkkode)
            .medPeriodeTom(tilkjentYtelsePeriode.getPeriodeTom(), språkkode)
            .medAntallTapteDager(antallTapteDager, tapteDagerHvisFlereTilkjentPerioder)
            .build();
        lovReferanser.addAll(mapLovReferanse(uttakResultatPeriode.getPeriodeResultatÅrsak()));
        return avslåttPeriode;
    }

    private static Set<String> mapLovReferanse(ÅrsakMedLovReferanse periodeResultatÅrsak) {
        return LovhjemmelUtil.hentLovhjemlerFraJson(periodeResultatÅrsak, "FP");
    }

    private static int mapAntallTapteDagerFra(List<UttakResultatPeriodeAktivitet> uttakAktiviteter) {
        return alleAktiviteterHarNullUtbetaling(uttakAktiviteter) ? uttakAktiviteter.stream()
            .map(UttakResultatPeriodeAktivitet::getTrekkdager)
            .filter(Objects::nonNull)
            .max(BigDecimal::compareTo)
            .map(bd -> bd.setScale(1, RoundingMode.DOWN).intValue())
            .orElse(0) : 0;
    }

    private static UttakResultatPeriode finnUttakResultatPeriode(Optional<ForeldrepengerUttak> uttakResultatPerioder,
                                                                 TilkjentYtelsePeriode tilkjentYtelsePeriode) {
        return PeriodeBeregner.finnUttaksperiode(tilkjentYtelsePeriode,
            uttakResultatPerioder.map(ForeldrepengerUttak::perioder).orElse(Collections.emptyList()));
    }
}

package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.avslagfp;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.PeriodeBeregner.alleAktiviteterHarNullUtbetaling;

import java.math.BigDecimal;
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
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelsePeriode;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BehandlingMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.FellesMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.LovhjemmelComparator;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.LovhjemmelUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.PeriodeBeregner;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.Tuple;
import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.avslagfp.AvslåttPeriode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.fpformidling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.fpformidling.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.fpformidling.uttak.kodeliste.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.fpformidling.vilkår.Avslagsårsak;

public class AvslåttPeriodeMapper {
    private static Set<String> lovReferanser;

    public static Tuple<List<AvslåttPeriode>, String> mapAvslåttePerioderOgLovhjemmel(Behandling behandling,
                                                                                      List<TilkjentYtelsePeriode> tilkjentYtelsePerioder,
                                                                                      Optional<UttakResultatPerioder> uttakResultatPerioder) {
        lovReferanser = new TreeSet<>(new LovhjemmelComparator());
        Behandlingsresultat behandlingsresultat = behandling.getBehandlingsresultat();

        List<AvslåttPeriode> avslåttePerioder = årsakerFra(tilkjentYtelsePerioder, uttakResultatPerioder, behandling.getSpråkkode());
        String lovhjemmelForAvslag = FellesMapper.formaterLovhjemlerUttak(lovReferanser,
                BehandlingMapper.kodeFra(behandlingsresultat.getKonsekvenserForYtelsen()),
                false);
        if (avslåttePerioder.isEmpty()) {
            avslåttePerioder = årsakerFra(behandlingsresultat, uttakResultatPerioder);
            lovhjemmelForAvslag = FellesMapper.formaterLovhjemlerUttak(lovReferanser);
        }
        avslåttePerioder = AvslåttPeriodeMerger.mergePerioder(avslåttePerioder);

        return new Tuple<>(avslåttePerioder, lovhjemmelForAvslag);
    }

    private static List<AvslåttPeriode> årsakerFra(List<TilkjentYtelsePeriode> tilkjentYtelsePerioder,
                                                   Optional<UttakResultatPerioder> uttakResultatPerioder,
                                                   Språkkode språkkode) {
        List<AvslåttPeriode> avslåttePerioder = new ArrayList<>();
        for (TilkjentYtelsePeriode tilkjentYtelsePeriode : tilkjentYtelsePerioder) {
            AvslåttPeriode avslåttPeriode = årsaktypeFra(tilkjentYtelsePeriode,
                    finnUttakResultatPeriode(uttakResultatPerioder, tilkjentYtelsePeriode), språkkode);
            avslåttePerioder.add(avslåttPeriode);
        }
        return avslåttePerioder;
    }

    private static List<AvslåttPeriode> årsakerFra(Behandlingsresultat behandlingsresultat,
                                                   Optional<UttakResultatPerioder> uttakResultatPerioder) {
        List<AvslåttPeriode> avslagsAarsaker = new ArrayList<>();

        Avslagsårsak avslagsårsak = behandlingsresultat.getAvslagsårsak();
        if (avslagsårsak != null) {
            avslagsAarsaker.add(årsaktypeFra(avslagsårsak));
        }
        for (UttakResultatPeriode periode : uttakResultatPerioder.map(UttakResultatPerioder::getPerioder).orElse(Collections.emptyList())) {
            PeriodeResultatÅrsak periodeResultatÅrsak = periode.getPeriodeResultatÅrsak();
            if (PeriodeResultatType.AVSLÅTT.equals(periode.getPeriodeResultatType()) && periodeResultatÅrsak != null) {
                avslagsAarsaker.add(årsaktypeFra(periodeResultatÅrsak));
            }
        }
        return avslagsAarsaker;
    }

    private static AvslåttPeriode årsaktypeFra(ÅrsakMedLovReferanse årsakKode) {
        AvslåttPeriode avslåttPeriode = AvslåttPeriode.ny()
                .medAvslagsårsak(Årsak.of(årsakKode.getKode()))
                .build();
        leggTilLovReferanse(årsakKode);
        return avslåttPeriode;
    }

    private static AvslåttPeriode årsaktypeFra(TilkjentYtelsePeriode tilkjentYtelsePeriode,
                                               UttakResultatPeriode uttakResultatPeriode,
                                               Språkkode språkkode) {
        AvslåttPeriode avslåttPeriode = AvslåttPeriode.ny()
                .medAvslagsårsak(Årsak.of(uttakResultatPeriode.getPeriodeResultatÅrsak().getKode()))
                .medPeriodeFom(tilkjentYtelsePeriode.getPeriodeFom(), språkkode)
                .medPeriodeTom(tilkjentYtelsePeriode.getPeriodeTom(), språkkode)
                .medAntallTapteDager(mapAntallTapteDagerFra(uttakResultatPeriode.getAktiviteter()))
                .build();
        leggTilLovReferanse(uttakResultatPeriode.getPeriodeResultatÅrsak());
        return avslåttPeriode;
    }

    private static void leggTilLovReferanse(ÅrsakMedLovReferanse periodeResultatÅrsak) {
        lovReferanser.addAll(LovhjemmelUtil.hentLovhjemlerFraJson(periodeResultatÅrsak, "FP"));
    }

    private static int mapAntallTapteDagerFra(List<UttakResultatPeriodeAktivitet> uttakAktiviteter) {
        return alleAktiviteterHarNullUtbetaling(uttakAktiviteter) ?
                uttakAktiviteter.stream()
                        .map(UttakResultatPeriodeAktivitet::getTrekkdager)
                        .filter(Objects::nonNull)
                        .max(BigDecimal::compareTo)
                        .map(BigDecimal::intValue)
                        .orElse(0) : 0;
    }

    private static UttakResultatPeriode finnUttakResultatPeriode(Optional<UttakResultatPerioder> uttakResultatPerioder,
                                                                 TilkjentYtelsePeriode tilkjentYtelsePeriode) {
        return PeriodeBeregner.finnUttaksperiode(tilkjentYtelsePeriode, uttakResultatPerioder
                .map(UttakResultatPerioder::getPerioder).orElse(Collections.emptyList()));
    }
}

package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.avslagfp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BehandlingMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.FellesMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.LovhjemmelComparator;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.PeriodeBeregner;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.Tuple;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse.TilkjentYtelsePeriode;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.ForeldrepengerUttak;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.UttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.VilkårType;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.avslagfp.AvslåttPeriode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;

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
            avslåttePerioder = finnAvslåttePerioder(behandling.getVilkårTyper(), behandlingsresultat, uttakResultatPerioder, lovReferanser);
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

    private static List<AvslåttPeriode> finnAvslåttePerioder(Collection<VilkårType> fraBehandling,
                                                             Behandlingsresultat behandlingsresultat,
                                                             Optional<ForeldrepengerUttak> uttakResultatPerioder,
                                                             TreeSet<String> lovReferanser) {
        List<AvslåttPeriode> avslagsAarsaker = new ArrayList<>();

        var avslagsårsak = behandlingsresultat.getAvslagsårsak();
        if (avslagsårsak != null) {
            avslagsAarsaker.add(AvslåttPeriode.ny().medAvslagsårsak(Årsak.of(avslagsårsak.getKode())).build());
            var referanser = FellesMapper.lovhjemmelFraAvslagsårsak(FagsakYtelseType.FORELDREPENGER, fraBehandling, avslagsårsak);
            lovReferanser.addAll(referanser);
        }
        for (var periode : uttakResultatPerioder.map(ForeldrepengerUttak::perioder).orElse(Collections.emptyList())) {
            var periodeResultatÅrsak = periode.getPeriodeResultatÅrsak();
            if (PeriodeResultatType.AVSLÅTT.equals(periode.getPeriodeResultatType()) && periodeResultatÅrsak != null) {
                avslagsAarsaker.add(AvslåttPeriode.ny().medAvslagsårsak(Årsak.of(periodeResultatÅrsak.getKode())).build());
                lovReferanser.addAll(periodeResultatÅrsak.hentLovhjemlerFraJson());
            }
        }
        return avslagsAarsaker;
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
        lovReferanser.addAll(uttakResultatPeriode.getPeriodeResultatÅrsak().hentLovhjemlerFraJson());
        return avslåttPeriode;
    }

    private static int mapAntallTapteDagerFra(List<UttakResultatPeriodeAktivitet> uttakAktiviteter) {
        return PeriodeBeregner.alleAktiviteterHarNullUtbetaling(uttakAktiviteter) ? uttakAktiviteter.stream()
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

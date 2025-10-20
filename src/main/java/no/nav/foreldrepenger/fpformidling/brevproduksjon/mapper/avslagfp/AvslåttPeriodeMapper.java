package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.avslagfp;

import static no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatÅrsak.PERIODE_ÅRSAK_DISCRIMINATOR;
import static no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto;

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
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.avslagfp.AvslåttPeriode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Foreldrepenger.Aktivitet;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.KodeverkMapper;

public final class AvslåttPeriodeMapper {

    private AvslåttPeriodeMapper() {
    }

    public static Tuple<List<AvslåttPeriode>, String> mapAvslåttePerioderOgLovhjemmel(BrevGrunnlagDto behandling,
                                                                                      List<TilkjentYtelsePeriodeDto> tilkjentYtelsePerioder,
                                                                                      Optional<BrevGrunnlagDto.Foreldrepenger> uttakResultatPerioder,
                                                                                      Språkkode språkkode) {
        var lovReferanser = new TreeSet<>(new LovhjemmelComparator());
        var behandlingsresultat = behandling.behandlingsresultat();

        var avslåttePerioder = finnAvslåttePerioder(tilkjentYtelsePerioder, uttakResultatPerioder, språkkode, lovReferanser);
        var lovhjemmelForAvslag = FellesMapper.formaterLovhjemlerUttak(lovReferanser,
            BehandlingMapper.kodeFra(behandlingsresultat.konsekvenserForYtelsen()), false);

        if (avslåttePerioder.isEmpty()) {
            avslåttePerioder = finnAvslåttePerioder(behandling.behandlingsresultat().vilkårTyper(), behandlingsresultat, uttakResultatPerioder, lovReferanser);
            lovhjemmelForAvslag = FellesMapper.formaterLovhjemlerUttak(lovReferanser);
        }
        avslåttePerioder = AvslåttPeriodeMerger.mergePerioder(avslåttePerioder);

        return new Tuple<>(avslåttePerioder, lovhjemmelForAvslag);
    }

    private static List<AvslåttPeriode> finnAvslåttePerioder(List<TilkjentYtelsePeriodeDto> tilkjentYtelsePerioder,
                                                             Optional<BrevGrunnlagDto.Foreldrepenger> uttakResultatPerioder,
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

    private static List<AvslåttPeriode> finnAvslåttePerioder(Collection<BrevGrunnlagDto.Behandlingsresultat.VilkårType> vilkårTyper,
                                                             BrevGrunnlagDto.Behandlingsresultat behandlingsresultat,
                                                             Optional<BrevGrunnlagDto.Foreldrepenger> uttakResultatPerioder,
                                                             TreeSet<String> lovReferanser) {
        List<AvslåttPeriode> avslagsAarsaker = new ArrayList<>();

        var avslagsårsak = Avslagsårsak.fraKode(behandlingsresultat.avslagsårsak());
        if (avslagsårsak != null) {
            avslagsAarsaker.add(AvslåttPeriode.ny().medAvslagsårsak(Årsak.of(avslagsårsak.getKode())).build());
            var vt = vilkårTyper.stream().map(KodeverkMapper::mapVilkårType).toList();
            var referanser = FellesMapper.lovhjemmelFraAvslagsårsak(FagsakYtelseType.FORELDREPENGER, vt, avslagsårsak);
            lovReferanser.addAll(referanser);
        }
        for (var periode : uttakResultatPerioder.map(BrevGrunnlagDto.Foreldrepenger::perioderSøker).orElse(Collections.emptyList())) {
            var periodeResultatÅrsak = new PeriodeResultatÅrsak(periode.periodeResultatÅrsak(), PERIODE_ÅRSAK_DISCRIMINATOR,
                periode.periodeResultatÅrsakLovhjemmel());
            if (BrevGrunnlagDto.PeriodeResultatType.AVSLÅTT.equals(periode.periodeResultatType()) && periodeResultatÅrsak != null) {
                avslagsAarsaker.add(AvslåttPeriode.ny().medAvslagsårsak(Årsak.of(periodeResultatÅrsak.getKode())).build());
                lovReferanser.addAll(periodeResultatÅrsak.hentLovhjemlerFraJson());
            }
        }
        return avslagsAarsaker;
    }

    private static AvslåttPeriode mapAvslåttPeriode(TilkjentYtelsePeriodeDto tilkjentYtelsePeriode,
                                                    List<TilkjentYtelsePeriodeDto> tilkjentPeriodeListe,
                                                    BrevGrunnlagDto.Foreldrepenger.Uttaksperiode uttakResultatPeriode,
                                                    Språkkode språkkode,
                                                    TreeSet<String> lovReferanser) {
        //TFP-5200 Hack for å forhindre dobbelt antall tapte dager om det er flere tilkjentperioder for en uttaksperiode
        var antallTilkjentPerioderForUttaksperioden = PeriodeBeregner.finnAntallTilkjentePerioderForUttaksperioden(tilkjentPeriodeListe,
            uttakResultatPeriode);
        var antallTapteDager = mapAntallTapteDagerFra(uttakResultatPeriode.aktiviteter());
        var tapteDagerHvisFlereTilkjentPerioder = BigDecimal.ZERO;
        if (antallTilkjentPerioderForUttaksperioden > 1) {
            tapteDagerHvisFlereTilkjentPerioder = BigDecimal.valueOf(antallTapteDager)
                .divide(BigDecimal.valueOf(antallTilkjentPerioderForUttaksperioden), 2, RoundingMode.HALF_UP);
        }
        var periodeResultatÅrsak = new PeriodeResultatÅrsak(uttakResultatPeriode.periodeResultatÅrsak(), PERIODE_ÅRSAK_DISCRIMINATOR,
            uttakResultatPeriode.periodeResultatÅrsakLovhjemmel());
        var avslåttPeriode = AvslåttPeriode.ny()
            .medAvslagsårsak(Årsak.of(periodeResultatÅrsak.getKode()))
            .medPeriodeFom(tilkjentYtelsePeriode.fom(), språkkode)
            .medPeriodeTom(tilkjentYtelsePeriode.tom(), språkkode)
            .medAntallTapteDager(antallTapteDager, tapteDagerHvisFlereTilkjentPerioder)
            .build();
        lovReferanser.addAll(periodeResultatÅrsak.hentLovhjemlerFraJson());
        return avslåttPeriode;
    }

    private static int mapAntallTapteDagerFra(List<Aktivitet> uttakAktiviteter) {
        return PeriodeBeregner.alleAktiviteterHarNullUtbetaling(uttakAktiviteter) ? uttakAktiviteter.stream()
            .map(Aktivitet::trekkdager)
            .filter(Objects::nonNull)
            .max(BigDecimal::compareTo)
            .map(bd -> bd.setScale(1, RoundingMode.DOWN).intValue())
            .orElse(0) : 0;
    }

    private static BrevGrunnlagDto.Foreldrepenger.Uttaksperiode finnUttakResultatPeriode(Optional<BrevGrunnlagDto.Foreldrepenger> uttakResultatPerioder,
                                                                                         TilkjentYtelsePeriodeDto tilkjentYtelsePeriode) {
        return PeriodeBeregner.finnUttaksperiode(tilkjentYtelsePeriode,
            uttakResultatPerioder.map(BrevGrunnlagDto.Foreldrepenger::perioderSøker).orElse(Collections.emptyList()));
    }
}

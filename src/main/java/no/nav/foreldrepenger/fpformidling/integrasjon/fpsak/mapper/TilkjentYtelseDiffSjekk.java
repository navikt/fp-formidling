package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse.TilkjentYtelseEngangsstønad;

import no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse.TilkjentYtelseForeldrepenger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class TilkjentYtelseDiffSjekk {
    private static final Logger LOG = LoggerFactory.getLogger(TilkjentYtelseDiffSjekk.class);

    private TilkjentYtelseDiffSjekk() {
        //CDI
    }

    public static void loggDiff(TilkjentYtelseEngangsstønad res, TilkjentYtelseEngangsstønad resV2) {
        var erLike = Objects.equals(res, resV2);
        if (!erLike) {
            var msg = String.format("Differanse mellom gammel %s og ny %s tilkjent ytelse engangsstønad", res, resV2);
            LOG.info(msg);
        }
    }

    public static void loggDiff(TilkjentYtelseForeldrepenger res, TilkjentYtelseForeldrepenger resV2) {
        var erLike = Objects.equals(res, resV2);
        if (!erLike) {
            var msg = String.format("Differanse mellom gammel %s og ny %s tilkjent ytelse dagytelse", res, resV2);
            LOG.info(msg);
        }
    }
}

package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;

import static no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto.Aktivitetstatus.FRILANSER;
import static no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto.Aktivitetstatus.KOMBINERT_AT_FL;
import static no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto.Aktivitetstatus.KOMBINERT_AT_FL_SN;
import static no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto.Aktivitetstatus.KOMBINERT_AT_SN;
import static no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto.Aktivitetstatus.KOMBINERT_FL_SN;
import static no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto.Aktivitetstatus.SELVSTENDIG_NÆRINGSDRIVENDE;

import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;

import no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto;

public final class AktivitetsbeskrivelseUtleder {

    private static final Set<TilkjentYtelseDagytelseDto.Aktivitetstatus> SN_STATUSER = Set.of(SELVSTENDIG_NÆRINGSDRIVENDE, KOMBINERT_AT_FL_SN,
        KOMBINERT_AT_SN, KOMBINERT_FL_SN);

    private static final Set<TilkjentYtelseDagytelseDto.Aktivitetstatus> FL_STATUSER = Set.of(FRILANSER, KOMBINERT_AT_FL_SN, KOMBINERT_AT_FL,
        KOMBINERT_FL_SN);

    private AktivitetsbeskrivelseUtleder() {
    }

    public static String utledAktivitetsbeskrivelse(TilkjentYtelseDagytelseDto.TilkjentYtelseAndelDto andel,
                                                    TilkjentYtelseDagytelseDto.Aktivitetstatus aktivitetStatus,
                                                    UnaryOperator<String> hentNavn) {
        var arbeidsgiverOpt = Optional.ofNullable(andel.arbeidsgiverReferanse());
        if (arbeidsgiverOpt.isPresent()) {
            return hentNavn.apply(arbeidsgiverOpt.get());
        }
        if (FL_STATUSER.contains(aktivitetStatus)) {
            return "Som frilanser";
        }
        if (SN_STATUSER.contains(aktivitetStatus)) {
            return "Som næringsdrivende";
        }
        return "";
    }
}

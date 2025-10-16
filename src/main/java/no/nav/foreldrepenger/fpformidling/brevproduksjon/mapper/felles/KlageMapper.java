package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import static no.nav.foreldrepenger.fpformidling.domene.klage.KlageAvvistÅrsak.KLAGET_FOR_SENT;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.klage.KlageAvvistÅrsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.KodeverkMapper;

public class KlageMapper {

    private KlageMapper() {
    }

    public static boolean gjelderTilbakekreving(BrevGrunnlagDto.KlageBehandling klage) {
        var påklagdBehandlingType = utledPåklagdBehandlingType(klage);
        return påklagdBehandlingType.filter(
                type -> Set.of(BrevGrunnlagDto.BehandlingType.TILBAKEKREVING, BrevGrunnlagDto.BehandlingType.TILBAKEKREVING_REVURDERING).contains(type))
            .isPresent();
    }

    private static Optional<BrevGrunnlagDto.BehandlingType> utledPåklagdBehandlingType(BrevGrunnlagDto.KlageBehandling klage) {
        var påklagdBehandlingTypeKA = Optional.ofNullable(klage.klageFormkravResultatKA())
            .map(BrevGrunnlagDto.KlageBehandling.KlageFormkravResultat::påklagdBehandlingType);
        if (påklagdBehandlingTypeKA.isPresent()) {
            return påklagdBehandlingTypeKA;
        }
        return Optional.ofNullable(klage.klageFormkravResultatNFP()).map(BrevGrunnlagDto.KlageBehandling.KlageFormkravResultat::påklagdBehandlingType);
    }

    public static List<KlageAvvistÅrsak> listeAvAvvisteÅrsaker(BrevGrunnlagDto.KlageBehandling klage) {
        return Optional.ofNullable(klage.klageFormkravResultatNFP()).map(BrevGrunnlagDto.KlageBehandling.KlageFormkravResultat::avvistÅrsaker)
            .orElse(List.of())
            .stream()
            .map(KodeverkMapper::mapKlageAvvistÅrsak)
            .toList();
    }

    public static Optional<String> hentOgFormaterLovhjemlerForAvvistKlage(BrevGrunnlagDto.KlageBehandling klage, Språkkode språkkode) {
        var klagehjemler = hentKlageHjemler(klage);
        var klagetEtterKlagefrist = listeAvAvvisteÅrsaker(klage).stream().anyMatch(KLAGET_FOR_SENT::equals);
        return formaterLovhjemlerForAvvistKlage(klagehjemler, klagetEtterKlagefrist, språkkode);
    }

    static Set<String> hentKlageHjemler(BrevGrunnlagDto.KlageBehandling klage) {
        var mengde = new TreeSet<>(new LovhjemmelComparator());
        var hjemler = listeAvAvvisteÅrsaker(klage).stream().map(KlageAvvistÅrsak::getLovHjemmel).flatMap(Collection::stream).toList();
        mengde.addAll(hjemler);
        return mengde;
    }

    static Optional<String> formaterLovhjemlerForAvvistKlage(Set<String> hjemler, boolean klagetEtterKlagefrist, Språkkode språkkode) {
        String startTillegg;
        if (Språkkode.NN.equals(språkkode)) {
            startTillegg = klagetEtterKlagefrist ? "folketrygdlova § 21-12 og forvaltningslova" : "forvaltningslova";
        } else {
            startTillegg = klagetEtterKlagefrist ? "folketrygdloven § 21-12 og forvaltningsloven" : "forvaltningsloven";
        }
        var lovhjemmelBuiloer = new StringBuilder();
        var antallLovreferanser = FellesMapper.formaterLovhjemler(hjemler, lovhjemmelBuiloer, startTillegg, null);
        if (antallLovreferanser == 0) {
            return Optional.empty();
        }
        return Optional.of(lovhjemmelBuiloer.toString());
    }
}

package no.nav.foreldrepenger.melding.datamapper.domene;

import static no.nav.foreldrepenger.melding.behandling.klage.KlageAvvistÅrsak.IKKE_KONKRET;
import static no.nav.foreldrepenger.melding.behandling.klage.KlageAvvistÅrsak.IKKE_PAKLAGD_VEDTAK;
import static no.nav.foreldrepenger.melding.behandling.klage.KlageAvvistÅrsak.IKKE_SIGNERT;
import static no.nav.foreldrepenger.melding.behandling.klage.KlageAvvistÅrsak.KLAGER_IKKE_PART;
import static no.nav.foreldrepenger.melding.behandling.klage.KlageAvvistÅrsak.KLAGET_FOR_SENT;
import static no.nav.foreldrepenger.melding.behandling.klage.KlageAvvistÅrsak.KLAGE_UGYLDIG;
import static no.nav.foreldrepenger.melding.datamapper.domene.FellesMapper.formaterLovhjemler;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.KlageRestKlient;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingIdDto;
import no.nav.foreldrepenger.fpsak.dto.klage.KlagebehandlingDto;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.klage.KlageAvvistÅrsak;
import no.nav.foreldrepenger.melding.behandling.klage.KlageVurdering;
import no.nav.foreldrepenger.melding.brevbestiller.api.dto.klage.Klage;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.avvist.AvvistGrunnKode;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.vedtak.util.StringUtils;

@ApplicationScoped
public class KlageMapper {


    public static Map<String, AvvistGrunnKode> avvistGrunnMap;

    static {
        avvistGrunnMap = new HashMap<>();
        avvistGrunnMap.put(KLAGET_FOR_SENT.getKode(), AvvistGrunnKode.ETTER_6_UKER);
        avvistGrunnMap.put(KLAGE_UGYLDIG.getKode(), AvvistGrunnKode.KLAGEUGYLDIG);
        avvistGrunnMap.put(IKKE_PAKLAGD_VEDTAK.getKode(), AvvistGrunnKode.KLAGEIKKEVEDTAK);
        avvistGrunnMap.put(KLAGER_IKKE_PART.getKode(), AvvistGrunnKode.KLAGEIKKEPART);
        avvistGrunnMap.put(IKKE_KONKRET.getKode(), AvvistGrunnKode.KLAGEIKKEKONKRET);
        avvistGrunnMap.put(IKKE_SIGNERT.getKode(), AvvistGrunnKode.KLAGEUGYLDIG);
    }

    private KodeverkRepository kodeverkRepository;
    private KlageRestKlient klageRestKlient;

    public KlageMapper() {
        //CDO
    }

    @Inject
    public KlageMapper(KodeverkRepository kodeverkRepository,
                       KlageRestKlient klageRestKlient) {
        this.kodeverkRepository = kodeverkRepository;
        this.klageRestKlient = klageRestKlient;
    }


    public Klage hentKlagebehandling(Behandling behandling) {
        KlagebehandlingDto klagebehandlingDto = klageRestKlient.hentKlagebehandling(new BehandlingIdDto(behandling.getId()));
        return Klage.fraDto(klagebehandlingDto, kodeverkRepository);
    }

    public List<KlageAvvistÅrsak> listeAvAvvisteÅrsaker(Klage klage) {
        if (klage.getFormkravKA() != null) {
            return klage.getFormkravKA().getAvvistÅrsaker();
        } else if (klage.getFormkravNFP() != null) {
            return klage.getFormkravNFP().getAvvistÅrsaker();
        }
        return Collections.emptyList();
    }

    public Optional<String> hentOgFormaterLovhjemlerForAvvistKlage(Klage klage) {
        Set<String> klagehjemler = hentKlageHjemler(klage);
        boolean klagetEtterKlagefrist = listeAvAvvisteÅrsaker(klage).stream()
                .anyMatch(kode -> KLAGET_FOR_SENT.equals(kode));
        return formaterLovhjemlerForAvvistKlage(klagehjemler, klagetEtterKlagefrist);
    }

    Set<String> hentKlageHjemler(Klage klage) {
        Set<String> klageHjemler = new TreeSet<>();
        String klageVurdertAv = klage.getFormkravKA() != null ? "KA" : "NFP";
        listeAvAvvisteÅrsaker(klage).forEach(årsak -> klageHjemler.addAll(LovhjemmelUtil.hentLovhjemlerFraJson(årsak, klageVurdertAv)));
        return klageHjemler;
    }

    static Optional<String> formaterLovhjemlerForAvvistKlage(Set<String> hjemler, boolean klagetEtterKlagefrist) {
        String startTillegg = klagetEtterKlagefrist ?
                "folketrygdloven § 21-12 og forvaltningsloven" : "forvaltningsloven";
        StringBuilder lovhjemmelBuiloer = new StringBuilder();
        int antallLovreferanser = formaterLovhjemler(hjemler, lovhjemmelBuiloer, startTillegg, null);
        if (antallLovreferanser == 0) {
            return Optional.empty();
        }
        return Optional.of(lovhjemmelBuiloer.toString());
    }

    public boolean erOpphevet(Klage klage) {
        String klageVurdering = null;
        if (klage.getKlageVurderingResultatNFP() != null) {
            klageVurdering = klage.getKlageVurderingResultatNFP().getKlageVurdering();
        } else if (klage.getKlageVurderingResultatNK() != null) {
            klageVurdering = klage.getKlageVurderingResultatNK().getKlageVurdering();
        }
        if (StringUtils.nullOrEmpty(klageVurdering)) {
            throw new IllegalStateException();
        }
        return kodeverkRepository.finn(KlageVurdering.class, klageVurdering).equals(KlageVurdering.OPPHEVE_YTELSESVEDTAK);
    }

}

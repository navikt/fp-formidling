package no.nav.foreldrepenger.melding.datamapper.domene;

import static no.nav.foreldrepenger.melding.datamapper.domene.FellesMapper.formaterLovhjemler;
import static no.nav.foreldrepenger.melding.klage.KlageAvvistÅrsak.IKKE_KONKRET;
import static no.nav.foreldrepenger.melding.klage.KlageAvvistÅrsak.IKKE_PAKLAGD_VEDTAK;
import static no.nav.foreldrepenger.melding.klage.KlageAvvistÅrsak.IKKE_SIGNERT;
import static no.nav.foreldrepenger.melding.klage.KlageAvvistÅrsak.KLAGER_IKKE_PART;
import static no.nav.foreldrepenger.melding.klage.KlageAvvistÅrsak.KLAGET_FOR_SENT;
import static no.nav.foreldrepenger.melding.klage.KlageAvvistÅrsak.KLAGE_UGYLDIG;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.BehandlingRestKlient;
import no.nav.foreldrepenger.fpsak.dto.klage.KlageFormkravResultatDto;
import no.nav.foreldrepenger.fpsak.dto.klage.KlageVurderingResultatDto;
import no.nav.foreldrepenger.fpsak.dto.klage.KlagebehandlingDto;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.avvist.AvvistGrunnKode;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.klage.KlageAvvistÅrsak;
import no.nav.foreldrepenger.melding.klage.KlageFormkravResultat;
import no.nav.foreldrepenger.melding.klage.KlageVurdering;
import no.nav.foreldrepenger.melding.klage.KlageVurderingResultat;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;

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
    private BehandlingRestKlient behandlingRestKlient;

    public KlageMapper() {
        //CDI
    }

    @Inject
    public KlageMapper(KodeverkRepository kodeverkRepository,
                       BehandlingRestKlient behandlingRestKlient) {
        this.kodeverkRepository = kodeverkRepository;
        this.behandlingRestKlient = behandlingRestKlient;
    }

    public Klage hentKlagebehandling(Behandling behandling) {
        KlagebehandlingDto klagebehandlingDto = behandlingRestKlient.hentKlagebehandling(behandling.getResourceLinkDtos());
        return mapKlagefraDto(klagebehandlingDto);
    }

    public Klage mapKlagefraDto(KlagebehandlingDto dto) {
        Klage.Builder builder = Klage.ny();
        if (dto.getKlageFormkravResultatNFP() != null) {
            builder.medFormkravNFP(mapKlageFormkravResultatfraDto(dto.getKlageFormkravResultatNFP()));
        }
        if (dto.getKlageFormkravResultatKA() != null) {
            builder.medFormkravKA(mapKlageFormkravResultatfraDto(dto.getKlageFormkravResultatKA()));
        }
        if (dto.getKlageVurderingResultatNFP() != null) {
            builder.medKlageVurderingResultatNFP(mapKlageVurderingResultatfraDto(dto.getKlageVurderingResultatNFP()));
        }
        if (dto.getKlageVurderingResultatNK() != null) {
            builder.medKlageVurderingResultatNK(mapKlageVurderingResultatfraDto(dto.getKlageVurderingResultatNK()));
        }
        return builder.build();
    }

    private KlageFormkravResultat mapKlageFormkravResultatfraDto(KlageFormkravResultatDto dto) {
        KlageFormkravResultat.Builder builder = KlageFormkravResultat.ny();
        builder.medBegrunnelse(dto.getBegrunnelse());
        List<KlageAvvistÅrsak> avvistÅrsaker = new ArrayList<>();
        dto.getAvvistArsaker().forEach(årsak -> {
            avvistÅrsaker.add(kodeverkRepository.finn(KlageAvvistÅrsak.class, årsak.kode));
        });
        builder.medAvvistÅrsaker(avvistÅrsaker);
        return builder.build();
    }

    private KlageVurderingResultat mapKlageVurderingResultatfraDto(KlageVurderingResultatDto dto) {
        KlageVurderingResultat.Builder builder = KlageVurderingResultat.ny();
        builder.medKlageVurdering(kodeverkRepository.finn(KlageVurdering.class, dto.getKlageVurdering()));
        return builder.build();
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
                .anyMatch(KLAGET_FOR_SENT::equals);
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
        KlageVurdering klageVurdering = null;
        if (klage.getKlageVurderingResultatNFP() != null) {
            klageVurdering = klage.getKlageVurderingResultatNFP().getKlageVurdering();
        } else if (klage.getKlageVurderingResultatNK() != null) {
            klageVurdering = klage.getKlageVurderingResultatNK().getKlageVurdering();
        }
        if (klageVurdering == null) {
            throw new IllegalStateException();
        }
        return KlageVurdering.OPPHEVE_YTELSESVEDTAK.equals(klageVurdering);
    }
}

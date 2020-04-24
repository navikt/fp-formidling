package no.nav.foreldrepenger.melding.datamapper.brev;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.AvvistGrunn;
import no.nav.foreldrepenger.melding.datamapper.domene.KlageMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.fritekstbrev.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.avvist.AvvistGrunnListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.avvist.AvvistGrunnType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.avvist.ObjectFactory;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.klage.KlageAvvistÅrsak;

@ApplicationScoped
@Named(DokumentMalType.KLAGE_AVVIST)
public class KlageAvvistBrevMapper extends FritekstmalBrevMapper {

    public KlageAvvistBrevMapper() {
        //CDI
    }

    @Inject
    public KlageAvvistBrevMapper(BrevParametere brevParametere, DomeneobjektProvider domeneobjektProvider) {
        super(brevParametere, domeneobjektProvider);
    }

    @Override
    public String displayName() {
        return "Vedtak om avvist klage";
    }

    @Override
    public String templateFolder() {
        return "vedtakomavvistklage";
    }

    @Override
    protected FagType mapFagType(DokumentHendelse hendelse, Behandling behandling) {
        initHandlebars(behandling.getSpråkkode());

        Map<String, Object> hovedoverskriftFelter = new HashMap<>();
        hovedoverskriftFelter.put("behandling", behandling);
        hovedoverskriftFelter.put("dokumentHendelse", hendelse);
        hovedoverskriftFelter.put("behandlesAvKlageinstans", behandlesAvKlageinstans(hendelse, behandling));

        Klage klage = domeneobjektProvider.hentKlagebehandling(behandling);
        if (klage != null) {
            hovedoverskriftFelter.put("paaklagdBehandlingErTilbakekreving", klage.getPåklagdBehandlingType().erTilbakekrevingBehandlingType());
        }

        FagType fagType = new FagType();
        fagType.setHovedoverskrift(tryApply(hovedoverskriftFelter, getOverskriftMal()));
        fagType.setBrødtekst(tryApply(mapTilBrevfelter(hendelse, behandling).getMap(), getBrødtekstMal()));
        return fagType;
    }

    @Override
    Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling) {
        Klage klage = domeneobjektProvider.hentKlagebehandling(behandling);
        Optional<String> lovhjemler = KlageMapper.hentOgFormaterLovhjemlerForAvvistKlage(klage, behandling.getSpråkkode());

        Brevdata brevdata = new Brevdata()
                .leggTil("lovhjemler", lovhjemler.get());

        List<KlageAvvistÅrsak> avvistÅrsaker = KlageMapper.listeAvAvvisteÅrsaker(klage);

        if (!avvistÅrsaker.isEmpty()) {
            AvvistGrunnListeType avvistGrunnListeType = avvistGrunnListeFra(avvistÅrsaker.stream().map(KlageAvvistÅrsak::getKode).collect(Collectors.toList()));
            List<AvvistGrunnType> avvistGrunnTypeListe = avvistGrunnListeType.getAvvistGrunn();
            List<AvvistGrunn> avvistGrunnListe = new ArrayList<>();
            for (int i=0; i<avvistGrunnTypeListe.size(); i++) {
                avvistGrunnListe.add(new AvvistGrunn(avvistGrunnListeType.getAvvistGrunn().get(i).getAvvistGrunnKode().value()));
            }
            brevdata.leggTil("avvistGrunnListe", avvistGrunnListe);

            if (avvistGrunnListe.size() == 1) {
                brevdata.leggTil("bareEnGrunn", true);
            } else {
                brevdata.leggTil("bareEnGrunn", false);
            }
        }

        return brevdata;
    }

    static AvvistGrunnListeType avvistGrunnListeFra(List<String> avvistGrunn) {
        AvvistGrunnListeType liste = new ObjectFactory().createAvvistGrunnListeType();
        avvistGrunn.forEach(avvistString -> {
            AvvistGrunnType avvistGrunnType = new ObjectFactory().createAvvistGrunnType();
            avvistGrunnType.setAvvistGrunnKode(KlageMapper.avvistGrunnMap.get(avvistString));
            liste.getAvvistGrunn().add(avvistGrunnType);
        });
        return liste;
    }
}




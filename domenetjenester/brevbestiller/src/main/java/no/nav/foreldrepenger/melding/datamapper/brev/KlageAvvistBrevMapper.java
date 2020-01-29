package no.nav.foreldrepenger.melding.datamapper.brev;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import no.nav.foreldrepenger.melding.anke.Anke;
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
    Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling) {

        Klage klage = domeneobjektProvider.hentKlagebehandling(behandling);
        Optional<String>  lovhjemler = KlageMapper.hentOgFormaterLovhjemlerForAvvistKlage(klage);

        Brevdata brevdata =new Brevdata()
                .leggTil("lovhjemler",  lovhjemler.get())
                .leggTil("saksbehandler", behandling.getAnsvarligSaksbehandler())
                .leggTil("medunderskriver",behandling.getAnsvarligBeslutter())
                .leggTil("behandlingtype",behandling.getBehandlingType().getKode());

        List<KlageAvvistÅrsak> avvistÅrsaker = KlageMapper.listeAvAvvisteÅrsaker(klage);

        if (!avvistÅrsaker.isEmpty()) {
            AvvistGrunnListeType avvistGrunnListeType  =  avvistGrunnListeFra(avvistÅrsaker.stream().map(KlageAvvistÅrsak::getKode).collect(Collectors.toList()));
            List<AvvistGrunnType>  avvistGrunnTypeListe =   avvistGrunnListeType.getAvvistGrunn();
            HashSet<AvvistGrunn> avvistGrunnListe = new HashSet<AvvistGrunn>();
            for (int i=0;i<avvistGrunnTypeListe.size();i++){
                avvistGrunnListe.add(new AvvistGrunn(avvistGrunnListeType.getAvvistGrunn().get(i).getAvvistGrunnKode().value()));
            }
            brevdata.leggTil("avvistGrunnListe",avvistGrunnListe);
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





package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.typer.Dato.medFormatering;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.datatype.XMLGregorianCalendar;

import no.nav.foreldrepenger.melding.anke.Anke;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.BrevMapperUtil;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.MottattdokumentMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeVerktøy;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.fritekstbrev.FagType;
import no.nav.foreldrepenger.melding.mottattdokument.MottattDokument;
import no.nav.foreldrepenger.melding.typer.Dato;

@ApplicationScoped
@Named(DokumentMalType.ANKE_VEDTAK_OMGJORING_DOK)
public class AnkeOmgjøreVedtakBrevMapper  extends FritekstmalBrevMapper {


    public AnkeOmgjøreVedtakBrevMapper() {
        //CDI
    }

    @Inject
    public AnkeOmgjøreVedtakBrevMapper(BrevParametere brevParametere, DomeneobjektProvider domeneobjektProvider) {
        super(brevParametere, domeneobjektProvider);
    }

    @Override
    public String displayName() {
        return "Vedtak om omgjøring i ankesak";
    }

    @Override
    public String templateFolder() {
        return "vedtakomomgjøringiankesak";
    }

    @Override
    Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling) {
        Optional<Anke> anke  = domeneobjektProvider.hentAnkebehandling(behandling);
        if( anke.isPresent()) {
            return new Brevdata()
                    .leggTil("mintekst",  anke.get().getFritekstTilBrev())
                    .leggTil("saksbehandler", behandling.getAnsvarligSaksbehandler())
                    .leggTil("medunderskriver",behandling.getAnsvarligBeslutter())
                    .leggTil("behandlingtype",behandling.getBehandlingType().getKode());
        }
        return new Brevdata()
                .leggTil("saksbehandler", behandling.getAnsvarligSaksbehandler())
                .leggTil("medunderskriver",behandling.getAnsvarligBeslutter())
                .leggTil("behandlingtype",behandling.getBehandlingType().getKode());


    }
    @Override
    protected FagType mapFagType(DokumentHendelse hendelse, Behandling behandling) {

        Dato vedtaksDato = null;
        boolean gunst = false;

        initHandlebars(behandling.getSpråkkode());

        Optional<Anke> anke = domeneobjektProvider.hentAnkebehandling(behandling);
        if(anke.isPresent()){
            UUID klageBehandlingUuid =  anke.get().getPaAnketBehandlingUuid();
            Behandling klageBehandling = domeneobjektProvider.hentBehandling(klageBehandlingUuid);
            vedtaksDato = klageBehandling.getOriginalVedtaksDato()!=null?medFormatering(klageBehandling.getOriginalVedtaksDato()):null;
            if( "ANKE_TIL_GUNST".equals(anke.get().getAnkeVurderingOmgjoer())){
                gunst= true;
            }
        }

        FagType fagType = new FagType();
        fagType.setBrødtekst(tryApply(mapTilBrevfelter(hendelse, behandling).leggTil("vedtaksDato",vedtaksDato).leggTil("gunst",gunst).getMap(), getBrødtekstMal()));
        if(vedtaksDato== null){
            fagType.setHovedoverskrift(tryApply(Map.of("behandling", behandling, "dokumentHendelse", hendelse), getOverskriftMal()));
        }
        else{
            fagType.setHovedoverskrift(tryApply(Map.of("behandling", behandling, "dokumentHendelse", hendelse,"vedtaksDato",vedtaksDato), getOverskriftMal()));
        }
        return fagType;
    }
}







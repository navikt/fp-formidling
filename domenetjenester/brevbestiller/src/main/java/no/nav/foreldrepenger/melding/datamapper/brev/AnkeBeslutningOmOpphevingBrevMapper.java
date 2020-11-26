package no.nav.foreldrepenger.melding.datamapper.brev;

import no.nav.foreldrepenger.melding.anke.Anke;
import no.nav.foreldrepenger.melding.anke.AnkeVurdering;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

@ApplicationScoped
@Named(DokumentMalTypeKode.ANKEBREV_BESLUTNING_OM_OPPHEVING)
public class AnkeBeslutningOmOpphevingBrevMapper extends FritekstmalBrevMapper {

    public AnkeBeslutningOmOpphevingBrevMapper() {
        //CDI
    }

    @Inject
    public AnkeBeslutningOmOpphevingBrevMapper(BrevParametere brevParametere, DomeneobjektProvider domeneobjektProvider) {
        super(brevParametere, domeneobjektProvider);
    }

    @Override
    public String displayName() {
        return "Ankebrev: Beslutning om oppheving";
    }

    @Override
    public String templateFolder() {
        return "ankebrevbeslutningomoppheving";
    }

    @Override
    Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling) {
        Optional<Anke> anke  = domeneobjektProvider.hentAnkebehandling(behandling);

        return new Brevdata()
               .leggTil("ytelseType", hendelse.getYtelseType().getKode())
               .leggTil("fritekst", anke.map(Anke::getFritekstTilBrev).orElse(""))
               .leggTil("utenOppheve", erAnkeHjemsendUtenOpphev(anke.map(Anke::getAnkeVurdering).orElse(AnkeVurdering.UDEFINERT)));
    }

    boolean erAnkeHjemsendUtenOpphev(AnkeVurdering ankeVurdering) {
        return AnkeVurdering.ANKE_HJEMSEND_UTEN_OPPHEV.equals(ankeVurdering);
    }
}
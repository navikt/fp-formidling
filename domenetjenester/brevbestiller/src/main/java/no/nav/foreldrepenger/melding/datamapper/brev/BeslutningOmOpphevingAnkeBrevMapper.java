package no.nav.foreldrepenger.melding.datamapper.brev;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;

@ApplicationScoped
@Named(DokumentMalType.ANKEBREV_BESLUTNING_OM_OPPHEVING)
public class BeslutningOmOpphevingAnkeBrevMapper extends FritekstmalBrevMapper {

    public BeslutningOmOpphevingAnkeBrevMapper() {
        //CDI
    }

    @Inject
    public BeslutningOmOpphevingAnkeBrevMapper(BrevParametere brevParametere, DomeneobjektProvider domeneobjektProvider) {
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
        return new Brevdata()
                .leggTil("ytelseType", hendelse.getYtelseType().getKode());
    }
}

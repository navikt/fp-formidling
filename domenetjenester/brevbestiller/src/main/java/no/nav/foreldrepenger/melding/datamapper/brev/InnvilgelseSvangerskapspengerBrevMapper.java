package no.nav.foreldrepenger.melding.datamapper.brev;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;

@ApplicationScoped
//@Named(DokumentMalType.INNVILGELSE_SVANGERSKAPSPENGER_DOK) //TODO
public class InnvilgelseSvangerskapspengerBrevMapper extends FritekstmalBrevMapper {
    @Override
    public String displayName() {
        return null;
    }

    @Override
    String templateFolder() {
        return "innvilgelsesvangerskapspenger";
    }

    @Override
    Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling) {
        return new Brevdata(behandling.getSpråkkode()) {

        };
    }
}

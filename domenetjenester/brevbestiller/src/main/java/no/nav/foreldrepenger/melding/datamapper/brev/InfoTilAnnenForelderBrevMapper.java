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
@Named(DokumentMalType.INFO_TIL_ANNEN_FORELDER_DOK)
public class InfoTilAnnenForelderBrevMapper extends FritekstmalBrevMapper {

    public InfoTilAnnenForelderBrevMapper() {
        //CDI
    }

    @Inject
    public InfoTilAnnenForelderBrevMapper(BrevParametere brevParametere, DomeneobjektProvider domeneobjektProvider) {
        super(brevParametere, domeneobjektProvider);
    }

    @Override
    public String displayName() {
        return "Informasjon til den andre forelderen";
    }

    @Override
    public String templateFolder() {
        return "informasjontilannenforelder";
    }

    @Override
    Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling) {
        return new Brevdata()
                .leggTil("kontaktTelefonnummer", null);  // null fordi det ikke skal være med i dette brevet.
    }
}

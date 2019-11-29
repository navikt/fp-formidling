package no.nav.foreldrepenger.melding.datamapper.brev;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingÅrsakType;
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
        BehandlingÅrsakType aarsak=null;
        //forutsetter at behandlingen har en av disse årsakstypene - er håndtert i fpsak
        if (behandling.harBehandlingÅrsak(BehandlingÅrsakType.INFOBREV_BEHANDLING)) {
            aarsak=BehandlingÅrsakType.INFOBREV_BEHANDLING;
        }
        else {
            aarsak=BehandlingÅrsakType.INFOBREV_OPPHOLD;
        }
            return new Brevdata()
                .leggTil("erAutomatiskVedtak", Boolean.FALSE) // For å unngå automatiskVedtakMvh_001 - bør skille informasjon/vedtak i tillegg til automatisk
                .leggTil("kontaktTelefonnummer", null)  // null fordi det ikke skal være med i dette brevet.
                .leggTil("behandlingsAarsak", aarsak); //skiller på tekst om vi mangler søknad pga oppholdsperioder eller siste uttaksdato hos mor

    }
}

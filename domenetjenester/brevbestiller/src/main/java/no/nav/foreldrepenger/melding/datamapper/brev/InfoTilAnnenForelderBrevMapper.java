package no.nav.foreldrepenger.melding.datamapper.brev;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingÅrsakType;
import no.nav.foreldrepenger.melding.typer.Dato;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;

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
        BehandlingÅrsakType aarsak = BehandlingÅrsakType.INFOBREV_BEHANDLING;
        //forutsetter at behandlingen har en av disse årsakstypene - er håndtert i fpsak
        if (behandling.harBehandlingÅrsak(BehandlingÅrsakType.INFOBREV_OPPHOLD)) {
            aarsak = BehandlingÅrsakType.INFOBREV_OPPHOLD;
        }

        Optional<UttakResultatPerioder> uttakResultatPerioder = domeneobjektProvider.hentUttaksresultatHvisFinnes(behandling);

        String sisteUttaksdagMor = null;
        if (uttakResultatPerioder.isPresent() && BehandlingÅrsakType.INFOBREV_BEHANDLING.equals(aarsak)) {
            sisteUttaksdagMor = uttakResultatPerioder.get().getPerioderAnnenPart().stream()
                .filter(up -> PeriodeResultatType.INNVILGET.equals(up.getPeriodeResultatType()) ||
                    up.getAktiviteter().stream().anyMatch(upa -> upa.getTrekkdager().compareTo(BigDecimal.ZERO) > 0))
                .map(UttakResultatPeriode::getTom).max(LocalDate::compareTo)
                .map(Dato::formaterDato)
                .orElse(null);
        }

        return new Brevdata()
                .leggTil("sisteUttaksdagMor", sisteUttaksdagMor)
                .leggTil("erAutomatiskVedtak", Boolean.FALSE) // For å unngå automatiskVedtakMvh_001 - bør skille informasjon/vedtak i tillegg til automatisk
                .leggTil("kontaktTelefonnummer", null)  // null fordi det ikke skal være med i dette brevet.
                .leggTil("behandlingsAarsak", aarsak.getKode()); //skiller på tekst om vi mangler søknad pga oppholdsperioder eller siste uttaksdato hos mor

    }
}

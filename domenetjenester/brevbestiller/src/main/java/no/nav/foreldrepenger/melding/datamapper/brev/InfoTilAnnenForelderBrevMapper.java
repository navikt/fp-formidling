package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.BrevMapperUtil.medFormatering;

import java.time.LocalDate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DokumentBestillerFeil;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.UttakMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeVerktøy;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.vedtak.feil.FeilFactory;

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
        UttakResultatPerioder uttakResultatPerioder = domeneobjektProvider.hentUttaksresultat(behandling);
        LocalDate fristDato = UttakMapper.finnSisteDagIFelleseriodeHvisFinnes(uttakResultatPerioder)
                .map(PeriodeVerktøy::xmlGregorianTilLocalDate)
                .orElseThrow(() -> FeilFactory.create(DokumentBestillerFeil.class).feltManglerVerdi("dato").toException());

        return new Brevdata()
                .leggTil("dato", medFormatering(fristDato));
    }
}

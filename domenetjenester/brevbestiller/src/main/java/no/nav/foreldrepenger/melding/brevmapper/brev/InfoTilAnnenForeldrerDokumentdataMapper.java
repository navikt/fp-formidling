package no.nav.foreldrepenger.melding.brevmapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.opprettFellesDokumentdataBuilder;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDato;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.InfoTilAnnenForelderDokumentdata;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingÅrsakType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.INFO_TIL_ANNEN_FORELDER)
public class InfoTilAnnenForeldrerDokumentdataMapper implements DokumentdataMapper {
    private DomeneobjektProvider domeneobjektProvider;

    InfoTilAnnenForeldrerDokumentdataMapper() {
        //CDI
    }

    @Inject
    public InfoTilAnnenForeldrerDokumentdataMapper(DomeneobjektProvider domeneobjektProvider) {
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String getTemplateNavn() {
        return "foreldrepenger-infotilannenforelder";
    }

    @Override
    public InfoTilAnnenForelderDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse, Behandling behandling) {

        var fellesBuilder = opprettFellesDokumentdataBuilder(dokumentFelles, hendelse, behandling);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), behandling.getSpråkkode()) : null);

        BehandlingÅrsakType aarsak = BehandlingÅrsakType.INFOBREV_BEHANDLING;

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
                    .map(d-> formaterDato(d, behandling.getSpråkkode()))
                    .orElse(null);
        }


        return InfoTilAnnenForelderDokumentdata.ny()
                .medFelles(fellesBuilder.build())
                .medBehandlingÅrsak(aarsak.getKode())
                .medSisteUttaksdagMor(sisteUttaksdagMor)
                .build();
    }
}
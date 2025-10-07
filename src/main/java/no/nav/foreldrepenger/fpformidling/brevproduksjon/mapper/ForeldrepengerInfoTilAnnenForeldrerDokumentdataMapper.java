package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.UttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.ForeldrepengerInfoTilAnnenForelderDokumentdata;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingÅrsakType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.FORELDREPENGER_INFOBREV_TIL_ANNEN_FORELDER)
public class ForeldrepengerInfoTilAnnenForeldrerDokumentdataMapper implements DokumentdataMapper {
    private DomeneobjektProvider domeneobjektProvider;

    ForeldrepengerInfoTilAnnenForeldrerDokumentdataMapper() {
        //CDI
    }

    @Inject
    public ForeldrepengerInfoTilAnnenForeldrerDokumentdataMapper(DomeneobjektProvider domeneobjektProvider) {
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String getTemplateNavn() {
        return "foreldrepenger-infotilannenforelder";
    }

    @Override
    public ForeldrepengerInfoTilAnnenForelderDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                                             DokumentHendelse hendelse,
                                                                             Behandling behandling,
                                                                             boolean erUtkast) {

        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, erUtkast);
        var språkkode = dokumentFelles.getSpråkkode();
        fellesBuilder.medBrevDato(
            dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), språkkode) : null);

        var aarsak = BehandlingÅrsakType.INFOBREV_BEHANDLING;

        if (behandling.harBehandlingÅrsak(BehandlingÅrsakType.INFOBREV_OPPHOLD)) {
            aarsak = BehandlingÅrsakType.INFOBREV_OPPHOLD;
        }

        var uttakResultatPerioder = domeneobjektProvider.hentForeldrepengerUttakHvisFinnes(behandling);

        String sisteUttaksdagMor = null;
        if (uttakResultatPerioder.isPresent() && BehandlingÅrsakType.INFOBREV_BEHANDLING.equals(aarsak)) {
            sisteUttaksdagMor = uttakResultatPerioder.get()
                .perioderAnnenPart()
                .stream()
                .filter(up -> PeriodeResultatType.INNVILGET.equals(up.getPeriodeResultatType()) || up.getAktiviteter()
                    .stream()
                    .anyMatch(upa -> upa.getTrekkdager().compareTo(BigDecimal.ZERO) > 0))
                .map(UttakResultatPeriode::getTom)
                .max(LocalDate::compareTo)
                .map(d -> formaterDato(d, språkkode))
                .orElse(null);
        }


        return ForeldrepengerInfoTilAnnenForelderDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medBehandlingÅrsak(aarsak.getKode())
            .medSisteUttaksdagMor(sisteUttaksdagMor)
            .build();
    }
}

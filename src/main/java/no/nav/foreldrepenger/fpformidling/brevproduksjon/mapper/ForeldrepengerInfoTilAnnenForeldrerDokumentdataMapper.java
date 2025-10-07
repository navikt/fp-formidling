package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.ForeldrepengerInfoTilAnnenForelderDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.BrevGrunnlag;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.BrevGrunnlag.ForeldrepengerUttak;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingÅrsakType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.FORELDREPENGER_INFOBREV_TIL_ANNEN_FORELDER)
public class ForeldrepengerInfoTilAnnenForeldrerDokumentdataMapper implements DokumentdataMapper {

    @Override
    public String getTemplateNavn() {
        return "foreldrepenger-infotilannenforelder";
    }

    @Override
    public ForeldrepengerInfoTilAnnenForelderDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                                             DokumentHendelse hendelse,
                                                                             BrevGrunnlag behandling,
                                                                             boolean erUtkast) {

        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, erUtkast);
        var språkkode = dokumentFelles.getSpråkkode();
        fellesBuilder.medBrevDato(
            dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), språkkode) : null);

        var aarsak = BehandlingÅrsakType.INFOBREV_BEHANDLING;

        if (behandling.behandlingÅrsakTyper().stream().anyMatch(bå -> bå == BrevGrunnlag.BehandlingÅrsakType.INFOBREV_OPPHOLD)) {
            aarsak = BehandlingÅrsakType.INFOBREV_OPPHOLD;
        }

        var uttak = Optional.ofNullable(behandling.foreldrepengerUttak());

        String sisteUttaksdagMor = null;
        if (uttak.isPresent() && BehandlingÅrsakType.INFOBREV_BEHANDLING.equals(aarsak)) {
            sisteUttaksdagMor = uttak.get()
                .perioderAnnenpart()
                .stream()
                .filter(up -> ForeldrepengerUttak.PeriodeResultatType.INNVILGET.equals(up.periodeResultatType()) || up.aktiviteter()
                    .stream()
                    .anyMatch(upa -> upa.trekkdager().compareTo(BigDecimal.ZERO) > 0))
                .map(ForeldrepengerUttak.Periode::tom)
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

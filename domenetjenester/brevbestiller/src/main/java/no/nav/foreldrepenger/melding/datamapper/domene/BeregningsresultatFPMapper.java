package no.nav.foreldrepenger.melding.datamapper.domene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.BehandlingRestKlient;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat.BeregningsresultatMedUttaksplanDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat.BeregningsresultatPeriodeAndelDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat.BeregningsresultatPeriodeDto;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatAndel;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.vedtak.util.StringUtils;

@ApplicationScoped
public class BeregningsresultatFPMapper {
    private BehandlingRestKlient behandlingRestKlient;
    private KodeverkRepository kodeverkRepository;

    public BeregningsresultatFPMapper() {
        //CDI
    }

    @Inject
    public BeregningsresultatFPMapper(BehandlingRestKlient behandlingRestKlient,
                                      KodeverkRepository kodeverkRepository) {
        this.behandlingRestKlient = behandlingRestKlient;
        this.kodeverkRepository = kodeverkRepository;
    }

    public BeregningsresultatFP hentBeregningsresultat(Behandling behandling) {
        return mapBeregningsresultatFPFraDto(behandlingRestKlient.hentBeregningsresultatForeldrepenger(behandling.getResourceLinkDtos()));
    }

    private BeregningsresultatFP mapBeregningsresultatFPFraDto(BeregningsresultatMedUttaksplanDto dto) {
        return BeregningsresultatFP.ny()
                .medBeregningsresultatPerioder(Arrays.stream(dto.getPerioder()).map(this::mapPeriodeFraDto).collect(Collectors.toList()))
                .build();
    }

    private BeregningsresultatPeriode mapPeriodeFraDto(BeregningsresultatPeriodeDto dto) {
        BeregningsresultatPeriode beregningsresultatPeriode = BeregningsresultatPeriode.ny()
                .medDagsats((long) dto.getDagsats())
                .medPeriode(DatoIntervall.fraOgMedTilOgMed(dto.getFom(), dto.getTom()))
                .medBeregningsresultatAndel(Arrays.stream(dto.getAndeler()).map(this::mapAndelerFraDto)
                        .flatMap(List::stream).collect(Collectors.toList()))
                .build();
        return beregningsresultatPeriode;
    }

    //Fpsak slår sammen andeler i dto, så vi må eventuelt splitte dem opp igjen
    private List<BeregningsresultatAndel> mapAndelerFraDto(BeregningsresultatPeriodeAndelDto dto) {
        List<BeregningsresultatAndel> andeler = new ArrayList<>();
        if (dto.getRefusjon() != null && dto.getRefusjon() != 0) {
            andeler.add(lagEnkelAndel(dto, false, dto.getRefusjon()));
        }
        if (dto.getTilSoker() != null && dto.getTilSoker() != 0) {
            andeler.add(lagEnkelAndel(dto, true, dto.getTilSoker()));
        }
        return andeler;
    }

    private BeregningsresultatAndel lagEnkelAndel(BeregningsresultatPeriodeAndelDto dto, boolean brukerErMottaker, int dagsats) {
        return BeregningsresultatAndel.ny()
                .medAktivitetStatus(kodeverkRepository.finn(AktivitetStatus.class, dto.getAktivitetStatus().getKode()))
                .medArbeidsforholdRef(!StringUtils.nullOrEmpty(dto.getArbeidsforholdId()) ? ArbeidsforholdRef.ref(dto.getArbeidsforholdId()) : null)
                .medArbeidsgiver(ArbeidsgiverMapper.finnArbeidsgiver(dto.getArbeidsgiverNavn(), dto.getArbeidsgiverOrgnr()))
                .medStillingsprosent(null/*TODO*/)
                .medBrukerErMottaker(brukerErMottaker)
                .build();
    }

}

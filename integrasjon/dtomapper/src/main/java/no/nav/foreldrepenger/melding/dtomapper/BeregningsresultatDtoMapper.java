package no.nav.foreldrepenger.melding.dtomapper;

import static no.nav.foreldrepenger.melding.dtomapper.ArbeidsgiverMapper.finnArbeidsgiver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.BehandlingRestKlient;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat.BeregningsresultatEngangsstønadDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat.BeregningsresultatMedUttaksplanDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat.BeregningsresultatPeriodeAndelDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat.BeregningsresultatPeriodeDto;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatAndel;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatES;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.dtomapper.sortering.PeriodeComparator;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.vedtak.util.StringUtils;

@ApplicationScoped
public class BeregningsresultatDtoMapper {
    private BehandlingRestKlient behandlingRestKlient;
    private KodeverkRepository kodeverkRepository;

    public BeregningsresultatDtoMapper() {
        //CDI
    }

    @Inject
    public BeregningsresultatDtoMapper(BehandlingRestKlient behandlingRestKlient,
                                       KodeverkRepository kodeverkRepository) {
        this.behandlingRestKlient = behandlingRestKlient;
        this.kodeverkRepository = kodeverkRepository;
    }


    public static BeregningsresultatES mapBeregningsresultatESFraDto(BeregningsresultatEngangsstønadDto dto) {
        return new BeregningsresultatES(dto.getBeregnetTilkjentYtelse());
    }

    public BeregningsresultatFP mapBeregningsresultatFPFraDto(BeregningsresultatMedUttaksplanDto dto) {
        List<BeregningsresultatPeriode> beregningsresultatPerioder = Arrays.stream(dto.getPerioder())
                .map(this::mapPeriodeFraDto)
                .sorted(PeriodeComparator.BEREGNINGSRESULTAT)
                .collect(Collectors.toList());
        return BeregningsresultatFP.ny()
                .medBeregningsresultatPerioder(beregningsresultatPerioder)
                .build();
    }


    private BeregningsresultatPeriode mapPeriodeFraDto(BeregningsresultatPeriodeDto dto) {
        List<BeregningsresultatAndel> andelListe = new ArrayList<>();
        for (BeregningsresultatPeriodeAndelDto beregningsresultatPeriodeAndelDto : dto.getAndeler()) {
            andelListe.add(mapAndelFraDto(beregningsresultatPeriodeAndelDto));
        }
        return BeregningsresultatPeriode.ny()
                .medDagsats((long) dto.getDagsats())
                .medPeriode(DatoIntervall.fraOgMedTilOgMed(dto.getFom(), dto.getTom()))
                .medBeregningsresultatAndel(andelListe)
                .build();
    }

    //Fpsak slår sammen andeler i dto, så vi må eventuelt splitte dem opp igjen
    private BeregningsresultatAndel mapAndelFraDto(BeregningsresultatPeriodeAndelDto dto) {
        return BeregningsresultatAndel.ny()
                .medAktivitetStatus(kodeverkRepository.finn(AktivitetStatus.class, dto.getAktivitetStatus().getKode()))
                .medArbeidsforholdRef(!StringUtils.nullOrEmpty(dto.getArbeidsforholdId()) ? ArbeidsforholdRef.ref(dto.getArbeidsforholdId()) : null)
                .medArbeidsgiver(finnArbeidsgiver(dto.getArbeidsgiverNavn(), dto.getArbeidsgiverOrgnr()))
                .medStillingsprosent(dto.getStillingsprosent())
                .medBrukerErMottaker(dto.getTilSoker() != null && dto.getTilSoker() != 0)
                .medArbeidsgiverErMottaker(dto.getRefusjon() != null && dto.getRefusjon() != 0)
                .medDagsats(summerDagsats(dto))
                .build();
    }

    private int summerDagsats(BeregningsresultatPeriodeAndelDto dto) {
        int sum = 0;
        if (dto.getTilSoker() != null) {
            sum += dto.getTilSoker();
        }
        if (dto.getRefusjon() != null) {
            sum += dto.getRefusjon();
        }
        return sum;
    }

}

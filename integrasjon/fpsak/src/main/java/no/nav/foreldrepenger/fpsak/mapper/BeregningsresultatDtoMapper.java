package no.nav.foreldrepenger.fpsak.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat.BeregningsresultatEngangsstønadDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat.BeregningsresultatMedUttaksplanDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat.BeregningsresultatPeriodeAndelDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat.BeregningsresultatPeriodeDto;
import no.nav.foreldrepenger.fpsak.mapper.sortering.PeriodeComparator;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatAndel;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatES;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;

public class BeregningsresultatDtoMapper {

    public BeregningsresultatDtoMapper() {
        //CDI
    }


    public static BeregningsresultatES mapBeregningsresultatESFraDto(BeregningsresultatEngangsstønadDto dto) {
        return new BeregningsresultatES(dto.getBeregnetTilkjentYtelse());
    }

    public static BeregningsresultatFP mapBeregningsresultatFPFraDto(BeregningsresultatMedUttaksplanDto dto, UnaryOperator<String> hentNavn) {
        List<BeregningsresultatPeriode> beregningsresultatPerioder = Arrays.stream(dto.getPerioder())
                .map(p -> mapPeriodeFraDto(p, hentNavn))
                .sorted(PeriodeComparator.BEREGNINGSRESULTAT)
                .collect(Collectors.toList());
        return BeregningsresultatFP.ny()
                .leggTilBeregningsresultatPerioder(beregningsresultatPerioder)
                .build();
    }


    private static BeregningsresultatPeriode mapPeriodeFraDto(BeregningsresultatPeriodeDto dto, UnaryOperator<String> hentNavn) {
        List<BeregningsresultatAndel> andelListe = new ArrayList<>();
        for (BeregningsresultatPeriodeAndelDto beregningsresultatPeriodeAndelDto : dto.getAndeler()) {
            andelListe.add(mapAndelFraDto(beregningsresultatPeriodeAndelDto, hentNavn));
        }
        return BeregningsresultatPeriode.ny()
                .medDagsats((long) dto.getDagsats())
                .medPeriode(DatoIntervall.fraOgMedTilOgMed(dto.getFom(), dto.getTom()))
                .medBeregningsresultatAndel(andelListe)
                .build();
    }

    //Fpsak slår sammen andeler i dto, så vi må eventuelt splitte dem opp igjen
    private static BeregningsresultatAndel mapAndelFraDto(BeregningsresultatPeriodeAndelDto dto, UnaryOperator<String> hentNavn) {
        return BeregningsresultatAndel.ny()
                .medAktivitetStatus(AktivitetStatus.fraKode(dto.getAktivitetStatus().getKode()))
                .medArbeidsforholdRef(dto.getArbeidsforholdId() != null && !dto.getArbeidsforholdId().isEmpty() ? ArbeidsforholdRef.ref(dto.getArbeidsforholdId()) : null)
                .medArbeidsgiver(mapArbeidsgiverFraDto(dto, hentNavn))
                .medStillingsprosent(dto.getStillingsprosent())
                .medBrukerErMottaker(dto.getTilSoker() != null && dto.getTilSoker() != 0)
                .medArbeidsgiverErMottaker(dto.getRefusjon() != null && dto.getRefusjon() != 0)
                .medDagsats(summerDagsats(dto))
                .medTilSoker(dto.getTilSoker())
                .build();
    }

    private static Arbeidsgiver mapArbeidsgiverFraDto(BeregningsresultatPeriodeAndelDto dto, UnaryOperator<String> hentNavn) {
        if (!AktivitetStatus.ARBEIDSTAKER.getKode().equals(dto.getAktivitetStatus().getKode())
                || dto.getArbeidsgiverReferanse() == null) {
            return null;
        }
        return new Arbeidsgiver(dto.getArbeidsgiverReferanse(), hentNavn.apply(dto.getArbeidsgiverReferanse()));
    }

    private static int summerDagsats(BeregningsresultatPeriodeAndelDto dto) {
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

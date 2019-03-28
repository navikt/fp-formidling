package no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import no.nav.foreldrepenger.melding.datamapper.domene.hjelperdto.ArbeidsforholdDto;
import no.nav.foreldrepenger.melding.datamapper.domene.hjelperdto.PeriodeDto;

public class DuplikatVerktøy {

    private DuplikatVerktøy() {
        //for sonar
    }

    public static void slåSammenLikeArbeidsforhold(PeriodeDto periodeDto) {
        List<ArbeidsforholdDto> arbeidsforhold = periodeDto.getArbeidsforhold();
        List<ArbeidsforholdDto> nyListe = new ArrayList<>();
        nyListe.addAll(arbeidsforhold);

        for (ArbeidsforholdDto dto : arbeidsforhold) {
            for (ArbeidsforholdDto dto2 : arbeidsforhold) {
                if (nyListe.containsAll(List.of(dto, dto2)) && sammeArbeidsforhold(dto, dto2)) {
                    dto.setDagsats(dto.getDagsats() + dto2.getDagsats());
                    nyListe.remove(dto2);
                }
            }
        }
        periodeDto.setArbeidsforhold(nyListe);
    }

    private static boolean sammeArbeidsforhold(ArbeidsforholdDto dto, ArbeidsforholdDto dto2) {
        return !dto.equals(dto2)
                && Objects.equals(dto.getArbeidsgiverNavn(), dto2.getArbeidsgiverNavn())
                && Objects.equals(dto.getArbeidsforholdId(), dto2.getArbeidsforholdId());
    }
}

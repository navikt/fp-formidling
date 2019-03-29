package no.nav.foreldrepenger.melding.familiehendelse;

import java.time.LocalDate;
import java.util.Optional;

import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.AvklartDataAdopsjonDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.AvklartDataFodselDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.AvklartDataOmsorgDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.FamiliehendelseDto;

public class FamilieHendelse {
    private String familieHendelseType; //Kodeliste.FamilieHendelseType
    private int antallBarn;
    private String barna;
    private String UidentifisertBarn;
    private String terminbekreftelse;
    private Optional<LocalDate> termindato;

    public FamilieHendelse(FamiliehendelseDto dto) {
        this.antallBarn = utledAntallBarnFraDto(dto);
        this.termindato = finnTermindato(dto);
    }

    static Optional<LocalDate> finnTermindato(FamiliehendelseDto dto) {
        if (dto instanceof AvklartDataFodselDto) {
            if (((AvklartDataFodselDto) dto).getTermindato() != null) {
                return Optional.of(((AvklartDataFodselDto) dto).getTermindato());
            }
        }
        return Optional.empty();
    }

    static int utledAntallBarnFraDto(FamiliehendelseDto familiehendelseDto) {
        if (familiehendelseDto instanceof AvklartDataAdopsjonDto) {
            return ((AvklartDataAdopsjonDto) familiehendelseDto).getAdopsjonFodelsedatoer().size();
        } else if (familiehendelseDto instanceof AvklartDataFodselDto) {
            return utledAntallBarnFødsel((AvklartDataFodselDto) familiehendelseDto);
        } else if (familiehendelseDto instanceof AvklartDataOmsorgDto) {
            return ((AvklartDataOmsorgDto) familiehendelseDto).getAntallBarnTilBeregning();
        }
        throw new IllegalStateException("Familihendelse er av ukjent type");
    }

    private static int utledAntallBarnFødsel(AvklartDataFodselDto familiehendelseDto) {
        int sum = 0;
        if (familiehendelseDto.getAntallBarnTermin() != null) {
            sum += familiehendelseDto.getAntallBarnTermin();
        }
        if (familiehendelseDto.getAntallBarnFødt() != null) {
            sum += familiehendelseDto.getAntallBarnFødt();
        }
        return sum;
    }

    public Optional<LocalDate> getTermindato() {
        return termindato;
    }

    public String getFamilieHendelseType() {
        return familieHendelseType;
    }

    public int getAntallBarn() {
        return antallBarn;
    }

    public String getBarna() {
        return barna;
    }

    public String getUidentifisertBarn() {
        return UidentifisertBarn;
    }

    public String getTerminbekreftelse() {
        return terminbekreftelse;
    }
}

package no.nav.foreldrepenger.melding.familiehendelse;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Optional;

import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.AvklartDataAdopsjonDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.AvklartDataFodselDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.AvklartDataOmsorgDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.FamiliehendelseDto;

public class FamilieHendelse {
    public static final String typeFødsel = "FODSL";
    public static final String typeTermin = "TERM";

    private String familieHendelseType; //Kodeliste.FamilieHendelseType
    private BigInteger antallBarn;
    private String barna;
    private String UidentifisertBarn;
    private String terminbekreftelse;
    private Optional<LocalDate> termindato;
    private boolean barnErFødt;

    public FamilieHendelse(BigInteger antallBarn, Optional<LocalDate> termindato, boolean barnErFødt) {
        this.antallBarn = antallBarn;
        this.termindato = termindato;
        this.barnErFødt = barnErFødt;
    }

    public static FamilieHendelse fraDto(FamiliehendelseDto dto) {
        BigInteger antallBarnFraDto = utledAntallBarnFraDto(dto);
        Optional<LocalDate> termindatoFraDto = finnTermindato(dto);
        boolean barnErFødtFraDto = false;
        if (dto instanceof AvklartDataFodselDto) {
            barnErFødtFraDto = !((AvklartDataFodselDto) dto).getAvklartBarn().isEmpty();
        }
        return new FamilieHendelse(antallBarnFraDto, termindatoFraDto, barnErFødtFraDto);
    }

    static Optional<LocalDate> finnTermindato(FamiliehendelseDto dto) {
        if (dto instanceof AvklartDataFodselDto) {
            if (((AvklartDataFodselDto) dto).getTermindato() != null) {
                return Optional.of(((AvklartDataFodselDto) dto).getTermindato());
            }
        }
        return Optional.empty();
    }

    static BigInteger utledAntallBarnFraDto(FamiliehendelseDto familiehendelseDto) {
        if (familiehendelseDto instanceof AvklartDataAdopsjonDto) {
            return BigInteger.valueOf(((AvklartDataAdopsjonDto) familiehendelseDto).getAdopsjonFodelsedatoer().size());
        } else if (familiehendelseDto instanceof AvklartDataFodselDto) {
            return BigInteger.valueOf(utledAntallBarnFødsel((AvklartDataFodselDto) familiehendelseDto));
        } else if (familiehendelseDto instanceof AvklartDataOmsorgDto) {
            return BigInteger.valueOf(((AvklartDataOmsorgDto) familiehendelseDto).getAntallBarnTilBeregning());
        }
        throw new IllegalStateException("Familihendelse er av ukjent type");
    }

    private static int utledAntallBarnFødsel(AvklartDataFodselDto familiehendelseDto) {
        int sum = 0;
        if (familiehendelseDto.getAntallBarnTermin() != null) {
            sum += familiehendelseDto.getAntallBarnTermin();
        }
        sum += familiehendelseDto.getAvklartBarn().size();
        return sum;
    }

    public boolean isBarnErFødt() {
        return barnErFødt;
    }

    public Optional<LocalDate> getTermindato() {
        return termindato;
    }

    public String getFamilieHendelseType() {
        return familieHendelseType;
    }

    public BigInteger getAntallBarn() {
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

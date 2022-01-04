package no.nav.foreldrepenger.fpsak.mapper;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.AvklartBarnDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.AvklartDataAdopsjonDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.AvklartDataFodselDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.AvklartDataOmsorgDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.FamilieHendelseGrunnlagDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.FamiliehendelseDto;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelseType;

public class FamiliehendelseDtoMapper {


    private static Optional<LocalDate> hentSkjæringstidspunkt(FamiliehendelseDto gjeldendeHendelseDto) {
        return Optional.ofNullable(gjeldendeHendelseDto.getSkjæringstidspunkt());
    }

    static Optional<LocalDate> finnTermindato(FamiliehendelseDto dto) {
        if (dto instanceof AvklartDataFodselDto && ((AvklartDataFodselDto) dto).getTermindato() != null) {
            return Optional.of(((AvklartDataFodselDto) dto).getTermindato());
        }
        return Optional.empty();
    }

    private static Optional<LocalDate> finnFødselsdatoFraDto(FamiliehendelseDto dto) {
        if (dto instanceof AvklartDataFodselDto && ((AvklartDataFodselDto) dto).getAvklartBarn() != null) {
            return ((AvklartDataFodselDto) dto).getAvklartBarn().stream()
                    .map(AvklartBarnDto::getFodselsdato)
                    .filter(Objects::nonNull)
                    .min(LocalDate::compareTo);
        }
        return Optional.empty();
    }

    private static Optional<LocalDate> finnDødsdatoFraDto(FamiliehendelseDto dto) {
        if (dto instanceof AvklartDataFodselDto && ((AvklartDataFodselDto) dto).getAvklartBarn() != null) {
            return ((AvklartDataFodselDto) dto).getAvklartBarn().stream()
                    .map(AvklartBarnDto::getDodsdato)
                    .filter(Objects::nonNull)
                    .min(LocalDate::compareTo);
        }
        return Optional.empty();
    }

    private static int utledAntallBarnFødsel(AvklartDataFodselDto familiehendelseDto) {
        //bruk antall barn som er født hvis det finnes
        if (familiehendelseDto.getAvklartBarn() != null && !familiehendelseDto.getAvklartBarn().isEmpty()) {
            return familiehendelseDto.getAvklartBarn().size();
        } else if (familiehendelseDto.getAntallBarnTermin() != null) {
            return familiehendelseDto.getAntallBarnTermin();
        }
        return 0;
    }

    public static FamilieHendelse mapFamiliehendelsefraDto(FamilieHendelseGrunnlagDto grunnlagDto) {
        FamiliehendelseDto gjeldendeHendelseDto = grunnlagDto.getGjeldende();
        if (alleFelterErNull(gjeldendeHendelseDto)) {
            gjeldendeHendelseDto = grunnlagDto.getOppgitt();
        }
        if (alleFelterErNull(gjeldendeHendelseDto)) {
            return new FamilieHendelse(BigInteger.ZERO,
                    0,
                    false,
                    false,
                    FamilieHendelseType.UDEFINERT,
                    new FamilieHendelse.OptionalDatoer(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
        }
        BigInteger antallBarnFraDto = utledAntallBarnFraDto(gjeldendeHendelseDto);
        int antallDødeBarn = utledAntallDødeBarnFraDto(gjeldendeHendelseDto);
        FamilieHendelse.OptionalDatoer optionalDatoer = new FamilieHendelse.OptionalDatoer(
                hentSkjæringstidspunkt(gjeldendeHendelseDto),
                finnTermindato(gjeldendeHendelseDto),
                finnFødselsdatoFraDto(gjeldendeHendelseDto),
                finnDødsdatoFraDto(gjeldendeHendelseDto));
        boolean barnErFødtFraDto;
        boolean gjelderFødsel = false;
        if (gjeldendeHendelseDto instanceof AvklartDataFodselDto) {
            barnErFødtFraDto = erBarnFraDto((AvklartDataFodselDto) gjeldendeHendelseDto);
            gjelderFødsel = true;
        } else if (gjeldendeHendelseDto instanceof AvklartDataAdopsjonDto) {
            barnErFødtFraDto = erBarnFraDto((AvklartDataAdopsjonDto) gjeldendeHendelseDto);
        } else {
            barnErFødtFraDto = true;
        }
        FamilieHendelseType familiehendelseType = mapFamiliehendelseType(gjeldendeHendelseDto);
        return new FamilieHendelse(antallBarnFraDto, antallDødeBarn, barnErFødtFraDto, gjelderFødsel, familiehendelseType, optionalDatoer);
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

    static int utledAntallDødeBarnFraDto(FamiliehendelseDto gjeldendeHendelseDto) {
        if (gjeldendeHendelseDto instanceof AvklartDataFodselDto) {
            return utledAntallDødeBarn((AvklartDataFodselDto) gjeldendeHendelseDto);
        }
        return 0;
    }

    static int utledAntallDødeBarn(AvklartDataFodselDto fødselDto) {
        if (fødselDto.getAvklartBarn() != null && !fødselDto.getAvklartBarn().isEmpty()) {
            return (int) fødselDto.getAvklartBarn().stream().map(AvklartBarnDto::getDodsdato).filter(Objects::nonNull).count();
        }
        return 0;
    }

    private static boolean alleFelterErNull(FamiliehendelseDto dto) {
        if (dto instanceof AvklartDataOmsorgDto) {
            return alleFelterOmsorgErNull((AvklartDataOmsorgDto) dto);
        }
        if (dto instanceof AvklartDataFodselDto) {
            return alleFelterFødselErNull((AvklartDataFodselDto) dto);
        }
        if (dto instanceof AvklartDataAdopsjonDto) {
            return alleFelterAdopsjonErNull((AvklartDataAdopsjonDto) dto);
        }
        return true;

    }

    private static boolean alleFelterAdopsjonErNull(AvklartDataAdopsjonDto dto) {
        return dto.getEktefellesBarn() == null &&
                dto.getMannAdoptererAlene() == null &&
                dto.getAdopsjonFodelsedatoer() == null &&
                dto.getAnkomstNorge() == null &&
                dto.getOmsorgsovertakelseDato() == null;
    }

    private static boolean alleFelterFødselErNull(AvklartDataFodselDto dto) {
        return dto.getAvklartBarn() == null &&
                dto.getBrukAntallBarnFraTps() == null &&
                dto.getErOverstyrt() == null &&
                dto.getMorForSykVedFodsel() == null &&
                dto.getAntallBarnTermin() == null &&
                dto.getTermindato() == null &&
                dto.getUtstedtdato() == null &&
                dto.getVedtaksDatoSomSvangerskapsuke() == null &&
                dto.getSkjæringstidspunkt() == null;
    }

    private static boolean alleFelterOmsorgErNull(AvklartDataOmsorgDto dto) {
        return dto.getAntallBarnTilBeregning() == null &&
                dto.getForeldreansvarDato() == null &&
                dto.getOmsorgsovertakelseDato() == null &&
                dto.getVilkarType() == null &&
                dto.getSkjæringstidspunkt() == null;
    }

    private static boolean erBarnFraDto(AvklartDataFodselDto gjeldendeHendelseDto) {
        return gjeldendeHendelseDto.getAvklartBarn() != null && !gjeldendeHendelseDto.getAvklartBarn().isEmpty();
    }

    private static boolean erBarnFraDto(AvklartDataAdopsjonDto gjeldendeHendelseDto) { //NOSONAR - denne er ikke unused...
        return gjeldendeHendelseDto.getAdopsjonFodelsedatoer() != null && !gjeldendeHendelseDto.getAdopsjonFodelsedatoer().isEmpty();
    }

    private static FamilieHendelseType mapFamiliehendelseType(FamiliehendelseDto dto) {
        if (dto instanceof AvklartDataAdopsjonDto) {
            return FamilieHendelseType.ADOPSJON;
        }
        if (dto instanceof AvklartDataOmsorgDto) {
            return FamilieHendelseType.OMSORG;
        }
        if (dto instanceof AvklartDataFodselDto) {
            return utledFødselEllerTermin((AvklartDataFodselDto) dto);
        }
        throw new IllegalStateException();
    }

    private static FamilieHendelseType utledFødselEllerTermin(AvklartDataFodselDto dto) {
        if (dto.getAvklartBarn() == null || dto.getAvklartBarn().isEmpty()) {
            return FamilieHendelseType.TERMIN;
        }
        return FamilieHendelseType.FØDSEL;
    }
}

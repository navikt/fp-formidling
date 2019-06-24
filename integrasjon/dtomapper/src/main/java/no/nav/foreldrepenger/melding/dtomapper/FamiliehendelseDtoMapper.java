package no.nav.foreldrepenger.melding.dtomapper;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.AvklartBarnDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.AvklartDataAdopsjonDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.AvklartDataFodselDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.AvklartDataOmsorgDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.FamilieHendelseGrunnlagDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.FamiliehendelseDto;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelseType;

@ApplicationScoped
public class FamiliehendelseDtoMapper {

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

    private Optional<LocalDate> hentSkjæringstidspunkt(FamiliehendelseDto gjeldendeHendelseDto) {
        return Optional.ofNullable(gjeldendeHendelseDto.getSkjæringstidspunkt());
    }

    static Optional<LocalDate> finnTermindato(FamiliehendelseDto dto) {
        if (dto instanceof AvklartDataFodselDto) {
            if (((AvklartDataFodselDto) dto).getTermindato() != null) {
                return Optional.of(((AvklartDataFodselDto) dto).getTermindato());
            }
        }
        return Optional.empty();
    }

    private Optional<LocalDate> finnFødselsdatoFraDto(FamiliehendelseDto dto) {
        if (dto instanceof AvklartDataFodselDto) {
            if (((AvklartDataFodselDto) dto).getAvklartBarn() != null) {
                return ((AvklartDataFodselDto) dto).getAvklartBarn().stream().filter(avklartBarnDto -> avklartBarnDto.getFodselsdato() != null).map(AvklartBarnDto::getFodselsdato).min(LocalDate::compareTo);
            }
        }
        return Optional.empty();
    }

    private Optional<LocalDate> finnDødsdatoFraDto(FamiliehendelseDto dto) {
        if (dto instanceof AvklartDataFodselDto) {
            if (((AvklartDataFodselDto) dto).getAvklartBarn() != null) {
                return ((AvklartDataFodselDto) dto).getAvklartBarn().stream().filter(avklartBarnDto -> avklartBarnDto.getDodsdato() != null).map(AvklartBarnDto::getDodsdato).min(LocalDate::compareTo);
            }
        }
        return Optional.empty();
    }

    private static int utledAntallBarnFødsel(AvklartDataFodselDto familiehendelseDto) {
        //bruk antall barn som er født hvis det finnes
        if (familiehendelseDto.getAvklartBarn() != null && familiehendelseDto.getAvklartBarn().size() > 0) {
            return familiehendelseDto.getAvklartBarn().size();
        } else if (familiehendelseDto.getAntallBarnTermin() != null) {
            return familiehendelseDto.getAntallBarnTermin();
        }
        return 0;
    }

    public FamilieHendelse mapFamiliehendelsefraDto(FamilieHendelseGrunnlagDto grunnlagDto) {
        FamiliehendelseDto gjeldendeHendelseDto = grunnlagDto.getGjeldende();
        if (alleFelterErNull(gjeldendeHendelseDto)) {
            gjeldendeHendelseDto = grunnlagDto.getOppgitt();
        }
        if (alleFelterErNull(gjeldendeHendelseDto)) {
            return new FamilieHendelse(BigInteger.ZERO,
                    false,
                    false,
                    FamilieHendelseType.UDEFINERT,
                    new FamilieHendelse.OptionalDatoer(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
        }
        BigInteger antallBarnFraDto = utledAntallBarnFraDto(gjeldendeHendelseDto);
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
        return new FamilieHendelse(antallBarnFraDto, barnErFødtFraDto, gjelderFødsel, familiehendelseType, optionalDatoer);
    }

    private boolean alleFelterErNull(FamiliehendelseDto dto) {
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

    private boolean alleFelterAdopsjonErNull(AvklartDataAdopsjonDto dto) {
        return dto.getEktefellesBarn() == null &&
                dto.getMannAdoptererAlene() == null &&
                dto.getAdopsjonFodelsedatoer() == null &&
                dto.getAnkomstNorge() == null &&
                dto.getOmsorgsovertakelseDato() == null;
    }

    private boolean alleFelterFødselErNull(AvklartDataFodselDto dto) {
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

    private boolean alleFelterOmsorgErNull(AvklartDataOmsorgDto dto) {
        return dto.getAntallBarnTilBeregning() == null &&
                dto.getForeldreansvarDato() == null &&
                dto.getOmsorgsovertakelseDato() == null &&
                dto.getVilkarType() == null &&
                dto.getSkjæringstidspunkt() == null;
    }

    private boolean erBarnFraDto(AvklartDataFodselDto gjeldendeHendelseDto) {
        return gjeldendeHendelseDto.getAvklartBarn() != null && !gjeldendeHendelseDto.getAvklartBarn().isEmpty();
    }


    private boolean erBarnFraDto(AvklartDataAdopsjonDto gjeldendeHendelseDto) {
        return gjeldendeHendelseDto.getAdopsjonFodelsedatoer() != null && !gjeldendeHendelseDto.getAdopsjonFodelsedatoer().isEmpty();
    }

    private FamilieHendelseType mapFamiliehendelseType(FamiliehendelseDto dto) {
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

    private FamilieHendelseType utledFødselEllerTermin(AvklartDataFodselDto dto) {
        if (dto.getAvklartBarn() == null || dto.getAvklartBarn().isEmpty()) {
            return FamilieHendelseType.TERMIN;
        }
        return FamilieHendelseType.FØDSEL;
    }
}

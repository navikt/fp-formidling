package no.nav.foreldrepenger.melding.datamapper.brev;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.melding.typer.Dato;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;

public class InfoTilAnnenForelderBrevMapperDatoTest {

    private InfoTilAnnenForelderBrevMapper mapper;
    private final DokumentFelles dokumentFelles = DatamapperTestUtil.getDokumentFelles();
    private final FellesType fellesType = DatamapperTestUtil.getFellesType();
    private final DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);

    @BeforeEach
    void setUp() {
        mapper = new InfoTilAnnenForelderBrevMapper(new BrevParametere(), domeneobjektProvider);
    }

    @Test
    public void skal_mappe_brev_med_fom_dato_fra_seneste_innvilgede_uttaksperiode() throws Exception {
        // Arrange
        Behandling behandling = DatamapperTestUtil.standardBehandling();
        DokumentHendelse dokumentHendelse = byggHendelse(DokumentMalType.INFO_TIL_ANNEN_FORELDER_DOK, FagsakYtelseType.FORELDREPENGER);
        UttakResultatPeriode periode1 = UttakResultatPeriode.ny()
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(LocalDate.now().minusDays(10), LocalDate.now().minusDays(7)))
                .build();
        UttakResultatPeriode periode2 = UttakResultatPeriode.ny()
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(LocalDate.now().minusDays(6), LocalDate.now().minusDays(4)))
                .build();
        UttakResultatPeriode periode3 = UttakResultatPeriode.ny()
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(LocalDate.now().minusDays(3), LocalDate.now()))
                .build();
        UttakResultatPerioder uttakResultatPerioder = UttakResultatPerioder.ny()
                .medPerioderAnnenPart(List.of(periode1, periode2, periode3))
                .build();
        when(domeneobjektProvider.hentUttaksresultatHvisFinnes(behandling)).thenReturn(Optional.of(uttakResultatPerioder));

        // Act
        String xml = mapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);

        // Assert
        assertThat(xml).contains(Dato.formaterDato(LocalDate.now().minusDays(4)));
    }

    @Test
    public void skal_mappe_brev_med_fom_dato_fra_seneste_uttaksperiode_med_trekkdager() throws Exception {
        // Arrange
        Behandling behandling = DatamapperTestUtil.standardBehandling();
        DokumentHendelse dokumentHendelse = byggHendelse(DokumentMalType.INFO_TIL_ANNEN_FORELDER_DOK, FagsakYtelseType.FORELDREPENGER);
        UttakResultatPeriode periode1 = UttakResultatPeriode.ny()
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(LocalDate.now().minusDays(10), LocalDate.now().minusDays(7)))
                .build();
        UttakResultatPeriodeAktivitet aktivitet2 = UttakResultatPeriodeAktivitet.ny()
                .medTrekkdager(BigDecimal.TEN)
                .build();
        UttakResultatPeriode periode2 = UttakResultatPeriode.ny()
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(LocalDate.now().minusDays(6), LocalDate.now().minusDays(4)))
                .medAktiviteter(List.of(aktivitet2))
                .build();
        UttakResultatPeriodeAktivitet aktivitet3 = UttakResultatPeriodeAktivitet.ny()
                .medTrekkdager(BigDecimal.ZERO)
                .build();
        UttakResultatPeriode periode3 = UttakResultatPeriode.ny()
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(LocalDate.now().minusDays(3), LocalDate.now()))
                .medAktiviteter(List.of(aktivitet3))
                .build();
        UttakResultatPerioder uttakResultatPerioder = UttakResultatPerioder.ny()
                .medPerioderAnnenPart(List.of(periode1, periode2, periode3))
                .build();
        when(domeneobjektProvider.hentUttaksresultatHvisFinnes(behandling)).thenReturn(Optional.of(uttakResultatPerioder));

        // Act
        String xml = mapper.mapTilBrevXML(fellesType, dokumentFelles, dokumentHendelse, behandling);

        // Assert
        assertThat(xml).contains(Dato.formaterDato(LocalDate.now().minusDays(4)));
    }

    private DokumentHendelse byggHendelse(DokumentMalType malType, FagsakYtelseType ytelseType) {
        return DokumentHendelse.builder()
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .medYtelseType(ytelseType)
                .medDokumentMalType(malType)
                .build();
    }
}

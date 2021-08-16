package no.nav.foreldrepenger.melding.brevmapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDato;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.behandling.BehandlingÅrsak;
import no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.InfoTilAnnenForelderDokumentdata;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingÅrsakType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;

class InfoBrevTilAnnenForeldrerDokumentdataMapperTest {
    private DokumentFelles dokumentFelles;
    private DokumentHendelse dokumentHendelse;

    private InfoTilAnnenForeldrerDokumentdataMapper infoTilAnnenForeldrerDokumentdataMapper;

    @Mock
    private DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);

    @BeforeEach
    void setUp() {
        dokumentFelles = DatamapperTestUtil.lagStandardDokumentFelles(DatamapperTestUtil.lagStandardDokumentData(DokumentMalType.INFOBREV_TIL_ANNEN_FORELDER));
        dokumentHendelse = lagStandardHendelseBuilder().medFritekst(null).build();

        infoTilAnnenForeldrerDokumentdataMapper = new InfoTilAnnenForeldrerDokumentdataMapper(domeneobjektProvider);
    }

    @Test
    void mapInfoTilAnnenForelder() {
        // Arrange
        Behandling behandling = opprettBehandling(lagBehÅrsak(BehandlingÅrsakType.INFOBREV_BEHANDLING));
        UttakResultatPerioder uttakResultatPerioder = settOppUttaksperioder(DatoIntervall.fraOgMedTilOgMed(LocalDate.now(), LocalDate.now().plusDays(20)),
                                                                            DatoIntervall.fraOgMedTilOgMed(LocalDate.now().plusDays(20), LocalDate.now().plusDays(40)));
        when(domeneobjektProvider.hentUttaksresultatHvisFinnes(behandling)).thenReturn(Optional.of(uttakResultatPerioder));

        //Act
        InfoTilAnnenForelderDokumentdata infoTilAnnenForelderData = infoTilAnnenForeldrerDokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        //assert
        assertThat(infoTilAnnenForelderData.getBehandlingsÅrsak()).isEqualTo(BehandlingÅrsakType.INFOBREV_BEHANDLING.getKode());
        assertThat(infoTilAnnenForelderData.getSisteUttaksdagMor()).isEqualTo(formaterDato(LocalDate.now().plusDays(40), behandling.getSpråkkode()));
    }

    @Test
    void mapInfoTilAnnenForelderOpphold() {
        // Arrange
        Behandling behandling = opprettBehandling(lagBehÅrsak(BehandlingÅrsakType.INFOBREV_OPPHOLD));
        UttakResultatPerioder uttakResultatPerioder = settOppUttaksperioder(DatoIntervall.fraOgMedTilOgMed(LocalDate.now(), LocalDate.now().plusDays(20)),
                DatoIntervall.fraOgMedTilOgMed(LocalDate.now().plusDays(20), LocalDate.now().plusDays(30)));
        when(domeneobjektProvider.hentUttaksresultatHvisFinnes(behandling)).thenReturn(Optional.of(uttakResultatPerioder));

        //Act
        InfoTilAnnenForelderDokumentdata infoTilAnnenForelderData = infoTilAnnenForeldrerDokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        //assert
        assertThat(infoTilAnnenForelderData.getBehandlingsÅrsak()).isEqualTo(BehandlingÅrsakType.INFOBREV_OPPHOLD.getKode());
        assertThat(infoTilAnnenForelderData.getSisteUttaksdagMor()).isEqualTo(null);
    }

    private BehandlingÅrsak lagBehÅrsak(BehandlingÅrsakType årsakType) {
        return BehandlingÅrsak.builder()
                .medBehandlingÅrsakType(årsakType)
                .build();
    }
    private Behandling opprettBehandling(BehandlingÅrsak behÅrsak) {
        var behandlingresultat = Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET);

        var behandlingBuilder = Behandling.builder()
                .medUuid(UUID.randomUUID())
                .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medBehandlingsresultat(behandlingresultat.build())
                .medSpråkkode(Språkkode.NB)
                .medBehandlingÅrsaker(List.of(behÅrsak));

        return behandlingBuilder.build();
    }
    private UttakResultatPerioder settOppUttaksperioder(DatoIntervall periodeEn, DatoIntervall periodeTo) {
        return UttakResultatPerioder.ny().medPerioder(
                List.of(lagUttakResPeriode(periodeEn), lagUttakResPeriode(periodeTo)))
                .medPerioderAnnenPart(List.of(lagUttakResPeriode(periodeEn), lagUttakResPeriode(periodeTo)))
                .build();
    }

    private UttakResultatPeriode lagUttakResPeriode(DatoIntervall periode) {
        return UttakResultatPeriode.ny()
                .medTidsperiode(periode)
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .build();
    }
}

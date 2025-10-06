package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;
import static no.nav.foreldrepenger.fpformidling.typer.DatoIntervall.fraOgMedTilOgMed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.opphorfp.ForeldrepengerOpphørDokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.domene.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.BeregningsgrunnlagAktivitetStatus;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.Hjemmel;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.Fagsak;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.ForeldrepengerUttak;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.UttakAktivitet;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.UttakArbeidType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.UttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.fpformidling.typer.Beløp;
import no.nav.foreldrepenger.fpformidling.typer.DokumentMal;

@ExtendWith(MockitoExtension.class)
class ForeldrepengerOpphørDokumentdataMapperTest {
    private static final int ANTALL_BARN = 2;

    private static final int KLAGEFRIST = 6;
    private static final int GRUNNBELØP = 100000;
    private static final LocalDate PERIODE1_FOM = LocalDate.now().plusDays(2);
    private static final LocalDate PERIODE1_TOM = LocalDate.now().plusDays(3);
    private static final LocalDate PERIODE2_FOM = LocalDate.now().plusDays(4);
    private static final LocalDate PERIODE2_TOM = LocalDate.now().plusDays(5);
    private static final LocalDate PERIODE3_FOM = LocalDate.now().plusDays(6);
    private static final LocalDate PERIODE3_TOM = LocalDate.now().plusDays(7);
    private static final LocalDate PERIODE4_FOM = LocalDate.now().plusDays(8);
    private static final LocalDate PERIODE4_TOM = LocalDate.now().plusDays(9);
    private static final PeriodeResultatÅrsak AVSLAG_ÅRSAK_1 = PeriodeResultatÅrsak.FOR_SEN_SØKNAD;
    private static final PeriodeResultatÅrsak OPPHØR_ÅRSAK_2_OG_3 = PeriodeResultatÅrsak.MOR_HAR_IKKE_OMSORG;
    private static final PeriodeResultatÅrsak BARN_DØD = PeriodeResultatÅrsak.BARNET_ER_DØD;
    private static final BigDecimal TREKKDAGER = BigDecimal.TEN;

    @Mock
    private DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);

    private ForeldrepengerOpphørDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    void before() {
        var brevParametere = new BrevParametere(KLAGEFRIST, 2, Period.ZERO, Period.ZERO);
        dokumentdataMapper = new ForeldrepengerOpphørDokumentdataMapper(brevParametere, domeneobjektProvider);

        when(domeneobjektProvider.hentBeregningsgrunnlagHvisFinnes(any(Behandling.class))).thenReturn(opprettBeregningsgrunnlag());
        when(domeneobjektProvider.hentForeldrepengerUttakHvisFinnes(any(Behandling.class))).thenReturn(opprettUttaksresultat());
    }

    @Test
    void skal_mappe_felter_for_brev_til_bruker() {
        // Arrange
        var dødsdato = LocalDate.now();
        var behandling = opprettBehandling(PERIODE2_FOM,
            new FamilieHendelse(List.of(new FamilieHendelse.Barn(LocalDate.now(), dødsdato)), LocalDate.now(), ANTALL_BARN, null));
        var dokumentFelles = lagStandardDokumentFelles(FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = lagStandardHendelseBuilder().medDokumentMal(DokumentMal.FORLENGET_SAKSBEHANDLINGSTID_MEDL).build();

        // Act
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getFelles()).isNotNull();
        assertThat(dokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(dokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(dokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(dokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDato(LocalDate.now(), dokumentFelles.getSpråkkode()));
        assertThat(dokumentdata.getFelles().getHarVerge()).isFalse();
        assertThat(dokumentdata.getFelles().getErKopi()).isFalse();
        assertThat(dokumentdata.getFelles().getSaksnummer()).isEqualTo(SAKSNUMMER);
        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo("FP");
        assertThat(dokumentdata.getFelles().getErUtkast()).isFalse();

        assertThat(dokumentdata.getAvslagÅrsaker()).contains(AVSLAG_ÅRSAK_1.getKode(), OPPHØR_ÅRSAK_2_OG_3.getKode(), BARN_DØD.getKode());
        assertThat(dokumentdata.erSøkerDød()).isFalse();
        assertThat(dokumentdata.erGjelderFødsel()).isTrue();
        assertThat(dokumentdata.getHalvG()).isEqualTo(GRUNNBELØP / 2);
        assertThat(dokumentdata.getLovhjemmelForAvslag()).isEmpty();
        assertThat(dokumentdata.getKlagefristUker()).isEqualTo(KLAGEFRIST);
        assertThat(dokumentdata.getBarnDødsdato()).isEqualTo(formaterDato(dødsdato, Språkkode.NB));
        assertThat(dokumentdata.getOpphørDato()).isEqualTo(formaterDato(PERIODE2_FOM, Språkkode.NB));
        assertThat(dokumentdata.getAntallBarn()).isEqualTo(ANTALL_BARN);
        assertThat(dokumentdata.isEndretDekningsgrad()).isTrue();
    }

    private Optional<Beregningsgrunnlag> opprettBeregningsgrunnlag() {
        return Optional.of(Beregningsgrunnlag.ny()
            .leggTilBeregningsgrunnlagAktivitetStatus(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.ARBEIDSTAKER))
            .medGrunnbeløp(new Beløp(BigDecimal.valueOf(GRUNNBELØP)))
            .medhHjemmel(Hjemmel.F_14_7)
            .medBesteberegnet(true)
            .medSeksAvDeTiBeste(true)
            .build());
    }

    private Optional<ForeldrepengerUttak> opprettUttaksresultat() {
        var uttakAktivitet = UttakResultatPeriodeAktivitet.ny()
            .medTrekkdager(TREKKDAGER)
            .medUtbetalingsprosent(BigDecimal.ZERO)
            .medUttakAktivitet(UttakAktivitet.ny().medUttakArbeidType(UttakArbeidType.ORDINÆRT_ARBEID).build())
            .medArbeidsprosent(BigDecimal.valueOf(100))
            .build();
        var uttakResultatPeriode1 = UttakResultatPeriode.ny()
            .medAktiviteter(of(uttakAktivitet))
            .medTidsperiode(fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
            .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
            .medPeriodeResultatÅrsak(AVSLAG_ÅRSAK_1)
            .build();
        var uttakResultatPeriode2 = UttakResultatPeriode.ny()
            .medAktiviteter(of(uttakAktivitet))
            .medTidsperiode(fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM))
            .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
            .medPeriodeResultatÅrsak(OPPHØR_ÅRSAK_2_OG_3)
            .build();
        var uttakResultatPeriode3 = UttakResultatPeriode.ny()
            .medAktiviteter(of(uttakAktivitet))
            .medTidsperiode(fraOgMedTilOgMed(PERIODE3_FOM, PERIODE3_TOM))
            .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
            .medPeriodeResultatÅrsak(OPPHØR_ÅRSAK_2_OG_3)
            .build();
        var uttakResultatPeriode4 = UttakResultatPeriode.ny()
            .medAktiviteter(of(uttakAktivitet))
            .medTidsperiode(fraOgMedTilOgMed(PERIODE4_FOM, PERIODE4_TOM))
            .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
            .medPeriodeResultatÅrsak(BARN_DØD)
            .build();
        return Optional.of(
            new ForeldrepengerUttak(of(uttakResultatPeriode1, uttakResultatPeriode2, uttakResultatPeriode3, uttakResultatPeriode4), List.of()));
    }

    private Behandling opprettBehandling(LocalDate opphørsdato, FamilieHendelse familieHendelse) {
        return Behandling.builder()
            .medUuid(UUID.randomUUID())
            .medBehandlingType(BehandlingType.REVURDERING)
            .medBehandlingsresultat(Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.AVSLÅTT)
                .medEndretDekningsgrad(true)
                .medKonsekvenserForYtelsen(of(KonsekvensForYtelsen.ENDRING_I_BEREGNING, KonsekvensForYtelsen.ENDRING_I_UTTAK))
                .medOpphørsdato(opphørsdato)
                .build())
            .medSpråkkode(Språkkode.NB)
            .medFagsak(Fagsak.ny().medBrukerRolle(RelasjonsRolleType.MORA).medYtelseType(FagsakYtelseType.FORELDREPENGER).build())
            .medFamilieHendelse(familieHendelse)
            .build();
    }

}

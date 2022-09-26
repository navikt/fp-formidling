package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentData;
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

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BeregningsgrunnlagAktivitetStatus;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.Hjemmel;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.opphørfp.ForeldrepengerOpphørDokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentData;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.fagsak.FagsakBackend;
import no.nav.foreldrepenger.fpformidling.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.fpformidling.familiehendelse.FamilieHendelseType;
import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.ForeldrepengerOpphørDokumentdata;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.fpformidling.typer.Beløp;
import no.nav.foreldrepenger.fpformidling.uttak.ForeldrepengerUttak;
import no.nav.foreldrepenger.fpformidling.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.fpformidling.uttak.UttakAktivitet;
import no.nav.foreldrepenger.fpformidling.uttak.UttakArbeidType;
import no.nav.foreldrepenger.fpformidling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.fpformidling.uttak.kodeliste.PeriodeResultatÅrsak;

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
    private static final PeriodeResultatÅrsak ÅRSAK_1 = PeriodeResultatÅrsak.FOR_SEN_SØKNAD;
    private static final PeriodeResultatÅrsak ÅRSAK_2_OG_3 = PeriodeResultatÅrsak.MOR_HAR_IKKE_OMSORG;
    private static final PeriodeResultatÅrsak ÅRSAK_4 = PeriodeResultatÅrsak.BARNET_ER_DØD;
    private static final BigDecimal TREKKDAGER = BigDecimal.TEN;

    @Mock
    private DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);

    private DokumentData dokumentData;

    private ForeldrepengerOpphørDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    public void before() {
        BrevParametere brevParametere = new BrevParametere(KLAGEFRIST, 2, Period.ZERO, Period.ZERO);
        dokumentData = lagStandardDokumentData(DokumentMalType.INNHENTE_OPPLYSNINGER);

        dokumentdataMapper = new ForeldrepengerOpphørDokumentdataMapper(brevParametere, domeneobjektProvider);

        when(domeneobjektProvider.hentFagsakBackend(any(Behandling.class))).thenReturn(opprettFagsakBackend());
        when(domeneobjektProvider.hentFamiliehendelse(any(Behandling.class))).thenReturn(opprettFamiliehendelse());
        when(domeneobjektProvider.hentBeregningsgrunnlagHvisFinnes(any(Behandling.class))).thenReturn(opprettBeregningsgrunnlag());
        when(domeneobjektProvider.hentForeldrepengerUttakHvisFinnes(any(Behandling.class))).thenReturn(opprettUttaksresultat());
    }

    @Test
    public void skal_mappe_felter_for_brev_til_bruker() {
        // Arrange
        Behandling behandling = opprettBehandling();
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        DokumentHendelse dokumentHendelse = lagStandardHendelseBuilder()
                .medDokumentMalType(DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_MEDL)
                .build();

        // Act
        ForeldrepengerOpphørDokumentdata dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getFelles()).isNotNull();
        assertThat(dokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(dokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(dokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(dokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDato(LocalDate.now(), behandling.getSpråkkode()));
        assertThat(dokumentdata.getFelles().getHarVerge()).isEqualTo(true);
        assertThat(dokumentdata.getFelles().getErKopi()).isEqualTo(true);
        assertThat(dokumentdata.getFelles().getSaksnummer()).isEqualTo(SAKSNUMMER);
        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo("FP");
        assertThat(dokumentdata.getFelles().getBehandlesAvKA()).isEqualTo(false);
        assertThat(dokumentdata.getFelles().getErUtkast()).isEqualTo(false);

        assertThat(dokumentdata.getAvslagÅrsaker()).contains(ÅRSAK_1.getKode(), ÅRSAK_2_OG_3.getKode(), ÅRSAK_4.getKode());
        assertThat(dokumentdata.erSøkerDød()).isFalse();
        assertThat(dokumentdata.erGjelderFødsel()).isTrue();
        assertThat(dokumentdata.getHalvG()).isEqualTo(GRUNNBELØP / 2);
        assertThat(dokumentdata.getLovhjemmelForAvslag()).isEmpty();
        assertThat(dokumentdata.getKlagefristUker()).isEqualTo(KLAGEFRIST);
        assertThat(dokumentdata.getBarnDødsdato()).isEqualTo(formaterDato(LocalDate.now(), Språkkode.NB));
        assertThat(dokumentdata.getOpphørDato()).isEqualTo(formaterDato(LocalDate.now(), Språkkode.NB));
        assertThat(dokumentdata.getAntallBarn()).isEqualTo(ANTALL_BARN);
    }

    private FagsakBackend opprettFagsakBackend() {
        return FagsakBackend.ny().medBrukerRolle(RelasjonsRolleType.MORA).build();
    }

    private FamilieHendelse opprettFamiliehendelse() {
        var now = LocalDate.now();
        return new FamilieHendelse(FamilieHendelseType.TERMIN, 2, 0, now, now, null, now, false, true);
    }

    private Optional<Beregningsgrunnlag> opprettBeregningsgrunnlag() {
        return Optional.of(Beregningsgrunnlag.ny()
                .leggTilBeregningsgrunnlagAktivitetStatus(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.ARBEIDSTAKER))
                .medGrunnbeløp(new Beløp(BigDecimal.valueOf(GRUNNBELØP)))
                .medhHjemmel(Hjemmel.F_14_7)
                .medBesteberegnet(true)
                .build());
    }

    private Optional<ForeldrepengerUttak> opprettUttaksresultat() {
        UttakResultatPeriodeAktivitet uttakAktivitet = UttakResultatPeriodeAktivitet.ny()
                .medTrekkdager(TREKKDAGER)
                .medUtbetalingsprosent(BigDecimal.ZERO)
                .medUttakAktivitet(UttakAktivitet.ny().medUttakArbeidType(UttakArbeidType.ORDINÆRT_ARBEID).build())
                .medArbeidsprosent(BigDecimal.valueOf(100))
                .build();
        UttakResultatPeriode uttakResultatPeriode1 = UttakResultatPeriode.ny()
                .medAktiviteter(of(uttakAktivitet))
                .medTidsperiode(fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .medPeriodeResultatÅrsak(ÅRSAK_1)
                .build();
        UttakResultatPeriode uttakResultatPeriode2 = UttakResultatPeriode.ny()
                .medAktiviteter(of(uttakAktivitet))
                .medTidsperiode(fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM))
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .medPeriodeResultatÅrsak(ÅRSAK_2_OG_3)
                .build();
        UttakResultatPeriode uttakResultatPeriode3 = UttakResultatPeriode.ny()
                .medAktiviteter(of(uttakAktivitet))
                .medTidsperiode(fraOgMedTilOgMed(PERIODE3_FOM, PERIODE3_TOM))
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .medPeriodeResultatÅrsak(ÅRSAK_2_OG_3)
                .build();
        UttakResultatPeriode uttakResultatPeriode4 = UttakResultatPeriode.ny()
                .medAktiviteter(of(uttakAktivitet))
                .medTidsperiode(fraOgMedTilOgMed(PERIODE4_FOM, PERIODE4_TOM))
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .medPeriodeResultatÅrsak(ÅRSAK_4)
                .build();
        return Optional.of(new ForeldrepengerUttak(of(uttakResultatPeriode1, uttakResultatPeriode2, uttakResultatPeriode3, uttakResultatPeriode4),
                List.of(), false, true, false, false));
    }

    private Behandling opprettBehandling() {
        return Behandling.builder()
                .medUuid(UUID.randomUUID())
                .medBehandlingType(BehandlingType.REVURDERING)
                .medBehandlingsresultat(Behandlingsresultat.builder()
                        .medBehandlingResultatType(BehandlingResultatType.AVSLÅTT)
                        .medKonsekvenserForYtelsen(of(KonsekvensForYtelsen.ENDRING_I_BEREGNING, KonsekvensForYtelsen.ENDRING_I_UTTAK))
                        .build())
                .medSpråkkode(Språkkode.NB)
                .build();
    }

}

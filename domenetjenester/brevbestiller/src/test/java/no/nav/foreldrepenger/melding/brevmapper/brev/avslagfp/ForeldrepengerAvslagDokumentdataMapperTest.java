package no.nav.foreldrepenger.melding.brevmapper.brev.avslagfp;

import static java.util.List.of;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.FRITEKST;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardDokumentData;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDatoNorsk;
import static no.nav.foreldrepenger.melding.typer.DatoIntervall.fraOgMedTilOgMed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.BigInteger;
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

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagAktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Hjemmel;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentKategori;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentTypeId;
import no.nav.foreldrepenger.melding.fagsak.FagsakBackend;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelseType;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.avslagfp.ForeldrepengerAvslagDokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Fritekst;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.melding.mottattdokument.MottattDokument;
import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.typer.Beløp;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.UttakAktivitet;
import no.nav.foreldrepenger.melding.uttak.UttakArbeidType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.melding.uttak.kodeliste.PeriodeResultatÅrsak;

@ExtendWith(MockitoExtension.class)
public class ForeldrepengerAvslagDokumentdataMapperTest {

    private static final int KLAGEFRIST = 6;
    private static final int GRUNNBELØP = 100000;
    private static final LocalDate SØKNAD_DATO = LocalDate.now().minusDays(1);
    private static final LocalDate PERIODE1_FOM = LocalDate.now().plusDays(2);
    private static final LocalDate PERIODE1_TOM = LocalDate.now().plusDays(3);
    private static final LocalDate PERIODE2_FOM = LocalDate.now().plusDays(4);
    private static final LocalDate PERIODE2_TOM = LocalDate.now().plusDays(5);
    private static final LocalDate PERIODE3_FOM = LocalDate.now().plusDays(6);
    private static final LocalDate PERIODE3_TOM = LocalDate.now().plusDays(7);
    private static final PeriodeResultatÅrsak ÅRSAK_1 = PeriodeResultatÅrsak.FOR_SEN_SØKNAD;
    private static final PeriodeResultatÅrsak ÅRSAK_2_OG_3 = PeriodeResultatÅrsak.MOR_HAR_IKKE_OMSORG;
    private static final BigDecimal TREKKDAGER = BigDecimal.TEN;

    @Mock
    private DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);

    private DokumentData dokumentData;

    private ForeldrepengerAvslagDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    public void before() {
        BrevParametere brevParametere = new BrevParametere(KLAGEFRIST, 2, Period.ZERO, Period.ZERO);
        dokumentData = lagStandardDokumentData(DokumentMalType.FORELDREPENGER_AVSLAG);
        dokumentdataMapper = new ForeldrepengerAvslagDokumentdataMapper(brevParametere, domeneobjektProvider);

        when(domeneobjektProvider.hentFagsakBackend(any(Behandling.class))).thenReturn(opprettFagsakBackend());
        when(domeneobjektProvider.hentMottatteDokumenter(any(Behandling.class))).thenReturn(opprettMottattDokument());
        when(domeneobjektProvider.hentFamiliehendelse(any(Behandling.class))).thenReturn(opprettFamiliehendelse());
        when(domeneobjektProvider.hentBeregningsresultatFPHvisFinnes(any(Behandling.class))).thenReturn(opprettBeregningsresultatFP());
        when(domeneobjektProvider.hentBeregningsgrunnlagHvisFinnes(any(Behandling.class))).thenReturn(opprettBeregningsgrunnlag());
        when(domeneobjektProvider.hentUttaksresultatHvisFinnes(any(Behandling.class))).thenReturn(opprettUttaksresultat());
    }

    @Test
    public void skal_mappe_felter_for_brev() {
        // Arrange
        Behandling behandling = opprettBehandling();
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        DokumentHendelse dokumentHendelse = lagStandardHendelseBuilder().build();

        // Act
        ForeldrepengerAvslagDokumentdata dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, true);

        // Assert
        assertThat(dokumentdata.getFelles()).isNotNull();
        assertThat(dokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(dokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(dokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(dokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoNorsk(LocalDate.now()));
        assertThat(dokumentdata.getFelles().getHarVerge()).isEqualTo(true);
        assertThat(dokumentdata.getFelles().getErKopi()).isEqualTo(true);
        assertThat(dokumentdata.getFelles().getSaksnummer()).isEqualTo(SAKSNUMMER);
        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo("FP");
        assertThat(dokumentdata.getFelles().getFritekst()).isEqualTo(Fritekst.fra(FRITEKST));
        assertThat(dokumentdata.getFelles().getBehandlesAvKA()).isEqualTo(false);
        assertThat(dokumentdata.getFelles().getErUtkast()).isEqualTo(true);

        assertThat(dokumentdata.getRelasjonskode()).isEqualTo("MOR");
        assertThat(dokumentdata.getMottattDato()).isEqualTo(formaterDatoNorsk(SØKNAD_DATO));
        assertThat(dokumentdata.getGjelderFødsel()).isTrue();
        assertThat(dokumentdata.getBarnErFødt()).isFalse();
        assertThat(dokumentdata.getAnnenForelderHarRett()).isTrue();
        assertThat(dokumentdata.getAntallBarn()).isEqualTo(2);
        assertThat(dokumentdata.getHalvG()).isEqualTo(GRUNNBELØP / 2);
        assertThat(dokumentdata.getKlagefristUker()).isEqualTo(KLAGEFRIST);
        assertThat(dokumentdata.getLovhjemmelForAvslag()).isEqualTo("§ forvaltningsloven § 35");
        assertThat(dokumentdata.getAntallPerioder()).isEqualTo(2);
        assertThat(dokumentdata.getAvslåttePerioder()).hasSize(2); // Periode 2 og 3 skal slås sammen
        assertThat(dokumentdata.getAvslåttePerioder().get(0).getAvslagsårsak().getKode()).isEqualTo(ÅRSAK_1.getKode());
        assertThat(dokumentdata.getAvslåttePerioder().get(0).getPeriodeFom()).isEqualTo(PERIODE1_FOM);
        assertThat(dokumentdata.getAvslåttePerioder().get(0).getPeriodeTom()).isEqualTo(PERIODE1_TOM);
        assertThat(dokumentdata.getAvslåttePerioder().get(0).getAntallTapteDager()).isEqualTo(TREKKDAGER.intValue());
        assertThat(dokumentdata.getAvslåttePerioder().get(1).getAvslagsårsak().getKode()).isEqualTo(ÅRSAK_2_OG_3.getKode());
        assertThat(dokumentdata.getAvslåttePerioder().get(1).getPeriodeFom()).isEqualTo(PERIODE2_FOM);
        assertThat(dokumentdata.getAvslåttePerioder().get(1).getPeriodeTom()).isEqualTo(PERIODE3_TOM);
        assertThat(dokumentdata.getAvslåttePerioder().get(1).getAntallTapteDager()).isEqualTo(TREKKDAGER.intValue() * 2);
    }

    private List<MottattDokument> opprettMottattDokument() {
        return of(new MottattDokument(SØKNAD_DATO, DokumentTypeId.FORELDREPENGER_ENDRING_SØKNAD, DokumentKategori.SØKNAD));
    }

    private FagsakBackend opprettFagsakBackend() {
        return FagsakBackend.ny().medBrukerRolle(RelasjonsRolleType.MORA).build();
    }

    private FamilieHendelse opprettFamiliehendelse() {
        FamilieHendelse.OptionalDatoer optionalDatoer = new FamilieHendelse.OptionalDatoer(Optional.empty(), Optional.of(LocalDate.now()), Optional.empty(), Optional.empty());
        return new FamilieHendelse(BigInteger.valueOf(2), false, true, FamilieHendelseType.TERMIN, optionalDatoer);
    }

    private Optional<BeregningsresultatFP> opprettBeregningsresultatFP() {
        return Optional.of(BeregningsresultatFP.ny()
                .leggTilBeregningsresultatPerioder(of(
                        BeregningsresultatPeriode.ny()
                                .medDagsats(100L)
                                .medPeriode(fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
                                .build(),
                        BeregningsresultatPeriode.ny()
                                .medDagsats(100L * 2)
                                .medPeriode(fraOgMedTilOgMed(PERIODE2_FOM, PERIODE2_TOM))
                                .build(),
                        BeregningsresultatPeriode.ny()
                                .medDagsats(100L * 3)
                                .medPeriode(fraOgMedTilOgMed(PERIODE3_FOM, PERIODE3_TOM))
                                .build()))
                .build());
    }

    private Optional<Beregningsgrunnlag> opprettBeregningsgrunnlag() {
        return Optional.of(Beregningsgrunnlag.ny()
                .leggTilBeregningsgrunnlagAktivitetStatus(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.ARBEIDSTAKER))
                .medGrunnbeløp(new Beløp(BigDecimal.valueOf(GRUNNBELØP)))
                .medhHjemmel(Hjemmel.F_14_7)
                .medBesteberegnet(true)
                .build());
    }

    private Optional<UttakResultatPerioder> opprettUttaksresultat() {
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
        return Optional.of(UttakResultatPerioder.ny()
                .medPerioder(of(uttakResultatPeriode1, uttakResultatPeriode2, uttakResultatPeriode3))
                .medAnnenForelderHarRett(true)
                .build());
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
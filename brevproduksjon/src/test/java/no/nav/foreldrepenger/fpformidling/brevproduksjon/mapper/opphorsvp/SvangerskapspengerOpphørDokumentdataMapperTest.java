package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.opphorsvp;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentData;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseSVPBuilder;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;
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

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.SvpMapperUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.BeregningsgrunnlagAktivitetStatus;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.Hjemmel;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentData;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.fpformidling.familiehendelse.FamilieHendelseType;
import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseAndel;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelseForeldrepenger;
import no.nav.foreldrepenger.fpformidling.tilkjentytelse.TilkjentYtelsePeriode;
import no.nav.foreldrepenger.fpformidling.typer.Beløp;
import no.nav.foreldrepenger.fpformidling.typer.DatoIntervall;
import no.nav.foreldrepenger.fpformidling.uttak.fp.PeriodeResultatType;
import no.nav.foreldrepenger.fpformidling.uttak.svp.PeriodeIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvangerskapspengerUttak;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvpUttakResultatArbeidsforhold;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvpUttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.fpformidling.virksomhet.Arbeidsgiver;

@ExtendWith(MockitoExtension.class)
class SvangerskapspengerOpphørDokumentdataMapperTest {
    private static final int KLAGEFRIST = 6;
    private static final int GRUNNBELØP = 100000;
    private static final LocalDate PERIODE1_FOM = LocalDate.now().plusDays(2);
    private static final LocalDate PERIODE1_TOM = LocalDate.now().plusDays(3);
    private static final String ARBEIDSGIVER_1_NAVN = "Arbeidsgiver_1";
    private static final Arbeidsgiver ARBEIDSGIVER_1 = new Arbeidsgiver("123456", ARBEIDSGIVER_1_NAVN);

    @Mock
    private DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);

    private DokumentData dokumentData;

    private SvangerskapspengerOpphørDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    void before() {
        var brevParametere = new BrevParametere(KLAGEFRIST, 2, Period.ZERO, Period.ZERO);
        dokumentData = lagStandardDokumentData(DokumentMalType.SVANGERSKAPSPENGER_OPPHØR);
        dokumentdataMapper = new SvangerskapspengerOpphørDokumentdataMapper(brevParametere, domeneobjektProvider);

        when(domeneobjektProvider.hentFamiliehendelse(any(Behandling.class))).thenReturn(opprettFamiliehendelse());
        when(domeneobjektProvider.hentBeregningsgrunnlagHvisFinnes(any(Behandling.class))).thenReturn(opprettBeregningsgrunnlag());
        when(domeneobjektProvider.hentSvangerskapspengerUttakHvisFinnes(any(Behandling.class))).thenReturn(opprettUttaksresultat());
        when(domeneobjektProvider.hentTilkjentYtelseDagytelseHvisFinnes(any(Behandling.class))).thenReturn(opprettTilkjentYtelse());
    }

    @Test
    void skal_mappe_felter_for_brev() {
        // Arrange
        var behandling = opprettBehandling();
        var dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        var dokumentHendelse = lagStandardHendelseSVPBuilder().build();

        // Act
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getFelles()).isNotNull();
        assertThat(dokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(dokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(dokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(dokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoNorsk(LocalDate.now()));
        assertThat(dokumentdata.getFelles().getHarVerge()).isTrue();
        assertThat(dokumentdata.getFelles().getErKopi()).isTrue();
        assertThat(dokumentdata.getFelles().getSaksnummer()).isEqualTo(SAKSNUMMER);
        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo(FagsakYtelseType.SVANGERSKAPSPENGER.getKode());
        assertThat(dokumentdata.getFelles().getBehandlesAvKA()).isFalse();
        assertThat(dokumentdata.getFelles().getErUtkast()).isFalse();

        assertThat(dokumentdata.getOpphørsdato()).isEqualTo(formaterDato(SvpMapperUtil.justerForHelg(PERIODE1_TOM.plusDays(1)), Språkkode.NB));
        assertThat(dokumentdata.getDødsdatoBarn()).isNull();
        assertThat(dokumentdata.getFødselsdato()).isEqualTo(formaterDato(LocalDate.now(), Språkkode.NB));
        assertThat(dokumentdata.getErSøkerDød()).isFalse();
        assertThat(dokumentdata.getHalvG()).isEqualTo(GRUNNBELØP / 2);
        assertThat(dokumentdata.getLovhjemmel()).isEqualTo("§ 14-4 og forvaltningsloven § 35");
        assertThat(dokumentdata.getOpphørtPeriode().getÅrsak()).isEqualTo(Årsak.of(PeriodeIkkeOppfyltÅrsak.PERIODEN_ER_IKKE_FØR_FØDSEL.getKode()));
        assertThat(dokumentdata.getOpphørtPeriode().getStønadsperiodeFom()).isEqualTo(formaterDato(PERIODE1_FOM, Språkkode.NB));
        assertThat(dokumentdata.getOpphørtPeriode().getStønadsperiodeTom()).isEqualTo(formaterDato(PERIODE1_TOM, Språkkode.NB));
        assertThat(dokumentdata.getOpphørtPeriode().getAntallArbeidsgivere()).isEqualTo(1);

    }

    private FamilieHendelse opprettFamiliehendelse() {
        var now = LocalDate.now();
        return new FamilieHendelse(FamilieHendelseType.TERMIN, 2, 0, null, now, now, null, false, true);
    }

    private Optional<Beregningsgrunnlag> opprettBeregningsgrunnlag() {
        return Optional.of(Beregningsgrunnlag.ny()
            .leggTilBeregningsgrunnlagAktivitetStatus(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.ARBEIDSTAKER))
            .medGrunnbeløp(new Beløp(BigDecimal.valueOf(GRUNNBELØP)))
            .medhHjemmel(Hjemmel.F_14_7)
            .medBesteberegnet(true)
            .build());
    }

    private Optional<TilkjentYtelseForeldrepenger> opprettTilkjentYtelse() {
        return Optional.of(TilkjentYtelseForeldrepenger.ny()
            .leggTilPerioder(of(TilkjentYtelsePeriode.ny()
                .medDagsats(500L)
                .medPeriode(fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
                .medAndeler(of(TilkjentYtelseAndel.ny()
                    .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                    .medArbeidsgiver(ARBEIDSGIVER_1)
                    .medStillingsprosent(BigDecimal.valueOf(50))
                    .build()))
                .build()))
            .build());
    }

    private Optional<SvangerskapspengerUttak> opprettUttaksresultat() {
        var uttakResultatPeriode1 = SvpUttakResultatPeriode.Builder.ny()
            .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
            .medArbeidsgiverNavn(ARBEIDSGIVER_1_NAVN)
            .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
            .build();
        var uttakResultatPeriode2 = SvpUttakResultatPeriode.Builder.ny()
            .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
            .medPeriodeIkkeOppfyltÅrsak(PeriodeIkkeOppfyltÅrsak.PERIODEN_ER_IKKE_FØR_FØDSEL)
            .medArbeidsgiverNavn(ARBEIDSGIVER_1_NAVN)
            .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
            .build();

        var uttaksresultat = SvangerskapspengerUttak.Builder.ny()
            .leggTilUttakResultatArbeidsforhold(SvpUttakResultatArbeidsforhold.Builder.ny()
                .medArbeidsgiver(new Arbeidsgiver("1234", ARBEIDSGIVER_1_NAVN))
                .leggTilPerioder(List.of(uttakResultatPeriode1, uttakResultatPeriode2))
                .build())
            .build();

        return Optional.of(uttaksresultat);
    }

    private Behandling opprettBehandling() {
        return Behandling.builder()
            .medUuid(UUID.randomUUID())
            .medBehandlingType(BehandlingType.REVURDERING)
            .medBehandlingsresultat(Behandlingsresultat.builder()
                .medBehandlingResultatType(BehandlingResultatType.OPPHØR)
                .medAvslagsårsak(Avslagsårsak.UDEFINERT)
                .build())
            .medSpråkkode(Språkkode.NB)
            .build();
    }
}

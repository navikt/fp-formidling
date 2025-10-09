package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.opphorfp.ForeldrepengerOpphørDokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.BrevGrunnlag;
import no.nav.foreldrepenger.fpformidling.typer.DokumentMal;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.AktivitetStatusDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.HjemmelDto;

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

    private ForeldrepengerOpphørDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    void before() {
        var brevParametere = new BrevParametere(KLAGEFRIST, 2, Period.ZERO, Period.ZERO);
        dokumentdataMapper = new ForeldrepengerOpphørDokumentdataMapper(brevParametere);
    }

    @Test
    void skal_mappe_felter_for_brev_til_bruker() {
        // Arrange
        var dødsdato = LocalDate.now();
        var barn = new BrevGrunnlag.Barn(LocalDate.now(), dødsdato);
        var behandling = opprettBehandling(PERIODE2_FOM, new BrevGrunnlag.FamilieHendelse(List.of(barn), LocalDate.now(), ANTALL_BARN, null));
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

    private BeregningsgrunnlagDto opprettBeregningsgrunnlag() {
        return new BeregningsgrunnlagDto(List.of(AktivitetStatusDto.ARBEIDSTAKER), HjemmelDto.F_14_7, BigDecimal.valueOf(GRUNNBELØP), List.of(), true,
            true);
    }

    private BrevGrunnlag.ForeldrepengerUttak opprettUttaksresultat() {
        var uttakAktivitet = new BrevGrunnlag.ForeldrepengerUttak.Aktivitet(BrevGrunnlag.ForeldrepengerUttak.TrekkontoType.MØDREKVOTE, TREKKDAGER,
            BigDecimal.valueOf(100), "123", null, BigDecimal.ZERO, BrevGrunnlag.UttakArbeidType.ORDINÆRT_ARBEID, false);

        var uttakResultatPeriode1 = new BrevGrunnlag.ForeldrepengerUttak.Periode(PERIODE1_FOM, PERIODE1_TOM, of(uttakAktivitet),
            BrevGrunnlag.PeriodeResultatType.AVSLÅTT, AVSLAG_ÅRSAK_1.getKode(), null, "14-10", null, null, false);
        var uttakResultatPeriode2 = new BrevGrunnlag.ForeldrepengerUttak.Periode(PERIODE2_FOM, PERIODE2_TOM, of(uttakAktivitet),
            BrevGrunnlag.PeriodeResultatType.AVSLÅTT, OPPHØR_ÅRSAK_2_OG_3.getKode(), null, "14-10", null, null, false);
        var uttakResultatPeriode3 = new BrevGrunnlag.ForeldrepengerUttak.Periode(PERIODE3_FOM, PERIODE3_TOM, of(uttakAktivitet),
            BrevGrunnlag.PeriodeResultatType.AVSLÅTT, OPPHØR_ÅRSAK_2_OG_3.getKode(), null, "14-10", null, null, false);
        var uttakResultatPeriode4 = new BrevGrunnlag.ForeldrepengerUttak.Periode(PERIODE4_FOM, PERIODE4_TOM, of(uttakAktivitet),
            BrevGrunnlag.PeriodeResultatType.AVSLÅTT, BARN_DØD.getKode(), null, "14-10", null, null, false);

        return new BrevGrunnlag.ForeldrepengerUttak(of(), 0,
            of(uttakResultatPeriode1, uttakResultatPeriode2, uttakResultatPeriode3, uttakResultatPeriode4), of(), false);
    }

    private BrevGrunnlag opprettBehandling(LocalDate opphørsdato, BrevGrunnlag.FamilieHendelse familieHendelse) {
        return new BrevGrunnlag(UUID.randomUUID(), UUID.randomUUID().toString(), BrevGrunnlag.FagsakYtelseType.FORELDREPENGER,
            BrevGrunnlag.FagsakStatus.UNDER_BEHANDLING, BrevGrunnlag.RelasjonsRolleType.MORA, UUID.randomUUID().toString(), null,
            BrevGrunnlag.BehandlingType.REVURDERING, LocalDateTime.now().minusDays(1), null, "enhet", BrevGrunnlag.Språkkode.BOKMÅL, true,
            familieHendelse, null, null,
            new BrevGrunnlag.Behandlingsresultat(null, null, BrevGrunnlag.Behandlingsresultat.BehandlingResultatType.AVSLÅTT, null, null, null, false,
                opphørsdato, List.of(BrevGrunnlag.Behandlingsresultat.KonsekvensForYtelsen.ENDRING_I_BEREGNING,
                BrevGrunnlag.Behandlingsresultat.KonsekvensForYtelsen.ENDRING_I_UTTAK), List.of()), List.of(), null, opprettBeregningsgrunnlag(),
            null, null, null, null, List.of(), null, null, null, null, null, opprettUttaksresultat());
    }
}

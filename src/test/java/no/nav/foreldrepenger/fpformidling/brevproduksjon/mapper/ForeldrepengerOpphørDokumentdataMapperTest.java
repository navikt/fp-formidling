package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.barn;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.behandlingsresultat;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.brevGrunnlag;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.familieHendelse;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.foreldrepenger;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.foreldrepengerUttakAktivitet;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.foreldrepengerUttakPeriode;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.originalBehandling;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.BehandlingType;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Behandlingsresultat;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.FamilieHendelse;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Foreldrepenger;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.PeriodeResultatType;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.RelasjonsRolleType;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.UttakArbeidType;
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
import no.nav.foreldrepenger.fpformidling.domene.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
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
        var barnData = barn().fødselsdato(LocalDate.now()).dødsdato(dødsdato).build();
        var familieHendelseData = familieHendelse().barn(List.of(barnData)).termindato(LocalDate.now()).antallBarn(ANTALL_BARN).build();
        var behandling = opprettBehandling(PERIODE2_FOM, familieHendelseData);
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

        assertThat(dokumentdata.getAvslagÅrsaker()).contains(PeriodeResultatÅrsak.FOR_SEN_SØKNAD.getKode(),
            PeriodeResultatÅrsak.MOR_HAR_IKKE_OMSORG.getKode(), PeriodeResultatÅrsak.BARNET_ER_DØD.getKode());
        assertThat(dokumentdata.erSøkerDød()).isFalse();
        assertThat(dokumentdata.erGjelderFødsel()).isTrue();
        assertThat(dokumentdata.getHalvG()).isEqualTo(GRUNNBELØP / 2);
        assertThat(dokumentdata.getLovhjemmelForAvslag()).isEqualTo("§ 14-11");
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

    private Foreldrepenger opprettForeldrepenger() {
        var uttakAktivitet = foreldrepengerUttakAktivitet().trekkontoType(Foreldrepenger.TrekkontoType.MØDREKVOTE)
            .trekkdager(TREKKDAGER)
            .prosentArbeid(BigDecimal.valueOf(100))
            .arbeidsgiverReferanse("123")
            .utbetalingsgrad(BigDecimal.ZERO)
            .uttakArbeidType(UttakArbeidType.ORDINÆRT_ARBEID)
            .gradering(false)
            .build();

        var uttakResultatPeriode1 = foreldrepengerUttakPeriode().fom(PERIODE1_FOM)
            .tom(PERIODE1_TOM)
            .aktiviteter(of(uttakAktivitet))
            .periodeResultatType(PeriodeResultatType.AVSLÅTT, PeriodeResultatÅrsak.FOR_SEN_SØKNAD.getKode())
            .lovhjemler("14-11")
            .build();
        var uttakResultatPeriode2 = foreldrepengerUttakPeriode().fom(PERIODE2_FOM)
            .tom(PERIODE2_TOM)
            .aktiviteter(of(uttakAktivitet))
            .periodeResultatType(PeriodeResultatType.AVSLÅTT, PeriodeResultatÅrsak.MOR_HAR_IKKE_OMSORG.getKode())
            .lovhjemler("14-11")
            .build();
        var uttakResultatPeriode3 = foreldrepengerUttakPeriode().fom(PERIODE3_FOM)
            .tom(PERIODE3_TOM)
            .aktiviteter(of(uttakAktivitet))
            .periodeResultatType(PeriodeResultatType.AVSLÅTT, PeriodeResultatÅrsak.MOR_HAR_IKKE_OMSORG.getKode())
            .lovhjemler("14-11")
            .build();
        var uttakResultatPeriode4 = foreldrepengerUttakPeriode().fom(PERIODE4_FOM)
            .tom(PERIODE4_TOM)
            .aktiviteter(of(uttakAktivitet))
            .periodeResultatType(PeriodeResultatType.AVSLÅTT, PeriodeResultatÅrsak.BARNET_ER_DØD.getKode())
            .lovhjemler("14-11")
            .build();

        return foreldrepenger().stønadskontoer(of())
            .tapteDagerFpff(0)
            .perioderSøker(of(uttakResultatPeriode1, uttakResultatPeriode2, uttakResultatPeriode3, uttakResultatPeriode4))
            .perioderAnnenpart(of())
            .ønskerJustertUttakVedFødsel(false)
            .build();
    }

    private BrevGrunnlagDto opprettBehandling(LocalDate opphørsdato, FamilieHendelse familieHendelse) {
        var behandlingsresultatData = behandlingsresultat().behandlingResultatType(Behandlingsresultat.BehandlingResultatType.AVSLÅTT)
            .avslagsårsak(Avslagsårsak.MANGLENDE_DOKUMENTASJON.getKode())
            .opphørsdato(opphørsdato)
            .konsekvenserForYtelsen(List.of(Behandlingsresultat.KonsekvensForYtelsen.ENDRING_I_BEREGNING,
                Behandlingsresultat.KonsekvensForYtelsen.ENDRING_I_UTTAK))
            .endretDekningsgrad(true)
            .build();

        var originalBehandling = originalBehandling().originalBehandlingResultatType(Behandlingsresultat.BehandlingResultatType.INNVILGET)
            .familieHendelse(familieHendelse)
            .førsteDagMedUtbetaltForeldrepenger(opphørsdato)
            .build();
        return brevGrunnlag().uuid(UUID.randomUUID())
            .saksnummer(UUID.randomUUID().toString())
            .fagsakYtelseType(BrevGrunnlagDto.FagsakYtelseType.FORELDREPENGER)
            .relasjonsRolleType(RelasjonsRolleType.MORA)
            .aktørId(UUID.randomUUID().toString())
            .behandlingType(BehandlingType.REVURDERING)
            .opprettet(LocalDateTime.now().minusDays(1))
            .behandlendeEnhet("enhet")
            .språkkode(BrevGrunnlagDto.Språkkode.BOKMÅL)
            .automatiskBehandlet(true)
            .familieHendelse(familieHendelse)
            .behandlingsresultat(behandlingsresultatData)
            .beregningsgrunnlag(opprettBeregningsgrunnlag())
            .foreldrepenger(opprettForeldrepenger())
            .originalBehandling(originalBehandling)
            .build();
    }
}

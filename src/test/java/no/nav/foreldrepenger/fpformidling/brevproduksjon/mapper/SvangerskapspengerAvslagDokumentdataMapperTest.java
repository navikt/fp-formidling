package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;
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
import org.mockito.Mock;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.BeregningsgrunnlagAktivitetStatus;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.Hjemmel;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentData;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentKategori;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentTypeId;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakBackend;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.mottattdokument.MottattDokument;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.svp.PeriodeIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.uttak.svp.SvangerskapspengerUttak;
import no.nav.foreldrepenger.fpformidling.domene.uttak.svp.SvpUttakResultatArbeidsforhold;
import no.nav.foreldrepenger.fpformidling.domene.uttak.svp.SvpUttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.fpformidling.domene.virksomhet.Arbeidsgiver;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.typer.Beløp;
import no.nav.foreldrepenger.fpformidling.typer.DatoIntervall;

class SvangerskapspengerAvslagDokumentdataMapperTest {
    private static final int KLAGEFRIST = 6;
    private static final int GRUNNBELØP = 100000;
    private static final LocalDate PERIODE1_FOM = LocalDate.now().plusDays(2);
    private static final LocalDate PERIODE1_TOM = LocalDate.now().plusDays(3);
    private static final String ARBEIDSGIVER_1 = "Arbeidsgiver_1";
    private static final Språkkode SPRÅKKODE_NB = Språkkode.NB;

    @Mock
    private DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);

    private DokumentData dokumentData;

    private SvangerskapspengerAvslagDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    void before() {
        var brevParametere = new BrevParametere(KLAGEFRIST, 2, Period.ZERO, Period.ZERO);
        dokumentData = DatamapperTestUtil.lagStandardDokumentData(DokumentMalType.SVANGERSKAPSPENGER_AVSLAG);
        dokumentdataMapper = new SvangerskapspengerAvslagDokumentdataMapper(brevParametere, domeneobjektProvider);

        var mottattDokument = new MottattDokument(LocalDate.now(), DokumentTypeId.SØKNAD_SVANGERSKAPSPENGER, DokumentKategori.SØKNAD);

        when(domeneobjektProvider.hentBeregningsgrunnlagHvisFinnes(any(Behandling.class))).thenReturn(opprettBeregningsgrunnlag());
        when(domeneobjektProvider.hentSvangerskapspengerUttakHvisFinnes(any(Behandling.class))).thenReturn(
            opprettUttaksresultat());
        when(domeneobjektProvider.hentMottatteDokumenter(any(Behandling.class))).thenReturn(List.of(mottattDokument));
    }

    @Test
    void skal_mappe_felter_for_brev_med_årsak_fra_behandling() {
        // Arrange
        var behandling = opprettBehandling(Avslagsårsak.ARBEIDSTAKER_KAN_OMPLASSERES);
        var dokumentFelles = DatamapperTestUtil.lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        var dokumentHendelse = DatamapperTestUtil.lagStandardHendelseBuilder().build();

        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getFelles()).isNotNull();
        assertThat(dokumentdata.getFelles().getSøkerNavn()).isEqualTo(DatamapperTestUtil.SØKERS_NAVN);
        assertThat(dokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(DatamapperTestUtil.SØKERS_FNR));
        assertThat(dokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(dokumentdata.getFelles().getHarVerge()).isTrue();
        assertThat(dokumentdata.getFelles().getErKopi()).isTrue();
        assertThat(dokumentdata.getFelles().getSaksnummer()).isEqualTo(DatamapperTestUtil.SAKSNUMMER);
        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo(FagsakYtelseType.SVANGERSKAPSPENGER.getKode());
        assertThat(dokumentdata.getFelles().getErUtkast()).isFalse();

        assertThat(dokumentdata.getÅrsak()).isEqualTo(Årsak.of(Avslagsårsak.ARBEIDSTAKER_KAN_OMPLASSERES.getKode()));
        assertThat(dokumentdata.getMottattDato()).isEqualTo(formaterDato(LocalDate.now(), SPRÅKKODE_NB));
        assertThat(dokumentdata.getAntallArbeidsgivere()).isEqualTo(1);
        assertThat(dokumentdata.getErSøkerDød()).isFalse();
        assertThat(dokumentdata.getHalvG()).isEqualTo(GRUNNBELØP / 2);
        assertThat(dokumentdata.getLovhjemmel()).isEqualTo("§ 14-4");
        assertThat(dokumentdata.getStønadsdatoFom()).isEqualTo(formaterDato(PERIODE1_FOM, SPRÅKKODE_NB));
        assertThat(dokumentdata.getKlagefristUker()).isEqualTo(6);
    }

    @Test
    void skal_mappe_felter_for_brev_med_årsak_fra_uttaket() {
        // Arrange
        var behandling = opprettBehandling(Avslagsårsak.UDEFINERT);
        var dokumentFelles = DatamapperTestUtil.lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        var dokumentHendelse = DatamapperTestUtil.lagStandardHendelseBuilder().build();

        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getFelles()).isNotNull();
        assertThat(dokumentdata.getFelles().getSøkerNavn()).isEqualTo(DatamapperTestUtil.SØKERS_NAVN);
        assertThat(dokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(DatamapperTestUtil.SØKERS_FNR));
        assertThat(dokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(dokumentdata.getFelles().getHarVerge()).isTrue();
        assertThat(dokumentdata.getFelles().getErKopi()).isTrue();
        assertThat(dokumentdata.getFelles().getSaksnummer()).isEqualTo(DatamapperTestUtil.SAKSNUMMER);
        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo(FagsakYtelseType.SVANGERSKAPSPENGER.getKode());
        assertThat(dokumentdata.getFelles().getErUtkast()).isFalse();

        assertThat(dokumentdata.getÅrsak()).isEqualTo(Årsak.of(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT.getKode()));
        assertThat(dokumentdata.getMottattDato()).isEqualTo(formaterDato(LocalDate.now(), SPRÅKKODE_NB));
        assertThat(dokumentdata.getAntallArbeidsgivere()).isEqualTo(1);
        assertThat(dokumentdata.getErSøkerDød()).isFalse();
        assertThat(dokumentdata.getHalvG()).isEqualTo(GRUNNBELØP / 2);
        assertThat(dokumentdata.getLovhjemmel()).isEqualTo("§ 14-4");
        assertThat(dokumentdata.getStønadsdatoFom()).isEqualTo(formaterDato(PERIODE1_FOM, SPRÅKKODE_NB));
        assertThat(dokumentdata.getKlagefristUker()).isEqualTo(6);
    }

    private FamilieHendelse opprettFamiliehendelse() {
        return new FamilieHendelse(List.of(), LocalDate.now(), 1, null);
    }

    private Optional<Beregningsgrunnlag> opprettBeregningsgrunnlag() {
        return Optional.of(Beregningsgrunnlag.ny()
            .leggTilBeregningsgrunnlagAktivitetStatus(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.ARBEIDSTAKER))
            .medGrunnbeløp(new Beløp(BigDecimal.valueOf(GRUNNBELØP)))
            .medhHjemmel(Hjemmel.F_14_7)
            .medBesteberegnet(true)
            .build());
    }

    private Optional<SvangerskapspengerUttak> opprettUttaksresultat() {
        var uttakResultatPeriode1 = SvpUttakResultatPeriode.Builder.ny()
            .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
            .medArbeidsgiverNavn(ARBEIDSGIVER_1)
            .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
            .build();
        var uttakResultatPeriode2 = SvpUttakResultatPeriode.Builder.ny()
            .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
            .medPeriodeIkkeOppfyltÅrsak(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT)
            .medArbeidsgiverNavn(ARBEIDSGIVER_1)
            .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
            .build();

        var uttaksresultat = SvangerskapspengerUttak.Builder.ny()
            .leggTilUttakResultatArbeidsforhold(SvpUttakResultatArbeidsforhold.Builder.ny()
                .medArbeidsgiver(new Arbeidsgiver("1234", ARBEIDSGIVER_1))
                .leggTilPerioder(List.of(uttakResultatPeriode1, uttakResultatPeriode2))
                .build())
            .build();

        return Optional.of(uttaksresultat);
    }

    private Behandling opprettBehandling(Avslagsårsak årsak) {
        return Behandling.builder()
            .medUuid(UUID.randomUUID())
            .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
            .medBehandlingsresultat(
                Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.AVSLÅTT).medAvslagsårsak(årsak).build())
            .medFagsakBackend(FagsakBackend.ny().medFagsakYtelseType(FagsakYtelseType.SVANGERSKAPSPENGER).build())
            .medSpråkkode(Språkkode.NB)
            .medFamilieHendelse(opprettFamiliehendelse())
            .build();
    }
}

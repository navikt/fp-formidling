package no.nav.foreldrepenger.melding.brevmapper.brev;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
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
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelseType;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.SvangerskapspengerAvslagDokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.melding.mottattdokument.MottattDokument;
import no.nav.foreldrepenger.melding.typer.Beløp;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.svp.PeriodeIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatArbeidsforhold;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttaksresultat;
import no.nav.foreldrepenger.melding.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardDokumentData;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardHendelseSVPBuilder;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDato;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    public void before() {
        BrevParametere brevParametere = new BrevParametere(KLAGEFRIST, 2, Period.ZERO, Period.ZERO);
        dokumentData = lagStandardDokumentData(DokumentMalType.SVANGERSKAPSPENGER_AVSLAG);
        dokumentdataMapper = new SvangerskapspengerAvslagDokumentdataMapper(brevParametere, domeneobjektProvider);

        MottattDokument mottattDokument = new MottattDokument(LocalDate.now(), DokumentTypeId.SØKNAD_SVANGERSKAPSPENGER, DokumentKategori.SØKNAD);

        when(domeneobjektProvider.hentFamiliehendelse(any(Behandling.class))).thenReturn(opprettFamiliehendelse());
        when(domeneobjektProvider.hentBeregningsgrunnlagHvisFinnes(any(Behandling.class))).thenReturn(opprettBeregningsgrunnlag());
        when(domeneobjektProvider.hentUttaksresultatSvpHvisFinnes(any(Behandling.class))).thenReturn(opprettUttaksresultat(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT));
        when(domeneobjektProvider.hentMottatteDokumenter(any(Behandling.class))).thenReturn(List.of(mottattDokument));
    }

    @Test
    public void skal_mappe_felter_for_brev_med_årsak_fra_behandling() {
        // Arrange
        Behandling behandling = opprettBehandling(Avslagsårsak.ARBEIDSTAKER_KAN_OMPLASSERES);
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        DokumentHendelse dokumentHendelse = lagStandardHendelseSVPBuilder().build();

        SvangerskapspengerAvslagDokumentdata dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getFelles()).isNotNull();
        assertThat(dokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(dokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(dokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(dokumentdata.getFelles().getHarVerge()).isEqualTo(true);
        assertThat(dokumentdata.getFelles().getErKopi()).isEqualTo(true);
        assertThat(dokumentdata.getFelles().getSaksnummer()).isEqualTo(SAKSNUMMER);
        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo(FagsakYtelseType.SVANGERSKAPSPENGER.getKode());
        assertThat(dokumentdata.getFelles().getBehandlesAvKA()).isEqualTo(false);
        assertThat(dokumentdata.getFelles().getErUtkast()).isEqualTo(false);

        assertThat(dokumentdata.getÅrsak()).isEqualTo(Årsak.of(Avslagsårsak.ARBEIDSTAKER_KAN_OMPLASSERES.getKode()));
        assertThat(dokumentdata.getMottattDato()).isEqualTo(formaterDato(LocalDate.now(), SPRÅKKODE_NB));
        assertThat(dokumentdata.getAntallArbeidsgivere()).isEqualTo(1);
        assertThat(dokumentdata.getErSøkerDød()).isFalse();
        assertThat(dokumentdata.getHalvG()).isEqualTo(GRUNNBELØP/2);
        assertThat(dokumentdata.getLovhjemmel()).isEqualTo("§ 14-4");
        assertThat(dokumentdata.getStønadsdatoFom()).isEqualTo(formaterDato(PERIODE1_FOM, SPRÅKKODE_NB));
        assertThat(dokumentdata.getKlagefristUker()).isEqualTo(6);
    }

    @Test
    public void skal_mappe_felter_for_brev_med_årsak_fra_uttaket() {
        // Arrange
        Behandling behandling = opprettBehandling(Avslagsårsak.UDEFINERT);
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        DokumentHendelse dokumentHendelse = lagStandardHendelseSVPBuilder().build();

        SvangerskapspengerAvslagDokumentdata dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getFelles()).isNotNull();
        assertThat(dokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(dokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(dokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(dokumentdata.getFelles().getHarVerge()).isEqualTo(true);
        assertThat(dokumentdata.getFelles().getErKopi()).isEqualTo(true);
        assertThat(dokumentdata.getFelles().getSaksnummer()).isEqualTo(SAKSNUMMER);
        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo(FagsakYtelseType.SVANGERSKAPSPENGER.getKode());
        assertThat(dokumentdata.getFelles().getBehandlesAvKA()).isEqualTo(false);
        assertThat(dokumentdata.getFelles().getErUtkast()).isEqualTo(false);

        assertThat(dokumentdata.getÅrsak()).isEqualTo(Årsak.of(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT.getKode()));
        assertThat(dokumentdata.getMottattDato()).isEqualTo(formaterDato(LocalDate.now(), SPRÅKKODE_NB));
        assertThat(dokumentdata.getAntallArbeidsgivere()).isEqualTo(1);
        assertThat(dokumentdata.getErSøkerDød()).isFalse();
        assertThat(dokumentdata.getHalvG()).isEqualTo(GRUNNBELØP/2);
        assertThat(dokumentdata.getLovhjemmel()).isEqualTo("§ 14-4");
        assertThat(dokumentdata.getStønadsdatoFom()).isEqualTo(formaterDato(PERIODE1_FOM, SPRÅKKODE_NB));
        assertThat(dokumentdata.getKlagefristUker()).isEqualTo(6);
    }

    private FamilieHendelse opprettFamiliehendelse() {
        FamilieHendelse.OptionalDatoer optionalDatoer = new FamilieHendelse.OptionalDatoer(Optional.empty(), Optional.of(LocalDate.now()), Optional.of(LocalDate.now()), Optional.empty());
        return new FamilieHendelse(BigInteger.valueOf(2), 0, false, true, FamilieHendelseType.TERMIN, optionalDatoer);
    }

    private Optional<Beregningsgrunnlag> opprettBeregningsgrunnlag() {
        return Optional.of(Beregningsgrunnlag.ny()
                .leggTilBeregningsgrunnlagAktivitetStatus(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.ARBEIDSTAKER))
                .medGrunnbeløp(new Beløp(BigDecimal.valueOf(GRUNNBELØP)))
                .medhHjemmel(Hjemmel.F_14_7)
                .medBesteberegnet(true)
                .build());
    }

    private Optional<SvpUttaksresultat> opprettUttaksresultat(PeriodeIkkeOppfyltÅrsak periodeIkkeOppfyltÅrsak) {
        SvpUttakResultatPeriode uttakResultatPeriode1 = SvpUttakResultatPeriode.Builder.ny()
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .medArbeidsgiverNavn(ARBEIDSGIVER_1)
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
                .build();
        SvpUttakResultatPeriode uttakResultatPeriode2 = SvpUttakResultatPeriode.Builder.ny()
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .medPeriodeIkkeOppfyltÅrsak(periodeIkkeOppfyltÅrsak)
                .medArbeidsgiverNavn(ARBEIDSGIVER_1)
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
                .build();

        SvpUttaksresultat uttaksresultat = SvpUttaksresultat.Builder.ny()
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
                .medBehandlingsresultat(Behandlingsresultat.builder()
                        .medBehandlingResultatType(BehandlingResultatType.AVSLÅTT)
                        .medAvslagsårsak(årsak)
                        .build())
                .medSpråkkode(Språkkode.NB)
                .build();
    }
}
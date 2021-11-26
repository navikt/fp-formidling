package no.nav.foreldrepenger.melding.brevmapper.brev.opphørsvp;

import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardDokumentData;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardHendelseSVPBuilder;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDato;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDatoNorsk;
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
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagAktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Hjemmel;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelseType;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.opphørsvp.SvangerskapspengerOpphørDokumentdata;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.melding.typer.Beløp;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.svp.PeriodeIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatArbeidsforhold;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttaksresultat;
import no.nav.foreldrepenger.melding.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;

@ExtendWith(MockitoExtension.class)
class SvangerskapspengerOpphørDokumentdataMapperTest {
    private static final int KLAGEFRIST = 6;
    private static final int GRUNNBELØP = 100000;
    private static final LocalDate PERIODE1_FOM = LocalDate.now().plusDays(2);
    private static final LocalDate PERIODE1_TOM = LocalDate.now().plusDays(3);
    private static final String ARBEIDSGIVER_1 = "Arbeidsgiver_1";

    @Mock
    private DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);

    private DokumentData dokumentData;

    private SvangerskapspengerOpphørDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    public void before() {
        BrevParametere brevParametere = new BrevParametere(KLAGEFRIST, 2, Period.ZERO, Period.ZERO);
        dokumentData = lagStandardDokumentData(DokumentMalType.SVANGERSKAPSPENGER_OPPHØR);
        dokumentdataMapper = new SvangerskapspengerOpphørDokumentdataMapper(brevParametere, domeneobjektProvider);

        when(domeneobjektProvider.hentFamiliehendelse(any(Behandling.class))).thenReturn(opprettFamiliehendelse());
        when(domeneobjektProvider.hentBeregningsgrunnlagHvisFinnes(any(Behandling.class))).thenReturn(opprettBeregningsgrunnlag());
        when(domeneobjektProvider.hentUttaksresultatSvpHvisFinnes(any(Behandling.class))).thenReturn(opprettUttaksresultat());
    }

    @Test
    public void skal_mappe_felter_for_brev() {
        // Arrange
        Behandling behandling = opprettBehandling();
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        DokumentHendelse dokumentHendelse = lagStandardHendelseSVPBuilder().build();

        // Act
        SvangerskapspengerOpphørDokumentdata dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getFelles()).isNotNull();
        assertThat(dokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(dokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(dokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(dokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoNorsk(LocalDate.now()));
        assertThat(dokumentdata.getFelles().getHarVerge()).isEqualTo(true);
        assertThat(dokumentdata.getFelles().getErKopi()).isEqualTo(true);
        assertThat(dokumentdata.getFelles().getSaksnummer()).isEqualTo(SAKSNUMMER);
        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo(FagsakYtelseType.SVANGERSKAPSPENGER.getKode());
        assertThat(dokumentdata.getFelles().getBehandlesAvKA()).isEqualTo(false);
        assertThat(dokumentdata.getFelles().getErUtkast()).isEqualTo(false);

        assertThat(dokumentdata.getOpphørsdato()).isEqualTo(formaterDato(PERIODE1_FOM, Språkkode.NB));
        assertThat(dokumentdata.getDødsdatoBarn()).isEqualTo(null);
        assertThat(dokumentdata.getFødselsdato()).isEqualTo(formaterDato(LocalDate.now(), Språkkode.NB));
        assertThat(dokumentdata.getErSøkerDød()).isFalse();
        assertThat(dokumentdata.getHalvG()).isEqualTo(GRUNNBELØP/2);
        assertThat(dokumentdata.getLovhjemmel()).isEqualTo("§ 14-4 og forvaltningsloven § 35");
        assertThat(dokumentdata.getOpphørtPeriode().getÅrsak()).isEqualTo(Årsak.of(PeriodeIkkeOppfyltÅrsak._8309.getKode()));
        assertThat(dokumentdata.getOpphørtPeriode().getStønadsperiodeFom()).isEqualTo(formaterDato(PERIODE1_FOM, Språkkode.NB));
        assertThat(dokumentdata.getOpphørtPeriode().getStønadsperiodeTom()).isEqualTo(formaterDato(PERIODE1_TOM, Språkkode.NB));
        assertThat(dokumentdata.getOpphørtPeriode().getAntallArbeidsgivere()).isEqualTo(1);


    }

    private FamilieHendelse opprettFamiliehendelse() {
        FamilieHendelse.OptionalDatoer optionalDatoer = new FamilieHendelse.OptionalDatoer(Optional.empty(), Optional.of(LocalDate.now()), Optional.of(LocalDate.now()), Optional.empty());
        return new FamilieHendelse(BigInteger.valueOf(2), false, true, FamilieHendelseType.TERMIN, optionalDatoer);
    }

    private Optional<Beregningsgrunnlag> opprettBeregningsgrunnlag() {
        return Optional.of(Beregningsgrunnlag.ny()
                .leggTilBeregningsgrunnlagAktivitetStatus(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.ARBEIDSTAKER))
                .medGrunnbeløp(new Beløp(BigDecimal.valueOf(GRUNNBELØP)))
                .medhHjemmel(Hjemmel.F_14_7)
                .medBesteberegnet(true)
                .build());
    }

    private Optional<SvpUttaksresultat> opprettUttaksresultat() {
        SvpUttakResultatPeriode uttakResultatPeriode1 = SvpUttakResultatPeriode.Builder.ny()
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .medArbeidsgiverNavn(ARBEIDSGIVER_1)
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(PERIODE1_FOM, PERIODE1_TOM))
                .build();
        SvpUttakResultatPeriode uttakResultatPeriode2 = SvpUttakResultatPeriode.Builder.ny()
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .medPeriodeIkkeOppfyltÅrsak(PeriodeIkkeOppfyltÅrsak._8309)
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

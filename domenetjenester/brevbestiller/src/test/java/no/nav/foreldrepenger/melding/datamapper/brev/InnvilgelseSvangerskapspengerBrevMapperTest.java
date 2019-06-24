package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.domene.SvpMapper.mapFra;
import static no.nav.foreldrepenger.melding.datamapper.mal.fritekst.BrevmalKilder.ROTMAPPE;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingResultatType;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatAndel;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BGAndelArbeidsforhold;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagAktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Hjemmel;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.fritekstbrev.FagType;
import no.nav.foreldrepenger.melding.typer.Beløp;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.svp.PeriodeIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatArbeidsforhold;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatPerioder;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttaksresultat;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;

public class InnvilgelseSvangerskapspengerBrevMapperTest {
    private static final long ID = 123L;
    private DokumentHendelse dokumentHendelse;
    private Behandling behandling;
    private Arbeidsgiver arbeidsgiver = new Arbeidsgiver("Tine", null, null);
    private DatoIntervall datoIntervallPeriode1 = DatoIntervall.fraOgMedTilOgMed(LocalDate.now(), LocalDate.now().plusMonths(1));
    private DatoIntervall datoIntervallPeriode2 = DatoIntervall.fraOgMedTilOgMed(LocalDate.now().plusMonths(1).plusDays(1), LocalDate.now().plusMonths(2));
    private DatoIntervall datoIntervallPeriode3 = DatoIntervall.fraOgMed(LocalDate.now().plusMonths(2).plusDays(1));

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().silent();
    @Mock
    DokumentFelles dokumentFelles;
    @Mock
    FellesType fellesType;
    @Mock
    BrevParametere brevParametere;

    @InjectMocks
    private InnvilgelseSvangerskapspengerBrevMapper mapper;

    @Before
    public void setup() {
        behandling = Behandling.builder().medId(ID)
                .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medBehandlendeEnhetNavn(HenleggBehandlingBrevMapper.FAMPEN)
                .medBehandlingsresultat(Behandlingsresultat.builder()
                        .medBehandlingResultatType(BehandlingResultatType.INNVILGET)
                        .build())
                .medSpråkkode(Språkkode.nb)
                .build();
        dokumentHendelse = DokumentHendelse.builder()
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .medYtelseType(FagsakYtelseType.SVANGERSKAPSPENGER)
                .build();
        SvpUttaksresultat uttakResultat = mockUttaksresultat();
        BeregningsresultatFP beregningsresultat = mockBeregningsresultat();


        mapper = new InnvilgelseSvangerskapspengerBrevMapper() {
            @Override
            Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling) {
                return new Brevdata()
                        .leggTilAlle(mapFra(uttakResultat, dokumentHendelse, mockBeregningsgrunnlag(), beregningsresultat, behandling))
                        .leggTil("manedsbelop", 25342L)
                        .leggTil("mottattDato", "1. januar 2000")
                        .leggTil("refusjonTilBruker", true)
                        .leggTil("refusjonerTilArbeidsgivere", 2)
                        .leggTil("erAutomatiskVedtak", true);
            }
        };
        MockitoAnnotations.initMocks(this);
    }

    @Ignore
    @Test
    public void test_map_fagtype() {
        ResourceBundle expectedValues = ResourceBundle.getBundle(
                String.join("/", ROTMAPPE, mapper.templateFolder(), "expected"),
                new Locale("nb", "NO"));

        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(expectedValues.getString("brødtekst"));
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(expectedValues.getString("overskrift"));
    }

    private BeregningsresultatFP mockBeregningsresultat() {
        return BeregningsresultatFP.ny()
                .leggTilBeregningsresultatPerioder(List.of(BeregningsresultatPeriode.ny()
                                .medPeriode(datoIntervallPeriode1)
                        .medBeregningsresultatAndel(List.of(BeregningsresultatAndel.ny()
                                .medArbeidsgiver(arbeidsgiver)
                                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                .medDagsats(2452)
                                .medBrukerErMottaker(true)
                                .build()))
                        .medDagsats(2452L)
                        .build(),
                        BeregningsresultatPeriode.ny()
                                .medPeriode(datoIntervallPeriode2)
                                .medBeregningsresultatAndel(List.of(BeregningsresultatAndel.ny()
                                        .medArbeidsgiver(arbeidsgiver)
                                        .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                        .medDagsats(2452)
                                        .medBrukerErMottaker(true)
                                        .build()))
                                .medDagsats(2452L)
                                .build(),
                        BeregningsresultatPeriode.ny()
                                .medPeriode(datoIntervallPeriode3)
                                .medBeregningsresultatAndel(List.of(BeregningsresultatAndel.ny()
                                        .medArbeidsgiver(arbeidsgiver)
                                        .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                        .medDagsats(2452)
                                        .medBrukerErMottaker(true)
                                        .build()))
                                .medDagsats(2452L)
                                .build()
                        ))
                .build();
    }

    private Beregningsgrunnlag mockBeregningsgrunnlag() {
        List<BeregningsgrunnlagPrStatusOgAndel> bgpsaList = List.of(BeregningsgrunnlagPrStatusOgAndel.ny()
                        .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                        .medBgAndelArbeidsforhold(new BGAndelArbeidsforhold(arbeidsgiver, null, null, null, null))
                        .medBruttoPrÅr(BigDecimal.valueOf(22431 * 12))
                        .medBesteberegningPrÅr(BigDecimal.TEN)
                        .build(),
                BeregningsgrunnlagPrStatusOgAndel.ny()
                        .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                        .medBgAndelArbeidsforhold(new BGAndelArbeidsforhold(new Arbeidsgiver("Forsvaret", null, null), null, null, null, null))
                        .medBruttoPrÅr(BigDecimal.valueOf(12431 * 12))
                        .medBesteberegningPrÅr(BigDecimal.TEN)
                        .build()
        );
        BeregningsgrunnlagPeriode periode = BeregningsgrunnlagPeriode.ny()
                .medBruttoPrÅr(BigDecimal.TEN)
                .medPeriode(datoIntervallPeriode1)
                .medBeregningsgrunnlagPrStatusOgAndelList(bgpsaList)
                .build();
        BeregningsgrunnlagPeriode periode2 = BeregningsgrunnlagPeriode.ny()
                .medBruttoPrÅr(BigDecimal.TEN)
                .medPeriode(datoIntervallPeriode2)
                .medBeregningsgrunnlagPrStatusOgAndelList(bgpsaList)
                .build();
        BeregningsgrunnlagPeriode periode3 = BeregningsgrunnlagPeriode.ny()
                .medBruttoPrÅr(BigDecimal.TEN)
                .medPeriode(datoIntervallPeriode3)
                .medBeregningsgrunnlagPrStatusOgAndelList(bgpsaList)
                .build();

        return Beregningsgrunnlag.ny()
                .leggTilBeregningsgrunnlagPeriode(periode)
                .leggTilBeregningsgrunnlagPeriode(periode2)
                .leggTilBeregningsgrunnlagPeriode(periode3)
                .leggTilBeregningsgrunnlagAktivitetStatus(new BeregningsgrunnlagAktivitetStatus(AktivitetStatus.ARBEIDSTAKER))
                .medhHjemmel(Hjemmel.F_14_7)
                .medGrunnbeløp(Beløp.ZERO)
                .build();
    }

    private SvpUttaksresultat mockUttaksresultat() {
        SvpUttakResultatPeriode innvilgetPeriode = SvpUttakResultatPeriode.ny()
                .medTidsperiode(datoIntervallPeriode1)
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .medUtbetalingsgrad(50L)
                .medAktivitetDagsats(2452).build();
        SvpUttakResultatPeriode innvilgetPeriode2 = SvpUttakResultatPeriode.ny()
                .medTidsperiode(datoIntervallPeriode2)
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .medUtbetalingsgrad(50L)
                .medAktivitetDagsats(2452).build();
        SvpUttakResultatPeriode innvilgetPeriode3 = SvpUttakResultatPeriode.ny()
                .medTidsperiode(datoIntervallPeriode3)
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .medUtbetalingsgrad(50L)
                .medAktivitetDagsats(2452).build();
        SvpUttakResultatPeriode avslåttPeriode = SvpUttakResultatPeriode.ny()
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(LocalDate.now(), LocalDate.now().plusMonths(1)))
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .medPeriodeIkkeOppfyltÅrsak(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT)
                .medAktivitetDagsats(3126).build();
        SvpUttakResultatPeriode avslåttPeriode2 = SvpUttakResultatPeriode.ny()
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(LocalDate.now(), LocalDate.now().plusMonths(1)))
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .medUtbetalingsgrad(50L)
                .medPeriodeIkkeOppfyltÅrsak(PeriodeIkkeOppfyltÅrsak.SØKT_FOR_SENT)
                .medAktivitetDagsats(312).build();
        SvpUttakResultatPerioder resArbeidsforhold = SvpUttakResultatPerioder.ny()
                .medArbeidsgiverNavn("Tine")
                .medPeriode(innvilgetPeriode).build();

        return SvpUttaksresultat.ny()
                .medUttakResultatArbeidsforhold(SvpUttakResultatArbeidsforhold.ny()
                        .medArbeidsgiver(arbeidsgiver)
                        .medPeriode(innvilgetPeriode)
                        .medPeriode(innvilgetPeriode2)
                        .medPeriode(innvilgetPeriode3)
                        .medPeriode(avslåttPeriode)
                        .medPeriode(avslåttPeriode2)
                        .medPeriode(avslåttPeriode)
                        .build())
                .medUttakResultatPerioder(resArbeidsforhold)
                .build();
    }
}

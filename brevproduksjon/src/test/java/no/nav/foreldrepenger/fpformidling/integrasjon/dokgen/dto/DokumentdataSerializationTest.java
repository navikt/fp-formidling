package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.fpformidling.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.fpformidling.behandling.RevurderingVarslingÅrsak;
import no.nav.foreldrepenger.fpformidling.behandling.innsyn.InnsynResultatType;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.fpformidling.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Beløp;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FellesDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Prosent;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.AnnenAktivitet;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Arbeidsforhold;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.BeregningsgrunnlagAndel;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.BeregningsgrunnlagRegel;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.ForMyeUtbetalt;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.ForeldrepengerInnvilgelseDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.NaturalytelseEndringType;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Næring;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Utbetalingsperiode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.VurderingsKode;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingÅrsakType;
import no.nav.foreldrepenger.fpformidling.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.fpformidling.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.fpformidling.vilkår.VilkårType;
import no.nav.vedtak.mapper.json.DefaultJsonMapper;

class DokumentdataSerializationTest {

    private static final ObjectMapper OBJECT_MAPPER = DefaultJsonMapper.getObjectMapper();

    @Test
    void skal_serialisere_og_deserialisere_dokumentdata_for_innvilgelse_foreldrepenger() throws IOException {
        // Arrange
        var arbeidsforhold1 = Arbeidsforhold.ny()
            .medArbeidsgiverNavn("Arbeidsgiver 1")
            .medGradering(true)
            .medProsentArbeid(Prosent.HUNDRE)
            .medStillingsprosent(Prosent.HUNDRE)
            .medUtbetalingsgrad(Prosent.HUNDRE)
            .medNaturalytelseEndringType(NaturalytelseEndringType.INGEN_ENDRING)
            .medNaturalytelseEndringDato(formaterDatoNorsk(LocalDate.now().minusDays(5)))
            .medBruttoInkludertBortfaltNaturalytelsePrAar(48200)
            .build();
        var arbeidsforhold2 = Arbeidsforhold.ny()
            .medArbeidsgiverNavn("Arbeidsgiver 2")
            .medGradering(true)
            .medProsentArbeid(Prosent.of(BigDecimal.TEN))
            .medStillingsprosent(Prosent.of(BigDecimal.valueOf(20)))
            .medUtbetalingsgrad(Prosent.of(BigDecimal.valueOf(30.55)))
            .medNaturalytelseEndringType(NaturalytelseEndringType.START)
            .medNaturalytelseEndringDato(formaterDatoNorsk(LocalDate.now().minusDays(50)))
            .medBruttoInkludertBortfaltNaturalytelsePrAar(52200)
            .build();
        var næring = Næring.ny()
            .medGradering(true)
            .medUtbetalingsgrad(Prosent.of(BigDecimal.valueOf(60)))
            .medProsentArbeid(Prosent.of(BigDecimal.valueOf(70)))
            .build();
        var annenAktivitet = AnnenAktivitet.ny()
            .medAktivitetStatus(AktivitetStatus.KOMBINERT_AT_FL.name())
            .medGradering(true)
            .medUtbetalingsgrad(Prosent.of(BigDecimal.valueOf(20)))
            .medProsentArbeid(Prosent.of(BigDecimal.valueOf(30)))
            .build();
        var periode1 = Utbetalingsperiode.ny()
            .medInnvilget(true)
            .medÅrsak(Årsak.of("2001"))
            .medPeriodeFom(LocalDate.now().minusDays(10), Språkkode.NB)
            .medPeriodeTom(LocalDate.now().minusDays(8), Språkkode.NB)
            .medPeriodeDagsats(123L)
            .medAntallTapteDager(10, BigDecimal.ZERO)
            .medPrioritertUtbetalingsgrad(Prosent.HUNDRE)
            .medArbeidsforhold(of(arbeidsforhold1, arbeidsforhold2))
            .medNæring(næring)
            .medAnnenAktivitet(List.of(annenAktivitet))
            .build();
        var periode2 = Utbetalingsperiode.ny()
            .medInnvilget(false)
            .medÅrsak(Årsak.of("2002"))
            .medPeriodeFom(LocalDate.now().minusDays(7), Språkkode.NB)
            .medPeriodeTom(LocalDate.now().minusDays(5), Språkkode.NB)
            .medPeriodeDagsats(234L)
            .medAntallTapteDager(11, BigDecimal.ZERO)
            .medPrioritertUtbetalingsgrad(Prosent.of(BigDecimal.valueOf(80)))
            .medArbeidsforhold(List.of(arbeidsforhold1))
            .medNæring(næring)
            .medAnnenAktivitet(List.of(annenAktivitet))
            .build();
        var andel1 = BeregningsgrunnlagAndel.ny()
            .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER.name())
            .medArbeidsgiverNavn("Arbeidsgiver 1")
            .medDagsats(400)
            .medMånedsinntekt(5000)
            .medÅrsinntekt(500000)
            .medEtterlønnSluttpakke(true)
            .medSistLignedeÅr(2019)
            .build();
        var andel2 = BeregningsgrunnlagAndel.ny()
            .medAktivitetStatus(AktivitetStatus.KOMBINERT_AT_FL.name())
            .medArbeidsgiverNavn("Arbeidsgiver 2")
            .medDagsats(200)
            .medMånedsinntekt(1000)
            .medÅrsinntekt(110000)
            .medEtterlønnSluttpakke(true)
            .medSistLignedeÅr(2020)
            .build();
        var regel1 = BeregningsgrunnlagRegel.ny()
            .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER.name())
            .medAntallArbeidsgivereIBeregningUtenEtterlønnSluttpakke(10)
            .medSnNyoppstartet(true)
            .medAndelListe(of(andel1, andel2))
            .build();
        var regel2 = BeregningsgrunnlagRegel.ny()
            .medAktivitetStatus(AktivitetStatus.KOMBINERT_AT_FL.name())
            .medAntallArbeidsgivereIBeregningUtenEtterlønnSluttpakke(3)
            .medSnNyoppstartet(true)
            .medAndelListe(List.of(andel2))
            .build();
        var dokumentdata = ForeldrepengerInnvilgelseDokumentdata.ny()
            .medFelles(opprettFellesDokumentdata())
            .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD.name())
            .medBehandlingResultatType(BehandlingResultatType.INNVILGET.name())
            .medKonsekvensForInnvilgetYtelse(KonsekvensForYtelsen.ENDRING_I_BEREGNING_OG_UTTAK.name())
            .medDekningsgrad(80)
            .medDagsats(100)
            .medMånedsbeløp(200)
            .medSekgG(600000)
            .medInntektOverSekgG(true)
            .medForMyeUtbetalt(ForMyeUtbetalt.GENERELL)
            .medInntektMottattArbeidsgiver(true)
            .medAnnenForelderHarRett(true)
            .medAnnenForelderHarRettVurdert(VurderingsKode.JA)
            .medAleneomsorgKode(VurderingsKode.JA)
            .medIkkeOmsorg(true)
            .medBarnErFødt(true)
            .medÅrsakErFødselshendelse(true)
            .medGjelderMor(true)
            .medGjelderFødsel(true)
            .medErBesteberegning(true)
            .medSeksAvDeTiBeste(true)
            .medIngenRefusjon(true)
            .medDelvisRefusjon(true)
            .medFullRefusjon(true)
            .medFbEllerRvInnvilget(true)
            .medAntallPerioder(3)
            .medAntallInnvilgedePerioder(1)
            .medAntallAvslåttePerioder(2)
            .medAntallArbeidsgivere(3)
            .medDagerTaptFørTermin(4)
            .medDisponibleDager(5)
            .medDisponibleDagerUtenAktivitetskrav(11)
            .medDisponibleDagerMedAktivitetskrav(15)
            .medDisponibleFellesDager(6)
            .medSisteDagAvSistePeriode(formaterDatoNorsk(LocalDate.now().minusDays(10)))
            .medStønadsperiodeFom(formaterDatoNorsk(LocalDate.now().minusDays(9)))
            .medStønadsperiodeTom(formaterDatoNorsk(LocalDate.now().minusDays(8)))
            .medForeldrepengeperiodenUtvidetUker(7)
            .medAntallBarn(8)
            .medPrematurDager(9)
            .medUtbetalingsperioder(of(periode1, periode2))
            .medBruttoBeregningsgrunnlag(Beløp.of(300L))
            .medHarBruktBruttoBeregningsgrunnlag(true)
            .medBeregningsgrunnlagregler(of(regel1, regel2))
            .build();

        // Act + Assert
        assertEquals(dokumentdata, utførTest(dokumentdata));
    }

    @Test
    void skal_serialisere_og_deserialisere_dokumentdata_for_avslag_engangsstønad() throws IOException {
        // Arrange
        var dokumentdata = EngangsstønadAvslagDokumentdata.ny()
            .medFelles(opprettFellesDokumentdata())
            .medAvslagsÅrsak(Avslagsårsak.SØKER_ER_IKKE_MEDLEM.getKode())
            .medFørstegangsbehandling(true)
            .medAntallBarn(2)
            .medRelasjonsRolle(RelasjonsRolleType.MORA.getKode())
            .medGjelderFødsel(true)
            .medVilkårTyper(of(VilkårType.FØDSELSVILKÅRET_MOR.getKode(), VilkårType.MEDLEMSKAPSVILKÅRET.getKode()))
            .medKlagefristUker(6)
            .medAvslagMedlemskap("IKKE_MEDL_FØR_STP")
            .build();

        // Act + Assert
        assertEquals(dokumentdata, utførTest(dokumentdata));
    }

    @Test
    void skal_serialisere_og_deserialisere_dokumentdata_for_innvilgelse_engangsstønad() throws IOException {
        // Arrange
        var dokumentdata = EngangsstønadInnvilgelseDokumentdata.ny()
            .medFelles(opprettFellesDokumentdata())
            .medRevurdering(true)
            .medFørstegangsbehandling(true)
            .medMedhold(true)
            .medInnvilgetBeløp("200")
            .medKlagefristUker(4)
            .medDød(true)
            .medFbEllerMedhold(true)
            .medErEndretSats(true)
            .build();

        // Act + Assert
        assertEquals(dokumentdata, utførTest(dokumentdata));
    }

    @Test
    void skal_serialisere_og_deserialisere_dokumentdata_for_forlenget_saksbehandlingstid() throws IOException {
        // Arrange
        var dokumentdata = ForlengetSaksbehandlingstidDokumentdata.ny()
            .medFelles(opprettFellesDokumentdata())
            .medVariantType(ForlengetSaksbehandlingstidDokumentdata.VariantType.FORLENGET)
            .medDød(true)
            .medBehandlingsfristUker(6)
            .medAntallBarn(2)
            .build();

        // Act + Assert
        assertEquals(dokumentdata, utførTest(dokumentdata));
    }

    @Test
    void skal_serialisere_og_deserialisere_dokumentdata_for_henleggelse() throws IOException {
        // Arrange
        var dokumentdata = HenleggelseDokumentdata.ny()
            .medFelles(opprettFellesDokumentdata())
            .medVanligBehandling(true)
            .medKlage(true)
            .medAnke(true)
            .medInnsyn(true)
            .medOpphavType("FAMPEN")
            .build();

        // Act + Assert
        assertEquals(dokumentdata, utførTest(dokumentdata));
    }

    @Test
    void skal_serialisere_og_deserialisere_dokumentdata_for_ikke_søkt() throws IOException {
        // Arrange
        var dokumentdata = IkkeSøktDokumentdata.ny()
            .medFelles(opprettFellesDokumentdata())
            .medArbeidsgiverNavn("Arbeidsgiver1")
            .medMottattDato(formaterDatoNorsk(LocalDate.now()))
            .build();

        // Act + Assert
        assertEquals(dokumentdata, utførTest(dokumentdata));
    }

    @Test
    void skal_serialisere_og_deserialisere_dokumentdata_for_info_til_annen_forelder() throws IOException {
        // Arrange
        var dokumentdata = ForeldrepengerInfoTilAnnenForelderDokumentdata.ny()
            .medFelles(opprettFellesDokumentdata())
            .medBehandlingÅrsak(BehandlingÅrsakType.RE_ENDRET_INNTEKTSMELDING.getKode())
            .medSisteUttaksdagMor(formaterDatoNorsk(LocalDate.now()))
            .build();

        // Act + Assert
        assertEquals(dokumentdata, utførTest(dokumentdata));
    }

    @Test
    void skal_serialisere_og_deserialisere_dokumentdata_for_innhente_opplysninger() throws IOException {
        // Arrange
        var dokumentdata = InnhenteOpplysningerDokumentdata.ny()
            .medFelles(opprettFellesDokumentdata())
            .medFørstegangsbehandling(true)
            .medRevurdering(true)
            .medEndringssøknad(true)
            .medDød(true)
            .medKlage(true)
            .medSøknadDato(formaterDatoNorsk(LocalDate.now()))
            .medFristDato(formaterDatoNorsk(LocalDate.now().plusDays(10)))
            .build();

        // Act + Assert
        assertEquals(dokumentdata, utførTest(dokumentdata));
    }

    @Test
    void skal_serialisere_og_deserialisere_dokumentdata_for_innsyn() throws IOException {
        // Arrange
        var dokumentdata = InnsynDokumentdata.ny()
            .medFelles(opprettFellesDokumentdata())
            .medInnsynResultat(InnsynResultatType.INNVILGET.getKode())
            .medKlagefrist(6)
            .build();

        // Act + Assert
        assertEquals(dokumentdata, utførTest(dokumentdata));
    }

    @Test
    void skal_serialisere_og_deserialisere_dokumentdata_for_varsel_om_revurdering() throws IOException {
        // Arrange
        var dokumentdata = VarselOmRevurderingDokumentdata.ny()
            .medFelles(opprettFellesDokumentdata())
            .medTerminDato(formaterDatoNorsk(LocalDate.now().minusDays(10)))
            .medFristDato(formaterDatoNorsk(LocalDate.now()))
            .medAntallBarn(2)
            .medAdvarselKode(RevurderingVarslingÅrsak.ARBEID_I_UTLANDET.getKode())
            .medFlereOpplysninger(true)
            .build();

        // Act + Assert
        assertEquals(dokumentdata, utførTest(dokumentdata));
    }

    private Object utførTest(Object object) throws IOException {
        return fraJson(tilJson(object), object.getClass());
    }

    private String tilJson(Object obj) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(obj);
    }

    private Object fraJson(String json, Class clazz) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(json, clazz);
    }

    private FellesDokumentdata opprettFellesDokumentdata() {
        return FellesDokumentdata.ny()
            .medSøkerNavn("Søker Søkersen")
            .medSøkerPersonnummer("11111111111")
            .medFritekst(FritekstDto.fra("Fritekst"))
            .medBrevDato(formaterDatoNorsk(LocalDate.now()))
            .medErAutomatiskBehandlet(true)
            .medErKopi(true)
            .medHarVerge(true)
            .medSaksnummer("123456789")
            .medMottakerNavn("Mottaker Mottakersen")
            .medYtelseType(FagsakYtelseType.FORELDREPENGER.getKode())
            .build();
    }
}

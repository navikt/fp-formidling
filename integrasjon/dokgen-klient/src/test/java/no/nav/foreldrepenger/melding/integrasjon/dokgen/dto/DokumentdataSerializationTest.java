package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto;

import static java.util.List.of;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDatoNorsk;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.felles.integrasjon.rest.DefaultJsonMapper;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.melding.behandling.RevurderingVarslingÅrsak;
import no.nav.foreldrepenger.melding.behandling.innsyn.InnsynResultatType;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.AnnenAktivitet;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.Arbeidsforhold;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.BeregningsgrunnlagAndel;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.BeregningsgrunnlagRegel;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.ForMyeUtbetalt;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.InnvilgelseForeldrepengerDokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.NaturalytelseEndringType;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.Næring;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.Utbetalingsperiode;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.VurderingsKode;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingÅrsakType;
import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.melding.vilkår.VilkårType;

public class DokumentdataSerializationTest {

    private static final ObjectMapper OBJECT_MAPPER = DefaultJsonMapper.getObjectMapper();

    @Test
    public void skal_serialisere_og_deserialisere_dokumentdata_for_innvilgelse_foreldrepenger() throws IOException {
        // Arrange
        Arbeidsforhold arbeidsforhold1 = Arbeidsforhold.ny()
                .medArbeidsgiverNavn("Arbeidsgiver 1")
                .medGradering(true)
                .medProsentArbeid(BigDecimal.valueOf(100))
                .medStillingsprosent(BigDecimal.valueOf(100))
                .medUtbetalingsgrad(BigDecimal.valueOf(100))
                .medNaturalytelseEndringType(NaturalytelseEndringType.INGEN_ENDRING)
                .medNaturalytelseEndringDato(formaterDatoNorsk(LocalDate.now().minusDays(5)))
                .medNaturalytelseNyDagsats(500)
                .build();
        Arbeidsforhold arbeidsforhold2 = Arbeidsforhold.ny()
                .medArbeidsgiverNavn("Arbeidsgiver 2")
                .medGradering(true)
                .medProsentArbeid(BigDecimal.valueOf(10))
                .medStillingsprosent(BigDecimal.valueOf(20))
                .medUtbetalingsgrad(BigDecimal.valueOf(30.55))
                .medNaturalytelseEndringType(NaturalytelseEndringType.START)
                .medNaturalytelseEndringDato(formaterDatoNorsk(LocalDate.now().minusDays(50)))
                .medNaturalytelseNyDagsats(200)
                .build();
        Næring næring = Næring.ny()
                .medGradering(true)
                .medUtbetalingsgrad(BigDecimal.valueOf(60))
                .medProsentArbeid(BigDecimal.valueOf(70))
                .build();
        AnnenAktivitet annenAktivitet = AnnenAktivitet.ny()
                .medAktivitetStatus(AktivitetStatus.KOMBINERT_AT_FL.name())
                .medGradering(true)
                .medUtbetalingsgrad(BigDecimal.valueOf(20))
                .medProsentArbeid(BigDecimal.valueOf(30))
                .build();
        Utbetalingsperiode periode1 = Utbetalingsperiode.ny()
                .medInnvilget(true)
                .medÅrsak("2001")
                .medPeriodeFom(LocalDate.now().minusDays(10))
                .medPeriodeTom(LocalDate.now().minusDays(8))
                .medPeriodeDagsats(123L)
                .medAntallTapteDager(10)
                .medPrioritertUtbetalingsgrad(BigDecimal.valueOf(100))
                .medArbeidsforhold(of(arbeidsforhold1, arbeidsforhold2))
                .medNæring(næring)
                .medAnnenAktivitet(of(annenAktivitet))
                .build();
        Utbetalingsperiode periode2 = Utbetalingsperiode.ny()
                .medInnvilget(false)
                .medÅrsak("2002")
                .medPeriodeFom(LocalDate.now().minusDays(7))
                .medPeriodeTom(LocalDate.now().minusDays(5))
                .medPeriodeDagsats(234L)
                .medAntallTapteDager(11)
                .medPrioritertUtbetalingsgrad(BigDecimal.valueOf(80))
                .medArbeidsforhold(of(arbeidsforhold1))
                .medNæring(næring)
                .medAnnenAktivitet(of(annenAktivitet))
                .build();
        BeregningsgrunnlagAndel andel1 = BeregningsgrunnlagAndel.ny()
                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER.name())
                .medArbeidsgiverNavn("Arbeidsgiver 1")
                .medDagsats(400)
                .medMånedsinntekt(5000)
                .medÅrsinntekt(500000)
                .medEtterlønnSluttpakke(true)
                .medSistLignedeÅr(2019)
                .build();
        BeregningsgrunnlagAndel andel2 = BeregningsgrunnlagAndel.ny()
                .medAktivitetStatus(AktivitetStatus.KOMBINERT_AT_FL.name())
                .medArbeidsgiverNavn("Arbeidsgiver 2")
                .medDagsats(200)
                .medMånedsinntekt(1000)
                .medÅrsinntekt(110000)
                .medEtterlønnSluttpakke(true)
                .medSistLignedeÅr(2020)
                .build();
        BeregningsgrunnlagRegel regel1 = BeregningsgrunnlagRegel.ny()
                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER.name())
                .medAntallArbeidsgivereIBeregningUtenEtterlønnSluttpakke(10)
                .medSnNyoppstartet(true)
                .medAndelListe(of(andel1, andel2))
                .build();
        BeregningsgrunnlagRegel regel2 = BeregningsgrunnlagRegel.ny()
                .medAktivitetStatus(AktivitetStatus.KOMBINERT_AT_FL.name())
                .medAntallArbeidsgivereIBeregningUtenEtterlønnSluttpakke(3)
                .medSnNyoppstartet(true)
                .medAndelListe(of(andel2))
                .build();
        InnvilgelseForeldrepengerDokumentdata dokumentdata = InnvilgelseForeldrepengerDokumentdata.ny()
                .medFelles(opprettFellesDokumentdata())
                .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD.name())
                .medBehandlingResultatType(BehandlingResultatType.INNVILGET.name())
                .medKonsekvensForInnvilgetYtelse(KonsekvensForYtelsen.ENDRING_I_BEREGNING_OG_UTTAK.name())
                .medSøknadsdato(formaterDatoNorsk(LocalDate.now()))
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
                .medIngenRefusjon(true)
                .medDelvisRefusjon(true)
                .medFullRefusjon(true)
                .medFbEllerRvInnvilget(true)
                .medAntallPerioder(2)
                .medHarInnvilgedePerioder(true)
                .medAntallArbeidsgivere(3)
                .medDagerTaptFørTermin(4)
                .medDisponibleDager(5)
                .medDisponibleFellesDager(6)
                .medSisteDagAvSistePeriode(formaterDatoNorsk(LocalDate.now().minusDays(10)))
                .medStønadsperiodeFom(formaterDatoNorsk(LocalDate.now().minusDays(9)))
                .medStønadsperiodeTom(formaterDatoNorsk(LocalDate.now().minusDays(8)))
                .medForeldrepengeperiodenUtvidetUker(7)
                .medAntallBarn(8)
                .medPrematurDager(9)
                .medUtbetalingsperioder(of(periode1, periode2))
                .medBruttoBeregningsgrunnlag(300)
                .medHarBruktBruttoBeregningsgrunnlag(true)
                .medBeregningsgrunnlagregler(of(regel1, regel2))
                .build();

        // Act + Assert
        assertEquals(dokumentdata, utførTest(dokumentdata));
    }

    @Test
    public void skal_serialisere_og_deserialisere_dokumentdata_for_avslag_engangsstønad() throws IOException {
        // Arrange
        EngangsstønadAvslagDokumentdata dokumentdata = EngangsstønadAvslagDokumentdata.ny()
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
    public void skal_serialisere_og_deserialisere_dokumentdata_for_innvilgelse_engangsstønad() throws IOException {
        // Arrange
        EngangsstønadInnvilgelseDokumentdata dokumentdata = EngangsstønadInnvilgelseDokumentdata.ny()
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
    public void skal_serialisere_og_deserialisere_dokumentdata_for_forlenget_saksbehandlingstid() throws IOException {
        // Arrange
        ForlengetSaksbehandlingstidDokumentdata dokumentdata = ForlengetSaksbehandlingstidDokumentdata.ny()
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
    public void skal_serialisere_og_deserialisere_dokumentdata_for_henleggelse() throws IOException {
        // Arrange
        HenleggelseDokumentdata dokumentdata = HenleggelseDokumentdata.ny()
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
    public void skal_serialisere_og_deserialisere_dokumentdata_for_ikke_søkt() throws IOException {
        // Arrange
        IkkeSøktDokumentdata dokumentdata = IkkeSøktDokumentdata.ny()
                .medFelles(opprettFellesDokumentdata())
                .medArbeidsgiverNavn("Arbeidsgiver1")
                .medMottattDato(formaterDatoNorsk(LocalDate.now()))
                .build();

        // Act + Assert
        assertEquals(dokumentdata, utførTest(dokumentdata));
    }

    @Test
    public void skal_serialisere_og_deserialisere_dokumentdata_for_info_til_annen_forelder() throws IOException {
        // Arrange
        InfoTilAnnenForelderDokumentdata dokumentdata = InfoTilAnnenForelderDokumentdata.ny()
                .medFelles(opprettFellesDokumentdata())
                .medBehandlingÅrsak(BehandlingÅrsakType.RE_ENDRET_INNTEKTSMELDING.getKode())
                .medSisteUttaksdagMor(formaterDatoNorsk(LocalDate.now()))
                .build();

        // Act + Assert
        assertEquals(dokumentdata, utførTest(dokumentdata));
    }

    @Test
    public void skal_serialisere_og_deserialisere_dokumentdata_for_innhente_opplysninger() throws IOException {
        // Arrange
        InnhenteOpplysningerDokumentdata dokumentdata = InnhenteOpplysningerDokumentdata.ny()
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
    public void skal_serialisere_og_deserialisere_dokumentdata_for_innsyn() throws IOException {
        // Arrange
        InnsynDokumentdata dokumentdata = InnsynDokumentdata.ny()
                .medFelles(opprettFellesDokumentdata())
                .medInnsynResultat(InnsynResultatType.INNVILGET.getKode())
                .medKlagefrist(6)
                .build();

        // Act + Assert
        assertEquals(dokumentdata, utførTest(dokumentdata));
    }

    @Test
    public void skal_serialisere_og_deserialisere_dokumentdata_for_varsel_om_revurdering() throws IOException {
        // Arrange
        VarselOmRevurderingDokumentdata dokumentdata = VarselOmRevurderingDokumentdata.ny()
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
                .medFritekst("Fritekst")
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
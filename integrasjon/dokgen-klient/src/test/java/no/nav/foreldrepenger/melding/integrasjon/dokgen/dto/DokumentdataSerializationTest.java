package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto;

import static java.util.List.of;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDatoNorsk;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.foreldrepenger.felles.integrasjon.rest.DefaultJsonMapper;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
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

public class DokumentdataSerializationTest {

    private ObjectMapper mapper = DefaultJsonMapper.getObjectMapper();

    @Test
    public void skal_serialisere_og_deserialisere_dokumentdata_for_innvilgelse_foreldrepenger() throws IOException {
        // Arrange
        Arbeidsforhold arbeidsforhold1 = Arbeidsforhold.ny()
                .medArbeidsgiverNavn("Arbeidsgiver 1")
                .medGradering(true)
                .medProsentArbeid(100)
                .medStillingsprosent(100)
                .medUtbetalingsgrad(100)
                .medNaturalytelseEndringType(NaturalytelseEndringType.INGEN_ENDRING)
                .medNaturalytelseEndringDato(formaterDatoNorsk(LocalDate.now().minusDays(5)))
                .medNaturalytelseNyDagsats(500)
                .build();
        Arbeidsforhold arbeidsforhold2 = Arbeidsforhold.ny()
                .medArbeidsgiverNavn("Arbeidsgiver 2")
                .medGradering(true)
                .medProsentArbeid(10)
                .medStillingsprosent(20)
                .medUtbetalingsgrad(30)
                .medNaturalytelseEndringType(NaturalytelseEndringType.START)
                .medNaturalytelseEndringDato(formaterDatoNorsk(LocalDate.now().minusDays(50)))
                .medNaturalytelseNyDagsats(200)
                .build();
        Næring næring = Næring.ny()
                .medGradering(true)
                .medUtbetalingsgrad(60)
                .medProsentArbeid(70)
                .medSistLignedeÅr(2020)
                .build();
        AnnenAktivitet annenAktivitet = AnnenAktivitet.ny()
                .medAktivitetStatus(AktivitetStatus.KOMBINERT_AT_FL.name())
                .medGradering(true)
                .medUtbetalingsgrad(20)
                .medProsentArbeid(30)
                .build();
        Utbetalingsperiode periode1 = Utbetalingsperiode.ny()
                .medInnvilget(true)
                .medÅrsak("2001")
                .medPeriodeFom(LocalDate.now().minusDays(10))
                .medPeriodeTom(LocalDate.now().minusDays(8))
                .medPeriodeDagsats(123L)
                .medAntallTapteDager(10)
                .medPrioritertUtbetalingsgrad(100)
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
                .medPrioritertUtbetalingsgrad(80)
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
                .build();
        BeregningsgrunnlagAndel andel2 = BeregningsgrunnlagAndel.ny()
                .medAktivitetStatus(AktivitetStatus.KOMBINERT_AT_FL.name())
                .medArbeidsgiverNavn("Arbeidsgiver 2")
                .medDagsats(200)
                .medMånedsinntekt(1000)
                .medÅrsinntekt(110000)
                .medEtterlønnSluttpakke(true)
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

    private Object utførTest(Object object) throws IOException {
        return fraJson(tilJson(object), object.getClass());
    }

    private String tilJson(Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

    private Object fraJson(String json, Class clazz) throws JsonProcessingException {
        return mapper.readValue(json, clazz);
    }
}
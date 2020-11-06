package no.nav.foreldrepenger.melding.datamapper.brev;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.InntektArbeidYtelse;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.Inntektsmelding;
import no.nav.foreldrepenger.melding.integrasjon.dokument.inntektsmeldingfortidlig.BehandlingsTypeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.inntektsmeldingfortidlig.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.inntektsmeldingfortidlig.YtelseTypeKode;

public class InntektsmeldingFørSøknadBrevMapperTest {

    private static final String ARBEIDSGIVER = "Arbeidsgiver";
    private static final LocalDate FØRSTE_JANUAR = LocalDate.of(2019, 1, 1);
    private InntektsmeldingFørSøknadBrevMapper brevMapper = new InntektsmeldingFørSøknadBrevMapper(null, DatamapperTestUtil.getBrevParametere());
    private Behandling behandling = DatamapperTestUtil.standardBehandlingBuilder().medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD).build();
    private DokumentHendelse dokumentHendelse;
    private InntektArbeidYtelse iay;
    private Inntektsmelding inntektsmelding;

    @BeforeEach
    public void setup() {
        dokumentHendelse = DatamapperTestUtil.standardDokumenthendelse();
        inntektsmelding = new Inntektsmelding(ARBEIDSGIVER, "123", FØRSTE_JANUAR, Collections.emptyList(), FØRSTE_JANUAR);
        iay = InntektArbeidYtelse.ny().medInntektsmeldinger(List.of(inntektsmelding)).build();
    }

    @Test
    public void skal_mappe_brev_inntektsmelding_før_søknad() {
        FagType fagType = brevMapper.mapFagType(behandling, iay, dokumentHendelse);
        assertThat(fagType.getArbeidsgiverNavn()).isEqualTo(ARBEIDSGIVER);
        assertThat(fagType.getBehandlingsType()).isEqualTo(BehandlingsTypeKode.FOERSTEGANGSBEHANDLING);
        assertThat(fagType.getMottattDato()).isEqualTo(XmlUtil.finnDatoVerdiAvUtenTidSone(FØRSTE_JANUAR));
        assertThat(fagType.getPeriodeListe().getPeriode()).isEmpty();
        assertThat(fagType.getYtelseType()).isEqualTo(YtelseTypeKode.FP);
        assertThat(fagType.getSokAntallUkerFor()).isEqualTo(BigInteger.valueOf(DatamapperTestUtil.getBrevParametere().getSøkAntallUker()));
    }

}

package no.nav.foreldrepenger.melding.datamapper.brev;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.fritekstbrev.FagType;

public class InnvilgelseSvangerskapspengerBrevMapperTest extends OppsettForGjengivelseAvManuellTest {

    @Mock
    private DokumentFelles dokumentFelles;
    @Mock
    private FellesType fellesType;

    @InjectMocks
    protected InnvilgelseSvangerskapspengerBrevMapper mapper;

    @BeforeEach
    public void setup() {
        mapper = new InnvilgelseSvangerskapspengerBrevMapper(brevParametere, domeneobjektProvider);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void scenario_med_to_arbeidsforhold() {
        setup("scenario");
        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(testScenario.getString("forventet_brødtekst"));
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(
                testScenario.getString("forventet_overskrift"));
    }

    @Test
    public void scenario_AT_FL_med_refusjon_til_arbeidsgiver() {
        setup("scenario_AT_FN_2");
        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(
                testScenario.getString("forventet_brødtekst_2"));
    }

    @Test
    public void revurdering_fra_avslag_til_innvilget_tekst() {
        mapper = medRevurderingData(true, true, false);
        MockitoAnnotations.initMocks(this);
        setup("scenario_AT_FN_2");
        Behandling revurdering = spy(behandling);
        when(revurdering.getBehandlingType()).thenReturn(BehandlingType.REVURDERING);
        when(revurdering.erRevurdering()).thenReturn(false); // hack for å omgå mangler i næværende testdata-sett
        FagType fagType = mapper.mapFagType(dokumentHendelse, revurdering);
        assertThat(fagType.getHovedoverskrift()).isEqualToNormalizingNewlines(
                "NAV har endret svangerskapspengene dine\n");
        assertThat(fagType.getBrødtekst()
                .contains(
                        "Vi har vurdert saken din på nytt, og du har rett til svangerskapspenger. Vi har derfor endret vedtaket du har fått tidligere."))
                .isTrue();
        assertThat(fagType.getBrødtekst()
                .contains(
                        "Vi har fått nye inntektsopplysningar. Derfor har vi endra det du får utbetalt.")).isFalse(); // skal ikke kunne komme i kombinasjon med teksten over.
    }

    @Test
    public void revurdering_med_endret_utbetaling_og_termindato_tekst() {
        mapper = medRevurderingData(false, true, true);
        MockitoAnnotations.initMocks(this);
        setup("scenario_AT_FN_2");
        Behandling revurdering = spy(behandling);
        when(revurdering.getBehandlingType()).thenReturn(BehandlingType.REVURDERING);
        when(revurdering.erRevurdering()).thenReturn(false); // hack for å omgå mangler i næværende testdata-sett
        FagType fagType = mapper.mapFagType(dokumentHendelse, revurdering);
        assertThat(fagType.getBrødtekst()
                .contains("Vi har fått nye inntektsopplysninger. Derfor har vi endret det du får utbetalt.")).isTrue();
        assertThat(fagType.getBrødtekst()
                .contains(
                        "Vi har endret den siste dagen din med svangerskapspenger til 9. juli 2019 fordi du har fått endret termindatoen din."))
                .isTrue();
    }

    private InnvilgelseSvangerskapspengerBrevMapper medRevurderingData(boolean erEndretFraAvslag,
                                                                       boolean erUtbetalingEndret,
                                                                       boolean erTermindatoEndret) {
        return new InnvilgelseSvangerskapspengerBrevMapper(brevParametere, domeneobjektProvider) {
            @Override
            Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling) {
                Brevdata brevdata = super.mapTilBrevfelter(hendelse, behandling);
                brevdata.leggTil("erEndretFraAvslag", erEndretFraAvslag)
                        .leggTil("erUtbetalingEndret", erUtbetalingEndret)
                        .leggTil("erTermindatoEndret", erTermindatoEndret);
                return brevdata;
            }
        };
    }

    @Override
    String mappenHvorFilenMedLoggetTestdataLigger() {
        return "innvilgelsesvangerskapspenger";
    }
}

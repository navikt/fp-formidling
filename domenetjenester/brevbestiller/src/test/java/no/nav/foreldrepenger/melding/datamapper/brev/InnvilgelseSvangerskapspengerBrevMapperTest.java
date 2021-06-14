package no.nav.foreldrepenger.melding.datamapper.brev;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.fritekstbrev.FagType;

@ExtendWith(MockitoExtension.class)
public class InnvilgelseSvangerskapspengerBrevMapperTest extends OppsettForGjengivelseAvManuellTest {

    @Mock
    private DokumentFelles dokumentFelles;
    @Mock
    private FellesType fellesType;

    @InjectMocks
    protected InnvilgelseSvangerskapspengerBrevMapper mapper;

    @BeforeEach
    public void setup() {
        lenient().when(arbeidsgiverTjeneste.hentArbeidsgiverNavn("973135678")).thenReturn("COLOR LINE CREW AS");
        lenient().when(arbeidsgiverTjeneste.hentArbeidsgiverNavn("973861778")).thenReturn("EQUINOR ASA AVD STATOIL SOKKELVIRKSOMHET");
        behandlingRestKlient = new RedirectedToJsonResource(null, null);
        domeneobjektProvider = new DomeneobjektProvider(behandlingRestKlient, arbeidsgiverTjeneste);
        mapper = new InnvilgelseSvangerskapspengerBrevMapper(brevParametere, domeneobjektProvider);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void scenario_med_to_arbeidsforhold() throws Exception {
        setup("scenario_to_arbforhold");
        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(testScenario.getString("forventet_brødtekst"));
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(
                testScenario.getString("forventet_overskrift"));
    }

    @Test
    public void scenario_AT_FL_med_refusjon_til_arbeidsgiver() throws Exception {
        setup("scenario_at_fn_2");
        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(
                testScenario.getString("forventet_brødtekst_2"));
    }

    @Test
    public void revurdering_fra_avslag_til_innvilget_tekst() throws Exception {
        mapper = medRevurderingData(true, true, false);
        MockitoAnnotations.openMocks(this);
        setup("scenario_at_fn_2");
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
                        "Vi har fått nye inntektsopplysningar. Derfor har vi endra det du får utbetalt.")).isFalse(); // skal ikke kunne komme i
                                                                                                                      // kombinasjon med teksten over.
    }

    @Test
    public void revurdering_med_endret_utbetaling_og_termindato_tekst() throws Exception {
        mapper = medRevurderingData(false, true, true);
        MockitoAnnotations.openMocks(this);
        setup("scenario_at_fn_2");
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

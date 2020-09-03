package no.nav.foreldrepenger.melding.datamapper.brev;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.fritekstbrev.FagType;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;

import static no.nav.foreldrepenger.melding.datamapper.mal.fritekst.BrevmalKilder.ROTMAPPE;
import static org.assertj.core.api.Assertions.assertThat;

public class InnvilgelseEngangstønadNyBrevMapperTest {
    private Behandling behandling;
    private DokumentHendelse dokumentHendelse;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().silent();
    @Mock
    DokumentFelles dokumentFelles;
    @Mock
    FellesType fellesType;
    @Mock
    BrevParametere brevParametere;

    @InjectMocks
    private InnvilgelseEngangstønadNyBrevMapper mapper;

    public void setup(FagsakYtelseType ytelseType, BehandlingType behandlingType, long ID) {
        behandling = Behandling.builder().medId(ID)
                .medBehandlingType(behandlingType)
                .medSpråkkode(Språkkode.nb)
                .build();
        dokumentHendelse = DokumentHendelse.builder()
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .medYtelseType(ytelseType)
                .medBehandlendeEnhetNavn(HenleggBehandlingBrevMapper.FAMPEN)
                .build();
       mapper = new InnvilgelseEngangstønadNyBrevMapper() {
            @Override
            FritekstmalBrevMapper.Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling) {
                Brevdata brevdata = new Brevdata();
                brevdata.leggTil("førstegangsBehandling",(behandling.erFørstegangssøknad()))
                        .leggTil("revurdering",(behandling.erRevurdering()))
                        .leggTil("medhold",BehandlingMapper.erMedhold(behandling))
                        .leggTil("innvilgetbeløp",12000L)
                        .leggTil("klageFristUker", "6")
                        .leggTil("fbEllerMedhold", (behandling.erFørstegangssøknad() || BehandlingMapper.erMedhold(behandling)))
                        .leggTil("død", false)
                        .leggTil("kontaktTelefonnummer", "21 07 02 02");

                if (behandling.erRevurdering()) {

                    brevdata.leggTil("endretSats", (1000L));
                }
                return brevdata;
            }
        };
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testMap_fagtype_førstegangsbehandling() {
        setup(FagsakYtelseType.ENGANGSTØNAD, BehandlingType.FØRSTEGANGSSØKNAD, 123L);

        ResourceBundle expectedValues = ResourceBundle.getBundle(
                String.join("/", ROTMAPPE, mapper.templateFolder(), "expected"),
                new Locale("nb", "NO"));

        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingPunctuationAndWhitespace(expectedValues.getString("brødtekstfb"));
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(expectedValues.getString("overskrift"));
    }
    @Test
    public void testMap_fagtype_revurdering() {
        setup(FagsakYtelseType.ENGANGSTØNAD, BehandlingType.REVURDERING, 123L);

        ResourceBundle expectedValues = ResourceBundle.getBundle(
                String.join("/", ROTMAPPE, mapper.templateFolder(), "expected"),
                new Locale("nb", "NO"));

        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingPunctuationAndWhitespace(expectedValues.getString("brødtekstrv"));
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(expectedValues.getString("overskrift"));
    }
}
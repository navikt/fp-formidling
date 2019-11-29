package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.mal.fritekst.BrevmalKilder.ROTMAPPE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.BehandlingÅrsak;
import no.nav.foreldrepenger.melding.behandling.BehandlingÅrsakType;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.fritekstbrev.FagType;

public class InfoTilAnnenForelderBrevMapperTest {
    private long ID;
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
    private InfoTilAnnenForelderBrevMapper mapper;

    public void setup(String behandlingÅrsakType, long ID) {
        behandling = Behandling.builder().medId(ID)
                .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medSpråkkode(Språkkode.nb)
                .medBehandlingÅrsaker(List.of(BehandlingÅrsak.builder()
                    .medBehandlingÅrsakType(behandlingÅrsakType)
                    .build()))
                .build();
        dokumentHendelse = DokumentHendelse.builder()
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .medYtelseType(FagsakYtelseType.FORELDREPENGER)
                .medBehandlendeEnhetNavn(HenleggBehandlingBrevMapper.FAMPEN)
                .build();

        mapper = new InfoTilAnnenForelderBrevMapper() {
            @Override
            Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling) {
                return new Brevdata()
                        .leggTil("kontaktTelefonnummer", null)
                        .leggTil("navnAvsenderEnhet", "Avsender")
                        .leggTil("erAutomatiskVedtak", true)
                        .leggTil("behandlingsAarsak", behandling.getBehandlingÅrsaker().get(0).getBehandlingÅrsakType());
            }
        };
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test_map_fagtype() {
        setup(BehandlingÅrsakType.INFOBREV_BEHANDLING.getKode(), 123L);

        ResourceBundle expectedValues = ResourceBundle.getBundle(
                String.join("/", ROTMAPPE, mapper.templateFolder(), "expected"),
                new Locale("nb", "NO"));

        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(expectedValues.getString("brødtekst"));
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(expectedValues.getString("overskrift"));
    }

    @Test
    public void test_map_fagtypeOpphold() {
      setup(BehandlingÅrsakType.INFOBREV_OPPHOLD_BEHANDLING.getKode(), 124L);

      ResourceBundle expectedValues = ResourceBundle.getBundle(
            String.join("/", ROTMAPPE, mapper.templateFolder(), "expected"),
            new Locale("nb", "NO"));

        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(expectedValues.getString("brødtekstOpphold"));
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(expectedValues.getString("overskrift"));
    }
}

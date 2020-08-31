package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.mal.fritekst.BrevmalKilder.ROTMAPPE;
import static no.nav.foreldrepenger.melding.typer.Dato.medFormatering;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
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
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.fritekstbrev.FagType;

public class EtterlysInntektsmeldingBrevMapperTest {
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
    private EtterlysInntektsmeldingBrevMapper mapper;

    public void setup(FagsakYtelseType ytelseType, long ID) {
        behandling = Behandling.builder().medId(ID)
                .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medSpråkkode(Språkkode.nb)
                .build();
        dokumentHendelse = DokumentHendelse.builder()
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .medYtelseType(ytelseType)
                .medBehandlendeEnhetNavn(HenleggBehandlingBrevMapper.FAMPEN)
                .build();

        mapper = new EtterlysInntektsmeldingBrevMapper() {
            @Override
            FritekstmalBrevMapper.Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling) {

                return new Brevdata()
                        .leggTil("ytelse", hendelse.getYtelseType().getKode())
                        .leggTil("soknadDato", medFormatering(LocalDate.of(2020,1,1)))
                        .leggTil("fristDato", medFormatering(LocalDate.of(2020,1,31)))
                        .leggTil("erAutomatiskVedtak", Boolean.FALSE);
                }
            };
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test_map_fagtype_foreldrepenger() {
        setup(FagsakYtelseType.FORELDREPENGER, 123L);

        ResourceBundle expectedValues = ResourceBundle.getBundle(
                String.join("/", ROTMAPPE, mapper.templateFolder(), "expected"),
                new Locale("nb", "NO"));

        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(expectedValues.getString("brødtekstfp"));
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(expectedValues.getString("overskrift"));
    }

    @Test
    public void test_map_fagtype_svangerskapspenger() {
      setup(FagsakYtelseType.SVANGERSKAPSPENGER, 124L);

      ResourceBundle expectedValues = ResourceBundle.getBundle(
            String.join("/", ROTMAPPE, mapper.templateFolder(), "expected"),
            new Locale("nb", "NO"));

        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(expectedValues.getString("brødtekstsvp"));
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(expectedValues.getString("overskrift"));
    }
}

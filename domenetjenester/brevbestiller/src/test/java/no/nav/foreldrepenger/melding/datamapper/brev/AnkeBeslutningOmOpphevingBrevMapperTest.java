package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.mal.fritekst.BrevmalKilder.ROTMAPPE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import no.nav.foreldrepenger.melding.anke.Anke;
import no.nav.foreldrepenger.melding.anke.AnkeVurdering;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.fritekstbrev.FagType;


class AnkeBeslutningOmOpphevingBrevMapperTest {
    private Behandling behandling;
    private DokumentHendelse dokumentHendelse;

    private static final String FRITEKST = "Tar med fristekst for å sjekke om det ser greit ut";

    @Mock
    private DokumentFelles dokumentFelles;
    @Mock
    private FellesType fellesType;
    @Mock
    private BrevParametere brevParametere;

    @InjectMocks
    protected AnkeBeslutningOmOpphevingBrevMapper mapper;

    void setUp(AnkeVurdering vurdering, String fritekst, long ID) {
        behandling = Behandling.builder().medId(ID)
                .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medSpråkkode(Språkkode.NB)
                .build();
        dokumentHendelse = DokumentHendelse.builder()
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .medYtelseType(FagsakYtelseType.FORELDREPENGER)
                .medBehandlendeEnhetNavn(HenleggBehandlingBrevMapper.FAMPEN)
                .build();

        mapper = new AnkeBeslutningOmOpphevingBrevMapper() {
            @Override
            AnkeBeslutningOmOpphevingBrevMapper.Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling) {
                Optional<Anke> anke = lagAnke(vurdering, fritekst);
                return new Brevdata()
                        .leggTil("ytelseType", dokumentHendelse.getYtelseType().getKode())
                        .leggTil("fritekst", anke.map(Anke::getFritekstTilBrev).orElse(""))
                        .leggTil("utenOppheve", erAnkeHjemsendUtenOpphev(anke.map(Anke::getAnkeVurdering).orElse(AnkeVurdering.UDEFINERT)));
            }
        };
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void map_for_hjemsende_og_oppheve_foreldrepenger() {
        //Arrange
        setUp(AnkeVurdering.ANKE_OPPHEVE_OG_HJEMSENDE, FRITEKST, 12345678);
        //Assert
        ResourceBundle expectedValues = ResourceBundle.getBundle(
                String.join("/", ROTMAPPE, mapper.templateFolder(), "expected"),
                new Locale("nb", "NO"));

        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);

        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(expectedValues.getString("ankeHjemsendOgOpphev"));
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(expectedValues.getString("overskrift"));
    }

    @Test
    void map_for_hjemsende_uten_oppheve() {
        //Arrange
        setUp(AnkeVurdering.ANKE_HJEMSEND_UTEN_OPPHEV, FRITEKST, 12345679);
        //Assert
        ResourceBundle expectedValues = ResourceBundle.getBundle(
                String.join("/", ROTMAPPE, mapper.templateFolder(), "expected"),
                new Locale("nb", "NO"));

        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);

        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(expectedValues.getString("ankeHjemsend"));
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(expectedValues.getString("overskrift"));
    }

    @Test
    void map_uten_anke() {
        //Arrange
        setUp(null, null, 12345679);
        //Assert
        ResourceBundle expectedValues = ResourceBundle.getBundle(
                String.join("/", ROTMAPPE, mapper.templateFolder(), "expected"),
                new Locale("nb", "NO"));

        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);

        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(expectedValues.getString("utenAnke"));
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(expectedValues.getString("overskrift"));
    }

    private Optional<Anke> lagAnke(AnkeVurdering vurdering, String fritekst) {
        return Optional.ofNullable(Anke.ny()
                .medAnkeVurdering(vurdering)
                .medFritekstTilBrev(fritekst)
                .build());
    }
}
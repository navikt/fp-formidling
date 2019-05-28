package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.mal.fritekst.BrevmalKilder.ROTMAPPE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.fritekstbrev.FagType;

public class InfoTilAnnenForelderBrevMapperTest {
    private static final long ID = 123L;
    private Behandling behandling;
    private DokumentHendelse dokumentHendelse;
    private InfoTilAnnenForelderBrevMapper mapper = new InfoTilAnnenForelderBrevMapper() {
        @Override
        Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling) {
            return new Brevdata(behandling.getSpråkkode()) {
                public String getDato() {
                    return "31.12.9999";
                }

                public String getKontaktTelefonnummer() {
                    return "11111111";
                }

                public String getNavnAvsenderEnhet() {
                    return "Avsender";
                }

                public boolean getErAutomatiskVedtak() {
                    return true;
                }
            };
        }
    };

    @Before
    public void setup() {
        behandling = Behandling.builder().medId(ID)
                .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medBehandlendeEnhetNavn(HenleggBehandlingBrevMapper.FAMPEN)
                .medSpråkkode(Språkkode.nb)
                .build();
        dokumentHendelse = DokumentHendelse.builder()
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .medYtelseType(FagsakYtelseType.FORELDREPENGER)
                .build();
    }

    @Test
    public void test_map_fagtype() {
        ResourceBundle expectedValues = ResourceBundle.getBundle(
                String.join("/", ROTMAPPE, mapper.templateFolder(), "expected"),
                new Locale("nb", "NO"));

        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(expectedValues.getString("brødtekst"));
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(expectedValues.getString("overskrift"));
    }
}

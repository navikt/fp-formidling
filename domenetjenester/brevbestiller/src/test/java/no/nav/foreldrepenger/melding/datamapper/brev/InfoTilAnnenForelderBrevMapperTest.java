package no.nav.foreldrepenger.melding.datamapper.brev;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.fritekstbrev.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.uendretutfall.YtelseTypeKode;

public class InfoTilAnnenForelderBrevMapperTest {
    private static final long ID = 123L;
    private static final String OVERSKRIFT_FASIT = "Du må søke om foreldrepenger i tideDu må søke om foreldrepenger i tide";
    private static final String BRØDTEKST_FASIT = "Du kan ha rett til foreldrepenger. Du må søke innen 5 milliarder år, som er den siste dagen i perioden\n" +
            "dere begge kan ta ut. Hvis du ikke søker, mister du foreldrepengene.\n" +
            "\n" +
            "_Har du spørsmål?\n" +
            "Du finner nyttig informasjon på nav.no/familie. Du kan også kontakte oss på telefon\n" +
            "11111111.\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "Med vennlig hilsen\n" +
            "Avsender\n" +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "Vedtaket har blitt automatisk saksbehandlet av vårt fagsystem.\n" +
            "Vedtaksbrevet er derfor ikke underskrevet av saksbehandler.\n";

    private Behandling behandling;
    private DokumentHendelse dokumentHendelse;
    private InfoTilAnnenForelderBrevMapper mapper = new InfoTilAnnenForelderBrevMapper() {
        @Override
        Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling) {
            return new Brevdata(behandling.getSpråkkode()) {
                public String getDato() {
                    return "5 milliarder år";
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
                .medYtelseType(FagsakYtelseType.FORELDREPENGER)
                .build();
    }

    @Test
    public void test_map_fagtype() {
        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(BRØDTEKST_FASIT);
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(OVERSKRIFT_FASIT);
    }
}

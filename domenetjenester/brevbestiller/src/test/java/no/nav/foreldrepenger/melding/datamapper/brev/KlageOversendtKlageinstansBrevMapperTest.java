package no.nav.foreldrepenger.melding.datamapper.brev;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.datamapper.BrevMapperUtil;
import no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.oversendt.klageinstans.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.klage.oversendt.klageinstans.YtelseTypeKode;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.klage.KlageDokument;

public class KlageOversendtKlageinstansBrevMapperTest {

    BrevParametere brevParametere = DatamapperTestUtil.getBrevParametere();
    KlageOversendtKlageinstansBrevMapper brevMapper = new KlageOversendtKlageinstansBrevMapper(brevParametere, null);
    private DokumentHendelse dokumentHendelse = DatamapperTestUtil.standardDokumenthendelse();
    private Behandling behandling = DatamapperTestUtil.standardBehandling();
    private Klage klage;
    private KlageDokument klageDokument;


    @Before
    public void setup() {
        klageDokument = Mockito.mock(KlageDokument.class);
        doReturn(DatamapperTestUtil.FØRSTE_JANUAR_TJUENITTEN).when(klageDokument).getMottattDato();
    }

    @Test
    public void skal_mappe_klage_oversendt() {
        FagType fagType = brevMapper.mapFagType(dokumentHendelse, klage, klageDokument, behandling);
        assertThat(fagType.getAntallUker()).isEqualTo(KlageOversendtKlageinstansBrevMapper.BEHANDLINGSFRIST_UKER_KA);
        assertThat(fagType.getFritekst()).isEqualTo(DatamapperTestUtil.FRITEKST);
        assertThat(fagType.getMottattDato()).isEqualTo(XmlUtil.finnDatoVerdiAvUtenTidSone(DatamapperTestUtil.FØRSTE_JANUAR_TJUENITTEN));
        assertThat(fagType.getYtelseType()).isEqualTo(YtelseTypeKode.FP);
        assertThat(fagType.getFristDato()).isEqualTo(XmlUtil.finnDatoVerdiAvUtenTidSone(BrevMapperUtil.getSvarFrist(brevParametere)));
    }
}

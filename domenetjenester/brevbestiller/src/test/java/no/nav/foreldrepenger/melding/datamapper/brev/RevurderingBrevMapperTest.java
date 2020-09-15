package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.FØRSTE_JANUAR_TJUENITTEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import java.math.BigInteger;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.RevurderingVarslingÅrsak;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil;
import no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.revurdering.AdvarselKodeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.revurdering.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.revurdering.YtelseTypeKode;

public class RevurderingBrevMapperTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    private RevurderingBrevMapper brevMapper;

    private DokumentHendelse dokumentHendelse;
    @Mock
    private Behandling behandling;
    @Mock
    private FamilieHendelse familieHendelse;
    private BrevMapperUtil brevMapperUtil;


    @Before
    public void setup() {
        dokumentHendelse = DatamapperTestUtil.lagStandardHendelseBuilder().medRevurderingVarslingÅrsak(RevurderingVarslingÅrsak.MOR_AKTIVITET_IKKE_OPPFYLT).build();
        doReturn(BigInteger.TWO).when(familieHendelse).getAntallBarn();
        doReturn(Optional.empty()).when(familieHendelse).getTermindato();
        brevMapperUtil = new BrevMapperUtil(DatamapperTestUtil.getBrevParametere());
        brevMapper = new RevurderingBrevMapper(null, brevMapperUtil);
    }

    @Test
    public void skal_mappe_varsel_om_revurdering() {
        FellesType fellesType = DatamapperTestUtil.getFellesType();
        fellesType.setAutomatiskBehandlet(true);
        FagType fagType = brevMapper.mapFagType(fellesType, dokumentHendelse, behandling, familieHendelse);
        assertThat(fagType.getAdvarselKode()).isEqualTo(AdvarselKodeKode.AKTIVITET);
        assertThat(fagType.getAntallBarn()).isEqualTo(BigInteger.TWO);
        assertThat(fagType.getFritekst()).isEqualTo(DatamapperTestUtil.FRITEKST);
        assertThat(fagType.getTerminDato()).isNull();
        assertThat(fagType.getYtelseType()).isEqualTo(YtelseTypeKode.FP);
        assertThat(fagType.getFristDato()).isEqualTo(XmlUtil.finnDatoVerdiAvUtenTidSone(brevMapperUtil.getSvarFrist()));
        assertThat(fellesType.isAutomatiskBehandlet()).isFalse();
    }

    @Test
    public void skal_mappe_varsel_om_revurdering_med_termindato() {
        doReturn(Optional.of(FØRSTE_JANUAR_TJUENITTEN)).when(familieHendelse).getTermindato();
        FagType fagType = brevMapper.mapFagType(DatamapperTestUtil.getFellesType(), dokumentHendelse, behandling, familieHendelse);
        assertThat(fagType.getTerminDato()).isEqualTo(XmlUtil.finnDatoVerdiAvUtenTidSone(FØRSTE_JANUAR_TJUENITTEN));
    }


}

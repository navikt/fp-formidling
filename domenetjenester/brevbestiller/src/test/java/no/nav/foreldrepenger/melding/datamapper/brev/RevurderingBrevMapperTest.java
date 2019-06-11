package no.nav.foreldrepenger.melding.datamapper.brev;

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
import no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.revurdering.FagType;

public class RevurderingBrevMapperTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    RevurderingBrevMapper brevMapper;

    private DokumentHendelse dokumentHendelse;
    @Mock
    private Behandling behandling;
    @Mock
    private FamilieHendelse familieHendelse;


    @Before
    public void setup() {
        dokumentHendelse = DatamapperTestUtil.lagStandardHendelseBuilder().medRevurderingVarslingÅrsak(RevurderingVarslingÅrsak.MOR_AKTIVITET_IKKE_OPPFYLT).build();
        doReturn(BigInteger.TWO).when(familieHendelse).getAntallBarn();
        doReturn(Optional.empty()).when(familieHendelse).getTermindato();
        brevMapper = new RevurderingBrevMapper(null, DatamapperTestUtil.getBrevParametere());
    }


    @Test
    public void skal_mappe_varsel_om_revurdering() {
        FellesType fellesType = DatamapperTestUtil.getFellesType();
        fellesType.setAutomatiskBehandlet(true);
        FagType fagType = brevMapper.mapFagType(fellesType, dokumentHendelse, behandling, familieHendelse);
    }


}

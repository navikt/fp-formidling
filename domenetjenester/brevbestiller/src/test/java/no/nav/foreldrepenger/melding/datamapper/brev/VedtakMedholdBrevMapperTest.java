package no.nav.foreldrepenger.melding.datamapper.brev;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.vedtak.mehold.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.vedtak.mehold.OpphavTypeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.vedtak.mehold.YtelseTypeKode;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.klage.KlageFormkravResultat;
import no.nav.foreldrepenger.melding.klage.KlageVurderingResultat;

public class VedtakMedholdBrevMapperTest {

    private static final String KLAGE_FRITEKST = "KLAGE FRITEKST";
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().silent();
    KlageFormkravResultat klageFormkravResultat;
    private VedtakMedholdBrevMapper brevMapper;
    private DokumentHendelse dokumentHendelse;
    @Mock
    private Klage klage;
    private KlageVurderingResultat klageVurderingResultat;

    @Before
    public void setup() {
        dokumentHendelse = DatamapperTestUtil.standardDokumenthendelse();
        klageFormkravResultat = KlageFormkravResultat.ny().build();
        doReturn(klageFormkravResultat).when(klage).getFormkravKA();
        klageVurderingResultat = KlageVurderingResultat.ny()
                .medFritekstTilbrev(KLAGE_FRITEKST)
                .build();
        doReturn(klageVurderingResultat).when(klage).getGjeldendeKlageVurderingsresultat();
        brevMapper = new VedtakMedholdBrevMapper(null, DatamapperTestUtil.getBrevParametere());
    }

    @Test
    public void skal_mappe_vedtak_om_medhold_i_klage() {
        FagType fagType = brevMapper.mapFagType(dokumentHendelse, klage);
        assertThat(fagType.getOpphavType()).isEqualTo(OpphavTypeKode.KLAGE);
        assertThat(fagType.getYtelseType()).isEqualTo(YtelseTypeKode.FP);
        assertThat(fagType.getFritekst()).isEqualTo(DatamapperTestUtil.FRITEKST);
        assertThat(fagType.getKlageFristUker()).isEqualTo(BigInteger.valueOf(DatamapperTestUtil.getBrevParametere().getKlagefristUker()));
    }

    @Test
    public void skal_sette_fritekst_lagret_i_klage() {
        dokumentHendelse = DatamapperTestUtil.lagStandardHendelseBuilder().medFritekst(null).build();
        FagType fagType = brevMapper.mapFagType(dokumentHendelse, klage);
        assertThat(fagType.getFritekst()).isEqualTo(KLAGE_FRITEKST);
    }

}

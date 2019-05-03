package no.nav.foreldrepenger.melding.datamapper.domene;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

import no.nav.foreldrepenger.melding.fagsak.Dekningsgrad;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.VurderingsstatusKode;
import no.nav.foreldrepenger.melding.søknad.Søknad;
import no.nav.foreldrepenger.melding.ytelsefordeling.OppgittRettighet;
import no.nav.foreldrepenger.melding.ytelsefordeling.YtelseFordeling;

public class YtelsefordelingMapperTest {

    Søknad søknad;
    YtelseFordeling ytelseFordeling;
    private LocalDate nå = LocalDate.now();

    @Before
    public void setup() {

    }

    @Test
    public void skal_oppdage_aleneomsorg() {
        søknad = new Søknad(nå, nå, new OppgittRettighet(true));
        ytelseFordeling = new YtelseFordeling(new Dekningsgrad(100), true, true);
        assertThat(YtelsefordelingMapper.harSøkerAleneomsorg(søknad, ytelseFordeling)).isEqualTo(VurderingsstatusKode.JA);
        assertThat(YtelsefordelingMapper.harSøkerAleneomsorgBoolean(søknad, ytelseFordeling)).isTrue();
    }

    @Test
    public void skal_oppdage_ikke_søkt_om_aleneomsorg() {
        søknad = new Søknad(nå, nå, new OppgittRettighet(false));
        ytelseFordeling = new YtelseFordeling(new Dekningsgrad(100), true, false);
        assertThat(YtelsefordelingMapper.harSøkerAleneomsorg(søknad, ytelseFordeling)).isEqualTo(VurderingsstatusKode.IKKE_VURDERT);
        assertThat(YtelsefordelingMapper.harSøkerAleneomsorgBoolean(søknad, ytelseFordeling)).isFalse();
    }


    @Test
    public void skal_oppdage_avslått_aleneomsorg() {
        søknad = new Søknad(nå, nå, new OppgittRettighet(true));
        ytelseFordeling = new YtelseFordeling(new Dekningsgrad(100), true, false);
        assertThat(YtelsefordelingMapper.harSøkerAleneomsorg(søknad, ytelseFordeling)).isEqualTo(VurderingsstatusKode.NEI);
        assertThat(YtelsefordelingMapper.harSøkerAleneomsorgBoolean(søknad, ytelseFordeling)).isFalse();
    }

}

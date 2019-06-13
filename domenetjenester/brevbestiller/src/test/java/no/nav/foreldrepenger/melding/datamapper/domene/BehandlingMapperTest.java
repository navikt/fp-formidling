package no.nav.foreldrepenger.melding.datamapper.domene;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Test;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.behandling.BehandlingÅrsak;
import no.nav.foreldrepenger.melding.behandling.BehandlingÅrsakType;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;

public class BehandlingMapperTest {

    private static final String HENDELSE_FRITEKST = "HENDELSE_FRITEKST";
    private static final String BEHANDLING_FRITEKST = "BEHANDLING_FRITEKST";
    private static final long BEHANDLING_ID = 123L;
    Behandling behandling;
    DokumentHendelse dokumentHendelse;

    @Test
    public void skal_velge_ingen_fritekst_når_ingen_finnes() {
        behandling = standardBehandlingBuilder()
                .build();
        dokumentHendelse = standardHendelseBuilder().build();
        assertThat(BehandlingMapper.avklarFritekst(dokumentHendelse, behandling)).isNotPresent();
    }

    @Test
    public void skal_prioritere_hendelse() {
        behandling = standardBehandlingBuilder()
                .medBehandlingsresultat(Behandlingsresultat.builder()
                        .medAvslagarsakFritekst(BEHANDLING_FRITEKST)
                        .build())
                .build();
        dokumentHendelse = standardHendelseBuilder()
                .medFritekst(HENDELSE_FRITEKST)
                .build();
        assertThat(BehandlingMapper.avklarFritekst(dokumentHendelse, behandling).get()).isEqualTo(HENDELSE_FRITEKST);
    }


    @Test
    public void skal_ta_fritekst_fra_behandling_når_mangler_i_hendelse() {
        behandling = standardBehandlingBuilder()
                .medBehandlingsresultat(Behandlingsresultat.builder()
                        .medAvslagarsakFritekst(BEHANDLING_FRITEKST)
                        .build())
                .build();
        DokumentHendelse dokumentHendelse = standardHendelseBuilder()
                .build();
        assertThat(BehandlingMapper.avklarFritekst(dokumentHendelse, behandling).get()).isEqualTo(BEHANDLING_FRITEKST);
    }

    @Test
    public void skal_mappe_fødselshendelse() {
        Behandling behandling = opprettBehandlingMedEnÅrsak(BehandlingÅrsakType.RE_HENDELSE_FØDSEL.getKode());

        assertThat(BehandlingMapper.erRevurderingPgaFødselshendelse(behandling, null, Optional.empty())).isTrue();

        behandling = opprettBehandlingMedEnÅrsak(BehandlingÅrsakType.ETTER_KLAGE.getKode());
        FamilieHendelse familieHendelse = opprettFamiliehendelse(false);

        assertThat(BehandlingMapper.erRevurderingPgaFødselshendelse(behandling, familieHendelse, Optional.empty())).isFalse();
        assertThat(BehandlingMapper.erRevurderingPgaFødselshendelse(behandling, familieHendelse, Optional.of(familieHendelse))).isFalse();
    }

    @Test
    public void skal_mappe_ny_fødsel_fra_register() {
        Behandling behandling = opprettBehandlingMedEnÅrsak(BehandlingÅrsakType.ETTER_KLAGE.getKode());
        FamilieHendelse familieHendelseNy = opprettFamiliehendelse(true);
        FamilieHendelse familieHendelseGammel = opprettFamiliehendelse(false);

        assertThat(BehandlingMapper.erRevurderingPgaFødselshendelse(behandling, familieHendelseNy, Optional.of(familieHendelseGammel))).isTrue();
    }

    @Test
    public void skal_mappe_eksisterende_fødsel() {
        Behandling behandling = opprettBehandlingMedEnÅrsak(BehandlingÅrsakType.ETTER_KLAGE.getKode());
        FamilieHendelse familieHendelse = opprettFamiliehendelse(true);

        assertThat(BehandlingMapper.erRevurderingPgaFødselshendelse(behandling, familieHendelse, Optional.of(familieHendelse))).isFalse();
    }

    private FamilieHendelse opprettFamiliehendelse(boolean barnErFødt) {
        return new FamilieHendelse(BigInteger.ONE, barnErFødt, false, FamilieHendelseType.FØDSEL, new FamilieHendelse.OptionalDatoer(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
    }

    private Behandling opprettBehandlingMedEnÅrsak(String årsak) {
        return standardBehandlingBuilder()
                .medBehandlingÅrsaker(List.of(BehandlingÅrsak.builder()
                        .medBehandlingÅrsakType(årsak)
                        .build()))
                .build();
    }

    private DokumentHendelse.Builder standardHendelseBuilder() {
        return DokumentHendelse.builder()
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .medYtelseType(FagsakYtelseType.FORELDREPENGER);
    }

    private Behandling.Builder standardBehandlingBuilder() {
        return Behandling.builder()
                .medId(BEHANDLING_ID);
    }

}

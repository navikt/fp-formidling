package no.nav.foreldrepenger.melding.brevmapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardDokumentData;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.standardBehandling;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDatoNorsk;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.KlageOversendtDokumentdata;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.klage.KlageDokument;
import no.nav.foreldrepenger.melding.klage.KlageVurderingResultat;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;

@ExtendWith(MockitoExtension.class)
public class KlageOversendtDokumentdataMapperTest {

    private static final String FRITEKST_TIL_BREV = "FRITEKST";
    private static final LocalDate MOTTATT_DATO = LocalDate.now();

    @Mock
    private DomeneobjektProvider domeneobjektProvider;

    private DokumentData dokumentData;

    private KlageOversendtDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    public void before() {
        dokumentData = lagStandardDokumentData(DokumentMalType.KLAGE_OVERSENDT);
        when(domeneobjektProvider.hentKlageDokument(any(Behandling.class))).thenReturn(new KlageDokument(MOTTATT_DATO));
        dokumentdataMapper = new KlageOversendtDokumentdataMapper(domeneobjektProvider);
    }

    @Test
    public void skal_mappe_felter_for_brevet() {
        // Arrange
        Behandling behandling = standardBehandling();
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        DokumentHendelse dokumentHendelse = lagStandardHendelseBuilder().medErOpphevetKlage(true).build();
        mockKlage(behandling);

        // Act
        KlageOversendtDokumentdata dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getFelles()).isNotNull();
        assertThat(dokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(dokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(dokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(dokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoNorsk(LocalDate.now()));
        assertThat(dokumentdata.getFelles().getHarVerge()).isEqualTo(true);
        assertThat(dokumentdata.getFelles().getErKopi()).isEqualTo(true);
        assertThat(dokumentdata.getFelles().getSaksnummer()).isEqualTo(SAKSNUMMER);
        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo("FP");
        assertThat(dokumentdata.getFelles().getBehandlesAvKA()).isEqualTo(false);
        assertThat(dokumentdata.getFelles().getErUtkast()).isEqualTo(false);
        assertThat(dokumentdata.getFelles().getFritekst()).isEqualTo(FRITEKST_TIL_BREV);

        assertThat(dokumentdata.getGjelderTilbakekreving()).isFalse();
        assertThat(dokumentdata.getMottattDato()).isEqualTo(formaterDatoNorsk(MOTTATT_DATO));
    }

    private void mockKlage(Behandling behandling) {
        Klage klage = Klage.ny()
                .medPåklagdBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medKlageVurderingResultatNK(new KlageVurderingResultat(null, FRITEKST_TIL_BREV))
                .build();
        when(domeneobjektProvider.hentKlagebehandling(behandling)).thenReturn(klage);
    }
}
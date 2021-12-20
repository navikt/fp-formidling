package no.nav.foreldrepenger.melding.brevmapper.brev;

import static java.util.List.of;
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
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.KlageAvvistDokumentdata;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.klage.KlageAvvistÅrsak;
import no.nav.foreldrepenger.melding.klage.KlageFormkravResultat;
import no.nav.foreldrepenger.melding.klage.KlageVurderingResultat;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;


@ExtendWith(MockitoExtension.class)
public class KlageAvvistDokumentdataMapperTest {

    @Mock
    private BrevParametere brevParametere;

    @Mock
    private DomeneobjektProvider domeneobjektProvider;

    private DokumentData dokumentData;

    private KlageAvvistDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    public void before() {
        dokumentData = lagStandardDokumentData(DokumentMalType.KLAGE_AVVIST);
        when(brevParametere.getKlagefristUker()).thenReturn(6);
        dokumentdataMapper = new KlageAvvistDokumentdataMapper(brevParametere, domeneobjektProvider);
    }

    @Test
    public void skal_mappe_felter_for_vanlig_behandling() {
        // Arrange
        Behandling behandling = standardBehandling();
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        DokumentHendelse dokumentHendelse = lagStandardHendelseBuilder().build();
        mockKlage(behandling, BehandlingType.FØRSTEGANGSSØKNAD, of(KlageAvvistÅrsak.KLAGER_IKKE_PART));

        // Act
        KlageAvvistDokumentdata dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

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

        assertThat(dokumentdata.getGjelderTilbakekreving()).isFalse();
        assertThat(dokumentdata.getLovhjemler()).isEqualTo("forvaltningsloven §§ 28 og 33");
        assertThat(dokumentdata.getKlagefristUker()).isEqualTo(6);
        assertThat(dokumentdata.getAvvistGrunner()).containsAll(of(KlageAvvistÅrsak.KLAGER_IKKE_PART.getKode()));
    }

    @Test
    public void skal_mappe_felter_for_tilbakekreving_med_alle_avvist_grunner_og_spesialhåndtere_for_sent_og_ikke_signert() {
        // Arrange
        Behandling behandling = standardBehandling();
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        DokumentHendelse dokumentHendelse = lagStandardHendelseBuilder().build();
        mockKlage(behandling, BehandlingType.TILBAKEKREVING, of(KlageAvvistÅrsak.KLAGET_FOR_SENT, KlageAvvistÅrsak.KLAGER_IKKE_PART,
                KlageAvvistÅrsak.KLAGE_UGYLDIG, KlageAvvistÅrsak.IKKE_KONKRET, KlageAvvistÅrsak.IKKE_PAKLAGD_VEDTAK,
                KlageAvvistÅrsak.IKKE_SIGNERT));

        // Act
        KlageAvvistDokumentdata dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getGjelderTilbakekreving()).isTrue();
        assertThat(dokumentdata.getLovhjemler()).isEqualTo("folketrygdloven § 21-12 og forvaltningsloven §§ 28, 31, 32 og 33");
        assertThat(dokumentdata.getAvvistGrunner()).containsAll(of(KlageAvvistÅrsak.KLAGET_FOR_SENT.getKode(),
                KlageAvvistÅrsak.KLAGER_IKKE_PART.getKode(), KlageAvvistÅrsak.KLAGE_UGYLDIG.getKode(),
                KlageAvvistÅrsak.IKKE_KONKRET.getKode(), KlageAvvistÅrsak.IKKE_PAKLAGD_VEDTAK.getKode()));
    }

    private void mockKlage(Behandling behandling, BehandlingType behandlingType, List<KlageAvvistÅrsak> avvistÅrsaker) {
        KlageVurderingResultat klageVurderingResultat = new KlageVurderingResultat(null, "FRITEKST");
        KlageFormkravResultat klageFormkravResultat = new KlageFormkravResultat(avvistÅrsaker);
        Klage klage = Klage.ny()
                .medPåklagdBehandlingType(behandlingType)
                .medKlageVurderingResultatNK(klageVurderingResultat)
                .medFormkravNFP(klageFormkravResultat)
                .build();
        when(domeneobjektProvider.hentKlagebehandling(behandling)).thenReturn(klage);
    }
}
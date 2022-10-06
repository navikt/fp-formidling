package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentData;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseSVPBuilder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.standardBehandling;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.standardDokumenthendelse;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Period;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentData;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ExtendWith(MockitoExtension.class)
class EtterlysInntektsmeldingDokumentdataMapperTest {

    @Mock
    private DomeneobjektProvider domeneobjektProvider;

    private DokumentData dokumentData;

    private EtterlysInntektsmeldingDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    void setup() {
        var brevParametere = new BrevParametere(6, 2, Period.parse("P3W"), Period.ZERO);
        dokumentData = lagStandardDokumentData(DokumentMalType.ETTERLYS_INNTEKTSMELDING);
        dokumentdataMapper = new EtterlysInntektsmeldingDokumentdataMapper(domeneobjektProvider, new BrevMapperUtil(brevParametere));
    }

    @Test
    void test_map_fagtype_foreldrepenger() {
        var behandling = standardBehandling();
        var dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        var dokumentHendelse = standardDokumenthendelse();

        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo(FagsakYtelseType.FORELDREPENGER.getKode());
        assertThat(dokumentdata.getSøknadDato()).isNotEmpty();
        assertThat(dokumentdata.getFristDato()).isNotEmpty();
    }

    @Test
    void test_map_fagtype_svangerskapspenger() {
        var behandling = standardBehandling();
        var dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        var dokumentHendelse = lagStandardHendelseSVPBuilder().build();

        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo(FagsakYtelseType.SVANGERSKAPSPENGER.getKode());
        assertThat(dokumentdata.getSøknadDato()).isNotEmpty();
        assertThat(dokumentdata.getFristDato()).isNotEmpty();
    }

}

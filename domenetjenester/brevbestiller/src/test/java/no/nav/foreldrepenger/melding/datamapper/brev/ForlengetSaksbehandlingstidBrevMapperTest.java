package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.BEHANDLINGSFRIST;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil;
import no.nav.foreldrepenger.melding.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepositoryImpl;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.forlenget.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.forlenget.PersonstatusKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.forlenget.VariantKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.forlenget.YtelseTypeKode;

public class ForlengetSaksbehandlingstidBrevMapperTest {

    private static final long ID = 123L;
    @Rule
    public UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    private DokumentRepository dokumentRepository = new DokumentRepositoryImpl(repoRule.getEntityManager());
    private ForlengetSaksbehandlingstidBrevMapper forlengetSaksbehandlingstidBrevMapper = new ForlengetSaksbehandlingstidBrevMapper();
    private Behandling behandling = DatamapperTestUtil.standardBehandling();
    private DokumentHendelse dokumentHendelse;
    private DokumentFelles dokumentFelles = DatamapperTestUtil.getDokumentFelles();

    private DokumentHendelse byggHendelse(String mal, FagsakYtelseType ytelseType) {
        return DokumentHendelse.builder()
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .medYtelseType(ytelseType)
                .medDokumentMalType(dokumentRepository.hentDokumentMalType(mal))
                .build();
    }

    @Before
    public void setup() {
        dokumentHendelse = byggHendelse(DokumentMalType.FORLENGET_OPPTJENING, FagsakYtelseType.FORELDREPENGER);
    }

    @Test
    public void skal_mappe_forlengelse_brev() {
        FagType fagType = forlengetSaksbehandlingstidBrevMapper.mapFagType(dokumentHendelse, behandling, dokumentFelles);
        assertThat(fagType.getYtelseType()).isEqualTo(YtelseTypeKode.FP);
        assertThat(fagType.getBehandlingsfristUker()).isEqualTo(BEHANDLINGSFRIST);
        assertThat(fagType.getVariant()).isEqualTo(VariantKode.OPPTJENING);
        assertThat(fagType.getPersonstatus()).isEqualTo(PersonstatusKode.ANNET);
        assertThat(fagType.getSokersNavn()).isEqualTo(dokumentFelles.getSakspartNavn());

    }


    @Test
    public void skal_mappe_forlengelse_brev_variant() {
        dokumentHendelse = byggHendelse(DokumentMalType.FORLENGET_DOK, FagsakYtelseType.ENGANGSTØNAD);
        FagType fagType = forlengetSaksbehandlingstidBrevMapper.mapFagType(dokumentHendelse, behandling, dokumentFelles);
        assertThat(fagType.getYtelseType()).isEqualTo(YtelseTypeKode.ES);
        assertThat(fagType.getBehandlingsfristUker()).isEqualTo(BEHANDLINGSFRIST);
        assertThat(fagType.getVariant()).isEqualTo(VariantKode.FORLENGET);
    }


}

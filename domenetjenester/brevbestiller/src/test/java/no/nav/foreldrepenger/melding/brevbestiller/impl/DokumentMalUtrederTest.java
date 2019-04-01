package no.nav.foreldrepenger.melding.brevbestiller.impl;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import no.nav.foreldrepenger.melding.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepositoryImpl;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkTabellRepository;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkTabellRepositoryImpl;

public class DokumentMalUtrederTest {

    @Rule
    public UnittestRepositoryRule repositoryRule = new UnittestRepositoryRule();

    private KodeverkTabellRepository kodeverkTabellRepository = new KodeverkTabellRepositoryImpl(repositoryRule.getEntityManager());
    private DokumentRepository dokumentRepository;


    private DokumentMalUtreder dokumentMalUtreder;
    private DokumentHendelse hendelse;

    @Before
    public void setup() {
        dokumentRepository = new DokumentRepositoryImpl(repositoryRule.getEntityManager());
        dokumentMalUtreder = new DokumentMalUtreder(kodeverkTabellRepository, null);
        hendelse = DokumentHendelse.builder()
                .medBehandlingId(123L)
                .medYtelseType(FagsakYtelseType.FORELDREPENGER)
                .medDokumentMalType(dokumentRepository.hentDokumentMalType(DokumentMalType.UENDRETUTFALL_DOK))
                .build();
    }

    @Test
    public void utred_fra_input_mal() {
        assertThat(dokumentMalUtreder.utredDokumentmal(null, hendelse).getKode()).isEqualTo(DokumentMalType.UENDRETUTFALL_DOK);
    }
}

package no.nav.foreldrepenger.melding.brevbestiller.impl;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import no.nav.foreldrepenger.melding.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse.DokumentHendelseDto;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkTabellRepository;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkTabellRepositoryImpl;

public class DokumentMalUtrederTest {

    @Rule
    public UnittestRepositoryRule repositoryRule = new UnittestRepositoryRule();

    private KodeverkTabellRepository kodeverkTabellRepository = new KodeverkTabellRepositoryImpl(repositoryRule.getEntityManager());


    private DokumentMalUtreder dokumentMalUtreder;
    private DokumentHendelseDto dto;

    @Before
    public void setup() {
        dokumentMalUtreder = new DokumentMalUtreder(kodeverkTabellRepository);
        dto = new DokumentHendelseDto();
    }

    @Test
    public void utred_fra_input_mal() {
        dto.setDokumentMal(DokumentMalType.UENDRETUTFALL_DOK);
        assertThat(dokumentMalUtreder.utredDokumentmal(null, dto).getKode()).isEqualTo(DokumentMalType.UENDRETUTFALL_DOK);
    }
}
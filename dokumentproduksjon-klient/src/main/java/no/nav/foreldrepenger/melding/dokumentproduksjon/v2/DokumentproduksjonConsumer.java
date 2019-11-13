package no.nav.foreldrepenger.melding.dokumentproduksjon.v2;

import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.FerdigstillForsendelseDokumentUnderRedigering;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.FerdigstillForsendelseJournalpostIkkeFunnet;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.FerdigstillForsendelseJournalpostIkkeUnderArbeid;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.KnyttVedleggTilForsendelseDokumentIkkeFunnet;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.KnyttVedleggTilForsendelseDokumentTillatesIkkeGjenbrukt;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.KnyttVedleggTilForsendelseEksterntVedleggIkkeTillatt;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.KnyttVedleggTilForsendelseJournalpostIkkeFerdigstilt;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.KnyttVedleggTilForsendelseJournalpostIkkeFunnet;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.KnyttVedleggTilForsendelseJournalpostIkkeUnderArbeid;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.KnyttVedleggTilForsendelseUlikeFagomraader;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.ProduserIkkeredigerbartDokumentDokumentErRedigerbart;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.binding.ProduserIkkeredigerbartDokumentDokumentErVedlegg;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.FerdigstillForsendelseRequest;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.KnyttVedleggTilForsendelseRequest;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.ProduserDokumentutkastRequest;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.ProduserDokumentutkastResponse;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.ProduserIkkeredigerbartDokumentRequest;
import no.nav.tjeneste.virksomhet.dokumentproduksjon.v2.meldinger.ProduserIkkeredigerbartDokumentResponse;

public interface DokumentproduksjonConsumer {
    ProduserIkkeredigerbartDokumentResponse produserIkkeredigerbartDokument(ProduserIkkeredigerbartDokumentRequest request)
            throws ProduserIkkeredigerbartDokumentDokumentErRedigerbart, ProduserIkkeredigerbartDokumentDokumentErVedlegg;

    ProduserDokumentutkastResponse produserDokumentutkast(ProduserDokumentutkastRequest request);

    void knyttVedleggTilForsendelse(KnyttVedleggTilForsendelseRequest request)
            throws KnyttVedleggTilForsendelseDokumentIkkeFunnet,
            KnyttVedleggTilForsendelseEksterntVedleggIkkeTillatt,
            KnyttVedleggTilForsendelseJournalpostIkkeFunnet,
            KnyttVedleggTilForsendelseJournalpostIkkeUnderArbeid,
            KnyttVedleggTilForsendelseDokumentTillatesIkkeGjenbrukt,
            KnyttVedleggTilForsendelseUlikeFagomraader,
            KnyttVedleggTilForsendelseJournalpostIkkeFerdigstilt;

    void ferdigstillForsendelse(FerdigstillForsendelseRequest request)
            throws FerdigstillForsendelseDokumentUnderRedigering,
            FerdigstillForsendelseJournalpostIkkeFunnet,
            FerdigstillForsendelseJournalpostIkkeUnderArbeid;
}

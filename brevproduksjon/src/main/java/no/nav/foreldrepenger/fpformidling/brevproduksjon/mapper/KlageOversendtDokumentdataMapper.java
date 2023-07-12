package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto.fra;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;

import java.time.LocalDate;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.KlageOversendtDokumentdata;
import no.nav.foreldrepenger.fpformidling.klage.KlageDokument;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalTypeKode;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.KLAGE_OVERSENDT)
public class KlageOversendtDokumentdataMapper implements DokumentdataMapper {

    private DomeneobjektProvider domeneobjektProvider;

    KlageOversendtDokumentdataMapper() {
        //CDI
    }

    @Inject
    public KlageOversendtDokumentdataMapper(DomeneobjektProvider domeneobjektProvider) {
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String getTemplateNavn() {
        return "klage-oversendt";
    }

    @Override
    public KlageOversendtDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                         DokumentHendelse hendelse,
                                                         Behandling behandling,
                                                         boolean erUtkast) {
        var klageDokument = domeneobjektProvider.hentKlageDokument(behandling);
        var klage = domeneobjektProvider.hentKlagebehandling(behandling);

        var fellesBuilder = opprettFellesBuilder(dokumentFelles, hendelse, behandling, erUtkast);
        // fpsak sender NKS som behandlende enhet - dette fører til at avsender blir satt feil til NKS.
        fellesBuilder.medBehandlesAvKA(false);

        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDatoNorsk(dokumentFelles.getDokumentDato()) : null);
        fra(hendelse, klage).ifPresent(fellesBuilder::medFritekst);

        var dokumentdataBuilder = KlageOversendtDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medGjelderTilbakekreving(klage.getPåklagdBehandlingType().erTilbakekrevingBehandlingType())
            .medMottattDato(formaterDatoNorsk(utledMottattDato(klageDokument, behandling)));

        return dokumentdataBuilder.build();
    }

    private LocalDate utledMottattDato(KlageDokument klageDokument, Behandling behandling) {
        return klageDokument.mottattDato() != null ? klageDokument.mottattDato() : behandling.getOpprettetDato().toLocalDate();
    }
}

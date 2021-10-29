package no.nav.foreldrepenger.melding.brevmapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDatoNorsk;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.KlageMapper;
import no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.KlageHjemsendtDokumentdata;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.KLAGE_HJEMSENDT)
public class KlageHjemsendtDokumentdataMapper implements DokumentdataMapper {

    private BrevMapperUtil brevMapperUtil;
    private DomeneobjektProvider domeneobjektProvider;

    KlageHjemsendtDokumentdataMapper() {
        //CDI
    }

    @Inject
    public KlageHjemsendtDokumentdataMapper(BrevMapperUtil brevMapperUtil, DomeneobjektProvider domeneobjektProvider) {
        this.brevMapperUtil = brevMapperUtil;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String getTemplateNavn() {
        return "klage-hjemsendt";
    }

    @Override
    public KlageHjemsendtDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse,
                                                         Behandling behandling, boolean erUtkast) {
        Klage klage = domeneobjektProvider.hentKlagebehandling(behandling);

        var fellesBuilder = opprettFellesBuilder(dokumentFelles, hendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDatoNorsk(dokumentFelles.getDokumentDato()) : null);
        KlageMapper.avklarFritekstKlage(hendelse, klage).ifPresent(fellesBuilder::medFritekst);

        var dokumentdataBuilder = KlageHjemsendtDokumentdata.ny()
                .medFelles(fellesBuilder.build())
                .medGjelderTilbakekreving(klage.getPåklagdBehandlingType().erTilbakekrevingBehandlingType())
                .medOpphevet(KlageMapper.erOpphevet(klage, hendelse))
                .medEttersendelsesfrist(formaterDatoNorsk(brevMapperUtil.getSvarFrist()));

        return dokumentdataBuilder.build();
    }
}

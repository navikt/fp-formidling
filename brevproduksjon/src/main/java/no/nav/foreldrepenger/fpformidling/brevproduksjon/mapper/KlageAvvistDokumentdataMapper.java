package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto.fra;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;

import java.util.Set;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.KlageMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.KlageAvvistDokumentdata;
import no.nav.foreldrepenger.fpformidling.klage.Klage;
import no.nav.foreldrepenger.fpformidling.klage.KlageAvvistÅrsak;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalTypeKode;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.KLAGE_AVVIST)
public class KlageAvvistDokumentdataMapper implements DokumentdataMapper {

    private BrevParametere brevParametere;
    private DomeneobjektProvider domeneobjektProvider;

    KlageAvvistDokumentdataMapper() {
        //CDI
    }

    @Inject
    public KlageAvvistDokumentdataMapper(BrevParametere brevParametere, DomeneobjektProvider domeneobjektProvider) {
        this.brevParametere = brevParametere;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String getTemplateNavn() {
        return "klage-avvist";
    }

    @Override
    public KlageAvvistDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                      DokumentHendelse hendelse,
                                                      Behandling behandling,
                                                      boolean erUtkast) {
        var klage = domeneobjektProvider.hentKlagebehandling(behandling);
        var fellesBuilder = opprettFellesBuilder(dokumentFelles, hendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDatoNorsk(dokumentFelles.getDokumentDato()) : null);
        fra(hendelse, klage).ifPresent(fellesBuilder::medFritekst);

        var avvistGrunner = getAvvistGrunner(klage);

        var dokumentdataBuilder = KlageAvvistDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medGjelderTilbakekreving(klage.getPåklagdBehandlingType().erTilbakekrevingBehandlingType())
            .medLovhjemler(getLovhjemler(behandling, klage))
            .medKlagefristUker(brevParametere.getKlagefristUker())
            .medAvvistGrunner(avvistGrunner);

        return dokumentdataBuilder.build();
    }

    private String getLovhjemler(Behandling behandling, Klage klage) {
        var lovhjemler = KlageMapper.hentOgFormaterLovhjemlerForAvvistKlage(klage, behandling.getSpråkkode());
        return lovhjemler.map(String::toString).orElse(null);
    }

    private Set<String> getAvvistGrunner(Klage klage) {
        return KlageMapper.listeAvAvvisteÅrsaker(klage)
            .stream()
            .map(å -> KlageAvvistÅrsak.IKKE_SIGNERT.equals(å) ? KlageAvvistÅrsak.KLAGE_UGYLDIG.getKode() : å.getKode())
            .collect(Collectors.toSet());
    }
}

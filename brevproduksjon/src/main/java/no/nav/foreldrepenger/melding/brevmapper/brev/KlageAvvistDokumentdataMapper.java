package no.nav.foreldrepenger.melding.brevmapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDatoNorsk;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.KlageMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.KlageAvvistDokumentdata;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.klage.KlageAvvistÅrsak;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;

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
    public KlageAvvistDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse,
                                                      Behandling behandling, boolean erUtkast) {
        var fellesBuilder = opprettFellesBuilder(dokumentFelles, hendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDatoNorsk(dokumentFelles.getDokumentDato()) : null);

        Klage klage = domeneobjektProvider.hentKlagebehandling(behandling);
        Set<String> avvistGrunner = getAvvistGrunner(klage);

        var dokumentdataBuilder = KlageAvvistDokumentdata.ny()
                .medFelles(fellesBuilder.build())
                .medGjelderTilbakekreving(klage.getPåklagdBehandlingType().erTilbakekrevingBehandlingType())
                .medLovhjemler(getLovhjemler(behandling, klage))
                .medKlagefristUker(brevParametere.getKlagefristUker())
                .medAvvistGrunner(avvistGrunner);

        return dokumentdataBuilder.build();
    }

    private String getLovhjemler(Behandling behandling, Klage klage) {
        Optional<String> lovhjemler = KlageMapper.hentOgFormaterLovhjemlerForAvvistKlage(klage, behandling.getSpråkkode());
        return lovhjemler.map(String::toString).orElse(null);
    }

    private Set<String> getAvvistGrunner(Klage klage) {
        return KlageMapper.listeAvAvvisteÅrsaker(klage).stream()
                .map(å -> KlageAvvistÅrsak.IKKE_SIGNERT.equals(å) ? KlageAvvistÅrsak.KLAGE_UGYLDIG.getKode() : å.getKode())
                .collect(Collectors.toSet());
    }
}

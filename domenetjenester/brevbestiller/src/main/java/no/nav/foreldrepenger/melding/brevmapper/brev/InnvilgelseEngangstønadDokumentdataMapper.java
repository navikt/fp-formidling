package no.nav.foreldrepenger.melding.brevmapper.brev;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatES;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.Dokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.EngangsstønadInnvilgelseDokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.FellesDokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokument.forlenget.PersonstatusKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.BehandlingsTypeType;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.INNVILGELSE_ENGANGSSTØNAD)
public class InnvilgelseEngangstønadDokumentdataMapper implements DokumentdataMapper {

    private BrevParametere brevParametere;
    private DomeneobjektProvider domeneobjektProvider;

    InnvilgelseEngangstønadDokumentdataMapper() {
        //CDI
    }

    @Inject
    public InnvilgelseEngangstønadDokumentdataMapper(BrevParametere brevParametere, DomeneobjektProvider domeneobjektProvider) {
        this.brevParametere = brevParametere;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String getTemplateNavn() {
        return "engangsstonad-innvilgelse";
    }

    @Override
    public Dokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse, Behandling behandling) {
        BeregningsresultatES beregningsresultat = domeneobjektProvider.hentBeregningsresultatES(behandling);

        FellesDokumentdata felles = new FellesDokumentdata.Builder()
                .søkerNavn(dokumentFelles.getMottakerNavn())
                .søkerPersonnummer(dokumentFelles.getMottakerId())
                .brevDato(dokumentFelles.getDokumentDato())
                .build();

        return new EngangsstønadInnvilgelseDokumentdata.Builder()
                .felles(felles)
                .revurdering(behandling.erRevurdering())
                .førstegangsbehandling(behandling.erFørstegangssøknad())
                .medhold(BehandlingMapper.erMedhold(behandling))
                .innvilgetBeløp(beregningsresultat.getBeløp())
                .klagefristUker(brevParametere.getKlagefristUker())
                .død(erDød(dokumentFelles))
                .fbEllerMedhold(erFBellerMedhold(behandling))
                .kontaktTelefonnummer(dokumentFelles.getKontaktTlf())
                .endretSats(0)
                .build();
    }

    private boolean erDød(DokumentFelles dokumentFelles) {
        return dokumentFelles.getSakspartPersonStatus().equalsIgnoreCase(PersonstatusKode.DOD.toString());
    }

    private boolean erFBellerMedhold(Behandling behandling) {
        return BehandlingMapper.utledBehandlingsTypeInnvilgetES(behandling).equals(BehandlingsTypeType.FOERSTEGANGSBEHANDLING)
                || BehandlingMapper.utledBehandlingsTypeInnvilgetES(behandling).equals(BehandlingsTypeType.MEDHOLD);
    }
}

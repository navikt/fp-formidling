package no.nav.foreldrepenger.melding.brevmapper.brev;

import static no.nav.foreldrepenger.melding.typer.Dato.formaterDato;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatES;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.EngangsstønadInnvilgelseDokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.FellesDokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokument.forlenget.PersonstatusKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.BehandlingsTypeType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.INNVILGELSE_ENGANGSSTØNAD)
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
    public EngangsstønadInnvilgelseDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse, Behandling behandling) {
        BeregningsresultatES beregningsresultat = domeneobjektProvider.hentBeregningsresultatES(behandling);

        FellesDokumentdata felles = new FellesDokumentdata.Builder()
                .søkerNavn(dokumentFelles.getMottakerNavn())
                .søkerPersonnummer(dokumentFelles.getMottakerId())
                .brevDato(dokumentFelles.getDokumentDato()!= null ? formaterDato(dokumentFelles.getDokumentDato()) : null)
                .erAutomatiskBehandlet(dokumentFelles.getAutomatiskBehandlet())
                .harVerge(dokumentFelles.getErKopi().isPresent())
                .erKopi(dokumentFelles.getErKopi().isPresent() && erKopi(dokumentFelles.getErKopi().get()))
                .build();

        var innvilgelseDokumentDataBuilder = EngangsstønadInnvilgelseDokumentdata.ny()
                .medFelles(felles)
                .medRevurdering(behandling.erRevurdering())
                .medFørstegangsbehandling(behandling.erFørstegangssøknad())
                .medMedhold(BehandlingMapper.erMedhold(behandling))
                .medInnvilgetBeløp(beregningsresultat.getBeløp())
                .medKlagefristUker(brevParametere.getKlagefristUker())
                .medDød(erDød(dokumentFelles))
                .medFbEllerMedhold(erFBellerMedhold(behandling))
                .medErEndretSats(false);

        if (behandling.erRevurdering()) {
            Long differanse = sjekkOmDifferanseHvisRevurdering(behandling, beregningsresultat);

            if (differanse != 0) {
                innvilgelseDokumentDataBuilder.medErEndretSats(true);
                innvilgelseDokumentDataBuilder.medInnvilgetBeløp(differanse);
            }
        }

        return innvilgelseDokumentDataBuilder.build();
    }

    private boolean erDød(DokumentFelles dokumentFelles) {
        return PersonstatusKode.DOD.toString().equalsIgnoreCase(dokumentFelles.getSakspartPersonStatus());
    }

    private boolean erFBellerMedhold(Behandling behandling) {
        return BehandlingMapper.utledBehandlingsTypeInnvilgetES(behandling).equals(BehandlingsTypeType.FOERSTEGANGSBEHANDLING)
                || BehandlingMapper.utledBehandlingsTypeInnvilgetES(behandling).equals(BehandlingsTypeType.MEDHOLD);
    }

    private Long sjekkOmDifferanseHvisRevurdering(Behandling behandling, BeregningsresultatES beregningsresultat) {
        Optional<Behandling> originalBehandling = domeneobjektProvider.hentOriginalBehandlingHvisFinnes(behandling);
        Optional<BeregningsresultatES> originaltBeregningsresultat = originalBehandling.map(domeneobjektProvider::hentBeregningsresultatESHvisFinnes)
                .orElseThrow(() -> new IllegalArgumentException("Utviklerfeil:Finner ikke informasjon om orginal behandling for revurdering "));
        return originaltBeregningsresultat.map(orgBeregningsresultat -> Math.abs(beregningsresultat.getBeløp() - orgBeregningsresultat.getBeløp()))
                .orElse(0L);
    }

    private boolean erKopi(DokumentFelles.Kopi kopi) {
        return DokumentFelles.Kopi.JA.equals(kopi);
    }
}

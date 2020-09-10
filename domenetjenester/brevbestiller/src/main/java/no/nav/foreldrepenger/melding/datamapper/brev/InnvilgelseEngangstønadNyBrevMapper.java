package no.nav.foreldrepenger.melding.datamapper.brev;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatES;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.forlenget.PersonstatusKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.BehandlingsTypeType;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;

@ApplicationScoped
@Named(DokumentMalType.INNVILGELSE_ENGANGSSTØNAD)
public class InnvilgelseEngangstønadNyBrevMapper extends FritekstmalBrevMapper {

    public InnvilgelseEngangstønadNyBrevMapper() {
        //CDI
    }

    @Inject
    public InnvilgelseEngangstønadNyBrevMapper(BrevParametere brevParametere,
                                               DomeneobjektProvider domeneobjektProvider) {
        super(brevParametere, domeneobjektProvider);
    }
    @Override
    public String displayName() {
        return "Innvilget engangsstønad";
    }

    @Override
    public String templateFolder() {
        return "innvilgelseengangsstønad";
    }

    @Override
    Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling) {
        Brevdata brevdata = new Brevdata();
        BeregningsresultatES beregningsresultat = domeneobjektProvider.hentBeregningsresultatES(behandling);

        brevdata.leggTil("revurdering",(behandling.erRevurdering()))
                .leggTil("førstegangsBehandling",(behandling.erFørstegangssøknad()))
                .leggTil("medhold", BehandlingMapper.erMedhold(behandling))
                .leggTil("innvilgetbeløp",beregningsresultat.getBeløp())
                .leggTil("klageFristUker", brevParametere.getKlagefristUker())
                .leggTil("død", erDød())
                .leggTil("søkersNavn", dokumentFelles.getSakspartNavn())
                .leggTil("fbEllerMedhold", (erFBellerMedhold(behandling)))
                .leggTil("kontaktTelefonnummer", dokumentFelles.getKontaktTlf());

        if (behandling.erRevurdering()) {
            Optional <Behandling> originalBehandling = domeneobjektProvider.hentOriginalBehandlingHvisFinnes(behandling);
            BeregningsresultatES originaltBeregningsresultat = originalBehandling.map(domeneobjektProvider::hentBeregningsresultatES).orElse(null);

            brevdata.leggTil("endretSats", (originaltBeregningsresultat !=null ? (float) Math.abs(beregningsresultat.getBeløp() - originaltBeregningsresultat.getBeløp()) : null));
        }
        return brevdata;
    }

    private boolean erDød() {
        return dokumentFelles.getSakspartPersonStatus().equalsIgnoreCase(PersonstatusKode.DOD.toString());
    }

    private boolean erFBellerMedhold(Behandling behandling) {
        return BehandlingMapper.utledBehandlingsTypeInnvilgetES(behandling).equals(BehandlingsTypeType.FOERSTEGANGSBEHANDLING)
                || BehandlingMapper.utledBehandlingsTypeInnvilgetES(behandling).equals(BehandlingsTypeType.MEDHOLD);
    }
}

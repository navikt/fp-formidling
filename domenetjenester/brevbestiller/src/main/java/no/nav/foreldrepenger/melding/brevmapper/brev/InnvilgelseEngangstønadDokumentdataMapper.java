package no.nav.foreldrepenger.melding.brevmapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.brevSendesTilVerge;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.erDød;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.erKopi;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.formaterBeløp;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.formaterPersonnummer;
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
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.EngangsstønadInnvilgelseDokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.FellesDokumentdata;
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

        var fellesbuilder = FellesDokumentdata.ny()
                .medSøkerNavn(dokumentFelles.getSakspartNavn())
                .medSøkerPersonnummer(formaterPersonnummer(dokumentFelles.getSakspartId()))
                .medBrevDato(dokumentFelles.getDokumentDato()!= null ? formaterDato(dokumentFelles.getDokumentDato()) : null)
                .medErAutomatiskBehandlet(dokumentFelles.getAutomatiskBehandlet())
                .medHarVerge(dokumentFelles.getErKopi().isPresent())
                .medErKopi(dokumentFelles.getErKopi().isPresent() && erKopi(dokumentFelles.getErKopi().get()))
                .medSaksnummer(dokumentFelles.getSaksnummer().getVerdi());

        if (brevSendesTilVerge(dokumentFelles)) {
            fellesbuilder.medMottakerNavn(dokumentFelles.getMottakerNavn());
        }

        var innvilgelseDokumentDataBuilder = EngangsstønadInnvilgelseDokumentdata.ny()
                .medFelles(fellesbuilder.build())
                .medRevurdering(behandling.erRevurdering())
                .medFørstegangsbehandling(behandling.erFørstegangssøknad())
                .medMedhold(BehandlingMapper.erMedhold(behandling))
                .medInnvilgetBeløp(formaterBeløp(beregningsresultat.getBeløp()))
                .medKlagefristUker(brevParametere.getKlagefristUker())
                .medDød(erDød(dokumentFelles))
                .medFbEllerMedhold(erFBellerMedhold(behandling))
                .medErEndretSats(false);

        if (behandling.erRevurdering()) {
            Behandling originalBehandling = domeneobjektProvider.hentOriginalBehandlingHvisFinnes(behandling)
                    .orElseThrow(()-> new IllegalArgumentException("Utviklerfeil:Finner ikke informasjon om orginal behandling for revurdering "));

            Long differanse = sjekkOmDifferanseHvisRevurdering(originalBehandling, beregningsresultat);

            if (differanse != 0L) {
                FamilieHendelse famHendelse = domeneobjektProvider.hentFamiliehendelse(behandling);
                FamilieHendelse orgFamHendelse = domeneobjektProvider.hentFamiliehendelse(originalBehandling);
                //dersom årsaken til differanse er økning av antall barn er det ikke endret sats
                if (!antallBarnEndret(famHendelse, orgFamHendelse)) {
                    innvilgelseDokumentDataBuilder.medErEndretSats(true);
                    innvilgelseDokumentDataBuilder.medInnvilgetBeløp(formaterBeløp(differanse));
                } else {
                    innvilgelseDokumentDataBuilder.medInnvilgetBeløp(formaterBeløp(differanse));
                }
            }
        }
        return innvilgelseDokumentDataBuilder.build();
    }

    private boolean erFBellerMedhold(Behandling behandling) {
        return BehandlingMapper.utledBehandlingsTypeInnvilgetES(behandling).equals(BehandlingsTypeType.FOERSTEGANGSBEHANDLING)
                || BehandlingMapper.utledBehandlingsTypeInnvilgetES(behandling).equals(BehandlingsTypeType.MEDHOLD);
    }

    private Long sjekkOmDifferanseHvisRevurdering(Behandling originalBehandling, BeregningsresultatES beregningsresultat) {
        Optional<BeregningsresultatES> originaltBeregningsresultat = domeneobjektProvider.hentBeregningsresultatESHvisFinnes(originalBehandling);

        return originaltBeregningsresultat.map(orgBeregningsresultat -> Math.abs(beregningsresultat.getBeløp() - orgBeregningsresultat.getBeløp()))
                .orElse(0L);
    }

    private boolean antallBarnEndret(FamilieHendelse famHendelse, FamilieHendelse orgFamhendelse) {
        return famHendelse.getAntallBarn().intValue()!= orgFamhendelse.getAntallBarn().intValue();
    }
}

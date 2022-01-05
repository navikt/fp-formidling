package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.erDød;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterBeløp;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.beregning.BeregningsresultatES;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BehandlingMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.EngangsstønadInnvilgelseDokumentdata;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingÅrsakType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalTypeKode;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.ENGANGSSTØNAD_INNVILGELSE)
public class EngangsstønadInnvilgelseDokumentdataMapper implements DokumentdataMapper {

    private BrevParametere brevParametere;
    private DomeneobjektProvider domeneobjektProvider;

    EngangsstønadInnvilgelseDokumentdataMapper() {
        //CDI
    }

    @Inject
    public EngangsstønadInnvilgelseDokumentdataMapper(BrevParametere brevParametere, DomeneobjektProvider domeneobjektProvider) {
        this.brevParametere = brevParametere;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String getTemplateNavn() {
        return "engangsstonad-innvilgelse";
    }

    @Override
    public EngangsstønadInnvilgelseDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse,
                                                                   Behandling behandling, boolean erUtkast) {
        BeregningsresultatES beregningsresultat = domeneobjektProvider.hentBeregningsresultatES(behandling);

        var fellesBuilder = opprettFellesBuilder(dokumentFelles, hendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), behandling.getSpråkkode()) : null);
        fellesBuilder.medErAutomatiskBehandlet(dokumentFelles.getAutomatiskBehandlet());

        var dokumentdataBuilder = EngangsstønadInnvilgelseDokumentdata.ny()
                .medFelles(fellesBuilder.build())
                .medRevurdering(behandling.erRevurdering())
                .medFørstegangsbehandling(behandling.erFørstegangssøknad())
                .medMedhold(BehandlingMapper.erMedhold(behandling))
                .medInnvilgetBeløp(formaterBeløp(beregningsresultat.beløp()))
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
                    dokumentdataBuilder.medErEndretSats(true);
                    dokumentdataBuilder.medInnvilgetBeløp(formaterBeløp(differanse));
                } else {
                    dokumentdataBuilder.medInnvilgetBeløp(formaterBeløp(differanse));
                }
            }
        }
        return dokumentdataBuilder.build();
    }

    private boolean erFBellerMedhold(Behandling behandling) {
        return !BehandlingType.REVURDERING.equals(behandling.getBehandlingType())
            || BehandlingÅrsakType.årsakerEtterKlageBehandling().stream().anyMatch(behandling::harBehandlingÅrsak);
    }

    private Long sjekkOmDifferanseHvisRevurdering(Behandling originalBehandling, BeregningsresultatES beregningsresultat) {
        Optional<BeregningsresultatES> originaltBeregningsresultat = domeneobjektProvider.hentBeregningsresultatESHvisFinnes(originalBehandling);

        return originaltBeregningsresultat.map(orgBeregningsresultat -> Math.abs(beregningsresultat.beløp() - orgBeregningsresultat.beløp()))
                .orElse(0L);
    }

    private boolean antallBarnEndret(FamilieHendelse famHendelse, FamilieHendelse orgFamhendelse) {
        return famHendelse.getAntallBarn().intValue()!= orgFamhendelse.getAntallBarn().intValue();
    }
}

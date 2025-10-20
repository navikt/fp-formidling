package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BehandlingMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.EngangsstønadInnvilgelseDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.KodeverkMapper;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingÅrsakType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseEngangsstønadDto;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.ENGANGSSTØNAD_INNVILGELSE)
public class EngangsstønadInnvilgelseDokumentdataMapper implements DokumentdataMapper {

    private final BrevParametere brevParametere;

    @Inject
    public EngangsstønadInnvilgelseDokumentdataMapper(BrevParametere brevParametere) {
        this.brevParametere = brevParametere;
    }

    @Override
    public String getTemplateNavn() {
        return "engangsstonad-innvilgelse";
    }

    @Override
    public EngangsstønadInnvilgelseDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                                   DokumentHendelse hendelse,
                                                                   BrevGrunnlagDto behandling,
                                                                   boolean erUtkast) {
        var tilkjentYtelse = behandling.tilkjentYtelse().engangsstønad();

        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, erUtkast);
        fellesBuilder.medBrevDato(
            dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), dokumentFelles.getSpråkkode()) : null);
        fellesBuilder.medErAutomatiskBehandlet(dokumentFelles.getAutomatiskBehandlet());

        var dokumentdataBuilder = EngangsstønadInnvilgelseDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medRevurdering(behandling.erRevurdering())
            .medFørstegangsbehandling(behandling.erFørstegangssøknad())
            .medMedhold(BehandlingMapper.erMedhold(behandling))
            .medInnvilgetBeløp(BrevMapperUtil.formaterBeløp(tilkjentYtelse.beregnetTilkjentYtelse()))
            .medKlagefristUker(brevParametere.getKlagefristUker())
            .medDød(BrevMapperUtil.erDød(dokumentFelles))
            .medFbEllerMedhold(erFBellerMedhold(behandling))
            .medErEndretSats(false);

        if (behandling.erRevurdering()) {
            var differanse = sjekkOmDifferanseHvisRevurdering(behandling.tilkjentYtelse().originalBehandlingEngangsstønad(), tilkjentYtelse);

            if (differanse != 0L) {
                var famHendelse = behandling.familieHendelse();
                var orgFamHendelse = behandling.originalBehandling().familieHendelse();
                //dersom årsaken til differanse er økning av antall barn er det ikke endret sats
                if (!antallBarnEndret(famHendelse, orgFamHendelse)) {
                    dokumentdataBuilder.medErEndretSats(true);
                    dokumentdataBuilder.medInnvilgetBeløp(BrevMapperUtil.formaterBeløp(differanse));
                } else {
                    dokumentdataBuilder.medInnvilgetBeløp(BrevMapperUtil.formaterBeløp(differanse));
                }
            }
        }
        return dokumentdataBuilder.build();
    }

    private boolean erFBellerMedhold(BrevGrunnlagDto behandling) {
        return !behandling.erRevurdering() || BehandlingÅrsakType.årsakerEtterKlageBehandling()
            .stream()
            .map(KodeverkMapper::mapBehandlingÅrsak)
            .anyMatch(behandling::harBehandlingÅrsak);
    }

    private Long sjekkOmDifferanseHvisRevurdering(TilkjentYtelseEngangsstønadDto originaltTilkjentYtelse,
                                                  TilkjentYtelseEngangsstønadDto tilkjentYtelse) {
        return Optional.ofNullable(originaltTilkjentYtelse)
            .map(orgTilkjentYtelse -> Math.abs(tilkjentYtelse.beregnetTilkjentYtelse() - orgTilkjentYtelse.beregnetTilkjentYtelse()))
            .orElse(0L);
    }

    private boolean antallBarnEndret(BrevGrunnlagDto.FamilieHendelse famHendelse, BrevGrunnlagDto.FamilieHendelse orgFamhendelse) {
        return famHendelse.antallBarn() != orgFamhendelse.antallBarn();
    }
}

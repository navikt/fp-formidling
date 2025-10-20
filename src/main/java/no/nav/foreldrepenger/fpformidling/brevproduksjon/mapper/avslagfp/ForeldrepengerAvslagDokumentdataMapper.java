package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.avslagfp;

import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Foreldrepenger;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.RelasjonsRolleType;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.TilkjentYtelse;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BeregningsgrunnlagMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.avslagfp.ForeldrepengerAvslagDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.FORELDREPENGER_AVSLAG)
public class ForeldrepengerAvslagDokumentdataMapper implements DokumentdataMapper {

    private static final Map<RelasjonsRolleType, String> RELASJONSKODE_TYPE_MAP;

    static {
        RELASJONSKODE_TYPE_MAP = new EnumMap<>(RelasjonsRolleType.class);
        RELASJONSKODE_TYPE_MAP.put(RelasjonsRolleType.MORA, "MOR");
        RELASJONSKODE_TYPE_MAP.put(RelasjonsRolleType.FARA, "FAR");
        RELASJONSKODE_TYPE_MAP.put(RelasjonsRolleType.MEDMOR, "MEDMOR");
    }

    private final BrevParametere brevParametere;

    @Inject
    public ForeldrepengerAvslagDokumentdataMapper(BrevParametere brevParametere) {
        this.brevParametere = brevParametere;
    }

    @Override
    public String getTemplateNavn() {
        return "foreldrepenger-avslag";
    }

    @Override
    public ForeldrepengerAvslagDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                               DokumentHendelse dokumentHendelse,
                                                               BrevGrunnlagDto behandling,
                                                               boolean erUtkast) {

        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, erUtkast);
        var språkkode = dokumentFelles.getSpråkkode();
        fellesBuilder.medBrevDato(
            dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), språkkode) : null);
        fellesBuilder.medErAutomatiskBehandlet(dokumentFelles.getAutomatiskBehandlet());
        FritekstDto.fraFritekst(dokumentHendelse, behandling.behandlingsresultat().fritekst()).ifPresent(fellesBuilder::medFritekst);

        var familiehendelse = behandling.familieHendelse();
        var beregningsgrunnlagOpt = Optional.ofNullable(behandling.beregningsgrunnlag());
        var halvG = BeregningsgrunnlagMapper.getHalvGOrElseZero(beregningsgrunnlagOpt);
        var uttakResultatPerioder = Optional.ofNullable(behandling.foreldrepenger());

        var dokumentdataBuilder = ForeldrepengerAvslagDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medRelasjonskode(RELASJONSKODE_TYPE_MAP.get(behandling.relasjonsRolleType()))
            .medMottattDato(formaterDato(behandling.førsteSøknadMottattDato(), språkkode))
            .medGjelderFødsel(familiehendelse.gjelderFødsel())
            .medBarnErFødt(familiehendelse.barnErFødt())
            .medAntallBarn(familiehendelse.antallBarn())
            .medHalvG(halvG)
            .medKlagefristUker(brevParametere.getKlagefristUker())
            .medGjelderMor(behandling.relasjonsRolleType() == RelasjonsRolleType.MORA);

        mapAvslåttePerioder(behandling, dokumentdataBuilder, uttakResultatPerioder, språkkode);

        return dokumentdataBuilder.build();
    }

    private void mapAvslåttePerioder(BrevGrunnlagDto behandling,
                                     ForeldrepengerAvslagDokumentdata.Builder dokumentdataBuilder,
                                     Optional<Foreldrepenger> uttakResultatPerioder, Språkkode språkkode) {
        var tilkjentYtelseFP = Optional.ofNullable(behandling.tilkjentYtelse()).map(TilkjentYtelse::dagytelse);
        var avslåttePerioderOgLovhjemmel = AvslåttPeriodeMapper.mapAvslåttePerioderOgLovhjemmel(behandling,
            tilkjentYtelseFP.map(TilkjentYtelseDagytelseDto::perioder).orElse(Collections.emptyList()), uttakResultatPerioder, språkkode);

        dokumentdataBuilder.medLovhjemmelForAvslag(avslåttePerioderOgLovhjemmel.element2());
        dokumentdataBuilder.medAvslåttePerioder(avslåttePerioderOgLovhjemmel.element1());
    }
}

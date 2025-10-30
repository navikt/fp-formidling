package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.opphorfp;

import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Barn;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Behandlingsresultat;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.FamilieHendelse;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Foreldrepenger;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.RelasjonsRolleType;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.time.LocalDate;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BeregningsgrunnlagMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.VilkårType;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.ForeldrepengerOpphørDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.KodeverkMapper;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.typer.Dato;
import no.nav.vedtak.exception.TekniskException;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.FORELDREPENGER_OPPHØR)
public class ForeldrepengerOpphørDokumentdataMapper implements DokumentdataMapper {

    private static final Map<RelasjonsRolleType, String> RELASJONSKODE_TYPE_MAP;

    static {
        RELASJONSKODE_TYPE_MAP = new EnumMap<>(RelasjonsRolleType.class);
        RELASJONSKODE_TYPE_MAP.put(RelasjonsRolleType.MORA, "MOR");
        RELASJONSKODE_TYPE_MAP.put(RelasjonsRolleType.FARA, "FAR");
        RELASJONSKODE_TYPE_MAP.put(RelasjonsRolleType.MEDMOR, "MEDMOR");
    }

    private final BrevParametere brevParametere;

    @Inject
    public ForeldrepengerOpphørDokumentdataMapper(BrevParametere brevParametere) {
        this.brevParametere = brevParametere;
    }

    @Override
    public String getTemplateNavn() {
        return "foreldrepenger-opphor";
    }

    @Override
    public ForeldrepengerOpphørDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                               DokumentHendelse hendelse,
                                                               BrevGrunnlagDto behandling,
                                                               boolean erUtkast) {

        var språkkode = dokumentFelles.getSpråkkode();
        var familiehendelse = behandling.familieHendelse();

        var uttaksperioder = Optional.ofNullable(behandling.foreldrepenger())
            .map(Foreldrepenger::perioderSøker)
            .orElse(List.of());

        var beregningsgrunnlagOpt = behandling.beregningsgrunnlag();
        var halvG = BeregningsgrunnlagMapper.getHalvGOrElseZero(Optional.ofNullable(beregningsgrunnlagOpt));
        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), språkkode) : null);
        var erSøkerDød = BrevMapperUtil.erDød(dokumentFelles);

        var behandlingsresultat = behandling.behandlingsresultat();

        var dokumentdataBuilder = ForeldrepengerOpphørDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medErSøkerDød(erSøkerDød)
            .medRelasjonskode(RELASJONSKODE_TYPE_MAP.get(behandling.relasjonsRolleType()), KodeverkMapper.mapRelasjonsRolle(behandling.relasjonsRolleType()))
            .medGjelderFødsel(familiehendelse.gjelderFødsel())
            .medAntallBarn(familiehendelse.antallBarn())
            .medHalvG(halvG)
            .medEndretDekningsgrad(behandlingsresultat.endretDekningsgrad())
            .medKlagefristUker(brevParametere.getKlagefristUker());

        var vilkårTyper = behandling.behandlingsresultat().vilkårTyper().stream().map(KodeverkMapper::mapVilkårType).toList();
        var årsakListe = mapAvslagårsaker(vilkårTyper, behandlingsresultat, dokumentdataBuilder, uttaksperioder);

        finnDødsdatoHvisFinnes(familiehendelse, årsakListe).map(d -> Dato.formaterDato(d, språkkode)).ifPresent(dokumentdataBuilder::medBarnDødsdato);

        var opphørsdato = Optional.ofNullable(behandlingsresultat.opphørsdato());
        opphørsdato.map(d -> Dato.formaterDato(d, språkkode)).ifPresent(dokumentdataBuilder::medOpphørDato);

        var fomStønadsdato = Optional.ofNullable(behandling.originalBehandling().førsteDagMedUtbetaltForeldrepenger())
            .map(d -> formaterDato(d, språkkode));
        fomStønadsdato.ifPresent(dokumentdataBuilder::medFomStønadsdato);

        var tomStønadsdato = finnStønadTomDatoHvisFinnes(uttaksperioder);
        tomStønadsdato.map(d -> Dato.formaterDato(d, språkkode)).ifPresent(dokumentdataBuilder::medTomStønadsdato);

        if (erSøkerDød && (fomStønadsdato.isEmpty() || tomStønadsdato.isEmpty())) {
            throw new TekniskException("FPFORMIDLING-743452",
                "Feil ved produksjon av opphørdokument: Klarte ikke utlede startdato eller siste utbetalingsdato fra uttaket. Påkrevd når personstatus = 'DØD'");
        }

        return dokumentdataBuilder.build();
    }

    private List<String> mapAvslagårsaker(Collection<VilkårType> vilkårTyper,
                                          Behandlingsresultat behandlingsresultat,
                                          ForeldrepengerOpphørDokumentdata.Builder builder,
                                          List<Foreldrepenger.Uttaksperiode> perioder) {
        var aarsakListeOgLovhjemmel = ÅrsakMapperOpphør.mapÅrsakslisteOgLovhjemmelFra(vilkårTyper, behandlingsresultat, perioder);
        var årsakListe = aarsakListeOgLovhjemmel.getElement1();

        builder.medAvslagÅrsaker(årsakListe);
        builder.medLovhjemmelForAvslag(aarsakListeOgLovhjemmel.getElement2());

        return årsakListe;
    }

    private Optional<LocalDate> finnDødsdatoHvisFinnes(FamilieHendelse familieHendelse, List<String> årsakListe) {
        if (årsakListe.contains(PeriodeResultatÅrsak.BARNET_ER_DØD.getKode())) {
            return familieHendelse.barn().stream().map(Barn::dødsdato).filter(Objects::nonNull).min(LocalDate::compareTo);
        }
        return Optional.empty();
    }

    private Optional<LocalDate> finnStønadTomDatoHvisFinnes(List<Foreldrepenger.Uttaksperiode> uttaksperioder) {
        return uttaksperioder.stream()
            .filter(Foreldrepenger.Uttaksperiode::isInnvilget)
            .map(Foreldrepenger.Uttaksperiode::tom)
            .max(LocalDate::compareTo);
    }
}

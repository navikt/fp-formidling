package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.opphorfp;

import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.time.LocalDate;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BeregningsgrunnlagMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakBackend;
import no.nav.foreldrepenger.fpformidling.domene.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.domene.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.ForeldrepengerUttak;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.UttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.ForeldrepengerOpphørDokumentdata;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.typer.Dato;
import no.nav.vedtak.exception.TekniskException;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.FORELDREPENGER_OPPHØR)
public class ForeldrepengerOpphørDokumentdataMapper implements DokumentdataMapper {

    private static final Map<RelasjonsRolleType, String> relasjonskodeTypeMap;

    static {
        relasjonskodeTypeMap = new EnumMap<>(RelasjonsRolleType.class);
        relasjonskodeTypeMap.put(RelasjonsRolleType.MORA, "MOR");
        relasjonskodeTypeMap.put(RelasjonsRolleType.FARA, "FAR");
        relasjonskodeTypeMap.put(RelasjonsRolleType.MEDMOR, "MEDMOR");
    }

    private BrevParametere brevParametere;
    private DomeneobjektProvider domeneobjektProvider;

    ForeldrepengerOpphørDokumentdataMapper() {
        //CDI
    }

    @Inject
    public ForeldrepengerOpphørDokumentdataMapper(BrevParametere brevParametere, DomeneobjektProvider domeneobjektProvider) {
        this.brevParametere = brevParametere;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String getTemplateNavn() {
        return "foreldrepenger-opphor";
    }

    @Override
    public ForeldrepengerOpphørDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                               DokumentHendelse hendelse,
                                                               Behandling behandling,
                                                               boolean erUtkast) {

        var språkkode = behandling.getSpråkkode();
        var fagsak = domeneobjektProvider.hentFagsakBackend(behandling);
        var familiehendelse = domeneobjektProvider.hentFamiliehendelse(behandling);

        var foreldrepengerUttak = domeneobjektProvider.hentForeldrepengerUttakHvisFinnes(behandling)
            .orElseGet(ForeldrepengerUttak::tomtUttak); // bestående av tomme lister.
        var originaltUttakResultat = domeneobjektProvider.hentOriginalBehandlingHvisFinnes(behandling)
            .flatMap(domeneobjektProvider::hentForeldrepengerUttakHvisFinnes);

        var beregningsgrunnlagOpt = domeneobjektProvider.hentBeregningsgrunnlagHvisFinnes(behandling);
        var halvG = BeregningsgrunnlagMapper.getHalvGOrElseZero(beregningsgrunnlagOpt);
        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, behandling, erUtkast);
        fellesBuilder.medBrevDato(
            dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), behandling.getSpråkkode()) : null);
        var erSøkerDød = BrevMapperUtil.erDød(dokumentFelles);

        var behandlingsresultat = behandling.getBehandlingsresultat();
        var dokumentdataBuilder = ForeldrepengerOpphørDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medErSøkerDød(erSøkerDød)
            .medRelasjonskode(finnRelasjonskode(fagsak))
            .medGjelderFødsel(familiehendelse.gjelderFødsel())
            .medAntallBarn(familiehendelse.antallBarn())
            .medHalvG(halvG)
            .medEndretDekningsgrad(behandlingsresultat.isEndretDekningsgrad())
            .medKlagefristUker(brevParametere.getKlagefristUker());

        var årsakListe = mapAvslagårsaker(behandlingsresultat, foreldrepengerUttak, dokumentdataBuilder);

        finnDødsdatoHvisFinnes(familiehendelse, årsakListe).map(d -> Dato.formaterDato(d, språkkode)).ifPresent(dokumentdataBuilder::medBarnDødsdato);

        var opphørsdato = behandlingsresultat.getOpphørsdato();
        opphørsdato.map(d -> Dato.formaterDato(d, språkkode)).ifPresent(dokumentdataBuilder::medOpphørDato);

        var fomStønadsdato = finnStønadFomDatoHvisFinnes(originaltUttakResultat);
        fomStønadsdato.map(d -> Dato.formaterDato(d, språkkode)).ifPresent(dokumentdataBuilder::medFomStønadsdato);

        var tomStønadsdato = finnStønadTomDatoHvisFinnes(foreldrepengerUttak );
        tomStønadsdato.map(d -> Dato.formaterDato(d, språkkode)).ifPresent(dokumentdataBuilder::medTomStønadsdato);

        if (erSøkerDød && (fomStønadsdato.isEmpty() || tomStønadsdato.isEmpty())) {
            throw new TekniskException("FPFORMIDLING-743452", "Feil ved produksjon av opphørdokument: Klarte ikke utlede startdato eller siste utbetalingsdato fra uttaket. Påkrevd når personstatus = 'DØD'");
        }

        return dokumentdataBuilder.build();
    }

    private List<String> mapAvslagårsaker(Behandlingsresultat behandlingsresultat,
                                          ForeldrepengerUttak foreldrepengerUttak,
                                          ForeldrepengerOpphørDokumentdata.Builder builder) {
        var aarsakListeOgLovhjemmel = ÅrsakMapperOpphør.mapÅrsakslisteOgLovhjemmelFra(behandlingsresultat, foreldrepengerUttak);
        var årsakListe = aarsakListeOgLovhjemmel.getElement1();

        builder.medAvslagÅrsaker(årsakListe);
        builder.medLovhjemmelForAvslag(aarsakListeOgLovhjemmel.getElement2());

        return årsakListe;
    }

    private String finnRelasjonskode(FagsakBackend fagsak) {
        if (RelasjonsRolleType.erRegistrertForeldre(fagsak.getRelasjonsRolleType())) {
            return relasjonskodeTypeMap.get(fagsak.getRelasjonsRolleType());
        }
        return "ANNET";
    }

    private Optional<LocalDate> finnDødsdatoHvisFinnes(final FamilieHendelse familieHendelse, List<String> årsakListe) {
        Optional<LocalDate> dødsdato = Optional.empty();
        if (årsakListe.contains(PeriodeResultatÅrsak.BARNET_ER_DØD.getKode())) {
            dødsdato = familieHendelse.dødsdato();
        }
        return dødsdato;
    }

    private Optional<LocalDate> finnStønadFomDatoHvisFinnes(Optional<ForeldrepengerUttak> originaltUttakResultat) {
        return originaltUttakResultat.map(ForeldrepengerUttak::perioder)
            .orElse(Collections.emptyList())
            .stream()
            .filter(UttakResultatPeriode::isInnvilget)
            .map(UttakResultatPeriode::getFom)
            .findFirst();
    }

    private Optional<LocalDate> finnStønadTomDatoHvisFinnes(ForeldrepengerUttak foreldrepengerUttak) {
        return  foreldrepengerUttak.perioder().stream().filter(UttakResultatPeriode::isInnvilget).map(UttakResultatPeriode::getTom).max(LocalDate::compareTo);
    }
}

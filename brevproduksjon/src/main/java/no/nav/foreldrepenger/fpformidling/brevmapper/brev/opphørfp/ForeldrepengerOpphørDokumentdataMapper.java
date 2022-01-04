package no.nav.foreldrepenger.fpformidling.brevmapper.brev.opphørfp;

import static no.nav.foreldrepenger.fpformidling.datamapper.util.BrevMapperUtil.erDød;
import static no.nav.foreldrepenger.fpformidling.datamapper.util.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpformidling.Tuple;
import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.fpformidling.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.datamapper.domene.BeregningsgrunnlagMapper;
import no.nav.foreldrepenger.fpformidling.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.fagsak.FagsakBackend;
import no.nav.foreldrepenger.fpformidling.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.ForeldrepengerOpphørDokumentdata;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalTypeKode;
import no.nav.foreldrepenger.fpformidling.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.fpformidling.typer.Dato;
import no.nav.foreldrepenger.fpformidling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.fpformidling.uttak.kodeliste.PeriodeResultatÅrsak;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.exception.VLException;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.FORELDREPENGER_OPPHØR)
public class ForeldrepengerOpphørDokumentdataMapper implements DokumentdataMapper {

    private static final Map<RelasjonsRolleType, String> relasjonskodeTypeMap;
    static {
        relasjonskodeTypeMap = new HashMap<>();
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
    public ForeldrepengerOpphørDokumentdataMapper(BrevParametere brevParametere,
                                                  DomeneobjektProvider domeneobjektProvider) {
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

        Språkkode språkkode = behandling.getSpråkkode();
        FagsakBackend fagsak = domeneobjektProvider.hentFagsakBackend(behandling);
        FamilieHendelse familiehendelse = domeneobjektProvider.hentFamiliehendelse(behandling);

        UttakResultatPerioder uttakResultatPerioder = domeneobjektProvider.hentUttaksresultatHvisFinnes(behandling)
                .orElseGet(() -> UttakResultatPerioder.ny().build()); // bestående av tomme lister.
        Optional<UttakResultatPerioder> originaltUttakResultat = domeneobjektProvider.hentOriginalBehandlingHvisFinnes(behandling)
                .flatMap(domeneobjektProvider::hentUttaksresultatHvisFinnes);

        Optional<Beregningsgrunnlag> beregningsgrunnlagOpt = domeneobjektProvider.hentBeregningsgrunnlagHvisFinnes(behandling);
        long halvG = BeregningsgrunnlagMapper.getHalvGOrElseZero(beregningsgrunnlagOpt);
        var fellesBuilder = opprettFellesBuilder(dokumentFelles, hendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), behandling.getSpråkkode()) : null);
        var erSøkerDød = erDød(dokumentFelles);

        var dokumentdataBuilder = ForeldrepengerOpphørDokumentdata.ny()
                .medFelles(fellesBuilder.build())
                .medErSøkerDød(erSøkerDød)
                .medRelasjonskode(finnRelasjonskode(fagsak))
                .medGjelderFødsel(familiehendelse.isGjelderFødsel())
                .medAntallBarn(familiehendelse.getAntallBarn().intValue())
                .medHalvG(halvG)
                .medKlagefristUker(brevParametere.getKlagefristUker());

        var årsakListe = mapAvslagårsaker(behandling.getBehandlingsresultat(), uttakResultatPerioder, dokumentdataBuilder);

        finnDødsdatoHvisFinnes(familiehendelse, årsakListe)
                .map(d -> Dato.formaterDato(d, språkkode))
                .ifPresent(dokumentdataBuilder::medBarnDødsdato);

        var opphørsdato = finnOpphørsdatoHvisFinnes(uttakResultatPerioder, familiehendelse);
        opphørsdato.map(d -> Dato.formaterDato(d, språkkode))
                .ifPresent(dokumentdataBuilder::medOpphørDato);

        var fomStønadsdato = finnStønadFomDatoHvisFinnes(originaltUttakResultat);
        fomStønadsdato.map(d -> Dato.formaterDato(d, språkkode))
                .ifPresent(dokumentdataBuilder::medFomStønadsdato);

        finnStønadTomDatoHvisFinnes(opphørsdato, fomStønadsdato, erSøkerDød)
                .map(d -> Dato.formaterDato(d, språkkode))
                .ifPresent(dokumentdataBuilder::medTomStønadsdato);

        return dokumentdataBuilder.build();
    }

    private List<String> mapAvslagårsaker(Behandlingsresultat behandlingsresultat,
                                  UttakResultatPerioder uttakResultatPerioder,
                                  ForeldrepengerOpphørDokumentdata.Builder builder) {
        Tuple<List<String>, String> aarsakListeOgLovhjemmel = ÅrsakMapperOpphør.mapÅrsakslisteOgLovhjemmelFra(
                behandlingsresultat,
                uttakResultatPerioder);
        List<String> årsakListe = aarsakListeOgLovhjemmel.getElement1();

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
            dødsdato = familieHendelse.getDødsdato();
        }
        return dødsdato;
    }

    private Optional<LocalDate> finnOpphørsdatoHvisFinnes(UttakResultatPerioder uttakResultatPerioder, FamilieHendelse familiehendelse) {
        LocalDate opphørsdato = utledOpphørsdatoFraUttak(uttakResultatPerioder);
        return Optional.ofNullable(opphørsdato).or(familiehendelse::getSkjæringstidspunkt);
    }

    private LocalDate utledOpphørsdatoFraUttak(UttakResultatPerioder uttakResultatPerioder) {
        Set<String> opphørsårsaker = PeriodeResultatÅrsak.opphørsAvslagÅrsaker();
        List<UttakResultatPeriode> perioder = uttakResultatPerioder.getPerioder();

        // Finn fom-dato i første periode av de siste sammenhengende periodene med
        // opphørårsaker
        LocalDate fom = null;
        for (int i = perioder.size() - 1; i >= 0; i--) {
            UttakResultatPeriode periode = perioder.get(i);
            if (opphørsårsaker.contains(periode.getPeriodeResultatÅrsak().getKode())) {
                fom = periode.getFom();
            } else if (fom != null && periode.isInnvilget()) {
                return fom;
            }
        }
        // bruker skjæringstidspunkt fom = null eller tidligste periode i uttaksplan er
        // opphørt eller avslått
        return null;
    }

    private Optional<LocalDate> finnStønadFomDatoHvisFinnes(Optional<UttakResultatPerioder> originaltUttakResultat) {
        return originaltUttakResultat.map(UttakResultatPerioder::getPerioder).orElse(Collections.emptyList()).stream()
                .filter(UttakResultatPeriode::isInnvilget)
                .map(UttakResultatPeriode::getFom)
                .findFirst();
    }

    private Optional<LocalDate> finnStønadTomDatoHvisFinnes(Optional<LocalDate> opphørsDato,
                                                            Optional<LocalDate> fomStønadsdato,
                                                            boolean erSøkerDød) {
        if (fomStønadsdato.isPresent() && opphørsDato.isPresent()) {
            return Optional.of(opphørsDato.get().minusDays(!fomStønadsdato.equals(opphørsDato) ? 1 : 0));
        } else if (erSøkerDød) {
            throw brevFeilPgaUtilstrekkeligTekstgrunnlag(opphørsDato.isPresent());
        }
        return Optional.empty();
    }

    private VLException brevFeilPgaUtilstrekkeligTekstgrunnlag(boolean opphørsdatoFinnes) {
        return opphørsdatoFinnes ? new TekniskException("FPFORMIDLING-743452",
                "Feil ved produksjon av opphørdokument: Klarte ikke utlede startdato fra det opprinnelige vedtaket. Påkrevd når personstatus = 'DØD'")
                : new TekniskException("FPFORMIDLING-724872",
                "Feil ved produksjon av opphørdokument: Klarte ikke utlede opphørsdato fra uttaksplanen. Påkrevd når personstatus = 'DØD'");
    }
}
package no.nav.foreldrepenger.melding.datamapper.domene;

import static no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.DuplikatVerktøy.slåSammenLikeArbeidsforhold;
import static no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeBeregner.alleAktiviteterHarNullUtbetaling;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.BehandlingRestKlient;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat.BeregningsresultatMedUttaksplanDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat.BeregningsresultatPeriodeAndelDto;
import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsresultat.BeregningsresultatPeriodeDto;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.ÅrsakskodeMedLovreferanse;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatAndel;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatES;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.melding.datamapper.domene.hjelperdto.ArbeidsforholdDto;
import no.nav.foreldrepenger.melding.datamapper.domene.hjelperdto.DokumentBeregningsresultatDto;
import no.nav.foreldrepenger.melding.datamapper.domene.hjelperdto.DokumentTypeMedPerioderDto;
import no.nav.foreldrepenger.melding.datamapper.domene.hjelperdto.PeriodeDto;
import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeBeregner;
import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeMerger;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.OrganisasjonsNummerValidator;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.typer.AktørId;
import no.nav.foreldrepenger.melding.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.uttak.GraderingAvslagÅrsak;
import no.nav.foreldrepenger.melding.uttak.IkkeOppfyltÅrsak;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.melding.uttak.UttakResultat;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;
import no.nav.foreldrepenger.melding.virksomhet.Virksomhet;
import no.nav.vedtak.util.StringUtils;

@ApplicationScoped
public class BeregningsresultatMapper {

    private BehandlingRestKlient behandlingRestKlient;
    private KodeverkRepository kodeverkRepository;

    public BeregningsresultatMapper() {
        //CDI
    }

    @Inject
    public BeregningsresultatMapper(BehandlingRestKlient behandlingRestKlient,
                                    KodeverkRepository kodeverkRepository) {
        this.behandlingRestKlient = behandlingRestKlient;
        this.kodeverkRepository = kodeverkRepository;
    }

    public BeregningsresultatES hentBeregningsresultatES(Behandling behandling) {
        return new BeregningsresultatES(behandlingRestKlient.hentBeregningsresultatEngangsstønad(behandling.getResourceLinkDtos()));
    }

    public BeregningsresultatFP hentBeregningsresultatFP(Behandling behandling) {
        return mapBeregningsresultatFPFraDto(behandlingRestKlient.hentBeregningsresultatForeldrepenger(behandling.getResourceLinkDtos()));
    }

    BeregningsresultatFP mapBeregningsresultatFPFraDto(BeregningsresultatMedUttaksplanDto dto) {
        return BeregningsresultatFP.ny()
                .medBeregningsresultatPerioder(Arrays.stream(dto.getPerioder()).map(this::mapPeriodeFraDto).collect(Collectors.toList()))
                .build();
    }

    BeregningsresultatPeriode mapPeriodeFraDto(BeregningsresultatPeriodeDto dto) {
        BeregningsresultatPeriode beregningsresultatPeriode = BeregningsresultatPeriode.ny()
                .medDagsats((long) dto.getDagsats())
                .medPeriode(DatoIntervall.fraOgMedTilOgMed(dto.getFom(), dto.getTom()))
                .medBeregningsresultatAndel(Arrays.stream(dto.getAndeler()).map(this::mapAndelerFraDto)
                        .flatMap(List::stream).collect(Collectors.toList()))
                .build();
        return beregningsresultatPeriode;
    }

    //Fpsak slår sammen andeler i dto, så vi må eventuelt splitte dem opp igjen
    List<BeregningsresultatAndel> mapAndelerFraDto(BeregningsresultatPeriodeAndelDto dto) {
        List<BeregningsresultatAndel> andeler = new ArrayList<>();
        if (dto.getRefusjon() != null && dto.getRefusjon() != 0) {
            andeler.add(lagEnkelAndel(dto, false, dto.getRefusjon()));
        }
        if (dto.getTilSoker() != null && dto.getTilSoker() != 0) {
            andeler.add(lagEnkelAndel(dto, true, dto.getTilSoker()));
        }
        return andeler;
    }

    private BeregningsresultatAndel lagEnkelAndel(BeregningsresultatPeriodeAndelDto dto, boolean brukerErMottaker, int dagsats) {
        return BeregningsresultatAndel.ny()
                .medAktivitetStatus(kodeverkRepository.finn(AktivitetStatus.class, dto.getAktivitetStatus().getKode()))
                .medArbeidsforholdRef(!StringUtils.nullOrEmpty(dto.getArbeidsforholdId()) ? ArbeidsforholdRef.ref(dto.getArbeidsforholdId()) : null)
                .medArbeidsgiver(finnArbeidsgiver(dto))
                .medStillingsprosent(null/*TODO*/)
                .medBrukerErMottaker(brukerErMottaker)
                .build();
    }

    Arbeidsgiver finnArbeidsgiver(BeregningsresultatPeriodeAndelDto dto) {
        Virksomhet virksomhet = null;
        AktørId aktørId = null;
        if (OrganisasjonsNummerValidator.erGyldig(dto.getArbeidsgiverOrgnr())) {
            virksomhet = new Virksomhet(dto.getArbeidsgiverNavn(), dto.getArbeidsgiverOrgnr());
        } else {
            aktørId = new AktørId(dto.getArbeidsgiverOrgnr());
        }
        return new Arbeidsgiver(dto.getArbeidsgiverNavn(), virksomhet, aktørId);
    }

    public BigInteger antallArbeidsgivere(BeregningsresultatFP beregningsresultat) {
        return BigInteger.valueOf(beregningsresultat.getBeregningsresultatPerioder().stream()
                .map(BeregningsresultatPeriode::getBeregningsresultatAndelList)
                .flatMap(Collection::stream)
                .filter(andel -> AktivitetStatus.ARBEIDSTAKER.getKode().equals(andel.getAktivitetStatus()))
                .map(BeregningsresultatAndel::getArbeidsgiver)
                .distinct()
                .count());

    }

    void mapDataRelatertTilBeregningsresultat(Behandling behandling, BeregningsresultatFP beregningsresultat, DokumentTypeMedPerioderDto dto) {
        mapDataRelatertTilPerioder(behandling, beregningsresultat, dto);
    }

    private void mapDataRelatertTilPerioder(Behandling behandling, BeregningsresultatFP beregningsresultat, DokumentTypeMedPerioderDto dto) {
        List<UttakResultatPeriode> uttakPerioder = finnUttaksPerioder(behandling);
        List<BeregningsgrunnlagPeriode> beregningsgrunnlagPerioder = finnBeregninsgrunnlagperioder(behandling);
        List<PeriodeDto> perioder = new ArrayList<>();
        for (BeregningsresultatPeriode periode : beregningsresultat.getBeregningsresultatPerioder()) {
            mapBeregningsresultatPeriode(periode, uttakPerioder, dto, beregningsgrunnlagPerioder)
                    .ifPresent(perioder::add);
        }
        if (uttakPerioder.size() != beregningsresultat.getBeregningsresultatPerioder().size()) {
            uttakPerioder.stream()
                    .filter(up -> !PeriodeBeregner.erPeriodeDekket(up, beregningsresultat.getBeregningsresultatPerioder()))
                    .forEach(up -> mapUttaksPeriode(up, dto)
                            .ifPresent(perioder::add));
        }
        List<PeriodeDto> etterSammenslåing = PeriodeMerger.mergePerioder(perioder);
        etterSammenslåing.forEach(dto::addPeriode);
        setAntallPerioder(etterSammenslåing, dto);

        if (behandling.erRevurdering()) {
            LocalDate vedtaksdato = hentOptionalVedtaksdato(behandling).orElse(null);
            String utbetaltKode = PeriodeBeregner.forMyeUtbetalt(etterSammenslåing, vedtaksdato);
            dto.setForMyeUtbetalt(utbetaltKode);
        }
    }

    private Optional<LocalDate> hentOptionalVedtaksdato(Behandling behandling) {
        //TODO: Map dette felt
        return Optional.empty();
//        return behandlingVedtakRepository.hentBehandlingvedtakForBehandlingId(behandling.getId())
//                .map(BehandlingVedtak::getVedtaksdato);
    }

    private void setAntallPerioder(List<PeriodeDto> perioder, DokumentTypeMedPerioderDto dto) {
        dto.setAntallPerioder(perioder.size());
        dto.setAntallInnvilget((int) perioder.stream().filter(p -> p.getInnvilget()).count());
        dto.setAntallAvslag((int) perioder.stream().filter(p -> !p.getInnvilget() && erÅrsakSomSkalTelle(p)).count());
    }

    private boolean erÅrsakSomSkalTelle(PeriodeDto p) {
        return !IkkeOppfyltÅrsak.MOR_TAR_IKKE_ALLE_UKENE.getKode().equals(p.getÅrsak());
    }

    private Optional<PeriodeDto> mapBeregningsresultatPeriode(BeregningsresultatPeriode periode,
                                                              List<UttakResultatPeriode> uttakPerioder,
                                                              DokumentTypeMedPerioderDto dtoMedPerioder,
                                                              List<BeregningsgrunnlagPeriode> beregninsgrunnlagPerioder) {

        UttakResultatPeriode uttakPeriode = PeriodeBeregner.finnUttaksPeriode(periode, uttakPerioder);
        Optional<PeriodeDto> periodeDto = mapUttaksPeriode(uttakPeriode, dtoMedPerioder);

        periodeDto.ifPresent(dto -> {
            dto.setPeriodeFom(periode.getBeregningsresultatPeriodeFom().toString());
            dto.setPeriodeTom(periode.getBeregningsresultatPeriodeTom().toString());
            dto.setPeriodeDagsats((long) periode.getDagsats());
            BeregningsgrunnlagPeriode bgPeriode = PeriodeBeregner.finnBeregninsgrunnlagperiode(periode, beregninsgrunnlagPerioder);
            List<BeregningsgrunnlagPrStatusOgAndel> bgPerStatusOgAndelListe = bgPeriode.getBeregningsgrunnlagPrStatusOgAndelList();
            for (BeregningsresultatAndel andel : periode.getBeregningsresultatAndelList()) {
                leggTilAndelAktivitet(andel, periode, uttakPeriode, bgPerStatusOgAndelListe, dto);
                oppdaterAndelsfordelingAnliggende(andel, dtoMedPerioder.getDokumentBeregningsresultatDto());
            }
            slåSammenLikeArbeidsforhold(dto);
        });
        return periodeDto;
    }

    private void leggTilAndelAktivitet(BeregningsresultatAndel andel, BeregningsresultatPeriode periode, UttakResultatPeriode uttakPeriode, List<BeregningsgrunnlagPrStatusOgAndel> bgPerStatusOgAndelListe, PeriodeDto dto) {
//        AktivitetStatus status = andel.getAktivitetStatus();
        AktivitetStatus status = AktivitetStatus.ARBEIDSTAKER;
        Optional<UttakResultatPeriodeAktivitet> uttakAktivitet = PeriodeBeregner.finnAktivitetMedStatusHvisFinnes(status, uttakPeriode.getAktiviteter(), andel);
        Optional<BeregningsgrunnlagPrStatusOgAndel> bgPrStatusOgAndel = PeriodeBeregner.finnBgPerStatusOgAndelHvisFinnes(status, bgPerStatusOgAndelListe, andel);
        if (AktivitetStatus.ARBEIDSTAKER.equals(status)) {
//            leggTilArbeidsforhold(dto, mapArbeidsforhold(uttakAktivitet, andel, bgPrStatusOgAndel, periode));
        } else if (AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE.equals(status)) {
//            dto.setNæring(mapNæring(andel, uttakAktivitet, bgPrStatusOgAndel));
        } else {
//            dto.leggTilAnnenAktivitet(mapAnnenAktivitet(andel, uttakAktivitet));
        }
    }

    private void oppdaterAndelsfordelingAnliggende(BeregningsresultatAndel andel, DokumentBeregningsresultatDto beregningsresultatDto) {
        if (andel.getDagsats() > 0) {
            if (andel.erBrukerMottaker()) {
                beregningsresultatDto.setTotalBrukerAndel(1L);
            } else {
                beregningsresultatDto.setTotalArbeidsgiverAndel(1L);
            }
        }
    }

    private Optional<PeriodeDto> mapUttaksPeriode(UttakResultatPeriode uttakPeriode,
                                                  DokumentTypeMedPerioderDto dtoMedPerioder) {
        PeriodeDto dto = new PeriodeDto();

        dto.setPeriodeFom(uttakPeriode.getFom().toString());
        dto.setPeriodeTom(uttakPeriode.getTom().toString());

        ÅrsakskodeMedLovreferanse årsak = utledÅrsakskode(uttakPeriode);
        if (erUkjent(årsak)) {
            return Optional.empty();
        }
        dto.setÅrsak(årsak.getKode());
        setLovhjemmelVurderingFor(årsak, uttakPeriode, dtoMedPerioder);
        setInnvilgetStatus(uttakPeriode, dto);

        List<UttakResultatPeriodeAktivitet> uttakAktiviteter = uttakPeriode.getAktiviteter();
        dto.setAntallTapteDager(mapAntallTapteDagerFra(uttakAktiviteter));

        return Optional.of(dto);
    }

    private ÅrsakskodeMedLovreferanse utledÅrsakskode(UttakResultatPeriode uttakPeriode) {
//        if (erGraderingAvslått(uttakPeriode) && erInnvilget(uttakPeriode)) {
//            return uttakPeriode.getGraderingAvslagÅrsak();
//        } else if (uttakPeriode.getPeriodeResultatÅrsak() != null) {
//            return uttakPeriode.getPeriodeResultatÅrsak();
//        }
        return PeriodeResultatÅrsak.UKJENT;
    }

    private boolean erUkjent(ÅrsakskodeMedLovreferanse årsaksKode) {
        return PeriodeResultatÅrsak.UKJENT.getKode().equals(årsaksKode.getKode());
    }

    private void setLovhjemmelVurderingFor(ÅrsakskodeMedLovreferanse årsak, UttakResultatPeriode uttakPeriode, DokumentTypeMedPerioderDto dtoMedPerioder) {
        leggTilLovhjemlerIDtoFor(årsak, dtoMedPerioder);
        if (årsak instanceof GraderingAvslagÅrsak) {
//            leggTilLovhjemlerIDtoFor(uttakPeriode.getPeriodeResultatÅrsak(), dtoMedPerioder);
        }
    }

    private void leggTilLovhjemlerIDtoFor(ÅrsakskodeMedLovreferanse årsakskode, DokumentTypeMedPerioderDto dto) {
        Set<String> lovhjemler = LovhjemmelUtil.hentLovhjemlerFraJson(FagsakYtelseType.FORELDREPENGER, årsakskode);
        dto.addLovhjemler(lovhjemler);
    }

    private void setInnvilgetStatus(UttakResultatPeriode uttakPeriode, PeriodeDto dto) {
        dto.setInnvilget(erInnvilget(uttakPeriode) && !erGraderingAvslått(uttakPeriode));
    }

    private boolean erInnvilget(UttakResultatPeriode uttakPeriode) {
        return PeriodeResultatType.INNVILGET.equals(uttakPeriode.getPeriodeResultatType());
    }

    private int mapAntallTapteDagerFra(List<UttakResultatPeriodeAktivitet> uttakAktiviteter) {
        return alleAktiviteterHarNullUtbetaling(uttakAktiviteter) ?
                uttakAktiviteter.stream()
                        .mapToInt(UttakResultatPeriodeAktivitet::getTrekkdager)
                        .max()
                        .orElse(0) : 0;
    }

    private boolean erGraderingAvslått(UttakResultatPeriode uttakPeriode) {
        return true;
//        return !uttakPeriode.isGraderingInnvilget() && erGraderingÅrsakKjent(uttakPeriode.getGraderingAvslagÅrsak());
    }

    private boolean erGraderingÅrsakKjent(GraderingAvslagÅrsak årsak) {
        return årsak != null && !årsak.getKode().equals(GraderingAvslagÅrsak.UKJENT.getKode());
    }

    private void leggTilArbeidsforhold(PeriodeDto dto, ArbeidsforholdDto arbeidsforhold) {
        dto.leggTilArbeidsforhold(arbeidsforhold);
    }

    private List<UttakResultatPeriode> finnUttaksPerioder(Behandling behandling) {
        //Optional<UttakResultat> uttakResultat = uttakRepository.hentUttakResultatHvisEksisterer(behandling);
        //TODO: hent Uttakresultat fra FPSAK
        return new UttakResultat().getGjeldendePerioder().getPerioder();
    }

    private List<BeregningsgrunnlagPeriode> finnBeregninsgrunnlagperioder(Behandling behandling) {
//        Optional<Beregningsgrunnlag> bg = beregningsgrunnlagRepository.hentBeregningsgrunnlagForBehandling(behandling.getId());
        //TODO: hente beregningsgrunnlag
        Optional<Beregningsgrunnlag> bg = Optional.empty();
        return (bg.map(Beregningsgrunnlag::getBeregningsgrunnlagPerioder).orElse(Collections.emptyList()));
    }

}

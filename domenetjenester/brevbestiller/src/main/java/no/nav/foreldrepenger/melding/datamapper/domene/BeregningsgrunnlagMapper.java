package no.nav.foreldrepenger.melding.datamapper.domene;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.BehandlingRestKlient;
import no.nav.foreldrepenger.melding.aktør.Personinfo;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BGAndelArbeidsforhold;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagAktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Hjemmel;
import no.nav.foreldrepenger.melding.datamapper.domene.hjelperdto.BeregningsgrunnlagAndelDto;
import no.nav.foreldrepenger.melding.datamapper.domene.hjelperdto.BeregningsgrunnlagRegelDto;
import no.nav.foreldrepenger.melding.datamapper.domene.hjelperdto.DokumentTypeMedPerioderDto;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.RelatertYtelseType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.AndelListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.AndelType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.BeregningsgrunnlagRegelListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.BeregningsgrunnlagRegelType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.StatusTypeKode;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.opptjening.OpptjeningAktivitetType;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;
import no.nav.foreldrepenger.tps.TpsTjeneste;

@ApplicationScoped
public class BeregningsgrunnlagMapper {
    private TpsTjeneste tpsTjeneste;
    private KodeverkRepository kodeverkRepository;
    private BehandlingRestKlient behandlingRestKlient;

    BeregningsgrunnlagMapper() {
        // CDI Proxy
    }

    @Inject
    public BeregningsgrunnlagMapper(TpsTjeneste tpsTjeneste, KodeverkRepository kodeverkRepository, BehandlingRestKlient behandlingRestKlient) {
        this.tpsTjeneste = tpsTjeneste;
        this.kodeverkRepository = kodeverkRepository;
        this.behandlingRestKlient = behandlingRestKlient;
    }

    public static BeregningsgrunnlagRegelListeType mapRegelListe(Beregningsgrunnlag beregningsgrunnlag) {
        ObjectFactory objectFactory = new ObjectFactory();
        BeregningsgrunnlagRegelListeType regelListe = objectFactory.createBeregningsgrunnlagRegelListeType();
        List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe = beregningsgrunnlag.getBeregningsgrunnlagPerioder().get(0).getBeregningsgrunnlagPrStatusOgAndelList();
        for (BeregningsgrunnlagAktivitetStatus bgAktivitetStatus : beregningsgrunnlag.getAktivitetStatuser()) {
            BeregningsgrunnlagRegelType beregningsgrunnlagRegel = objectFactory.createBeregningsgrunnlagRegelType();
            List<BeregningsgrunnlagPrStatusOgAndel> filtrertListe = finnAktivitetStatuserForAndeler(bgAktivitetStatus, bgpsaListe);
            beregningsgrunnlagRegel.setRegelStatus(tilStatusTypeKode(bgAktivitetStatus.getAktivitetStatus()));
            beregningsgrunnlagRegel.setAndelListe(mapAndelListe(filtrertListe));
            beregningsgrunnlagRegel.setAntallArbeidsgivereIBeregning(tellAntallArbeidsforholdIBeregning(filtrertListe));
            beregningsgrunnlagRegel.setBesteBeregning(harNoenAvAndeleneBesteberegning(filtrertListe));
            beregningsgrunnlagRegel.setSNNyoppstartet(nyoppstartetSelvstendingNæringsdrivende(filtrertListe));
        }
        return regelListe;
    }

    private static Map<String, StatusTypeKode> aktivitetStatusKodeStatusTypeKodeMap = new HashMap<>();
    private static List<String> kombinerteStatuser = AktivitetStatus.KOMBINERTE_STATUSER.stream().map(AktivitetStatus::getKode).collect(Collectors.toList());
    private static Map<String, List<String>> kombinerteRegelStatuserMap = new HashMap<>();

    static {
        kombinerteRegelStatuserMap.put(AktivitetStatus.KOMBINERT_AT_FL.getKode(), List.of(AktivitetStatus.ARBEIDSTAKER.getKode(), AktivitetStatus.FRILANSER.getKode()));
        kombinerteRegelStatuserMap.put(AktivitetStatus.KOMBINERT_AT_SN.getKode(), List.of(AktivitetStatus.ARBEIDSTAKER.getKode(), AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE.getKode()));
        kombinerteRegelStatuserMap.put(AktivitetStatus.KOMBINERT_AT_FL_SN.getKode(), List.of(AktivitetStatus.ARBEIDSTAKER.getKode(), AktivitetStatus.FRILANSER.getKode(), AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE.getKode()));
        kombinerteRegelStatuserMap.put(AktivitetStatus.KOMBINERT_FL_SN.getKode(), List.of(AktivitetStatus.FRILANSER.getKode(), AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE.getKode()));
    }

    static {
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.ARBEIDSTAKER.getKode(), StatusTypeKode.ARBEIDSTAKER);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.FRILANSER.getKode(), StatusTypeKode.FRILANSER);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE.getKode(), StatusTypeKode.SELVSTENDIG_NÆRINGSDRIVENDE);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.KOMBINERT_AT_FL.getKode(), StatusTypeKode.KOMBINERT_AT_FL);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.KOMBINERT_AT_FL_SN.getKode(), StatusTypeKode.KOMBINERT_AT_FL_SN);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.KOMBINERT_AT_SN.getKode(), StatusTypeKode.KOMBINERT_AT_SN);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.KOMBINERT_FL_SN.getKode(), StatusTypeKode.KOMBINERT_FL_SN);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.DAGPENGER.getKode(), StatusTypeKode.DAGPENGER);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.ARBEIDSAVKLARINGSPENGER.getKode(), StatusTypeKode.ARBEIDSAVKLARINGSPENGER);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.MILITÆR_ELLER_SIVIL.getKode(), StatusTypeKode.MILITÆR_ELLER_SIVIL);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.BRUKERS_ANDEL.getKode(), StatusTypeKode.BRUKERSANDEL);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.KUN_YTELSE.getKode(), StatusTypeKode.KUN_YTELSE);
    }

    @Inject
    public BeregningsgrunnlagMapper(TpsTjeneste tpsTjeneste, KodeverkRepository kodeverkRepository) {
        this.tpsTjeneste = tpsTjeneste;
        this.kodeverkRepository = kodeverkRepository;
    }

    public static boolean erOverbetalt(Beregningsgrunnlag beregningsgrunnlag, Beregningsgrunnlag originaltBeregningsgrunnlag) {
        if (originaltBeregningsgrunnlag == null) {
            return false;
        }
        return originaltBeregningsgrunnlag.getBeregningsgrunnlagPerioder().get(0).getDagsats() < beregningsgrunnlag.getBeregningsgrunnlagPerioder().get(0).getDagsats();
    }

    public static long finnBrutto(Beregningsgrunnlag beregningsgrunnlag) {
        AtomicLong sum = new AtomicLong();
        beregningsgrunnlag.getBeregningsgrunnlagPerioder().get(0)
                .getBeregningsgrunnlagPrStatusOgAndelList()
                .forEach(andel -> {
                    if (andel.getAvkortetPrÅr() != null) {
                        sum.addAndGet(andel.getAvkortetPrÅr().longValue());
                    } else if (andel.getBruttoPrÅr() != null) {
                        sum.addAndGet(andel.getBruttoPrÅr().longValue());
                    }
                });
        return sum.get();
    }

    static List<BeregningsgrunnlagPrStatusOgAndel> finnAktivitetStatuserForAndeler(BeregningsgrunnlagAktivitetStatus bgAktivitetStatus, List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe) {
        if (kombinerteStatuser.contains(bgAktivitetStatus.getAktivitetStatus())) {
            List<String> relevanteStatuser = kombinerteRegelStatuserMap.get(bgAktivitetStatus.getAktivitetStatus());
            return bgpsaListe.stream().filter(andel -> relevanteStatuser.contains(andel.getAktivitetStatus())).collect(Collectors.toList());
        }
        return bgpsaListe.stream().filter(andel -> bgAktivitetStatus.getAktivitetStatus().equals(andel.getAktivitetStatus())).collect(Collectors.toList());
    }

    static AndelType lagAndelType(BeregningsgrunnlagPrStatusOgAndel andel) {
        ObjectFactory objectFactory = new ObjectFactory();
        AndelType andelType = objectFactory.createAndelType();
        andelType.setStatus(tilStatusTypeKode(andel.getAktivitetStatus()));
        andelType.setDagsats(andel.getOriginalDagsatsFraTilstøtendeYtelse() == null ? andel.getDagsats() : andel.getOriginalDagsatsFraTilstøtendeYtelse());
        BigDecimal bgBruttoPrÅr = andel.getAvkortetPrÅr() != null ? andel.getAvkortetPrÅr() : andel.getBruttoPrÅr();
        if (bgBruttoPrÅr != null) {
            andelType.setMånedsinntekt(andel.getBruttoPrÅr().divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP).longValue());
            andelType.setÅrsinntekt(andel.getBruttoPrÅr().longValue());
        }
        if (AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE.getKode().equals(andel.getAktivitetStatus())) {
            andelType.setPensjonsgivendeInntekt(andel.getPgiSnitt() == null ? null : andel.getPgiSnitt().longValue());
            andelType.setSisteLignedeÅr(andel.getBeregningsperiodeTom() == null ? null : (long) andel.getBeregningsperiodeTom().getYear());
        }
        if (AktivitetStatus.ARBEIDSTAKER.getKode().equals(andel.getAktivitetStatus())) {
            andel.getBgAndelArbeidsforhold().flatMap(BGAndelArbeidsforhold::getArbeidsgiver).map(Arbeidsgiver::getNavn).ifPresent(andelType::setArbeidsgiverNavn);
            if (OpptjeningAktivitetType.ETTERLØNN_SLUTTPAKKE.getKode().equals(andel.getOpptjeningAktivitetType())) {
                andelType.setEtterlønnSluttpakke(true);
            }
        }

        return andelType;
    }

    static BigInteger tellAntallArbeidsforholdIBeregning(List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe) {
        return BigInteger.valueOf(bgpsaListe.stream()
                .filter(bgpsa -> AktivitetStatus.ARBEIDSTAKER.getKode().equals(bgpsa.getAktivitetStatus()))
                .count());
    }

    static boolean harNoenAvAndeleneBesteberegning(List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe) {
        return bgpsaListe.stream()
                .anyMatch(andel -> andel.getBesteberegningPrÅr() != null);
    }

    static Boolean nyoppstartetSelvstendingNæringsdrivende(List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe) {
        return bgpsaListe.stream()
                .filter(andel -> AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE.getKode().equals(andel.getAktivitetStatus()))
                .filter(andel -> andel.getNyIArbeidslivet() != null)
                .map(BeregningsgrunnlagPrStatusOgAndel::getNyIArbeidslivet)
                .findFirst()
                .orElse(null);
    }

    static AndelListeType mapAndelListe(List<BeregningsgrunnlagPrStatusOgAndel> bgpsaListe) {
        //Litt stygt å ha disse her, spesielt uten qualifiers
        ObjectFactory objectFactory = new ObjectFactory();
        AndelListeType andelListeType = objectFactory.createAndelListeType();
        bgpsaListe.forEach(bgpsa -> andelListeType.getAndel().add(lagAndelType(bgpsa)));
        return andelListeType;
    }

    private Beregningsgrunnlag hentBeregningsgrunnlag(Behandling behandling) {
        behandlingRestKlient.hentBeregningsgrunnlag(behandling.getResourceLinkDtos());
        //TODO Aleksander
        return null;
    }

    static StatusTypeKode tilStatusTypeKode(String statuskode) {
        //TODO - Hvor mye koster det å bygge denne mappen hver gang? Virker unødvendig
        if (aktivitetStatusKodeStatusTypeKodeMap.containsKey(statuskode)) {
            return aktivitetStatusKodeStatusTypeKodeMap.get(statuskode);
        }
        throw new IllegalArgumentException("Utviklerfeil: Fant ikke riktig aktivitetstatus " + statuskode);
    }

    public boolean overstyrBeløpBeregning(Beregningsgrunnlag beregningsgrunnlag) {
        return beregningsgrunnlag.getBeregningsgrunnlagPerioder().get(0)
                .getBeregningsgrunnlagPrStatusOgAndelList()
                .stream()
                .anyMatch(andel -> andel.getOverstyrtPrÅr() != null);
    }

    void mapDataRelatertTilBeregningsgrunnlag(final Beregningsgrunnlag beregningsgrunnlag,
                                              final DokumentTypeMedPerioderDto dto) {
        BigDecimal seksG = beregningsgrunnlag.getGrunnbeløp().getVerdi().multiply(BigDecimal.valueOf(6));
        dto.getDokumentBeregningsgrunnlagDto().setSeksG(seksG.longValue());
        List<BeregningsgrunnlagPeriode> perioder = beregningsgrunnlag.getBeregningsgrunnlagPerioder();
        BeregningsgrunnlagPeriode førstePeriode = perioder.get(0);
        dto.getDokumentBeregningsgrunnlagDto().setDagsats(førstePeriode.getDagsats() != null ? førstePeriode.getDagsats() : 0);
        dto.getDokumentBeregningsgrunnlagDto().setMånedsbeløp(førstePeriode.getBruttoPrÅr().divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP).longValue());
        dto.getDokumentBeregningsgrunnlagDto().setLovhjemmelBeregning(kodeverkRepository.finn(Hjemmel.class, beregningsgrunnlag.getHjemmel()).getNavn());
        dto.getDokumentBeregningsgrunnlagDto().setInntektOverSeksG(førstePeriode.getBruttoPrÅr().compareTo(seksG) > 0);

        for (BeregningsgrunnlagAktivitetStatus aktivitetStatus : beregningsgrunnlag.getAktivitetStatuser()) {
            dto.getDokumentBeregningsgrunnlagDto().addBeregningsgrunnlagRegel(mapRegel(kodeverkRepository.finn(AktivitetStatus.class, aktivitetStatus.getAktivitetStatus()), førstePeriode, dto));
        }
        dto.getDokumentBeregningsgrunnlagDto().setOverbetaling(false);
    }

    void mapDataRelatertTilBeregningsgrunnlag(final Beregningsgrunnlag beregningsgrunnlag,
                                              final Beregningsgrunnlag gammeltBeregningsgrunnlag,
                                              final DokumentTypeMedPerioderDto dto) {
        mapDataRelatertTilBeregningsgrunnlag(beregningsgrunnlag, dto);

        BeregningsgrunnlagPeriode gammelPeriode = gammeltBeregningsgrunnlag.getBeregningsgrunnlagPerioder().get(0);

        final Long nyDagsats = dto.getDokumentBeregningsgrunnlagDto().getDagsats();
        Long gammelDagsats = gammelPeriode.getDagsats() != null ? gammelPeriode.getDagsats() : 0;

        if (nyDagsats < gammelDagsats) {
            dto.getDokumentBeregningsgrunnlagDto().setOverbetaling(true);
        }
    }

    private BeregningsgrunnlagRegelDto mapRegel(AktivitetStatus aktivitetStatus, BeregningsgrunnlagPeriode periode, DokumentTypeMedPerioderDto dokumentDto) {
        BeregningsgrunnlagRegelDto dto = new BeregningsgrunnlagRegelDto();
        dto.setStatus(aktivitetStatus.getKode());
        Set<AktivitetStatus> aktivitetStatuserMedArbeidstaker = new HashSet<>();
        aktivitetStatuserMedArbeidstaker.add(AktivitetStatus.ARBEIDSTAKER);
        aktivitetStatuserMedArbeidstaker.add(AktivitetStatus.KOMBINERT_AT_FL_SN);
        aktivitetStatuserMedArbeidstaker.add(AktivitetStatus.KOMBINERT_AT_FL);
        aktivitetStatuserMedArbeidstaker.add(AktivitetStatus.KOMBINERT_AT_SN);

        if (aktivitetStatuserMedArbeidstaker.contains(aktivitetStatus)) {
            //Feltet skal være antall arbeidsforhold - ikke antall arbeidsgivere. Burde få nytt navn i ny XSD
            dto.setAntallArbeidsgivereIBeregning((int) periode.getBeregningsgrunnlagPrStatusOgAndelList().stream()
                    .filter(bgPrStatusOgAndel -> bgPrStatusOgAndel.getAktivitetStatus().equals(AktivitetStatus.ARBEIDSTAKER))
                    .count());
        } else {
            dto.setAntallArbeidsgivereIBeregning(0);
        }
        // Håndter ev enkeltstatus
        if (mapEnkeltstatus(aktivitetStatus, periode, dto, dokumentDto)) {
            return dto;
        }
        // Håndter sammensatt status
        if (AktivitetStatus.KOMBINERT_AT_FL.equals(aktivitetStatus)) {
            mapEnkeltstatus(AktivitetStatus.FRILANSER, periode, dto, dokumentDto);
            mapEnkeltstatus(AktivitetStatus.ARBEIDSTAKER, periode, dto, dokumentDto);
        } else if (AktivitetStatus.KOMBINERT_AT_FL_SN.equals(aktivitetStatus)) {
            mapEnkeltstatus(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE, periode, dto, dokumentDto);
            mapEnkeltstatus(AktivitetStatus.FRILANSER, periode, dto, dokumentDto);
            mapEnkeltstatus(AktivitetStatus.ARBEIDSTAKER, periode, dto, dokumentDto);
        } else if (AktivitetStatus.KOMBINERT_AT_SN.equals(aktivitetStatus)) {
            mapEnkeltstatus(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE, periode, dto, dokumentDto);
            mapEnkeltstatus(AktivitetStatus.ARBEIDSTAKER, periode, dto, dokumentDto);
        } else if (AktivitetStatus.KOMBINERT_FL_SN.equals(aktivitetStatus)) {
            mapEnkeltstatus(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE, periode, dto, dokumentDto);
            mapEnkeltstatus(AktivitetStatus.FRILANSER, periode, dto, dokumentDto);
        }
        return dto;
    }

    private boolean mapEnkeltstatus(AktivitetStatus aktivitetStatus, BeregningsgrunnlagPeriode periode, BeregningsgrunnlagRegelDto dto, DokumentTypeMedPerioderDto dokumentDto) {
        boolean funnet = false;
        for (BeregningsgrunnlagPrStatusOgAndel andel : periode.getBeregningsgrunnlagPrStatusOgAndelList()) {
            if (aktivitetStatus.equals(andel.getAktivitetStatus())) {
                funnet = true;
                mapMatchetStatus(dto, andel, dokumentDto);
            } else if (aktivitetStatus.equals(AktivitetStatus.KUN_YTELSE)) {
                if (!RelatertYtelseType.UDEFINERT.equals(andel.getYtelse())) {
                    funnet = true;
                }
                mapMatchetStatus(dto, andel, dokumentDto);
            }
        }
        return funnet;
    }

    private void mapMatchetStatus(BeregningsgrunnlagRegelDto dto, BeregningsgrunnlagPrStatusOgAndel andel, DokumentTypeMedPerioderDto dokumentDto) {
        dto.addBeregningsgrunnlagAndelDto(mapAndel(andel, dokumentDto, dto));
    }

    private BeregningsgrunnlagAndelDto mapAndel(BeregningsgrunnlagPrStatusOgAndel andel, DokumentTypeMedPerioderDto dokumentDto, BeregningsgrunnlagRegelDto regelDto) {
        final BeregningsgrunnlagAndelDto dto = new BeregningsgrunnlagAndelDto();
        dto.setStatus(kodeverkRepository.finn(AktivitetStatus.class, andel.getAktivitetStatus()).getKode());
        if (AktivitetStatus.ARBEIDSTAKER.equals(andel.getAktivitetStatus())) {
            if (OpptjeningAktivitetType.ETTERLØNN_SLUTTPAKKE.equals(andel.getArbeidsforholdType())) {
                dto.setEtterlønnSluttpakke(Boolean.TRUE.toString());
            }
            andel.getBgAndelArbeidsforhold().flatMap(BGAndelArbeidsforhold::getArbeidsgiver).ifPresent(arbeidsgiver ->
                    dto.setArbeidsgiverNavn(finnArbeidsgivernavn(arbeidsgiver)));
        }
        dto.setDagsats(String.valueOf(andel.getOriginalDagsatsFraTilstøtendeYtelse() == null ? andel.getDagsats() : andel.getOriginalDagsatsFraTilstøtendeYtelse()));
        if (andel.getBruttoPrÅr() != null) {
            dto.setMånedsinntekt(String.valueOf(andel.getBruttoPrÅr().divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_UP).longValue()));
            dto.setÅrsinntekt(String.valueOf(andel.getBruttoPrÅr().longValue()));
            dokumentDto.addToBruttoBeregningsgrunnlag(andel.getBruttoPrÅr().longValue());
        }
        if (andel.getBesteberegningPrÅr() != null) {
            regelDto.setBesteBeregning(true);
        }
        if (andel.getOverstyrtPrÅr() != null) {
            dokumentDto.setOverstyrtBeløpBeregning(true);
        }
        if (AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE.equals(andel.getAktivitetStatus())) {
            dto.setPensjonsgivendeInntekt(andel.getPgiSnitt() == null ? null : String.valueOf(andel.getPgiSnitt().longValue()));
            dto.setSisteLignedeÅr(andel.getBeregningsperiodeTom() == null ? null : String.valueOf(andel.getBeregningsperiodeTom().getYear()));
            if (andel.getNyIArbeidslivet() != null) {
                regelDto.setSNnyoppstartet(andel.getNyIArbeidslivet().toString());
            }
        }
        return dto;
    }

    private String finnArbeidsgivernavn(Arbeidsgiver arbeidsgiver) {
        if (arbeidsgiver.erAktørId()) {
            Optional<Personinfo> personinfo = tpsTjeneste.hentBrukerForAktør(arbeidsgiver.getAktørId());
            if (!personinfo.isPresent()) {
                throw new IllegalStateException("Finner ikke arbeidsgiver i TPS");
            }
            return personinfo.get().getNavn();
        } else if (arbeidsgiver.getErVirksomhet()) {
            return arbeidsgiver.getVirksomhet().getNavn();
        }
        throw new IllegalStateException("Klarer ikke identifisere arbeidsgiver under mapping fra beregningsgrunnlag til dokument");
    }
}

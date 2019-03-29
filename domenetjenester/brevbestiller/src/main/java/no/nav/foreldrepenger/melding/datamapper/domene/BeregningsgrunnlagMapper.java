package no.nav.foreldrepenger.melding.datamapper.domene;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.aktør.Personinfo;
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
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.opptjening.OpptjeningAktivitetType;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;
import no.nav.foreldrepenger.tps.TpsTjeneste;

@ApplicationScoped
public class BeregningsgrunnlagMapper {
    private TpsTjeneste tpsTjeneste;
    private KodeverkRepository kodeverkRepository;

    BeregningsgrunnlagMapper() {
        // CDI Proxy
    }

    @Inject
    public BeregningsgrunnlagMapper(TpsTjeneste tpsTjeneste, KodeverkRepository kodeverkRepository) {
        this.tpsTjeneste = tpsTjeneste;
        this.kodeverkRepository = kodeverkRepository;
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
        dto.setDagsats(String.valueOf(andel.getOrginalDagsatsFraTilstøtendeYtelse() == null ? andel.getDagsats() : andel.getOrginalDagsatsFraTilstøtendeYtelse()));
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

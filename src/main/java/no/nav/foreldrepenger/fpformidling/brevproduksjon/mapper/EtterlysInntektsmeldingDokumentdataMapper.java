package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.arbeidsgiver.ArbeidsgiverTjeneste;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.domene.inntektarbeidytelse.ArbeidsforholdInntektsmelding;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.EtterlysInntektsmeldingDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.kontrakter.fpsak.inntektsmeldinger.ArbeidsforholdInntektsmeldingerDto;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.ETTERLYS_INNTEKTSMELDING)
public class EtterlysInntektsmeldingDokumentdataMapper implements DokumentdataMapper {

    private static final Logger SECURE_LOG = LoggerFactory.getLogger("secureLogger");
    private final ArbeidsgiverTjeneste arbeidsgiverTjeneste;

    @Inject
    public EtterlysInntektsmeldingDokumentdataMapper(ArbeidsgiverTjeneste arbeidsgiverTjeneste) {
        this.arbeidsgiverTjeneste = arbeidsgiverTjeneste;
    }

    @Override
    public String getTemplateNavn() {
        return "etterlys-inntektsmelding";
    }

    @Override
    public EtterlysInntektsmeldingDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                                  DokumentHendelse hendelse,
                                                                  BrevGrunnlagDto behandling,
                                                                  boolean erUtkast) {
        var språkkode = dokumentFelles.getSpråkkode();

        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), språkkode) : null);

        var søknadDato = behandling.sisteSøknadMottattDato();
        var inntektsmeldingerStatus = Optional.ofNullable(behandling.inntektsmeldingerStatus())
            .map(ArbeidsforholdInntektsmeldingerDto::arbeidsforholdInntektsmelding)
            .orElse(List.of()).stream().map(this::map).toList();

        if (inntektsmeldingerStatus.isEmpty() || inntektsmeldingerStatus.stream().allMatch(
            ArbeidsforholdInntektsmelding::erInntektsmeldingMottatt)) {
            SECURE_LOG.info("Liste over påkrevde inntektsmeldinger og status for disse: {}", inntektsmeldingerStatus);
            throw new IllegalStateException("Kan ikke etterlyse inntektsmeldinger når ingen innteksmeldinger mangler");
        }

        var dokumentdataBuilder = EtterlysInntektsmeldingDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medSøknadDato(formaterDato(søknadDato, språkkode))
            .medAntallIkkeMottattIM((int) inntektsmeldingerStatus.stream().filter(imStatus -> !imStatus.erInntektsmeldingMottatt()).count())
            .medAntallMottattIM((int) inntektsmeldingerStatus.stream().filter(ArbeidsforholdInntektsmelding::erInntektsmeldingMottatt).count())
            .medInntektsmeldingerStatus(inntektsmeldingerStatus);

        return dokumentdataBuilder.build();
    }

    private ArbeidsforholdInntektsmelding map(ArbeidsforholdInntektsmeldingerDto.ArbeidsforholdInntektsmeldingDto is) {
        return new ArbeidsforholdInntektsmelding(is.arbeidsgiverIdent(), arbeidsgiverTjeneste.hentArbeidsgiverNavn(is.arbeidsgiverIdent()),
            is.stillingsprosent(), is.erInntektsmeldingMottatt());
    }
}

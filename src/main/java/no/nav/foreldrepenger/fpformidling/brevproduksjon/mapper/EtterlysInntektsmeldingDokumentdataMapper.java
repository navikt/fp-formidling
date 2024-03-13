package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.time.LocalDate;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.MottattdokumentMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.domene.inntektarbeidytelse.ArbeidsforholdInntektsmelding;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.EtterlysInntektsmeldingDokumentdata;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.ETTERLYS_INNTEKTSMELDING)
public class EtterlysInntektsmeldingDokumentdataMapper implements DokumentdataMapper {

    private DomeneobjektProvider domeneobjektProvider;

    EtterlysInntektsmeldingDokumentdataMapper() {
        //CDI
    }

    @Inject
    public EtterlysInntektsmeldingDokumentdataMapper(DomeneobjektProvider domeneobjektProvider) {
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String getTemplateNavn() {
        return "etterlys-inntektsmelding";
    }

    @Override
    public EtterlysInntektsmeldingDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                                  DokumentHendelse hendelse,
                                                                  Behandling behandling,
                                                                  boolean erUtkast) {
        var språkkode = behandling.getSpråkkode();

        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, behandling, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), språkkode) : null);

        var søknadDato = getSøknadsdato(behandling);
        var inntektsmeldingerStatus = domeneobjektProvider.hentArbeidsforholdInntektsmeldingerStatus(behandling);

        if (inntektsmeldingerStatus.isEmpty() || inntektsmeldingerStatus.stream().allMatch(ArbeidsforholdInntektsmelding::erInntektsmeldingMottatt)) {
            throw new IllegalStateException("Kan ikke etterlyse inntektsmeldinger når ingen innteksmeldinger mangler");
        }

        var dokumentdataBuilder = EtterlysInntektsmeldingDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medSøknadDato(formaterDato(søknadDato, språkkode))
            .medAntallIkkeMottattIM((int)inntektsmeldingerStatus.stream().filter(imStatus -> !imStatus.erInntektsmeldingMottatt()).count())
            .medAntallMottattIM((int)inntektsmeldingerStatus.stream().filter(ArbeidsforholdInntektsmelding::erInntektsmeldingMottatt).count())
            .medInntektsmeldingerStatus(inntektsmeldingerStatus);

        return dokumentdataBuilder.build();
    }

    private LocalDate getSøknadsdato(Behandling behandling) {
        var mottatteDokumenter = domeneobjektProvider.hentMottatteDokumenter(behandling);
        return MottattdokumentMapper.finnSisteMottatteSøknad(mottatteDokumenter);
    }
}

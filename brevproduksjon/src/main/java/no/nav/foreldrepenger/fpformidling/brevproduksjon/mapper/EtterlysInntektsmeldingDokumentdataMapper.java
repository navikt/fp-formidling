package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.time.LocalDate;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.MottattdokumentMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.EtterlysInntektsmeldingDokumentdata;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalTypeKode;
import no.nav.foreldrepenger.fpformidling.mottattdokument.MottattDokument;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.ETTERLYS_INNTEKTSMELDING)
public class EtterlysInntektsmeldingDokumentdataMapper implements DokumentdataMapper {

    private DomeneobjektProvider domeneobjektProvider;
    private BrevMapperUtil brevMapperUtil;

    EtterlysInntektsmeldingDokumentdataMapper() {
        //CDI
    }

    @Inject
    public EtterlysInntektsmeldingDokumentdataMapper(DomeneobjektProvider domeneobjektProvider,
                                                     BrevMapperUtil brevMapperUtil) {
        this.domeneobjektProvider = domeneobjektProvider;
        this.brevMapperUtil = brevMapperUtil;
    }

    @Override
    public String getTemplateNavn() {
        return "etterlys-inntektsmelding";
    }

    @Override
    public EtterlysInntektsmeldingDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse,
                                                   Behandling behandling, boolean erUtkast) {
        Språkkode språkkode = behandling.getSpråkkode();

        var fellesBuilder = opprettFellesBuilder(dokumentFelles, hendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), språkkode) : null);

        LocalDate søknadDato = getSøknadsdato(behandling);
        LocalDate fristDato = brevMapperUtil.getSvarFrist();

        var dokumentdataBuilder = EtterlysInntektsmeldingDokumentdata.ny()
                .medFelles(fellesBuilder.build())
                .medSøknadDato(formaterDato(søknadDato, språkkode))
                .medFristDato(formaterDato(fristDato, språkkode));

        return dokumentdataBuilder.build();
    }

    private LocalDate getSøknadsdato(Behandling behandling) {
        List<MottattDokument> mottatteDokumenter = domeneobjektProvider.hentMottatteDokumenter(behandling);
        return MottattdokumentMapper.finnSisteMottatteSøknad(mottatteDokumenter);
    }
}

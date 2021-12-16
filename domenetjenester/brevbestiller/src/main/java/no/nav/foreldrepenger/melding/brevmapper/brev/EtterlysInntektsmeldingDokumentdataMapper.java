package no.nav.foreldrepenger.melding.brevmapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDato;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDatoNorsk;

import java.time.LocalDate;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.MottattdokumentMapper;
import no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.EtterlysInntektsmeldingDokumentdata;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;
import no.nav.foreldrepenger.melding.mottattdokument.MottattDokument;

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
        return MottattdokumentMapper.finnSøknadsdatoFraMottatteDokumenter(behandling, mottatteDokumenter);
    }
}

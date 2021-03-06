package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.typer.Dato.medFormatering;

import java.time.LocalDate;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.MottattdokumentMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;
import no.nav.foreldrepenger.melding.mottattdokument.MottattDokument;

@ApplicationScoped
@Named(DokumentMalTypeKode.ETTERLYS_INNTEKTSMELDING_DOK)
public class EtterlysInntektsmeldingBrevMapper extends FritekstmalBrevMapper {

    private BrevMapperUtil brevMapperUtil;

    public EtterlysInntektsmeldingBrevMapper() {
        //CDI
    }

    @Inject
    public EtterlysInntektsmeldingBrevMapper(BrevParametere brevParametere,
                                             DomeneobjektProvider domeneobjektProvider,
                                             BrevMapperUtil brevMapperUtil) {
        super(brevParametere, domeneobjektProvider);
        this.brevMapperUtil = brevMapperUtil;
    }

    @Override
    public String displayName() {
        return "Etterlys inntektsmelding";
    }

    @Override
    public String templateFolder() {
        return "etterlysinntektsmelding";
    }

    @Override
    Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling) {
        LocalDate soknadDato = getSøknadsdato(behandling);
        LocalDate fristDato = brevMapperUtil.getSvarFrist();

        return new Brevdata()
                .leggTil("ytelse", hendelse.getYtelseType().getKode())
                .leggTil("soknadDato", medFormatering(soknadDato))
                .leggTil("fristDato", medFormatering(fristDato))
                .leggTil("erAutomatiskVedtak", Boolean.FALSE); // For å unngå automatiskVedtakMvh_001 - bør skille informasjon/vedtak i tillegg til automatisk
    }

    private LocalDate getSøknadsdato(Behandling behandling) {
        List<MottattDokument> mottatteDokumenter = domeneobjektProvider.hentMottatteDokumenter(behandling);
        return MottattdokumentMapper.finnSøknadsdatoFraMottatteDokumenter(behandling, mottatteDokumenter);
    }
}

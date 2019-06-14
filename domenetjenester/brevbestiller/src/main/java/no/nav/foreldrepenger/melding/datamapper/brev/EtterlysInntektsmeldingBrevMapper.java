package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.BrevMapperUtil.medFormatering;

import java.time.LocalDate;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.datatype.XMLGregorianCalendar;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.BrevMapperUtil;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.MottattdokumentMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeVerktøy;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.mottattdokument.MottattDokument;

@ApplicationScoped
@Named(DokumentMalType.ETTERLYS_INNTEKTSMELDING_DOK)
public class EtterlysInntektsmeldingBrevMapper extends FritekstmalBrevMapper {

    public EtterlysInntektsmeldingBrevMapper() {
        //CDI
    }

    @Inject
    public EtterlysInntektsmeldingBrevMapper(BrevParametere brevParametere, DomeneobjektProvider domeneobjektProvider) {
        super(brevParametere, domeneobjektProvider);
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
        LocalDate fristDato = BrevMapperUtil.getSvarFrist(brevParametere);

        return new Brevdata()
                .leggTil("ytelse", hendelse.getYtelseType().getKode())
                .leggTil("soknadDato", medFormatering(soknadDato))
                .leggTil("fristDato", medFormatering(fristDato));
    }

    private LocalDate getSøknadsdato(Behandling behandling) {
        List<MottattDokument> mottatteDokumenter = domeneobjektProvider.hentMottatteDokumenter(behandling);
        XMLGregorianCalendar søknadsDato = MottattdokumentMapper.finnSøknadsDatoFraMottatteDokumenter(behandling, mottatteDokumenter);
        return PeriodeVerktøy.xmlGregorianTilLocalDate(søknadsDato);
    }
}

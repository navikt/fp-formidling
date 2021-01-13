package no.nav.foreldrepenger.melding.datamapper.brev;

import java.math.BigInteger;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.datamapper.DokumentMapperFeil;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.IAYMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.InntektArbeidYtelse;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.Inntektsmelding;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.inntektsmeldingfortidlig.BehandlingsTypeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.inntektsmeldingfortidlig.BrevdataType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.inntektsmeldingfortidlig.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.inntektsmeldingfortidlig.InntektsmeldingForTidligConstants;
import no.nav.foreldrepenger.melding.integrasjon.dokument.inntektsmeldingfortidlig.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.inntektsmeldingfortidlig.PeriodeListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.inntektsmeldingfortidlig.PeriodeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.inntektsmeldingfortidlig.YtelseTypeKode;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;
import no.nav.foreldrepenger.melding.ytelsefordeling.UtsettelseÅrsak;
import no.nav.vedtak.felles.integrasjon.felles.ws.JaxbHelper;

@ApplicationScoped
@Named(DokumentMalTypeKode.INNTEKTSMELDING_FOR_TIDLIG_DOK)
public class InntektsmeldingFørSøknadBrevMapper extends DokumentTypeMapper {

    private ObjectFactory objectFactory = new ObjectFactory();

    private BrevParametere brevParametere;

    public InntektsmeldingFørSøknadBrevMapper() {
        //CDI
    }

    @Inject
    public InntektsmeldingFørSøknadBrevMapper(DomeneobjektProvider domeneobjektProvider, BrevParametere brevParametere) {
        this.domeneobjektProvider = domeneobjektProvider;
        this.brevParametere = brevParametere;
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType, DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse, Behandling behandling) throws JAXBException, SAXException, XMLStreamException {
        InntektArbeidYtelse iay = domeneobjektProvider.hentInntektArbeidYtelse(behandling);
        FagType fagType = mapFagType(behandling, iay, dokumentHendelse);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        return JaxbHelper.marshalNoNamespaceXML(InntektsmeldingForTidligConstants.JAXB_CLASS, brevdataTypeJAXBElement, null);
    }

    FagType mapFagType(Behandling behandling, InntektArbeidYtelse iay, DokumentHendelse dokumentHendelse) {
        FagType fagType = new FagType();
        fagType.setBehandlingsType(mapToXmlBehandlingsType(behandling.getBehandlingType()));
        Inntektsmelding inntektsmelding = IAYMapper.hentNyesteInntektsmelding(iay);
        fagType.setSokAntallUkerFor(BigInteger.valueOf(brevParametere.getSøkAntallUker()));
        fagType.setArbeidsgiverNavn(inntektsmelding.getArbeidsgiverNavn());
        fagType.setMottattDato(XmlUtil.finnDatoVerdiAvUtenTidSone(inntektsmelding.getInnsendingstidspunkt()));
        fagType.setPeriodeListe(mapFeriePerioder(inntektsmelding));
        fagType.setYtelseType(YtelseTypeKode.fromValue(dokumentHendelse.getYtelseType().getKode()));
        return fagType;
    }

    static BehandlingsTypeKode mapToXmlBehandlingsType(BehandlingType behandlingType) {
        if (Objects.equals(behandlingType, BehandlingType.FØRSTEGANGSSØKNAD)) {
            return BehandlingsTypeKode.FOERSTEGANGSBEHANDLING;
        } else if (Objects.equals(behandlingType, BehandlingType.REVURDERING)) {
            return BehandlingsTypeKode.REVURDERING;
        }
        throw DokumentMapperFeil.FACTORY.innhentDokumentasjonKreverGyldigBehandlingstype(behandlingType.getKode()).toException();
    }

    private JAXBElement<BrevdataType> mapintoBrevdataType(FellesType fellesType, FagType fagType) {
        ObjectFactory of = new ObjectFactory();
        BrevdataType brevdataType = of.createBrevdataType();
        brevdataType.setFag(fagType);
        brevdataType.setFelles(fellesType);
        return of.createBrevdata(brevdataType);
    }

    private PeriodeListeType mapFeriePerioder(Inntektsmelding inntektsmelding) {
        PeriodeListeType periodeListe = objectFactory.createPeriodeListeType();
        inntektsmelding.getUtsettelsePerioder()
                .stream()
                .filter(up -> UtsettelseÅrsak.FERIE.equals(up.getUtsettelseÅrsak()))
                .forEach(up -> {
                    PeriodeType periode = objectFactory.createPeriodeType();
                    periode.setPeriodeFom(XmlUtil.finnDatoVerdiAvUtenTidSone(up.getFom()));
                    periode.setPeriodeTom(XmlUtil.finnDatoVerdiAvUtenTidSone(up.getTom()));
                    periodeListe.getPeriode().add(periode);
                });
        return periodeListe;
    }

}

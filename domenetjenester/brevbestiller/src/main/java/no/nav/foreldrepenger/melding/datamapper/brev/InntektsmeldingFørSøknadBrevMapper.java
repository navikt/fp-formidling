package no.nav.foreldrepenger.melding.datamapper.brev;

import java.math.BigInteger;
import java.time.LocalDate;
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
import no.nav.foreldrepenger.melding.datamapper.domene.IAYMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
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
import no.nav.foreldrepenger.melding.ytelsefordeling.UtsettelseÅrsak;
import no.nav.vedtak.felles.integrasjon.felles.ws.JaxbHelper;

@ApplicationScoped
@Named(DokumentMalType.INNTEKTSMELDING_FOR_TIDLIG_DOK)
public class InntektsmeldingFørSøknadBrevMapper implements DokumentTypeMapper {

    private ObjectFactory objectFactory;

    private IAYMapper iayMapper;
    private BrevParametere brevParametere;

    public InntektsmeldingFørSøknadBrevMapper() {
        //CDI
    }

    @Inject
    public InntektsmeldingFørSøknadBrevMapper(IAYMapper iayMapper, BrevParametere brevParametere) {
        this.iayMapper = iayMapper;
        this.brevParametere = brevParametere;
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType, DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse, Behandling behandling) throws JAXBException, SAXException, XMLStreamException {
        FagType fagType = mapFagType(behandling);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        return JaxbHelper.marshalNoNamespaceXML(InntektsmeldingForTidligConstants.JAXB_CLASS, brevdataTypeJAXBElement, null);
    }

    private FagType mapFagType(Behandling behandling) {
        FagType fagType = new FagType();
        fagType.setBehandlingsType(mapToXmlBehandlingsType(behandling.getBehandlingType()));
        InntektArbeidYtelse iay = iayMapper.hentInntektArbeidYtelse(behandling);
        Inntektsmelding inntektsmelding = IAYMapper.hentVillkårligInntektsmelding(iay);
        fagType.setSokAntallUkerFor(BigInteger.valueOf(brevParametere.getSøkAntallUker()));
        fagType.setArbeidsgiverNavn(inntektsmelding.getArbeidsgiver());
        //TODO mangler denne verdien..
        fagType.setMottattDato(XmlUtil.finnDatoVerdiAvUtenTidSone(LocalDate.now()));
        fagType.setPeriodeListe(mapFeriePerioder(inntektsmelding));
        return fagType;
    }

    static BehandlingsTypeKode mapToXmlBehandlingsType(String vlKode) {
        if (Objects.equals(vlKode, BehandlingType.FØRSTEGANGSSØKNAD.getKode())) {
            return BehandlingsTypeKode.FOERSTEGANGSBEHANDLING;
        } else if (Objects.equals(vlKode, BehandlingType.REVURDERING.getKode())) {
            return BehandlingsTypeKode.REVURDERING;
        }
        throw DokumentMapperFeil.FACTORY.innhentDokumentasjonKreverGyldigBehandlingstype(vlKode).toException();
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
                .filter(up -> UtsettelseÅrsak.FERIE.getKode().equals(up.getUtsettelseÅrsak()))
                .forEach(up -> {
                    PeriodeType periode = objectFactory.createPeriodeType();
                    periode.setPeriodeFom(XmlUtil.finnDatoVerdiAvUtenTidSone(up.getFom()));
                    periode.setPeriodeTom(XmlUtil.finnDatoVerdiAvUtenTidSone(up.getTom()));
                    periodeListe.getPeriode().add(periode);
                });
        return periodeListe;
    }

}

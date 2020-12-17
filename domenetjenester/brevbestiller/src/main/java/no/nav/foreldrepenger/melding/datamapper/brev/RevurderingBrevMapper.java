package no.nav.foreldrepenger.melding.datamapper.brev;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.RevurderingVarslingÅrsak;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.revurdering.AdvarselKodeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.revurdering.BrevdataType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.revurdering.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.revurdering.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.revurdering.RevurderingConstants;
import no.nav.foreldrepenger.melding.integrasjon.dokument.revurdering.YtelseTypeKode;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;
import no.nav.vedtak.felles.integrasjon.felles.ws.JaxbHelper;
import no.nav.vedtak.util.StringUtils;
import org.xml.sax.SAXException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

@ApplicationScoped
@Named(DokumentMalTypeKode.REVURDERING_DOK)
public class RevurderingBrevMapper extends DokumentTypeMapper {

    private BrevMapperUtil brevMapperUtil;

    public RevurderingBrevMapper() {
        //CDI
    }

    @Inject
    public RevurderingBrevMapper(DomeneobjektProvider domeneobjektProvider,
                                 BrevMapperUtil brevMapperUtil) {
        this.domeneobjektProvider = domeneobjektProvider;
        this.brevMapperUtil = brevMapperUtil;
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType, DokumentFelles dokumentFelles, DokumentHendelse hendelse, Behandling behandling) throws JAXBException, XMLStreamException, SAXException {
        FamilieHendelse familieHendelse = domeneobjektProvider.hentFamiliehendelse(behandling);
        FagType fagType = mapFagType(fellesType, hendelse, familieHendelse);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        return JaxbHelper.marshalNoNamespaceXML(RevurderingConstants.JAXB_CLASS, brevdataTypeJAXBElement, null);
    }

    FagType mapFagType(FellesType fellesType, DokumentHendelse hendelse, FamilieHendelse familieHendelse) {
        final FagType fagType = new FagType();
        fagType.setYtelseType(YtelseTypeKode.fromValue(hendelse.getYtelseType().getKode()));
        fagType.setFristDato(XmlUtil.finnDatoVerdiAvUtenTidSone(brevMapperUtil.getSvarFrist()));
        fagType.setAdvarselKode(utledAdvarselkode(hendelse));
        fagType.setFritekst(hendelse.getFritekst());
        fagType.setAntallBarn(familieHendelse.getAntallBarn());
        familieHendelse.getTermindato().map(XmlUtil::finnDatoVerdiAvUtenTidSone).ifPresent(fagType::setTerminDato);
        fellesType.setAutomatiskBehandlet(StringUtils.nullOrEmpty(hendelse.getFritekst()));
        return fagType;
    }

    private AdvarselKodeKode utledAdvarselkode(DokumentHendelse hendelse) {
        if (hendelse.getRevurderingVarslingÅrsak().equals(RevurderingVarslingÅrsak.UDEFINERT)) {
            if (!StringUtils.nullOrEmpty(hendelse.getFritekst())) {
                return AdvarselKodeKode.fromValue(RevurderingVarslingÅrsak.ANNET.getKode());
            }
            return null;
        }
        return AdvarselKodeKode.fromValue(hendelse.getRevurderingVarslingÅrsak().getKode());
    }

    private JAXBElement<BrevdataType> mapintoBrevdataType(FellesType fellesType, FagType fagType) {
        ObjectFactory of = new ObjectFactory();
        BrevdataType brevdataType = of.createBrevdataType();
        brevdataType.setFag(fagType);
        brevdataType.setFelles(fellesType);
        return of.createBrevdata(brevdataType);
    }

}

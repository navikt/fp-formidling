package no.nav.foreldrepenger.melding.datamapper.brev;

import java.math.BigInteger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.RevurderingVarslingÅrsak;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.datamapper.BrevMapperUtil;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeFelles;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.FamiliehendelseMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.revurdering.AdvarselKodeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.revurdering.BrevdataType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.revurdering.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.revurdering.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.revurdering.RevurderingConstants;
import no.nav.foreldrepenger.melding.integrasjon.dokument.revurdering.YtelseTypeKode;
import no.nav.vedtak.felles.integrasjon.felles.ws.JaxbHelper;
import no.nav.vedtak.util.StringUtils;

@ApplicationScoped
@Named(DokumentMalType.REVURDERING_DOK)
public class RevurderingBrevMapper implements DokumentTypeMapper {

    private FamiliehendelseMapper familiehendelseMapper;
    private BrevParametere brevParametere;

    public RevurderingBrevMapper() {
        //CDI
    }

    @Inject
    public RevurderingBrevMapper(FamiliehendelseMapper familiehendelseMapper,
                                 BrevParametere brevParametere) {
        this.familiehendelseMapper = familiehendelseMapper;
        this.brevParametere = brevParametere;
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType, DokumentFelles dokumentFelles, DokumentHendelse hendelse, Behandling behandling) throws JAXBException {
        FamilieHendelse familieHendelse = familiehendelseMapper.hentFamiliehendelse(behandling);
        FagType fagType = mapFagType(fellesType, hendelse, behandling, familieHendelse);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        String brevXmlMedNamespace = JaxbHelper.marshalJaxb(RevurderingConstants.JAXB_CLASS, brevdataTypeJAXBElement);
        return DokumentTypeFelles.fjernNamespaceFra(brevXmlMedNamespace);
    }

    private FagType mapFagType(FellesType fellesType, DokumentHendelse hendelse, Behandling behandling, FamilieHendelse familieHendelse) {
        final FagType fagType = new FagType();
        fagType.setYtelseType(YtelseTypeKode.fromValue(hendelse.getYtelseType().getKode()));
        fagType.setFristDato(XmlUtil.finnDatoVerdiAvUtenTidSone(BrevMapperUtil.getSvarFrist(brevParametere)));
        fagType.setAdvarselKode(utledAdvarselkode(hendelse));
        fagType.setFritekst(hendelse.getFritekst());
        fagType.setAntallBarn(BigInteger.valueOf(familieHendelse.getAntallBarn()));
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

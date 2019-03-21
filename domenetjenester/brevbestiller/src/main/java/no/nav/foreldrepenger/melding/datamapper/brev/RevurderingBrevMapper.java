package no.nav.foreldrepenger.melding.datamapper.brev;

import java.math.BigInteger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import no.nav.foreldrepenger.fpsak.BehandlingRestKlient;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.FamiliehendelseDto;
import no.nav.foreldrepenger.melding.behandling.RevurderingVarslingÅrsak;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.brevbestiller.api.dto.Behandling;
import no.nav.foreldrepenger.melding.datamapper.BrevMapperUtil;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeFelles;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.FamiliehendelseMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
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

    private BehandlingRestKlient behandlingRestKlient;
    private BrevParametere brevParametere;

    public RevurderingBrevMapper() {
        //CDI
    }

    @Inject
    public RevurderingBrevMapper(BehandlingRestKlient behandlingRestKlient,
                                 BrevParametere brevParametere) {
        this.behandlingRestKlient = behandlingRestKlient;
        this.brevParametere = brevParametere;
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType, DokumentFelles dokumentFelles, DokumentHendelse hendelse, Behandling behandling) throws JAXBException {
        FagType fagType = mapFagType(fellesType, hendelse, behandling);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        String brevXmlMedNamespace = JaxbHelper.marshalJaxb(RevurderingConstants.JAXB_CLASS, brevdataTypeJAXBElement);
        return DokumentTypeFelles.fjernNamespaceFra(brevXmlMedNamespace);
    }

    private FagType mapFagType(FellesType fellesType, DokumentHendelse hendelse, Behandling behandling) {
        final FagType fagType = new FagType();
        fagType.setYtelseType(YtelseTypeKode.fromValue(hendelse.getYtelseType().getKode()));
        fagType.setFristDato(XmlUtil.finnDatoVerdiAvUtenTidSone(BrevMapperUtil.getSvarFrist(brevParametere)));
        mapFamiliehendelse(fagType, behandling);
        fagType.setAdvarselKode(utledAdvarselkode(hendelse));
        fagType.setFritekst(hendelse.getFritekst());
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

    private void mapFamiliehendelse(FagType fagType, Behandling behandling) {
        FamiliehendelseDto dto = behandlingRestKlient.hentFamiliehendelse(behandling.getResourceLinkDtos()).orElseThrow(IllegalStateException::new);
        fagType.setAntallBarn(BigInteger.valueOf(FamiliehendelseMapper.utledAntallBarnFraDto(dto)));
        FamiliehendelseMapper.finnTermindato(dto).ifPresent(fagType::setTerminDato);
    }


}

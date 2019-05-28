package no.nav.foreldrepenger.melding.datamapper.brev;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

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
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.MottattdokumentMapper;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.forlenget.BrevdataType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.forlenget.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.forlenget.ForlengetConstants;
import no.nav.foreldrepenger.melding.integrasjon.dokument.forlenget.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.forlenget.PersonstatusKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.forlenget.VariantKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.forlenget.YtelseTypeKode;
import no.nav.foreldrepenger.melding.mottattdokument.MottattDokument;
import no.nav.foreldrepenger.melding.søknad.Søknad;
import no.nav.vedtak.felles.integrasjon.felles.ws.JaxbHelper;

@ApplicationScoped
@Named(DokumentMalType.FORLENGET_DOK)
public class ForlengetSaksbehandlingstidBrevMapper implements DokumentTypeMapper {

    private DomeneobjektProvider domeneobjektProvider;

    private Map<String, VariantKode> malTilVariantMap = Map.of(DokumentMalType.FORLENGET_MEDL_DOK, VariantKode.MEDLEM,
            DokumentMalType.FORLENGET_TIDLIG_SOK, VariantKode.FORTIDLIG,
            DokumentMalType.FORLENGET_OPPTJENING, VariantKode.OPPTJENING);

    public ForlengetSaksbehandlingstidBrevMapper() {
        //CDI
    }

    @Inject
    public ForlengetSaksbehandlingstidBrevMapper(DomeneobjektProvider domeneobjektProvider) {
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType, DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse, Behandling behandling) throws JAXBException, SAXException, XMLStreamException {
        FamilieHendelse familieHendelse = domeneobjektProvider.hentFamiliehendelse(behandling);
        List<MottattDokument> mottateDokumenter = domeneobjektProvider.hentMottatteDokumenter(behandling);
        FagType fagType = mapFagType(dokumentHendelse, behandling, dokumentFelles, familieHendelse, mottateDokumenter);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        return JaxbHelper.marshalNoNamespaceXML(ForlengetConstants.JAXB_CLASS, brevdataTypeJAXBElement, null);

    }

    private FagType mapFagType(DokumentHendelse dokumentHendelse, Behandling behandling, DokumentFelles dokumentFelles, FamilieHendelse familieHendelse, List<MottattDokument> mottateDokumenter) {
        FagType fagType = new FagType();
        fagType.setAntallBarn(familieHendelse.getAntallBarn());
        fagType.setBehandlingsfristUker(BigInteger.valueOf(BehandlingMapper.finnAntallUkerBehandlingsfrist(behandling.getBehandlingType())));
        fagType.setPersonstatus(PersonstatusKode.fromValue(dokumentFelles.getSakspartPersonStatus()));
        fagType.setVariant(mapVariant(dokumentHendelse, behandling));
        fagType.setSokersNavn(dokumentFelles.getSakspartNavn());
        fagType.setYtelseType(YtelseTypeKode.fromValue(dokumentHendelse.getYtelseType().getKode()));
        fagType.setSoknadsdato(MottattdokumentMapper.finnSøknadsDatoFraMottatteDokumenter(behandling, mottateDokumenter));
        fagType.setAntallBarn(familieHendelse.getAntallBarn());
        return fagType;
    }

    private VariantKode mapVariant(DokumentHendelse dokumentHendelse, Behandling behandling) {
        if (malTilVariantMap.containsKey(dokumentHendelse.getDokumentMalType().getKode())) {
            return malTilVariantMap.get(dokumentHendelse.getDokumentMalType().getKode());
        }
        if (BehandlingType.KLAGE.equals(behandling.getBehandlingType())) {
            return VariantKode.KLAGE;
        }
        return VariantKode.FORLENGET;
    }

    private JAXBElement<BrevdataType> mapintoBrevdataType(FellesType fellesType, FagType fagType) {
        ObjectFactory of = new ObjectFactory();
        BrevdataType brevdataType = of.createBrevdataType();
        brevdataType.setFag(fagType);
        brevdataType.setFelles(fellesType);
        return of.createBrevdata(brevdataType);
    }

}

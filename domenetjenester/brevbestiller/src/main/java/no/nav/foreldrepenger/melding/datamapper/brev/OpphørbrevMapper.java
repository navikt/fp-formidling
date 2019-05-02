package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter.REVURDERING;
import static no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter.SØKNAD;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import no.nav.foreldrepenger.melding.aktør.Personinfo;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.datamapper.DokumentMapperFeil;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.ÅrsakMapperOpphør;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.fagsak.Fagsak;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.opphor.AarsakListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.opphor.OpphørConstants;
import no.nav.foreldrepenger.melding.integrasjon.dokument.opphor.BehandlingsTypeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.opphor.BrevdataType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.opphor.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.opphor.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.opphor.PersonstatusKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.opphor.RelasjonskodeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.uttak.IkkeOppfyltÅrsak;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.felles.integrasjon.felles.ws.JaxbHelper;
import no.nav.vedtak.util.Tuple;

@ApplicationScoped
@Named(DokumentMalType.OPPHØR_DOK)
public class OpphørbrevMapper implements DokumentTypeMapper {
    private static final Map<RelasjonsRolleType, RelasjonskodeKode> relasjonskodeTypeMap;

    static {
        relasjonskodeTypeMap = new HashMap<>();
        relasjonskodeTypeMap.put(RelasjonsRolleType.MORA, RelasjonskodeKode.MOR);
        relasjonskodeTypeMap.put(RelasjonsRolleType.FARA, RelasjonskodeKode.FAR);
        relasjonskodeTypeMap.put(RelasjonsRolleType.MEDMOR, RelasjonskodeKode.MEDMOR);
    }

    private BrevParametere brevParametere;
    private DomeneobjektProvider domeneobjektProvider;

    public OpphørbrevMapper() {
    }

    @Inject
    public OpphørbrevMapper(BrevParametere brevParametere,
                            DomeneobjektProvider domeneobjektProvider) {
        this.brevParametere = brevParametere;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType,
                                DokumentFelles dokumentFelles,
                                DokumentHendelse dokumentHendelse,
                                Behandling behandling) throws JAXBException, SAXException, XMLStreamException {

        FamilieHendelse familiehendelse = domeneobjektProvider.hentFamiliehendelse(behandling);
        Beregningsgrunnlag beregningsgrunnlag = domeneobjektProvider.hentBeregningsgrunnlag(behandling);
        UttakResultatPerioder uttakResultatPerioder = domeneobjektProvider.hentUttaksresultat(behandling);
        String behandlingstype = BehandlingMapper.utledBehandlingsTypeForAvslagVedtak(behandling);
        Personinfo personinfo = behandling.getFagsak().getPersoninfo();
        UttakResultatPerioder originaltUttakResultat = null;
        if (behandling.getOriginalBehandlingId() != null) {
            Behandling originalBehandling = domeneobjektProvider.hentBehandling(behandling.getOriginalBehandlingId());
            originaltUttakResultat = domeneobjektProvider.hentUttaksresultat(originalBehandling);
        }
        FagType fagType = mapFagType(behandlingstype, behandling,
                dokumentFelles,
                familiehendelse,
                beregningsgrunnlag,
                uttakResultatPerioder,
                personinfo,
                originaltUttakResultat);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        return JaxbHelper.marshalNoNamespaceXML(OpphørConstants.JAXB_CLASS, brevdataTypeJAXBElement, null);
    }

    private FagType mapFagType(String behandlingstypeKode, Behandling behandling,
                               DokumentFelles dokumentFelles,
                               FamilieHendelse familiehendelse,
                               Beregningsgrunnlag beregningsgrunnlag,
                               UttakResultatPerioder uttakResultatPerioder,
                               Personinfo personinfo,
                               UttakResultatPerioder originaltUttakResultat) {
        FagType fagType = new FagType();
        fagType.setBehandlingsType(fra(behandlingstypeKode));
        fagType.setSokersNavn(dokumentFelles.getSakspartNavn());
        fagType.setPersonstatus(PersonstatusKode.fromValue(dokumentFelles.getSakspartPersonStatus()));
        fagType.setRelasjonskode(fra(behandling.getFagsak()));
        fagType.setGjelderFoedsel(familiehendelse.isGjelderFødsel());
        fagType.setAntallBarn(familiehendelse.getAntallBarn());
        fagType.setHalvG(beregningsgrunnlag.getGrunnbeløp().getVerdi().divide(BigDecimal.valueOf(2)).longValue());
        fagType.setKlageFristUker(BigInteger.valueOf(brevParametere.getKlagefristUker()));

        mapFelterRelatertTilAvslagårsaker(behandling.getBehandlingsresultat(),
                uttakResultatPerioder,
                fagType);

        //TODO Ikke obligatoriske felter
        finnDødsdatoHvisFinnes(personinfo, familiehendelse, fagType.getAarsakListe()).map(XmlUtil::finnDatoVerdiAvUtenTidSone)
                .ifPresent(fagType::setDodsdato);
        finnOpphørsdatoHvisFinnes(uttakResultatPerioder, familiehendelse).map(XmlUtil::finnDatoVerdiAvUtenTidSone)
                .ifPresent(fagType::setOpphorDato);
        finnStønadFomDatoHvisFinnes(originaltUttakResultat).map(XmlUtil::finnDatoVerdiAvUtenTidSone)
                .ifPresent(fagType::setFomStonadsdato);
        finnStønadTomDatoHvisFinnes(fagType).map(XmlUtil::finnDatoVerdiAvUtenTidSone)
                .ifPresent(fagType::setTomStonadsdato);
        return fagType;
    }

    private void mapFelterRelatertTilAvslagårsaker(Behandlingsresultat behandlingsresultat,
                                                   UttakResultatPerioder uttakResultatPerioder, FagType fagType) {
        Tuple<AarsakListeType, String> AarsakListeOgLovhjemmel = ÅrsakMapperOpphør.mapAarsakListeOgLovhjemmelFra(
                behandlingsresultat,
                uttakResultatPerioder);
        AarsakListeType aarsakListe = AarsakListeOgLovhjemmel.getElement1();

        fagType.setAntallAarsaker(BigInteger.valueOf(aarsakListe.getAvslagsAarsak().size()));
        fagType.setAarsakListe(aarsakListe);
        fagType.setLovhjemmelForAvslag(AarsakListeOgLovhjemmel.getElement2());
    }

    private RelasjonskodeKode fra(Fagsak fagsak) {
        if (RelasjonsRolleType.erRegistrertForeldre(fagsak.getRelasjonsRolleType())) {
            return relasjonskodeTypeMap.get(fagsak.getRelasjonsRolleType());
        }
        return RelasjonskodeKode.ANNET;
    }

    private BehandlingsTypeKode fra(String behandlingsType) {
        if (REVURDERING.equals(behandlingsType)) {
            return BehandlingsTypeKode.REVURDERING;
        }
        if (SØKNAD.equals(behandlingsType)) {
            return BehandlingsTypeKode.SØKNAD;
        }
        return BehandlingsTypeKode.FOERSTEGANGSBEHANDLING;
    }

    private Optional<LocalDate> finnDødsdatoHvisFinnes(final Personinfo personinfo, final FamilieHendelse familieHendelse, AarsakListeType årsakListe) {
        Optional<LocalDate> dødsdato = Optional.empty();
        if (årsakListe.getAvslagsAarsak().contains(IkkeOppfyltÅrsak.SØKER_ER_DØD.getKode())) {
            dødsdato = Optional.ofNullable(personinfo.getDødsdato());
        }
        if (årsakListe.getAvslagsAarsak().contains(IkkeOppfyltÅrsak.BARNET_ER_DØD.getKode())) {
            dødsdato = familieHendelse.getDødsdato();
        }
        return dødsdato;
    }

    private Optional<LocalDate> finnOpphørsdatoHvisFinnes(UttakResultatPerioder uttakResultatPerioder, FamilieHendelse familiehendelse) {
        LocalDate opphørsdato = utledOpphørsdatoFraUttak(uttakResultatPerioder);
        return Optional.ofNullable(opphørsdato).or(() -> familiehendelse.getSkjæringstidspunkt());
    }

    private LocalDate utledOpphørsdatoFraUttak(UttakResultatPerioder uttakResultatPerioder) {
        Set<PeriodeResultatÅrsak> opphørsårsaker = IkkeOppfyltÅrsak.opphørsAvslagÅrsaker();
        List<UttakResultatPeriode> perioder = uttakResultatPerioder.getPerioder();

        // Finn fom-dato i første periode av de siste sammenhengende periodene med opphørårsaker
        LocalDate fom = null;
        for (int i = perioder.size() - 1; i >= 0 ; i--) {
            UttakResultatPeriode periode = perioder.get(i);
            if (opphørsårsaker.contains(periode.getPeriodeResultatÅrsak())) {
                fom = periode.getFom();
            } else if (fom != null && periode.isInnvilget()) {
                return fom;
            }
        }
        // bruker skjæringstidspunkt fom = null eller tidligste periode i uttaksplan er opphørt eller avslått
        return null;
    }

    private Optional<LocalDate> finnStønadFomDatoHvisFinnes(UttakResultatPerioder originaltUttakResultat) {
        return originaltUttakResultat.getPerioder().stream()
                .filter(UttakResultatPeriode::isInnvilget)
                .map(UttakResultatPeriode::getFom)
                .findFirst();
    }

    private Optional<LocalDate> finnStønadTomDatoHvisFinnes(FagType fagType) {
        if (fagType.getFomStonadsdato() != null && fagType.getOpphorDato() != null) {
            LocalDate stønadFom = XmlUtil.finnDatoVerdiAv(fagType.getFomStonadsdato());
            LocalDate opphorDato = XmlUtil.finnDatoVerdiAv(fagType.getOpphorDato());
            boolean minusEnDag = !stønadFom.isEqual(opphorDato);
            return Optional.of(opphorDato.minusDays(minusEnDag ? 1 : 0));
        } else if (PersonstatusKode.DOD.equals(fagType.getPersonstatus())) {
            throw brevFeilPgaUtilstrekkeligTekstgrunnlag(fagType.getOpphorDato() != null).toException();
        }
        return Optional.empty();
    }

    private Feil brevFeilPgaUtilstrekkeligTekstgrunnlag(boolean opphørsdatoPresent) {
        return opphørsdatoPresent ?
                DokumentMapperFeil.FACTORY.ingenStartdatoVedPersonstatusDød() :
                DokumentMapperFeil.FACTORY.ingenOpphørsdatoVedPersonstatusDød();
    }

    private JAXBElement<BrevdataType> mapintoBrevdataType(FellesType fellesType, FagType fagType) {
        ObjectFactory of = new ObjectFactory();
        BrevdataType brevdataType = of.createBrevdataType();
        brevdataType.setFag(fagType);
        brevdataType.setFelles(fellesType);
        return of.createBrevdata(brevdataType);
    }
}

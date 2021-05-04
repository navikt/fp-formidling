package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter.REVURDERING;
import static no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter.SØKNAD;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import no.nav.foreldrepenger.PersonAdapter;
import no.nav.foreldrepenger.melding.aktør.Personinfo;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.datamapper.DokumentMapperFeil;
import no.nav.foreldrepenger.melding.datamapper.DokumentTypeMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.BeregningsgrunnlagMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.ÅrsakMapperOpphør;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakBackend;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.opphor.AarsakListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.opphor.AvslagsAarsakType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.opphor.BehandlingsTypeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.opphor.BrevdataType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.opphor.FagType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.opphor.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.opphor.OpphørConstants;
import no.nav.foreldrepenger.melding.integrasjon.dokument.opphor.PersonstatusKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.opphor.RelasjonskodeKode;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;
import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.melding.uttak.kodeliste.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.xmlutils.JaxbHelper;
import no.nav.vedtak.exception.VLException;
import no.nav.vedtak.util.Tuple;

@ApplicationScoped
@Named(DokumentMalTypeKode.OPPHØR_DOK)
public class OpphørbrevMapper extends DokumentTypeMapper {
    private static final Map<RelasjonsRolleType, RelasjonskodeKode> relasjonskodeTypeMap;

    static {
        relasjonskodeTypeMap = new HashMap<>();
        relasjonskodeTypeMap.put(RelasjonsRolleType.MORA, RelasjonskodeKode.MOR);
        relasjonskodeTypeMap.put(RelasjonsRolleType.FARA, RelasjonskodeKode.FAR);
        relasjonskodeTypeMap.put(RelasjonsRolleType.MEDMOR, RelasjonskodeKode.MEDMOR);
    }

    private BrevParametere brevParametere;
    private PersonAdapter personAdapter;

    public OpphørbrevMapper() {
    }

    @Inject
    public OpphørbrevMapper(BrevParametere brevParametere,
                            DomeneobjektProvider domeneobjektProvider,
                            PersonAdapter personAdapter) {
        this.brevParametere = brevParametere;
        this.domeneobjektProvider = domeneobjektProvider;
        this.personAdapter = personAdapter;
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType,
                                DokumentFelles dokumentFelles,
                                DokumentHendelse dokumentHendelse,
                                Behandling behandling) throws JAXBException, SAXException, XMLStreamException {

        String behandlingstype = BehandlingMapper.utledBehandlingsTypeForAvslagVedtak(behandling, dokumentHendelse);
        FagType fagType = mapFagType(behandlingstype, behandling, dokumentFelles);
        JAXBElement<BrevdataType> brevdataTypeJAXBElement = mapintoBrevdataType(fellesType, fagType);
        return JaxbHelper.marshalNoNamespaceXML(OpphørConstants.JAXB_CLASS, brevdataTypeJAXBElement, null);
    }

    private FagType mapFagType(String behandlingstypeKode, Behandling behandling, DokumentFelles dokumentFelles) {
        FamilieHendelse familiehendelse = domeneobjektProvider.hentFamiliehendelse(behandling);
        Optional<Beregningsgrunnlag> beregningsgrunnlagOpt = domeneobjektProvider.hentBeregningsgrunnlagHvisFinnes(behandling);
        UttakResultatPerioder uttakResultatPerioder = domeneobjektProvider.hentUttaksresultatHvisFinnes(behandling)
                .orElseGet(() -> UttakResultatPerioder.ny().build()); // bestående av tomme lister.
        Optional<UttakResultatPerioder> originaltUttakResultat = domeneobjektProvider.hentOriginalBehandlingHvisFinnes(behandling).flatMap(domeneobjektProvider::hentUttaksresultatHvisFinnes);
        long halvG = BeregningsgrunnlagMapper.getHalvGOrElseZero(beregningsgrunnlagOpt);
        FagsakBackend fagsak = domeneobjektProvider.hentFagsakBackend(behandling);

        FagType fagType = new FagType();
        fagType.setBehandlingsType(fra(behandlingstypeKode));
        fagType.setSokersNavn(dokumentFelles.getSakspartNavn());
        fagType.setPersonstatus(PersonstatusKode.fromValue(dokumentFelles.getSakspartPersonStatus()));
        fagType.setRelasjonskode(fra(fagsak));
        fagType.setGjelderFoedsel(familiehendelse.isGjelderFødsel());
        fagType.setAntallBarn(familiehendelse.getAntallBarn());
        fagType.setHalvG(halvG);
        fagType.setKlageFristUker(BigInteger.valueOf(brevParametere.getKlagefristUker()));

        mapFelterRelatertTilAvslagårsaker(behandling.getBehandlingsresultat(),
                uttakResultatPerioder,
                fagType);

        finnDødsdatoHvisFinnes(fagsak, familiehendelse, fagType.getAarsakListe()).map(XmlUtil::finnDatoVerdiAvUtenTidSone)
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
        Tuple<AarsakListeType, String> aarsakListeOgLovhjemmel = ÅrsakMapperOpphør.mapAarsakListeOgLovhjemmelFra(
                behandlingsresultat,
                uttakResultatPerioder);
        AarsakListeType aarsakListe = aarsakListeOgLovhjemmel.getElement1();

        fagType.setAntallAarsaker(BigInteger.valueOf(aarsakListe.getAvslagsAarsak().size()));
        fagType.setAarsakListe(aarsakListe);
        fagType.setLovhjemmelForAvslag(aarsakListeOgLovhjemmel.getElement2());
    }

    private RelasjonskodeKode fra(FagsakBackend fagsak) {
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

    private Optional<LocalDate> finnDødsdatoHvisFinnes(FagsakBackend fagsak, final FamilieHendelse familieHendelse, AarsakListeType årsakListe) {
        Optional<LocalDate> dødsdato = Optional.empty();
        List<String> avslagsArsaker = hentAvslagsårsaker(årsakListe);
        if (avslagsArsaker.contains(PeriodeResultatÅrsak.SØKER_ER_DØD.getKode())) {
            var dødsdatoFREG = personAdapter.hentBrukerForAktør(fagsak.getAktørId()).map(Personinfo::getDødsdato).orElse(null);
            dødsdato = Optional.ofNullable(dødsdatoFREG);
        }
        if (avslagsArsaker.contains(PeriodeResultatÅrsak.BARNET_ER_DØD.getKode())) {
            dødsdato = familieHendelse.getDødsdato();
        }
        return dødsdato;
    }

    private List<String> hentAvslagsårsaker(AarsakListeType årsakListe) {
        return årsakListe.getAvslagsAarsak() == null ? Collections.emptyList() :
                årsakListe.getAvslagsAarsak().stream()
                        .map(AvslagsAarsakType::getAvslagsAarsakKode)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
    }

    private Optional<LocalDate> finnOpphørsdatoHvisFinnes(UttakResultatPerioder uttakResultatPerioder, FamilieHendelse familiehendelse) {
        LocalDate opphørsdato = utledOpphørsdatoFraUttak(uttakResultatPerioder);
        return Optional.ofNullable(opphørsdato).or(familiehendelse::getSkjæringstidspunkt);
    }

    private LocalDate utledOpphørsdatoFraUttak(UttakResultatPerioder uttakResultatPerioder) {
        Set<String> opphørsårsaker = PeriodeResultatÅrsak.opphørsAvslagÅrsaker();
        List<UttakResultatPeriode> perioder = uttakResultatPerioder.getPerioder();

        // Finn fom-dato i første periode av de siste sammenhengende periodene med opphørårsaker
        LocalDate fom = null;
        for (int i = perioder.size() - 1; i >= 0; i--) {
            UttakResultatPeriode periode = perioder.get(i);
            if (opphørsårsaker.contains(periode.getPeriodeResultatÅrsak().getKode())) {
                fom = periode.getFom();
            } else if (fom != null && periode.isInnvilget()) {
                return fom;
            }
        }
        // bruker skjæringstidspunkt fom = null eller tidligste periode i uttaksplan er opphørt eller avslått
        return null;
    }

    private Optional<LocalDate> finnStønadFomDatoHvisFinnes(Optional<UttakResultatPerioder> originaltUttakResultat) {
        return originaltUttakResultat.map(UttakResultatPerioder::getPerioder).orElse(Collections.emptyList()).stream()
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
            throw brevFeilPgaUtilstrekkeligTekstgrunnlag(fagType.getOpphorDato() != null);
        }
        return Optional.empty();
    }

    private VLException brevFeilPgaUtilstrekkeligTekstgrunnlag(boolean opphørsdatoPresent) {
        return opphørsdatoPresent ?
                DokumentMapperFeil.ingenStartdatoVedPersonstatusDød() :
                DokumentMapperFeil.ingenOpphørsdatoVedPersonstatusDød();
    }

    private JAXBElement<BrevdataType> mapintoBrevdataType(FellesType fellesType, FagType fagType) {
        ObjectFactory of = new ObjectFactory();
        BrevdataType brevdataType = of.createBrevdataType();
        brevdataType.setFag(fagType);
        brevdataType.setFelles(fellesType);
        return of.createBrevdata(brevdataType);
    }
}

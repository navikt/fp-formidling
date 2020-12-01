package no.nav.foreldrepenger.pdl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.melding.aktør.AdresseType;
import no.nav.foreldrepenger.melding.aktør.Adresseinfo;
import no.nav.foreldrepenger.melding.aktør.Personinfo;
import no.nav.foreldrepenger.melding.geografisk.Landkoder;
import no.nav.foreldrepenger.melding.geografisk.Poststed;
import no.nav.foreldrepenger.melding.geografisk.PoststedKodeverkRepository;
import no.nav.foreldrepenger.melding.personopplysning.NavBrukerKjønn;
import no.nav.foreldrepenger.melding.typer.AktørId;
import no.nav.foreldrepenger.melding.typer.PersonIdent;
import no.nav.pdl.Bostedsadresse;
import no.nav.pdl.BostedsadresseResponseProjection;
import no.nav.pdl.Doedsfall;
import no.nav.pdl.DoedsfallResponseProjection;
import no.nav.pdl.Foedsel;
import no.nav.pdl.FoedselResponseProjection;
import no.nav.pdl.Folkeregisterpersonstatus;
import no.nav.pdl.FolkeregisterpersonstatusResponseProjection;
import no.nav.pdl.HentIdenterQueryRequest;
import no.nav.pdl.HentPersonQueryRequest;
import no.nav.pdl.IdentGruppe;
import no.nav.pdl.IdentInformasjon;
import no.nav.pdl.IdentInformasjonResponseProjection;
import no.nav.pdl.Identliste;
import no.nav.pdl.IdentlisteResponseProjection;
import no.nav.pdl.Kjoenn;
import no.nav.pdl.KjoennResponseProjection;
import no.nav.pdl.KjoennType;
import no.nav.pdl.Kontaktadresse;
import no.nav.pdl.KontaktadresseResponseProjection;
import no.nav.pdl.Matrikkeladresse;
import no.nav.pdl.MatrikkeladresseResponseProjection;
import no.nav.pdl.Navn;
import no.nav.pdl.NavnResponseProjection;
import no.nav.pdl.Oppholdsadresse;
import no.nav.pdl.OppholdsadresseResponseProjection;
import no.nav.pdl.Person;
import no.nav.pdl.PersonResponseProjection;
import no.nav.pdl.PostadresseIFrittFormat;
import no.nav.pdl.PostadresseIFrittFormatResponseProjection;
import no.nav.pdl.Postboksadresse;
import no.nav.pdl.PostboksadresseResponseProjection;
import no.nav.pdl.UkjentBosted;
import no.nav.pdl.UkjentBostedResponseProjection;
import no.nav.pdl.UtenlandskAdresse;
import no.nav.pdl.UtenlandskAdresseIFrittFormat;
import no.nav.pdl.UtenlandskAdresseIFrittFormatResponseProjection;
import no.nav.pdl.UtenlandskAdresseResponseProjection;
import no.nav.pdl.Vegadresse;
import no.nav.pdl.VegadresseResponseProjection;
import no.nav.vedtak.exception.VLException;
import no.nav.vedtak.felles.integrasjon.pdl.PdlKlient;
import no.nav.vedtak.felles.integrasjon.pdl.Tema;
import no.nav.vedtak.util.LRUCache;

@ApplicationScoped
public class AktørTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(AktørTjeneste.class);
    private static final String HARDKODET_POSTNR = "XXXX";
    private static final String HARDKODET_POSTSTED = "UKJENT";

    private static final String FREG_DØD = "doed";
    private static final String FREG_DØD_FORENKLET = "doedIFolkeregisteret";

    private static final int DEFAULT_CACHE_SIZE = 1000;
    private static final long DEFAULT_CACHE_TIMEOUT = TimeUnit.MILLISECONDS.convert(8, TimeUnit.HOURS);

    private LRUCache<AktørId, PersonIdent> cacheAktørIdTilIdent;

    private PdlKlient pdlKlient;
    private PoststedKodeverkRepository poststedKodeverkRepository;

    AktørTjeneste() {
        // CDI
    }

    @Inject
    public AktørTjeneste(PdlKlient pdlKlient, PoststedKodeverkRepository repository) {
        this.pdlKlient = pdlKlient;
        this.poststedKodeverkRepository = repository;
        this.cacheAktørIdTilIdent = new LRUCache<>(DEFAULT_CACHE_SIZE, DEFAULT_CACHE_TIMEOUT);
    }

    public Optional<PersonIdent> hentPersonIdentForAktørId(AktørId aktørId) {
        var fraCache = cacheAktørIdTilIdent.get(aktørId);
        if (fraCache != null) {
            return Optional.of(fraCache);
        }
        var request = new HentIdenterQueryRequest();
        request.setIdent(aktørId.getId());
        request.setGrupper(List.of(IdentGruppe.FOLKEREGISTERIDENT));
        request.setHistorikk(Boolean.FALSE);
        var projection = new IdentlisteResponseProjection()
                .identer(new IdentInformasjonResponseProjection().ident());

        final Identliste identliste;

        try {
            identliste = pdlKlient.hentIdenter(request, projection, Tema.FOR);
        } catch (VLException v) {
            if (PdlKlient.PDL_KLIENT_NOT_FOUND_KODE.equals(v.getKode())) {
                return Optional.empty();
            }
            throw v;
        }

        var ident = identliste.getIdenter().stream().findFirst().map(IdentInformasjon::getIdent).map(PersonIdent::new);
        ident.ifPresent(i -> cacheAktørIdTilIdent.put(aktørId, i));
        return ident;
    }

    public void hentPersoninfo(AktørId aktørId, PersonIdent personIdent, Personinfo fraTPS) {
        try {
            var query = new HentPersonQueryRequest();
            query.setIdent(aktørId.getId());
            var projection = new PersonResponseProjection()
                .navn(new NavnResponseProjection().forkortetNavn().fornavn().mellomnavn().etternavn())
                .foedsel(new FoedselResponseProjection().foedselsdato())
                .doedsfall(new DoedsfallResponseProjection().doedsdato())
                .kjoenn(new KjoennResponseProjection().kjoenn())
                .folkeregisterpersonstatus(new FolkeregisterpersonstatusResponseProjection().forenkletStatus());

            var person = pdlKlient.hentPerson(query, projection, Tema.FOR);

            var fødselsdato = person.getFoedsel().stream()
                .map(Foedsel::getFoedselsdato)
                .filter(Objects::nonNull)
                .findFirst().map(d -> LocalDate.parse(d, DateTimeFormatter.ISO_LOCAL_DATE)).orElse(null);
            var dødssdato = person.getDoedsfall().stream()
                .map(Doedsfall::getDoedsdato)
                .filter(Objects::nonNull)
                .findFirst().map(d -> LocalDate.parse(d, DateTimeFormatter.ISO_LOCAL_DATE)).orElse(null);
            var pdlStatusDød = person.getFolkeregisterpersonstatus().stream()
                .map(Folkeregisterpersonstatus::getForenkletStatus)
                .findFirst().map(AktørTjeneste::harPersonstatusDød).orElse(false);
            var fraPDL = Personinfo.getbuilder(aktørId).medPersonIdent(personIdent)
                .medNavn(person.getNavn().stream().map(AktørTjeneste::mapNavn).filter(Objects::nonNull).findFirst().orElse("MANGLER NAVN"))
                .medFødselsdato(fødselsdato)
                .medDødsdato(dødssdato)
                .medNavBrukerKjønn(mapKjønn(person))
                .medRegistrertDød(pdlStatusDød)
                .build();

            if (!erLike(fraPDL, fraTPS)) {
                var avvik = finnAvvik(fraTPS, fraPDL);
                LOG.info("FPFORMIDLING PDL PERSON: avvik {}", avvik);
            }
        } catch (Exception e) {
            LOG.info("FPFORMIDLING PDL PERSON: error", e);
        }
    }

    public Optional<Adresseinfo> hentAdresseinformasjon(AktørId aktørId, PersonIdent personIdent, Adresseinfo fraTPS) {
        try {
            var query = new HentPersonQueryRequest();
            query.setIdent(aktørId.getId());
            var projection = new PersonResponseProjection()
                    .navn(new NavnResponseProjection().forkortetNavn().fornavn().mellomnavn().etternavn())
                    .doedsfall(new DoedsfallResponseProjection().doedsdato())
                    .folkeregisterpersonstatus(new FolkeregisterpersonstatusResponseProjection().forenkletStatus().status())
                    .bostedsadresse(new BostedsadresseResponseProjection()
                            .vegadresse(new VegadresseResponseProjection().matrikkelId().adressenavn().husnummer().husbokstav().tilleggsnavn().postnummer())
                            .matrikkeladresse(new MatrikkeladresseResponseProjection().matrikkelId().bruksenhetsnummer().tilleggsnavn().postnummer())
                            .ukjentBosted(new UkjentBostedResponseProjection().bostedskommune())
                            .utenlandskAdresse(new UtenlandskAdresseResponseProjection().adressenavnNummer().bygningEtasjeLeilighet().postboksNummerNavn().bySted().regionDistriktOmraade().postkode().landkode()))
                    .oppholdsadresse(new OppholdsadresseResponseProjection()
                            .vegadresse(new VegadresseResponseProjection().matrikkelId().adressenavn().husnummer().husbokstav().tilleggsnavn().postnummer())
                            .matrikkeladresse(new MatrikkeladresseResponseProjection().matrikkelId().bruksenhetsnummer().tilleggsnavn().postnummer())
                            .utenlandskAdresse(new UtenlandskAdresseResponseProjection().adressenavnNummer().bygningEtasjeLeilighet().postboksNummerNavn().bySted().regionDistriktOmraade().postkode().landkode()))
                    .kontaktadresse(new KontaktadresseResponseProjection().type()
                            .vegadresse(new VegadresseResponseProjection().matrikkelId().adressenavn().husnummer().husbokstav().tilleggsnavn().postnummer())
                            .postboksadresse(new PostboksadresseResponseProjection().postboks().postbokseier().postnummer())
                            .postadresseIFrittFormat(new PostadresseIFrittFormatResponseProjection().adresselinje1().adresselinje2().adresselinje3().postnummer())
                            .utenlandskAdresse(new UtenlandskAdresseResponseProjection().adressenavnNummer().bygningEtasjeLeilighet().postboksNummerNavn().bySted().regionDistriktOmraade().postkode().landkode())
                            .utenlandskAdresseIFrittFormat(new UtenlandskAdresseIFrittFormatResponseProjection().adresselinje1().adresselinje2().adresselinje3().byEllerStedsnavn().postkode().landkode()))
                    ;

            var person = pdlKlient.hentPerson(query, projection, Tema.FOR);

            var navn = person.getNavn().stream().map(AktørTjeneste::mapNavn).filter(Objects::nonNull).findFirst().orElse("MANGLER NAVN");
            var dødssdato = person.getDoedsfall().stream()
                    .map(Doedsfall::getDoedsdato)
                    .filter(Objects::nonNull)
                    .findFirst().map(d -> LocalDate.parse(d, DateTimeFormatter.ISO_LOCAL_DATE)).orElse(null);
            var pdlStatusDød = person.getFolkeregisterpersonstatus().stream()
                    .map(Folkeregisterpersonstatus::getForenkletStatus)
                    .findFirst().map(AktørTjeneste::harPersonstatusDød).orElse(false);
            var adresser = mapAdresser(person.getBostedsadresse(), person.getKontaktadresse(), person.getOppholdsadresse());
            var antattGjeldende = velgAdresse(adresser);
            var fraPDL = new Adresseinfo.Builder(antattGjeldende.getGjeldendePostadresseType(), personIdent, navn, pdlStatusDød)
                    .medAdresselinje1(antattGjeldende.getAdresselinje1())
                    .medAdresselinje2(antattGjeldende.getAdresselinje2())
                    .medAdresselinje3(antattGjeldende.getAdresselinje3())
                    .medAdresselinje4(antattGjeldende.getAdresselinje4())
                    .medPostNr(antattGjeldende.getPostNr())
                    .medPoststed(antattGjeldende.getPoststed())
                    .medLand(antattGjeldende.getLand())
                    .build();

            if (!erLikeAdresse(fraPDL, fraTPS)) {
                var avvik = finnAvvikAdresse(fraTPS, fraPDL, adresser);
                LOG.info("FPFORMIDLING PDL ADRESSE: avvik {}", avvik);
            }

        } catch (Exception e) {
            LOG.info("FPFORMIDLING PDL ADRESSE: error", e);
        }
        return Optional.empty();
    }

    private static String mapNavn(Navn navn) {
        if (navn.getForkortetNavn() != null)
            return navn.getForkortetNavn();
        return navn.getEtternavn() + " " + navn.getFornavn() + (navn.getMellomnavn() == null ? "" : " " + navn.getMellomnavn());
    }

    public static boolean harPersonstatusDød(String fregStatus) {
        return fregStatus != null && (Objects.equals(FREG_DØD_FORENKLET, fregStatus) || Objects.equals(FREG_DØD, fregStatus));
    }

    private static NavBrukerKjønn mapKjønn(Person person) {
        var kode = person.getKjoenn().stream()
            .map(Kjoenn::getKjoenn)
            .filter(Objects::nonNull)
            .findFirst().orElse(KjoennType.UKJENT);
        if (KjoennType.MANN.equals(kode))
            return NavBrukerKjønn.MANN;
        return KjoennType.KVINNE.equals(kode) ? NavBrukerKjønn.KVINNE : NavBrukerKjønn.UDEFINERT;
    }

    private static boolean erLike(Personinfo pdl, Personinfo tps) {
        if (tps == null && pdl == null) return true;
        if (pdl == null || tps == null || tps.getClass() != pdl.getClass()) return false;
        return // Objects.equals(pdl.getNavn(), tps.getNavn()) && - avvik skyldes tegnsett
            Objects.equals(pdl.getFødselsdato(), tps.getFødselsdato()) &&
                Objects.equals(pdl.getDødsdato(), tps.getDødsdato()) &&
                pdl.getKjønn() == tps.getKjønn() &&
                pdl.isRegistrertDød() == tps.isRegistrertDød();
    }

    private static String finnAvvik(Personinfo tps, Personinfo pdl) {
        //String navn = Objects.equals(tps.getNavn(), pdl.getNavn()) ? "" : " navn ";
        String kjonn = Objects.equals(tps.getKjønn(), pdl.getKjønn()) ? "" : " kjønn ";
        String fdato = Objects.equals(tps.getFødselsdato(), pdl.getFødselsdato()) ? "" : " fødsel ";
        String ddato = Objects.equals(tps.getDødsdato(), pdl.getDødsdato()) ? "" : " dødato ";
        String ddreg = Objects.equals(tps.isRegistrertDød(), pdl.isRegistrertDød()) ? "" : " dødreg ";

        return "Avvik" + kjonn + fdato + ddato + ddreg;
    }

    private List<Adresseinfo> mapAdresser(List<Bostedsadresse> bostedsadresser, List<Kontaktadresse> kontaktadresser, List<Oppholdsadresse> oppholdsadresser) {
        List<Adresseinfo> resultat = new ArrayList<>();
        bostedsadresser.stream().map(Bostedsadresse::getVegadresse).map(a -> mapVegadresse(AdresseType.BOSTEDSADRESSE, a)).filter(Objects::nonNull).forEach(resultat::add);
        bostedsadresser.stream().map(Bostedsadresse::getMatrikkeladresse).map(a -> mapMatrikkeladresse(AdresseType.BOSTEDSADRESSE, a)).filter(Objects::nonNull).forEach(resultat::add);
        bostedsadresser.stream().map(Bostedsadresse::getUkjentBosted).filter(Objects::nonNull).map(AktørTjeneste::mapUkjentadresse).forEach(resultat::add);
        bostedsadresser.stream().map(Bostedsadresse::getUtenlandskAdresse).map(a -> mapUtenlandskadresse(AdresseType.BOSTEDSADRESSE, a)).filter(Objects::nonNull).forEach(resultat::add);

        oppholdsadresser.stream().map(Oppholdsadresse::getVegadresse).map(a -> mapVegadresse(AdresseType.MIDLERTIDIG_POSTADRESSE_NORGE, a)).filter(Objects::nonNull).forEach(resultat::add);
        oppholdsadresser.stream().map(Oppholdsadresse::getMatrikkeladresse).map(a -> mapMatrikkeladresse(AdresseType.MIDLERTIDIG_POSTADRESSE_NORGE, a)).filter(Objects::nonNull).forEach(resultat::add);
        oppholdsadresser.stream().map(Oppholdsadresse::getUtenlandskAdresse).map(a -> mapUtenlandskadresse(AdresseType.MIDLERTIDIG_POSTADRESSE_UTLAND, a)).filter(Objects::nonNull).forEach(resultat::add);

        kontaktadresser.stream().map(Kontaktadresse::getVegadresse).map(a -> mapVegadresse(AdresseType.POSTADRESSE, a)).filter(Objects::nonNull).forEach(resultat::add);
        kontaktadresser.stream().map(Kontaktadresse::getPostboksadresse).map(a -> mapPostboksadresse(AdresseType.POSTADRESSE, a)).filter(Objects::nonNull).forEach(resultat::add);
        kontaktadresser.stream().map(Kontaktadresse::getPostadresseIFrittFormat).map(a -> mapFriAdresseNorsk(AdresseType.POSTADRESSE, a)).filter(Objects::nonNull).forEach(resultat::add);
        kontaktadresser.stream().map(Kontaktadresse::getUtenlandskAdresse).map(a -> mapUtenlandskadresse(AdresseType.POSTADRESSE_UTLAND, a)).filter(Objects::nonNull).forEach(resultat::add);
        kontaktadresser.stream().map(Kontaktadresse::getUtenlandskAdresseIFrittFormat).map(a -> mapFriAdresseUtland(AdresseType.POSTADRESSE_UTLAND, a)).filter(Objects::nonNull).forEach(resultat::add);
        if (resultat.isEmpty()) {
            resultat.add(mapUkjentadresse(null));
        }
        return resultat;
    }

    private Adresseinfo mapVegadresse(AdresseType type, Vegadresse vegadresse) {
        if (vegadresse == null)
            return null;
        String postnummer = Optional.ofNullable(vegadresse.getPostnummer()).orElse(HARDKODET_POSTNR);
        var gateadresse = vegadresse.getAdressenavn().toUpperCase() + hvisfinnes(vegadresse.getHusnummer()) + hvisfinnes(vegadresse.getHusbokstav());
        return Adresseinfo.builder(type)
                // TODO: enable når sammenligning stabil .medMatrikkelId(vegadresse.getMatrikkelId())
                .medAdresselinje1(vegadresse.getTilleggsnavn() != null ? vegadresse.getTilleggsnavn().toUpperCase() : gateadresse)
                .medAdresselinje2(vegadresse.getTilleggsnavn() != null ? gateadresse : null)
                .medPostNr(postnummer)
                .medPoststed(tilPoststed(postnummer))
                .medLand(Landkoder.NOR.getKode())
                .buildTemporary();
    }

    private Adresseinfo mapMatrikkeladresse(AdresseType type, Matrikkeladresse matrikkeladresse) {
        if (matrikkeladresse == null)
            return null;
        String postnummer = Optional.ofNullable(matrikkeladresse.getPostnummer()).orElse(HARDKODET_POSTNR);
        return Adresseinfo.builder(type)
                // TODO: enable når sammenligning stabil .medMatrikkelId(matrikkeladresse.getMatrikkelId())
                .medAdresselinje1(matrikkeladresse.getTilleggsnavn() != null ? matrikkeladresse.getTilleggsnavn().toUpperCase() : matrikkeladresse.getBruksenhetsnummer())
                .medAdresselinje2(matrikkeladresse.getTilleggsnavn() != null ? matrikkeladresse.getBruksenhetsnummer() : null)
                .medPostNr(postnummer)
                .medPoststed(tilPoststed(postnummer))
                .medLand(Landkoder.NOR.getKode())
                .buildTemporary();
    }

    private Adresseinfo mapPostboksadresse(AdresseType type, Postboksadresse postboksadresse) {
        if (postboksadresse == null)
            return null;
        String postnummer = Optional.ofNullable(postboksadresse.getPostnummer()).orElse(HARDKODET_POSTNR);
        var postboks = "Postboks" + hvisfinnes(postboksadresse.getPostboks());
        return Adresseinfo.builder(type)
                .medAdresselinje1(postboksadresse.getPostbokseier() != null ? postboksadresse.getPostbokseier().toUpperCase() : postboks)
                .medAdresselinje2(postboksadresse.getPostbokseier() != null ? postboks : null)
                .medPostNr(postnummer)
                .medPoststed(tilPoststed(postnummer))
                .medLand(Landkoder.NOR.getKode())
                .buildTemporary();
    }

    private Adresseinfo mapFriAdresseNorsk(AdresseType type, PostadresseIFrittFormat postadresse) {
        if (postadresse == null)
            return null;
        String postnummer = Optional.ofNullable(postadresse.getPostnummer()).orElse(HARDKODET_POSTNR);
        return Adresseinfo.builder(type)
                .medAdresselinje1(postadresse.getAdresselinje1() != null ? postadresse.getAdresselinje1().toUpperCase() : null)
                .medAdresselinje2(postadresse.getAdresselinje2() != null ? postadresse.getAdresselinje2().toUpperCase() : null)
                .medAdresselinje3(postadresse.getAdresselinje3() != null ? postadresse.getAdresselinje3().toUpperCase() : null)
                .medPostNr(postnummer)
                .medPoststed(tilPoststed(postnummer))
                .medLand(Landkoder.NOR.getKode())
                .buildTemporary();
    }

    private static Adresseinfo mapUkjentadresse(UkjentBosted ukjentBosted) {
        return Adresseinfo.builder(AdresseType.UKJENT_ADRESSE).buildTemporary();
    }

    private static Adresseinfo mapUtenlandskadresse(AdresseType type, UtenlandskAdresse utenlandskAdresse) {
        if (utenlandskAdresse == null)
            return null;
        var linje1 = hvisfinnes(utenlandskAdresse.getAdressenavnNummer()) + hvisfinnes(utenlandskAdresse.getBygningEtasjeLeilighet()) + hvisfinnes(utenlandskAdresse.getPostboksNummerNavn());
        var linje2 = hvisfinnes(utenlandskAdresse.getPostkode()) + hvisfinnes(utenlandskAdresse.getBySted()) + hvisfinnes(utenlandskAdresse.getRegionDistriktOmraade());
        return Adresseinfo.builder(type)
                .medAdresselinje1(linje1)
                .medAdresselinje2(linje2)
                .medAdresselinje3(utenlandskAdresse.getLandkode())
                .medLand(utenlandskAdresse.getLandkode())
                .buildTemporary();
    }

    private static Adresseinfo mapFriAdresseUtland(AdresseType type, UtenlandskAdresseIFrittFormat utenlandskAdresse) {
        if (utenlandskAdresse == null)
            return null;
        var postlinje = hvisfinnes(utenlandskAdresse.getPostkode()) + hvisfinnes(utenlandskAdresse.getByEllerStedsnavn());
        var sisteline = utenlandskAdresse.getAdresselinje3() != null ? postlinje + utenlandskAdresse.getLandkode()
                : (utenlandskAdresse.getAdresselinje2() != null ? utenlandskAdresse.getLandkode() : null);
        return Adresseinfo.builder(type)
                .medAdresselinje1(utenlandskAdresse.getAdresselinje1())
                .medAdresselinje2(utenlandskAdresse.getAdresselinje2() != null ? utenlandskAdresse.getAdresselinje2().toUpperCase() : postlinje)
                .medAdresselinje3(utenlandskAdresse.getAdresselinje3() != null ? utenlandskAdresse.getAdresselinje3().toUpperCase() : (utenlandskAdresse.getAdresselinje2() != null ? postlinje : utenlandskAdresse.getLandkode()))
                .medAdresselinje4(sisteline)
                .medLand(utenlandskAdresse.getLandkode())
                .buildTemporary();
    }

    private static String hvisfinnes(Object object) {
        return object == null ? "" : " " + object.toString().trim().toUpperCase();
    }

    private String tilPoststed(String postnummer) {
        if (HARDKODET_POSTNR.equals(postnummer)) {
            return HARDKODET_POSTSTED;
        }
        return poststedKodeverkRepository.finnPostnummer(postnummer).map(Poststed::getPoststednavn).orElse(HARDKODET_POSTSTED);
    }

    private static Adresseinfo velgAdresse(List<Adresseinfo> alleAdresser) {
        return alleAdresser.stream().filter(a -> AdresseType.POSTADRESSE.equals(a.getGjeldendePostadresseType())).findFirst()
            .orElseGet(() -> alleAdresser.stream().filter(a -> AdresseType.POSTADRESSE_UTLAND.equals(a.getGjeldendePostadresseType())).findFirst()
                .orElseGet(() -> alleAdresser.stream().filter(a -> AdresseType.MIDLERTIDIG_POSTADRESSE_NORGE.equals(a.getGjeldendePostadresseType())).findFirst()
                    .orElseGet(() -> alleAdresser.stream().filter(a -> AdresseType.MIDLERTIDIG_POSTADRESSE_UTLAND.equals(a.getGjeldendePostadresseType())).findFirst()
                        .orElseGet(() -> alleAdresser.stream().filter(a -> AdresseType.BOSTEDSADRESSE.equals(a.getGjeldendePostadresseType())).findFirst()
                            .orElse(Adresseinfo.builder(AdresseType.UKJENT_ADRESSE).buildTemporary())

                ))));
    }

    private static boolean erLikeAdresse(Adresseinfo pdl, Adresseinfo tps) {
        if (tps == null && pdl == null) return true;
        if (pdl == null || tps == null || tps.getClass() != pdl.getClass()) return false;
        return Objects.equals(pdl.getMottakerNavn(), tps.getMottakerNavn()) &&
            pdl.isRegistrertDød() == tps.isRegistrertDød() &&
            Objects.equals(pdl,tps) ;
    }

    private static String finnAvvikAdresse(Adresseinfo tps, Adresseinfo pdl, List<Adresseinfo> alle) {
        String navn = Objects.equals(tps.getMottakerNavn(), pdl.getMottakerNavn()) ? "" : " navn ";
        //String ddato = Objects.equals(tps.getDødsdato(), pdl.getDødsdato()) ? "" : " død ";
        String status = Objects.equals(tps.isRegistrertDød(), pdl.isRegistrertDød()) ? "" : " status " + tps.isRegistrertDød() + " PDL " + pdl.isRegistrertDød();
        String adresse = Objects.equals(tps.getGjeldendePostadresseType(), pdl.getGjeldendePostadresseType()) ? "" :
                " type " + tps.getGjeldendePostadresseType() + " PDL " + pdl.getGjeldendePostadresseType();
        String adresse2 = Objects.equals(tps.getPostNr(), pdl.getPostNr()) ? "" : " land " + tps.getPostNr() + " PDL " + pdl.getPostNr();
        String adresse3 = Objects.equals(tps.getLand(), pdl.getLand()) ? "" : " land " + tps.getLand() + " PDL " + pdl.getLand();
        String typer = Objects.equals(tps.getGjeldendePostadresseType(), pdl.getGjeldendePostadresseType()) ? "" :
                " typer " + alle.stream().map(Adresseinfo::getGjeldendePostadresseType).collect(Collectors.toList());
        return "Avvik" + navn + status + adresse + adresse2 + adresse3 + typer;
    }

}

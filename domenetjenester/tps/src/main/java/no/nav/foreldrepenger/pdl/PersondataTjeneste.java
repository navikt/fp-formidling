package no.nav.foreldrepenger.pdl;

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
import no.nav.vedtak.konfig.Tid;
import no.nav.vedtak.util.LRUCache;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class PersondataTjeneste {

    /*
     * Dokumentasjon adresser: https://navikt.github.io/pdl/#_adresser
     * Det er brukt TPS-kompatible forretningsregler - se kommentar i bunn av fil for gjeldendeAdresse
     */

    private static final String HARDKODET_POSTNR = "XXXX";
    private static final String HARDKODET_POSTSTED = "UKJENT";

    private static final String FREG_DØD = "doed";
    private static final String FREG_DØD_FORENKLET = "doedIFolkeregisteret";

    private static final int DEFAULT_CACHE_SIZE = 1000;
    private static final long DEFAULT_CACHE_TIMEOUT = TimeUnit.MILLISECONDS.convert(8, TimeUnit.HOURS);

    private LRUCache<AktørId, PersonIdent> cacheAktørIdTilIdent;

    private PdlKlient pdlKlient;
    private PoststedKodeverkRepository poststedKodeverkRepository;

    PersondataTjeneste() {
        // CDI
    }

    @Inject
    public PersondataTjeneste(PdlKlient pdlKlient, PoststedKodeverkRepository repository) {
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
            identliste = pdlKlient.hentIdenter(request, projection);
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

    public Personinfo hentPersoninfo(AktørId aktørId, PersonIdent personIdent) {

        var query = new HentPersonQueryRequest();
        query.setIdent(aktørId.getId());
        var projection = new PersonResponseProjection()
            .navn(new NavnResponseProjection().forkortetNavn().fornavn().mellomnavn().etternavn())
            .foedsel(new FoedselResponseProjection().foedselsdato())
            .doedsfall(new DoedsfallResponseProjection().doedsdato())
            .kjoenn(new KjoennResponseProjection().kjoenn())
            .folkeregisterpersonstatus(new FolkeregisterpersonstatusResponseProjection().forenkletStatus());

        var person = pdlKlient.hentPerson(query, projection);

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
            .findFirst().map(PersondataTjeneste::harPersonstatusDød).orElse(false);
        return Personinfo.getbuilder(aktørId).medPersonIdent(personIdent)
            .medNavn(person.getNavn().stream().map(PersondataTjeneste::mapNavn).filter(Objects::nonNull).findFirst().orElse("MANGLER NAVN"))
            .medFødselsdato(fødselsdato)
            .medDødsdato(dødssdato)
            .medNavBrukerKjønn(mapKjønn(person))
            .medRegistrertDød(pdlStatusDød)
            .build();
    }

    public Adresseinfo hentAdresseinformasjon(AktørId aktørId, PersonIdent personIdent) {
        var query = new HentPersonQueryRequest();
        query.setIdent(aktørId.getId());
        var projection = new PersonResponseProjection()
                .navn(new NavnResponseProjection().forkortetNavn().fornavn().mellomnavn().etternavn())
                .doedsfall(new DoedsfallResponseProjection().doedsdato())
                .folkeregisterpersonstatus(new FolkeregisterpersonstatusResponseProjection().forenkletStatus().status())
                .bostedsadresse(new BostedsadresseResponseProjection().gyldigFraOgMed().angittFlyttedato().coAdressenavn()
                        .vegadresse(new VegadresseResponseProjection().adressenavn().husnummer().husbokstav().postnummer())
                        .matrikkeladresse(new MatrikkeladresseResponseProjection().tilleggsnavn().postnummer())
                        .ukjentBosted(new UkjentBostedResponseProjection().bostedskommune())
                        .utenlandskAdresse(new UtenlandskAdresseResponseProjection().adressenavnNummer().bygningEtasjeLeilighet().postboksNummerNavn().bySted().regionDistriktOmraade().postkode().landkode()))
                .oppholdsadresse(new OppholdsadresseResponseProjection().gyldigFraOgMed().coAdressenavn()
                        .vegadresse(new VegadresseResponseProjection().adressenavn().husnummer().husbokstav().postnummer())
                        .matrikkeladresse(new MatrikkeladresseResponseProjection().tilleggsnavn().postnummer())
                        .utenlandskAdresse(new UtenlandskAdresseResponseProjection().adressenavnNummer().bygningEtasjeLeilighet().postboksNummerNavn().bySted().regionDistriktOmraade().postkode().landkode()))
                .kontaktadresse(new KontaktadresseResponseProjection().type().gyldigFraOgMed().coAdressenavn()
                        .vegadresse(new VegadresseResponseProjection().adressenavn().husnummer().husbokstav().postnummer())
                        .postboksadresse(new PostboksadresseResponseProjection().postboks().postbokseier().postnummer())
                        .postadresseIFrittFormat(new PostadresseIFrittFormatResponseProjection().adresselinje1().adresselinje2().adresselinje3().postnummer())
                        .utenlandskAdresse(new UtenlandskAdresseResponseProjection().adressenavnNummer().bygningEtasjeLeilighet().postboksNummerNavn().bySted().regionDistriktOmraade().postkode().landkode())
                        .utenlandskAdresseIFrittFormat(new UtenlandskAdresseIFrittFormatResponseProjection().adresselinje1().adresselinje2().adresselinje3().byEllerStedsnavn().postkode().landkode()))
                ;

        var person = pdlKlient.hentPerson(query, projection);

        var navn = person.getNavn().stream().map(PersondataTjeneste::mapNavn).filter(Objects::nonNull).findFirst().orElse("MANGLER NAVN");
        // TODO: Avklar om man skal sette personstatus død hvis det foreligger dødsdato !
        var pdlStatusDød = person.getFolkeregisterpersonstatus().stream()
                .map(Folkeregisterpersonstatus::getForenkletStatus)
                .findFirst().map(PersondataTjeneste::harPersonstatusDød).orElse(false);

        // TODO: Avklar om man skal hente dødsboadresse og evt bruke den.
        var adresser = mapAdresser(person.getBostedsadresse(), person.getKontaktadresse(), person.getOppholdsadresse());

        // Implementerer TPS-lignende rangering for minimalt avvik mot TPS
        var antattGjeldende = velgAdresse(adresser);

        return new Adresseinfo.Builder(antattGjeldende.getGjeldendePostadresseType(), personIdent, navn, pdlStatusDød)
                .medAdresselinje1(antattGjeldende.getAdresselinje1())
                .medAdresselinje2(antattGjeldende.getAdresselinje2())
                .medAdresselinje3(antattGjeldende.getAdresselinje3())
                .medAdresselinje4(antattGjeldende.getAdresselinje4())
                .medPostNr(antattGjeldende.getPostNr())
                .medPoststed(antattGjeldende.getPoststed())
                .medLand(antattGjeldende.getLand())
                .build();
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

    private List<Adresseinfo> mapAdresser(List<Bostedsadresse> bostedsadresser, List<Kontaktadresse> kontaktadresser, List<Oppholdsadresse> oppholdsadresser) {
        List<Adresseinfo> resultat = new ArrayList<>();
        var bostedFom = bostedsadresser.stream().map(PersondataTjeneste::bostedAdresseFom).filter(Objects::nonNull).max(Comparator.naturalOrder()).orElse(Tid.TIDENES_BEGYNNELSE);
        bostedsadresser.stream().map(a -> mapVegadresse(AdresseType.BOSTEDSADRESSE, a.getVegadresse(), a.getCoAdressenavn(), bostedFom)).filter(Objects::nonNull).forEach(resultat::add);
        bostedsadresser.stream().map(a -> mapMatrikkeladresse(AdresseType.BOSTEDSADRESSE, a.getMatrikkeladresse(), a.getCoAdressenavn(), bostedFom)).filter(Objects::nonNull).forEach(resultat::add);
        bostedsadresser.stream().map(a -> mapUkjentadresse(a.getUkjentBosted(), bostedFom)).filter(Objects::nonNull).forEach(resultat::add);
        bostedsadresser.stream().map(a -> mapUtenlandskadresse(AdresseType.BOSTEDSADRESSE, a.getUtenlandskAdresse(), a.getCoAdressenavn(), bostedFom)).filter(Objects::nonNull).forEach(resultat::add);

        var oppholdFom = oppholdsadresser.stream().map(Oppholdsadresse::getGyldigFraOgMed).filter(Objects::nonNull).map(PersondataTjeneste::tilLocalDate).max(Comparator.naturalOrder()).orElse(Tid.TIDENES_BEGYNNELSE);
        oppholdsadresser.stream().map(a -> mapVegadresse(AdresseType.MIDLERTIDIG_POSTADRESSE_NORGE, a.getVegadresse(), a.getCoAdressenavn(), oppholdFom)).filter(Objects::nonNull).forEach(resultat::add);
        oppholdsadresser.stream().map(a -> mapMatrikkeladresse(AdresseType.MIDLERTIDIG_POSTADRESSE_NORGE, a.getMatrikkeladresse(), a.getCoAdressenavn(), oppholdFom)).filter(Objects::nonNull).forEach(resultat::add);
        oppholdsadresser.stream().map(a -> mapUtenlandskadresse(AdresseType.MIDLERTIDIG_POSTADRESSE_UTLAND, a.getUtenlandskAdresse(), a.getCoAdressenavn(), oppholdFom)).filter(Objects::nonNull).forEach(resultat::add);

        var kontaktFom = kontaktadresser.stream().map(Kontaktadresse::getGyldigFraOgMed).filter(Objects::nonNull).map(PersondataTjeneste::tilLocalDate).max(Comparator.naturalOrder()).orElse(Tid.TIDENES_BEGYNNELSE);
        kontaktadresser.stream().map(a -> mapVegadresse(AdresseType.POSTADRESSE, a.getVegadresse(), a.getCoAdressenavn(), kontaktFom)).filter(Objects::nonNull).forEach(resultat::add);
        kontaktadresser.stream().map(a -> mapPostboksadresse(AdresseType.POSTADRESSE, a.getPostboksadresse(), a.getCoAdressenavn(), kontaktFom)).filter(Objects::nonNull).forEach(resultat::add);
        kontaktadresser.stream().map(a -> mapFriAdresseNorsk(AdresseType.POSTADRESSE, a.getPostadresseIFrittFormat(), a.getCoAdressenavn(), kontaktFom)).filter(Objects::nonNull).forEach(resultat::add);
        kontaktadresser.stream().map(a -> mapUtenlandskadresse(AdresseType.POSTADRESSE_UTLAND, a.getUtenlandskAdresse(), a.getCoAdressenavn(), kontaktFom)).filter(Objects::nonNull).forEach(resultat::add);
        kontaktadresser.stream().map(a -> mapFriAdresseUtland(AdresseType.POSTADRESSE_UTLAND, a.getUtenlandskAdresseIFrittFormat(), a.getCoAdressenavn(), kontaktFom)).filter(Objects::nonNull).forEach(resultat::add);
        if (resultat.isEmpty()) {
            resultat.add(Adresseinfo.builder(AdresseType.UKJENT_ADRESSE).medGyldigFom(Tid.TIDENES_BEGYNNELSE).buildTemporary());
        }
        return resultat;
    }

    // TODO: Vurder om man skal bruke flyttedato dersom satt. Gyldigfom ga minst avvik mot tps men kan være en stund etter (el før) flyttedato.
    private static LocalDate bostedAdresseFom(Bostedsadresse bostedsadresse) {
        if (bostedsadresse.getGyldigFraOgMed() != null)
            return tilLocalDate(bostedsadresse.getGyldigFraOgMed());
        return bostedsadresse.getAngittFlyttedato() != null ? LocalDate.parse(bostedsadresse.getAngittFlyttedato(), DateTimeFormatter.ISO_LOCAL_DATE) : null;
    }

    private static LocalDate tilLocalDate(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).toLocalDate();
    }

    private Adresseinfo mapVegadresse(AdresseType type, Vegadresse vegadresse, String coNavn, LocalDate fom) {
        if (vegadresse == null)
            return null;
        String postnummer = Optional.ofNullable(vegadresse.getPostnummer()).orElse(HARDKODET_POSTNR);
        var coledd = coNavn != null && !coNavn.isBlank() ? coNavn : null;
        var gateadresse = vegadresse.getAdressenavn() + hvisfinnes(vegadresse.getHusnummer()) + hvisfinnes(vegadresse.getHusbokstav());
        return Adresseinfo.builder(type)
                .medAdresselinje1(coledd != null ? coledd : gateadresse)
                .medAdresselinje2(coledd != null ? gateadresse : null)
                .medPostNr(postnummer)
                .medPoststed(tilPoststed(postnummer))
                .medLand(Landkoder.NOR.getKode())
                .medGyldigFom(fom)
                .buildTemporary();
    }

    private Adresseinfo mapMatrikkeladresse(AdresseType type, Matrikkeladresse matrikkeladresse, String coNavn, LocalDate fom) {
        if (matrikkeladresse == null)
            return null;
        String postnummer = Optional.ofNullable(matrikkeladresse.getPostnummer()).orElse(HARDKODET_POSTNR);
        var coledd = coNavn != null && !coNavn.isBlank() ? coNavn : null;
        return Adresseinfo.builder(type)
                .medAdresselinje1(coledd != null ? coledd : matrikkeladresse.getTilleggsnavn())
                .medAdresselinje2(coledd != null ? matrikkeladresse.getTilleggsnavn() : null)
                .medPostNr(postnummer)
                .medPoststed(tilPoststed(postnummer))
                .medLand(Landkoder.NOR.getKode())
                .medGyldigFom(fom)
                .buildTemporary();
    }

    private Adresseinfo mapPostboksadresse(AdresseType type, Postboksadresse postboksadresse, String coNavn, LocalDate fom) {
        if (postboksadresse == null)
            return null;
        String postnummer = Optional.ofNullable(postboksadresse.getPostnummer()).orElse(HARDKODET_POSTNR);
        var postboksRaw = hvisfinnes(postboksadresse.getPostboks());
        var postboks = postboksRaw.toLowerCase().contains("postboks") ? postboksRaw : "Postboks " + postboksRaw;
        var coledd = coNavn != null && !coNavn.isBlank() ? coNavn : postboksadresse.getPostbokseier();
        return Adresseinfo.builder(type)
                .medAdresselinje1(coledd != null ? coledd : postboks)
                .medAdresselinje2(coledd != null ? postboks : null)
                .medPostNr(postnummer)
                .medPoststed(tilPoststed(postnummer))
                .medLand(Landkoder.NOR.getKode())
                .medGyldigFom(fom)
                .buildTemporary();
    }

    private Adresseinfo mapFriAdresseNorsk(AdresseType type, PostadresseIFrittFormat postadresse, String coNavn, LocalDate fom) {
        if (postadresse == null)
            return null;
        String postnummer = Optional.ofNullable(postadresse.getPostnummer()).orElse(HARDKODET_POSTNR);
        var coledd = coNavn != null && !coNavn.isBlank() ? coNavn : null;
        return Adresseinfo.builder(type)
                .medAdresselinje1(coledd != null ? coledd : postadresse.getAdresselinje1())
                .medAdresselinje2(coledd != null ? postadresse.getAdresselinje1() : postadresse.getAdresselinje2())
                .medAdresselinje3(coledd != null ? postadresse.getAdresselinje2() : postadresse.getAdresselinje3())
                .medAdresselinje4(coledd != null ? postadresse.getAdresselinje3() : null)
                .medPostNr(postnummer)
                .medPoststed(tilPoststed(postnummer))
                .medLand(Landkoder.NOR.getKode())
                .medGyldigFom(fom)
                .buildTemporary();
    }

    private static Adresseinfo mapUkjentadresse(UkjentBosted ukjentBosted, LocalDate fom) {
        return ukjentBosted == null ? null : Adresseinfo.builder(AdresseType.UKJENT_ADRESSE).medGyldigFom(fom).buildTemporary();
    }

    private static Adresseinfo mapUtenlandskadresse(AdresseType type, UtenlandskAdresse utenlandskAdresse, String coNavn, LocalDate fom) {
        if (utenlandskAdresse == null)
            return null;
        var coledd = coNavn != null && !coNavn.isBlank() ? coNavn : null;
        var linje1 = hvisfinnes(utenlandskAdresse.getAdressenavnNummer()) + hvisfinnes(utenlandskAdresse.getBygningEtasjeLeilighet()) + hvisfinnes(utenlandskAdresse.getPostboksNummerNavn());
        var linje2 = hvisfinnes(utenlandskAdresse.getPostkode()) + hvisfinnes(utenlandskAdresse.getBySted()) + hvisfinnes(utenlandskAdresse.getRegionDistriktOmraade());
        // TODO: Vurder om postkode bør i postnr og om bysted + region bør i poststed. Var ikke slik med TPS.
        return Adresseinfo.builder(type)
                .medAdresselinje1(coledd != null ? coledd : linje1)
                .medAdresselinje2(coledd != null ? linje1 : linje2)
                .medAdresselinje3(coledd != null ? linje2 : utenlandskAdresse.getLandkode())
                .medAdresselinje4(coledd != null ? utenlandskAdresse.getLandkode() : null)
                .medLand(utenlandskAdresse.getLandkode())
                .medGyldigFom(fom)
                .buildTemporary();
    }

    private static Adresseinfo mapFriAdresseUtland(AdresseType type, UtenlandskAdresseIFrittFormat utenlandskAdresse, String coNavn, LocalDate fom) {
        if (utenlandskAdresse == null)
            return null;
        var coledd = coNavn != null && !coNavn.isBlank() ? coNavn : null;
        var postlinje = hvisfinnes(utenlandskAdresse.getPostkode()) + hvisfinnes(utenlandskAdresse.getByEllerStedsnavn());
        // TODO: Vurder om postkode bør i postnr og om bysted + region bør i poststed. Var ikke slik med TPS.
        if (coledd != null) {
            return Adresseinfo.builder(type)
                    .medAdresselinje1(coledd)
                    .medAdresselinje2(utenlandskAdresse.getAdresselinje1())
                    .medAdresselinje3(utenlandskAdresse.getAdresselinje2())
                    .medAdresselinje4(hvisfinnes(utenlandskAdresse.getAdresselinje3()) + postlinje)
                    .medLand(utenlandskAdresse.getLandkode())
                    .medGyldigFom(fom)
                    .buildTemporary();
        } else {
            return Adresseinfo.builder(type)
                    .medAdresselinje1(utenlandskAdresse.getAdresselinje1())
                    .medAdresselinje2(utenlandskAdresse.getAdresselinje2())
                    .medAdresselinje3(utenlandskAdresse.getAdresselinje3())
                    .medAdresselinje4(postlinje.isBlank() ? null : postlinje)
                    .medLand(utenlandskAdresse.getLandkode())
                    .medGyldigFom(fom)
                    .buildTemporary();
        }
    }

    private static String hvisfinnes(Object object) {
        return object == null ? "" : " " + object.toString().trim();
    }

    private String tilPoststed(String postnummer) {
        if (HARDKODET_POSTNR.equals(postnummer)) {
            return HARDKODET_POSTSTED;
        }
        return poststedKodeverkRepository.finnPostnummer(postnummer).map(Poststed::getPoststednavn).orElse(HARDKODET_POSTSTED);
    }

    /*
     * Tilpasset forretningsregler fra TPS. Bruker rangering nedenfor med mindre det finnes en bostedsadresse
     * OBS: Bostedsadresser har en gyldigfom og flyttedato. Nå er brukt gyldigfom ettersom det ga færrest avvik ift TPS.
     */
    private static Adresseinfo velgAdresse(List<Adresseinfo> alleAdresser) {
        var nyesteBostedAdresse = nyesteAdresseAvType(alleAdresser, AdresseType.BOSTEDSADRESSE).orElse(null);
        var rangertAdresse = nyesteAdresseAvType(alleAdresser, AdresseType.POSTADRESSE)
            .orElseGet(() -> nyesteAdresseAvType(alleAdresser, AdresseType.POSTADRESSE_UTLAND)
                .orElseGet(() -> nyesteAdresseAvType(alleAdresser, AdresseType.MIDLERTIDIG_POSTADRESSE_NORGE)
                    .orElseGet(() -> nyesteAdresseAvType(alleAdresser, AdresseType.MIDLERTIDIG_POSTADRESSE_UTLAND)
                        .orElseGet(() -> nyesteAdresseAvType(alleAdresser, AdresseType.BOSTEDSADRESSE)
                            .orElse(Adresseinfo.builder(AdresseType.UKJENT_ADRESSE).medGyldigFom(Tid.TIDENES_BEGYNNELSE).buildTemporary())

                ))));
        if (nyesteBostedAdresse != null && nyesteBostedAdresse.getGyldigFom().isAfter(rangertAdresse.getGyldigFom()))
            return nyesteBostedAdresse;
        return rangertAdresse;
    }

    private static Optional<Adresseinfo> nyesteAdresseAvType(List<Adresseinfo> alleAdresser, AdresseType adresseType) {
        return alleAdresser.stream().filter(a -> adresseType.equals(a.getGjeldendePostadresseType())).max(Comparator.comparing(Adresseinfo::getGyldigFom));
    }

}

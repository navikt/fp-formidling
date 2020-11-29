package no.nav.foreldrepenger.pdl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.melding.aktør.Personinfo;
import no.nav.foreldrepenger.melding.personopplysning.NavBrukerKjønn;
import no.nav.foreldrepenger.melding.typer.AktørId;
import no.nav.foreldrepenger.melding.typer.PersonIdent;
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
import no.nav.pdl.Navn;
import no.nav.pdl.NavnResponseProjection;
import no.nav.pdl.Person;
import no.nav.pdl.PersonResponseProjection;
import no.nav.vedtak.exception.VLException;
import no.nav.vedtak.felles.integrasjon.pdl.PdlKlient;
import no.nav.vedtak.felles.integrasjon.pdl.Tema;
import no.nav.vedtak.util.LRUCache;

@ApplicationScoped
public class AktørTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(AktørTjeneste.class);

    private static final String FREG_DØD = "doed";
    private static final String FREG_DØD_FORENKLET = "doedIFolkeregisteret";

    private static final int DEFAULT_CACHE_SIZE = 1000;
    private static final long DEFAULT_CACHE_TIMEOUT = TimeUnit.MILLISECONDS.convert(8, TimeUnit.HOURS);

    private LRUCache<AktørId, PersonIdent> cacheAktørIdTilIdent;
    private LRUCache<PersonIdent, AktørId> cacheIdentTilAktørId;

    private PdlKlient pdlKlient;

    AktørTjeneste() {
        // CDI
    }

    @Inject
    public AktørTjeneste(PdlKlient pdlKlient) {
        this.pdlKlient = pdlKlient;
        this.cacheAktørIdTilIdent = new LRUCache<>(DEFAULT_CACHE_SIZE, DEFAULT_CACHE_TIMEOUT);
        this.cacheIdentTilAktørId = new LRUCache<>(DEFAULT_CACHE_SIZE, DEFAULT_CACHE_TIMEOUT);
    }

    public Optional<PersonIdent> hentPersonIdentForAktørId(AktørId aktørId, Optional<PersonIdent> fraTps) {
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
            LOG.info("FPRISK PDL AKTØRID hentident error", v);
            return Optional.empty();
        } catch (Exception e) {
            LOG.info("FPRISK PDL AKTØRID hentident error", e);
            return Optional.empty();
        }

        var ident = identliste.getIdenter().stream().findFirst().map(IdentInformasjon::getIdent).map(PersonIdent::new);
        var antall = identliste.getIdenter().size();
        ident.ifPresent(i -> cacheAktørIdTilIdent.put(aktørId, i));
        if (antall == 1 && Objects.equals(ident, fraTps)) {
            LOG.info("FPRISK PDL AKTØRID: like identer");
        } else if (antall != 1 && Objects.equals(ident, fraTps)) {
            LOG.info("FPRISK PDL AKTØRID: ulikt antall identer {}", antall);
        } else {
            LOG.info("FPRISK PDL AKTØRID: ulike identer TPS og PDL antall {}", antall);
        }
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
}

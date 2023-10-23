package no.nav.foreldrepenger.fpformidling.integrasjon.pdl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.aktør.Personinfo;
import no.nav.foreldrepenger.fpformidling.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.personopplysning.NavBrukerKjønn;
import no.nav.foreldrepenger.fpformidling.typer.AktørId;
import no.nav.foreldrepenger.fpformidling.typer.PersonIdent;
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
import no.nav.vedtak.felles.integrasjon.person.Persondata;
import no.nav.vedtak.util.LRUCache;

@ApplicationScoped
public class PersondataTjeneste {

    private static final String FREG_DØD = "doed";
    private static final String FREG_DØD_FORENKLET = "doedIFolkeregisteret";

    private static final int DEFAULT_CACHE_SIZE = 1000;
    private static final long DEFAULT_CACHE_TIMEOUT = TimeUnit.MILLISECONDS.convert(8, TimeUnit.HOURS);

    private LRUCache<AktørId, PersonIdent> cacheAktørIdTilIdent;

    private Persondata pdlKlient;

    PersondataTjeneste() {
        // CDI
    }

    @Inject
    public PersondataTjeneste(Persondata pdlKlient) {
        this.pdlKlient = pdlKlient;
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
        var projection = new IdentlisteResponseProjection().identer(new IdentInformasjonResponseProjection().ident());

        final Identliste identliste;

        try {
            identliste = pdlKlient.hentIdenter(request, projection);
        } catch (VLException v) {
            if (Persondata.PDL_KLIENT_NOT_FOUND_KODE.equals(v.getKode())) {
                return Optional.empty();
            }
            throw v;
        }

        var ident = identliste.getIdenter().stream().findFirst().map(IdentInformasjon::getIdent).map(PersonIdent::new);
        ident.ifPresent(i -> cacheAktørIdTilIdent.put(aktørId, i));
        return ident;
    }

    public Personinfo hentPersoninfo(FagsakYtelseType ytelseType, AktørId aktørId, PersonIdent personIdent) {

        var query = new HentPersonQueryRequest();
        query.setIdent(aktørId.getId());
        var projection = new PersonResponseProjection().navn(new NavnResponseProjection().forkortetNavn().fornavn().mellomnavn().etternavn())
            .foedsel(new FoedselResponseProjection().foedselsdato())
            .doedsfall(new DoedsfallResponseProjection().doedsdato())
            .kjoenn(new KjoennResponseProjection().kjoenn())
            .folkeregisterpersonstatus(new FolkeregisterpersonstatusResponseProjection().forenkletStatus());

        var ytelse = utledYtelse(ytelseType);
        var person = pdlKlient.hentPerson(ytelse, query, projection);

        var fødselsdato = person.getFoedsel()
            .stream()
            .map(Foedsel::getFoedselsdato)
            .filter(Objects::nonNull)
            .findFirst()
            .map(d -> LocalDate.parse(d, DateTimeFormatter.ISO_LOCAL_DATE))
            .orElse(null);
        var dødssdato = person.getDoedsfall()
            .stream()
            .map(Doedsfall::getDoedsdato)
            .filter(Objects::nonNull)
            .findFirst()
            .map(d -> LocalDate.parse(d, DateTimeFormatter.ISO_LOCAL_DATE))
            .orElse(null);
        var pdlStatusDød = person.getFolkeregisterpersonstatus()
            .stream()
            .map(Folkeregisterpersonstatus::getForenkletStatus)
            .findFirst()
            .map(PersondataTjeneste::harPersonstatusDød)
            .orElse(false);
        return Personinfo.getbuilder(aktørId)
            .medPersonIdent(personIdent)
            .medNavn(person.getNavn().stream().map(PersondataTjeneste::mapNavn).filter(Objects::nonNull).findFirst().orElse("MANGLER NAVN"))
            .medFødselsdato(fødselsdato)
            .medDødsdato(dødssdato)
            .medNavBrukerKjønn(mapKjønn(person))
            .medRegistrertDød(pdlStatusDød)
            .build();
    }

    private static String mapNavn(Navn navn) {
        if (navn.getForkortetNavn() != null) {
            return navn.getForkortetNavn();
        }
        return navn.getEtternavn() + " " + navn.getFornavn() + (navn.getMellomnavn() == null ? "" : " " + navn.getMellomnavn());
    }

    public static boolean harPersonstatusDød(String fregStatus) {
        return fregStatus != null && (Objects.equals(FREG_DØD_FORENKLET, fregStatus) || Objects.equals(FREG_DØD, fregStatus));
    }

    private static NavBrukerKjønn mapKjønn(Person person) {
        var kode = person.getKjoenn().stream().map(Kjoenn::getKjoenn).filter(Objects::nonNull).findFirst().orElse(KjoennType.UKJENT);
        if (KjoennType.MANN.equals(kode)) {
            return NavBrukerKjønn.MANN;
        }
        return KjoennType.KVINNE.equals(kode) ? NavBrukerKjønn.KVINNE : NavBrukerKjønn.UDEFINERT;
    }

    private static Persondata.Ytelse utledYtelse(FagsakYtelseType ytelseType) {
        if (FagsakYtelseType.ENGANGSTØNAD.equals(ytelseType)) {
            return Persondata.Ytelse.ENGANGSSTØNAD;
        } else if (FagsakYtelseType.SVANGERSKAPSPENGER.equals(ytelseType)) {
            return Persondata.Ytelse.SVANGERSKAPSPENGER;
        } else {
            return Persondata.Ytelse.FORELDREPENGER;
        }
    }
}

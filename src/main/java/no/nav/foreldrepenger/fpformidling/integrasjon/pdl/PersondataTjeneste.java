package no.nav.foreldrepenger.fpformidling.integrasjon.pdl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.domene.aktør.Personinfo;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.typer.AktørId;
import no.nav.foreldrepenger.fpformidling.typer.PersonIdent;
import no.nav.pdl.DoedsfallResponseProjection;
import no.nav.pdl.FolkeregisteridentifikatorResponseProjection;
import no.nav.pdl.Folkeregisterpersonstatus;
import no.nav.pdl.FolkeregisterpersonstatusResponseProjection;
import no.nav.pdl.HentIdenterQueryRequest;
import no.nav.pdl.HentPersonQueryRequest;
import no.nav.pdl.IdentGruppe;
import no.nav.pdl.IdentInformasjon;
import no.nav.pdl.IdentInformasjonResponseProjection;
import no.nav.pdl.Identliste;
import no.nav.pdl.IdentlisteResponseProjection;
import no.nav.pdl.NavnResponseProjection;
import no.nav.pdl.PersonResponseProjection;
import no.nav.vedtak.exception.VLException;
import no.nav.vedtak.felles.integrasjon.person.FalskIdentitet;
import no.nav.vedtak.felles.integrasjon.person.PersonMappers;
import no.nav.vedtak.felles.integrasjon.person.Persondata;
import no.nav.vedtak.util.LRUCache;

@ApplicationScoped
public class PersondataTjeneste {

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
        var projection = new PersonResponseProjection().navn(new NavnResponseProjection().fornavn().mellomnavn().etternavn())
            .folkeregisteridentifikator(new FolkeregisteridentifikatorResponseProjection().identifikasjonsnummer().status())
            .doedsfall(new DoedsfallResponseProjection().doedsdato())
            .folkeregisterpersonstatus(new FolkeregisterpersonstatusResponseProjection().forenkletStatus());

        var ytelse = utledYtelse(ytelseType);
        var person = pdlKlient.hentPerson(ytelse, query, projection);

        if (PersonMappers.manglerIdentifikator(person)) {
            var falskIdent = FalskIdentitet.finnFalskIdentitet(aktørId.getId(), pdlKlient).orElse(null);
            if (falskIdent != null) {
                return Personinfo.getbuilder(aktørId)
                    .medPersonIdent(personIdent)
                    .medNavn(falskIdent.navn())
                    .medRegistrertDød(false)
                    .build();
            }
        }

        var dødsdato = PersonMappers.mapDødsdato(person);
        var pdlStatusDød = person.getFolkeregisterpersonstatus()
            .stream()
            .map(Folkeregisterpersonstatus::getForenkletStatus)
            .findFirst()
            .map(PersondataTjeneste::harPersonstatusDød)
            .orElse(false);
        return Personinfo.getbuilder(aktørId)
            .medPersonIdent(personIdent)
            .medNavn(PersonMappers.mapNavn(person).orElse("Ukjent Navn"))
            .medRegistrertDød(pdlStatusDød || dødsdato.isPresent())
            .build();
    }

    public static boolean harPersonstatusDød(String fregStatus) {
        return fregStatus != null && (Objects.equals(FREG_DØD_FORENKLET, fregStatus));
    }

    private static Persondata.Ytelse utledYtelse(FagsakYtelseType ytelseType) {
        return switch (ytelseType) {
            case ENGANGSTØNAD -> Persondata.Ytelse.ENGANGSSTØNAD;
            case SVANGERSKAPSPENGER -> Persondata.Ytelse.SVANGERSKAPSPENGER;
            default -> Persondata.Ytelse.FORELDREPENGER;
        };
    }
}

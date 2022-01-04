package no.nav.foreldrepenger.fpformidling.datamapper.arbeidsgiver;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.PersonAdapter;
import no.nav.foreldrepenger.fpformidling.aktør.Personinfo;
import no.nav.foreldrepenger.fpformidling.inntektarbeidytelse.OrganisasjonsNummerValidator;
import no.nav.foreldrepenger.fpformidling.organisasjon.VirksomhetTjeneste;
import no.nav.foreldrepenger.fpformidling.typer.AktørId;
import no.nav.vedtak.exception.VLException;
import no.nav.vedtak.util.LRUCache;

@ApplicationScoped
public class ArbeidsgiverTjeneste {

    public static final String KUNSTIG_ORG = "342352362";  // magic constant

    private static final long CACHE_ELEMENT_LIVE_TIME_MS = TimeUnit.MILLISECONDS.convert(12, TimeUnit.HOURS);
    private static final long SHORT_CACHE_ELEMENT_LIVE_TIME_MS = TimeUnit.MILLISECONDS.convert(10, TimeUnit.MINUTES);
    private PersonAdapter personAdapter;
    private LRUCache<String, ArbeidsgiverOpplysninger> cache = new LRUCache<>(1000, CACHE_ELEMENT_LIVE_TIME_MS);
    private LRUCache<String, ArbeidsgiverOpplysninger> failBackoffCache = new LRUCache<>(100, SHORT_CACHE_ELEMENT_LIVE_TIME_MS);
    private VirksomhetTjeneste virksomhetTjeneste;

    ArbeidsgiverTjeneste() {
        // CDI
    }

    @Inject
    public ArbeidsgiverTjeneste(PersonAdapter personAdapter, VirksomhetTjeneste virksomhetTjeneste) {
        this.personAdapter = personAdapter;
        this.virksomhetTjeneste = virksomhetTjeneste;
    }

    public String hentArbeidsgiverNavn(String arbeidsgiverReferanse) {
        var arbeidsgiver = hent(arbeidsgiverReferanse);
        return arbeidsgiver != null ? arbeidsgiver.getNavn() : null;
    }

    public ArbeidsgiverOpplysninger hent(String arbeidsgiver) {
        if (arbeidsgiver == null) {
            return null;
        }
        ArbeidsgiverOpplysninger arbeidsgiverOpplysninger = cache.get(arbeidsgiver);
        if (arbeidsgiverOpplysninger != null) {
            return arbeidsgiverOpplysninger;
        }
        arbeidsgiverOpplysninger = failBackoffCache.get(arbeidsgiver);
        if (arbeidsgiverOpplysninger != null) {
            return arbeidsgiverOpplysninger;
        }
        if (OrganisasjonsNummerValidator.erGyldig(arbeidsgiver) && !KUNSTIG_ORG.equals(arbeidsgiver)) {
            var virksomhet = hentVirksomhetNavn(arbeidsgiver);
            ArbeidsgiverOpplysninger nyOpplysninger = new ArbeidsgiverOpplysninger(arbeidsgiver, virksomhet.orElse("Ikke tilgjengelig"));
            cache.put(arbeidsgiver, nyOpplysninger);
            return nyOpplysninger;
        } else if (OrganisasjonsNummerValidator.erGyldig(arbeidsgiver) && KUNSTIG_ORG.equals(arbeidsgiver)) {
            return new ArbeidsgiverOpplysninger(KUNSTIG_ORG, "Arbeidsgiver utenom register");
        } else {
            Optional<Personinfo> personinfo = hentInformasjonFraTps(arbeidsgiver);
            if (personinfo.isPresent()) {
                Personinfo info = personinfo.get();
                ArbeidsgiverOpplysninger nyOpplysninger = new ArbeidsgiverOpplysninger(new AktørId(arbeidsgiver), info.getNavn());
                cache.put(arbeidsgiver, nyOpplysninger);
                return nyOpplysninger;
            } else {
                // Putter bevist ikke denne i cache da denne aktøren ikke er kjent, men legger denne i en backoff cache som benyttes for at vi ikke skal hamre på tps ved sikkerhetsbegrensning
                ArbeidsgiverOpplysninger opplysninger = new ArbeidsgiverOpplysninger(arbeidsgiver, "N/A");
                failBackoffCache.put(arbeidsgiver, opplysninger);
                return opplysninger;
            }
        }
    }

    private Optional<String> hentVirksomhetNavn(String orgNummer) {
        return virksomhetTjeneste.getNavnFor(orgNummer);
    }

    private Optional<Personinfo> hentInformasjonFraTps(String arbeidsgiver) {
        try {
            return personAdapter.hentBrukerForAktør(new AktørId(arbeidsgiver));
        } catch (VLException|IllegalArgumentException feil) {
            // Ønsker ikke å gi GUI problemer ved å eksponere exceptions
            return Optional.empty();
        }
    }
}

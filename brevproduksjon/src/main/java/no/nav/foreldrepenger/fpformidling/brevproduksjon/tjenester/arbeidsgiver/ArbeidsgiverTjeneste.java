package no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.arbeidsgiver;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.aktør.Personinfo;
import no.nav.foreldrepenger.fpformidling.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.inntektarbeidytelse.OrganisasjonsNummerValidator;
import no.nav.foreldrepenger.fpformidling.integrasjon.organisasjon.VirksomhetTjeneste;
import no.nav.foreldrepenger.fpformidling.integrasjon.pdl.PersonAdapter;
import no.nav.foreldrepenger.fpformidling.typer.AktørId;
import no.nav.vedtak.exception.VLException;
import no.nav.vedtak.util.LRUCache;

@ApplicationScoped
public class ArbeidsgiverTjeneste {

    public static final String KUNSTIG_ORG = "342352362";  // magic constant

    private static final long CACHE_ELEMENT_LIVE_TIME_MS = TimeUnit.MILLISECONDS.convert(12, TimeUnit.HOURS);
    private static final long SHORT_CACHE_ELEMENT_LIVE_TIME_MS = TimeUnit.MILLISECONDS.convert(10, TimeUnit.MINUTES);
    private PersonAdapter personAdapter;
    private final LRUCache<String, String> cache = new LRUCache<>(1000, CACHE_ELEMENT_LIVE_TIME_MS);
    private final LRUCache<String, String> failBackoffCache = new LRUCache<>(100, SHORT_CACHE_ELEMENT_LIVE_TIME_MS);
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
        if (arbeidsgiverReferanse == null) {
            return null;
        }
        var arbeidsgiverOpplysninger = cache.get(arbeidsgiverReferanse);
        if (arbeidsgiverOpplysninger != null) {
            return arbeidsgiverOpplysninger;
        }
        arbeidsgiverOpplysninger = failBackoffCache.get(arbeidsgiverReferanse);
        if (arbeidsgiverOpplysninger != null) {
            return arbeidsgiverOpplysninger;
        }
        if (OrganisasjonsNummerValidator.erGyldig(arbeidsgiverReferanse) && !KUNSTIG_ORG.equals(arbeidsgiverReferanse)) {
            var virksomhet = hentVirksomhetNavn(arbeidsgiverReferanse);
            var nyttNavn = virksomhet.orElse("Ikke tilgjengelig");
            cache.put(arbeidsgiverReferanse, nyttNavn);
            return nyttNavn;
        }
        if (OrganisasjonsNummerValidator.erGyldig(arbeidsgiverReferanse) && KUNSTIG_ORG.equals(arbeidsgiverReferanse)) {
            return "Arbeidsgiver utenom register";
        }
        var personinfo = hentInformasjonFraTps(arbeidsgiverReferanse);
        if (personinfo.isPresent()) {
            var info = personinfo.get();
            var nyttNavn = info.getNavn();
            cache.put(arbeidsgiverReferanse, nyttNavn);
            return nyttNavn;
        }
        // Putter bevist ikke denne i cache da denne aktøren ikke er kjent, men legger denne i en backoff cache som benyttes for at vi ikke skal hamre på tps ved sikkerhetsbegrensning
        var nyttNavn = "N/A";
        failBackoffCache.put(arbeidsgiverReferanse, nyttNavn);
        return nyttNavn;
    }

    private Optional<String> hentVirksomhetNavn(String orgNummer) {
        return virksomhetTjeneste.getNavnFor(orgNummer);
    }

    private Optional<Personinfo> hentInformasjonFraTps(String arbeidsgiver) {
        try {
            return personAdapter.hentBrukerForAktør(FagsakYtelseType.FORELDREPENGER, new AktørId(arbeidsgiver));
        } catch (VLException | IllegalArgumentException feil) {
            // Ønsker ikke å gi GUI problemer ved å eksponere exceptions
            return Optional.empty();
        }
    }
}

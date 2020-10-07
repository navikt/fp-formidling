package no.nav.foreldrepenger.melding.poststed;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.melding.geografisk.Postnummer;
import no.nav.foreldrepenger.melding.geografisk.PoststedKodeverkRepository;
import no.nav.vedtak.exception.IntegrasjonException;

@ApplicationScoped
public
class PostnummerSynkroniseringTjeneste {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostnummerSynkroniseringTjeneste.class);

    private static final String KODEVERK_POSTNUMMER = "Postnummer";

    private PoststedKodeverkRepository poststedKodeverkRepository;
    private KodeverkTjeneste kodeverkTjeneste;

    PostnummerSynkroniseringTjeneste() {
        // for CDI proxy
    }

    @Inject
    PostnummerSynkroniseringTjeneste(PoststedKodeverkRepository poststedKodeverkRepository,
                                     KodeverkTjeneste kodeverkTjeneste) {
        this.poststedKodeverkRepository = poststedKodeverkRepository;
        this.kodeverkTjeneste = kodeverkTjeneste;
    }

    public void synkroniserPostnummer() {
        LOGGER.info("Synkroniserer kodeverk: {}", KODEVERK_POSTNUMMER); // NOSONAR

        LocalDate kodeverksDato = poststedKodeverkRepository.getPostnummerKodeverksDato();

        try {
            KodeverkInfo pnrInfo = kodeverkTjeneste.hentGjeldendeKodeverk(KODEVERK_POSTNUMMER).orElseThrow();
            if (pnrInfo.getVersjonDato().isAfter(kodeverksDato)) {
                lagreNyVersjon(pnrInfo);
                LOGGER.info("Nye Postnummer lagret: versjon {} med dato {}", pnrInfo.getVersjon(), pnrInfo.getVersjonDato());
            } else {
                LOGGER.info("Har allerede Postnummer: versjon {} med dato {}", pnrInfo.getVersjon(), pnrInfo.getVersjonDato());
            }
        } catch (IntegrasjonException ex) {
            throw KodeverkFeil.FACTORY.synkronoseringAvKodeverkFeilet(ex).toException();
        }
    }

    private void lagreNyVersjon(KodeverkInfo pnrInfo) {
        Map<String, Postnummer> eksisterendeMap = poststedKodeverkRepository.hentAllePostnummer().stream()
                .collect(Collectors.toMap(Postnummer::getPoststednummer, p -> p));
        Map<String, KodeverkKode> masterKoderMap = kodeverkTjeneste.hentKodeverk(KODEVERK_POSTNUMMER, pnrInfo.getVersjon());
        masterKoderMap.forEach((key, value) -> synkroniserNyEllerEksisterendeKode(eksisterendeMap, value));
        poststedKodeverkRepository.setPostnummerKodeverksDato(pnrInfo.getVersjon(), pnrInfo.getVersjonDato());
    }

    private void synkroniserNyEllerEksisterendeKode(Map<String, Postnummer> eksisterendeKoderMap, KodeverkKode masterKode) {
        if (eksisterendeKoderMap.containsKey(masterKode.getKode())) {
            synkroniserEksisterendeKode(masterKode, eksisterendeKoderMap.get(masterKode.getKode()));
        } else {
            synkroniserNyKode(masterKode);
        }
    }

    private void synkroniserNyKode(KodeverkKode kodeverkKode) {
        Postnummer nytt = new Postnummer(kodeverkKode.getKode(), kodeverkKode.getNavn(), kodeverkKode.getGyldigFom(), kodeverkKode.getGyldigTom());
        poststedKodeverkRepository.lagrePostnummer(nytt);
    }

    private void synkroniserEksisterendeKode(KodeverkKode kodeverkKode, Postnummer postnummer) {
        if (!erLike(kodeverkKode, postnummer)) {
            postnummer.setPoststednavn(kodeverkKode.getNavn());
            postnummer.setGyldigFom(kodeverkKode.getGyldigFom());
            postnummer.setGyldigTom(kodeverkKode.getGyldigTom());
            poststedKodeverkRepository.lagrePostnummer(postnummer);
        }
    }

    private static boolean erLike(KodeverkKode kodeverkKode, Postnummer postnummer) {
        return Objects.equals(kodeverkKode.getGyldigFom(), postnummer.getGyldigFom())
                && Objects.equals(kodeverkKode.getGyldigTom(), postnummer.getGyldigTom())
                && Objects.equals(kodeverkKode.getNavn(), postnummer.getPoststednavn());
    }

}

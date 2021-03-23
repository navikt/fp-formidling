package no.nav.foreldrepenger.melding.poststed;

import no.nav.foreldrepenger.melding.integrasjon.kodeverk.KodeverkConsumer;
import no.nav.tjeneste.virksomhet.kodeverk.v2.HentKodeverkHentKodeverkKodeverkIkkeFunnet;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.EnkeltKodeverk;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.IdentifiserbarEntitet;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.Kode;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.Kodeverkselement;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.Periode;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.Term;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.FinnKodeverkListeRequest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.FinnKodeverkListeResponse;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.HentKodeverkRequest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.HentKodeverkResponse;
import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.util.Tuple;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class KodeverkTjeneste {

    private KodeverkConsumer kodeverkConsumer;

    private static final String NORSK_BOKMÅL = "nb";

    KodeverkTjeneste() {
        // for CDI proxy
    }

    @Inject
    public KodeverkTjeneste(KodeverkConsumer kodeverkConsumer) {
        this.kodeverkConsumer = kodeverkConsumer;
    }

    public Optional<KodeverkInfo> hentGjeldendeKodeverk(String kodeverk) {
        FinnKodeverkListeRequest request = new FinnKodeverkListeRequest();

        FinnKodeverkListeResponse response = kodeverkConsumer.finnKodeverkListe(request);
        if (response != null) {
            return Optional.ofNullable(oversettFraKodeverkListe(response, kodeverk));
        }
        return Optional.empty();
    }

    private static KodeverkInfo oversettFraKodeverkListe(FinnKodeverkListeResponse response, String kodeverk) {
        return response.getKodeverkListe().stream()
                .filter(k -> kodeverk.equalsIgnoreCase(k.getNavn()))
                .map(k -> new KodeverkInfo.Builder()
                        .medNavn(k.getNavn())
                        .medVersjon(k.getVersjonsnummer())
                        .medVersjonDato(convertToLocalDate(k.getVersjoneringsdato()))
                        .build())
                .findFirst().orElse(null);
    }

    public Map<String, KodeverkKode> hentKodeverk(String kodeverkNavn, String kodeverkVersjon) {
        HentKodeverkRequest request = new HentKodeverkRequest();
        request.setNavn(kodeverkNavn);
        request.setVersjonsnummer(kodeverkVersjon);

        Map<String, KodeverkKode> kodeverkKodeMap = Collections.emptyMap();
        try {
            HentKodeverkResponse response = kodeverkConsumer.hentKodeverk(request);
            if (response != null) {
                kodeverkKodeMap = oversettFraHentKodeverkResponse(response);
            }
        } catch (HentKodeverkHentKodeverkKodeverkIkkeFunnet ex) {
            throw new IntegrasjonException("FP-868813", "Kodeverk ikke funnet", ex);
        }
        return kodeverkKodeMap;
    }

    private Map<String, KodeverkKode> oversettFraHentKodeverkResponse(HentKodeverkResponse response) {
        if (response.getKodeverk() instanceof EnkeltKodeverk) {
            return ((EnkeltKodeverk) response.getKodeverk()).getKode().stream()
                    .map(KodeverkTjeneste::oversettFraKode)
                    .collect(Collectors.toMap(KodeverkKode::getKode, kodeverkKode -> kodeverkKode));
        } else {
            throw new IntegrasjonException("FP-402870", String.format("Kodeverktype ikke støttet: %s", response.getKodeverk().getClass().getSimpleName()));
        }
    }

    private static KodeverkKode oversettFraKode(Kode kode) {
        Optional<Tuple<LocalDate, LocalDate>> gyldighetsperiode = finnGyldighetsperiode(kode.getGyldighetsperiode());
        Optional<String> term = finnTerm(kode.getTerm());
        return new KodeverkKode.Builder()
                .medKode(kode.getNavn())
                .medNavn(term.orElse(null))
                .medGyldigFom(gyldighetsperiode.map(Tuple::getElement1).orElse(null))
                .medGyldigTom(gyldighetsperiode.map(Tuple::getElement2).orElse(null))
                .build();
    }

    /**
     * Finner term navnet med nyeste gyldighetsdato og angitt språk (default norsk
     * bokmål).
     */
    private static Optional<String> finnTerm(List<Term> termList) {
        Comparator<Kodeverkselement> vedGyldigFom = (e1, e2) -> e1.getGyldighetsperiode().get(0).getFom()
                .compare(e2.getGyldighetsperiode().get(0).getFom());
        return termList.stream()
                .filter(term -> term.getSpraak().compareToIgnoreCase(NORSK_BOKMÅL) == 0)
                .max(vedGyldigFom)
                .map(IdentifiserbarEntitet::getNavn);
    }

    /**
     * Finner nyeste gyldighetsperiode ut fra fom dato.
     */
    private static Optional<Tuple<LocalDate, LocalDate>> finnGyldighetsperiode(List<Periode> periodeList) {
        Comparator<Periode> vedGyldigFom = (p1, p2) -> p1.getFom().compare(p2.getFom());
        Optional<Periode> periodeOptional = periodeList.stream().max(vedGyldigFom);
        return periodeOptional.map(periode -> new Tuple<>(convertToLocalDate(periode.getFom()),
                convertToLocalDate(periode.getTom())));
    }

    private static LocalDate convertToLocalDate(XMLGregorianCalendar xmlGregorianCalendar) {
        if (xmlGregorianCalendar == null) {
            return null;
        }
        return xmlGregorianCalendar.toGregorianCalendar().toZonedDateTime().toLocalDate();
    }
}

package no.nav.foreldrepenger.melding.poststed;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.datatype.XMLGregorianCalendar;

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
import no.nav.vedtak.util.Tuple;

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
            throw KodeverkFeil.FACTORY.hentKodeverkKodeverkIkkeFunnet(ex).toException();
        }
        return kodeverkKodeMap;
    }

    private Map<String, KodeverkKode> oversettFraHentKodeverkResponse(HentKodeverkResponse response) {
        String kodeverkNavn = response.getKodeverk().getNavn();
        if (response.getKodeverk() instanceof EnkeltKodeverk) {
            return ((EnkeltKodeverk) response.getKodeverk()).getKode().stream()
                    .map(KodeverkTjeneste::oversettFraKode)
                    .collect(Collectors.toMap(KodeverkKode::getKode, kodeverkKode -> kodeverkKode));
        } else {
            throw KodeverkFeil.FACTORY.hentKodeverkKodeverkTypeIkkeStøttet(response.getKodeverk().getClass().getSimpleName()).toException();
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
        String språk = NORSK_BOKMÅL;
        return termList.stream()
                .filter(term -> term.getSpraak().compareToIgnoreCase(språk) == 0)
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

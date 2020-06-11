package no.nav.foreldrepenger.tps;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.melding.aktør.AdresseType;
import no.nav.foreldrepenger.melding.aktør.Adresseinfo;
import no.nav.foreldrepenger.melding.aktør.PersonstatusType;
import no.nav.foreldrepenger.melding.geografisk.Poststed;
import no.nav.foreldrepenger.melding.geografisk.PoststedKodeverkRepository;
import no.nav.foreldrepenger.melding.repository.PersonInfoKodeverkRepository;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Bruker;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Gateadresse;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Kodeverdi;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Landkoder;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Matrikkeladresse;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.MidlertidigPostadresse;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.MidlertidigPostadresseNorge;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.MidlertidigPostadresseUtland;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Person;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Personstatus;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PostboksadresseNorsk;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.StedsadresseNorge;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.StrukturertAdresse;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.UstrukturertAdresse;
import no.nav.vedtak.exception.TekniskException;
import no.nav.vedtak.exception.VLException;

@ApplicationScoped
public class TpsAdresseOversetter {

    private static final String NORGE = "NOR";
    private static final Logger log = LoggerFactory.getLogger(TpsAdresseOversetter.class);
    private static final String HARDKODET_POSTNR = "XXXX";
    private static final String HARDKODET_POSTSTED = "UKJENT";
    private static final String POSTNUMMER_POSTSTED = "^\\d{4} \\D*";  // Mønster for postnummer og poststed, f.eks. "0034 OSLO"

    private PersonInfoKodeverkRepository personInfoKodeverkRepository;
    private PoststedKodeverkRepository poststedKodeverkRepository;

    TpsAdresseOversetter() {
        // for CDI proxy
    }

    @Inject
    public TpsAdresseOversetter(PersonInfoKodeverkRepository personInfoKodeverkRepository,
                                PoststedKodeverkRepository poststedKodeverkRepository) {
        this.personInfoKodeverkRepository = personInfoKodeverkRepository;
        this.poststedKodeverkRepository = poststedKodeverkRepository;
    }

    List<Adresseinfo> lagListeMedAdresseInfo(Bruker person) {
        Optional<AdresseType> gjeldende = finnGjeldendePostadressetype(person);
        if (gjeldende.isPresent() && Objects.equals(AdresseType.UKJENT_ADRESSE, gjeldende.get())) {
            return Collections.singletonList(byggUkjentAdresse(person));
        }

        List<Adresseinfo> adresseInfoList = new ArrayList<>();
        if (person.getBostedsadresse() != null) {
            StrukturertAdresse adresseStruk = person.getBostedsadresse().getStrukturertAdresse();
            adresseInfoList.add(konverterStrukturertAdresse(person, adresseStruk, AdresseType.BOSTEDSADRESSE));
        }
        if (person.getPostadresse() != null) {
            UstrukturertAdresse adresseUstruk = person.getPostadresse().getUstrukturertAdresse();
            adresseInfoList.add(konverterUstrukturertAdresse(person, adresseUstruk, AdresseType.POSTADRESSE));
            Landkoder landkode = adresseUstruk.getLandkode();
            if (NORGE.equals(landkode.getValue())) {
                adresseInfoList.add(konverterUstrukturertAdresse(person, adresseUstruk, AdresseType.POSTADRESSE));
            } else {
                adresseInfoList.add(konverterUstrukturertAdresse(person, adresseUstruk, AdresseType.POSTADRESSE_UTLAND));
            }
        }
        if (person.getMidlertidigPostadresse() != null) {
            if (person.getMidlertidigPostadresse() instanceof MidlertidigPostadresseNorge) {
                StrukturertAdresse adresseStruk = ((MidlertidigPostadresseNorge) person.getMidlertidigPostadresse()).getStrukturertAdresse();
                adresseInfoList.add(konverterStrukturertAdresse(person, adresseStruk, AdresseType.MIDLERTIDIG_POSTADRESSE_NORGE));
            } else if (person.getMidlertidigPostadresse() instanceof MidlertidigPostadresseUtland) {
                UstrukturertAdresse adresseUstruk = ((MidlertidigPostadresseUtland) person.getMidlertidigPostadresse()).getUstrukturertAdresse();
                adresseInfoList.add(konverterUstrukturertAdresse(person, adresseUstruk, AdresseType.MIDLERTIDIG_POSTADRESSE_UTLAND));
            }
        }
        return adresseInfoList;
    }

    private Adresseinfo finnGjeldendeAdresseFor(Bruker bruker) {
        Optional<AdresseType> gjeldende = finnGjeldendePostadressetype(bruker);
        if (gjeldende.isPresent()) {
            if (AdresseType.BOSTEDSADRESSE.equals(gjeldende.get())) {
                return konverterStrukturertAdresse(bruker, bruker.getBostedsadresse().getStrukturertAdresse(), gjeldende.get());
            } else if (AdresseType.POSTADRESSE.equals(gjeldende.get()) || AdresseType.POSTADRESSE_UTLAND.equals(gjeldende.get())) {
                return konverterUstrukturertAdresse(bruker, bruker.getPostadresse().getUstrukturertAdresse(), gjeldende.get());
            } else if (AdresseType.MIDLERTIDIG_POSTADRESSE_NORGE.equals(gjeldende.get())) {
                return konverterMidlertidigPostadresseNorge(bruker, gjeldende.get());
            } else if (AdresseType.MIDLERTIDIG_POSTADRESSE_UTLAND.equals(gjeldende.get())) {
                return konverterMidlertidigPostadresseUtland(bruker, gjeldende.get());
            } else if (AdresseType.UKJENT_ADRESSE.equals(gjeldende.get())) {
                return byggUkjentAdresse(bruker);
            }
        }
        throw TpsOversetterFeilmeldinger.FACTORY.ikkeGjenkjentAdresseType(TpsUtil.getPersonIdent(bruker).getIdent(),
                bruker.getGjeldendePostadressetype().getValue()).toException();
    }

    private Adresseinfo konverterMidlertidigPostadresseNorge(Bruker bruker,
                                                             AdresseType gjeldende) {
        StrukturertAdresse midlertidigAdresse = ((MidlertidigPostadresseNorge) bruker.getMidlertidigPostadresse()).getStrukturertAdresse();
        return konverterStrukturertAdresse(bruker, midlertidigAdresse, gjeldende);
    }

    private Adresseinfo konverterMidlertidigPostadresseUtland(Bruker bruker,
                                                              AdresseType gjeldende) {
        UstrukturertAdresse midlertidigAdresse = ((MidlertidigPostadresseUtland) bruker.getMidlertidigPostadresse()).getUstrukturertAdresse();
        return konverterUstrukturertAdresse(bruker, midlertidigAdresse, gjeldende);
    }

    Optional<AdresseType> finnGjeldendePostadressetype(Bruker bruker) {
        return poststedKodeverkRepository.finnAdresseType(bruker.getGjeldendePostadressetype().getValue());
    }

    Adresseinfo konverterStrukturertAdresse(Bruker bruker,
                                            StrukturertAdresse adresse,
                                            AdresseType adresseType) {
        requireNonNull(adresse);
        if (adresse instanceof Gateadresse) {
            return konverterStrukturertAdresse(bruker, adresseType, (Gateadresse) adresse);
        } else if (adresse instanceof Matrikkeladresse) {
            return konverterStrukturertAdresse(bruker, adresseType, (Matrikkeladresse) adresse);
        } else if (adresse instanceof PostboksadresseNorsk) {
            return konverterStrukturertAdresse(bruker, adresseType, (PostboksadresseNorsk) adresse);
        } else if (adresse instanceof StedsadresseNorge) {
            return konverterStrukturertAdresse(bruker, adresseType, (StedsadresseNorge) adresse);
        } else {
            throw new IllegalArgumentException("Ikke-støttet klasse for strukturert adresse: " + adresse.getClass());
        }
    }

    private Adresseinfo konverterStrukturertAdresse(Bruker bruker,
                                                    AdresseType gjeldende,
                                                    Gateadresse gateadresse) {

        Adresse adresse = konverterStrukturertAdresse(gateadresse);
        return byggAddresseinfo(bruker, gjeldende, adresse);
    }

    private String adresseFraGateadresse(Gateadresse gateadresse) {
        return gateadresse.getGatenavn() +
                hvisfinnes(gateadresse.getHusnummer()) +
                hvisfinnes(gateadresse.getHusbokstav());
    }

    Adresseinfo byggUkjentAdresse(Bruker bruker) {
        return new Adresseinfo.Builder(AdresseType.UKJENT_ADRESSE,
                TpsUtil.getPersonIdent(bruker),
                bruker.getPersonnavn().getSammensattNavn(),
                tilPersonstatusType(bruker.getPersonstatus())).build();
    }

    private Adresseinfo konverterStrukturertAdresse(Bruker bruker,
                                                    AdresseType gjeldende,
                                                    StedsadresseNorge stedsadresseNorge) {

        Adresse adresse = konverterStrukturertAdresse(stedsadresseNorge);
        return byggAddresseinfo(bruker, gjeldende, adresse);
    }

    private Adresseinfo konverterStrukturertAdresse(Bruker bruker,
                                                    AdresseType gjeldende,
                                                    PostboksadresseNorsk postboksadresseNorsk) {
        Adresse adresse = konverterStrukturertAdresse(postboksadresseNorsk);
        return byggAddresseinfo(bruker, gjeldende, adresse);
    }

    private Adresseinfo.Builder adresseBuilderForPerson(Bruker bruker,
                                                        AdresseType gjeldende) {
        Personstatus personstatus = bruker.getPersonstatus();
        return new Adresseinfo.Builder(gjeldende,
                TpsUtil.getPersonIdent(bruker),
                TpsUtil.getPersonnavn(bruker),
                personstatus == null ? null : tilPersonstatusType(personstatus));
    }

    private String postboksadresselinje(PostboksadresseNorsk postboksadresseNorsk) {
        return "Postboks" + hvisfinnes(postboksadresseNorsk.getPostboksnummer()) +
                hvisfinnes(postboksadresseNorsk.getPostboksanlegg());
    }

    Adresseinfo konverterUstrukturertAdresse(Bruker bruker,
                                             UstrukturertAdresse ustrukturertAdresse,
                                             AdresseType gjeldende) {

        Adresse adresse = konverterUstrukturertAdresse(ustrukturertAdresse);
        return byggAddresseinfo(bruker, gjeldende, adresse);
    }

    private Adresseinfo byggAddresseinfo(Bruker bruker, AdresseType gjeldende, Adresse adresse) {
        return adresseBuilderForPerson(bruker, gjeldende)
                .medPostNr(adresse.postnummer)
                .medPoststed(adresse.poststed)
                .medLand(adresse.land)
                .medAdresselinje1(adresse.adresselinje1)
                .medAdresselinje2(adresse.adresselinje2)
                .medAdresselinje3(adresse.adresselinje3)
                .medAdresselinje4(adresse.adresselinje4)
                .build();
    }

    private Adresse konverterStrukturertAdresse(Gateadresse gateadresse) {

        String postnummer = Optional.ofNullable(gateadresse.getPoststed()).map(Kodeverdi::getValue).orElse(HARDKODET_POSTNR);

        Adresse adresse = new Adresse();
        adresse.postnummer = postnummer;
        adresse.poststed = tilPoststed(postnummer);
        adresse.land = tilLand(gateadresse.getLandkode());

        if (gateadresse.getTilleggsadresse() == null) {
            adresse.adresselinje1 = adresseFraGateadresse(gateadresse);
        } else {
            adresse.adresselinje1 = gateadresse.getTilleggsadresse();
            adresse.adresselinje2 = adresseFraGateadresse(gateadresse);
        }
        return adresse;
    }

    private Adresse konverterStrukturertAdresse(PostboksadresseNorsk postboksadresseNorsk) {
        Adresse adresse = new Adresse();
        adresse.postnummer = postboksadresseNorsk.getPoststed().getValue();
        adresse.poststed = tilPoststed(adresse.postnummer);
        adresse.land = tilLand(postboksadresseNorsk.getLandkode());

        if (postboksadresseNorsk.getTilleggsadresse() == null) {
            adresse.adresselinje1 = postboksadresselinje(postboksadresseNorsk);
        } else {
            adresse.adresselinje1 = postboksadresseNorsk.getTilleggsadresse();
            adresse.adresselinje2 = postboksadresselinje(postboksadresseNorsk);
        }

        return adresse;
    }

    private Adresse konverterStrukturertAdresse(StedsadresseNorge stedsadresseNorge) {

        Adresse adresse = new Adresse();
        adresse.postnummer = stedsadresseNorge.getPoststed().getValue();
        adresse.poststed = tilPoststed(adresse.postnummer);
        adresse.land = tilLand(stedsadresseNorge.getLandkode());
        adresse.adresselinje1 = stedsadresseNorge.getBolignummer();
        adresse.adresselinje2 = stedsadresseNorge.getTilleggsadresse();

        return adresse;
    }

    private Adresse konverterUstrukturertAdresse(UstrukturertAdresse ustrukturertAdresse) {
        Adresse adresse = new Adresse();
        adresse.adresselinje1 = ustrukturertAdresse.getAdresselinje1();
        adresse.adresselinje2 = ustrukturertAdresse.getAdresselinje2();
        adresse.adresselinje3 = ustrukturertAdresse.getAdresselinje3();
        adresse.land = tilLand(ustrukturertAdresse.getLandkode());


        String linje4 = ustrukturertAdresse.getAdresselinje4();
        // Ustrukturert adresse kan ha postnr + poststed i adresselinje4
        if (linje4 != null && linje4.matches(POSTNUMMER_POSTSTED)) {
            adresse.postnummer = linje4.substring(0, 4);
            adresse.poststed = linje4.substring(5);
        } else {
            adresse.adresselinje4 = linje4;
        }
        return adresse;
    }

    String finnAdresseLandkodeFor(Bruker bruker) {
        try {
            Adresseinfo adresseinfo = tilAdresseInfo(bruker);
            return adresseinfo.getLand();
        } catch (VLException e) {
            TpsOversetterFeilmeldinger.FACTORY.manglerLandBrukerNorge(e).log(log);
            return NORGE;
        }
    }

    public String finnUtlandsadresseFor(Bruker bruker) {
        MidlertidigPostadresse midlertidigPostadresse = bruker.getMidlertidigPostadresse();
        if (midlertidigPostadresse instanceof MidlertidigPostadresseUtland) {
            MidlertidigPostadresseUtland postadresseUtland = (MidlertidigPostadresseUtland) midlertidigPostadresse;
            return byggOppAdresse(konverterUstrukturertAdresse(bruker,
                    postadresseUtland.getUstrukturertAdresse(),
                    AdresseType.MIDLERTIDIG_POSTADRESSE_UTLAND));
        }
        return null;
    }

    String finnAdresseFor(Person person) {
        if (person instanceof Bruker) {
            try {
                Adresseinfo adresseinfo = tilAdresseInfo(person);
                return byggOppAdresse(adresseinfo);
            } catch (TekniskException tps) { //NOSONAR
                //Ukjent adresse eller adresse som ikke kan oversettes.
            }
        }
        return "UKJENT ADRESSE";
    }

    private String byggOppAdresse(Adresseinfo adresseinfo) {
        String linje1 = adresseinfo.getAdresselinje1();
        String linje2 = Optional.ofNullable(adresseinfo.getAdresselinje2()).map(linje -> "\n" + linje).orElse("");
        String linje3 = Optional.ofNullable(adresseinfo.getAdresselinje3()).map(linje -> "\n" + linje).orElse("");
        String linje4 = Optional.ofNullable(adresseinfo.getAdresselinje4()).map(linje -> "\n" + linje).orElse("");
        String postnr = Optional.ofNullable(adresseinfo.getPostNr()).map(nr -> "\n" + nr).orElse("");
        String poststed = Optional.ofNullable(adresseinfo.getPoststed()).map(sted -> " " + sted).orElse("");
        String land = Optional.ofNullable(adresseinfo.getLand()).map(landKode -> "\n" + landKode).orElse("");
        return linje1 + linje2 + linje3 + linje4 + postnr + poststed + land;
    }

    //1
    Adresseinfo tilAdresseInfo(Person person) {
        if (person instanceof Bruker) {
            return finnGjeldendeAdresseFor((Bruker) person);
        }
        throw TpsOversetterFeilmeldinger.FACTORY.ukjentBrukerTypeFinnerIkkeAdresse(TpsUtil.getFnr(person)).toException();
    }

    private String tilPoststed(String postnummer) {
        if (HARDKODET_POSTNR.equals(postnummer)) {
            return HARDKODET_POSTSTED;
        }
        Optional<Poststed> poststed = poststedKodeverkRepository.finnPoststed(postnummer);
        if (poststed.isPresent()) {
            return poststed.get().getNavn();
        }
        return HARDKODET_POSTSTED;
    }

    private String tilLand(Landkoder landkoder) {
        return null == landkoder ? null : landkoder.getValue();
    }

    private PersonstatusType tilPersonstatusType(Personstatus personstatus) {
        return personInfoKodeverkRepository.finnPersonstatus(personstatus.getPersonstatus().getValue());
    }

    private String hvisfinnes(Object object) {
        return object == null ? "" : " " + object.toString().trim();
    }

    private class Adresse {
        String adresselinje1;
        String adresselinje2;
        String adresselinje3;
        String adresselinje4;
        String postnummer;
        String poststed;
        String land;
    }
}

package no.nav.foreldrepenger.tps.impl;

import static java.util.stream.Collectors.toSet;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.melding.aktør.Adresseinfo;
import no.nav.foreldrepenger.melding.aktør.Familierelasjon;
import no.nav.foreldrepenger.melding.aktør.NavBrukerKjønn;
import no.nav.foreldrepenger.melding.aktør.Personinfo;
import no.nav.foreldrepenger.melding.aktør.PersonstatusType;
import no.nav.foreldrepenger.melding.behandling.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.behandling.personopplysning.SivilstandType;
import no.nav.foreldrepenger.melding.behandling.repository.PersonInfoKodeverkRepository;
import no.nav.foreldrepenger.melding.geografisk.Landkoder;
import no.nav.foreldrepenger.melding.geografisk.Region;
import no.nav.foreldrepenger.melding.geografisk.SpråkKodeverkRepository;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.typer.AktørId;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Aktoer;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Bruker;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Doedsdato;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Foedselsdato;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Kjoenn;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Person;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Personstatus;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Spraak;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Statsborgerskap;
import no.nav.vedtak.felles.integrasjon.felles.ws.DateUtil;
import no.nav.vedtak.log.util.LoggerUtils;

@ApplicationScoped
public class TpsOversetter {
    private static final Logger log = LoggerFactory.getLogger(TpsOversetter.class);

    private PersonInfoKodeverkRepository personInfoKodeverkRepository;
    private SpråkKodeverkRepository språkKodeverkRepository;
    private TpsAdresseOversetter tpsAdresseOversetter;

    TpsOversetter() {
        // for CDI proxy
    }

    @Inject
    public TpsOversetter(PersonInfoKodeverkRepository personInfoKodeverkRepository,
                         SpråkKodeverkRepository språkKodeverkRepository,
                         TpsAdresseOversetter tpsAdresseOversetter) {
        this.personInfoKodeverkRepository = personInfoKodeverkRepository;
        this.språkKodeverkRepository = språkKodeverkRepository;
        this.tpsAdresseOversetter = tpsAdresseOversetter;
    }

    public Adresseinfo tilAdresseInfo(Person person) {
        return tpsAdresseOversetter.tilAdresseInfo(person);
    }

    public Personinfo tilBrukerInfo(AktørId aktørId, Bruker bruker) { // NOSONAR - ingen forbedring å forkorte metoden her
        String navn = bruker.getPersonnavn().getSammensattNavn();
        String adresse = tpsAdresseOversetter.finnAdresseFor(bruker);
        String adresseLandkode = tpsAdresseOversetter.finnAdresseLandkodeFor(bruker);
        String utlandsadresse = tpsAdresseOversetter.finnUtlandsadresseFor(bruker);

        LocalDate fødselsdato = finnFødselsdato(bruker);
        LocalDate dødsdato = finnDødsdato(bruker);

        Aktoer aktoer = bruker.getAktoer();
        PersonIdent pi = (PersonIdent) aktoer;
        String ident = pi.getIdent().getIdent();
        NavBrukerKjønn kjønn = tilBrukerKjønn(bruker.getKjoenn());
        PersonstatusType personstatus = tilPersonstatusType(bruker.getPersonstatus());
        Set<Familierelasjon> familierelasjoner = bruker.getHarFraRolleI().stream()
                .map(this::tilRelasjon)
                .collect(toSet());

        Landkoder landkoder = utledLandkode(bruker.getStatsborgerskap());
        Region region = personInfoKodeverkRepository.finnHøyestRangertRegion(Collections.singletonList(landkoder.getKode()));

        String diskresjonskode = bruker.getDiskresjonskode() == null ? null : bruker.getDiskresjonskode().getValue();
        String geografiskTilknytning = bruker.getGeografiskTilknytning() != null ? bruker.getGeografiskTilknytning().getGeografiskTilknytning() : null;

        List<Adresseinfo> adresseinfoList = tpsAdresseOversetter.lagListeMedAdresseInfo(bruker);
        SivilstandType sivilstandType = bruker.getSivilstand() == null ? null : personInfoKodeverkRepository.finnSivilstandType(bruker.getSivilstand().getSivilstand().getValue());

        return new Personinfo.Builder()
                .medAktørId(aktørId)
                .medPersonIdent(no.nav.foreldrepenger.melding.typer.PersonIdent.fra(ident))
                .medNavn(navn)
                .medAdresse(adresse)
                .medAdresseLandkode(adresseLandkode)
                .medFødselsdato(fødselsdato)
                .medDødsdato(dødsdato)
                .medNavBrukerKjønn(kjønn)
                .medPersonstatusType(personstatus)
                .medStatsborgerskap(new no.nav.foreldrepenger.melding.aktør.Statsborgerskap(landkoder.getKode()))
                .medRegion(region)
                .medFamilierelasjon(familierelasjoner)
                .medUtlandsadresse(utlandsadresse)
                .medForetrukketSpråk(bestemForetrukketSpråk(bruker))
                .medGegrafiskTilknytning(geografiskTilknytning)
                .medDiskresjonsKode(diskresjonskode)
                .medAdresseInfoList(adresseinfoList)
                .medSivilstandType(sivilstandType)
                .medLandkode(landkoder)
                .build();
    }

    private Landkoder utledLandkode(Statsborgerskap statsborgerskap) {
        Landkoder landkode = Landkoder.UDEFINERT;
        if (Optional.ofNullable(statsborgerskap).isPresent()) {
            landkode = personInfoKodeverkRepository.finnLandkode(statsborgerskap.getLand().getValue());
        }
        return landkode;
    }

    private LocalDate finnDødsdato(Bruker person) {
        LocalDate dødsdato = null;
        Doedsdato dødsdatoJaxb = person.getDoedsdato();
        if (dødsdatoJaxb != null) {
            dødsdato = DateUtil.convertToLocalDate(dødsdatoJaxb.getDoedsdato());
        }
        return dødsdato;
    }

    private LocalDate finnFødselsdato(Bruker person) {
        LocalDate fødselsdato = null;
        Foedselsdato fødselsdatoJaxb = person.getFoedselsdato();
        if (fødselsdatoJaxb != null) {
            fødselsdato = DateUtil.convertToLocalDate(fødselsdatoJaxb.getFoedselsdato());
        }
        return fødselsdato;
    }

    private Språkkode bestemForetrukketSpråk(Bruker person) {
        Språkkode defaultSpråk = Språkkode.nb;
        Spraak språk = person.getMaalform();
        // For å slippe å håndtere foreldet forkortelse "NO" andre steder i løsningen
        if (språk == null || "NO".equals(språk.getValue())) {
            return defaultSpråk;
        }
        Optional<Språkkode> kode = språkKodeverkRepository.finnSpråkMedKodeverkEiersKode(språk.getValue());
        if (kode.isPresent()) {
            return kode.get();
        }
        if (log.isInfoEnabled()) {
            log.info("Mottok ukjent språkkode: '{}'. Defaulter til '{}'", LoggerUtils.removeLineBreaks(språk.getValue()), defaultSpråk.getKode()); //NOSONAR
        }
        return defaultSpråk;
    }

    private Familierelasjon tilRelasjon(no.nav.tjeneste.virksomhet.person.v3.informasjon.Familierelasjon familierelasjon) {
        String rollekode = familierelasjon.getTilRolle().getValue();
        RelasjonsRolleType relasjonsrolle = new RelasjonsRolleType(rollekode);
        String adresse = tpsAdresseOversetter.finnAdresseFor(familierelasjon.getTilPerson());
        PersonIdent personIdent = (PersonIdent) familierelasjon.getTilPerson().getAktoer();
        no.nav.foreldrepenger.melding.typer.PersonIdent ident = no.nav.foreldrepenger.melding.typer.PersonIdent.fra(personIdent.getIdent().getIdent());
        Boolean harSammeBosted = familierelasjon.isHarSammeBosted();

        return new Familierelasjon(ident, relasjonsrolle,
                tilLocalDate(familierelasjon.getTilPerson().getFoedselsdato()), adresse, harSammeBosted);
    }

    private NavBrukerKjønn tilBrukerKjønn(Kjoenn kjoenn) {
        return Optional.ofNullable(kjoenn)
                .map(Kjoenn::getKjoenn)
                .map(kj -> personInfoKodeverkRepository.finnBrukerKjønn(kj.getValue()))
                .orElse(NavBrukerKjønn.UDEFINERT);
    }

    private PersonstatusType tilPersonstatusType(Personstatus personstatus) {
        return personInfoKodeverkRepository.finnPersonstatus(personstatus.getPersonstatus().getValue());
    }

    private LocalDate tilLocalDate(Foedselsdato fødselsdatoJaxb) {
        if (fødselsdatoJaxb != null) {
            return DateUtil.convertToLocalDate(fødselsdatoJaxb.getFoedselsdato());
        }
        return null;
    }
}

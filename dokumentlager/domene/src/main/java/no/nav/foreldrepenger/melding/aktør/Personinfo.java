package no.nav.foreldrepenger.melding.aktør;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import no.nav.foreldrepenger.melding.geografisk.Landkoder;
import no.nav.foreldrepenger.melding.geografisk.Region;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.personopplysning.SivilstandType;
import no.nav.foreldrepenger.melding.typer.AktørId;
import no.nav.foreldrepenger.melding.typer.PersonIdent;

public class Personinfo {
    private AktørId aktørId;
    private String adresse;
    private LocalDate fødselsdato;
    private LocalDate dødsdato;
    private Set<Familierelasjon> familierelasjoner = Collections.emptySet();
    private Statsborgerskap statsborgerskap;
    private Region region;
    private String utlandsadresse;
    private String geografiskTilknytning;
    private String diskresjonskode;
    private Språkkode foretrukketSpråk;
    private String adresseLandkode;
    private Landkoder landkode;
    private List<Adresseinfo> adresseInfoList = new ArrayList<>();
    private SivilstandType sivilstand;

    //Brukt for mapping
    private String navn;
    private PersonstatusType personstatus;
    private String PersonstatusType; //Kodeliste.PersonstatusType
    private PersonIdent personIdent;
    private NavBrukerKjønn kjønn;

    private Personinfo() {
    }

    public AktørId getAktørId() {
        return aktørId;
    }

    public PersonIdent getPersonIdent() {
        return personIdent;
    }

    public String getNavn() {
        return navn;
    }

    public NavBrukerKjønn getKjønn() {
        return kjønn;
    }

    public PersonstatusType getPersonstatus() {
        return personstatus;
    }

    public LocalDate getFødselsdato() {
        return fødselsdato;
    }

    public int getAlder() {
        return (int) ChronoUnit.YEARS.between(fødselsdato, LocalDate.now());
    }

    public Set<Familierelasjon> getFamilierelasjoner() {
        return Collections.unmodifiableSet(familierelasjoner);
    }

    public boolean erKvinne() {
        return kjønn.equals(NavBrukerKjønn.KVINNE);
    }

    public String getAdresse() {
        return adresse;
    }

    public LocalDate getDødsdato() {
        return dødsdato;
    }

    public Statsborgerskap getStatsborgerskap() {
        return statsborgerskap;
    }

    public String getUtlandsadresse() {
        return utlandsadresse;
    }

    public Region getRegion() {
        return region;
    }

    public String getAdresseLandkode() {
        return adresseLandkode;
    }

    public Språkkode getForetrukketSpråk() {
        return foretrukketSpråk;
    }

    public String getGeografiskTilknytning() {
        return geografiskTilknytning;
    }

    public String getDiskresjonskode() {
        return diskresjonskode;
    }

    public List<Adresseinfo> getAdresseInfoList() {
        return adresseInfoList;
    }

    public SivilstandType getSivilstandType() {
        return sivilstand;
    }

    public Landkoder getLandkode() {
        return landkode;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "<aktørId=" + aktørId + ">"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    public static class Builder {
        private Personinfo personinfoMal;

        public Builder() {
            personinfoMal = new Personinfo();
        }

        public Builder medAktørId(AktørId aktørId) {
            personinfoMal.aktørId = aktørId;
            return this;
        }

        public Builder medNavn(String navn) {
            personinfoMal.navn = navn;
            return this;
        }

        /**
         * @deprecated Bruk {@link #medPersonIdent(PersonIdent)} i stedet!
         */
        @Deprecated
        public Builder medFnr(String fnr) {
            personinfoMal.personIdent = PersonIdent.fra(fnr);
            return this;
        }

        public Builder medPersonIdent(PersonIdent fnr) {
            personinfoMal.personIdent = fnr;
            return this;
        }

        public Builder medAdresse(String adresse) {
            personinfoMal.adresse = adresse;
            return this;
        }

        public Builder medFødselsdato(LocalDate fødselsdato) {
            personinfoMal.fødselsdato = fødselsdato;
            return this;
        }

        public Builder medDødsdato(LocalDate dødsdato) {
            personinfoMal.dødsdato = dødsdato;
            return this;
        }

        public Builder medPersonstatusType(PersonstatusType personstatus) {
            personinfoMal.personstatus = personstatus;
            return this;
        }

        public Builder medNavBrukerKjønn(NavBrukerKjønn kjønn) {
            personinfoMal.kjønn = kjønn;
            return this;
        }

        public Builder medFamilierelasjon(Set<Familierelasjon> familierelasjon) {
            personinfoMal.familierelasjoner = familierelasjon;
            return this;
        }

        public Builder medStatsborgerskap(Statsborgerskap statsborgerskap) {
            personinfoMal.statsborgerskap = statsborgerskap;
            return this;
        }

        public Builder medRegion(Region region) {
            personinfoMal.region = region;
            return this;
        }

        public Builder medUtlandsadresse(String utlandsadresse) {
            personinfoMal.utlandsadresse = utlandsadresse;
            return this;
        }

        public Builder medGegrafiskTilknytning(String geoTilkn) {
            personinfoMal.geografiskTilknytning = geoTilkn;
            return this;
        }

        public Builder medDiskresjonsKode(String diskresjonsKode) {
            personinfoMal.diskresjonskode = diskresjonsKode;
            return this;
        }

        public Builder medForetrukketSpråk(Språkkode språk) {
            personinfoMal.foretrukketSpråk = språk;
            return this;
        }

        public Builder medAdresseLandkode(String adresseLandkode) {
            personinfoMal.adresseLandkode = adresseLandkode;
            return this;
        }

        public Builder medAdresseInfoList(List<Adresseinfo> adresseinfoArrayList) {
            personinfoMal.adresseInfoList = adresseinfoArrayList;
            return this;
        }

        public Builder medSivilstandType(SivilstandType sivilstandType) {
            personinfoMal.sivilstand = sivilstandType;
            return this;
        }

        public Builder medLandkode(Landkoder landkode) {
            personinfoMal.landkode = landkode;
            return this;
        }

        public Personinfo build() {
            requireNonNull(personinfoMal.aktørId, "Navbruker må ha aktørId"); //$NON-NLS-1$
            requireNonNull(personinfoMal.personIdent, "Navbruker må ha fødselsnummer"); //$NON-NLS-1$
            requireNonNull(personinfoMal.navn, "Navbruker må ha navn"); //$NON-NLS-1$
            requireNonNull(personinfoMal.fødselsdato, "Navbruker må ha fødselsdato"); //$NON-NLS-1$
            requireNonNull(personinfoMal.kjønn, "Navbruker må ha kjønn"); //$NON-NLS-1$
            return personinfoMal;
        }

    }

}

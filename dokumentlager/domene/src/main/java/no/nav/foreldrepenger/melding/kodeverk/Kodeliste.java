package no.nav.foreldrepenger.melding.kodeverk;


import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.melding.kodeverk.diff.JsonObjectMapper;

/**
 * Et innslag i en liste av koder tilgjengelig for et Kodeverk.
 * Koder kan legges til og oppdateres, men tracker ikke endringer over tid (kun av om de er tilgjengelig).
 * <p>
 * Koder skal ikke gjenbrukes, i tråd med anbefalinger fra Kodeverkforvaltningen.Derfor vil kun en
 * gyldighetsperiode vedlikeholdes per kode.
 */
@MappedSuperclass
@Table(name = "KODELISTE")
@DiscriminatorColumn(name = "kodeverk")
public abstract class Kodeliste extends KodeverkBaseEntitet implements Comparable<Kodeliste> {
    public static final Comparator<Kodeliste> NULLSAFE_KODELISTE_COMPARATOR = Comparator.nullsFirst(Kodeliste::compareTo);
    private static final Logger LOG = LoggerFactory.getLogger(Kodeliste.class);

    @Id
    @Column(name = "kodeverk", nullable = false)
    private String kodeverk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kodeverk", referencedColumnName = "kode", insertable = false, updatable = false, nullable = false)
    private Kodeverk kodeverkEntitet;

    @Id
    @Column(name = "kode", nullable = false, updatable = false, insertable = false)
    private String kode;

    /**
     * Konverter til kodeverdienum hvis du har bruk for denne
     */
    @Deprecated
    @Column(name = "offisiell_kode", updatable = false, insertable = false)
    private String offisiellKode;

    @Column(name = "beskrivelse", updatable = false, insertable = false)
    private String beskrivelse;

    @Column(name = "navn", updatable = false, insertable = false)
    private String navn;

    /**
     * ISO-639-1-alpha-2 lower-case språk.
     */
    @Column(name = "sprak", nullable = false, updatable = false, insertable = false)
    private String språk = "nb";

    /**
     * Når koden gjelder fra og med.
     */
    @Column(name = "gyldig_fom", nullable = false, updatable = false, insertable = false)
    private LocalDate gyldigFraOgMed = LocalDate.of(2000, 01, 01); // NOSONAR

    /**
     * Når koden gjelder til og med.
     */
    @Column(name = "gyldig_tom", nullable = false, updatable = false, insertable = false)
    private LocalDate gyldigTilOgMed = LocalDate.of(9999, 12, 31); // NOSONAR

    /**
     * Denne skal kun inneholde JSON data. Struktur på Json er opp til konkret subklasse å tolke (bruk {@link #getJsonField(String)}
     */
    @Column(name = "ekstra_data", updatable = false, insertable = false)
    private String ekstraData;

    /**
     * Skal ikke leses fra databasen, kun slås opp.
     */
    @Transient
    private String displayNavn;

    protected Kodeliste() {
        // proxy for hibernate
    }

    public Kodeliste(String kode, String kodeverk) {
        Objects.requireNonNull(kode, "kode"); //$NON-NLS-1$
        Objects.requireNonNull(kodeverk, "kodeverk"); //$NON-NLS-1$
        this.kode = kode;
        this.kodeverk = kodeverk;
    }

    public Kodeliste(String kode, String kodeverk, String offisiellKode, String navn, LocalDate fom, LocalDate tom) {
        this(kode, kodeverk);
        this.offisiellKode = offisiellKode;
        this.navn = navn;
        this.gyldigFraOgMed = fom;
        this.gyldigTilOgMed = tom;
    }

    public static List<String> kodeVerdier(Kodeliste... entries) {
        return kodeVerdier(Arrays.asList(entries));
    }

    public static List<String> kodeVerdier(Collection<? extends Kodeliste> entries) {
        return entries.stream().map(k -> k.getKode()).collect(Collectors.toList());
    }

    public String getBeskrivelse() {
        throw new UnsupportedOperationException("Utvikler-feil konverter til kodeliste");
    }

    public String getKode() {
        return kode;
    }

    public String getOffisiellKode() {
        throw new UnsupportedOperationException("Utvikler-feil konverter til kodeliste");
    }

    public String getNavn() {
        return navn;
    }

    /**
     * Returnerer språk i standard ISO 639-1-alpha2 lower case.
     */
    public String getSpråk() {
        return språk;
    }

    public LocalDate getGyldigFraOgMed() {
        throw new UnsupportedOperationException("Utvikler-feil konverter til kodeliste");
    }

    public LocalDate getGyldigTilOgMed() {
        throw new UnsupportedOperationException("Utvikler-feil konverter til kodeliste");
    }

    protected String getEkstraData() {
        return ekstraData;
    }

    protected String getJsonField(String... keys) {
        if (getEkstraData() == null) {
            return null;
        }
        JsonObjectMapper jsonObjectMapper = new JsonObjectMapper();
        try {
            return jsonObjectMapper.readKey(getEkstraData(), keys);
        } catch (IOException e) {
            StringBuilder allkeys = new StringBuilder();
            try {
                for (String key : keys) {
                    allkeys.append(key);
                    allkeys.append(':');
                }
            } catch (Exception stringException) { //$NON-NLS-1$ //NOSONAR
                allkeys.append("Klarer ikke å hente nøklene som feiler."); //$NON-NLS-1$ //NOSONAR
            }
            throw new IllegalStateException("Ugyldig format (forventet JSON) for kodeverk=" + getKodeverk() + ", kode=" + getKode() //$NON-NLS-1$ //$NON-NLS-2$
                    + ", jsonKey=" + allkeys.toString() + " " + getEkstraData(), e); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Kodeliste)) {
            return false;
        }
        Kodeliste other = (Kodeliste) obj;
        return Objects.equals(getKode(), other.getKode())
                && Objects.equals(getKodeverk(), other.getKodeverk());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKode(), getKodeverk());
    }

    @Override
    public int compareTo(Kodeliste that) {
        return that.getKode().compareTo(this.getKode());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "<" //$NON-NLS-1$
                + "kode=" + getKode() //$NON-NLS-1$
                + ", kodeverk=" + getKodeverk() //$NON-NLS-1$
                + ", språk=" + språk //$NON-NLS-1$
                + ", offisiellKode=" + offisiellKode //$NON-NLS-1$
                + ", gyldigFom=" + gyldigFraOgMed //$NON-NLS-1$
                + ", gyldigTom=" + gyldigTilOgMed //$NON-NLS-1$
                + ">"; //$NON-NLS-1$
    }

    public String getKodeverk() {
        if (kodeverk == null) {
            DiscriminatorValue dc = getClass().getDeclaredAnnotation(DiscriminatorValue.class);
            if (dc != null) {
                kodeverk = dc.value();
            }
        }
        return kodeverk;
    }
}

package no.nav.foreldrepenger.melding.kodeverk;


import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    private static final String I18N_MELDINGER_KEY = "i18n.Meldinger"; //$NON-NLS-1$
    /**
     * Default fil er samme som property key navn.
     */
    private static final String I18N_MELDINGER = System.getProperty(I18N_MELDINGER_KEY, I18N_MELDINGER_KEY);
    private static final String I18N_KEYFORMAT = "Kodeverk.%s.%s";//$NON-NLS-1$
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(I18N_MELDINGER); // $NON-NLS-1$
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
     * Kode bestemt av kodeeier. Kan avvike fra intern kodebruk
     */
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

    public static String getI18nMeldingerKey() {
        return I18N_MELDINGER_KEY;
    }

    public static List<String> kodeVerdier(Kodeliste... entries) {
        return kodeVerdier(Arrays.asList(entries));
    }

    public static List<String> kodeVerdier(Collection<? extends Kodeliste> entries) {
        return entries.stream().map(k -> k.getKode()).collect(Collectors.toList());
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public String getKode() {
        return kode;
    }

    public String getOffisiellKode() {
        return offisiellKode;
    }

    public boolean erLikOffisiellKode(String annenOffisiellKode) {
        if (offisiellKode == null) {
            throw new IllegalArgumentException("Har ikke offisiellkode for, Kodeverk=" + getKodeverk() + ", kode=" + getKode()); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return offisiellKode.equals(annenOffisiellKode);
    }

    /**
     * Returnerer språk i standard ISO 639-1-alpha2 lower case.
     */
    public String getSpråk() {
        return språk;
    }

    public String getNavn() {
        if (displayNavn == null) {
            String key = String.format(I18N_KEYFORMAT, getClass().getSimpleName(), getKode());
            if (BUNDLE.containsKey(key)) {
                this.displayNavn = BUNDLE.getString(key);
            } else {
                // FIXME (FC): må her bytte ut med brukers lang fra HTTP Accept-Language header når får på plass full
                // i18n
                this.displayNavn = navn;
            }
        }
        return displayNavn;
    }

    public LocalDate getGyldigFraOgMed() {
        return gyldigFraOgMed;
    }

    public LocalDate getGyldigTilOgMed() {
        return gyldigTilOgMed;
    }

    protected String getEkstraData() {
        return ekstraData;
    }

    protected String getJsonField(String key) {
        if (getEkstraData() == null) {
            return null;
        }
        ObjectMapper om = new ObjectMapper();
        try {
            JsonNode jsonNode = om.readTree(getEkstraData()).get(key);
            return jsonNode == null ? null : jsonNode.asText();
        } catch (IOException e) {
            throw new IllegalStateException("Ugyldig format (forventet JSON) for kodeverk=" + getKodeverk() + ", kode=" + getKode() //$NON-NLS-1$ //$NON-NLS-2$
                    + ", jsonKey=" + key + ": " + getEkstraData(), e); //$NON-NLS-1$ //$NON-NLS-2$
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

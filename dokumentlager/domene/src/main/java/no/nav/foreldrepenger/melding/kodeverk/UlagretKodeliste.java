package no.nav.foreldrepenger.melding.kodeverk;


import java.util.Objects;

public abstract class UlagretKodeliste implements Comparable<UlagretKodeliste> {

    private String kodeverk;
    private String kode;

    public UlagretKodeliste(String kode, String kodeverk) {
        Objects.requireNonNull(kode, "kode"); //$NON-NLS-1$
        Objects.requireNonNull(kodeverk, "kodeverk"); //$NON-NLS-1$
        this.kode = kode;
        this.kodeverk = kodeverk;
    }

    public String getKode() {
        return kode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof UlagretKodeliste)) {
            return false;
        }
        UlagretKodeliste other = (UlagretKodeliste) obj;
        return Objects.equals(getKode(), other.getKode())
                && Objects.equals(getKodeverk(), other.getKodeverk());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKode(), getKodeverk());
    }

    @Override
    public int compareTo(UlagretKodeliste that) {
        return that.getKode().compareTo(this.getKode());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "<" //$NON-NLS-1$
                + "kode=" + getKode() //$NON-NLS-1$
                + ", kodeverk=" + getKodeverk() //$NON-NLS-1$
                + ">"; //$NON-NLS-1$
    }

    public String getKodeverk() {
        return kodeverk;
    }
}

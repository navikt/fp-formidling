package no.nav.foreldrepenger.melding.dokumentdata;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import no.nav.foreldrepenger.melding.kodeverk.diff.IndexKey;

@Entity(name = "DokumentTypeData")
@Table(name = "DOKUMENT_TYPE_DATA")
public class DokumentTypeData extends BaseEntitet implements IndexKey {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DOKUMENT_TYPE_DATA")
    private Long id;

    @Version
    @Column(name = "versjon", nullable = false)
    private long versjon;

    @Column(name = "doksys_id", nullable = false)
    private String doksysId;

    @Column(name = "verdi", nullable = true)
    private String verdi;

    @Lob
    @Column(name = "strukturert_verdi")
    private String strukturertVerdi;

    @ManyToOne(optional = false)
    @JoinColumn(name = "dokument_felles_id", nullable = false, updatable = false)
    private DokumentFelles dokumentFelles;

    @Override
    public String getIndexKey() {
        return IndexKey.createKey(doksysId);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDoksysId() {
        return doksysId;
    }

    public void setDoksysId(String doksysId) {
        this.doksysId = doksysId;
    }

    public String getVerdi() {
        return verdi;
    }

    public void setVerdi(String verdi) {
        this.verdi = verdi;
    }

    public String getStrukturertVerdi() {
        return strukturertVerdi;
    }

    public void setStrukturertVerdi(String strukturertVerdi) {
        this.strukturertVerdi = strukturertVerdi;
    }

    public DokumentFelles getDokumentFelles() {
        return dokumentFelles;
    }

    public void setDokumentFelles(DokumentFelles dokumentFelles) {
        this.dokumentFelles = dokumentFelles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DokumentTypeData)) {
            return false;
        }
        DokumentTypeData that = (DokumentTypeData) o;
        return Objects.equals(getDoksysId(), that.getDoksysId()) &&
                Objects.equals(getDokumentFelles(), that.getDokumentFelles());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDoksysId(), getDokumentFelles());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "<>";
    }
}

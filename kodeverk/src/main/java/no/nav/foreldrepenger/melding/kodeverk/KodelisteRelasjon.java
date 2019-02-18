package no.nav.foreldrepenger.melding.kodeverk;

import java.time.LocalDate;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import no.nav.vedtak.felles.jpa.tid.DatoIntervallEntitet;

@Entity(name = "KodelisteRelasjon")
@Table(name = "KODELISTE_RELASJON")
public class KodelisteRelasjon {

    @Id
    @Column(name = "id", updatable = false, insertable = false)
    private Integer id;

    @Column(name = "kodeverk1")
    private String kodeverk1;

    @Column(name = "kode1")
    private String kode1;

    @Column(name = "kodeverk2")
    private String kodeverk2;

    @Column(name = "kode2")
    private String kode2;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "fomDato", column = @Column(name = "gyldig_fom")),
            @AttributeOverride(name = "tomDato", column = @Column(name = "gyldig_tom"))
    })
    private DatoIntervallEntitet gyldigIntervall;

    KodelisteRelasjon() {
        // proxy for hibernate
    }

    public KodelisteRelasjon(String kodeverk1, String kode1, String kodeverk2, String kode2, LocalDate gyldigFom, LocalDate gyldigTom) {
        this.kodeverk1 = kodeverk1;
        this.kode1 = kode1;
        this.kodeverk2 = kodeverk2;
        this.kode2 = kode2;
        this.gyldigIntervall = DatoIntervallEntitet.fraOgMedTilOgMed(gyldigFom, gyldigTom);
    }

    public String getKodeverk1() {
        return kodeverk1;
    }

    public String getKode1() {
        return kode1;
    }

    public String getKodeverk2() {
        return kodeverk2;
    }

    public String getKode2() {
        return kode2;
    }

    public LocalDate getGyldigFom() {
        return gyldigIntervall.getFomDato();
    }

    public LocalDate getGyldigTom() {
        return gyldigIntervall.getTomDato();
    }
}

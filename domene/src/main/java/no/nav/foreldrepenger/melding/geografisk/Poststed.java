package no.nav.foreldrepenger.melding.geografisk;

import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import no.nav.foreldrepenger.melding.jpa.BaseEntitet;

@Entity(name = "Poststed")
@Table(name = "POSTSTED")
public class Poststed extends BaseEntitet {

    @Id
    @Column(name = "poststednummer", updatable = false, nullable = false)
    private String poststednummer;

    @Column(name = "poststednavn", nullable = false)
    private String poststednavn;

    @Column(name = "gyldigfom", nullable = false)
    private LocalDate gyldigFom;

    @Column(name = "gyldigtom", nullable = false)
    private LocalDate gyldigTom;

    public Poststed() {
        //For Hibernate
    }

    public Poststed(String poststednummer, String poststednavn, LocalDate gyldigFom, LocalDate gyldigTom) {
        this.poststednummer = poststednummer;
        this.poststednavn = poststednavn;
        this.gyldigFom = gyldigFom;
        this.gyldigTom = gyldigTom;
    }

    public String getPoststednummer() {
        return poststednummer;
    }

    public String getPoststednavn() {
        return poststednavn;
    }

    public LocalDate getGyldigFom() {
        return gyldigFom;
    }

    public LocalDate getGyldigTom() {
        return gyldigTom;
    }

    public void setPoststednavn(String poststednavn) {
        this.poststednavn = poststednavn;
    }

    public void setGyldigFom(LocalDate gyldigFom) {
        this.gyldigFom = gyldigFom;
    }

    public void setGyldigTom(LocalDate gyldigTom) {
        this.gyldigTom = gyldigTom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Poststed that = (Poststed) o;
        return poststednummer.equals(that.poststednummer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(poststednummer);
    }

    @Override
    public String toString() {
        return "Postnummer{" +
                "poststednummer='" + poststednummer + '\'' +
                ", poststednavn='" + poststednavn + '\'' +
                ", gyldigFom=" + gyldigFom +
                ", gyldigTom=" + gyldigTom +
                '}';
    }

}

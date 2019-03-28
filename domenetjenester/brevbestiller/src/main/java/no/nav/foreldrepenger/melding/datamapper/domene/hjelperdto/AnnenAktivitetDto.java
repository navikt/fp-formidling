package no.nav.foreldrepenger.melding.datamapper.domene.hjelperdto;

import java.util.Objects;

public class AnnenAktivitetDto implements AktivitetDto {

    private String aktivitetType;
    private long dagsats;
    private boolean gradering;
    private int prosentArbeid;
    private int uttaksgrad;

    @Override
    public int getUttaksgrad() {
        return uttaksgrad;
    }

    public void setUttaksgrad(int uttaksgrad) {
        this.uttaksgrad = uttaksgrad;
    }

    public String getAktivitetType() {
        return aktivitetType;
    }

    public void setAktivitetType(String aktivitetType) {
        this.aktivitetType = aktivitetType;
    }

    @Override
    public long getDagsats() {
        return dagsats;
    }

    public void setDagsats(long dagsats) {
        this.dagsats = dagsats;
    }

    @Override
    public boolean getGradering() {
        return gradering;
    }

    public void setGradering(boolean gradering) {
        this.gradering = gradering;
    }

    public int getProsentArbeid() {
        return prosentArbeid;
    }

    public void setProsentArbeid(int prosentArbeid) {
        this.prosentArbeid = prosentArbeid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnenAktivitetDto that = (AnnenAktivitetDto) o;
        return dagsats == that.dagsats &&
                gradering == that.gradering &&
                prosentArbeid == that.prosentArbeid &&
                uttaksgrad == that.uttaksgrad &&
                Objects.equals(aktivitetType, that.aktivitetType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aktivitetType, dagsats, gradering, prosentArbeid, uttaksgrad);
    }
}

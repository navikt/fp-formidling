package no.nav.foreldrepenger.melding.datamapper.domene.hjelperdto;

import java.util.Objects;

public class NæringDto implements AktivitetDto {

    private long dagsats;
    private boolean gradering;
    private int prosentArbeid;
    private long sistLignedeÅr;
    private long inntekt1;
    private long inntekt2;
    private long inntekt3;
    private int uttaksgrad;

    @Override
    public int getUttaksgrad() {
        return uttaksgrad;
    }

    public void setUttaksgrad(int uttaksgrad) {
        this.uttaksgrad = uttaksgrad;
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

    public long getSistLignedeÅr() {
        return sistLignedeÅr;
    }

    public void setSistLignedeÅr(long sistLignedeÅr) {
        this.sistLignedeÅr = sistLignedeÅr;
    }

    public long getInntekt1() {
        return inntekt1;
    }

    public void setInntekt1(long inntekt1) {
        this.inntekt1 = inntekt1;
    }

    public long getInntekt2() {
        return inntekt2;
    }

    public void setInntekt2(long inntekt2) {
        this.inntekt2 = inntekt2;
    }

    public long getInntekt3() {
        return inntekt3;
    }

    public void setInntekt3(long inntekt3) {
        this.inntekt3 = inntekt3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NæringDto næringDto = (NæringDto) o;
        return dagsats == næringDto.dagsats &&
                gradering == næringDto.gradering &&
                prosentArbeid == næringDto.prosentArbeid &&
                sistLignedeÅr == næringDto.sistLignedeÅr &&
                inntekt1 == næringDto.inntekt1 &&
                inntekt2 == næringDto.inntekt2 &&
                inntekt3 == næringDto.inntekt3 &&
                uttaksgrad == næringDto.uttaksgrad;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dagsats, gradering, prosentArbeid, sistLignedeÅr, inntekt1, inntekt2, inntekt3, uttaksgrad);
    }
}

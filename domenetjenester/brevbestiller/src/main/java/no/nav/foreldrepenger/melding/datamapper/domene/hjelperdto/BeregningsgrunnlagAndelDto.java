package no.nav.foreldrepenger.melding.datamapper.domene.hjelperdto;

@Deprecated
public class BeregningsgrunnlagAndelDto {
    private String status;
    private String arbeidsgiverNavn;
    private String dagsats;
    private String månedsinntekt;
    private String årsinntekt;
    private String sisteLignedeÅr;
    private String pensjonsgivendeInntekt;
    private String etterlønnSluttpakke;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getArbeidsgiverNavn() {
        return arbeidsgiverNavn;
    }

    public void setArbeidsgiverNavn(String arbeidsgiverNavn) {
        this.arbeidsgiverNavn = arbeidsgiverNavn;
    }

    public String getDagsats() {
        return dagsats;
    }

    public void setDagsats(String dagsats) {
        this.dagsats = dagsats;
    }

    public String getMånedsinntekt() {
        return månedsinntekt;
    }

    public void setMånedsinntekt(String månedsinntekt) {
        this.månedsinntekt = månedsinntekt;
    }

    public String getÅrsinntekt() {
        return årsinntekt;
    }

    public void setÅrsinntekt(String årsinntekt) {
        this.årsinntekt = årsinntekt;
    }

    public String getSisteLignedeÅr() {
        return sisteLignedeÅr;
    }

    public void setSisteLignedeÅr(String sisteLignedeÅr) {
        this.sisteLignedeÅr = sisteLignedeÅr;
    }

    public String getPensjonsgivendeInntekt() {
        return pensjonsgivendeInntekt;
    }

    public void setPensjonsgivendeInntekt(String pensjonsgivendeInntekt) {
        this.pensjonsgivendeInntekt = pensjonsgivendeInntekt;
    }

    public void setEtterlønnSluttpakke(String etterlønnSluttpakke) {
        this.etterlønnSluttpakke = etterlønnSluttpakke;
    }

    public String getEtterlønnSluttpakke() {
        return etterlønnSluttpakke;
    }

    @Override
    public String toString() {
        return "BeregningsgrunnlagAndelDto{" +
                "status='" + status + '\'' +
                ", arbeidsgiverNavn='" + arbeidsgiverNavn + '\'' +
                ", dagsats='" + dagsats + '\'' +
                ", månedsinntekt='" + månedsinntekt + '\'' +
                ", årsinntekt='" + årsinntekt + '\'' +
                ", sisteLignedeÅr='" + sisteLignedeÅr + '\'' +
                ", pensjonsgivendeInntekt='" + pensjonsgivendeInntekt + '\'' +
                ", etterlønnSluttpakke='" + etterlønnSluttpakke + '\'' +
                '}';
    }
}

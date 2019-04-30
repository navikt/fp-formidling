package no.nav.foreldrepenger.melding.dokumentdata;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import no.nav.foreldrepenger.melding.kodeverk.KodeverkTabell;
import no.nav.vedtak.felles.jpa.converters.BooleanToStringConverter;

@Entity(name = "DokumentMalType")
@Table(name = "DOKUMENT_MAL_TYPE")
public class DokumentMalType extends KodeverkTabell {

    public static final String POSITIVT_VEDTAK_DOK = "POSVED";
    public static final String INNHENT_DOK = "INNHEN";
    public static final String HENLEGG_BEHANDLING_DOK = "HENLEG";
    public static final String AVSLAGSVEDTAK_DOK = "AVSLAG";
    public static final String UENDRETUTFALL_DOK = "UENDRE";
    public static final String FORLENGET_DOK = "FORLEN";
    public static final String FORLENGET_MEDL_DOK = "FORLME";
    public static final String FORLENGET_TIDLIG_SOK = "FORLTS";
    public static final String FORLENGET_OPPTJENING = "FOROPT";
    public static final String REVURDERING_DOK = "REVURD";
    public static final String KLAGE_AVVIST_DOK = "KLAGAV";
    public static final String KLAGE_YTELSESVEDTAK_STADFESTET_DOK = "KLAGVE";
    public static final String KLAGE_YTELSESVEDTAK_OPPHEVET_DOK = "KLAGNY";
    public static final String KLAGE_OVERSENDT_KLAGEINSTANS_DOK = "KLAGOV";
    public static final String INNSYNSKRAV_SVAR = "INSSKR";
    public static final String INNVILGELSE_FORELDREPENGER_DOK = "INNVFP";
    public static final String OPPHØR_DOK = "OPPHOR";
    public static final String INNTEKTSMELDING_FOR_TIDLIG_DOK = "INNTID";
    public static final String AVSLAG_FORELDREPENGER_DOK = "AVSLFP";
    public static final String FRITEKST_DOK = "FRITKS";
    public static final String VEDTAK_MEDHOLD = "VEDMED";

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "tilgjengelig_manuell_utsendelse", nullable = false)
    private boolean tilgjengeligManuellUtsendelse;

    @ManyToOne(optional = false)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = "DOKUMENT_MAL_RESTRIKSJON", referencedColumnName = "kode", nullable = false)),
            @JoinColumnOrFormula(formula = @JoinFormula(referencedColumnName = "kodeverk", value = "'"
                    + DokumentMalRestriksjon.DISCRIMINATOR + "'"))})
    private DokumentMalRestriksjon dokumentMalRestriksjon = DokumentMalRestriksjon.INGEN;
    @Column(name = "doksys_kode", nullable = false, updatable = false, insertable = false)
    private String doksysKode;

    DokumentMalType() {
        // Hibernate trenger default konstruktør
    }

    public String getDoksysKode() {
        return doksysKode;
    }

    public DokumentMalRestriksjon getDokumentMalRestriksjon() {
        return dokumentMalRestriksjon;
    }

    public boolean erTilgjengeligForManuellUtsendelse() {
        return tilgjengeligManuellUtsendelse;
    }
}

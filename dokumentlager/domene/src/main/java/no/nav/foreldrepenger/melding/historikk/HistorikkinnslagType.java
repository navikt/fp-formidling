package no.nav.foreldrepenger.melding.historikk;


import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "HistorikkinnslagType")
@DiscriminatorValue(HistorikkinnslagType.DISCRIMINATOR)
public class HistorikkinnslagType extends Kodeliste {

    public static final String DISCRIMINATOR = "HISTORIKKINNSLAG_TYPE"; //$NON-NLS-1$
    public static final HistorikkinnslagType UDEFINIERT = new HistorikkinnslagType("-");
    static final String MAL_TYPE_1 = "TYPE1";
    // type 1
    public static final HistorikkinnslagType KØET_BEH_GJEN = new HistorikkinnslagType("KØET_BEH_GJEN", MAL_TYPE_1);
    public static final HistorikkinnslagType BEH_GJEN = new HistorikkinnslagType("BEH_GJEN", MAL_TYPE_1);
    public static final HistorikkinnslagType BEH_MAN_GJEN = new HistorikkinnslagType("BEH_MAN_GJEN", MAL_TYPE_1);
    public static final HistorikkinnslagType BEH_STARTET = new HistorikkinnslagType("BEH_STARTET", MAL_TYPE_1);
    public static final HistorikkinnslagType BEH_STARTET_PÅ_NYTT = new HistorikkinnslagType("BEH_STARTET_PÅ_NYTT", MAL_TYPE_1);
    public static final HistorikkinnslagType VEDLEGG_MOTTATT = new HistorikkinnslagType("VEDLEGG_MOTTATT", MAL_TYPE_1);
    public static final HistorikkinnslagType BREV_SENT = new HistorikkinnslagType("BREV_SENT", MAL_TYPE_1);
    public static final HistorikkinnslagType BREV_BESTILT = new HistorikkinnslagType("BREV_BESTILT", MAL_TYPE_1);
    public static final HistorikkinnslagType REVURD_OPPR = new HistorikkinnslagType("REVURD_OPPR", MAL_TYPE_1);
    public static final HistorikkinnslagType REGISTRER_PAPIRSØK = new HistorikkinnslagType("REGISTRER_PAPIRSØK", MAL_TYPE_1);
    public static final HistorikkinnslagType MANGELFULL_SØKNAD = new HistorikkinnslagType("MANGELFULL_SØKNAD", MAL_TYPE_1);
    public static final HistorikkinnslagType INNSYN_OPPR = new HistorikkinnslagType("INNSYN_OPPR", MAL_TYPE_1);
    public static final HistorikkinnslagType VRS_REV_IKKE_SNDT = new HistorikkinnslagType("VRS_REV_IKKE_SNDT", MAL_TYPE_1);
    public static final HistorikkinnslagType NYE_REGOPPLYSNINGER = new HistorikkinnslagType("NYE_REGOPPLYSNINGER", MAL_TYPE_1);
    public static final HistorikkinnslagType BEH_AVBRUTT_VUR = new HistorikkinnslagType("BEH_AVBRUTT_VUR", MAL_TYPE_1);
    public static final HistorikkinnslagType KLAGEBEH_STARTET = new HistorikkinnslagType("KLAGEBEH_STARTET", MAL_TYPE_1);
    public static final HistorikkinnslagType BEH_OPPDATERT_NYE_OPPL = new HistorikkinnslagType("BEH_OPPDATERT_NYE_OPPL", MAL_TYPE_1);
    public static final HistorikkinnslagType SPOLT_TILBAKE = new HistorikkinnslagType("SPOLT_TILBAKE", MAL_TYPE_1);
    public static final HistorikkinnslagType TERMINBEKREFTELSE_UGYLDIG = new HistorikkinnslagType("TERMINBEKREFTELSE_UGYLDIG", MAL_TYPE_1);
    static final String MAL_TYPE_2 = "TYPE2";
    // type 2
    public static final HistorikkinnslagType FORSLAG_VEDTAK = new HistorikkinnslagType("FORSLAG_VEDTAK", MAL_TYPE_2);
    public static final HistorikkinnslagType FORSLAG_VEDTAK_UTEN_TOTRINN = new HistorikkinnslagType("FORSLAG_VEDTAK_UTEN_TOTRINN", MAL_TYPE_2);
    public static final HistorikkinnslagType VEDTAK_FATTET = new HistorikkinnslagType("VEDTAK_FATTET", MAL_TYPE_2);
    public static final HistorikkinnslagType UENDRET_UTFALL = new HistorikkinnslagType("UENDRET_UTFALL", MAL_TYPE_2);
    public static final HistorikkinnslagType REGISTRER_OM_VERGE = new HistorikkinnslagType("REGISTRER_OM_VERGE", MAL_TYPE_2);
    public static final HistorikkinnslagType TILBAKEKREVING_VIDEREBEHANDLING = new HistorikkinnslagType("TILBAKEKR_VIDEREBEHANDLING", MAL_TYPE_2);
    static final String MAL_TYPE_3 = "TYPE3";
    // type 3
    public static final HistorikkinnslagType SAK_RETUR = new HistorikkinnslagType("SAK_RETUR", MAL_TYPE_3);
    static final String MAL_TYPE_4 = "TYPE4";
    // type 4
    public static final HistorikkinnslagType AVBRUTT_BEH = new HistorikkinnslagType("AVBRUTT_BEH", MAL_TYPE_4);
    public static final HistorikkinnslagType BEH_VENT = new HistorikkinnslagType("BEH_VENT", MAL_TYPE_4);
    public static final HistorikkinnslagType BEH_KØET = new HistorikkinnslagType("BEH_KØET", MAL_TYPE_4);
    public static final HistorikkinnslagType IVERKSETTELSE_VENT = new HistorikkinnslagType("IVERKSETTELSE_VENT", MAL_TYPE_4);
    static final String MAL_TYPE_5 = "TYPE5";
    // type 5
    public static final HistorikkinnslagType FAKTA_ENDRET = new HistorikkinnslagType("FAKTA_ENDRET", MAL_TYPE_5);
    public static final HistorikkinnslagType BYTT_ENHET = new HistorikkinnslagType("BYTT_ENHET", MAL_TYPE_5);
    public static final HistorikkinnslagType KLAGE_BEH_NFP = new HistorikkinnslagType("KLAGE_BEH_NFP", MAL_TYPE_5);
    public static final HistorikkinnslagType KLAGE_BEH_NK = new HistorikkinnslagType("KLAGE_BEH_NK", MAL_TYPE_5);
    public static final HistorikkinnslagType UTTAK = new HistorikkinnslagType("UTTAK", MAL_TYPE_5);
    static final String MAL_TYPE_6 = "TYPE6";
    // type 6
    public static final HistorikkinnslagType NY_INFO_FRA_TPS = new HistorikkinnslagType("NY_INFO_FRA_TPS", MAL_TYPE_6);
    static final String MAL_TYPE_7 = "TYPE7";
    // type 7
    public static final HistorikkinnslagType OVERSTYRT = new HistorikkinnslagType("OVERSTYRT", MAL_TYPE_7);
    static final String MAL_TYPE_8 = "TYPE8";
    // type 8
    public static final HistorikkinnslagType OPPTJENING = new HistorikkinnslagType("OPPTJENING", MAL_TYPE_8);
    static final String MAL_TYPE_9 = "TYPE9";
    // type 9
    public static final HistorikkinnslagType OVST_UTTAK_SPLITT = new HistorikkinnslagType("OVST_UTTAK_SPLITT", MAL_TYPE_9);
    public static final HistorikkinnslagType FASTSATT_UTTAK_SPLITT = new HistorikkinnslagType("FASTSATT_UTTAK_SPLITT", MAL_TYPE_9);
    static final String MAL_TYPE_10 = "TYPE10";
    // type 10
    public static final HistorikkinnslagType OVST_UTTAK = new HistorikkinnslagType("OVST_UTTAK", MAL_TYPE_10);
    public static final HistorikkinnslagType FASTSATT_UTTAK = new HistorikkinnslagType("FASTSATT_UTTAK", MAL_TYPE_10);
    @Transient
    private String mal;

    public HistorikkinnslagType() {
        //
    }

    public HistorikkinnslagType(String kode) {
        super(kode, DISCRIMINATOR);
    }

    public HistorikkinnslagType(String kode, String mal) {
        this(kode);
        this.mal = mal;
    }

    public String getMal() {
        if (mal == null) {
            mal = getJsonField("mal"); //$NON-NLS-1$
        }
        return mal;
    }
}

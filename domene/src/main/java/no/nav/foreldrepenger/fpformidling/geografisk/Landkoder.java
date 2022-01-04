package no.nav.foreldrepenger.fpformidling.geografisk;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.KodeverdiMedNavn;


@JsonFormat(shape = Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum Landkoder implements Kodeverdi, KodeverdiMedNavn {

    /**
     * Konstanter for å skrive ned kodeverdi.
     */
    UOPPGITT("???", "UOPPGITT/UKJENT"),
    ABW("ABW", "ARUBA"),
    AFG("AFG", "AFGHANISTAN"),
    AGO("AGO", "ANGOLA"),
    AIA("AIA", "ANGUILLA"),
    ALA("ALA", "ÅLAND"),
    ALB("ALB", "ALBANIA"),
    AND("AND", "ANDORRA"),
    ANT("ANT", "DE NEDERLANDSKE ANTILLENE"),
    ARE("ARE", "DE ARABISKE EMIRATER"),
    ARG("ARG", "ARGENTINA"),
    ARM("ARM", "ARMENIA"),
    ASM("ASM", "AM. SAMOA"),
    ATG("ATG", "ANTIGUA OG BARBUDA"),
    AUS("AUS", "AUSTRALIA"),
    AUT("AUT", "ØSTERRIKE"),
    AZE("AZE", "AZERBAJDZJAN"),
    BDI("BDI", "BURUNDI"),
    BEL("BEL", "BELGIA"),
    BEN("BEN", "BENIN"),
    BES("BES", "BONAIRE, SINT EUSTATIUS, SABA"),
    BFA("BFA", "BURKINA FASO"),
    BGD("BGD", "BANGLADESH"),
    BGR("BGR", "BULGARIA"),
    BHR("BHR", "BAHRAIN"),
    BHS("BHS", "BAHAMAS"),
    BIH("BIH", "BOSNIA-HERCEGOVINA"),
    BLM("BLM", "SAINT BARTHELEMY"),
    BLR("BLR", "HVITERUSSLAND"),
    BLZ("BLZ", "BELIZE"),
    BMU("BMU", "BERMUDA"),
    BOL("BOL", "BOLIVIA"),
    BRA("BRA", "BRASIL"),
    BRB("BRB", "BARBADOS"),
    BRN("BRN", "BRUNEI"),
    BTN("BTN", "BHUTAN"),
    BVT("BVT", "BOUVETØYA"),
    BWA("BWA", "BOTSWANA"),
    CAF("CAF", "SENTRALAFRIKA. REP."),
    CAN("CAN", "CANADA"),
    CCK("CCK", "KOKOSØYENE"),
    CHE("CHE", "SVEITS"),
    CHL("CHL", "CHILE"),
    CHN("CHN", "REPUBLIKKEN KINA"),
    CIV("CIV", "ELFENBEINSKYSTEN"),
    CMR("CMR", "KAMERUN"),
    COD("COD", "KONGO, DEN DEMOKR. REPUBL"),
    COG("COG", "KONGO, REPUBLIKKEN"),
    COK("COK", "COOKØYENE"),
    COL("COL", "COLOMBIA"),
    COM("COM", "KOMORENE"),
    CPV("CPV", "KAPP VERDE"),
    CRI("CRI", "COSTA RICA"),
    CSK("CSK", "TSJEKKOSLOVAKIA"),
    CUB("CUB", "CUBA"),
    CUW("CUW", "CURACAO"),
    CXR("CXR", "CHRISTMASØYA"),
    CYM("CYM", "CAYMANØYENE"),
    CYP("CYP", "KYPROS"),
    CZE("CZE", "DEN TSJEKKISKE REP."),
    DDR("DDR", "TYSKLAND (ØST)"),
    DEU("DEU", "TYSKLAND"),
    DJI("DJI", "DJIBOUTI"),
    DMA("DMA", "DOMINICA"),
    DNK("DNK", "DANMARK"),
    DOM("DOM", "DEN DOMINIKANSKE REP"),
    DZA("DZA", "ALGERIE"),
    ECU("ECU", "ECUADOR"),
    EGY("EGY", "EGYPT"),
    ERI("ERI", "ERITREA"),
    ESH("ESH", "VEST-SAHARA"),
    ESP("ESP", "SPANIA"),
    EST("EST", "ESTLAND"),
    ETH("ETH", "ETIOPIA"),
    FIN("FIN", "FINLAND"),
    FJI("FJI", "FIJI"),
    FLK("FLK", "FALKLANDSØYENE"),
    FRA("FRA", "FRANKRIKE"),
    FRO("FRO", "FÆRØYENE"),
    FSM("FSM", "MIKRONESIAFØD."),
    GAB("GAB", "GABON"),
    GBR("GBR", "STORBRITANNIA"),
    GEO("GEO", "GEORGIA"),
    GGY("GGY", "GUERNSEY"),
    GHA("GHA", "GHANA"),
    GIB("GIB", "GIBRALTAR"),
    GIN("GIN", "GUINEA"),
    GLP("GLP", "GUADELOUPE"),
    GMB("GMB", "GAMBIA"),
    GNB("GNB", "GUINEA-BISSAU"),
    GNQ("GNQ", "EKVATORIAL-GUINEA"),
    GRC("GRC", "HELLAS"),
    GRD("GRD", "GRENADA"),
    GRL("GRL", "GRØNLAND"),
    GTM("GTM", "GUATEMALA"),
    GUF("GUF", "FRANSK GUYANA"),
    GUM("GUM", "GUAM"),
    GUY("GUY", "GUYANA"),
    HKG("HKG", "HONGKONG"),
    HMD("HMD", "HEARD OG MCDONALD ØYENE"),
    HND("HND", "HONDURAS"),
    HRV("HRV", "KROATIA"),
    HTI("HTI", "HAITI"),
    HUN("HUN", "UNGARN"),
    IDN("IDN", "INDONESIA"),
    IMN("IMN", "ISLE OF MAN"),
    IND("IND", "INDIA"),
    IOT("IOT", "BRITISK-INDISKE HAV"),
    IRL("IRL", "IRLAND"),
    IRN("IRN", "IRAN"),
    IRQ("IRQ", "IRAK"),
    ISL("ISL", "ISLAND"),
    ISR("ISR", "ISRAEL"),
    ITA("ITA", "ITALIA"),
    JAM("JAM", "JAMAICA"),
    JEY("JEY", "JERSEY"),
    JOR("JOR", "JORDAN"),
    JPN("JPN", "JAPAN"),
    KAZ("KAZ", "KAZAKHSTAN"),
    KEN("KEN", "KENYA"),
    KGZ("KGZ", "KIRGISISTAN"),
    KHM("KHM", "KAMBODSJA"),
    KIR("KIR", "KIRIBATI"),
    KNA("KNA", "ST.KITTS OG NEVIS"),
    KOR("KOR", "SØR-KOREA"),
    KWT("KWT", "KUWAIT"),
    LAO("LAO", "LAOS"),
    LBN("LBN", "LIBANON"),
    LBR("LBR", "LIBERIA"),
    LBY("LBY", "LIBYA"),
    LCA("LCA", "ST. LUCIA"),
    LIE("LIE", "LIECHTENSTEIN"),
    LKA("LKA", "SRI LANKA"),
    LSO("LSO", "LESOTHO"),
    LTU("LTU", "LITAUEN"),
    LUX("LUX", "LUXEMBOURG"),
    LVA("LVA", "LATVIA"),
    MAC("MAC", "MACAO"),
    MAF("MAF", "SAINT MARTIN"),
    MAR("MAR", "MAROKKO"),
    MCO("MCO", "MONACO"),
    MDA("MDA", "MOLDOVA"),
    MDG("MDG", "MADAGASKAR"),
    MDV("MDV", "MALDIVENE"),
    MEX("MEX", "MEXICO"),
    MHL("MHL", "MARSHALLØYENE"),
    MKD("MKD", "MAKEDONIA"),
    MLI("MLI", "MALI"),
    MLT("MLT", "MALTA"),
    MMR("MMR", "MYANMAR (BURMA)"),
    MNE("MNE", "MONTENEGRO"),
    MNG("MNG", "MONGOLIA"),
    MNP("MNP", "NORD-MARIANENE"),
    MOZ("MOZ", "MOSAMBIK"),
    MRT("MRT", "MAURITANIA"),
    MSR("MSR", "MONSERRAT"),
    MTQ("MTQ", "MARTINIQUE"),
    MUS("MUS", "MAURITIUS"),
    MWI("MWI", "MALAWI"),
    MYS("MYS", "MALAYSIA"),
    MYT("MYT", "MAYOTTE"),
    NAM("NAM", "NAMIBIA"),
    NCL("NCL", "NY-KALEDONIA"),
    NER("NER", "NIGER"),
    NFK("NFK", "NORFOLKØYA"),
    NGA("NGA", "NIGERIA"),
    NIC("NIC", "NICARAGUA"),
    NIU("NIU", "NIUE"),
    NLD("NLD", "NEDERLAND"),
    NOR("NOR", "NORGE"),
    NPL("NPL", "NEPAL"),
    NRU("NRU", "NAURU"),
    NZL("NZL", "NEW ZEALAND"),
    OMN("OMN", "OMAN"),
    PAK("PAK", "PAKISTAN"),
    PAN("PAN", "PANAMA"),
    PCN("PCN", "PITCAIRN"),
    PER("PER", "PERU"),
    PHL("PHL", "FILIPPINENE"),
    PLW("PLW", "PALAU"),
    PNG("PNG", "PAPUA NY-GUINEA"),
    POL("POL", "POLEN"),
    PRI("PRI", "PUERTO RICO"),
    PRK("PRK", "NORD-KOREA"),
    PRT("PRT", "PORTUGAL"),
    PRY("PRY", "PARAGUAY"),
    PSE("PSE", "DET PALESTINSKE OMRÅDET"),
    PYF("PYF", "FRANSK POLYNESIA"),
    QAT("QAT", "QATAR"),
    REU("REU", "REUNION"),
    ROU("ROU", "ROMANIA"),
    RUS("RUS", "RUSSLAND"),
    RWA("RWA", "RWANDA"),
    SAU("SAU", "SAUDI-ARABIA"),
    SCG("SCG", "SERBIA OG MONTENEGRO"),
    SDN("SDN", "SUDAN"),
    SEN("SEN", "SENEGAL"),
    SGP("SGP", "SINGAPORE"),
    SGS("SGS", "SØR-GEORGIA OG SØR-SANDWICHØYE"),
    SHN("SHN", "ST. HELENA"),
    SJM("SJM", "SVALBARD OG JAN MAYEN"),
    SLB("SLB", "SALOMONØYENE"),
    SLE("SLE", "SIERRA LEONE"),
    SLV("SLV", "EL SALVADOR"),
    SMR("SMR", "SAN MARINO"),
    SOM("SOM", "SOMALIA"),
    SPM("SPM", "ST-PIERRE OG MIQUELON"),
    SRB("SRB", "SERBIA"),
    SSD("SSD", "SØR-SUDAN"),
    STP("STP", "SAO TOME OG PRINCIPE"),
    SUN("SUN", "SOVJETUNIONEN"),
    SUR("SUR", "SURINAM"),
    SVK("SVK", "SLOVAKIA"),
    SVN("SVN", "SLOVENIA"),
    SWE("SWE", "SVERIGE"),
    SWZ("SWZ", "SWAZILAND"),
    SXM("SXM", "SINT MAARTEN"),
    SYC("SYC", "SEYCHELLENE"),
    SYR("SYR", "SYRIA"),
    TCA("TCA", "TURKS/CAICOSØYENE"),
    TCD("TCD", "TSJAD"),
    TGO("TGO", "TOGO"),
    THA("THA", "THAILAND"),
    TJK("TJK", "TADZJIKISTAN"),
    TKL("TKL", "TOKELAU"),
    TKM("TKM", "TURKMENISTAN"),
    TLS("TLS", "ØST-TIMOR"),
    TON("TON", "TONGA"),
    TTO("TTO", "TRINIDAD OG TOBAGO"),
    TUN("TUN", "TUNISIA"),
    TUR("TUR", "TYRKIA"),
    TUV("TUV", "TUVALU"),
    TWN("TWN", "TAIWAN"),
    TZA("TZA", "TANZANIA"),
    UGA("UGA", "UGANDA"),
    UKR("UKR", "UKRAINA"),
    UMI("UMI", "MINDRE STILLEHAVSØYER"),
    URY("URY", "URUGUAY"),
    USA("USA", "USA"),
    UZB("UZB", "UZBEKISTAN"),
    VAT("VAT", "VATIKANSTATEN"),
    VCT("VCT", "ST. VINCENT"),
    VEN("VEN", "VENEZUELA"),
    VGB("VGB", "JOMFRUØYENE BRIT."),
    VIR("VIR", "JOMFRUØYENE AM."),
    VNM("VNM", "VIETNAM"),
    VUT("VUT", "VANUATU"),
    WLF("WLF", "WALLIS/FUTUNAØYENE"),
    WSM("WSM", "SAMOA"),
    XXK("XXK", "KOSOVO"),
    XXX("XXX", "STATSLØS"),
    YEM("YEM", "JEMEN"),
    YUG("YUG", "JUGOSLAVIA"),
    ZAF("ZAF", "SØR-AFRIKA"),
    ZMB("ZMB", "ZAMBIA"),
    ZWE("ZWE", "ZIMBABWE"),

    UDEFINERT("-", "Ikke definert"),
    ;

    public static final String KODEVERK = "LANDKODER";
    private static final Map<String, Landkoder> KODER = new LinkedHashMap<>();

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }

    @JsonIgnore
    private String navn;

    private String kode;

    Landkoder(String kode, String navn) {
        this.kode = kode;
        this.navn = navn;
    }

    @JsonCreator
    public static Landkoder fraKode(@JsonProperty("kode") String kode) {
        if (kode == null) {
            return null;
        }
        var ad = KODER.get(kode);
        if (ad == null) {
            throw new IllegalArgumentException("Ukjent Landkode: " + kode);
        }
        return ad;
    }

    public static Landkoder fraKodeDefaultNorge(String kode) {
        if (kode == null) {
            return null;
        }
        return KODER.getOrDefault(kode, NOR);
    }

    public static Landkoder fraKodeDefaultUoppgitt(String kode) {
        if (kode == null) {
            return UOPPGITT;
        }
        return KODER.getOrDefault(kode, UOPPGITT);
    }

    @Override
    public String getNavn() {
        return navn;
    }


    @JsonProperty
    @Override
    public String getKode() {
        return kode;
    }

    @JsonProperty
    @Override
    public String getKodeverk() {
        return KODEVERK;
    }


    public static boolean erNorge(String kode) {
        return NOR.getKode().equals(kode);
    }

}

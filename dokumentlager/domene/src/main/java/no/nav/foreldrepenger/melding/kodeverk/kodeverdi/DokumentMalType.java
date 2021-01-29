package no.nav.foreldrepenger.melding.kodeverk.kodeverdi;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum DokumentMalType implements Kodeverdi {

    //Mal hos team dokument
    INNHENT_DOK(DokumentMalTypeKode.INNHENT_DOK, "Innhent dokumentasjon", TILGJENGELIG_MANUELL_UTSENDELSE.J.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.INNHEN),
    HENLEGG_BEHANDLING_DOK(DokumentMalTypeKode.HENLEGG_BEHANDLING_DOK, "Behandling henlagt", TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.HENLEG),
    AVSLAGSVEDTAK_DOK(DokumentMalTypeKode.AVSLAGSVEDTAK_DOK, "Avslagsbrev", TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.AVSLAG),
    UENDRETUTFALL_DOK(DokumentMalTypeKode.UENDRETUTFALL_DOK, "Uendret utfall", TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.UENDRE),
    REVURDERING_DOK(DokumentMalTypeKode.REVURDERING_DOK, "Varsel om revurdering", TILGJENGELIG_MANUELL_UTSENDELSE.J.toString(), DokumentMalRestriksjon.REVURDERING, DoksysKode.REVURD),
    FORLENGET_DOK(DokumentMalTypeKode.FORLENGET_DOK, "Forlenget saksbehandlingstid", TILGJENGELIG_MANUELL_UTSENDELSE.J.toString(), DokumentMalRestriksjon.ÅPEN_BEHANDLING, DoksysKode.FORLEN),
    FORLENGET_MEDL_DOK(DokumentMalTypeKode.FORLENGET_MEDL_DOK, "Forlenget saksbehandlingstid - medlemskap", TILGJENGELIG_MANUELL_UTSENDELSE.J.toString(), DokumentMalRestriksjon.ÅPEN_BEHANDLING_IKKE_SENDT, DoksysKode.FORLEN),
    FORLENGET_TIDLIG_SOK(DokumentMalTypeKode.FORLENGET_TIDLIG_SOK, "Forlenget saksbehandlingstid - Tidlig søknad", TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.ÅPEN_BEHANDLING, DoksysKode.FORLEN),
    FORLENGET_OPPTJENING(DokumentMalTypeKode.FORLENGET_OPPTJENING, "Forlenget saksbehandlingstid - Venter Opptjening",TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.ÅPEN_BEHANDLING, DoksysKode.FORLEN),
    INNSYNSKRAV_SVAR(DokumentMalTypeKode.INNSYNSKRAV_SVAR, "Svar på innsynskrav", TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.INSSKR),
    INNVILGELSE_FORELDREPENGER_DOK(DokumentMalTypeKode.INNVILGELSE_FORELDREPENGER_DOK, "Innvilgelsesbrev Foreldrepenger", TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.INNVFP),
    OPPHØR_DOK(DokumentMalTypeKode.OPPHØR_DOK, "Opphør brev", TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.OPPHOR),
    AVSLAG_FORELDREPENGER_DOK(DokumentMalTypeKode.AVSLAG_FORELDREPENGER_DOK, "Avslagsbrev Foreldrepenger", TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.AVSLFP ),

    //Fritekstbrev - tekst unntatt header og footer genereres av fpformidling
    FRITEKST_DOK(DokumentMalTypeKode.FRITEKST_DOK, "Fritekstbrev", TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS ),
    ETTERLYS_INNTEKTSMELDING_DOK(DokumentMalTypeKode.ETTERLYS_INNTEKTSMELDING_DOK, "Etterlys inntektsmelding", TILGJENGELIG_MANUELL_UTSENDELSE.J.toString(), DokumentMalRestriksjon.ÅPEN_BEHANDLING, DoksysKode.FRITKS),
    INFO_TIL_ANNEN_FORELDER_DOK(DokumentMalTypeKode.INFO_TIL_ANNEN_FORELDER_DOK, "Informasjonsbrev til den andre forelderen", TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    INNVILGELSE_SVANGERSKAPSPENGER_DOK(DokumentMalTypeKode.INNVILGELSE_SVANGERSKAPSPENGER_DOK, "Innvilgelsesbrev svangerskapspenger", TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    ANKEBREV_BESLUTNING_OM_OPPHEVING(DokumentMalTypeKode.ANKEBREV_BESLUTNING_OM_OPPHEVING, "Ankebrev om beslutning om oppheving", TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    ANKE_VEDTAK_OMGJORING_DOK(DokumentMalTypeKode.ANKE_VEDTAK_OMGJORING_DOK, "Vedtak om omgjøring i ankesak", TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    KLAGE_STADFESTET(DokumentMalTypeKode.KLAGE_STADFESTET, "Vedtak om stadfestelse i klagesak", TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    KLAGE_AVVIST(DokumentMalTypeKode.KLAGE_AVVIST, "Vedtak om avvist klage",TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    KLAGE_OMGJØRING(DokumentMalTypeKode.KLAGE_OMGJØRING, "Vedtak om omgjøring av klage", TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    KLAGE_OVERSENDT_KLAGEINSTANS(DokumentMalTypeKode.KLAGE_OVERSENDT_KLAGEINSTANS, "Klage oversendt til klageinstans", TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    KLAGE_HJEMSENDT(DokumentMalTypeKode.KLAGE_HJEMSENDT, "Klage hjemsendt/opphevet", TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),

    //Dokgen
    INNVILGELSE_ENGANGSSTØNAD(DokumentMalTypeKode.INNVILGELSE_ENGANGSSTØNAD, "Vedtak om innvilgelse av engangsstønad", TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    INNHENTE_OPPLYSNINGER(DokumentMalTypeKode.INNHENTE_OPPLYSNINGER, "Innhent dokumentasjon", TILGJENGELIG_MANUELL_UTSENDELSE.J.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    AVSLAG_ENGANGSSTØNAD(DokumentMalTypeKode.AVSLAG_ENGANGSSTØNAD, "Avslag engangsstønad", TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    VARSEL_OM_REVURDERING(DokumentMalTypeKode.VARSEL_OM_REVURDERING, "Varsel om revurdering", TILGJENGELIG_MANUELL_UTSENDELSE.J.toString(), DokumentMalRestriksjon.REVURDERING, DoksysKode.FRITKS),
    INFO_OM_HENLEGGELSE(DokumentMalTypeKode.INFO_OM_HENLEGGELSE, "Behandling henlagt", TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    INNSYN_SVAR(DokumentMalTypeKode.INNSYN_SVAR, "Svar på innsynskrav", TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    IKKE_SØKT(DokumentMalTypeKode.IKKE_SØKT, "Ikke mottatt søknad", TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    INGEN_ENDRING(DokumentMalTypeKode.INGEN_ENDRING, "Uendret utfall", TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),

    // Disse brevene er utgåtte, men beholdes her grunnet historisk bruk i databasen:
    @Deprecated
    KLAGE_OVERSENDT_KLAGEINSTANS_DOK(DokumentMalTypeKode.KLAGE_OVERSENDT_KLAGEINSTANS_DOK,"Overføring til NAV Klageinstans", TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.KLAGOV), //NOSONAR
    @Deprecated
    KLAGE_AVVIST_DOK(DokumentMalTypeKode.KLAGE_AVVIST_DOK, "Vedtak om avvist klage", TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.KLAGAV), //NOSONAR
    @Deprecated
    KLAGE_YTELSESVEDTAK_OPPHEVET_DOK(DokumentMalTypeKode.KLAGE_YTELSESVEDTAK_OPPHEVET_DOK, "Vedtak opphevet, sendt til ny behandling", TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.KLAGNY), //NOSONAR
    @Deprecated
    VEDTAK_MEDHOLD(DokumentMalTypeKode.VEDTAK_MEDHOLD, "Vedtak om medhold", TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.VEDMED), //NOSONAR
    @Deprecated
    KLAGE_YTELSESVEDTAK_STADFESTET_DOK(DokumentMalTypeKode.KLAGE_YTELSESVEDTAK_STADFESTET_DOK, "Vedtak om stadfestelse", TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.KLAGVE), //NOSONAR
    @Deprecated
    POSITIVT_VEDTAK_DOK(DokumentMalTypeKode.POSITIVT_VEDTAK_DOK, "Positivt vedtaksbrev",TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.POSVED ), //NOSONAR
    @Deprecated
    INNTEKTSMELDING_FOR_TIDLIG_DOK(DokumentMalTypeKode.INNTEKTSMELDING_FOR_TIDLIG_DOK, "Ikke mottatt søknad", TILGJENGELIG_MANUELL_UTSENDELSE.N.toString(), DokumentMalRestriksjon.INGEN, DoksysKode.INNTID), //NOSONAR

    UDEFINERT("-"),

    ;
    public static final String KODEVERK = "DOKUMENT_MAL_TYPE";

    public static final Set<DokumentMalType> FORLENGET_BREVMALER = Set.of(DokumentMalType.FORLENGET_MEDL_DOK,
            DokumentMalType.FORLENGET_TIDLIG_SOK,
            DokumentMalType.FORLENGET_OPPTJENING,
            DokumentMalType.FORLENGET_DOK);

    private static final Map<String, DokumentMalType> KODER = new LinkedHashMap<>();

    private static final Set<DokumentMalType> GYLDIGE_MALER;

    static {
        GYLDIGE_MALER = Arrays.stream(DokumentMalType.values()).filter(mal -> {
            try {
                Field field = DokumentMalType.class.getField(mal.name());
                return !UDEFINERT.equals(mal) && !field.isAnnotationPresent(Deprecated.class);
            } catch (NoSuchFieldException | SecurityException e) {
                return false;
            }
        }).collect(Collectors.toSet());
    }

    private String kode;
    @JsonIgnore
    private String navn;
    @JsonIgnore
    private String manuellUtsendelse;
    @JsonIgnore
    private DokumentMalRestriksjon dokumentmalRestriksjon;
    @JsonIgnore
    private DoksysKode dokSysKode;

    DokumentMalType() {
        // for hibernate
    }

    DokumentMalType (String kode,
                     String navn,
                     String manuellUtsendelse,
                     DokumentMalRestriksjon dokumentmalRestriksjon,
                     DoksysKode dokSysKode) {
        this.kode = kode;
        this.navn = navn;
        this.manuellUtsendelse = manuellUtsendelse;
        this.dokumentmalRestriksjon = dokumentmalRestriksjon;
        this.dokSysKode = dokSysKode;
    }

    public String getNavn() {
        return navn;
    }

    public String getManuellUtsendelse() {
        return manuellUtsendelse;
    }

    public DoksysKode getDokSysKode() {
        return dokSysKode;
    }

    public DokumentMalRestriksjon getDokumentmalRestriksjon() { return dokumentmalRestriksjon;}

    DokumentMalType(String kode) {
        this.kode = kode;
    }

    @JsonCreator
    public static DokumentMalType fraKode(@JsonProperty("kode") String kode) {
        if (kode == null) {
            return null;
        }
        var ad = KODER.get(kode);
        if (ad == null) {
            throw new IllegalArgumentException("Ukjent Dokumentmaltype: " + kode);
        }
        return ad;
    }

    public static DokumentMalType fraKodeDefaultUdefinert(@JsonProperty("kode") String kode) {
        if (kode == null) {
            return null;
        }
        return KODER.getOrDefault(kode, UDEFINERT);
    }

    @JsonProperty
    @Override
    public String getKodeverk() {
        return KODEVERK;
    }

    @JsonProperty
    @Override
    public String getKode() {
        return kode;
    }

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }

    @Converter(autoApply = true)
    public static class KodeverdiConverter implements AttributeConverter<DokumentMalType, String> {
        @Override
        public String convertToDatabaseColumn(DokumentMalType attribute) {
            return attribute == null ? null : attribute.getKode();
        }

        @Override
        public DokumentMalType convertToEntityAttribute(String dbData) {
            return dbData == null ? null : fraKode(dbData);
        }
    }

    public static Set<DokumentMalType> hentAlleGyldige() {
        return Collections.unmodifiableSet(GYLDIGE_MALER);
    }

    public boolean erTilgjengeligForManuellUtsendelse() {
        return this.manuellUtsendelse.equals(TILGJENGELIG_MANUELL_UTSENDELSE.J.toString());
    }

    private enum  TILGJENGELIG_MANUELL_UTSENDELSE {
        J,
        N,
    }
}

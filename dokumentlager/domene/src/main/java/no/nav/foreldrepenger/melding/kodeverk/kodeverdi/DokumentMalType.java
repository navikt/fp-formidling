package no.nav.foreldrepenger.melding.kodeverk.kodeverdi;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum DokumentMalType implements Kodeverdi {

    //Mal hos team dokument
    OPPHØR_DOK(DokumentMalTypeKode.OPPHØR_DOK, "Opphør brev", "N", DokumentMalRestriksjon.INGEN, DoksysKode.OPPHOR),

    //Fritekstbrev - tekst unntatt header og footer genereres av fpformidling
    FRITEKST_DOK(DokumentMalTypeKode.FRITEKST_DOK, "Fritekstbrev", "N", DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS ),
    ETTERLYS_INNTEKTSMELDING_DOK(DokumentMalTypeKode.ETTERLYS_INNTEKTSMELDING_DOK, "Etterlys inntektsmelding", "J", DokumentMalRestriksjon.ÅPEN_BEHANDLING, DoksysKode.FRITKS),
    INNVILGELSE_SVANGERSKAPSPENGER_DOK(DokumentMalTypeKode.INNVILGELSE_SVANGERSKAPSPENGER_DOK, "Innvilgelsesbrev svangerskapspenger", "N", DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    ANKEBREV_BESLUTNING_OM_OPPHEVING(DokumentMalTypeKode.ANKEBREV_BESLUTNING_OM_OPPHEVING, "Ankebrev om beslutning om oppheving", "N", DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    ANKE_VEDTAK_OMGJORING_DOK(DokumentMalTypeKode.ANKE_VEDTAK_OMGJORING_DOK, "Vedtak om omgjøring i ankesak", "N", DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    KLAGE_STADFESTET(DokumentMalTypeKode.KLAGE_STADFESTET, "Vedtak om stadfestelse i klagesak", "N", DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    KLAGE_AVVIST(DokumentMalTypeKode.KLAGE_AVVIST, "Vedtak om avvist klage","N", DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    KLAGE_OMGJØRING(DokumentMalTypeKode.KLAGE_OMGJØRING, "Vedtak om omgjøring av klage", "N", DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    KLAGE_OVERSENDT_KLAGEINSTANS(DokumentMalTypeKode.KLAGE_OVERSENDT_KLAGEINSTANS, "Klage oversendt til klageinstans", "N", DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    KLAGE_HJEMSENDT(DokumentMalTypeKode.KLAGE_HJEMSENDT, "Klage hjemsendt/opphevet", "N", DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),

    //Dokgen
    ENGANGSSTØNAD_INNVILGELSE(DokumentMalTypeKode.ENGANGSSTØNAD_INNVILGELSE, "Vedtak om innvilgelse av engangsstønad", "N", DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    ENGANGSSTØNAD_AVSLAG(DokumentMalTypeKode.ENGANGSSTØNAD_AVSLAG, "Avslag engangsstønad", "N", DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    FORELDREPENGER_INNVILGELSE(DokumentMalTypeKode.FORELDREPENGER_INNVILGELSE, "Innvilgelsesbrev Foreldrepenger", "N", DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    FORELDREPENGER_AVSLAG(DokumentMalTypeKode.FORELDREPENGER_AVSLAG, "Avslagsbrev Foreldrepenger", "N", DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    FORELDREPENGER_ANNULLERT(DokumentMalTypeKode.FORELDREPENGER_ANNULLERT, "Annullering av Foreldrepenger", "N", DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    FORELDREPENGER_INFOBREV_TIL_ANNEN_FORELDER(DokumentMalTypeKode.FORELDREPENGER_INFO_TIL_ANNEN_FORELDER, "Informasjonsbrev til den andre forelderen", "N", DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    INNHENTE_OPPLYSNINGER(DokumentMalTypeKode.INNHENTE_OPPLYSNINGER, "Innhent dokumentasjon", "J", DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    VARSEL_OM_REVURDERING(DokumentMalTypeKode.VARSEL_OM_REVURDERING, "Varsel om revurdering", "J", DokumentMalRestriksjon.REVURDERING, DoksysKode.FRITKS),
    INFO_OM_HENLEGGELSE(DokumentMalTypeKode.INFO_OM_HENLEGGELSE, "Behandling henlagt", "N", DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    INNSYN_SVAR(DokumentMalTypeKode.INNSYN_SVAR, "Svar på innsynskrav", "N", DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    IKKE_SØKT(DokumentMalTypeKode.IKKE_SØKT, "Ikke mottatt søknad", "N", DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    INGEN_ENDRING(DokumentMalTypeKode.INGEN_ENDRING, "Uendret utfall", "N", DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS),
    FORLENGET_SAKSBEHANDLINGSTID(DokumentMalTypeKode.FORLENGET_SAKSBEHANDLINGSTID, "Forlenget saksbehandlingstid", "J", DokumentMalRestriksjon.ÅPEN_BEHANDLING, DoksysKode.FRITKS),
    FORLENGET_SAKSBEHANDLINGSTID_MEDL(DokumentMalTypeKode.FORLENGET_SAKSBEHANDLINGSTID_MEDL, "Forlenget saksbehandlingstid - medlemskap", "J", DokumentMalRestriksjon.ÅPEN_BEHANDLING_IKKE_SENDT, DoksysKode.FRITKS),
    FORLENGET_SAKSBEHANDLINGSTID_TIDLIG(DokumentMalTypeKode.FORLENGET_SAKSBEHANDLINGSTID_TIDLIG, "Forlenget saksbehandlingstid - Tidlig søknad", "N", DokumentMalRestriksjon.ÅPEN_BEHANDLING, DoksysKode.FRITKS),

    // Disse brevene er utgåtte, men beholdes her grunnet historisk bruk i databasen:
    @Deprecated
    KLAGE_OVERSENDT_KLAGEINSTANS_DOK(DokumentMalTypeKode.KLAGE_OVERSENDT_KLAGEINSTANS_DOK,"Overføring til NAV Klageinstans", "N", DokumentMalRestriksjon.INGEN, DoksysKode.KLAGOV), //NOSONAR
    @Deprecated
    KLAGE_AVVIST_DOK(DokumentMalTypeKode.KLAGE_AVVIST_DOK, "Vedtak om avvist klage", "N", DokumentMalRestriksjon.INGEN, DoksysKode.KLAGAV), //NOSONAR
    @Deprecated
    KLAGE_YTELSESVEDTAK_OPPHEVET_DOK(DokumentMalTypeKode.KLAGE_YTELSESVEDTAK_OPPHEVET_DOK, "Vedtak opphevet, sendt til ny behandling", "N", DokumentMalRestriksjon.INGEN, DoksysKode.KLAGNY), //NOSONAR
    @Deprecated
    VEDTAK_MEDHOLD(DokumentMalTypeKode.VEDTAK_MEDHOLD, "Vedtak om medhold", "N", DokumentMalRestriksjon.INGEN, DoksysKode.VEDMED), //NOSONAR
    @Deprecated
    KLAGE_YTELSESVEDTAK_STADFESTET_DOK(DokumentMalTypeKode.KLAGE_YTELSESVEDTAK_STADFESTET_DOK, "Vedtak om stadfestelse", "N", DokumentMalRestriksjon.INGEN, DoksysKode.KLAGVE), //NOSONAR
    @Deprecated
    POSITIVT_VEDTAK_DOK(DokumentMalTypeKode.POSITIVT_VEDTAK_DOK, "Positivt vedtaksbrev","N", DokumentMalRestriksjon.INGEN, DoksysKode.POSVED ), //NOSONAR
    @Deprecated
    INNTEKTSMELDING_FOR_TIDLIG_DOK(DokumentMalTypeKode.INNTEKTSMELDING_FOR_TIDLIG_DOK, "Ikke mottatt søknad", "N", DokumentMalRestriksjon.INGEN, DoksysKode.INNTID), //NOSONAR
    @Deprecated
    AVSLAGSVEDTAK_DOK(DokumentMalTypeKode.AVSLAGSVEDTAK_DOK, "Avslagsbrev", "N", DokumentMalRestriksjon.INGEN, DoksysKode.AVSLAG), //NOSONAR
    @Deprecated
    REVURDERING_DOK(DokumentMalTypeKode.REVURDERING_DOK, "Varsel om revurdering", "J", DokumentMalRestriksjon.REVURDERING, DoksysKode.REVURD), //NOSONAR
    @Deprecated
    UENDRETUTFALL_DOK(DokumentMalTypeKode.UENDRETUTFALL_DOK, "Uendret utfall", "N", DokumentMalRestriksjon.INGEN, DoksysKode.UENDRE), //NOSONAR
    @Deprecated
    INFO_TIL_ANNEN_FORELDER_DOK(DokumentMalTypeKode.INFO_TIL_ANNEN_FORELDER_DOK, "Informasjonsbrev til den andre forelderen", "N", DokumentMalRestriksjon.INGEN, DoksysKode.FRITKS), //NOSONAR
    @Deprecated
    INNSYNSKRAV_SVAR(DokumentMalTypeKode.INNSYNSKRAV_SVAR, "Svar på innsynskrav", "N", DokumentMalRestriksjon.INGEN, DoksysKode.INSSKR), //NOSONAR
    @Deprecated
    FORLENGET_DOK(DokumentMalTypeKode.FORLENGET_DOK, "Forlenget saksbehandlingstid", "J", DokumentMalRestriksjon.ÅPEN_BEHANDLING, DoksysKode.FORLEN), //NOSONAR
    @Deprecated
    FORLENGET_MEDL_DOK(DokumentMalTypeKode.FORLENGET_MEDL_DOK, "Forlenget saksbehandlingstid - medlemskap", "J", DokumentMalRestriksjon.ÅPEN_BEHANDLING_IKKE_SENDT, DoksysKode.FORLEN), //NOSONAR
    @Deprecated
    FORLENGET_TIDLIG_SOK(DokumentMalTypeKode.FORLENGET_TIDLIG_SOK, "Forlenget saksbehandlingstid - Tidlig søknad", "N", DokumentMalRestriksjon.ÅPEN_BEHANDLING, DoksysKode.FORLEN), //NOSONAR
    @Deprecated
    HENLEGG_BEHANDLING_DOK(DokumentMalTypeKode.HENLEGG_BEHANDLING_DOK, "Behandling henlagt", "N", DokumentMalRestriksjon.INGEN, DoksysKode.HENLEG), //NOSONAR
    @Deprecated
    INNHENT_DOK(DokumentMalTypeKode.INNHENT_DOK, "Innhent dokumentasjon", "J", DokumentMalRestriksjon.INGEN, DoksysKode.INNHEN), //NOSONAR
    @Deprecated
    AVSLAG_FORELDREPENGER_DOK(DokumentMalTypeKode.AVSLAG_FORELDREPENGER_DOK, "Avslagsbrev Foreldrepenger", "N", DokumentMalRestriksjon.INGEN, DoksysKode.AVSLFP ), //NOSONAR
    @Deprecated
    INNVILGELSE_FORELDREPENGER_DOK(DokumentMalTypeKode.INNVILGELSE_FORELDREPENGER_DOK, "Innvilgelsesbrev Foreldrepenger", "N", DokumentMalRestriksjon.INGEN, DoksysKode.INNVFP), //NOSONAR

    UDEFINERT("-")
    ;

    public static final String KODEVERK = "DOKUMENT_MAL_TYPE";

    public static final Set<DokumentMalType> FORLENGET_SAKSBEHANDLINGSTID_BREVMALER = Set.of(
            DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID,
            DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_MEDL,
            DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_TIDLIG);

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
        return this.manuellUtsendelse.equals("J");
    }
}

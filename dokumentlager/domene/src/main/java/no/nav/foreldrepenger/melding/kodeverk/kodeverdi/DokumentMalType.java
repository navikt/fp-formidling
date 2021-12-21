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

    FRITEKSTBREV(DokumentMalTypeKode.FRITEKSTBREV, "Fritekstbrev", "N", DokumentMalRestriksjon.INGEN),
    ENGANGSSTØNAD_INNVILGELSE(DokumentMalTypeKode.ENGANGSSTØNAD_INNVILGELSE, "Vedtak om innvilgelse av engangsstønad", "N", DokumentMalRestriksjon.INGEN),
    ENGANGSSTØNAD_AVSLAG(DokumentMalTypeKode.ENGANGSSTØNAD_AVSLAG, "Avslag engangsstønad", "N", DokumentMalRestriksjon.INGEN),
    FORELDREPENGER_INNVILGELSE(DokumentMalTypeKode.FORELDREPENGER_INNVILGELSE, "Innvilgelsesbrev Foreldrepenger", "N", DokumentMalRestriksjon.INGEN),
    FORELDREPENGER_AVSLAG(DokumentMalTypeKode.FORELDREPENGER_AVSLAG, "Avslagsbrev Foreldrepenger", "N", DokumentMalRestriksjon.INGEN),
    FORELDREPENGER_OPPHØR(DokumentMalTypeKode.FORELDREPENGER_OPPHØR, "Opphør Foreldrepenger", "N", DokumentMalRestriksjon.INGEN),
    FORELDREPENGER_ANNULLERT(DokumentMalTypeKode.FORELDREPENGER_ANNULLERT, "Annullering av Foreldrepenger", "N", DokumentMalRestriksjon.INGEN),
    FORELDREPENGER_INFOBREV_TIL_ANNEN_FORELDER(DokumentMalTypeKode.FORELDREPENGER_INFO_TIL_ANNEN_FORELDER, "Informasjonsbrev til den andre forelderen", "N", DokumentMalRestriksjon.INGEN),
    SVANGERSKAPSPENGER_INNVILGELSE(DokumentMalTypeKode.SVANGERSKAPSPENGER_INNVILGELSE, "Innvilgelsesbrev svangerskapspenger", "N", DokumentMalRestriksjon.INGEN),
    SVANGERSKAPSPENGER_OPPHØR(DokumentMalTypeKode.SVANGERSKAPSPENGER_OPPHØR, "Opphørsbrev svangerskapspenger", "N", DokumentMalRestriksjon.INGEN),
    SVANGERSKAPSPENGER_AVSLAG(DokumentMalTypeKode.SVANGERSKAPSPENGER_AVSLAG, "Avslagsbrev svangerskapspenger", "N", DokumentMalRestriksjon.INGEN),
    INNHENTE_OPPLYSNINGER(DokumentMalTypeKode.INNHENTE_OPPLYSNINGER, "Innhent dokumentasjon", "J", DokumentMalRestriksjon.INGEN),
    VARSEL_OM_REVURDERING(DokumentMalTypeKode.VARSEL_OM_REVURDERING, "Varsel om revurdering", "J", DokumentMalRestriksjon.REVURDERING),
    INFO_OM_HENLEGGELSE(DokumentMalTypeKode.INFO_OM_HENLEGGELSE, "Behandling henlagt", "N", DokumentMalRestriksjon.INGEN),
    INNSYN_SVAR(DokumentMalTypeKode.INNSYN_SVAR, "Svar på innsynskrav", "N", DokumentMalRestriksjon.INGEN),
    IKKE_SØKT(DokumentMalTypeKode.IKKE_SØKT, "Ikke mottatt søknad", "N", DokumentMalRestriksjon.INGEN),
    INGEN_ENDRING(DokumentMalTypeKode.INGEN_ENDRING, "Uendret utfall", "N", DokumentMalRestriksjon.INGEN),
    FORLENGET_SAKSBEHANDLINGSTID(DokumentMalTypeKode.FORLENGET_SAKSBEHANDLINGSTID, "Forlenget saksbehandlingstid", "J", DokumentMalRestriksjon.ÅPEN_BEHANDLING),
    FORLENGET_SAKSBEHANDLINGSTID_MEDL(DokumentMalTypeKode.FORLENGET_SAKSBEHANDLINGSTID_MEDL, "Forlenget saksbehandlingstid - medlemskap", "J", DokumentMalRestriksjon.ÅPEN_BEHANDLING_IKKE_SENDT),
    FORLENGET_SAKSBEHANDLINGSTID_TIDLIG(DokumentMalTypeKode.FORLENGET_SAKSBEHANDLINGSTID_TIDLIG, "Forlenget saksbehandlingstid - Tidlig søknad", "N", DokumentMalRestriksjon.ÅPEN_BEHANDLING),
    KLAGE_AVVIST(DokumentMalTypeKode.KLAGE_AVVIST, "Vedtak om avvist klage","N", DokumentMalRestriksjon.INGEN),
    KLAGE_HJEMSENDT(DokumentMalTypeKode.KLAGE_HJEMSENDT, "Klage hjemsendt/opphevet", "N", DokumentMalRestriksjon.INGEN),
    KLAGE_OMGJORT(DokumentMalTypeKode.KLAGE_OMGJORT, "Vedtak om omgjøring av klage", "N", DokumentMalRestriksjon.INGEN),
    KLAGE_OVERSENDT(DokumentMalTypeKode.KLAGE_OVERSENDT, "Klage oversendt til klageinstans", "N", DokumentMalRestriksjon.INGEN),
    KLAGE_STADFESTET(DokumentMalTypeKode.KLAGE_STADFESTET, "Vedtak om stadfestelse i klagesak", "N", DokumentMalRestriksjon.INGEN),
    ANKE_OMGJORT(DokumentMalTypeKode.ANKE_OMGJORT, "Vedtak om omgjøring i ankesak", "N", DokumentMalRestriksjon.INGEN),
    ANKE_OPPHEVET(DokumentMalTypeKode.ANKE_OPPHEVET, "Ankebrev om beslutning om oppheving", "N", DokumentMalRestriksjon.INGEN),
    ETTERLYS_INNTEKTSMELDING(DokumentMalTypeKode.ETTERLYS_INNTEKTSMELDING, "Etterlys inntektsmelding", "J", DokumentMalRestriksjon.ÅPEN_BEHANDLING),

    // Disse brevene er utgåtte, men beholdes her grunnet historisk bruk i databasen:
    @Deprecated
    FRITEKSTBREV_DOK(DokumentMalTypeKode.FRITEKSTBREV_DOK, "Fritekstbrev", "N", DokumentMalRestriksjon.INGEN), //NOSONAR
    @Deprecated
    ENGANGSSTØNAD_INNVILGELSE_DOK(DokumentMalTypeKode.ENGANGSSTØNAD_INNVILGELSE_DOK, "Positivt vedtaksbrev","N", DokumentMalRestriksjon.INGEN), //NOSONAR
    @Deprecated
    ENGANGSSTØNAD_AVSLAG_DOK(DokumentMalTypeKode.ENGANGSSTØNAD_AVSLAG_DOK, "Avslagsbrev", "N", DokumentMalRestriksjon.INGEN), //NOSONAR
    @Deprecated
    FORELDREPENGER_INNVILGELSE_DOK(DokumentMalTypeKode.FORELDREPENGER_INNVILGELSE_DOK, "Innvilgelsesbrev Foreldrepenger", "N", DokumentMalRestriksjon.INGEN), //NOSONAR
    @Deprecated
    FORELDREPENGER_AVSLAG_DOK(DokumentMalTypeKode.FORELDREPENGER_AVSLAG_DOK, "Avslagsbrev Foreldrepenger", "N", DokumentMalRestriksjon.INGEN), //NOSONAR
    @Deprecated
    FORELDREPENGER_OPPHØR_DOK(DokumentMalTypeKode.FORELDREPENGER_OPPHØR_DOK, "Opphør brev", "N", DokumentMalRestriksjon.INGEN), //NOSONAR
    @Deprecated
    FORELDREPENGER_INFOBREV_TIL_ANNEN_FORELDER_DOK(DokumentMalTypeKode.FORELDREPENGER_INFOBREV_TIL_ANNEN_FORELDER_DOK, "Informasjonsbrev til den andre forelderen", "N", DokumentMalRestriksjon.INGEN), //NOSONAR
    @Deprecated
    SVANGERSKAPSPENGER_INNVILGELSE_FRITEKST(DokumentMalTypeKode.SVANGERSKAPSPENGER_INNVILGELSE_FRITEKST, "Innvilgelsesbrev svangerskapspenger", "N", DokumentMalRestriksjon.INGEN), //NOSONAR
    @Deprecated
    INNHENTE_OPPLYSNINGER_DOK(DokumentMalTypeKode.INNHENTE_OPPLYSNINGER_DOK, "Innhent dokumentasjon", "J", DokumentMalRestriksjon.INGEN), //NOSONAR
    @Deprecated
    VARSEL_OM_REVURDERING_DOK(DokumentMalTypeKode.VARSEL_OM_REVURDERING_DOK, "Varsel om revurdering", "J", DokumentMalRestriksjon.REVURDERING), //NOSONAR
    @Deprecated
    INFO_OM_HENLEGGELSE_DOK(DokumentMalTypeKode.INFO_OM_HENLEGGELSE_DOK, "Behandling henlagt", "N", DokumentMalRestriksjon.INGEN), //NOSONAR
    @Deprecated
    INNSYN_SVAR_DOK(DokumentMalTypeKode.INNSYN_SVAR_DOK, "Svar på innsynskrav", "N", DokumentMalRestriksjon.INGEN), //NOSONAR
    @Deprecated
    IKKE_SØKT_DOK(DokumentMalTypeKode.IKKE_SØKT_DOK, "Ikke mottatt søknad", "N", DokumentMalRestriksjon.INGEN), //NOSONAR
    @Deprecated
    INGEN_ENDRING_DOK(DokumentMalTypeKode.INGEN_ENDRING_DOK, "Uendret utfall", "N", DokumentMalRestriksjon.INGEN), //NOSONAR
    @Deprecated
    FORLENGET_SAKSBEHANDLINGSTID_DOK(DokumentMalTypeKode.FORLENGET_SAKSBEHANDLINGSTID_DOK, "Forlenget saksbehandlingstid", "J", DokumentMalRestriksjon.ÅPEN_BEHANDLING), //NOSONAR
    @Deprecated
    FORLENGET_SAKSBEHANDLINGSTID_MEDL_DOK(DokumentMalTypeKode.FORLENGET_SAKSBEHANDLINGSTID_MEDL_DOK, "Forlenget saksbehandlingstid - medlemskap", "J", DokumentMalRestriksjon.ÅPEN_BEHANDLING_IKKE_SENDT), //NOSONAR
    @Deprecated
    FORLENGET_SAKSBEHANDLINGSTID_TIDLIG_DOK(DokumentMalTypeKode.FORLENGET_SAKSBEHANDLINGSTID_TIDLIG_DOK, "Forlenget saksbehandlingstid - Tidlig søknad", "N", DokumentMalRestriksjon.ÅPEN_BEHANDLING), //NOSONAR
    @Deprecated
    KLAGE_AVVIST_DOK(DokumentMalTypeKode.KLAGE_AVVIST_DOK, "Vedtak om avvist klage", "N", DokumentMalRestriksjon.INGEN), //NOSONAR
    @Deprecated
    KLAGE_AVVIST_FRITEKST(DokumentMalTypeKode.KLAGE_AVVIST_FRITEKST, "Vedtak om avvist klage","N", DokumentMalRestriksjon.INGEN), //NOSONAR
    @Deprecated
    KLAGE_HJEMSENDT_DOK(DokumentMalTypeKode.KLAGE_HJEMSENDT_DOK, "Vedtak opphevet, sendt til ny behandling", "N", DokumentMalRestriksjon.INGEN), //NOSONAR
    @Deprecated
    KLAGE_HJEMSENDT_FRITEKST(DokumentMalTypeKode.KLAGE_HJEMSENDT_FRITEKST, "Klage hjemsendt/opphevet", "N", DokumentMalRestriksjon.INGEN), //NOSONAR
    @Deprecated
    KLAGE_OMGJORT_DOK(DokumentMalTypeKode.KLAGE_OMGJORT_DOK, "Vedtak om medhold", "N", DokumentMalRestriksjon.INGEN), //NOSONAR
    @Deprecated
    KLAGE_OMGJORT_FRITEKST(DokumentMalTypeKode.KLAGE_OMGJORT_FRITEKST, "Vedtak om omgjøring av klage", "N", DokumentMalRestriksjon.INGEN), //NOSONAR
    @Deprecated
    KLAGE_OVERSENDT_DOK(DokumentMalTypeKode.KLAGE_OVERSENDT_DOK,"Overføring til NAV Klageinstans", "N", DokumentMalRestriksjon.INGEN), //NOSONAR
    @Deprecated
    KLAGE_OVERSENDT_FRITEKST(DokumentMalTypeKode.KLAGE_OVERSENDT_FRITEKST, "Klage oversendt til klageinstans", "N", DokumentMalRestriksjon.INGEN), //NOSONAR
    @Deprecated
    KLAGE_STADFESTET_DOK(DokumentMalTypeKode.KLAGE_STADFESTET_DOK, "Vedtak om stadfestelse", "N", DokumentMalRestriksjon.INGEN), //NOSONAR
    @Deprecated
    KLAGE_STADFESTET_FRITEKST(DokumentMalTypeKode.KLAGE_STADFESTET_FRITEKST, "Vedtak om stadfestelse i klagesak", "N", DokumentMalRestriksjon.INGEN), //NOSONAR
    @Deprecated
    ANKE_OMGJORT_FRITEKST(DokumentMalTypeKode.ANKE_OMGJORT_FRITEKST, "Vedtak om omgjøring i ankesak", "N", DokumentMalRestriksjon.INGEN), //NOSONAR
    @Deprecated
    ANKE_OPPHEVET_FRITEKST(DokumentMalTypeKode.ANKE_OPPHEVET_FRITEKST, "Ankebrev om beslutning om oppheving", "N", DokumentMalRestriksjon.INGEN), //NOSONAR
    @Deprecated
    ETTERLYS_INNTEKTSMELDING_FRITEKST(DokumentMalTypeKode.ETTERLYS_INNTEKTSMELDING_FRITEKST, "Etterlys inntektsmelding", "J", DokumentMalRestriksjon.ÅPEN_BEHANDLING), //NOSONAR

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

    DokumentMalType() {
        // for hibernate
    }

    DokumentMalType (String kode,
                     String navn,
                     String manuellUtsendelse,
                     DokumentMalRestriksjon dokumentmalRestriksjon) {
        this.kode = kode;
        this.navn = navn;
        this.manuellUtsendelse = manuellUtsendelse;
        this.dokumentmalRestriksjon = dokumentmalRestriksjon;
    }

    public String getNavn() {
        return navn;
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

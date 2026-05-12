package no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi;

import java.util.Set;

public enum DokumentMalType {

    FRITEKSTBREV("FRITEK", "Fritekstbrev"),
    FRITEKSTBREV_HTML("FRIHTM", "Fritekstbrev html"),
    ENGANGSSTØNAD_INNVILGELSE("INNVES", "Vedtak om innvilgelse av engangsstønad"),
    ENGANGSSTØNAD_AVSLAG("AVSLES", "Avslag engangsstønad"),
    FORELDREPENGER_INNVILGELSE("INVFOR", "Innvilgelsesbrev Foreldrepenger"),
    FORELDREPENGER_AVSLAG("AVSFOR", "Avslagsbrev Foreldrepenger"),
    FORELDREPENGER_OPPHØR("OPPFOR", "Opphør Foreldrepenger"),
    FORELDREPENGER_ANNULLERT("ANUFOR", "Annullering av Foreldrepenger"),
    FORELDREPENGER_INFOBREV_TIL_ANNEN_FORELDER("INFOAF", "Informasjonsbrev til den andre forelderen"),
    FORELDREPENGER_FEIL_PRAKSIS_UTSETTELSE_INFOBREV("INFOPU", "Melding om ny vurdering av tidligere avslag"),
    FORELDREPENGER_FEIL_PRAKSIS_UTSETTELSE_FORLENGET_SAKSBEHANDLINGSTID("FORPUS", "Forlenget saksbehandlingstid - Fedrekvotesaken"),
    SVANGERSKAPSPENGER_INNVILGELSE("INVSVP", "Innvilgelsesbrev svangerskapspenger"),
    SVANGERSKAPSPENGER_OPPHØR("OPPSVP", "Opphørsbrev svangerskapspenger"),
    SVANGERSKAPSPENGER_AVSLAG("AVSSVP", "Avslagsbrev svangerskapspenger"),
    INNHENTE_OPPLYSNINGER("INNOPP", "Innhent dokumentasjon"),
    VARSEL_OM_REVURDERING("VARREV", "Varsel om revurdering"),
    INFO_OM_HENLEGGELSE("IOHENL", "Behandling henlagt"),
    INNSYN_SVAR("INNSYN", "Svar på innsynskrav"),
    IKKE_SØKT("IKKESO", "Ikke mottatt søknad"),
    INGEN_ENDRING("INGEND", "Uendret utfall"),
    FORLENGET_SAKSBEHANDLINGSTID("FORSAK", "Forlenget saksbehandlingstid"),
    FORLENGET_SAKSBEHANDLINGSTID_MEDL("FORMED", "Forlenget saksbehandlingstid - medlemskap"),
    FORLENGET_SAKSBEHANDLINGSTID_MEDL_FORUTGÅENDE("FORMEF", "Forlenget saksbehandlingstid - forutgående medlemskap"),
    FORLENGET_SAKSBEHANDLINGSTID_TIDLIG("FORTID", "Forlenget saksbehandlingstid - Tidlig søknad"),
    KLAGE_AVVIST("KGEAVV", Constants.VEDTAK_OM_AVVIST_KLAGE),
    KLAGE_OMGJORT("KGEOMG", "Vedtak om omgjøring av klage"),
    KLAGE_OVERSENDT("KGEOVE", "Klage oversendt til klageinstans"),
    ETTERLYS_INNTEKTSMELDING("ELYSIM", "Etterlys inntektsmelding"),
    ENDRING_UTBETALING("ENDUTB", "Endring i fordeling av ytelsen"), // Brukes som journalførSom-type, ikke egen brevmal. Gir tittel og kode til Joark ved overstyrt vedtaksbrev

    UDEFINERT(Fpsak.STANDARDKODE_UDEFINERT, null);

    private static final Set<DokumentMalType> VEDTAKSBREV = Set.of(ENGANGSSTØNAD_INNVILGELSE, ENGANGSSTØNAD_AVSLAG, FORELDREPENGER_INNVILGELSE,
        FORELDREPENGER_AVSLAG, FORELDREPENGER_OPPHØR, FORELDREPENGER_ANNULLERT, SVANGERSKAPSPENGER_INNVILGELSE, SVANGERSKAPSPENGER_AVSLAG,
        SVANGERSKAPSPENGER_OPPHØR);

    private static final Set<DokumentMalType> KLAGE_VEDTAKSBREV = Set.of(KLAGE_AVVIST, KLAGE_OMGJORT);


    public static boolean erVedtaksBrev(DokumentMalType brev) {
        return VEDTAKSBREV.contains(brev) || KLAGE_VEDTAKSBREV.contains(brev);
    }

    public static final Set<DokumentMalType> FORLENGET_SAKSBEHANDLINGSTID_BREVMALER = Set.of(DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID,
        DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_MEDL, DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_TIDLIG, FORLENGET_SAKSBEHANDLINGSTID_MEDL_FORUTGÅENDE);

    private final String kode;
    private final String navn;

    DokumentMalType(String kode, String navn) {
        this.kode = kode;
        this.navn = navn;
    }

    public String getNavn() {
        return navn;
    }

    public String getKode() {
        return kode;
    }

    private static class Constants {
        protected static final String VEDTAK_OM_AVVIST_KLAGE = "Vedtak om avvist klage";
    }
}

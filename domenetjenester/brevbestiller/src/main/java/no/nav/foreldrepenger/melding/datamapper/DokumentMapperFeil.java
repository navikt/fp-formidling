package no.nav.foreldrepenger.melding.datamapper;

import no.nav.foreldrepenger.melding.typer.JournalpostId;
import no.nav.vedtak.exception.TekniskException;


public class DokumentMapperFeil {

    public static TekniskException innhentDokumentasjonKreverGyldigBehandlingstype(String behandlingstype) {
        return new TekniskException("FPFORMIDLING-875839", String.format("Ugyldig behandlingstype %s for brev med malkode INNHEN.", behandlingstype));
    }

    public static TekniskException innsynskravSvarHarUkjentResultatType(String type) {
        return new TekniskException("FPFORMIDLING-729430", String.format("Ugyldig innsynsresultattype %s", type));
    }

    public static TekniskException ferdigstillingAvDokumentFeil(JournalpostId journalpostId, Exception e) {
        return new TekniskException("FPFORMIDLING-316712", String.format("Feil i ferdigstilling av dokument med journalpostId %s", journalpostId), e );
    }

    public static TekniskException knyttingAvVedleggFeil(String dokumentId, Exception e) {
        return new TekniskException("FPFORMIDLING-795245", String.format("Feil i knytting av vedlegg til dokument med id %s", dokumentId), e );
    }

    public static TekniskException henleggBehandlingBrevKreverGyldigBehandlingstype(String behandlingstype) {
        return new TekniskException("FPFORMIDLING-875835", String.format("Ugyldig behandlingstype %s for brev med malkode HENLEG", behandlingstype));
    }

    public static TekniskException manglerInfoOmLovhjemmelForAvslagsårsak(String avslagsårsakKode) {
        return new TekniskException("FPFORMIDLING-693339", String.format("Mangler informasjon om lovhjemmel for avslagsårsak med kode %s.", avslagsårsakKode));
    }

    public static TekniskException ingenOpphørsdatoVedPersonstatusDød() {
        return new TekniskException("FPFORMIDLING-724872", "Feil ved produksjon av opphørdokument: Klarte ikke utlede opphørsdato fra uttaksplanen. Påkrevd når personstatus = 'DØD'");
    }

    public static TekniskException ingenStartdatoVedPersonstatusDød() {
        return new TekniskException("FPFORMIDLING-743452", "Feil ved produksjon av opphørdokument: Klarte ikke utlede startdato fra det opprinnelige vedtaket. Påkrevd når personstatus = 'DØD'");
    }
}


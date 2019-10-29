package no.nav.foreldrepenger.melding.datamapper;

import no.nav.foreldrepenger.melding.typer.JournalpostId;
import no.nav.foreldrepenger.organisasjon.OrganisasjonIkkeFunnetException;
import no.nav.foreldrepenger.organisasjon.OrganisasjonUgyldigInputException;
import no.nav.tjeneste.virksomhet.organisasjon.v4.binding.HentOrganisasjonOrganisasjonIkkeFunnet;
import no.nav.tjeneste.virksomhet.organisasjon.v4.binding.HentOrganisasjonUgyldigInput;
import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.LogLevel;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.IntegrasjonFeil;
import no.nav.vedtak.feil.deklarasjon.TekniskFeil;

public interface DokumentMapperFeil extends DeklarerteFeil {
    DokumentMapperFeil FACTORY = FeilFactory.create(DokumentMapperFeil.class);

    @TekniskFeil(feilkode = "FPFORMIDLING-290951", feilmelding = "Brev med mal %s kan ikke sendes i denne behandlingen.", logLevel = LogLevel.ERROR)
    Feil brevmalIkkeTilgjengelig(String brevmalkode);

    @TekniskFeil(feilkode = "FPFORMIDLING-290952", feilmelding = "Brev med malkode INNHEN krever at fritekst ikke er tom.", logLevel = LogLevel.WARN)
    Feil innhentDokumentasjonKreverFritekst();

    @TekniskFeil(feilkode = "FPFORMIDLING-875839", feilmelding = "Ugyldig behandlingstype %s for bred med malkode INNHEN", logLevel = LogLevel.ERROR)
    Feil innhentDokumentasjonKreverGyldigBehandlingstype(String behandlingstype);

    @TekniskFeil(feilkode = "FPFORMIDLING-729430", feilmelding = "Ugyldig innsynsresultattype %s", logLevel = LogLevel.ERROR)
    Feil innsynskravSvarHarUkjentResultatType(String type);

    @TekniskFeil(feilkode = "FPFORMIDLING-782631", feilmelding = "Ugyldig vilkårtype %s", logLevel = LogLevel.ERROR)
    Feil behandlingHarUkjentVilkårType(String type);

    @TekniskFeil(feilkode = "FPFORMIDLING-316712", feilmelding = "Feil i ferdigstilling av dokument med journalpostId %s", logLevel = LogLevel.ERROR)
    Feil ferdigstillingAvDokumentFeil(JournalpostId journalpostId, Exception cause);

    @TekniskFeil(feilkode = "FPFORMIDLING-795245", feilmelding = "Feil i knytting av vedlegg til dokument med id %s", logLevel = LogLevel.ERROR)
    Feil knyttingAvVedleggFeil(String dokumentId, Exception cause);

    @TekniskFeil(feilkode = "FPFORMIDLING-875840", feilmelding = "Ugyldig behandlingstype %s for brev med malkode INNTID", logLevel = LogLevel.ERROR)
    Feil inntektsmeldingForTidligBrevKreverGyldigBehandlingstype(String behandlingstype);

    @TekniskFeil(feilkode = "FPFORMIDLING-875835", feilmelding = "Ugyldig behandlingstype %s for brev med malkode HENLEG", logLevel = LogLevel.ERROR)
    Feil HenleggBehandlingBrevKreverGyldigBehandlingstype(String behandlingstype);

    @TekniskFeil(feilkode = "FPFORMIDLING-672326", feilmelding = "Ingen brev avslagsårsak kode konfigurert for denne avslagsårsak kode %s.", logLevel = LogLevel.ERROR)
    Feil ingenBrevAvslagsårsakKodeKonfigurert(String avslagsårsakKode);

    @TekniskFeil(feilkode = "FPFORMIDLING-693339", feilmelding = "Mangler informasjon om lovhjemmel for avslagsårsak med kode %s.", logLevel = LogLevel.ERROR)
    Feil manglerInfoOmLovhjemmelForAvslagsårsak(String avslagsårsakKode);

    @TekniskFeil(feilkode = "FPFORMIDLING-724872", feilmelding = "Feil ved produksjon av opphørdokument: Klarte ikke utlede opphørsdato fra uttaksplanen. Påkrevd når personstatus = 'DØD'", logLevel = LogLevel.ERROR)
    Feil ingenOpphørsdatoVedPersonstatusDød();

    @TekniskFeil(feilkode = "FPFORMIDLING-743452", feilmelding = "Feil ved produksjon av opphørdokument: Klarte ikke utlede startdato fra det opprinnelige vedtaket. Påkrevd når personstatus = 'DØD", logLevel = LogLevel.ERROR)
    Feil ingenStartdatoVedPersonstatusDød();

    @IntegrasjonFeil(feilkode = "FP-254132", feilmelding = "Fant ikke organisasjon for orgNummer %s", logLevel = LogLevel.WARN, exceptionClass = OrganisasjonIkkeFunnetException.class)
    Feil organisasjonIkkeFunnet(String orgnr, HentOrganisasjonOrganisasjonIkkeFunnet årsak);

    @IntegrasjonFeil(feilkode= "FP-934726", feilmelding = "Funksjonell feil i grensesnitt mot %s, med orgnr %s", logLevel = LogLevel.WARN, exceptionClass = OrganisasjonUgyldigInputException.class)
    Feil ugyldigInput(String tjeneste, String orgnr, HentOrganisasjonUgyldigInput årsak);
}


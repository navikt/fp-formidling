package no.nav.foreldrepenger.melding.datamapper;

import no.nav.foreldrepenger.melding.typer.AktørId;
import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.LogLevel;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.FunksjonellFeil;
import no.nav.vedtak.feil.deklarasjon.TekniskFeil;

public interface DokumentBestillerFeil extends DeklarerteFeil {
    DokumentBestillerFeil FACTORY = FeilFactory.create(DokumentBestillerFeil.class);

    @FunksjonellFeil(feilkode = "FPFORMIDLING-151311", feilmelding = "Vedtaksbrev kan ikke lages for behandlingId %s, behandlingen mangler behandlingsresultat", logLevel = LogLevel.WARN, løsningsforslag = "Fortsett saksbehandlingen")
    Feil behandlingManglerResultat(Long behandlingId);

    @FunksjonellFeil(feilkode = "FPFORMIDLING-153212", feilmelding = "Vedtaksbrev kan ikke lages for behandlingId %s, behandlingen inneholder periode(r) som ikke er fastsatt. Forventes å være enten innvilget eller avslått på dette stadiet", løsningsforslag = "Fortsett saksbehandlingen")
    Feil behandlingInneholderIkkeFastsattPeriode(Long behandlingId);

    @FunksjonellFeil(feilkode = "FPFORMIDLING-212808", feilmelding = "Avslagsbrev kan ikke lages for behandlingId %s, behandlingen er ikke avslått", logLevel = LogLevel.WARN, løsningsforslag = "Fortsett saksbehandlingen")
    Feil behandlingIkkeAvslått(Long id);

    @TekniskFeil(feilkode = "FPFORMIDLING-151666", feilmelding = "Kan ikke bestille dokument for dokumentdata_id %s. Problemer ved generering av xml", logLevel = LogLevel.ERROR)
    Feil xmlgenereringsfeil(Long dokumentDataId, Exception cause);

    @TekniskFeil(feilkode = "FPFORMIDLING-103209", feilmelding = "Kan ikke bestille dokument for dokumentdata_id %s. Teknisk feil", logLevel = LogLevel.ERROR)
    Feil annentekniskfeil(Long dokumentDataId, Exception cause);

    @TekniskFeil(feilkode = "FPFORMIDLING-151337", feilmelding = "Kan ikke konvertere dato %s til xmlformatert dato.", logLevel = LogLevel.ERROR)
    Feil datokonverteringsfeil(String dokumentDataId, Exception cause);

    @TekniskFeil(feilkode = "FPFORMIDLING-151911", feilmelding = "Kan ikke produsere dokument på grunn av feil type.", logLevel = LogLevel.ERROR)
    Feil dokumentErAvFeilType(Exception cause);

    @TekniskFeil(feilkode = "FPFORMIDLING-220913", feilmelding = "Kan ikke produsere dokument, obligatorisk felt %s mangler innhold.", logLevel = LogLevel.ERROR)
    Feil feltManglerVerdi(String feltnavn);

    @TekniskFeil(feilkode = "FPFORMIDLING-350513", feilmelding = "Kan ikke produsere dokument, ukjent dokumenttype %s.", logLevel = LogLevel.ERROR)
    Feil ukjentDokumentType(String dokumentMalNavn);

    @TekniskFeil(feilkode = "FPFORMIDLING-210631", feilmelding = "Feilmelding fra DokProd for dokumentdata_id %s.", logLevel = LogLevel.ERROR)
    Feil feilFraDokumentProduksjon(Long dokumentDataId, Exception exception);

    @TekniskFeil(feilkode = "FPFORMIDLING-246979", feilmelding = "Finner ikke mottatt dato for søknad på behandling med id %s", logLevel = LogLevel.ERROR)
    Feil harIkkeSøknadMottattDato(Long behandlingId);

    @TekniskFeil(feilkode = "FPFORMIDLING-368280", feilmelding = "Klarte ikke matche beregningsresultatperiode og %S for brev", logLevel = LogLevel.ERROR)
    Feil kanIkkeMatchePerioder(String periode);

    @FunksjonellFeil(feilkode = "FPFORMIDLING-100507", feilmelding = "Klagebehandling med id %s mangler resultat av klagevurderingen", logLevel = LogLevel.WARN, løsningsforslag = "Fortsett saksbehandlingen")
    Feil behandlingManglerKlageVurderingResultat(Long behandlingId);

    @TekniskFeil(feilkode = "FPFORMIDLING-109013", feilmelding = "Fant ikke personinfo for aktørId: %s. Kan ikke bestille dokument", logLevel = LogLevel.WARN)
    Feil fantIkkeFnrForAktørId(AktørId aktørId);

    @TekniskFeil(feilkode = "FPFORMIDLING-119013", feilmelding = "Fant ikke personinfo for aktørId: %s. Kan ikke bestille dokument", logLevel = LogLevel.WARN)
    Feil fantIkkeAdresse(AktørId aktørId);
}

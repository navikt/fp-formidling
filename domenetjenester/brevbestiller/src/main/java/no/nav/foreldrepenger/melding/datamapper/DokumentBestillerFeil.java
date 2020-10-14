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

    @TekniskFeil(feilkode = "FPFORMIDLING-151666", feilmelding = "Kan ikke bestille dokument for behandling %s. Problemer ved generering av xml", logLevel = LogLevel.ERROR)
    Feil xmlgenereringsfeil(String behandlingUuid, Exception cause);

    @TekniskFeil(feilkode = "FPFORMIDLING-151337", feilmelding = "Kan ikke konvertere dato %s til xmlformatert dato.", logLevel = LogLevel.ERROR)
    Feil datokonverteringsfeil(String dokumentDataId, Exception cause);

    @TekniskFeil(feilkode = "FPFORMIDLING-220913", feilmelding = "Kan ikke produsere dokument, obligatorisk felt %s mangler innhold.", logLevel = LogLevel.ERROR)
    Feil feltManglerVerdi(String feltnavn);

    @TekniskFeil(feilkode = "FPFORMIDLING-368280", feilmelding = "Klarte ikke matche beregningsresultatperiode og %S for brev", logLevel = LogLevel.ERROR)
    Feil kanIkkeMatchePerioder(String periode);

    @FunksjonellFeil(feilkode = "FPFORMIDLING-100507", feilmelding = "Klagebehandling med id %s mangler resultat av klagevurderingen", logLevel = LogLevel.WARN, løsningsforslag = "Fortsett saksbehandlingen")
    Feil behandlingManglerKlageVurderingResultat(String behandlingUuid);

    @TekniskFeil(feilkode = "FPFORMIDLING-109013", feilmelding = "Fant ikke personinfo for aktørId: %s. Kan ikke bestille dokument", logLevel = LogLevel.WARN)
    Feil fantIkkeFnrForAktørId(AktørId aktørId);

    @TekniskFeil(feilkode = "FPFORMIDLING-119013", feilmelding = "Fant ikke personinfo for aktørId: %s. Kan ikke bestille dokument", logLevel = LogLevel.WARN)
    Feil fantIkkeAdresse(AktørId aktørId);

    @TekniskFeil(feilkode = "FPFORMIDLING-666915", feilmelding = "Ingen brevmal konfigurert for denne type behandlingen %s.", logLevel = LogLevel.WARN)
    Feil ingenBrevmalKonfigurert(String behandlingUuid);

    @TekniskFeil(feilkode = "FPFORMIDLING-666915", feilmelding = "Kjenner ikke igjen ytelse %s for behandling %s.", logLevel = LogLevel.ERROR)
    Feil kjennerIkkeYtelse(String ytelseType, String behandlingUuid);
}

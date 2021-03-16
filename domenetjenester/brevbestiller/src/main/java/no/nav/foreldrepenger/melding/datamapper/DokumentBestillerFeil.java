package no.nav.foreldrepenger.melding.datamapper;

import no.nav.foreldrepenger.melding.typer.AktørId;
import no.nav.vedtak.exception.FunksjonellException;
import no.nav.vedtak.exception.TekniskException;


public class DokumentBestillerFeil {

    public static TekniskException xmlgenereringsfeil(String behandlingUuid, Exception e) {
        return new TekniskException("FPFORMIDLING-151666", String.format("Kan ikke bestille dokument for behandling %s. Problemer ved generering av xml", behandlingUuid), e);
    }

    public static TekniskException datokonverteringsfeil(String dokumentDataId, Exception e) {
        return new TekniskException("FPFORMIDLING-151337", String.format("Kan ikke konvertere dato %s til xmlformatert dato.", dokumentDataId), e);
    }

    public static TekniskException feltManglerVerdi(String feltnavn, Exception e) {
        return new TekniskException("FPFORMIDLING-220913", String.format("Kan ikke produsere dokument, obligatorisk felt %s mangler innhold", feltnavn), e);
    }

    public static TekniskException kanIkkeMatchePerioder(String periode) {
        return new TekniskException("FPFORMIDLING-368280", String.format("Klarte ikke matche beregningsresultatperiode og %S for brev", periode));
    }

    public static FunksjonellException behandlingManglerKlageVurderingResultat(String behandlingUuid) {
        return new FunksjonellException("FPFORMIDLING-100507", String.format("Klagebehandling med id %s mangler resultat av klagevurderingen", behandlingUuid), "Fortsett saksbehandlingen" );
    }

    public static TekniskException fantIkkeFnrForAktørId(AktørId aktørId) {
        return new TekniskException("FPFORMIDLING-109013", String.format("Fant ikke fødselsnummer for aktørId: %s. Kan ikke bestille dokument", aktørId));
    }

    public static TekniskException fantIkkeAdresse(AktørId aktørId) {
        return new TekniskException("FPFORMIDLING-119013", String.format("Fant ikke adresse for aktørId: %s. Kan ikke bestille dokument", aktørId));
    }

    public static TekniskException ingenBrevmalKonfigurert(String behandlingUuid) {
        return new TekniskException("FPFORMIDLING-666915", String.format("Ingen brevmal konfigurert for denne type behandlingen %s.", behandlingUuid));
    }

    public static TekniskException kjennerIkkeYtelse(String ytelseType, String behandlingUuid) {
        return new TekniskException("FPFORMIDLING-666915", String.format("Kjenner ikke igjen ytelse %s for behandling %s.", ytelseType, behandlingUuid));
    }
}

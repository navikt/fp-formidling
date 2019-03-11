package no.nav.foreldrepenger.melding.datamapper;

import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.LogLevel;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.TekniskFeil;

public interface DokumentBestillerFeil extends DeklarerteFeil {
    DokumentBestillerFeil FACTORY = FeilFactory.create(DokumentBestillerFeil.class);

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
}

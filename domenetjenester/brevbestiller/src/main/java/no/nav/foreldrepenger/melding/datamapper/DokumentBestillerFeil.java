package no.nav.foreldrepenger.melding.datamapper;

import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.LogLevel;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.TekniskFeil;

public interface DokumentBestillerFeil extends DeklarerteFeil {
    DokumentBestillerFeil FACTORY = FeilFactory.create(DokumentBestillerFeil.class);

    @TekniskFeil(feilkode = "FP-151666", feilmelding = "Kan ikke bestille dokument for dokumentdata_id %s. Problemer ved generering av xml", logLevel = LogLevel.ERROR)
    Feil xmlgenereringsfeil(Long dokumentDataId, Exception cause);

    @TekniskFeil(feilkode = "FP-103209", feilmelding = "Kan ikke bestille dokument for dokumentdata_id %s. Teknisk feil", logLevel = LogLevel.ERROR)
    Feil annentekniskfeil(Long dokumentDataId, Exception cause);

    @TekniskFeil(feilkode = "FP-151337", feilmelding = "Kan ikke konvertere dato %s til xmlformatert dato.", logLevel = LogLevel.ERROR)
    Feil datokonverteringsfeil(String dokumentDataId, Exception cause);

    @TekniskFeil(feilkode = "FP-151911", feilmelding = "Kan ikke produsere dokument på grunn av feil type.", logLevel = LogLevel.ERROR)
    Feil dokumentErAvFeilType(Exception cause);

    @TekniskFeil(feilkode = "FP-220913", feilmelding = "Kan ikke produsere dokument, obligatorisk felt %s mangler innhold.", logLevel = LogLevel.ERROR)
    Feil feltManglerVerdi(String feltnavn);

    @TekniskFeil(feilkode = "FP-350513", feilmelding = "Kan ikke produsere dokument, ukjent dokumenttype %s.", logLevel = LogLevel.ERROR)
    Feil ukjentDokumentType(String dokumentMalNavn);

    @TekniskFeil(feilkode = "FP-210631", feilmelding = "Feilmelding fra DokProd for dokumentdata_id %s.", logLevel = LogLevel.ERROR)
    Feil feilFraDokumentProduksjon(Long dokumentDataId, Exception exception);
}

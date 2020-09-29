package no.nav.foreldrepenger.melding.brevbestiller;

import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.LogLevel;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.TekniskFeil;

public interface BrevbestillerFeil extends DeklarerteFeil {
    BrevbestillerFeil FACTORY = FeilFactory.create(BrevbestillerFeil.class);

    @TekniskFeil(feilkode = "FPFORMIDLING-221005", feilmelding = "Klarte ikke hente forhåndvise mal %s for behandling %s.", logLevel = LogLevel.ERROR)
    Feil klarteIkkeForhåndviseDokprodbrev(String dokumentMal, String behandlingId);

    @TekniskFeil(feilkode = "FPFORMIDLING-221006", feilmelding = "Klarte ikke hente forhåndvise mal %s for behandling %s.", logLevel = LogLevel.ERROR)
    Feil klarteIkkeForhåndvise(String dokumentMal, String behandlingId, Exception cause);

    @TekniskFeil(feilkode = "FPFORMIDLING-210631", feilmelding = "Feilmelding fra DokProd", logLevel = LogLevel.ERROR)
    Feil feilFraDokumentProduksjon(Exception exception);


}


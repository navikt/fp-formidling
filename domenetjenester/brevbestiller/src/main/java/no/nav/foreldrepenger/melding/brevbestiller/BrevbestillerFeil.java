package no.nav.foreldrepenger.melding.brevbestiller;

import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.LogLevel;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.TekniskFeil;

public interface BrevbestillerFeil extends DeklarerteFeil {
    BrevbestillerFeil FACTORY = FeilFactory.create(BrevbestillerFeil.class);

    @TekniskFeil(feilkode = "FPFORMIDLING-747342", feilmelding = "Klarte ikke hente informasjon om behandlingen %d.", logLevel = LogLevel.ERROR)
    Feil klarteIkkeHenteBehandling(Long behandlingId);

    @TekniskFeil(feilkode = "FPFORMIDLING-221005", feilmelding = "Klarte ikke hente forhåndvise mal %s for behandling %d.", logLevel = LogLevel.ERROR)
    Feil klarteIkkeForhåndvise(String dokumentMal, Long behandlingId);

    @TekniskFeil(feilkode = "FPFORMIDLING-210631", feilmelding = "Feilmelding fra DokProd", logLevel = LogLevel.ERROR)
    Feil feilFraDokumentProduksjon(Exception exception);


}


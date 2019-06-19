package no.nav.foreldrepenger.melding.kodeverk;

import javax.persistence.NoResultException;

import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.LogLevel;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.TekniskFeil;

interface KodeverkFeil extends DeklarerteFeil {

    KodeverkFeil FEILFACTORY = FeilFactory.create(KodeverkFeil.class);

    @TekniskFeil(feilkode = "FPFORMIDLING-314678", feilmelding = "Kan ikke finne kodeverk for type '%s', kode '%s'", logLevel = LogLevel.ERROR)
    Feil kanIkkeFinneKodeverk(String klassetype, String kode, NoResultException e);

    @TekniskFeil(feilkode = "FPFORMIDLING-314679", feilmelding = "Kan ikke finne kodeverk for type '%s', kode '%s'", logLevel = LogLevel.WARN)
    Feil kanIkkeFinneKodeverkForOffisiellKode(String klassetype, String kode, NoResultException e);

    @TekniskFeil(feilkode = "FPFORMIDLING-314680", feilmelding = "Kan ikke finne kodeverk for type '%s', kode '%s'", logLevel = LogLevel.WARN)
    Feil kanIkkeFinneKodeverkForKoder(String klassetype, String kode, NoResultException e);

}

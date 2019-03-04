package no.nav.foreldrepenger.melding.historikk;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "HistorikkAktør")
@DiscriminatorValue(HistorikkAktør.DISCRIMINATOR)
public class HistorikkAktør extends Kodeliste {

    public static final String DISCRIMINATOR = "HISTORIKK_AKTOER"; //$NON-NLS-1$
    public static final HistorikkAktør BESLUTTER = new HistorikkAktør("BESL"); //$NON-NLS-1$
    public static final HistorikkAktør SAKSBEHANDLER = new HistorikkAktør("SBH"); //$NON-NLS-1$
    public static final HistorikkAktør SØKER = new HistorikkAktør("SOKER"); //$NON-NLS-1$
    public static final HistorikkAktør ARBEIDSGIVER = new HistorikkAktør("ARBEIDSGIVER"); //$NON-NLS-1$
    public static final HistorikkAktør VEDTAKSLØSNINGEN = new HistorikkAktør("VL"); //$NON-NLS-1$
    public static final HistorikkAktør UDEFINERT = new HistorikkAktør("-"); //$NON-NLS-1$

    public HistorikkAktør() {
        //
    }

    public HistorikkAktør(String kode) {
        super(kode, DISCRIMINATOR);
    }

}

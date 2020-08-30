package no.nav.foreldrepenger.melding.behandling;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import no.nav.foreldrepenger.melding.kodeverk.UlagretKodeliste;

public class BehandlingÅrsakType extends UlagretKodeliste {

    public static final String DISCRIMINATOR = "BEHANDLING_AARSAK"; //$NON-NLS-1$
    public static final BehandlingÅrsakType RE_ENDRING_FRA_BRUKER = new BehandlingÅrsakType("RE-END-FRA-BRUKER"); //$NON-NLS-1$
    public static final BehandlingÅrsakType RE_ENDRET_INNTEKTSMELDING = new BehandlingÅrsakType("RE-END-INNTEKTSMELD"); //$NON-NLS-1$

    // Manuelt opprettet revurdering (obs: årsakene kan også bli satt på en automatisk opprettet revurdering)
    public static final BehandlingÅrsakType RE_KLAGE_UTEN_END_INNTEKT = new BehandlingÅrsakType("RE-KLAG-U-INNTK");
    public static final BehandlingÅrsakType RE_KLAGE_MED_END_INNTEKT = new BehandlingÅrsakType("RE-KLAG-M-INNTK");

    public static final BehandlingÅrsakType ETTER_KLAGE = new BehandlingÅrsakType("ETTER_KLAGE"); //$NON-NLS-1$

    public static final BehandlingÅrsakType RE_HENDELSE_FØDSEL = new BehandlingÅrsakType("RE-HENDELSE-FØDSEL"); //$NON-NLS-1$

    // For å håndtere automatiske informasjonsbrev
    public static final BehandlingÅrsakType INFOBREV_BEHANDLING = new BehandlingÅrsakType("INFOBREV_BEHANDLING");//$NON-NLS-1$
    public static final BehandlingÅrsakType INFOBREV_OPPHOLD = new BehandlingÅrsakType("INFOBREV_OPPHOLD");//$NON-NLS-1$
    public static final BehandlingÅrsakType UDEFINERT = new BehandlingÅrsakType("-"); //$NON-NLS-1$

    public BehandlingÅrsakType(String kode) {
        super(kode, DISCRIMINATOR);
    }

    public static Set<String> årsakerEtterKlageBehandling() {
        return new HashSet<>(Arrays.asList(ETTER_KLAGE.getKode(), RE_KLAGE_MED_END_INNTEKT.getKode(), RE_KLAGE_UTEN_END_INNTEKT.getKode()));
    }
}

package no.nav.foreldrepenger.melding.behandling;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import no.nav.foreldrepenger.melding.kodeverk.UlagretKodeliste;

public class BehandlingÅrsakType extends UlagretKodeliste {

    public static final String DISCRIMINATOR = "BEHANDLING_AARSAK"; //$NON-NLS-1$
    public static final BehandlingÅrsakType RE_MANGLER_FØDSEL = new BehandlingÅrsakType("RE-MF"); //$NON-NLS-1$
    public static final BehandlingÅrsakType RE_MANGLER_FØDSEL_I_PERIODE = new BehandlingÅrsakType("RE-MFIP"); //$NON-NLS-1$
    public static final BehandlingÅrsakType RE_AVVIK_ANTALL_BARN = new BehandlingÅrsakType("RE-AVAB"); //$NON-NLS-1$
    public static final BehandlingÅrsakType RE_FEIL_I_LOVANDVENDELSE = new BehandlingÅrsakType("RE-LOV"); //$NON-NLS-1$
    public static final BehandlingÅrsakType RE_FEIL_REGELVERKSFORSTÅELSE = new BehandlingÅrsakType("RE-RGLF"); //$NON-NLS-1$
    public static final BehandlingÅrsakType RE_FEIL_ELLER_ENDRET_FAKTA = new BehandlingÅrsakType("RE-FEFAKTA"); //$NON-NLS-1$
    public static final BehandlingÅrsakType RE_FEIL_PROSESSUELL = new BehandlingÅrsakType("RE-PRSSL"); //$NON-NLS-1$
    public static final BehandlingÅrsakType RE_ENDRING_FRA_BRUKER = new BehandlingÅrsakType("RE-END-FRA-BRUKER"); //$NON-NLS-1$
    public static final BehandlingÅrsakType RE_ENDRET_INNTEKTSMELDING = new BehandlingÅrsakType("RE-END-INNTEKTSMELD"); //$NON-NLS-1$
    public static final BehandlingÅrsakType BERØRT_BEHANDLING = new BehandlingÅrsakType("BERØRT-BEHANDLING"); //$NON-NLS-1$
    public static final BehandlingÅrsakType KØET_BEHANDLING = new BehandlingÅrsakType("KØET-BEHANDLING"); //$NON-NLS-1$
    public static final BehandlingÅrsakType RE_ANNET = new BehandlingÅrsakType("RE-ANNET"); //$NON-NLS-1$

    // Manuelt opprettet revurdering (obs: årsakene kan også bli satt på en automatisk opprettet revurdering)
    public static final BehandlingÅrsakType RE_KLAGE_UTEN_END_INNTEKT = new BehandlingÅrsakType("RE-KLAG-U-INNTK");
    public static final BehandlingÅrsakType RE_KLAGE_MED_END_INNTEKT = new BehandlingÅrsakType("RE-KLAG-M-INNTK");
    public static final BehandlingÅrsakType RE_OPPLYSNINGER_OM_MEDLEMSKAP = new BehandlingÅrsakType("RE-MDL");
    public static final BehandlingÅrsakType RE_OPPLYSNINGER_OM_OPPTJENING = new BehandlingÅrsakType("RE-OPTJ");
    public static final BehandlingÅrsakType RE_OPPLYSNINGER_OM_FORDELING = new BehandlingÅrsakType("RE-FRDLING");
    public static final BehandlingÅrsakType RE_OPPLYSNINGER_OM_INNTEKT = new BehandlingÅrsakType("RE-INNTK");
    public static final BehandlingÅrsakType RE_OPPLYSNINGER_OM_DØD = new BehandlingÅrsakType("RE-DØD");
    public static final BehandlingÅrsakType RE_OPPLYSNINGER_OM_SØKERS_REL = new BehandlingÅrsakType("RE-SRTB");
    public static final BehandlingÅrsakType RE_OPPLYSNINGER_OM_SØKNAD_FRIST = new BehandlingÅrsakType("RE-FRIST");
    public static final BehandlingÅrsakType RE_OPPLYSNINGER_OM_BEREGNINGSGRUNNLAG = new BehandlingÅrsakType("RE-BER-GRUN");

    public static final BehandlingÅrsakType ETTER_KLAGE = new BehandlingÅrsakType("ETTER_KLAGE"); //$NON-NLS-1$

    public static final BehandlingÅrsakType RE_HENDELSE_FØDSEL = new BehandlingÅrsakType("RE-HENDELSE-FØDSEL"); //$NON-NLS-1$
    public static final BehandlingÅrsakType RE_HENDELSE_DØD_FORELDER = new BehandlingÅrsakType("RE-HENDELSE-DØD-F"); //$NON-NLS-1$
    public static final BehandlingÅrsakType RE_HENDELSE_DØD_BARN = new BehandlingÅrsakType("RE-HENDELSE-DØD-B"); //$NON-NLS-1$

    public static final BehandlingÅrsakType RE_REGISTEROPPLYSNING = new BehandlingÅrsakType("RE-REGISTEROPPL"); //$NON-NLS-1$
    public static final BehandlingÅrsakType RE_OPPLYSNINGER_OM_YTELSER = new BehandlingÅrsakType("RE-YTELSE"); //$NON-NLS-1$
    public static final BehandlingÅrsakType RE_TILSTØTENDE_YTELSE_INNVILGET = new BehandlingÅrsakType("RE-TILST-YT-INNVIL"); //$NON-NLS-1$
    public static final BehandlingÅrsakType RE_ENDRING_BEREGNINGSGRUNNLAG = new BehandlingÅrsakType("RE-ENDR-BER-GRUN"); //$NON-NLS-1$
    public static final BehandlingÅrsakType RE_TILSTØTENDE_YTELSE_OPPHØRT = new BehandlingÅrsakType("RE-TILST-YT-OPPH");//$NON-NLS-1$
    // For å håndtere automatiske informasjonsbrev
    public static final BehandlingÅrsakType INFOBREV_BEHANDLING = new BehandlingÅrsakType("INFOBREV_BEHANDLING");//$NON-NLS-1$
    public static final BehandlingÅrsakType INFOBREV_OPPHOLD = new BehandlingÅrsakType("INFOBREV_OPPHOLD");//$NON-NLS-1$
    public static final BehandlingÅrsakType UDEFINERT = new BehandlingÅrsakType("-"); //$NON-NLS-1$

    public BehandlingÅrsakType(String kode) {
        super(kode, DISCRIMINATOR);
    }

    public static Set<String> årsakerForAutomatiskRevurdering() {
        return new HashSet<>(Arrays.asList(RE_MANGLER_FØDSEL.getKode(), RE_MANGLER_FØDSEL_I_PERIODE.getKode(), RE_AVVIK_ANTALL_BARN.getKode(),
                RE_TILSTØTENDE_YTELSE_INNVILGET.getKode(), RE_ENDRING_BEREGNINGSGRUNNLAG.getKode(), RE_TILSTØTENDE_YTELSE_OPPHØRT.getKode()));
    }

    public static Set<String> årsakerEtterKlageBehandling() {
        return new HashSet<>(Arrays.asList(ETTER_KLAGE.getKode(), RE_KLAGE_MED_END_INNTEKT.getKode(), RE_KLAGE_UTEN_END_INNTEKT.getKode()));
    }
}

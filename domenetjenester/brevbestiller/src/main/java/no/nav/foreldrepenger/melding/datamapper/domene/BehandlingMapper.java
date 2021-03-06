package no.nav.foreldrepenger.melding.datamapper.domene;

import static no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter.ENDRINGSSØKNAD;
import static no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter.FØRSTEGANGSSØKNAD;
import static no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter.REVURDERING;
import static no.nav.foreldrepenger.melding.datamapper.mal.BehandlingTypeKonstanter.SØKNAD;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.BehandlingsTypeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.BehandlingsResultatKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.BehandlingsTypeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.KonsekvensForYtelseKode;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingÅrsakType;

public class BehandlingMapper {

    public static final String ENDRING_BEREGNING_OG_UTTAK = "ENDRING_BEREGNING_OG_UTTAK";
    private static Map<String, KonsekvensForYtelseKode> konsekvensForYtelseKodeMap = new HashMap<>();

    static {
        konsekvensForYtelseKodeMap.put(ENDRING_BEREGNING_OG_UTTAK, KonsekvensForYtelseKode.ENDRING_I_BEREGNING_OG_UTTAK);
        konsekvensForYtelseKodeMap.put(KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode(), KonsekvensForYtelseKode.ENDRING_I_BEREGNING);
        konsekvensForYtelseKodeMap.put(KonsekvensForYtelsen.INGEN_ENDRING.getKode(), KonsekvensForYtelseKode.INGEN_ENDRING);
        konsekvensForYtelseKodeMap.put(KonsekvensForYtelsen.ENDRING_I_UTTAK.getKode(), KonsekvensForYtelseKode.ENDRING_I_UTTAK);
        konsekvensForYtelseKodeMap.put(KonsekvensForYtelsen.FORELDREPENGER_OPPHØRER.getKode(), KonsekvensForYtelseKode.FORELDREPENGER_OPPHØRER);
    }

    public static Optional<String> avklarFritekst(DokumentHendelse dokumentHendelse, Behandling behandling) {
        if (dokumentHendelse.getFritekst() != null && !dokumentHendelse.getFritekst().isEmpty()) {
            return Optional.of(dokumentHendelse.getFritekst());
        } else if (behandling.getBehandlingsresultat() != null) {
            return Optional.ofNullable(behandling.getBehandlingsresultat().getAvslagarsakFritekst());
        }
        return Optional.empty();
    }

    public static int finnAntallUkerBehandlingsfrist(BehandlingType behandlingType) {
        return behandlingType.getBehandlingstidFristUker();
    }

    public static String finnBehandlingTypeForDokument(Behandling behandling) {
        return gjelderEndringsøknad(behandling) ?
                ENDRINGSSØKNAD :
                behandling.getBehandlingType().getKode();
        }


    public static boolean gjelderEndringsøknad(Behandling behandling) {
        // endringssøknad kan være satt ved førstegangsbehandling og henleggelse pga håndtering av tapte dager
        return behandling.erRevurdering() && behandling.harBehandlingÅrsak(BehandlingÅrsakType.RE_ENDRING_FRA_BRUKER);
    }

    public static boolean erTermindatoEndret(FamilieHendelse familieHendelse, Optional<FamilieHendelse> originalFamiliehendelse) {
        if (originalFamiliehendelse.isEmpty()) {
            return false;
        }
        return !originalFamiliehendelse.get().getTermindato().equals(familieHendelse.getTermindato());
    }


    public static Boolean erEndretFraAvslått(Optional<Behandling> orginalBehandling) {
        return orginalBehandling.map(forrigeBehandling -> forrigeBehandling.getBehandlingsresultat().erAvslått())
                .orElse(false);
    }

    public static boolean erRevurderingPgaEndretBeregningsgrunnlag(Behandling revurdering) {
        List<KonsekvensForYtelsen> konsekvenserForYtelsen = revurdering.getBehandlingsresultat().getKonsekvenserForYtelsen();
        boolean kunEndringIBeregning = konsekvenserForYtelsen.contains(KonsekvensForYtelsen.ENDRING_I_BEREGNING) &&
                konsekvenserForYtelsen.size() == 1;

        return kunEndringIBeregning;
    }

    public static boolean erRevurderingPgaFødselshendelse(Behandling behandling, FamilieHendelse familieHendelse, Optional<FamilieHendelse> originalFamiliehendelse) {
        return behandling.harBehandlingÅrsak(BehandlingÅrsakType.RE_HENDELSE_FØDSEL) ||
                familieHendelse.isBarnErFødt() && originalFamiliehendelse.map(fh -> !fh.isBarnErFødt()).orElse(false);
    }

    public static BehandlingsTypeKode utledBehandlingsTypeInnvilgetFP(Behandling behandling) {
        return BehandlingType.REVURDERING.equals(behandling.getBehandlingType()) ?
                BehandlingsTypeKode.REVURDERING : BehandlingsTypeKode.FOERSTEGANGSBEHANDLING;
    }

    public static BehandlingsTypeType utledBehandlingsTypeInnvilgetES(Behandling behandling) {
        boolean etterKlage = BehandlingÅrsakType.årsakerEtterKlageBehandling().stream().anyMatch(behandling::harBehandlingÅrsak);
        if (etterKlage) {
            return BehandlingsTypeType.MEDHOLD;
        }
        return BehandlingType.REVURDERING.equals(behandling.getBehandlingType()) ? BehandlingsTypeType.REVURDERING : BehandlingsTypeType.FOERSTEGANGSBEHANDLING;
    }

    public static boolean erMedhold(Behandling behandling) {
        return BehandlingÅrsakType.årsakerEtterKlageBehandling().stream().anyMatch(behandling::harBehandlingÅrsak);
    }

    public static String utledBehandlingsTypeForAvslagVedtak(Behandling behandling, DokumentHendelse dokumentHendelse) {
        if (behandling.erRevurdering()) {
            return REVURDERING;
        } else if (behandling.erFørstegangssøknad() && dokumentHendelse.getYtelseType().gjelderForeldrepenger()) {
            return FØRSTEGANGSSØKNAD;
        }
        return SØKNAD;
    }

    public static boolean erEndringMedEndretInntektsmelding(Behandling behandling) {
        return erEndring(behandling.getBehandlingType())
                && behandling.harBehandlingÅrsak(BehandlingÅrsakType.RE_ENDRET_INNTEKTSMELDING);
    }

    private static boolean erEndring(BehandlingType behandlingType) {
        return BehandlingType.REVURDERING.equals(behandlingType)
                || BehandlingType.KLAGE.equals(behandlingType);
    }

    public static BehandlingsResultatKode tilBehandlingsResultatKode(BehandlingResultatType behandlingsresultatkode) {
        if (BehandlingResultatType.INNVILGET.equals(behandlingsresultatkode)) {
            return BehandlingsResultatKode.INNVILGET;
        } else if (BehandlingResultatType.AVSLÅTT.equals(behandlingsresultatkode)) {
            return BehandlingsResultatKode.AVSLÅTT;
        } else if (BehandlingResultatType.OPPHØR.equals(behandlingsresultatkode)) {
            return BehandlingsResultatKode.OPPHØR;
        } else if (BehandlingResultatType.FORELDREPENGER_ENDRET.equals(behandlingsresultatkode)) {
            return BehandlingsResultatKode.FORELDREPENGER_ENDRET;
        } else {
            return BehandlingsResultatKode.INGEN_ENDRING;
        }
    }

    public static KonsekvensForYtelseKode finnKonsekvensForYtelseKode(String konsekvens) {
        if (konsekvensForYtelseKodeMap.containsKey(konsekvens)) {
            return konsekvensForYtelseKodeMap.get(konsekvens);
        }
        return KonsekvensForYtelseKode.INGEN_ENDRING;
    }

    public static String kodeFra(List<KonsekvensForYtelsen> konsekvenserForYtelsen) {
        if (konsekvenserForYtelsen.contains(KonsekvensForYtelsen.ENDRING_I_BEREGNING)) { // viktigst å få med endring i beregning
            return konsekvenserForYtelsen.contains(KonsekvensForYtelsen.ENDRING_I_UTTAK) ?
                    ENDRING_BEREGNING_OG_UTTAK : KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode();
        } else {
            return konsekvenserForYtelsen.isEmpty() ?
                    KonsekvensForYtelsen.UDEFINERT.getKode() : konsekvenserForYtelsen.get(0).getKode(); // velger bare den første i listen (finnes ikke koder for andre ev. kombinasjoner)
        }
    }
}

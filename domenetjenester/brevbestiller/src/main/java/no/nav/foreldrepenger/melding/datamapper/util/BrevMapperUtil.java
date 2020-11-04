package no.nav.foreldrepenger.melding.datamapper.util;

import static java.util.Collections.emptyList;

import java.time.LocalDate;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.integrasjon.dokument.forlenget.PersonstatusKode;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingÅrsakType;

@ApplicationScoped
public class BrevMapperUtil {
    private BrevParametere brevParametere;

    BrevMapperUtil() {
        // CDI
    }

    @Inject
    public BrevMapperUtil(BrevParametere brevParametere) {
        this.brevParametere = brevParametere;
    }

    public LocalDate getSvarFrist() {
        return LocalDate.now().plusDays(brevParametere.getSvarfristDager());
    }

    public static List<String> konverterFritekstTilListe(String fritekst) {
        if (fritekst == null) {
            return emptyList();
        }
        return List.of(fritekst.split("\n"));
    }

    public static String formaterPersonnummer(String personnummer) {
        if (personnummer != null && personnummer.length() == 11) {
            StringBuilder formatertPersonnummer = new StringBuilder(personnummer);
            formatertPersonnummer.insert(6, " ");
            return formatertPersonnummer.toString();
        }
        return personnummer;
    }

    public static String formaterBeløp(Long beløp) {
        return String.format("%,d", beløp);
    }

    public static boolean erKopi(DokumentFelles.Kopi kopi) {
        return DokumentFelles.Kopi.JA.equals(kopi);
    }

    public static boolean erEndringssøknad(Behandling behandling) {
        return behandling.erRevurdering() && behandling.harBehandlingÅrsak(BehandlingÅrsakType.RE_ENDRING_FRA_BRUKER);
    }

    public static boolean erDød(DokumentFelles dokumentFelles) {
        return PersonstatusKode.DOD.toString().equalsIgnoreCase(dokumentFelles.getSakspartPersonStatus());
    }

    public static boolean brevSendesTilVerge(DokumentFelles dokumentFelles) {
        // Hvis brevet sendes til verge skal verges navn vises i brevet
        return !dokumentFelles.getMottakerId().equals(dokumentFelles.getSakspartId());
    }
}

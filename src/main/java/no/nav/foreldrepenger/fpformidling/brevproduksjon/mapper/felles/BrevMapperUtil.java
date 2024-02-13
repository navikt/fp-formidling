package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles;

import java.time.LocalDate;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FellesDokumentdata;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingÅrsakType;

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

    public static String formaterPersonnummer(String personnummer) {
        if (personnummer != null && personnummer.length() == 11) {
            var formatertPersonnummer = new StringBuilder(personnummer);
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
        return DokumentFelles.PersonStatus.DOD.equals(dokumentFelles.getSakspartPersonStatus());
    }

    public static boolean brevSendesTilVerge(DokumentFelles dokumentFelles) {
        // Hvis brevet sendes til verge skal verges navn vises i brevet
        return !dokumentFelles.getMottakerId().equals(dokumentFelles.getSakspartId());
    }

    public static FellesDokumentdata.Builder opprettFellesBuilder(DokumentFelles dokumentFelles, Behandling behandling,
                                                                  boolean erUtkast) {
        var erKopi = dokumentFelles.getErKopi();
        var fellesBuilder = FellesDokumentdata.ny()
            .medSøkerNavn(dokumentFelles.getSakspartNavn())
            .medSøkerPersonnummer(formaterPersonnummer(dokumentFelles.getSakspartId()))
            .medErKopi(erKopi.isPresent() && erKopi(erKopi.get()))
            .medHarVerge(erKopi.isPresent())
            .medSaksnummer(dokumentFelles.getSaksnummer().getVerdi())
            .medYtelseType(behandling.getFagsakBackend().getYtelseType().getKode())
            .medErUtkast(erUtkast);

        if (brevSendesTilVerge(dokumentFelles)) {
            fellesBuilder.medMottakerNavn(dokumentFelles.getMottakerNavn());
        }

        return fellesBuilder;
    }
}

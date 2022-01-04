package no.nav.foreldrepenger.fpformidling.datamapper.util;

import static no.nav.foreldrepenger.fpformidling.brevbestiller.impl.DokumentFellesDataMapper.DOD_PERSON_STATUS;

import java.time.LocalDate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
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
        return DOD_PERSON_STATUS.equalsIgnoreCase(dokumentFelles.getSakspartPersonStatus());
    }

    public static boolean brevSendesTilVerge(DokumentFelles dokumentFelles) {
        // Hvis brevet sendes til verge skal verges navn vises i brevet
        return !dokumentFelles.getMottakerId().equals(dokumentFelles.getSakspartId());
    }

    public static FellesDokumentdata.Builder opprettFellesBuilder(DokumentFelles dokumentFelles,
                                                                  DokumentHendelse dokumentHendelse,
                                                                  Behandling behandling,
                                                                  boolean erUtkast) {
        FellesDokumentdata.Builder fellesBuilder = FellesDokumentdata.ny()
                .medSøkerNavn(dokumentFelles.getSakspartNavn())
                .medSøkerPersonnummer(formaterPersonnummer(dokumentFelles.getSakspartId()))
                .medErKopi(dokumentFelles.getErKopi() != null && dokumentFelles.getErKopi().isPresent() && erKopi(dokumentFelles.getErKopi().get()))
                .medHarVerge(dokumentFelles.getErKopi() != null && dokumentFelles.getErKopi().isPresent())
                .medSaksnummer(dokumentFelles.getSaksnummer().getVerdi())
                .medYtelseType(dokumentHendelse.getYtelseType().getKode())
                .medBehandlesAvKA(behandlesAvKlageinstans(dokumentHendelse, behandling))
                .medErUtkast(erUtkast);

        if (brevSendesTilVerge(dokumentFelles)) {
            fellesBuilder.medMottakerNavn(dokumentFelles.getMottakerNavn());
        }

        return fellesBuilder;
    }

    private static boolean behandlesAvKlageinstans(DokumentHendelse hendelse, Behandling behandling) {
        // Behandlende enhet vil være angitt på DokumentHendelse ved bestilling av brev,
        // og dette skal overstyre behandlende enhet på Behandling, da denne kan ha endret seg
        // siden brevet ble bestilt. Ved forhåndsvisning må det hentes fra Behandling.
        return (hendelse.getBehandlendeEnhetNavn() != null && hendelse.behandlesAvKlageinstans())
                || (hendelse.getBehandlendeEnhetNavn() == null && behandling.getBehandlendeEnhetNavn() != null && behandling.behandlesAvKlageinstans());
    }
}

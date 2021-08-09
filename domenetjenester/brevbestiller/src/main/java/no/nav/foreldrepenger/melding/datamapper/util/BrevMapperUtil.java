package no.nav.foreldrepenger.melding.datamapper.util;

import java.time.LocalDate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.FellesDokumentdata;
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

    public static String ivaretaLinjeskiftIFritekst(String fritekst) {
        if (fritekst != null && !fritekst.equals("")) {
            String[] fritekstLinjer = fritekst.split("\n");
            if (fritekstLinjer.length > 1) {
                StringBuilder resultat = new StringBuilder();
                resultat.append(fritekstLinjer[0]);
                boolean sisteVarPunktliste = false;
                for (int i=1; i < fritekstLinjer.length; i++) {
                    resultat.append("\n");
                    String linje = fritekstLinjer[i];
                    if (linje.startsWith("-")) {
                        resultat.append(linje);
                        sisteVarPunktliste = true;
                    } else {
                        resultat.append("\n"); //Ekstra linjeskift må inn for at det ikke skal ignoreres i Dokgen...
                        resultat.append(linje);
                        sisteVarPunktliste = false;
                    }
                }
                if (!sisteVarPunktliste) {
                    resultat.append("\n"); //Uten ekstra linjeskift her kommer neste overskrift for nærme...
                }
                return resultat.toString();
            } else if (fritekstLinjer.length == 1) {
                if (fritekstLinjer[0].startsWith("-")) {
                    return fritekstLinjer[0];
                } else {
                    return fritekstLinjer[0] + "\n";
                }
            }
        }
        return fritekst;
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

    public static FellesDokumentdata.Builder opprettFellesDokumentdataBuilder(DokumentFelles dokumentFelles,
                                                                              DokumentHendelse dokumentHendelse,
                                                                              Behandling behandling) {
        FellesDokumentdata.Builder fellesBuilder = FellesDokumentdata.ny()
                .medSøkerNavn(dokumentFelles.getSakspartNavn())
                .medSøkerPersonnummer(formaterPersonnummer(dokumentFelles.getSakspartId()))
                .medErKopi(dokumentFelles.getErKopi() != null && dokumentFelles.getErKopi().isPresent() && erKopi(dokumentFelles.getErKopi().get()))
                .medHarVerge(dokumentFelles.getErKopi() != null && dokumentFelles.getErKopi().isPresent())
                .medSaksnummer(dokumentFelles.getSaksnummer().getVerdi())
                .medYtelseType(dokumentHendelse.getYtelseType().getKode())
                .medBehandlesAvKA(behandlesAvKlageinstans(dokumentHendelse, behandling));

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

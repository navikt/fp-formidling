package no.nav.foreldrepenger.melding.dokumentdata;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "DokumentMalRestriksjon")
@DiscriminatorValue(DokumentMalRestriksjon.DISCRIMINATOR)
public class DokumentMalRestriksjon extends Kodeliste {

    public static final String DISCRIMINATOR = "DOKUMENT_MAL_RESTRIKSJON";
    public static final DokumentMalRestriksjon INGEN = new DokumentMalRestriksjon("INGEN");
    public static final DokumentMalRestriksjon REVURDERING = new DokumentMalRestriksjon("REVURDERING");
    public static final DokumentMalRestriksjon ÅPEN_BEHANDLING = new DokumentMalRestriksjon("ÅPEN_BEHANDLING");
    public static final DokumentMalRestriksjon ÅPEN_BEHANDLING_IKKE_SENDT = new DokumentMalRestriksjon("ÅPEN_BEHANDLING_IKKE_SENDT");

    DokumentMalRestriksjon() {
        // Hibernate trenger default konstruktør
    }

    private DokumentMalRestriksjon(String kode) {
        super(kode, DISCRIMINATOR);
    }

}

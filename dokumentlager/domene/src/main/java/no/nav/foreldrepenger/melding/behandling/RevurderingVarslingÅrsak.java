package no.nav.foreldrepenger.melding.behandling;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;

@Entity(name = "RevurderingVarslingÅrsak")
@DiscriminatorValue(RevurderingVarslingÅrsak.DISCRIMINATOR)
public class RevurderingVarslingÅrsak extends Kodeliste {

    public static final String DISCRIMINATOR = "REVURDERING_VARSLING_AARSAK";

    public static final RevurderingVarslingÅrsak BARN_IKKE_REGISTRERT_FOLKEREGISTER = new RevurderingVarslingÅrsak("BARNIKKEREG");
    public static final RevurderingVarslingÅrsak ARBEIDS_I_STØNADSPERIODEN = new RevurderingVarslingÅrsak("JOBBFULLTID");
    public static final RevurderingVarslingÅrsak BEREGNINGSGRUNNLAG_UNDER_HALV_G = new RevurderingVarslingÅrsak("IKKEOPPTJENT");
    public static final RevurderingVarslingÅrsak BRUKER_REGISTRERT_UTVANDRET = new RevurderingVarslingÅrsak("UTVANDRET");
    public static final RevurderingVarslingÅrsak ARBEID_I_UTLANDET = new RevurderingVarslingÅrsak("JOBBUTLAND");
    public static final RevurderingVarslingÅrsak IKKE_LOVLIG_OPPHOLD = new RevurderingVarslingÅrsak("IKKEOPPHOLD");
    public static final RevurderingVarslingÅrsak OPPTJENING_IKKE_OPPFYLT = new RevurderingVarslingÅrsak("JOBB6MND");
    public static final RevurderingVarslingÅrsak MOR_AKTIVITET_IKKE_OPPFYLT = new RevurderingVarslingÅrsak("AKTIVITET");
    public static final RevurderingVarslingÅrsak ANNET = new RevurderingVarslingÅrsak("ANNET");

    public static final RevurderingVarslingÅrsak UDEFINERT = new RevurderingVarslingÅrsak("-"); //$NON-NLS-1$

    RevurderingVarslingÅrsak() {
        //hibernate trenger en
    }

    private RevurderingVarslingÅrsak(String kode) {
        super(kode, DISCRIMINATOR);
    }

}

package no.nav.foreldrepenger.melding.uttak.svp;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import no.nav.foreldrepenger.melding.behandling.ÅrsakMedLovReferanse;
import no.nav.foreldrepenger.melding.kodeverk.Kodeliste;


@Entity(name = "ArbeidsforholdIkkeOppfyltÅrsak")
@DiscriminatorValue(ArbeidsforholdIkkeOppfyltÅrsak.DISCRIMINATOR)
public class ArbeidsforholdIkkeOppfyltÅrsak extends Kodeliste implements ÅrsakMedLovReferanse {

    public static final String DISCRIMINATOR = "SVP_ARBEIDSFORHOLD_IKKE_OPPFYLT_AARSAK";

    public static final ArbeidsforholdIkkeOppfyltÅrsak INGEN = new ArbeidsforholdIkkeOppfyltÅrsak("-");
    public static final ArbeidsforholdIkkeOppfyltÅrsak HELE_UTTAKET_ER_ETTER_3_UKER_FØR_TERMINDATO = new ArbeidsforholdIkkeOppfyltÅrsak("8301");
    public static final ArbeidsforholdIkkeOppfyltÅrsak UTTAK_KUN_PÅ_HELG = new ArbeidsforholdIkkeOppfyltÅrsak("8302");
    public static final ArbeidsforholdIkkeOppfyltÅrsak ARBEIDSGIVER_KAN_TILRETTELEGGE = new ArbeidsforholdIkkeOppfyltÅrsak("8303");
    public static final ArbeidsforholdIkkeOppfyltÅrsak ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN = new ArbeidsforholdIkkeOppfyltÅrsak("8312");

    ArbeidsforholdIkkeOppfyltÅrsak() {
        //For Hibernate
    }

    ArbeidsforholdIkkeOppfyltÅrsak(String kode) {
        super(kode, DISCRIMINATOR);
    }

    @Override
    public String getLovHjemmelData() {
        return getEkstraData();
    }

}

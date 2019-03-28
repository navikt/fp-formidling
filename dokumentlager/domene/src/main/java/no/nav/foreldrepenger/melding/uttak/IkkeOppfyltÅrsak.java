package no.nav.foreldrepenger.melding.uttak;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "IkkeOppfyltÅrsak")
@DiscriminatorValue(IkkeOppfyltÅrsak.DISCRIMINATOR)
public class IkkeOppfyltÅrsak extends PeriodeResultatÅrsak {
    public static final String DISCRIMINATOR = "IKKE_OPPFYLT_AARSAK";

    public static final IkkeOppfyltÅrsak UKJENT = new IkkeOppfyltÅrsak("-");
    // Uttak årsaker

    public static final IkkeOppfyltÅrsak HULL_MELLOM_FORELDRENES_PERIODER = new IkkeOppfyltÅrsak("4005");
    public static final IkkeOppfyltÅrsak IKKE_STØNADSDAGER_IGJEN = new IkkeOppfyltÅrsak("4002");
    public static final IkkeOppfyltÅrsak SØKNADSFRIST = new IkkeOppfyltÅrsak("4020");
    public static final IkkeOppfyltÅrsak BARN_OVER_3_ÅR = new IkkeOppfyltÅrsak("4022");
    public static final IkkeOppfyltÅrsak ARBEIDER_I_UTTAKSPERIODEN_MER_ENN_0_PROSENT = new IkkeOppfyltÅrsak("4023");
    public static final IkkeOppfyltÅrsak AVSLAG_GRADERING_ARBEIDER_100_PROSENT_ELLER_MER = new IkkeOppfyltÅrsak("4025");
    public static final IkkeOppfyltÅrsak AVSLAG_GRADERING_SØKER_ER_IKKE_I_ARBEID = new IkkeOppfyltÅrsak("4093");
    public static final IkkeOppfyltÅrsak FAR_HAR_IKKE_OMSORG = new IkkeOppfyltÅrsak("4012");
    public static final IkkeOppfyltÅrsak MOR_HAR_IKKE_OMSORG = new IkkeOppfyltÅrsak("4003");
    public static final IkkeOppfyltÅrsak MOR_SØKER_FELLESPERIODE_FØR_12_UKER_FØR_TERMIN_FØDSEL = new IkkeOppfyltÅrsak("4013");
    public static final IkkeOppfyltÅrsak SAMTIDIG_UTTAK_IKKE_GYLDIG_KOMBINASJON = new IkkeOppfyltÅrsak("4060");
    public static final IkkeOppfyltÅrsak MOR_IKKE_RETT_TIL_FORELDREPENGER = new IkkeOppfyltÅrsak("4073");
    public static final IkkeOppfyltÅrsak BARNET_ER_DØD = new IkkeOppfyltÅrsak("4072");
    public static final IkkeOppfyltÅrsak SØKER_ER_DØD = new IkkeOppfyltÅrsak("4071");
    public static final IkkeOppfyltÅrsak DEN_ANDRE_PART_OVERLAPPENDE_UTTAK_IKKE_SØKT_INNVILGET_SAMTIDIT_UTTAK = new IkkeOppfyltÅrsak("4084");
    public static final IkkeOppfyltÅrsak IKKE_SAMTYKKE_MELLOM_PARTENE = new IkkeOppfyltÅrsak("4085");
    public static final IkkeOppfyltÅrsak DEN_ANDRE_PART_HAR_OVERLAPPENDE_UTTAKSPERIODER_SOM_ER_INNVILGET_UTSETTELSE = new IkkeOppfyltÅrsak("4086");
    public static final IkkeOppfyltÅrsak HULL_MELLOM_SØKNADSPERIODE_ETTER_SISTE_UTTAK = new IkkeOppfyltÅrsak("4090");
    public static final IkkeOppfyltÅrsak MOR_TAR_IKKE_ALLE_UKENE = new IkkeOppfyltÅrsak("4095");
    //Vilkår
    public static final IkkeOppfyltÅrsak OPPHØR_MEDLEMSKAP = new IkkeOppfyltÅrsak("4087");
    public static final IkkeOppfyltÅrsak FØDSELSVILKÅRET_IKKE_OPPFYLT = new IkkeOppfyltÅrsak("4096");
    public static final IkkeOppfyltÅrsak ADOPSJONSVILKÅRET_IKKE_OPPFYLT = new IkkeOppfyltÅrsak("4097");
    public static final IkkeOppfyltÅrsak FORELDREANSVARSVILKÅRET_IKKE_OPPFYLT = new IkkeOppfyltÅrsak("4098");
    public static final IkkeOppfyltÅrsak OPPTJENINGSVILKÅRET_IKKE_OPPFYLT = new IkkeOppfyltÅrsak("4099");
    // Aktivitetskravet ikke oppfylt
    public static final IkkeOppfyltÅrsak AKTIVITETSKRAVET_ARBEID_IKKE_OPPFYLT = new IkkeOppfyltÅrsak("4050");
    public static final IkkeOppfyltÅrsak AKTIVITETSKRAVET_OFFENTLIG_GODKJENT_UTDANNING_IKKE_OPPFYLT = new IkkeOppfyltÅrsak("4051");
    public static final IkkeOppfyltÅrsak AKTIVITETSKRAVET_OFFENTLIG_GODKJENT_UTDANNING_I_KOMBINASJON_MED_ARBEID_IKKE_OPPFYLT = new IkkeOppfyltÅrsak("4052");
    public static final IkkeOppfyltÅrsak AKTIVITETSKRAVET_MORS_SYKDOM_IKKE_OPPFYLT = new IkkeOppfyltÅrsak("4053");
    public static final IkkeOppfyltÅrsak AKTIVITETSKRAVET_MORS_INNLEGGELSE_IKKE_OPPFYLT = new IkkeOppfyltÅrsak("4054");
    public static final IkkeOppfyltÅrsak AKTIVITETSKRAVET_MORS_DELTAKELSE_PÅ_INTRODUKSJONSPROGRAM_IKKE_OPPFYLT = new IkkeOppfyltÅrsak("4055");
    public static final IkkeOppfyltÅrsak AKTIVITETSKRAVET_MORS_DELTAKELSE_PÅ_KVALIFISERINGSPROGRAM_IKKE_OPPFYLT = new IkkeOppfyltÅrsak("4056");
    public static final IkkeOppfyltÅrsak MORS_MOTTAK_AV_UFØRETRYGD_IKKE_OPPFYLT = new IkkeOppfyltÅrsak("4057");
    public static final IkkeOppfyltÅrsak STEBARNSADOPSJON_IKKE_NOK_DAGER = new IkkeOppfyltÅrsak("4058");
    public static final IkkeOppfyltÅrsak FLERBARNSFØDSEL_IKKE_NOK_DAGER = new IkkeOppfyltÅrsak("4059");
    public static final IkkeOppfyltÅrsak AKTIVITETSKRAVET_ARBEID_IKKE_DOKUMENTERT = new IkkeOppfyltÅrsak("4066");
    public static final IkkeOppfyltÅrsak AKTIVITETSKRAVET_UTDANNING_IKKE_DOKUMENTERT = new IkkeOppfyltÅrsak("4067");
    public static final IkkeOppfyltÅrsak AKTIVITETSKRAVET_ARBEID_I_KOMB_UTDANNING_IKKE_DOKUMENTERT = new IkkeOppfyltÅrsak("4068");
    public static final IkkeOppfyltÅrsak AKTIVITETSKRAVET_SYKDOM_ELLER_SKADE_IKKE_DOKUMENTERT = new IkkeOppfyltÅrsak("4069");
    public static final IkkeOppfyltÅrsak AKTIVITETSKRAVET_INNLEGGELSE_IKKE_DOKUMENTERT = new IkkeOppfyltÅrsak("4070");
    public static final IkkeOppfyltÅrsak AKTIVITETSKRAVET_INTROPROGRAM_IKKE_DOKUMENTERT = new IkkeOppfyltÅrsak("4088");
    public static final IkkeOppfyltÅrsak AKTIVITETSKRAVET_KVP_IKKE_DOKUMENTERT = new IkkeOppfyltÅrsak("4089");
    // Overføring årsaker
    public static final IkkeOppfyltÅrsak DEN_ANDRE_PART_SYK_SKADET_IKKE_OPPFYLT = new IkkeOppfyltÅrsak("4007");
    public static final IkkeOppfyltÅrsak DEN_ANDRE_PART_INNLEGGELSE_IKKE_OPPFYLT = new IkkeOppfyltÅrsak("4008");
    public static final IkkeOppfyltÅrsak SYKDOM_SKADE_INNLEGGELSE_IKKE_DOKUMENTERT = new IkkeOppfyltÅrsak("4074");
    public static final IkkeOppfyltÅrsak HAR_IKKE_ALENEOMSORG_FOR_BARNET = new IkkeOppfyltÅrsak("4092");
    // Utsettelse årsaker
    public static final IkkeOppfyltÅrsak IKKE_LOVBESTEMT_FERIE = new IkkeOppfyltÅrsak("4033");
    public static final IkkeOppfyltÅrsak FERIE_SELVSTENDIG_NÆRINGSDRIVENDSE_FRILANSER = new IkkeOppfyltÅrsak("4032");
    public static final IkkeOppfyltÅrsak IKKE_HELTIDSARBEID = new IkkeOppfyltÅrsak("4037");
    public static final IkkeOppfyltÅrsak SØKERS_SYKDOM_SKADE_IKKE_OPPFYLT = new IkkeOppfyltÅrsak("4038");
    public static final IkkeOppfyltÅrsak SØKERS_INNLEGGELSE_IKKE_OPPFYLT = new IkkeOppfyltÅrsak("4039");
    public static final IkkeOppfyltÅrsak BARNETS_INNLEGGELSE_IKKE_OPPFYLT = new IkkeOppfyltÅrsak("4040");
    public static final IkkeOppfyltÅrsak FERIE_INNENFOR_DE_FØRSTE_6_UKENE = new IkkeOppfyltÅrsak("4031");
    public static final IkkeOppfyltÅrsak INGEN_STØNADSDAGER_IGJEN = new IkkeOppfyltÅrsak("4034");
    public static final IkkeOppfyltÅrsak BARE_FAR_RETT_MOR_FYLLES_IKKE_AKTIVITETSKRAVET = new IkkeOppfyltÅrsak("4035");
    public static final IkkeOppfyltÅrsak UTSETTELSE_FØR_TERMIN_FØDSEL = new IkkeOppfyltÅrsak("4030");
    public static final IkkeOppfyltÅrsak UTSETTELSE_FERIE_PÅ_BEVEGELIG_HELLIGDAG = new IkkeOppfyltÅrsak("4041");
    public static final IkkeOppfyltÅrsak UTSETTELSE_FERIE_IKKE_DOKUMENTERT = new IkkeOppfyltÅrsak("4061");
    public static final IkkeOppfyltÅrsak UTSETTELSE_ARBEID_IKKE_DOKUMENTERT = new IkkeOppfyltÅrsak("4062");
    public static final IkkeOppfyltÅrsak UTSETTELSE_SØKERS_SYKDOM_ELLER_SKADE_IKKE_DOKUMENTERT = new IkkeOppfyltÅrsak("4063");
    public static final IkkeOppfyltÅrsak UTSETTELSE_SØKERS_INNLEGGELSE_IKKE_DOKUMENTERT = new IkkeOppfyltÅrsak("4064");
    public static final IkkeOppfyltÅrsak UTSETTELSE_BARNETS_INNLEGGELSE_IKKE_DOKUMENTERT = new IkkeOppfyltÅrsak("4065");
    public static final IkkeOppfyltÅrsak AVSLAG_UTSETTELSE_PGA_FERIE_TILBAKE_I_TID = new IkkeOppfyltÅrsak("4081");
    public static final IkkeOppfyltÅrsak AVSLAG_UTSETTELSE_PGA_ARBEID_TILBAKE_I_TID = new IkkeOppfyltÅrsak("4082");
    public static final IkkeOppfyltÅrsak HULL_MELLOM_SØKNADSPERIOD_ETTER_SISTE_UTSETTELSE = new IkkeOppfyltÅrsak("4091");

    //TODO Ikke gyldig lenger, skal ikke brukes. Ligger for å støtte historiske behandlinger
    public static final IkkeOppfyltÅrsak AVSLAG_GRADERING_PÅ_GRUNN_AV_FOR_SEN_SØKNAD = new IkkeOppfyltÅrsak("4080");
    public static final IkkeOppfyltÅrsak AVSLAG_GRADERINGSAVTALE_MANGLER_IKKE_DOKUMENTERT = new IkkeOppfyltÅrsak("4094");


    IkkeOppfyltÅrsak(String kode) {
        super(kode, DISCRIMINATOR);
    }

    IkkeOppfyltÅrsak() {
        // For hibernate
    }

    public static Set<PeriodeResultatÅrsak> opphørsAvslagÅrsaker() {
        return new HashSet<>(Arrays.asList(
                MOR_HAR_IKKE_OMSORG,
                FAR_HAR_IKKE_OMSORG,
                BARNET_ER_DØD,
                SØKER_ER_DØD,
                OPPHØR_MEDLEMSKAP,
                FØDSELSVILKÅRET_IKKE_OPPFYLT,
                ADOPSJONSVILKÅRET_IKKE_OPPFYLT,
                FORELDREANSVARSVILKÅRET_IKKE_OPPFYLT,
                OPPTJENINGSVILKÅRET_IKKE_OPPFYLT
        ));
    }
}

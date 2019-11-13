package no.nav.foreldrepenger.melding.datamapper.domene;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.Test;

import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.PeriodeListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.PeriodeType;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.melding.uttak.kodeliste.PeriodeResultatÅrsak;

public class BeregningsresultatMapperTest {

    @Test
    public void skal_finne_første_stønadsdato_null_ikke_satt() {
        ObjectFactory objectFactory = new ObjectFactory();
        PeriodeListeType periodeListeType = objectFactory.createPeriodeListeType();
        assertThat(BeregningsresultatMapper.finnStønadsperiodeFomHvisFinnes(periodeListeType)).isEmpty();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void skal_finne_første_og_siste_stønadsdato_og_håndtere_null() {
        ObjectFactory objectFactory = new ObjectFactory();
        PeriodeListeType periodeListeType = objectFactory.createPeriodeListeType();
        LocalDate førsteJanuarTjueAtten = LocalDate.of(2018, 1, 1);
        LocalDate TrettiendeAprilTjueAtten = LocalDate.of(2018, 4, 30);
        leggtilPeriode(LocalDate.of(2017, 1, 1), LocalDate.of(2017, 1, 30), false, periodeListeType, objectFactory);
        leggtilPeriode(førsteJanuarTjueAtten, LocalDate.of(2018, 1, 30), true, periodeListeType, objectFactory);
        leggtilPeriode(LocalDate.of(2018, 2, 1), LocalDate.of(2018, 2, 25), true, periodeListeType, objectFactory);
        leggtilPeriode(LocalDate.of(2018, 3, 1), LocalDate.of(2018, 3, 30), null, periodeListeType, objectFactory);
        leggtilPeriode(LocalDate.of(2018, 4, 1), TrettiendeAprilTjueAtten, true, periodeListeType, objectFactory);
        leggtilPeriode(LocalDate.of(2019, 3, 1), LocalDate.of(2019, 3, 30), false, periodeListeType, objectFactory);
        assertThat(BeregningsresultatMapper.finnStønadsperiodeFomHvisFinnes(periodeListeType).get()).isEqualTo(XmlUtil.finnDatoVerdiAvUtenTidSone(førsteJanuarTjueAtten));
        assertThat(BeregningsresultatMapper.finnStønadsperiodeTomHvisFinnes(periodeListeType).get()).isEqualTo(XmlUtil.finnDatoVerdiAvUtenTidSone(TrettiendeAprilTjueAtten));
    }

    @Test
    public void skal_finne_dagsats() {
        DatoIntervall ubetydeligPeriode = DatoIntervall.fraOgMedTilOgMed(LocalDate.now(), LocalDate.now().plusDays(1));
        BeregningsresultatFP beregningsresultat = BeregningsresultatFP.ny()
                .leggTilBeregningsresultatPerioder(List.of(BeregningsresultatPeriode.ny()
                                .medDagsats(100L)
                                .medPeriode(ubetydeligPeriode)
                                .build(),
                        BeregningsresultatPeriode.ny()
                                .medDagsats(100L * 2)
                                .medPeriode(ubetydeligPeriode)
                                .build()))
                .build();
        assertThat(BeregningsresultatMapper.finnDagsats(beregningsresultat)).isEqualTo(100L);
    }

    @Test
    public void skal_finne_månedsbeløp() {
        DatoIntervall ubetydeligPeriode = DatoIntervall.fraOgMedTilOgMed(LocalDate.now(), LocalDate.now().plusDays(1));
        BeregningsresultatFP beregningsresultat = BeregningsresultatFP.ny()
                .leggTilBeregningsresultatPerioder(List.of(BeregningsresultatPeriode.ny()
                                .medDagsats(100L)
                                .medPeriode(ubetydeligPeriode)
                                .build(),
                        BeregningsresultatPeriode.ny()
                                .medDagsats(100L * 2)
                                .medPeriode(ubetydeligPeriode)
                                .build()))
                .build();
        assertThat(BeregningsresultatMapper.finnMånedsbeløp(beregningsresultat)).isEqualTo(2166);
    }

    @Test
    public void skal_ignorere_avslåtte_manglende_søkte_perioder_med_null_trekkdager_ved_mapping_av_periodeliste() {
        DatoIntervall tidsperiode = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 9, 16), LocalDate.of(2019, 9, 16));
        BeregningsresultatPeriode brPeriode = BeregningsresultatPeriode.ny()
                .medPeriode(tidsperiode)
                .build();
        List<BeregningsresultatPeriode> beregningsresultatPerioder = List.of(brPeriode);
        UttakResultatPeriodeAktivitet uttakAktivitet = UttakResultatPeriodeAktivitet.ny()
                .medTrekkdager(BigDecimal.ZERO)
                .medUtbetalingsprosent(BigDecimal.ZERO)
                .build();
        UttakResultatPeriode uPeriode = UttakResultatPeriode.ny()
                .medAktiviteter(List.of(uttakAktivitet))
                .medTidsperiode(tidsperiode)
                .medPeriodeResultatÅrsak(PeriodeResultatÅrsak.HULL_MELLOM_FORELDRENES_PERIODER)
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .build();
        UttakResultatPerioder uttaksPerioder = UttakResultatPerioder.ny().medPerioder(List.of(uPeriode)).build();
        BeregningsgrunnlagPeriode bgPeriode = BeregningsgrunnlagPeriode.ny()
                .medPeriode(tidsperiode)
                .build();
        List<BeregningsgrunnlagPeriode> beregningsgrunnlagPerioder = List.of(bgPeriode);

        PeriodeListeType resultat = BeregningsresultatMapper.mapPeriodeListe(beregningsresultatPerioder, uttaksPerioder, beregningsgrunnlagPerioder);

        assertThat(resultat.getPeriode()).hasSize(0);
    }

    @Test
    public void skal_ta_med_avslåtte_manglende_søkte_perioder_med_trekkdager_ved_mapping_av_periodeliste() {
        DatoIntervall tidsperiode = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 9, 16), LocalDate.of(2019, 9, 16));
        BeregningsresultatPeriode brPeriode = BeregningsresultatPeriode.ny()
                .medPeriode(tidsperiode)
                .build();
        List<BeregningsresultatPeriode> beregningsresultatPerioder = List.of(brPeriode);
        UttakResultatPeriodeAktivitet uttakAktivitet = UttakResultatPeriodeAktivitet.ny()
                .medTrekkdager(BigDecimal.TEN)
                .medUtbetalingsprosent(BigDecimal.ZERO)
                .build();
        UttakResultatPeriode uPeriode = UttakResultatPeriode.ny()
                .medAktiviteter(List.of(uttakAktivitet))
                .medTidsperiode(tidsperiode)
                .medPeriodeResultatÅrsak(PeriodeResultatÅrsak.HULL_MELLOM_FORELDRENES_PERIODER)
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .build();
        UttakResultatPerioder uttaksPerioder = UttakResultatPerioder.ny().medPerioder(List.of(uPeriode)).build();
        BeregningsgrunnlagPeriode bgPeriode = BeregningsgrunnlagPeriode.ny()
                .medPeriode(tidsperiode)
                .build();
        List<BeregningsgrunnlagPeriode> beregningsgrunnlagPerioder = List.of(bgPeriode);

        PeriodeListeType resultat = BeregningsresultatMapper.mapPeriodeListe(beregningsresultatPerioder, uttaksPerioder, beregningsgrunnlagPerioder);

        assertThat(resultat.getPeriode()).hasSize(1);
    }

    @Test
    public void skal_hente_dato_fra_uttaksperiode_når_denne_er_før_beregningsresulatperioden_og_det_er_forste_beregningResPeriode() {
        DatoIntervall tidsperiodeBp1 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 9, 30), LocalDate.of(2019, 10, 2));
        DatoIntervall tidsperiodeBp2 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 10, 3), LocalDate.of(2019, 10, 4));
        DatoIntervall tidsperiodeBp3 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 10, 7), LocalDate.of(2019, 12, 31));
        DatoIntervall tidsperiodeUp1 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 7, 3), LocalDate.of(2019, 10, 4));
        DatoIntervall tidsperiodeUp2 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 10, 7), LocalDate.of(2019, 12, 31));
        DatoIntervall beregningPer = DatoIntervall.fraOgMed(LocalDate.of(2019, 9, 30));

        BeregningsresultatPeriode brPeriode = BeregningsresultatPeriode.ny()
                .medPeriode(tidsperiodeBp1)
                .medDagsats(0L)
                .build();
        BeregningsresultatPeriode brPeriode2 = BeregningsresultatPeriode.ny()
                .medPeriode(tidsperiodeBp2)
                .medDagsats(0L)
                .build();
        BeregningsresultatPeriode brPeriode3 = BeregningsresultatPeriode.ny()
                .medPeriode(tidsperiodeBp3)
                .medDagsats(620L)
                .build();
        List<BeregningsresultatPeriode> beregningsresultatPerioder = List.of(brPeriode, brPeriode2, brPeriode3);

       BeregningsgrunnlagPeriode bgPeriode = BeregningsgrunnlagPeriode.ny()
                .medPeriode(beregningPer)
                .medDagsats(620L)
                .build();
        List<BeregningsgrunnlagPeriode> beregningsgrunnlagPerioder = List.of(bgPeriode);

        UttakResultatPeriodeAktivitet uttakAktivitet = UttakResultatPeriodeAktivitet.ny()
                .medTrekkdager(BigDecimal.TEN)
                .medUtbetalingsprosent(BigDecimal.ZERO)
                .build();
        UttakResultatPeriode uPeriode = UttakResultatPeriode.ny()
                .medAktiviteter(List.of(uttakAktivitet))
                .medTidsperiode(tidsperiodeUp1)
                .medPeriodeResultatÅrsak(PeriodeResultatÅrsak.HULL_MELLOM_FORELDRENES_PERIODER)
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .build();
        UttakResultatPeriodeAktivitet uttakAktivitet2 = UttakResultatPeriodeAktivitet.ny()
                .medTrekkdager(BigDecimal.ZERO)
                .medUtbetalingsprosent(BigDecimal.valueOf(100L))
                .build();
        UttakResultatPeriode uPeriode2 = UttakResultatPeriode.ny()
                .medAktiviteter(List.of(uttakAktivitet2))
                .medTidsperiode(tidsperiodeUp2)
                .medPeriodeResultatÅrsak(PeriodeResultatÅrsak.OVERFORING_KVOTE_GYLDIG_KUN_FAR_HAR_RETT)
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .build();
        UttakResultatPerioder uttaksPerioder = UttakResultatPerioder.ny().medPerioder(List.of(uPeriode, uPeriode2)).build();

        PeriodeListeType resultat = BeregningsresultatMapper.mapPeriodeListe(beregningsresultatPerioder, uttaksPerioder, beregningsgrunnlagPerioder);

        assertThat(resultat.getPeriode()).hasSize(2);
        assertThat(XmlUtil.finnDatoVerdiAv(resultat.getPeriode().get(0).getPeriodeFom())).isEqualTo(uPeriode.getFom());
        assertThat(XmlUtil.finnDatoVerdiAv(resultat.getPeriode().get(1).getPeriodeFom())).isEqualTo(brPeriode3.getBeregningsresultatPeriodeFom());
    }

    @Test
    public void verifisere_at_resultatet_blir_det_samme_om_periodene_ikke_er_sortert() {
        DatoIntervall tidsperiodeBp1 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 9, 30), LocalDate.of(2019, 10, 2));
        DatoIntervall tidsperiodeBp2 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 10, 3), LocalDate.of(2019, 10, 4));
        DatoIntervall tidsperiodeBp3 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 10, 7), LocalDate.of(2019, 12, 31));
        DatoIntervall tidsperiodeUp1 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 7, 3), LocalDate.of(2019, 10, 4));
        DatoIntervall tidsperiodeUp2 = DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 10, 7), LocalDate.of(2019, 12, 31));
        DatoIntervall beregningPer = DatoIntervall.fraOgMed(LocalDate.of(2019, 9, 30));

        BeregningsresultatPeriode brPeriode = BeregningsresultatPeriode.ny()
                .medPeriode(tidsperiodeBp1)
                .medDagsats(0L)
                .build();
        BeregningsresultatPeriode brPeriode2 = BeregningsresultatPeriode.ny()
                .medPeriode(tidsperiodeBp2)
                .medDagsats(0L)
                .build();
        BeregningsresultatPeriode brPeriode3 = BeregningsresultatPeriode.ny()
                .medPeriode(tidsperiodeBp3)
                .medDagsats(620L)
                .build();
        List<BeregningsresultatPeriode> beregningsresultatPerioder = List.of(brPeriode3, brPeriode2, brPeriode);

        BeregningsgrunnlagPeriode bgPeriode = BeregningsgrunnlagPeriode.ny()
                .medPeriode(beregningPer)
                .medDagsats(620L)
                .build();
        List<BeregningsgrunnlagPeriode> beregningsgrunnlagPerioder = List.of(bgPeriode);

        UttakResultatPeriodeAktivitet uttakAktivitet = UttakResultatPeriodeAktivitet.ny()
                .medTrekkdager(BigDecimal.TEN)
                .medUtbetalingsprosent(BigDecimal.ZERO)
                .build();
        UttakResultatPeriode uPeriode = UttakResultatPeriode.ny()
                .medAktiviteter(List.of(uttakAktivitet))
                .medTidsperiode(tidsperiodeUp1)
                .medPeriodeResultatÅrsak(PeriodeResultatÅrsak.HULL_MELLOM_FORELDRENES_PERIODER)
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .build();
        UttakResultatPeriodeAktivitet uttakAktivitet2 = UttakResultatPeriodeAktivitet.ny()
                .medTrekkdager(BigDecimal.ZERO)
                .medUtbetalingsprosent(BigDecimal.valueOf(100L))
                .build();
        UttakResultatPeriode uPeriode2 = UttakResultatPeriode.ny()
                .medAktiviteter(List.of(uttakAktivitet2))
                .medTidsperiode(tidsperiodeUp2)
                .medPeriodeResultatÅrsak(PeriodeResultatÅrsak.OVERFORING_KVOTE_GYLDIG_KUN_FAR_HAR_RETT)
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .build();
        UttakResultatPerioder uttaksPerioder = UttakResultatPerioder.ny().medPerioder(List.of(uPeriode2, uPeriode)).build();

        PeriodeListeType resultat = BeregningsresultatMapper.mapPeriodeListe(beregningsresultatPerioder, uttaksPerioder, beregningsgrunnlagPerioder);

        assertThat(resultat.getPeriode()).hasSize(2);
        assertThat(XmlUtil.finnDatoVerdiAv(resultat.getPeriode().get(0).getPeriodeFom())).isEqualTo(uPeriode.getFom());
        assertThat(XmlUtil.finnDatoVerdiAv(resultat.getPeriode().get(1).getPeriodeFom())).isEqualTo(brPeriode3.getBeregningsresultatPeriodeFom());
    }

    private void leggtilPeriode(LocalDate fom, LocalDate tom, Boolean innvilget, PeriodeListeType periodeListeType, ObjectFactory objectFactory) {
        periodeListeType.getPeriode().add(lagPeriode(fom, tom, innvilget, objectFactory));
    }

    private PeriodeType lagPeriode(LocalDate fom, LocalDate tom, Boolean innvilget, ObjectFactory objectFactory) {
        PeriodeType periodeType = objectFactory.createPeriodeType();
        periodeType.setInnvilget(innvilget);
        periodeType.setPeriodeFom(XmlUtil.finnDatoVerdiAvUtenTidSone(fom));
        periodeType.setPeriodeTom(XmlUtil.finnDatoVerdiAvUtenTidSone(tom));
        return periodeType;
    }

}

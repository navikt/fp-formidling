package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.mal.fritekst.BrevmalKilder.ROTMAPPE;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.datamapper.domene.SvpMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.fritekstbrev.FagType;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.svp.PeriodeIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatPerioder;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttaksresultat;

public class InnvilgelseSvangerskapspengerBrevMapperTest {
    private static final long ID = 123L;
    private DokumentHendelse dokumentHendelse;
    private Behandling behandling;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().silent();
    @Mock
    DokumentFelles dokumentFelles;
    @Mock
    FellesType fellesType;
    @Mock
    BrevParametere brevParametere;

    @InjectMocks
    private InnvilgelseSvangerskapspengerBrevMapper mapper;

    @Before
    public void setup() {
        behandling = Behandling.builder().medId(ID)
                .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medBehandlendeEnhetNavn(HenleggBehandlingBrevMapper.FAMPEN)
                .medSpråkkode(Språkkode.nb)
                .build();
        dokumentHendelse = DokumentHendelse.builder()
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .medYtelseType(FagsakYtelseType.SVANGERSKAPSPENGER)
                .build();
        SvpUttaksresultat uttakResultat = mockUttaksresultat();

        mapper = new InnvilgelseSvangerskapspengerBrevMapper() {
            @Override
            Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling) {
                return new Brevdata()
                        .leggTil("resultat", uttakResultat)
                        .leggTil("beregning", mockBeregningsdata())
                        .leggTil("manedsbelop", 25342L)
                        .leggTil("mottattDato", "1. januar 2000")
                        .leggTil("antallPerioder", SvpMapper.getAntallPerioder(uttakResultat))
                        .leggTil("antallAvslag", uttakResultat.getAvslagPerioder().size())
                        .leggTil("refusjonTilBruker", true)
                        .leggTil("refusjonerTilArbeidsgivere", 2);
            }
        };
        MockitoAnnotations.initMocks(this);
    }

    @Ignore
    @Test
    public void test_map_fagtype() {
        ResourceBundle expectedValues = ResourceBundle.getBundle(
                String.join("/", ROTMAPPE, mapper.templateFolder(), "expected"),
                new Locale("nb", "NO"));

        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(expectedValues.getString("brødtekst"));
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(expectedValues.getString("overskrift"));
    }

    private SvpUttaksresultat mockUttaksresultat() {
        SvpUttakResultatPeriode innvilgetPeriode = SvpUttakResultatPeriode.ny()
                .medTidsperiode(DatoIntervall.fraOgMed(LocalDate.now().plusMonths(1)))
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .medUtbetalingsgrad(50L)
                .medAktivitetDagsats(2452).build();
        SvpUttakResultatPeriode avslåttPeriode = SvpUttakResultatPeriode.ny()
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(LocalDate.now(), LocalDate.now().plusMonths(1)))
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .medPeriodeIkkeOppfyltÅrsak(PeriodeIkkeOppfyltÅrsak.SVANGERSKAPSVILKÅRET_IKKE_OPPFYLT)
                .medAktivitetDagsats(3126).build();
        SvpUttakResultatPerioder resArbeidsforhold = SvpUttakResultatPerioder.ny()
                .medArbeidsgiverNavn("Tine")
                .medPeriode(innvilgetPeriode).build();
        return SvpUttaksresultat.ny()
                .medUttakResultatPerioder(resArbeidsforhold)
                .medAvslåttePerioder(List.of(avslåttPeriode))
                .build();
    }

    private Map<String, Object> mockBeregningsdata() {
        Map<String, Object> map = new HashMap<>();
        map.put("nyEllerEndretBeregning", true);
        map.put("bruttoBeregningsgrunnlag", 2334);
        map.put("arbeidstakerEllerFrilanser", true);
        map.put("arbeidstaker", Map.of("inntektHoyereEnnSnittAvKombinertInntekt", true));
        map.put("arbeidsforhold", List.of(
                Map.of("arbeidsgiverNavn", "Tine", "manedsinntekt", "22431"),
                Map.of("arbeidsgiverNavn", "Forsvaret", "manedsinntekt", "12431")
        ));
        map.put("ikkeSoktForAlleArbeidsforhold", true);
        map.put("frilanser", Map.of("inntektHoyereEnnSnittAvKombinertInntekt", true));
        map.put("ikkeSoktForAlleArbeidsforholdOgOppdrag", true);
        map.put("selvstendigNaringsdrivende", Map.of("inntektHoyereEnnKombinertInntektATFL", false, "nyoppstartet", true, "aarsinntekt", "24334234", "sistLignedeAar1", "62634", "sistLignedeAar2", "234235", "sistLignedeAar3", "345252"));
        map.put("ikkeSoktForAlleArbeidsforholdOgNaringsvirksomhet", true);
        map.put("ikkeSoktForAlleOppdragOgNaringsvirksomhet", true);
        map.put("ikkeSoktForAlleArbeidsforholdOppdragOgNaringsvirksomhet", true);
        map.put("naturalYtelse", Map.of("utbetalingEndret", Map.of("opp", true), "endringsDato", "12.12.84", "nyDagsats", "2342", "arbeidsgiverNavn", "Tine"));
        map.put("militarSivil", true);
        map.put("fritekst", null);
        map.put("inntektOver6G", true);
        map.put("seksG", "<seksG>");
        map.put("lovhjemmel", "§ 14-4");
        return map;
    }
}

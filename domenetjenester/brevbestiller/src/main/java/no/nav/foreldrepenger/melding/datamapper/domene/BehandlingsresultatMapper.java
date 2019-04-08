package no.nav.foreldrepenger.melding.datamapper.domene;

import static no.nav.foreldrepenger.melding.datamapper.brev.InnvilgelseForeldrepengerMapper.ENDRING_BEREGNING_OG_UTTAK;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingResultatType;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.melding.beregning.Beregning;
import no.nav.foreldrepenger.melding.beregning.Sats;
import no.nav.foreldrepenger.melding.datamapper.DokumentBestillerFeil;
import no.nav.foreldrepenger.melding.datamapper.domene.hjelperdto.DokumentTypeDto;
import no.nav.foreldrepenger.melding.datamapper.mal.DokumentType;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.personopplysning.Personopplysning;
import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.uttak.IkkeOppfyltÅrsak;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.melding.uttak.StønadskontoType;
import no.nav.foreldrepenger.melding.uttak.UttakResultat;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.melding.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.melding.vilkår.Vilkår;
import no.nav.foreldrepenger.melding.vilkår.VilkårType;
import no.nav.vedtak.feil.Feil;

@ApplicationScoped
public class BehandlingsresultatMapper {
    private KodeverkRepository kodeverkRepository;

    @Inject
    public BehandlingsresultatMapper(KodeverkRepository kodeverkRepository) {
        this.kodeverkRepository = kodeverkRepository;
    }

    public BehandlingsresultatMapper() {
        //For CDI
    }

    void mapDataRelatertTilBehandlingsResultat(final Behandling behandling, final DokumentTypeDto dto) {

        Behandlingsresultat behandlingsresultat = behandling.getBehandlingsresultat();
        // Fritekstbrev
        dto.getDokumentBehandlingsresultatDto().setBrødtekst(behandlingsresultat.getFritekstbrev());
        dto.getDokumentBehandlingsresultatDto().setOverskrift(behandlingsresultat.getOverskrift());

        // Alle andre brevene.
        mapDataRelatertTilAvslag(behandling, dto);
        dto.getDokumentBehandlingsresultatDto().setFritekst(behandlingsresultat.getAvslagarsakFritekst());

        List<String> konsekvenserForYtelsen = behandlingsresultat.getKonsekvenserForYtelsen();
        dto.getDokumentBehandlingsresultatDto().setKonsekvensForYtelse(kodeFra(konsekvenserForYtelsen));

        if (behandlingsresultat.getBehandlingResultatType() != null) {
            dto.getDokumentBehandlingsresultatDto().setBehandlingsResultat(kodeverkRepository.finn(BehandlingResultatType.class, behandlingsresultat.getBehandlingResultatType()).getKode());
        }
        if (behandlingsresultat.getBeregningResultat() != null) {
            final Optional<Beregning> sisteBeregning = behandlingsresultat.getBeregningResultat().getSisteBeregning();
            if (sisteBeregning.isPresent()) {
                Beregning beregning = sisteBeregning.get();
                dto.getDokumentBehandlingsresultatDto().setBeløp(beregning.getBeregnetTilkjentYtelse());
                if (BehandlingType.REVURDERING.equals(behandling.getBehandlingType())) {
                    setDifferanseFraTidligereBeløp(behandling, dto, beregning);
                }
            }
        }
    }

    private void setDifferanseFraTidligereBeløp(Behandling behandling, DokumentTypeDto dto, Beregning beregning) {
        behandling.getOriginalBehandling()
                .ifPresent(originalBehandling -> hentSisteBeregning(originalBehandling)
                        .ifPresent(forrigeBeregning -> {
                            Long differanse = Math.abs(beregning.getBeregnetTilkjentYtelse() - forrigeBeregning.getBeregnetTilkjentYtelse());
                            dto.getDokumentBehandlingsresultatDto().setDifferanse(differanse);
                        }));
    }

    private Optional<Beregning> hentSisteBeregning(Behandling originalBehandling) {
        return Optional.empty();
//        return beregningsRepository.getSisteBeregning(originalBehandling.getId());
    }

    private String kodeFra(List<String> konsekvenserForYtelsen) {
        if (konsekvenserForYtelsen.contains(KonsekvensForYtelsen.ENDRING_I_BEREGNING)) { // viktigst å få med endring i beregning
            return konsekvenserForYtelsen.contains(KonsekvensForYtelsen.ENDRING_I_UTTAK) ?
                    ENDRING_BEREGNING_OG_UTTAK : KonsekvensForYtelsen.ENDRING_I_BEREGNING.getKode();
        } else {
            return konsekvenserForYtelsen.isEmpty() ?
                    KonsekvensForYtelsen.UDEFINERT.getKode() : kodeverkRepository.finn(KonsekvensForYtelsen.class, konsekvenserForYtelsen.get(0)).getKode(); // velger bare den første i listen (finnes ikke koder for andre ev. kombinasjoner)
        }
    }

    private void mapDataRelatertTilAvslag(final Behandling behandling, final DokumentTypeDto dto) {
        Behandlingsresultat behandlingsresultat = behandling.getBehandlingsresultat();
        FagsakYtelseType ytelseType = kodeverkRepository.finn(FagsakYtelseType.class, behandlingsresultat.getBehandling().getFagsakYtelseType());
        final Avslagsårsak avslagsårsak = kodeverkRepository.finn(Avslagsårsak.class, behandlingsresultat.getAvslagsårsak());

        if (avslagsårsak != null && ytelseType.gjelderEngangsstønad()) {
            dto.getDokumentBehandlingsresultatDto().leggTilAvslagsårsak(avslagsårsak.getKode());
            dto.getDokumentBehandlingsresultatDto().setVilkårTypeKode(finnVilkårKode(behandlingsresultat.getBehandling(), avslagsårsak));
        } else if (ytelseType.gjelderForeldrepenger() && (avslagsårsak != null
                || behandlingsresultat.isBehandlingsresultatAvslåttOrOpphørt())) {

            mapDataFraUttak(behandlingsresultat.getBehandling(), dto);
            mapDataRelatertTilSkjæringstidspunkt(dto, behandling);
            if (avslagsårsak != null) {
                Set<String> lovReferanser = LovhjemmelUtil.hentLovhjemlerFraJson(ytelseType, avslagsårsak);
                dto.getDokumentBehandlingsresultatDto().leggTilAvslagsårsak(avslagsårsak.getKode());
                dto.getDokumentBehandlingsresultatDto().leggTilLovhjemlerForAvslag(lovReferanser);
            }
            if (behandlingsresultat.isBehandlingsresultatOpphørt()) {
                mapDataRelatertTilOpphør(behandlingsresultat, dto);
            }
        }
    }

    private void mapDataRelatertTilOpphør(Behandlingsresultat behandlingsresultat, DokumentTypeDto dto) {
        Optional<LocalDate> førsteStønadsDato = hentFørstStøndsDato(behandlingsresultat);
        Optional<LocalDate> opphørsdato = hentOpphørdato(behandlingsresultat);

        if (førsteStønadsDato.isPresent() && opphørsdato.isPresent()) {
            boolean minusEnDag = !førsteStønadsDato.get().isEqual(opphørsdato.get());
            dto.getDokumentBeregningsresultatDto()
                    .setSisteStønadsDato(opphørsdato.get().minusDays(minusEnDag ? 1 : 0));
        } else if (DokumentType.DOD_PERSON_STATUS.equals(dto.getPersonstatus())) {
            throw brevFeilPgaUtilstrekkeligTekstgrunnlag(opphørsdato.isPresent()).toException();
        }
        førsteStønadsDato.ifPresent(dto.getDokumentBeregningsresultatDto()::setFørsteStønadsDato);
        opphørsdato.ifPresent(dto.getDokumentBeregningsresultatDto()::setOpphorDato);
        finnDødsdato(behandlingsresultat.getBehandling(), dto.getDokumentBehandlingsresultatDto().getAvslagsårsakListe())
                .ifPresent(dto.getDokumentBehandlingsresultatDto()::setDodsdato);
    }

    private Optional<LocalDate> hentOpphørdato(Behandlingsresultat behandlingsresultat) {
        return Optional.empty();
//        return opphørTjeneste.getOpphørsdato(behandlingsresultat);
    }

    private Optional<LocalDate> hentFørstStøndsDato(Behandlingsresultat behandlingsresultat) {
        return Optional.empty();
//        return opphørTjeneste.getFørsteStønadsDato(behandlingsresultat.getBehandling());
    }

    private Feil brevFeilPgaUtilstrekkeligTekstgrunnlag(boolean opphørsdatoPresent) {
        return opphørsdatoPresent ?
                DokumentBestillerFeil.FACTORY.ingenStartdatoVedPersonstatusDød() : DokumentBestillerFeil.FACTORY.ingenOpphørsdatoVedPersonstatusDød();
    }

    private void mapDataRelatertTilSkjæringstidspunkt(final DokumentTypeDto dto, Behandling behandling) {
        Sats sats = hentEksaktSats(behandling);
        if (sats != null) {
            dto.setHalvG(Math.round(sats.getVerdi() / 2.0));
        }
    }

    private Sats hentEksaktSats(Behandling behandling) {
        return null;
//        return beregningsRepository.finnEksaktSats(SatsType.GRUNNBELØP, hentSkjæringstidspunkt(behandling));
    }

    private LocalDate hentSkjæringstidspunkt(Behandling behandling) {
        return null;
//        return skjæringstidspunktTjeneste.utledSkjæringstidspunktFor(behandling);
    }

    private void mapDataFraUttak(final Behandling behandling, final DokumentTypeDto dto) {
        Optional<UttakResultat> uttakResultat = hentUttakResultat(behandling);
        if (uttakResultat.isPresent()) {
            UttakResultatPerioder uttakResultatPeriode = uttakResultat.get().getGjeldendePerioder();
            List<UttakResultatPeriode> perioder = uttakResultatPeriode.getPerioder();

            setÅrsaksListeOgSisteDagIfellesPeriodeHvisFinnes(behandling, dto, perioder);

            if ((behandling.getRelasjonsRolleType().equals(RelasjonsRolleType.FARA)
                    || behandling.getRelasjonsRolleType().equals(RelasjonsRolleType.MEDMOR))
                    && dto.getSisteDagIFellesPeriode() != null) {

                setUkerEtterFellesPeriode(dto, perioder);
            }
        }
    }

    private Optional<UttakResultat> hentUttakResultat(Behandling behandling) {
        return Optional.empty();
//        return uttakRepository.hentUttakResultatHvisEksisterer(behandling);
    }

    private void setÅrsaksListeOgSisteDagIfellesPeriodeHvisFinnes(final Behandling behandling, final DokumentTypeDto dto, final List<UttakResultatPeriode> perioder) {
        for (UttakResultatPeriode periode : perioder) {
            if (periode.getPeriodeResultatType().equals(PeriodeResultatType.AVSLÅTT)) {
                setAvslagsårsakOgLovhjemmel(behandling, dto, periode);
            }
            setSisteDagIFellesPeriode(dto, periode);
        }
    }

    private void setSisteDagIFellesPeriode(DokumentTypeDto dto, UttakResultatPeriode periode) {
        periode.getAktiviteter().stream().filter(aktivitet -> StønadskontoType.FELLESPERIODE.equals(aktivitet.getTrekkonto()))
                .forEach(aktivitet -> {
                    if (dto.getSisteDagIFellesPeriode() == null || dto.getSisteDagIFellesPeriode().isBefore(aktivitet.getTom())) {
                        dto.setSisteDagIFellesPeriode(aktivitet.getTom());
                    }
                });
    }

    private void setAvslagsårsakOgLovhjemmel(final Behandling behandling, final DokumentTypeDto dto, final UttakResultatPeriode periode) {
        Optional<String> avslagsÅrsak = Optional.ofNullable(kodeverkRepository.finn(PeriodeResultatÅrsak.class, periode.getPeriodeResultatÅrsak()).getKode());
        avslagsÅrsak.ifPresent(årsak -> {
            dto.getDokumentBehandlingsresultatDto().leggTilAvslagsårsak(årsak);
            Set<String> lovhjemler = LovhjemmelUtil.hentLovhjemlerFraJson(
                    kodeverkRepository.finn(FagsakYtelseType.class, behandling.getFagsakYtelseType()),
                    kodeverkRepository.finn(PeriodeResultatÅrsak.class, periode.getPeriodeResultatÅrsak()));
            dto.getDokumentBehandlingsresultatDto().leggTilLovhjemlerForAvslag(lovhjemler);
        });
    }

    private void setUkerEtterFellesPeriode(final DokumentTypeDto dto, final List<UttakResultatPeriode> perioder) {
        perioder.stream().filter(periode -> IkkeOppfyltÅrsak.HULL_MELLOM_FORELDRENES_PERIODER.equals(periode.getPeriodeResultatÅrsak()))
                .forEach(p -> {
                    int ukerEtterFellesPeriode = p.getAktiviteter().stream()
                            .filter(a -> a.getFom().equals(dto.getSisteDagIFellesPeriode().plusDays(1)))
                            .mapToInt(a -> a.getTrekkdager() / 5).sum();
                    dto.setUkerEtterFellesPeriode(ukerEtterFellesPeriode);
                });
    }

    private String finnVilkårKode(final Behandling behandling, Avslagsårsak avslagsårsak) {
        List<VilkårType> vilkårTyper = finnVilkårTypeListe(avslagsårsak);
        List<Vilkår> vilkårene = behandling.getBehandlingsresultat().getVilkårResultat().getVilkårene();
        Vilkår vilkår = vilkårene.stream()
                .filter(v -> vilkårTyper.contains(v.getVilkårType()))
                .findFirst().orElseThrow(() -> new IllegalStateException("Fant ingen vilkår"));

        return vilkår.getVilkårType();
    }

    private List<VilkårType> finnVilkårTypeListe(Avslagsårsak avslagsårsak) {
        return new ArrayList<>();
//        return vilkårKodeverkRepository.finnVilkårTypeListe(avslagsårsak.getKode());
    }

    private Optional<LocalDate> finnDødsdato(final Behandling behandling, final Set<String> avslagsårsakListe) {
        Optional<LocalDate> dødsdato = Optional.empty();
/*
        if (avslagsårsakListe.contains(IkkeOppfyltÅrsak.SØKER_ER_DØD.getKode())) {
            dødsdato = hentPersonopplysninger(behandling)
                    .map(Personopplysninger::getSøker)
                    .map(Personopplysning::getDødsdato);
        }
        if (avslagsårsakListe.contains(IkkeOppfyltÅrsak.BARNET_ER_DØD.getKode())) {
            dødsdato = hentFamiliehendelseDetaljer(behandling).stream()
                    .filter(personopplysning -> personopplysning.getDødsdato() != null)
                    .map(Personopplysning::getDødsdato)
                    .findFirst();
        }
*/
        return dødsdato;
    }

    private List<Personopplysning> hentFamiliehendelseDetaljer(Behandling behandling) {
        return new ArrayList<>();
//        return familiehendelseTjeneste.finnBarnSøktStønadFor(behandling);
    }
}

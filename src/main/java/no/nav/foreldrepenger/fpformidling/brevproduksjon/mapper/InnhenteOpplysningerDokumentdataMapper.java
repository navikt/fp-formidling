package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.InnhenteOpplysningerDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.INNHENTE_OPPLYSNINGER)
public class InnhenteOpplysningerDokumentdataMapper implements DokumentdataMapper {

    private final BrevMapperUtil brevMapperUtil;

    @Inject
    public InnhenteOpplysningerDokumentdataMapper(BrevMapperUtil brevMapperUtil) {
        this.brevMapperUtil = brevMapperUtil;
    }

    @Override
    public String getTemplateNavn() {
        return "innhente-opplysninger";
    }

    @Override
    public InnhenteOpplysningerDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                               DokumentHendelse hendelse,
                                                               BrevGrunnlagDto behandling,
                                                               boolean erUtkast) {

        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, erUtkast);
        var språkkode = dokumentFelles.getSpråkkode();
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), språkkode) : null);
        fellesBuilder.medFritekst(FritekstDto.fra(hendelse.getFritekst()));

        return InnhenteOpplysningerDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medFørstegangsbehandling(behandling.erFørstegangssøknad())
            .medRevurdering(behandling.erRevurdering())
            .medEndringssøknad(behandling.erRevurdering() && behandling.harBehandlingÅrsak(BrevGrunnlagDto.BehandlingÅrsakType.RE_ENDRING_FRA_BRUKER))
            .medDød(BrevMapperUtil.erDød(dokumentFelles))
            .medKlage(behandling.erKlage())
            .medSøknadDato(finnSøknadDato(behandling, dokumentFelles))
            .medFristDato(formaterDato(brevMapperUtil.getSvarFrist(), språkkode)).build();
    }

    private String finnSøknadDato(BrevGrunnlagDto behandling, DokumentFelles dokumentFelles) {
        var klageBehandling = Optional.ofNullable(behandling.klageBehandling());
        var dato = klageBehandling.map(klage -> Optional.ofNullable(klage.mottattDato()).orElse(behandling.opprettet().toLocalDate()))
            .orElse(behandling.sisteSøknadMottattDato());
        return formaterDato(dato, dokumentFelles.getSpråkkode());
    }
}

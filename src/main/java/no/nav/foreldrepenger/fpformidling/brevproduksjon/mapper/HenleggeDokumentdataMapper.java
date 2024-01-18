package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import jakarta.enterprise.context.ApplicationScoped;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.HenleggelseDokumentdata;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalTypeKode;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.INFO_OM_HENLEGGELSE)
public class HenleggeDokumentdataMapper implements DokumentdataMapper {
    private static final String FAMPEN = "NAV Familie- og pensjonsytelser";

    @Override
    public String getTemplateNavn() {
        return "henleggelse";
    }

    @Override
    public HenleggelseDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                      DokumentHendelse hendelse,
                                                      Behandling behandling,
                                                      boolean erUtkast) {

        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, hendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(
            dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), behandling.getSpråkkode()) : null);
        fellesBuilder.medErAutomatiskBehandlet(dokumentFelles.getAutomatiskBehandlet());

        return HenleggelseDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medVanligBehandling(behandling.getBehandlingType().erYtelseBehandlingType())
            .medAnke(behandling.erAnke())
            .medInnsyn(behandling.erInnsyn())
            .medKlage(behandling.erKlage())
            .medOpphavType(utledOpphavType(hendelse.getBehandlendeEnhetNavn() == null || hendelse.getBehandlendeEnhetNavn()
                .isEmpty() ? behandling.getBehandlendeEnhetNavn() : hendelse.getBehandlendeEnhetNavn()))
            .build();
    }

    private String utledOpphavType(String behandlendeEnhetNavn) {
        if (behandlendeEnhetNavn == null || behandlendeEnhetNavn.contains(FAMPEN)) {
            return "FAMPEN";
        }
        return "KLAGE";
    }

}

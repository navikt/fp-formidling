package no.nav.foreldrepenger.fpformidling.brevmapper.brev;

import static no.nav.foreldrepenger.fpformidling.datamapper.util.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.HenleggelseDokumentdata;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalTypeKode;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.INFO_OM_HENLEGGELSE)
public class HenleggeDokumentdataMapper implements DokumentdataMapper {
    private static final String FAMPEN = "NAV Familie- og pensjonsytelser";

    @Inject
    public HenleggeDokumentdataMapper() {
    }

    @Override
    public String getTemplateNavn() {
        return "henleggelse";
    }

    @Override
    public HenleggelseDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse,
                                                      Behandling behandling, boolean erUtkast) {

        var fellesBuilder = opprettFellesBuilder(dokumentFelles, hendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), behandling.getSpråkkode()) : null);
        fellesBuilder.medErAutomatiskBehandlet(dokumentFelles.getAutomatiskBehandlet());

        return HenleggelseDokumentdata.ny()
                .medFelles(fellesBuilder.build())
                .medVanligBehandling(behandling.getBehandlingType().erYtelseBehandlingType())
                .medAnke(behandling.erAnke())
                .medInnsyn(behandling.erInnsyn())
                .medKlage(behandling.erKlage())
                .medOpphavType(utledOpphavType(hendelse.getBehandlendeEnhetNavn() == null || hendelse.getBehandlendeEnhetNavn().isEmpty() ? behandling.getBehandlendeEnhetNavn() : hendelse.getBehandlendeEnhetNavn()))
                .build();
    }

    private String utledOpphavType(String behandlendeEnhetNavn) {
        if (behandlendeEnhetNavn == null || behandlendeEnhetNavn.contains(FAMPEN)) {
            return "FAMPEN";
        }
        return "KLAGE";
    }

}

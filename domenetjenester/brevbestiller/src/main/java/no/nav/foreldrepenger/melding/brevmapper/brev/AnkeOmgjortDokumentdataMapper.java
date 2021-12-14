package no.nav.foreldrepenger.melding.brevmapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDatoNorsk;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.anke.Anke;
import no.nav.foreldrepenger.melding.anke.AnkeVurderingOmgjør;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.AnkeOmgjortDokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Fritekst;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.ANKE_OMGJORT)
public class AnkeOmgjortDokumentdataMapper implements DokumentdataMapper {

    private DomeneobjektProvider domeneobjektProvider;

    AnkeOmgjortDokumentdataMapper() {
        //CDI
    }

    @Inject
    public AnkeOmgjortDokumentdataMapper(DomeneobjektProvider domeneobjektProvider) {
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String getTemplateNavn() {
        return "anke-omgjort";
    }

    @Override
    public AnkeOmgjortDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse,
                                                      Behandling behandling, boolean erUtkast) {
        Optional<Anke> anke = domeneobjektProvider.hentAnkebehandling(behandling);

        var fellesBuilder = opprettFellesBuilder(dokumentFelles, hendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDatoNorsk(dokumentFelles.getDokumentDato()) : null);
        fellesBuilder.medFritekst(Fritekst.fra(anke.map(Anke::getFritekstTilBrev).orElse(hendelse.getFritekst())));

        return AnkeOmgjortDokumentdata.ny()
                .medFelles(fellesBuilder.build())
                .medGunst(erTilGunst(anke))
                .medVedtaksdato(formaterDatoNorsk(getVedtaksdato(anke)))
                .build();
    }

    private boolean erTilGunst(Optional<Anke> anke) {
        return anke.isPresent() && AnkeVurderingOmgjør.ANKE_TIL_GUNST.equals(anke.get().getAnkeVurderingOmgjoer());
    }

    private LocalDate getVedtaksdato(Optional<Anke> anke) {
        UUID påAnketKlageBehandlingUuid = anke.map(Anke::getPåAnketKlageBehandlingUuid).orElse(null);
        if (påAnketKlageBehandlingUuid != null) {
            return domeneobjektProvider.hentBehandling(påAnketKlageBehandlingUuid).getOriginalVedtaksDato();
        } else {
            return null;
        }
    }
}

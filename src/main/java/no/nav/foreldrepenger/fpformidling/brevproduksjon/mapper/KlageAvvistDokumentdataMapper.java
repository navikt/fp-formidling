package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.KlageMapper.gjelderTilbakekreving;
import static no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto.fra;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;

import java.util.Set;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.KlageMapper;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.domene.klage.KlageAvvistÅrsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.KlageAvvistDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.KLAGE_AVVIST)
public class KlageAvvistDokumentdataMapper implements DokumentdataMapper {

    private final BrevParametere brevParametere;

    @Inject
    public KlageAvvistDokumentdataMapper(BrevParametere brevParametere) {
        this.brevParametere = brevParametere;
    }

    @Override
    public String getTemplateNavn() {
        return "klage-avvist";
    }

    @Override
    public KlageAvvistDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                      DokumentHendelse hendelse,
                                                      BrevGrunnlagDto behandling,
                                                      boolean erUtkast) {
        var klage = behandling.klageBehandling();
        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDatoNorsk(dokumentFelles.getDokumentDato()) : null);
        fra(hendelse, klage).ifPresent(fellesBuilder::medFritekst);

        var avvistGrunner = getAvvistGrunner(klage);

        var dokumentdataBuilder = KlageAvvistDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medGjelderTilbakekreving(gjelderTilbakekreving(klage))
            .medLovhjemler(getLovhjemler(klage, dokumentFelles.getSpråkkode()))
            .medKlagefristUker(brevParametere.getKlagefristUker())
            .medAvvistGrunner(avvistGrunner);

        return dokumentdataBuilder.build();
    }

    private String getLovhjemler(BrevGrunnlagDto.KlageBehandling klage, Språkkode språkkode) {
        var lovhjemler = KlageMapper.hentOgFormaterLovhjemlerForAvvistKlage(klage, språkkode);
        return lovhjemler.map(String::toString).orElse(null);
    }

    private Set<String> getAvvistGrunner(BrevGrunnlagDto.KlageBehandling klage) {
        return KlageMapper.listeAvAvvisteÅrsaker(klage)
            .stream()
            .map(å -> KlageAvvistÅrsak.IKKE_SIGNERT.equals(å) ? KlageAvvistÅrsak.KLAGE_UGYLDIG.getKode() : å.getKode())
            .collect(Collectors.toSet());
    }
}

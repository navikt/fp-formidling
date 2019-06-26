package no.nav.foreldrepenger.melding.web.app.tjenester.brev;

import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.READ;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursActionAttributt.UPDATE;
import static no.nav.vedtak.sikkerhet.abac.BeskyttetRessursResourceAttributt.FAGSAK;

import java.util.Objects;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import no.nav.foreldrepenger.kontrakter.formidling.v1.TekstFraSaksbehandlerDto;
import no.nav.foreldrepenger.melding.brevbestiller.dto.SaksbehandlerTekstMapper;
import no.nav.foreldrepenger.melding.dokumentdata.SaksbehandlerTekst;
import no.nav.foreldrepenger.melding.dokumentdata.repository.DokumentRepository;
import no.nav.vedtak.felles.jpa.Transaction;
import no.nav.vedtak.sikkerhet.abac.BeskyttetRessurs;

@Api(tags = "saksbehandlertekst")
@Path("/saksbehandlertekst")
@ApplicationScoped
public class SaksbehandlerTekstRestTjeneste {
    private SaksbehandlerTekstMapper saksbehandlerTekstMapper;
    private DokumentRepository dokumentRepository;

    public SaksbehandlerTekstRestTjeneste() {//CDI
    }

    @Inject
    public SaksbehandlerTekstRestTjeneste(SaksbehandlerTekstMapper saksbehandlerTekstMapper,
                                          DokumentRepository dokumentRepository) {
        this.saksbehandlerTekstMapper = saksbehandlerTekstMapper;
        this.dokumentRepository = dokumentRepository;
    }

    @POST
    @Timed(name = "fpformidling.in.lagre-saksbehandlertekst", absolute = true)
    @Path("/lagre-saksbehandler-tekst")
    @Transaction
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Lagrer tekst fra saksbehandler")
    @BeskyttetRessurs(action = UPDATE, ressurs = FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public void lagreTekstFraSaksbehandler(
            @ApiParam("Inneholder tekst saksbehandler fylt ut")
            @Valid AbacTekstFraSaksbehandlerDto tekstFraSaksbehandlerDto) { // NOSONAR

        final Optional<SaksbehandlerTekst> formidlingDataOptional = dokumentRepository.hentSaksbehandlerTekstHvisEksisterer(tekstFraSaksbehandlerDto.getBehandlingUuid());
        SaksbehandlerTekst saksbehandlerTekst = saksbehandlerTekstMapper.mapSaksbehandlerTekstFraDto(tekstFraSaksbehandlerDto);
        if (formidlingDataOptional.isPresent()) {
            final SaksbehandlerTekst eksisterendeSaksbehandlerTekst = formidlingDataOptional.get();
            if (Objects.nonNull(saksbehandlerTekst.getVedtaksbrev()) && !saksbehandlerTekst.getVedtaksbrev().equals(eksisterendeSaksbehandlerTekst.getVedtaksbrev())) {
                eksisterendeSaksbehandlerTekst.setVedtaksbrev(saksbehandlerTekst.getVedtaksbrev());
            }
            if (Objects.nonNull(saksbehandlerTekst.getAvklarFritekst()) && !saksbehandlerTekst.getAvklarFritekst().equals(eksisterendeSaksbehandlerTekst.getAvklarFritekst())) {
                eksisterendeSaksbehandlerTekst.setAvklarFritekst(saksbehandlerTekst.getAvklarFritekst());
            }
            if (Objects.nonNull(saksbehandlerTekst.getTittel()) && !saksbehandlerTekst.getTittel().equals(eksisterendeSaksbehandlerTekst.getTittel())) {
                eksisterendeSaksbehandlerTekst.setTittel(saksbehandlerTekst.getTittel());
            }
            if (Objects.nonNull(saksbehandlerTekst.getFritekst()) && !saksbehandlerTekst.getFritekst().equals(eksisterendeSaksbehandlerTekst.getFritekst())) {
                eksisterendeSaksbehandlerTekst.setFritekst(saksbehandlerTekst.getFritekst());
            }
        } else {
            dokumentRepository.lagre(saksbehandlerTekst);
        }
    }

    @POST
    @Timed(name = "fpformidling.in.hent-saksbehandlertekst", absolute = true)
    @Path("/hent-saksbehandler-tekst")
    @Transaction
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    @ApiOperation(value = "hent tekst fra saksbehandler")
    @BeskyttetRessurs(action = READ, ressurs = FAGSAK)
    @SuppressWarnings("findsecbugs:JAXRS_ENDPOINT")
    public TekstFraSaksbehandlerDto hentTekstFraSaksbehandler(@Valid AbacBehandlingUuidDto dto) { // NOSONAR
        final Optional<SaksbehandlerTekst> formidlingDataOptional = dokumentRepository.hentSaksbehandlerTekstHvisEksisterer(dto.getBehandlingUuid());
        return formidlingDataOptional.map(formidlingData -> saksbehandlerTekstMapper.mapSaksbehandlerTekstTilDto(formidlingData)).orElse(null);
    }
}

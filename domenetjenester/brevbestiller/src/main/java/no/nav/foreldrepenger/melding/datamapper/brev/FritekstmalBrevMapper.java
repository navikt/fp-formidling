package no.nav.foreldrepenger.melding.datamapper.brev;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.FileTemplateLoader;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brev.fritekstmal.BrevmalKildefiler;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.fritekstbrev.FagType;

abstract class FritekstmalBrevMapper extends FritekstBrevMapper implements BrevmalKildefiler {

    private FileTemplateLoader templateLoader;
    private Handlebars handlebars;
    private Template overskriftMal;
    private Template brødtekstMal;

    protected BrevParametere brevParametere;
    protected DomeneobjektProvider domeneobjektProvider;
    protected FellesType fellesType;
    protected DokumentFelles dokumentFelles;

    public FritekstmalBrevMapper() {
    }

    @Inject
    public FritekstmalBrevMapper(BrevParametere brevParametere,
                                 DomeneobjektProvider domeneobjektProvider) {
        this.brevParametere = brevParametere;
        this.domeneobjektProvider = domeneobjektProvider;
        initHandlebars();
        overskriftMal = tryCompile(OVERSKRIFT);
        brødtekstMal = tryCompile(BRØDTEKST);
    }

    @Override
    public String mapTilBrevXML(FellesType fellesType,
                                DokumentFelles dokumentFelles,
                                DokumentHendelse hendelse,
                                Behandling behandling) throws JAXBException, SAXException, XMLStreamException {
        this.fellesType = fellesType;
        this.dokumentFelles = dokumentFelles;
        return super.mapTilBrevXML(fellesType, dokumentFelles, hendelse, behandling);
    }

    @Override
    protected FagType mapFagType(DokumentHendelse hendelse, Behandling behandling) {
        FagType fagType = new FagType();
        fagType.setHovedoverskrift(tryApply(new Brevdata(behandling.getSpråkkode()) {}, overskriftMal));
        fagType.setBrødtekst(tryApply(mapTilBrevfelter(hendelse, behandling), brødtekstMal));
        return fagType;
    }

    private void initHandlebars() {
        templateLoader = new FileTemplateLoader(Paths.get(TEMPLATES_PATH + getSubfolder()).toFile());
        handlebars = new Handlebars(templateLoader).setCharset(Charset.forName("latin1"));
        handlebars.setInfiniteLoops(false);
        handlebars.setPrettyPrint(true);
    }

    private Template tryCompile(String location) {
        try {
            return handlebars.compile(location);
        } catch (IOException e) {
            throw new IllegalStateException("Kunne ikke kompilere fil: " + location + ".hbs", e);
        }
    }

    private String tryApply(Brevdata brevdata, Template template) {
        try {
            return template.apply(brevdata);
        } catch (IOException e) {
            throw new IllegalArgumentException("Klarte ikke mappe feltverdier til dokumentmal", e);
        }
    }

    abstract String getSubfolder();

    abstract Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling);

    abstract class Brevdata {
        private String locale;
        private String bundle;

        public Brevdata(Språkkode språkkode) {
            locale = BrevmalKildefiler.getLocaleSuffixFor(språkkode);
            bundle = RESOURCE_BUNDLE_ROOT + getSubfolder();
        }

        public String getLocale() {
            return locale;
        }

        public String getBundle() {
            return bundle;
        }
    }
}

package no.nav.foreldrepenger.melding.datamapper.brev;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.helper.ConditionalHelpers;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.CompositeTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.mal.fritekst.BrevmalKilder;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.fritekstbrev.FagType;

public abstract class FritekstmalBrevMapper extends FritekstBrevMapper implements BrevmalKilder {

    private CompositeTemplateLoader templateLoader;
    private Handlebars handlebars;
    private Template overskriftMal;
    private Template brødtekstMal;

    protected BrevParametere brevParametere;
    protected DomeneobjektProvider domeneobjektProvider;
    protected FellesType fellesType;
    protected DokumentFelles dokumentFelles;

    protected FritekstmalBrevMapper() {

    }

    @Inject
    public FritekstmalBrevMapper(BrevParametere brevParametere,
                                 DomeneobjektProvider domeneobjektProvider) {
        this.brevParametere = brevParametere;
        this.domeneobjektProvider = domeneobjektProvider;
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
        initHandlebars();
        FagType fagType = new FagType();
        fagType.setHovedoverskrift(tryApply(new Brevdata(behandling.getSpråkkode()) {}, overskriftMal));
        fagType.setBrødtekst(tryApply(mapTilBrevfelter(hendelse, behandling), brødtekstMal));
        return fagType;
    }

    private void initHandlebars() {
        TemplateLoader folder = new ClassPathTemplateLoader(getClassPath("", ROTMAPPE, templateFolder()));
        TemplateLoader felles = new ClassPathTemplateLoader(getClassPath("", ROTMAPPE, FELLES));
        templateLoader = new CompositeTemplateLoader(folder, felles);
        handlebars = new Handlebars(templateLoader).setCharset(Charset.forName("latin1"));
        handlebars.setInfiniteLoops(false);
        handlebars.setPrettyPrint(true);
        handlebars.registerHelpers(ConditionalHelpers.class);
        overskriftMal = tryCompile(OVERSKRIFT);
        brødtekstMal = tryCompile(BRØDTEKST);
    }

    private Template tryCompile(String source) {
        try {
            return handlebars.compile(source);
        } catch (IOException e) {
            throw new IllegalStateException("Klarte ikke kompilere fil: " + source + ".hbs", e);
        }
    }

    private String tryApply(Brevdata brevdata, Template template) {
        try {
            return template.apply(brevdata);
        } catch (IOException e) {
            throw new IllegalArgumentException("Klarte ikke mappe feltverdier til dokumentmal", e);
        }
    }

    public abstract String displayName();

    abstract String templateFolder();

    abstract Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling);

    abstract class Brevdata {
        private Språkkode språkkode;

        public Brevdata(Språkkode språkkode) {
            this.språkkode = språkkode;
        }

        public String getLocale() {
            return getLocaleSuffixFor(språkkode);
        }

        public String getBundle() {
            return getResourceBundle(templateFolder());
        }

        public String getFelles() {
            return getResourceBundle(FELLES);
        }
    }
}

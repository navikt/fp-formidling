package no.nav.foreldrepenger.melding.datamapper.brev;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.helper.ConditionalHelpers;
import com.github.jknack.handlebars.helper.I18nHelper;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.CompositeTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.mal.fritekst.BrevmalKilder;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
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

    FritekstmalBrevMapper(BrevParametere brevParametere,
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
        initHandlebars(BrevmalKilder.getLocale(behandling.getSpråkkode()));
        FagType fagType = new FagType();
        fagType.setHovedoverskrift(tryApply(new Brevdata(dokumentFelles, fellesType) {}, overskriftMal));
        fagType.setBrødtekst(tryApply(mapTilBrevfelter(hendelse, behandling), brødtekstMal));
        return fagType;
    }

    private void initHandlebars(Locale locale) {
        TemplateLoader folder = new ClassPathTemplateLoader(getTemplateClassPath());
        TemplateLoader felles = new ClassPathTemplateLoader(getFellesClassPath());
        templateLoader = new CompositeTemplateLoader(folder, felles);
        handlebars = new Handlebars(templateLoader).setCharset(Charset.forName("latin1"));
        handlebars.setInfiniteLoops(false);
        handlebars.setPrettyPrint(true);
        handlebars.registerHelpers(ConditionalHelpers.class);
        I18nHelper.i18n.setDefaultBundle(BrevmalKilder.getResourceBundle(templateFolder()));
        I18nHelper.i18n.setDefaultLocale(locale);
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

    abstract Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling);

    public static abstract class Brevdata {
        private DokumentFelles dokumentFelles;
        private FellesType fellesType;

        public Brevdata(DokumentFelles dokumentFelles, FellesType fellesType) {
            this.dokumentFelles = dokumentFelles;
            this.fellesType = fellesType;
        }

        public String getFelles() {
            return BrevmalKilder.getResourceBundle(FELLES);
        }

        public String getKontaktTelefonnummer() {
            return dokumentFelles.getKontaktTlf();
        }

        public String getNavnAvsenderEnhet() {
            return dokumentFelles.getNavnAvsenderEnhet();
        }

        public boolean getErAutomatiskVedtak() {
            return fellesType.isAutomatiskBehandlet();
        }
    }
}

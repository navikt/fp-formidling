package no.nav.foreldrepenger.melding.datamapper.brev;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.helper.ConditionalHelpers;
import com.github.jknack.handlebars.helper.I18nHelper;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.CompositeTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.datamapper.mal.fritekst.BrevmalKilder;
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
        initHandlebars(behandling.getSpråkkode());
        FagType fagType = new FagType();
        fagType.setHovedoverskrift(tryApply(null, overskriftMal));
        fagType.setBrødtekst(tryApply(mapTilBrevfelter(hendelse, behandling).map, brødtekstMal));
        return fagType;
    }

    private void initHandlebars(Språkkode språkkode) {
        TemplateLoader folder = new ClassPathTemplateLoader(getTemplateClassPath());
        TemplateLoader felles = new ClassPathTemplateLoader(getFellesClassPath());
        templateLoader = new CompositeTemplateLoader(folder, felles);
        handlebars = new Handlebars(templateLoader).setCharset(Charset.forName("latin1"));
        handlebars.setInfiniteLoops(false);
        handlebars.setPrettyPrint(true);
        registerHelpers(språkkode);
        overskriftMal = tryCompile(OVERSKRIFT);
        brødtekstMal = tryCompile(BRØDTEKST);
    }

    private void registerHelpers(Språkkode språkkode) {
        handlebars.registerHelpers(ConditionalHelpers.class);
        handlebars.registerHelper("size", (o, options) -> {
            if (o instanceof Collection) {
                return ((Collection) o).size();
            }
            return 0;
        });
        handlebars.registerHelper(I18nHelper.i18n.name(), (resource, options) -> {
            options.hash.put("bundle", resolveBundle(options));
            options.hash.put("locale", BrevmalKilder.getLocaleSuffixFor(språkkode));
            return I18nHelper.i18n.apply((String) resource, options);
        });
    }

    private Object resolveBundle(Options options) {
        String defaultBundle = "messages";
        return Optional.ofNullable(options.hash.getOrDefault("bundle", defaultBundle))
                .map(bundle -> !bundle.equals(defaultBundle) ? bundle :
                        BrevmalKilder.getResourceBundle(templateFolder()))
                .orElse(BrevmalKilder.getResourceBundle(FELLES));
    }

    private Template tryCompile(String source) {
        try {
            return handlebars.compile(source);
        } catch (IOException e) {
            throw new IllegalStateException("Klarte ikke kompilere fil: " + source + ".hbs", e);
        }
    }

    private String tryApply(Object brevdata, Template template) {
        try {
            return template.apply(brevdata);
        } catch (IOException e) {
            throw new IllegalArgumentException("Klarte ikke mappe feltverdier til dokumentmal", e);
        }
    }

    public abstract String displayName();

    abstract Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling);

    public class Brevdata {
        private Map<String, Object> map = new HashMap<>();

        public Brevdata() {
            mapFellesdata();
        }

        Brevdata leggTil(String felt, Object data) {
            map.put(felt, data);
            return this;
        }

        private void mapFellesdata() {
            map.put("kontaktTelefonnummer", dokumentFelles.getKontaktTlf());
            map.put("navnAvsenderEnhet", dokumentFelles.getNavnAvsenderEnhet());
            map.put("erAutomatiskVedtak", fellesType.isAutomatiskBehandlet());
        }
    }
}

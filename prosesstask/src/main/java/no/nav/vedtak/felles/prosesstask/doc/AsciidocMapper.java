package no.nav.vedtak.felles.prosesstask.doc;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import io.github.swagger2markup.markup.builder.MarkupDocBuilder;
import io.github.swagger2markup.markup.builder.MarkupDocBuilders;
import io.github.swagger2markup.markup.builder.MarkupLanguage;

class AsciidocMapper {

    void writeTo(Path path, MarkupOutput model) {
        MarkupDocBuilder documentBuilder = MarkupDocBuilders.documentBuilder(MarkupLanguage.ASCIIDOC);

        model.apply(1, documentBuilder);

        documentBuilder.writeToFile(path, Charset.forName("UTF8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}

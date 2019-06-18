package no.nav.vedtak.felles.prosesstask.doc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.github.swagger2markup.markup.builder.MarkupBlockStyle;
import io.github.swagger2markup.markup.builder.MarkupDocBuilder;
import no.nav.vedtak.felles.prosesstask.impl.ProsessTaskType;

public class ProsessTaskModell implements MarkupOutput {

    private List<Entry> entries = new ArrayList<>();

    @Override
    public void apply(int sectionLevel, MarkupDocBuilder doc) {

        Map<String, List<Entry>> gruppertPrefix = new TreeMap<>();

        entries.forEach(e -> {
            List<Entry> prefixEntries;
            String prefix = e.getKodePrefiks();
            if (!gruppertPrefix.containsKey(prefix)) {
                prefixEntries = new ArrayList<>();
                gruppertPrefix.put(prefix, prefixEntries);
            } else {
                prefixEntries = gruppertPrefix.get(prefix);
            }
            prefixEntries.add(e);
        });

        gruppertPrefix.forEach((prefix, prefixEntries) -> {
            doc.sectionTitleLevel(sectionLevel, prefix);

            int level = sectionLevel + 1;
            prefixEntries.forEach(entry -> {
                doc.sectionTitleLevel(level, entry.getNavn() != null ? entry.getNavn() : entry.getKode());

                String konfig = getKonfigTekst(entry);
                doc.block(konfig, MarkupBlockStyle.EXAMPLE);

                if (entry.docs != null) {
                    entry.docs.forEach(txt -> {
                        doc.block(txt, MarkupBlockStyle.PASSTHROUGH);
                    });
                }
            });

        });
    }

    String getKonfigTekst(Entry entry) {
        String konfig = ".Konfigurasjon" +
                "\n* *Kode:* " + entry.getKode() +
                "\n* *Klasse:* " + entry.targetClassQualifiedName +
                "";

        ProsessTaskType taskType = entry.getTaskType();
        if (taskType != null) {
            konfig += "\n* *Feilhåndteringalgoritme:* " + taskType.getFeilhåndteringAlgoritme().getNavn() +
                    "\n* *Maks forsøk:* " + taskType.getMaksForsøk() +
                    "";
        }
        return konfig;
    }

    public Entry leggTil(String kode, String targetClass, String docs) {
        Entry entry = new Entry(kode, targetClass, docs);
        entries.add(entry);
        return entry;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public static class Entry {
        private final String targetClassQualifiedName;
        private final String kode;
        private List<String> docs = new ArrayList<>();
        private String navn;
        private ProsessTaskType taskType;

        Entry(String kode, String targetClass, String javaDoc) {
            super();
            this.kode = kode;
            this.targetClassQualifiedName = targetClass;
            leggTil(javaDoc);
        }

        public String getKode() {
            return kode;
        }

        public String getNavn() {
            return navn;
        }

        public void setNavn(String navn) {
            this.navn = navn;
        }

        public void leggTil(String doc) {
            if (doc != null && !doc.isEmpty()) {
                this.docs.add(doc);
            }
        }

        public List<String> getDocs() {
            return docs;
        }

        public void setProsessTaskType(ProsessTaskType ptt) {
            this.taskType = ptt;
        }

        public ProsessTaskType getTaskType() {
            return taskType;
        }

        public String getKodePrefiks() {
            return kode.contains(".") ? kode.substring(0, kode.indexOf('.')) : kode;
        }
    }

}

package no.nav.foreldrepenger.melding.db.validering;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import no.nav.foreldrepenger.melding.dbstoette.DatasourceConfiguration;
import no.nav.vedtak.felles.lokal.dbstoette.ConnectionHandler;
import no.nav.vedtak.felles.lokal.dbstoette.DBConnectionProperties;

/**
 * Tester at alle migreringer følger standarder for navn og god praksis.
 */
public class SjekkDbStrukturTest {

    private static final String HJELP = "\n\nDu har nylig lagt til en ny tabell eller kolonne som ikke er dokumentert ihht. gjeldende regler for dokumentasjon."
            + "\nVennligst gå over sql scriptene og dokumenter tabellene på korrekt måte.";

    private static DataSource ds;

    private static String schema;

    @BeforeClass
    public static void setup() throws FileNotFoundException {
        List<DBConnectionProperties> connectionProperties = DatasourceConfiguration.UNIT_TEST.get();

        DBConnectionProperties dbconp = DBConnectionProperties.finnDefault(connectionProperties).get();
        ds = ConnectionHandler.opprettFra(dbconp);
        schema = dbconp.getSchema();
    }

    @Test
    public void sjekk_at_alle_tabeller_er_dokumentert() throws Exception {
        String sql = "select t.table_name from information_schema.tables t\n" +
                "join pg_class c on t.table_name = c.relname\n" +
                "where t.table_schema = current_schema\n" +
                "and t.table_name not like 'schema_%' AND t.table_name not like 'flyway_%' and t.table_name not like 'mock_%' \n" +
                "and obj_description(c.oid) is null";
        List<String> avvik = new ArrayList<>();
        try (Connection conn = ds.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                avvik.add(rs.getString(1));
            }

        }

        assertThat(avvik).isEmpty();
    }

    @Test
    public void sjekk_at_alle_relevant_kolonner_er_dokumentert() throws Exception {
        List<String> avvik = new ArrayList<>();

        String sql = "SELECT c.table_name||'.'||c.column_name  " +
                "FROM pg_catalog.pg_statio_all_tables as st\n" +
                "       right join pg_catalog.pg_description pgd on (pgd.objoid=st.relid)\n" +
                "       right join information_schema.columns c on (pgd.objsubid=c.ordinal_position\n" +
                "                                                     and  c.table_schema=st.schemaname and c.table_name=st.relname)\n" +
                "WHERE c.table_schema = current_schema\n" +
                "AND upper(c.column_name) NOT IN ('OPPRETTET_TID','ENDRET_TID','OPPRETTET_AV','ENDRET_AV','VERSJON','BESKRIVELSE','NAVN','FOM', 'TOM','LAND', 'LANDKODE', 'KL_LANDKODE', 'KL_LANDKODER', 'AKTIV')\n" +
                "AND c.table_name not like 'schema_%'\n" +
                "AND c.table_name not like 'flyway_%'\n" +
                "AND c.table_name not like 'mock_%' \n" +
                "AND pgd.description is null\n" +
                "AND not exists (\n" +
                "          SELECT\n" +
                "                 1\n" +
                "          FROM\n" +
                "               information_schema.table_constraints AS tc\n" +
                "                 JOIN information_schema.key_column_usage AS kcu\n" +
                "                   ON tc.constraint_name = kcu.constraint_name\n" +
                "                        AND tc.table_schema = kcu.table_schema\n" +
                "          WHERE constraint_type IN ('FOREIGN KEY', 'PRIMARY KEY')\n" +
                "            AND tc.table_schema=current_schema\n" +
                "            AND tc.table_name = c.table_name and kcu.column_name = c.column_name\n" +
                "    )\n" +
                "ORDER BY c.table_name, c.column_name";

        try (Connection conn = ds.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                avvik.add("\n" + rs.getString(1));
            }

        }

        assertThat(avvik).withFailMessage("Mangler dokumentasjon for %s kolonner. %s\n %s", avvik.size(), avvik, HJELP).isEmpty();
    }


    @Test
    public void sjekk_alle_KL_kolonner_har_FK_referanser_til_Kodeliste() throws Exception {
        String sql = "select c.table_name, c.column_name from information_schema.columns c\n" +
                "    where column_name like 'kl_%'\n" +
                "    and table_schema = current_schema\n" +
                "    and (c.table_name, c.column_name) not in\n" +
                "        (select table_name, column_name from information_schema.key_column_usage kcu where kcu.column_name like 'kl_%' and kcu.constraint_name like 'fk_%'\n" +
                "         and kcu.constraint_catalog = c.table_catalog\n" +
                "        and kcu.constraint_schema = c.table_schema)";

        List<String> avvik = new ArrayList<>();
        StringBuilder tekst = new StringBuilder();
        try (Connection conn = ds.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);) {

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String t = rs.getString(1) + ", " + rs.getString(2);
                    avvik.add(t);
                    tekst.append(t).append("\n");
                }
            }

        }
        int sz = avvik.size();
        String beskrivelse = "Kolonner som starter med KL_ forventes å ha foreign key referanse til KODELISTE tabell (som må dekke både kode og kodeverk) og tilhørende indeks.";

        assertThat(avvik).withFailMessage(beskrivelse + sz + " foreign keys\n" + tekst).isEmpty();
    }

    @Test
    @Ignore // TODO
    public void sjekk_at_alle_FK_kolonner_har_fornuftig_indekser() throws Exception {
        String sql = "SELECT "
                + "  uc.table_name, uc.constraint_name, LISTAGG(dcc.column_name, ',') WITHIN GROUP (ORDER BY dcc.position) as columns" +
                " FROM all_Constraints Uc" +
                "   INNER JOIN all_cons_columns dcc ON dcc.constraint_name  =uc.constraint_name AND dcc.owner=uc.owner" +
                " WHERE Uc.Constraint_Type='R'" +
                "   AND Uc.Owner            = upper(?)" +
                "   AND Dcc.Column_Name NOT LIKE 'KL_%'" +
                "   AND EXISTS" +
                "       (SELECT ucc.position, ucc.column_name" +
                "         FROM all_cons_columns ucc" +
                "         WHERE Ucc.Constraint_Name=Uc.Constraint_Name" +
                "           AND Uc.Owner             =Ucc.Owner" +
                "           AND ucc.column_name NOT LIKE 'KL_%'" +
                "       MINUS" +
                "        SELECT uic.column_position AS position, uic.column_name" +
                "        FROM all_ind_columns uic" +
                "        WHERE uic.table_name=uc.table_name" +
                "          AND uic.table_owner =uc.owner" +
                "       )" +
                " GROUP BY Uc.Table_Name, Uc.Constraint_Name" +
                " ORDER BY uc.table_name";

        List<String> avvik = new ArrayList<>();
        StringBuilder tekst = new StringBuilder();
        try (Connection conn = ds.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);) {

            stmt.setString(1, schema);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String t = rs.getString(1) + ", " + rs.getString(2) + ", " + rs.getString(3);
                    avvik.add(t);
                    tekst.append(t).append("\n");
                }
            }

        }
        int sz = avvik.size();
        String manglerIndeks = "Kolonner som inngår i Foreign Keys skal ha indeker (ikke KL_ kolonner).\nMangler indekser for ";

        assertThat(avvik).withFailMessage(manglerIndeks + sz + " foreign keys\n" + tekst).isEmpty();

    }

    @Test
    public void skal_ha_KL_prefiks_for_kodeverk_kolonne_i_source_tabell() throws Exception {
        String sql = "select t1.relname as tabname, cs1.column_name as tabcol from\n" +
                "    pg_class t1, pg_class t2, information_schema.columns cs1, information_schema.columns cs2,\n" +
                "    lateral (select c.conname, c.conrelid, unnest(c.conkey) as conkeypos, c.confrelid, unnest(c.confkey) as confkeypos from pg_constraint c where c.contype = 'f') as fk\n" +
                "where fk.conrelid = t1.oid and fk.confrelid = t2.oid\n" +
                "  and t1.relname = cs1.table_name\n" +
                "  and t2.relname = cs2.table_name\n" +
                "  and conkeypos = cs1.ordinal_position\n" +
                "  and confkeypos = cs2.ordinal_position\n" +
                "  and t2.relname = 'kodeliste'\n" +
                "  and cs2.column_name = 'kodeverk'\n" +
                "  and cs1.column_name not like 'kl_%'\n" +
                "  and cs1.table_schema = current_schema\n" +
                "  and cs2.table_schema = current_schema\n" +
                "  and t1.relname not like 'kodeli%'";

        List<String> avvik = new ArrayList<>();
        StringBuilder tekst = new StringBuilder();
        try (Connection conn = ds.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);) {

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String t = rs.getString(1) + ", " + rs.getString(2);
                    avvik.add(t);
                    tekst.append(t).append("\n");
                }
            }

        }

        int sz = avvik.size();
        String feilTekst = "Feil navn på kolonner som refererer KODELISTE, skal ha 'KL_' prefiks. Antall feil=";

        assertThat(avvik).withFailMessage(feilTekst + sz + ".\n\nTabell, kolonne\n" + tekst).isEmpty();

    }

    @Test
    @Ignore
    public void skal_ha_virtual_column_definisjon_for_kodeverk_kolonne_i_source_tabell() throws Exception {
        String sql = "SELECT T.TABLE_NAME, T.CONSTRAINT_NAME, LISTAGG(COLC.COLUMN_NAME, ',') WITHIN GROUP (ORDER BY COLC.POSITION) AS COLUMNS FROM ALL_CONSTRAINTS T\n" +
                "INNER JOIN ALL_CONS_COLUMNS COLC ON COLC.CONSTRAINT_NAME=T.CONSTRAINT_NAME AND COLC.TABLE_NAME = T.TABLE_NAME AND COLC.OWNER=T.OWNER \n" +
                "WHERE T.OWNER = UPPER(?) AND COLC.OWNER = UPPER(?) \n" +
                "AND EXISTS\n" +
                "  (SELECT 1 FROM ALL_CONSTRAINTS UC\n" +
                "    INNER JOIN ALL_CONS_COLUMNS COLA ON COLA.CONSTRAINT_NAME=UC.CONSTRAINT_NAME AND COLA.OWNER =UC.OWNER \n" +
                "    INNER JOIN ALL_CONS_COLUMNS COLB ON COLB.CONSTRAINT_NAME=UC.R_CONSTRAINT_NAME AND COLB.OWNER =UC.OWNER AND COLB.OWNER=UC.OWNER AND COLB.OWNER=COLA.OWNER\n" +
                "    INNER JOIN ALL_TAB_COLS AT ON AT.COLUMN_NAME       =COLA.COLUMN_NAME AND AT.TABLE_NAME       =COLA.TABLE_NAME AND AT.OWNER =COLA.OWNER\n" +
                "    WHERE UC.CONSTRAINT_TYPE=T.CONSTRAINT_TYPE AND UC.CONSTRAINT_NAME = T.CONSTRAINT_NAME AND UC.OWNER = T.OWNER\n" +
                "      AND COLA.TABLE_NAME = T.TABLE_NAME AND T.TABLE_NAME=COLA.TABLE_NAME AND COLA.owner=T.OWNER AND COLA.CONSTRAINT_NAME=T.CONSTRAINT_NAME AND COLB.OWNER=T.OWNER \n" +
                "      AND COLB.COLUMN_NAME    ='KODEVERK' AND COLB.TABLE_NAME ='KODELISTE' AND COLB.POSITION =COLA.POSITION\n" +
                "      AND COLA.TABLE_NAME NOT LIKE 'KODELI%'\n" +
                "      AND AT.VIRTUAL_COLUMN='NO'\n" +
                "      AND UC.OWNER = UPPER(?) AND AT.OWNER = UPPER(?) AND COLA.OWNER = UPPER(?) AND COLB.OWNER = UPPER(?) \n" +
                "  )\n" +
                "\n" +
                "GROUP BY T.TABLE_NAME, T.CONSTRAINT_NAME\n" +
                "ORDER BY 1, 2";

        //System.out.println(sql);

        List<String> avvik = new ArrayList<>();
        StringBuilder tekst = new StringBuilder();
        try (Connection conn = ds.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);) {

            stmt.setString(1, schema);
            stmt.setString(2, schema);
            stmt.setString(3, schema);
            stmt.setString(4, schema);
            stmt.setString(5, schema);
            stmt.setString(6, schema);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String table = rs.getString(1);
                    String fk = rs.getString(2);
                    String cols = rs.getString(3);

                    if (ignoreColumn(table, cols)) {
                        continue;
                    }

                    @SuppressWarnings("unused")
                    String klCol = cols.split(",\\s*")[1];

                    String t = table + ", " + fk + ", " + cols;
                    avvik.add(t);
                    tekst.append(t).append("\n");
                }
            }

        }

        int sz = avvik.size();
        String feilTekst = "Feil definisjon på kolonner som refererer KODELISTE, definieres som virtual column, ikke med default eller annet. Antall feil=";

        assertThat(avvik).withFailMessage(feilTekst + sz + ".\n\nTabell, kolonne\n" + tekst).isEmpty();

    }

    private boolean ignoreColumn(String table, String cols) {
        String[][] ignored = new String[][]{
                {"IAY_INNTEKTSPOST", "KL_YTELSE_TYPE"},
                {"UTTAK_RESULTAT_PERIODE", "KL_PERIODE_RESULTAT_AARSAK"},
                {"HISTORIKKINNSLAG_FELT", "KL_FRA_VERDI"},
                {"HISTORIKKINNSLAG_FELT", "KL_TIL_VERDI"},
                {"HISTORIKKINNSLAG_FELT", "KL_NAVN"},
        };

        table = table.toUpperCase(Locale.getDefault());
        cols = cols.toUpperCase(Locale.getDefault());

        for (String[] ignore : ignored) {
            if (ignore[0].equals(table) && cols.contains(ignore[1])) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void skal_ha_primary_key_i_hver_tabell_som_begynner_med_PK() throws Exception {
        String sql = "SELECT t.table_name FROM information_schema.tables t where t.table_schema = current_schema\n" +
                "and t.table_name not like 'schema_%' AND t.table_name not like 'flyway_%'\n" +
                "and t.table_name not in\n" +
                "    (select c.table_name from information_schema.table_constraints c where c.constraint_type = 'PRIMARY KEY' and constraint_name like 'pk_%')";

        List<String> avvik = new ArrayList<>();
        StringBuilder tekst = new StringBuilder();
        try (Connection conn = ds.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);) {

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String t = rs.getString(1);
                    avvik.add(t);
                    tekst.append(t).append("\n");
                }
            }

        }

        int sz = avvik.size();
        String feilTekst = "Feil eller mangelende definisjon av primary key (skal hete 'pk_<tabell navn>'). Antall feil=";

        assertThat(avvik).withFailMessage(feilTekst + +sz + "\n\nTabell\n" + tekst).isEmpty();

    }

    @Test
    public void skal_ha_alle_foreign_keys_begynne_med_FK() throws Exception {
        String sql = "select table_name, constraint_name from information_schema.table_constraints\n" +
                "where constraint_type = 'FOREIGN KEY' and table_catalog=lower(?) and constraint_name NOT LIKE 'fk_%'";
        // ev. bytt ut table_catalog med table_schema

        List<String> avvik = new ArrayList<>();
        StringBuilder tekst = new StringBuilder();
        try (Connection conn = ds.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);) {

            stmt.setString(1, schema);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String t = rs.getString(1) + ", " + rs.getString(2);
                    avvik.add(t);
                    tekst.append(t).append("\n");
                }
            }

        }

        int sz = avvik.size();
        String feilTekst = "Feil eller mangelende definisjon av foreign key (skal hete 'FK_<tabell navn>_<løpenummer>'). Antall feil=";

        assertThat(avvik).withFailMessage(feilTekst + sz + "\n\nTabell, Foreign Key\n" + tekst).isEmpty();

    }

    @Test
    public void skal_ha_korrekt_index_navn() throws Exception {
        String sql = "select\n" +
                "       t.relname as table_name,\n" +
                "       i.relname as index_name,\n" +
                "       a.attname as column_name\n" +
                "from\n" +
                "     pg_class t,\n" +
                "     pg_class i,\n" +
                "     pg_index ix,\n" +
                "     pg_attribute a,\n" +
                "     information_schema.tables it\n" +
                "where\n" +
                "    t.oid = ix.indrelid\n" +
                "  and i.oid = ix.indexrelid\n" +
                "  and a.attrelid = t.oid\n" +
                "  and a.attnum = ANY(ix.indkey)\n" +
                "  and t.relkind = 'r'\n" +
                "  and t.relname = it.table_name\n" +
                "  and it.table_schema = current_schema\n" +
                "  and it.table_name not like 'schema_%' AND it.table_name not like 'flyway_%' and it.table_name not like 'mock_%'\n" +
                "  --and t.relname like 'test%'\n" +
                "  and i.relname not like 'pk_%' and i.relname not like 'idx_%' and i.relname not like 'uidx_%' and i.relname not like 'chk_%'\n" +
                "order by\n" +
                "         t.relname,\n" +
                "         i.relname;\n";

        List<String> avvik = new ArrayList<>();
        StringBuilder tekst = new StringBuilder();
        try (Connection conn = ds.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);) {

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String t = rs.getString(1) + ", " + rs.getString(2);
                    avvik.add(t);
                    tekst.append(t).append("\n");
                }
            }

        }

        int sz = avvik.size();
        String feilTekst = "Feil navngiving av index.  Primary Keys skal ha prefiks PK_, andre unike indekser prefiks UIDX_, vanlige indekser prefiks IDX_, unique constraints CHK_. Antall feil=";

        assertThat(avvik).withFailMessage(feilTekst + +sz + "\n\nTabell, Index, Kolonne\n" + tekst).isEmpty();

    }

    @Test
    public void skal_ha_samme_data_type_for_begge_sider_av_en_FK() throws Exception {
        String sql = "select t1.relname, cs1.column_name, cs1.data_type, cs1.character_maximum_length, cs1.character_octet_length,\n" +
                "    cs2.column_name, cs2.data_type, cs2.character_maximum_length, cs2.character_octet_length from\n" +
                "     pg_class t1, pg_class t2, information_schema.columns cs1, information_schema.columns cs2,\n" +
                "     lateral (select c.conname, c.conrelid, unnest(c.conkey) as conkeypos, c.confrelid, unnest(c.confkey) as confkeypos from pg_constraint c where c.contype = 'f') as fk\n" +
                "where fk.conrelid = t1.oid and fk.confrelid = t2.oid\n" +
                "  and t1.relname = cs1.table_name\n" +
                "  and t2.relname = cs2.table_name\n" +
                "  and conkeypos = cs1.ordinal_position\n" +
                "  and confkeypos = cs2.ordinal_position\n" +
                "  and cs1.table_schema = current_schema\n" +
                "  and cs2.table_schema = current_schema\n" +
                "\n" +
                "  and ((cs1.data_type != cs2.data_type)\n" +
                "         OR (cs1.character_maximum_length != cs2.character_maximum_length)\n" +
                "         OR (cs1.character_octet_length != cs2.character_octet_length)\n" +
                "         OR (cs1.numeric_precision != cs2.numeric_precision)\n" +
                "         OR (cs1.numeric_precision_radix != cs2.numeric_precision_radix)\n" +
                "         OR (cs1.numeric_scale != cs2.numeric_scale)\n" +
                "  )";

        List<String> avvik = new ArrayList<>();
        StringBuilder tekst = new StringBuilder();
        try (Connection conn = ds.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);) {

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String t = rs.getString(1) + ", " + rs.getString(2) + ", " + rs.getString(3) + ", " + rs.getString(4) + ", " + rs.getString(5) + ", " + rs.getString(6) + ", " + rs.getString(7) + ", " + rs.getString(8) + ", " + rs.getString(9);
                    avvik.add(t);
                    tekst.append(t).append("\n");
                }
            }

        }

        int sz = avvik.size();
        String feilTekst = "Forskjellig datatype for kolonne på hver side av en FK. Antall feil=";
        String cols = ".\n\nTABELL, KOL_A, KOL_A_DATA_TYPE, KOL_A_CHAR_LENGTH, KOL_A_CHAR_USED, KOL_B, KOL_B_DATA_TYPE, KOL_B_CHAR_LENGTH, KOL_B_CHAR_USED\n";

        assertThat(avvik).withFailMessage(feilTekst + +sz + cols + tekst).isEmpty();

    }


    @Test
    public void skal_ikke_bruke_FLOAT_REAL_eller_DOUBLEPRECISION() throws Exception {
        String sql = "select table_name, column_name, data_type From information_schema.columns where table_schema = current_schema and data_type in ('real', 'double precision')";

        List<String> avvik = new ArrayList<>();
        StringBuilder tekst = new StringBuilder();
        try (Connection conn = ds.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);) {

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String t = rs.getString(1) + ", " + rs.getString(2) + ", " + rs.getString(3);
                    avvik.add(t);
                    tekst.append(t).append("\n");
                }
            }

        }

        int sz = avvik.size();
        String feilTekst = "Feil bruk av datatype, skal ikke ha REAL/FLOAT eller DOUBLE PRECISION (bruk NUMBER for alle desimaltall, spesielt der penger representeres). Antall feil=";

        assertThat(avvik).withFailMessage(feilTekst + +sz + "\n\nTabell, Kolonne, Datatype\n" + tekst).isEmpty();

    }

}


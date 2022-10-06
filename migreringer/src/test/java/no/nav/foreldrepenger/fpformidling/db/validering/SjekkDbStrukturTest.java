package no.nav.foreldrepenger.fpformidling.db.validering;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.dbstoette.Databaseskjemainitialisering;

/**
 * Tester at alle migreringer følger standarder for navn og god praksis.
 */
public class SjekkDbStrukturTest {

    private static final String HJELP = "\n\nDu har nylig lagt til en ny tabell eller kolonne som ikke er dokumentert ihht. gjeldende regler for dokumentasjon."
            + "\nVennligst gå over sql scriptene og dokumenter tabellene på korrekt måte.";

    private static DataSource ds;

    private static String schema = "public";

    @BeforeAll
    public static void setup() throws FileNotFoundException {
        ds = Databaseskjemainitialisering.initUnitTestDataSource();
    }

    @Test
    void sjekk_at_alle_tabeller_er_dokumentert() throws Exception {
        var sql = """
                select t.table_name from information_schema.tables t
                join pg_class c on t.table_name = c.relname
                where t.table_schema = current_schema
                and t.table_name not like 'schema_%' AND t.table_name not like 'flyway_%'
                AND t.table_name not like 'prosess_task_partition_%'
                AND t.table_name not like 'prosess_task'
                and t.table_name not like 'mock_%'
                and obj_description(c.oid) is null
                """;
        List<String> avvik = new ArrayList<>();
        try (var conn = ds.getConnection(); var stmt = conn.prepareStatement(sql); var rs = stmt.executeQuery()) {

            while (rs.next()) {
                avvik.add(rs.getString(1));
            }

        }

        assertThat(avvik).isEmpty();
    }

    @Test
    void sjekk_at_alle_relevante_kolonner_er_dokumentert() throws Exception {
        List<String> avvik = new ArrayList<>();

        var sql = """
                SELECT c.table_name||'.'||c.column_name
                FROM pg_catalog.pg_statio_all_tables as st
                       right join pg_catalog.pg_description pgd on (pgd.objoid=st.relid)
                       right join information_schema.columns c on (pgd.objsubid=c.ordinal_position
                                                                     and  c.table_schema=st.schemaname and c.table_name=st.relname)
                WHERE c.table_schema = current_schema
                AND upper(c.column_name) NOT IN ('OPPRETTET_TID','ENDRET_TID','OPPRETTET_AV','ENDRET_AV','VERSJON','BESKRIVELSE','NAVN','FOM', 'TOM','LAND', 'LANDKODE', 'KL_LANDKODE', 'KL_LANDKODER', 'AKTIV')
                AND c.table_name not like 'schema_%'
                AND c.table_name not like 'flyway_%'
                AND c.table_name not like 'prosess_task_partition_%'
                AND c.table_name not like 'prosess_task'
                AND c.table_name not like 'mock_%'
                AND pgd.description is null
                AND not exists (
                          SELECT 1
                          FROM
                               information_schema.table_constraints AS tc
                                 JOIN information_schema.key_column_usage AS kcu
                                   ON tc.constraint_name = kcu.constraint_name
                                        AND tc.table_schema = kcu.table_schema
                          WHERE constraint_type IN ('FOREIGN KEY', 'PRIMARY KEY')
                            AND tc.table_schema=current_schema
                            AND tc.table_name = c.table_name and kcu.column_name = c.column_name
                    )
                ORDER BY c.table_name, c.column_name
                """;

        try (var conn = ds.getConnection(); var stmt = conn.prepareStatement(sql); var rs = stmt.executeQuery()) {

            while (rs.next()) {
                avvik.add("\n" + rs.getString(1));
            }

        }

        assertThat(avvik).withFailMessage("Mangler dokumentasjon for %s kolonner. %s\n %s", avvik.size(), avvik, HJELP).isEmpty();
    }

    @Disabled("Denne må tilpasses til Postgresql")
    @Test
    void sjekk_at_alle_FK_kolonner_har_fornuftig_indekser() throws Exception {
        var sql = "SELECT "
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
        var tekst = new StringBuilder();
        try (var conn = ds.getConnection(); var stmt = conn.prepareStatement(sql);) {

            stmt.setString(1, schema);

            try (var rs = stmt.executeQuery()) {

                while (rs.next()) {
                    var t = rs.getString(1) + ", " + rs.getString(2) + ", " + rs.getString(3);
                    avvik.add(t);
                    tekst.append(t).append("\n");
                }
            }

        }
        var sz = avvik.size();
        var manglerIndeks = "Kolonner som inngår i Foreign Keys skal ha indeker (ikke KL_ kolonner).\nMangler indekser for ";

        assertThat(avvik).withFailMessage(manglerIndeks + sz + " foreign keys\n" + tekst).isEmpty();
    }

    @Test
    void skal_ha_KL_prefiks_for_kodeverk_kolonne_i_source_tabell() throws Exception {
        var sql = "select t1.relname as tabname, cs1.column_name as tabcol from\n" +
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
        var tekst = new StringBuilder();
        try (var conn = ds.getConnection(); var stmt = conn.prepareStatement(sql);) {

            try (var rs = stmt.executeQuery()) {

                while (rs.next()) {
                    var t = rs.getString(1) + ", " + rs.getString(2);
                    avvik.add(t);
                    tekst.append(t).append("\n");
                }
            }

        }

        var sz = avvik.size();
        var feilTekst = "Feil navn på kolonner som refererer KODELISTE, skal ha 'KL_' prefiks. Antall feil=";

        assertThat(avvik).withFailMessage(feilTekst + sz + ".\n\nTabell, kolonne\n" + tekst).isEmpty();
    }

    @Test
    void skal_ha_primary_key_i_hver_tabell_som_begynner_med_PK() throws Exception {
        var sql = "SELECT t.table_name FROM information_schema.tables t where t.table_schema = current_schema\n" +
                "and t.table_name not like 'schema_%' AND t.table_name not like 'flyway_%'\n" +
                "AND t.table_name not like 'prosess_task_partition_%'\n" +
                "and t.table_name not in\n" +
                "    (select c.table_name from information_schema.table_constraints c where c.constraint_type = 'PRIMARY KEY' and constraint_name like 'pk_%')";

        List<String> avvik = new ArrayList<>();
        var tekst = new StringBuilder();
        try (var conn = ds.getConnection(); var stmt = conn.prepareStatement(sql);) {

            try (var rs = stmt.executeQuery()) {

                while (rs.next()) {
                    var t = rs.getString(1);
                    avvik.add(t);
                    tekst.append(t).append("\n");
                }
            }

        }

        var sz = avvik.size();
        var feilTekst = "Feil eller mangelende definisjon av primary key (skal hete 'pk_<tabell navn>'). Antall feil=";

        assertThat(avvik).withFailMessage(feilTekst + +sz + "\n\nTabell\n" + tekst).isEmpty();
    }

    @Test
    void skal_ha_alle_foreign_keys_begynne_med_FK() throws Exception {
        var sql = "select table_name, constraint_name from information_schema.table_constraints\n" +
                "where constraint_type = 'FOREIGN KEY' and table_catalog=lower(?) and constraint_name NOT LIKE 'fk_%'";
        // ev. bytt ut table_catalog med table_schema

        List<String> avvik = new ArrayList<>();
        var tekst = new StringBuilder();
        try (var conn = ds.getConnection(); var stmt = conn.prepareStatement(sql);) {

            stmt.setString(1, schema);

            try (var rs = stmt.executeQuery()) {

                while (rs.next()) {
                    var t = rs.getString(1) + ", " + rs.getString(2);
                    avvik.add(t);
                    tekst.append(t).append("\n");
                }
            }

        }

        var sz = avvik.size();
        var feilTekst = "Feil eller mangelende definisjon av foreign key (skal hete 'FK_<tabell navn>_<løpenummer>'). Antall feil=";

        assertThat(avvik).withFailMessage(feilTekst + sz + "\n\nTabell, Foreign Key\n" + tekst).isEmpty();
    }

    @Test
    void skal_ha_korrekt_index_navn() throws Exception {
        var sql = "select\n" +
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
                "  and it.table_name not like 'schema_%' AND it.table_name not like 'flyway_%' and it.table_name not like 'mock_%' AND it.table_name not like 'prosess_task_partition_%' " +
                "  --and t.relname like 'test%'\n" +
                "  and i.relname not like 'pk_%' and i.relname not like 'idx_%' and i.relname not like 'uidx_%' and i.relname not like 'chk_%'\n" +
                "order by\n" +
                "         t.relname,\n" +
                "         i.relname;\n";

        List<String> avvik = new ArrayList<>();
        var tekst = new StringBuilder();
        try (var conn = ds.getConnection(); var stmt = conn.prepareStatement(sql);) {

            try (var rs = stmt.executeQuery()) {

                while (rs.next()) {
                    var t = rs.getString(1) + ", " + rs.getString(2);
                    avvik.add(t);
                    tekst.append(t).append("\n");
                }
            }

        }

        var sz = avvik.size();
        var feilTekst = "Feil navngiving av index.  Primary Keys skal ha prefiks PK_, andre unike indekser prefiks UIDX_, vanlige indekser prefiks IDX_, unique constraints CHK_. Antall feil=";

        assertThat(avvik).withFailMessage(feilTekst + +sz + "\n\nTabell, Index, Kolonne\n" + tekst).isEmpty();
    }

    @Test
    void skal_ha_samme_data_type_for_begge_sider_av_en_FK() throws Exception {
        var sql = "select t1.relname, cs1.column_name, cs1.data_type, cs1.character_maximum_length, cs1.character_octet_length,\n" +
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
                "  AND cs1.table_name not like 'prosess_task_partition_%' " +
                "  AND cs2.table_name not like 'prosess_task_partition_%' " +
                "\n" +
                "  and ((cs1.data_type != cs2.data_type)\n" +
                "         OR (cs1.character_maximum_length != cs2.character_maximum_length)\n" +
                "         OR (cs1.character_octet_length != cs2.character_octet_length)\n" +
                "         OR (cs1.numeric_precision != cs2.numeric_precision)\n" +
                "         OR (cs1.numeric_precision_radix != cs2.numeric_precision_radix)\n" +
                "         OR (cs1.numeric_scale != cs2.numeric_scale)\n" +
                "  )";

        List<String> avvik = new ArrayList<>();
        var tekst = new StringBuilder();
        try (var conn = ds.getConnection(); var stmt = conn.prepareStatement(sql);) {

            try (var rs = stmt.executeQuery()) {

                while (rs.next()) {
                    var t = rs.getString(1) + ", " + rs.getString(2) + ", " + rs.getString(3) + ", " + rs.getString(4) + ", " + rs.getString(5) + ", " + rs.getString(6) + ", " + rs.getString(7) + ", " + rs.getString(8) + ", " + rs.getString(9);
                    avvik.add(t);
                    tekst.append(t).append("\n");
                }
            }

        }

        var sz = avvik.size();
        var feilTekst = "Forskjellig datatype for kolonne på hver side av en FK. Antall feil=";
        var cols = ".\n\nTABELL, KOL_A, KOL_A_DATA_TYPE, KOL_A_CHAR_LENGTH, KOL_A_CHAR_USED, KOL_B, KOL_B_DATA_TYPE, KOL_B_CHAR_LENGTH, KOL_B_CHAR_USED\n";

        assertThat(avvik).withFailMessage(feilTekst + +sz + cols + tekst).isEmpty();
    }

    @Test
    void skal_ikke_bruke_FLOAT_REAL_eller_DOUBLEPRECISION() throws Exception {
        var sql = "select table_name, column_name, data_type From information_schema.columns where table_schema = current_schema and data_type in ('real', 'double precision')";

        List<String> avvik = new ArrayList<>();
        var tekst = new StringBuilder();
        try (var conn = ds.getConnection(); var stmt = conn.prepareStatement(sql);) {

            try (var rs = stmt.executeQuery()) {

                while (rs.next()) {
                    var t = rs.getString(1) + ", " + rs.getString(2) + ", " + rs.getString(3);
                    avvik.add(t);
                    tekst.append(t).append("\n");
                }
            }

        }

        var sz = avvik.size();
        var feilTekst = "Feil bruk av datatype, skal ikke ha REAL/FLOAT eller DOUBLE PRECISION (bruk NUMBER for alle desimaltall, spesielt der penger representeres). Antall feil=";

        assertThat(avvik).withFailMessage(feilTekst + +sz + "\n\nTabell, Kolonne, Datatype\n" + tekst).isEmpty();
    }

}


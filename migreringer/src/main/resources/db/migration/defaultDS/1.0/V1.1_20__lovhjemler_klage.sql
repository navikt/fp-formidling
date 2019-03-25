-- Ikke vedtak
update KODELISTE
set ekstra_data = '{"klageAvvistAarsak":{"NFP": {"lovreferanser": ["28", "33"]},"KA": {"lovreferanser": ["28", "34"]}}}'
where kodeverk = 'KLAGE_AVVIST_AARSAK'
  and kode = 'IKKE_PAKLAGD_VEDTAK';
--  Ikke part i saken
update KODELISTE
set ekstra_data = '{"klageAvvistAarsak":{"NFP": {"lovreferanser": ["28", "33"]},"KA": {"lovreferanser": ["28", "34"]}}}'
where kodeverk = 'KLAGE_AVVIST_AARSAK'
  and kode = 'KLAGER_IKKE_PART';
-- Ikke underskrift
update KODELISTE
set ekstra_data = '{"klageAvvistAarsak":{"NFP": {"lovreferanser": ["31", "33"]},"KA": {"lovreferanser": ["31", "34"]}}}'
where kodeverk = 'KLAGE_AVVIST_AARSAK'
  and kode = 'IKKE_SIGNERT';
-- Klagefrist
update KODELISTE
set ekstra_data = '{"klageAvvistAarsak":{"NFP": {"lovreferanser": ["31", "33"]},"KA": {"lovreferanser": ["31", "34"]}}}'
where kodeverk = 'KLAGE_AVVIST_AARSAK'
  and kode = 'KLAGET_FOR_SENT';
-- Ikke konkret klage
update KODELISTE
set ekstra_data = '{"klageAvvistAarsak":{"NFP": {"lovreferanser": ["32", "33"]},"KA": {"lovreferanser": ["32", "34"]}}}'
where kodeverk = 'KLAGE_AVVIST_AARSAK'
  and kode = 'IKKE_KONKRET';

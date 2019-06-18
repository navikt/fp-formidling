insert into PROSESS_TASK_TYPE (kode, navn, feil_maks_forsoek, feilhandtering_algoritme, beskrivelse, cron_expression)
values ('retry.feilendeTasks', 'Retry av feilendetasks', 1, 'DEFAULT',
        'Kjører alle feilende tasks i henhold til cron-expreesion', '0 30 7 * * *');

Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'SVP_ARBEIDSFORHOLD_IKKE_OPPFYLT_AARSAK', '8312', null, 'Arbeidsgiver kan tilrettelegge frem til 3 uker før termin',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);

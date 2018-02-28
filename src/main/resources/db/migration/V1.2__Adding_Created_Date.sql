alter table flight_gar add created timestamp;
alter table flight_gar alter column created set default now();

update flight_gar
SET created = DEFAULT
where created is null;
create table attribute (
    gar_uuid uuid not null,
    name varchar(255) not null,
    value varchar(255),
    primary key (gar_uuid, name)
);

create table flight_crew (
    gar_uuid uuid not null,
    value_uuid uuid not null,
    primary key (gar_uuid, value_uuid)
);

create table flight_gar (
    gar_uuid uuid not null,
    aircraft_uuid uuid,
    captain_uuid uuid,
    last_modified timestamp,
    user_uuid uuid not null,
    primary key (gar_uuid)
);

create table flight_location (
    gar_uuid uuid not null,
    value_uuid uuid not null,
    location_order int4 not null,
    primary key (gar_uuid, value_uuid)
);

create table flight_passenger (
    gar_uuid uuid not null,
    value_uuid uuid not null,
    primary key (gar_uuid, value_uuid)
);

create table supporting_file_uuid (
    gar_uuid uuid not null,
    value_uuid uuid not null,
    primary key (gar_uuid, value_uuid)
);

create index userIdx on flight_gar (user_uuid);

alter table attribute
  add constraint FKgkldipbpgwgxe9mnc4waagcp3
  foreign key (gar_uuid)
  references flight_gar;

alter table flight_crew
  add constraint FKfgji24m76vql2jf08ysjkhxc7
  foreign key (gar_uuid)
  references flight_gar;

alter table flight_location
  add constraint FKk0u36etu7wleseknwx54em635
  foreign key (gar_uuid)
  references flight_gar;

alter table flight_passenger
  add constraint FKnhlym7r4msni6ljjawtutx4xm
  foreign key (gar_uuid)
  references flight_gar;

alter table supporting_file_uuid
  add constraint FK8py00huqo459rwqmj4j4phias
  foreign key (gar_uuid)
  references flight_gar;
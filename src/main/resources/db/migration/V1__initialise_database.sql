DROP SCHEMA IF EXISTS mcve CASCADE;

CREATE SCHEMA mcve;

CREATE DOMAIN mcve.external_id AS text;

CREATE TYPE mcve.access_right AS ENUM ('administrator', 'supervisor', 'foo', 'bar', 'baz', 'qux', 'quux');

CREATE TABLE mcve.test (
  id            text                PRIMARY KEY,
  access_rights mcve.access_right[] NOT NULL DEFAULT '{}',
  external_ids  mcve.external_id[]  NOT NULL DEFAULT '{}',

  access_right  mcve.access_right   NOT NULL,
  external_id   mcve.external_id    NOT NULL
);

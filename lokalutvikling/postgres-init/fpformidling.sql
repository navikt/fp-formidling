CREATE DATABASE fpformidling_unit;
CREATE USER fpformidling_unit WITH PASSWORD 'fpformidling_unit';
GRANT ALL PRIVILEGES ON DATABASE fpformidling_unit TO fpformidling_unit;
ALTER DATABASE fpformidling_unit SET timezone TO 'Europe/Oslo';

CREATE DATABASE fpformidling;
CREATE USER vl_dba WITH PASSWORD 'vl_dba';
CREATE USER fpformidling WITH PASSWORD 'fpformidling';
GRANT ALL PRIVILEGES ON DATABASE fpformidling TO vl_dba;
GRANT ALL PRIVILEGES ON DATABASE fpformidling TO fpformidling;
ALTER DATABASE fpformidling SET timezone TO 'Europe/Oslo';

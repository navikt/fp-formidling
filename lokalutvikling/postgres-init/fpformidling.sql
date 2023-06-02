CREATE DATABASE fpformidling_unit;
CREATE USER fpformidling_unit WITH PASSWORD 'fpformidling_unit';
GRANT ALL ON DATABASE fpformidling_unit TO fpformidling_unit;
ALTER DATABASE fpformidling_unit SET timezone TO 'Europe/Oslo';
ALTER DATABASE fpformidling_unit OWNER TO fpformidling_unit;

CREATE DATABASE fpformidling;
CREATE USER fpformidling WITH PASSWORD 'fpformidling';
GRANT ALL ON DATABASE fpformidling TO fpformidling;
ALTER DATABASE fpformidling SET timezone TO 'Europe/Oslo';
ALTER DATABASE fpformidling OWNER TO fpformidling;

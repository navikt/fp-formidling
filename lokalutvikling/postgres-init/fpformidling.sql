CREATE DATABASE fpformidling;
CREATE USER fpformidling WITH PASSWORD 'fpformidling';
GRANT ALL ON DATABASE fpformidling TO fpformidling;
ALTER DATABASE fpformidling SET timezone TO 'Europe/Oslo';
ALTER DATABASE fpformidling OWNER TO fpformidling;

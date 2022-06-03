CREATE TABLE users
(
    USERNAME VARCHAR(50)  NOT NULL,
    PASSWORD VARCHAR(100) NOT NULL,
    ENABLED  BOOLEAN      NOT NULL DEFAULT TRUE,
    PRIMARY KEY (USERNAME)
);

CREATE TABLE authorities
(
    USERNAME  VARCHAR(50) NOT NULL,
    AUTHORITY VARCHAR(50) NOT NULL,
    FOREIGN KEY (USERNAME) REFERENCES users (USERNAME)
);

CREATE UNIQUE INDEX ix_auth_username
    on authorities (USERNAME, AUTHORITY);
INSERT INTO public.users (username, password, enabled)
VALUES ('deswier', 'dXNlcjE6dXNlcjFQYXNz', DEFAULT);
INSERT INTO public.users (username, password, enabled)
VALUES ('zer0chance', 'dXNlcjE6dXNlcjFQYXNz', DEFAULT);
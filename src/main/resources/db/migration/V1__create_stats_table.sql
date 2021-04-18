CREATE TABLE stats (
  id Serial PRIMARY KEY,
  Time timestamp with time zone NOT NULL,
  customer text NOT NULL,
  content text NOT NULL,
  cdn bigint NOT NULL,
  p2p bigint NOT NULL);

CREATE INDEX customer_index
ON stats (customer);

CREATE INDEX content_index
ON stats (content);

CREATE INDEX index_picture_location IF NOT EXISTS FOR (p:Picture) ON (p.location)

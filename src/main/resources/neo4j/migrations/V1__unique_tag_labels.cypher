CREATE INDEX index_property_tag_label IF NOT EXISTS FOR (tag:Tag) ON (tag.label)
CREATE CONSTRAINT unique_property_tag_label IF NOT EXISTS ON (tag:Tag) ASSERT tag.label IS UNIQUE

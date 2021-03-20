CREATE CONSTRAINT unique_directory_location IF NOT EXISTS ON (d:Directory) ASSERT d.location IS UNIQUE;

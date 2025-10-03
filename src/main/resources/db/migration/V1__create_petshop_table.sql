CREATE TABLE IF NOT EXISTS petshop_pokemons (
  id UUID PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  types TEXT,
  available BOOLEAN NOT NULL,
  owner_id VARCHAR(255),
  created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_petshop_pokemons_available ON petshop_pokemons (available);

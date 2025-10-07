// MongoDB initialization script for Archetype
db = db.getSiblingDB('archetype');

// Create collections with validation
db.createCollection('pokemon', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['name'],
      properties: {
        name: {
          bsonType: 'string',
          description: 'Pokemon name - required'
        },
        types: {
          bsonType: 'array',
          items: { bsonType: 'string' },
          description: 'Pokemon types'
        }
      }
    }
  }
});

// Create indexes
db.pokemons.createIndex({ name: 1 }, { unique: true });

print('MongoDB initialization completed for Archetype database');

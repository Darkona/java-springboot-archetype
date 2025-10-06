// Initialize DB and collections for Species, Moves and Pokemon (Gen I canonical subset + full moves list parsed)
db = db.getSiblingDB('pokemon_db');

// Drop existing collections to make the script idempotent in dev environments (comment out in prod)
try { db.species.drop(); } catch (e) {}
try { db.moves.drop(); } catch (e) {}
try { db.pokemons.drop(); } catch (e) {}

// Create collections
db.createCollection('species');
db.createCollection('moves');
db.createCollection('pokemons');

// Indexes
db.species.createIndex({ nationalId: 1 }, { unique: true });
db.moves.createIndex({ number: 1 }, { unique: true });
db.pokemons.createIndex({ name: 1 }, { unique: true });

// ---------------------------
// Insert moves (parse from embedded TSV)
// ---------------------------

// Embedded Gen I moves data (columns: number<TAB>name<TAB>type<TAB>category<TAB>PP<TAB>Power<TAB>Accuracy<TAB>Gen)
const rawMoves = `1	Pound	Normal	Physical	35	40	100%	I
2	Karate Chop	Fighting	Physical	25	50	100%	I
3	Double Slap	Normal	Physical	10	15	85%	I
4	Comet Punch	Normal	Physical	15	18	85%	I
5	Mega Punch	Normal	Physical	20	80	85%	I
6	Pay Day	Normal	Physical	20	40	100%	I
7	Fire Punch	Fire	Physical	15	75	100%	I
8	Ice Punch	Ice	Physical	15	75	100%	I
9	Thunder Punch	Electric	Physical	15	75	100%	I
10	Scratch	Normal	Physical	35	40	100%	I
11	Vise Grip	Normal	Physical	30	55	100%	I
12	Guillotine	Normal	Physical	5	∞	30%	I
13	Razor Wind	Normal	Special	10	80	100%	I
14	Swords Dance	Normal	Status	20	—	—%	I
15	Cut	Normal	Physical	30	50	95%	I
16	Gust	Flying	Special	35	40	100%	I
17	Wing Attack	Flying	Physical	35	60	100%	I
18	Whirlwind	Normal	Status	20	—	∞%	I
19	Fly	Flying	Physical	15	90	95%	I
20	Bind	Normal	Physical	20	15	85%	I
21	Slam	Normal	Physical	20	80	75%	I
22	Vine Whip	Grass	Physical	25	45	100%	I
23	Stomp	Normal	Physical	20	65	100%	I
24	Double Kick	Fighting	Physical	30	30	100%	I
25	Mega Kick	Normal	Physical	5	120	75%	I
26	Jump Kick	Fighting	Physical	10	100	95%	I
27	Rolling Kick	Fighting	Physical	15	60	85%	I
28	Sand Attack	Ground	Status	15	—	100%	I
29	Headbutt	Normal	Physical	15	70	100%	I
30	Horn Attack	Normal	Physical	25	65	100%	I
31	Fury Attack	Normal	Physical	20	15	85%	I
32	Horn Drill	Normal	Physical	5	∞	30%	I
33	Tackle	Normal	Physical	35	40	100%	I
34	Body Slam	Normal	Physical	15	85	100%	I
35	Wrap	Normal	Physical	20	15	90%	I
36	Take Down	Normal	Physical	20	90	85%	I
37	Thrash	Normal	Physical	10	120	100%	I
38	Double-Edge	Normal	Physical	15	120	100%	I
39	Tail Whip	Normal	Status	30	—	100%	I
40	Poison Sting	Poison	Physical	35	15	100%	I
41	Twineedle	Bug	Physical	20	25	100%	I
42	Pin Missile	Bug	Physical	20	25	95%	I
43	Leer	Normal	Status	30	—	100%	I
44	Bite	Dark	Physical	25	60	100%	I
45	Growl	Normal	Status	40	—	100%	I
46	Roar	Normal	Status	20	—	∞%	I
47	Sing	Normal	Status	15	—	55%	I
48	Supersonic	Normal	Status	20	—	55%	I
49	Sonic Boom	Normal	Special	20	—	90%	I
50	Disable	Normal	Status	20	—	100%	I
51	Acid	Poison	Special	30	40	100%	I
52	Ember	Fire	Special	25	40	100%	I
53	Flamethrower	Fire	Special	15	90	100%	I
54	Mist	Ice	Status	30	—	—%	I
55	Water Gun	Water	Special	25	40	100%	I
56	Hydro Pump	Water	Special	5	110	80%	I
57	Surf	Water	Special	15	90	100%	I
58	Ice Beam	Ice	Special	10	90	100%	I
59	Blizzard	Ice	Special	5	110	70%	I
60	Psybeam	Psychic	Special	20	65	100%	I
61	Bubble Beam	Water	Special	20	65	100%	I
62	Aurora Beam	Ice	Special	20	65	100%	I
63	Hyper Beam	Normal	Special	5	150	90%	I
64	Peck	Flying	Physical	35	35	100%	I
65	Drill Peck	Flying	Physical	20	80	100%	I
66	Submission	Fighting	Physical	20	80	80%	I
67	Low Kick	Fighting	Physical	20	—	100%	I
68	Counter	Fighting	Physical	20	—	100%	I
69	Seismic Toss	Fighting	Physical	20	—	100%	I
70	Strength	Normal	Physical	15	80	100%	I
71	Absorb	Grass	Special	25	20	100%	I
72	Mega Drain	Grass	Special	15	40	100%	I
73	Leech Seed	Grass	Status	10	—	90%	I
74	Growth	Normal	Status	20	—	—%	I
75	Razor Leaf	Grass	Physical	25	55	95%	I
76	Solar Beam	Grass	Special	10	120	100%	I
77	Poison Powder	Poison	Status	35	—	75%	I
78	Stun Spore	Grass	Status	30	—	75%	I
79	Sleep Powder	Grass	Status	15	—	75%	I
80	Petal Dance	Grass	Special	10	120	100%	I
81	String Shot	Bug	Status	40	—	95%	I
82	Dragon Rage	Dragon	Special	10	—	100%	I
83	Fire Spin	Fire	Special	15	35	85%	I
84	Thunder Shock	Electric	Special	30	40	100%	I
85	Thunderbolt	Electric	Special	15	90	100%	I
86	Thunder Wave	Electric	Status	20	—	90%	I
87	Thunder	Electric	Special	10	110	70%	I
88	Rock Throw	Rock	Physical	15	50	90%	I
89	Earthquake	Ground	Physical	10	100	100%	I
90	Fissure	Ground	Physical	5	∞	30%	I
91	Dig	Ground	Physical	10	80	100%	I
92	Toxic	Poison	Status	10	—	90%	I
93	Confusion	Psychic	Special	25	50	100%	I
94	Psychic	Psychic	Special	10	90	100%	I
95	Hypnosis	Psychic	Status	20	—	60%	I
96	Meditate	Psychic	Status	40	—	—%	I
97	Agility	Psychic	Status	30	—	—%	I
98	Quick Attack	Normal	Physical	30	40	100%	I
99	Rage	Normal	Physical	20	20	100%	I
100	Teleport	Psychic	Status	20	—	—%	I
101	Night Shade	Ghost	Special	15	—	100%	I
102	Mimic	Normal	Status	10	—	∞%	I
103	Screech	Normal	Status	40	—	85%	I
104	Double Team	Normal	Status	15	—	—%	I
105	Recover	Normal	Status	5	—	—%	I
106	Harden	Normal	Status	30	—	—%	I
107	Minimize	Normal	Status	10	—	—%	I
108	Smokescreen	Normal	Status	20	—	100%	I
109	Confuse Ray	Ghost	Status	10	—	100%	I
110	Withdraw	Water	Status	40	—	—%	I
111	Defense Curl	Normal	Status	40	—	—%	I
112	Barrier	Psychic	Status	20	—	—%	I
113	Light Screen	Psychic	Status	30	—	—%	I
114	Haze	Ice	Status	30	—	—%	I
115	Reflect	Psychic	Status	20	—	—%	I
116	Focus Energy	Normal	Status	30	—	—%	I
117	Bide	Normal	Physical	10	—	∞%	I
118	Metronome	Normal	Status	10	—	—%	I
119	Mirror Move	Flying	Status	20	—	—%	I
120	Self-Destruct	Normal	Physical	5	200	100%	I
121	Egg Bomb	Normal	Physical	10	100	75%	I
122	Lick	Ghost	Physical	30	30	100%	I
123	Smog	Poison	Special	20	30	70%	I
124	Sludge	Poison	Special	20	65	100%	I
125	Bone Club	Ground	Physical	20	65	85%	I
126	Fire Blast	Fire	Special	5	110	85%	I
127	Waterfall	Water	Physical	15	80	100%	I
128	Clamp	Water	Physical	15	35	85%	I
129	Swift	Normal	Special	20	60	∞%	I
130	Skull Bash	Normal	Physical	10	130	100%	I
131	Spike Cannon	Normal	Physical	15	20	100%	I
132	Constrict	Normal	Physical	35	10	100%	I
133	Amnesia	Psychic	Status	20	—	—%	I
134	Kinesis	Psychic	Status	15	—	80%	I
135	Soft-Boiled	Normal	Status	5	—	—%	I
136	High Jump Kick	Fighting	Physical	10	130	90%	I
137	Glare	Normal	Status	30	—	100%	I
138	Dream Eater	Psychic	Special	15	100	100%	I
139	Poison Gas	Poison	Status	40	—	90%	I
140	Barrage	Normal	Physical	20	15	85%	I
141	Leech Life	Bug	Physical	10	80	100%	I
142	Lovely Kiss	Normal	Status	10	—	75%	I
143	Sky Attack	Flying	Physical	5	140	90%	I
144	Transform	Normal	Status	10	—	∞%	I
145	Bubble	Water	Special	30	40	100%	I
146	Dizzy Punch	Normal	Physical	10	70	100%	I
147	Spore	Grass	Status	15	—	100%	I
148	Flash	Normal	Status	20	—	100%	I
149	Psywave	Psychic	Special	15	—	100%	I
150	Splash	Normal	Status	40	—	—%	I
151	Acid Armor	Poison	Status	20	—	—%	I
152	Crabhammer	Water	Physical	10	100	90%	I
153	Explosion	Normal	Physical	5	250	100%	I
154	Fury Swipes	Normal	Physical	15	18	80%	I
155	Bonemerang	Ground	Physical	10	50	90%	I
156	Rest	Psychic	Status	5	—	—%	I
157	Rock Slide	Rock	Physical	10	75	90%	I
158	Hyper Fang	Normal	Physical	15	80	90%	I
159	Sharpen	Normal	Status	30	—	—%	I
160	Conversion	Normal	Status	30	—	—%	I
161	Tri Attack	Normal	Special	10	80	100%	I
162	Super Fang	Normal	Physical	10	—	90%	I
163	Slash	Normal	Physical	20	70	100%	I
164	Substitute	Normal	Status	10	—	—%	I
165	Struggle	Normal	Physical	1	50	∞%	I`;

// Parse moves
const hmNames = new Set(["Cut","Fly","Surf","Strength"]);
const moves = rawMoves.split('\n')
  .map(line => line.trim())
  .filter(line => line.length > 0)
  .map(line => {
      const parts = line.split(/\t+/);
      const number = parseInt(parts[0], 10);
      const name = parts[1];
      const element = parts[2] || null;
      const ppRaw = parts[4] || '';
      const powerRaw = parts[5] || '';
      const pp = (ppRaw === '—' || ppRaw === '' || isNaN(parseInt(ppRaw,10))) ? null : parseInt(ppRaw, 10);
      const power = (powerRaw === '—' || powerRaw === '' || powerRaw === '∞') ? null :
                    (powerRaw === '∞' ? null : parseInt(powerRaw, 10));
      const hm = hmNames.has(name);
      return {
          number: number,
          name: name,
          element: element,
          power: power,
          pp: pp,
          hm: hm
      };
  });

// Insert moves
if (moves.length > 0) {
    db.moves.insertMany(moves);
}

// ---------------------------
// Insert a small canonical species set (aligned with Species.java)
// Each species document follows a practical schema representing the Species record:
// {
//   nationalId: Int,
//   name: String,
//   firstType: String,      // element name string
//   secondType: String|null,
//   abilities: [String],
//   eggGroups: [String],    // use EggGroup enum names
//   baseHp: Int,
//   evolutions: [Int],      // nationalIds of evolutions
//   moves: [ { level: Int, moveNumber: Int } ]  // References to move.number
// }
// ---------------------------

const speciesDocs = [
  {
    nationalId: 1,
    name: "Bulbasaur",
    firstType: "Grass",
    secondType: "Poison",
    abilities: ["Overgrow","Chlorophyll"],
    eggGroups: ["GRASS","MONSTER"],
    evolutions: [2,3],
    moves: [
      { level: 1, moveNumber: 33 }, // Tackle
      { level: 3, moveNumber: 45 }, // Growl
      { level: 7, moveNumber: 73 }, // Leech Seed
      { level: 13, moveNumber: 22 }, // Vine Whip
      { level: 20, moveNumber: 77 }  // Poison Powder
    ]
  },
  {
    nationalId: 2,
    name: "Ivysaur",
    firstType: "Grass",
    secondType: "Poison",
    abilities: ["Overgrow","Chlorophyll"],
    eggGroups: ["GRASS","MONSTER"],
  
    evolutions: [3],
    moves: [
      { level: 1, moveNumber: 33 },
      { level: 1, moveNumber: 45 },
      { level: 7, moveNumber: 73 },
      { level: 13, moveNumber: 22 },
      { level: 25, moveNumber: 75 } // Razor Leaf
    ]
  },
  {
    nationalId: 3,
    name: "Venusaur",
    firstType: "Grass",
    secondType: "Poison",
    abilities: ["Overgrow","Chlorophyll"],
    eggGroups: ["GRASS","MONSTER"],
  
    evolutions: [],
    moves: [
      { level: 1, moveNumber: 33 },
      { level: 1, moveNumber: 45 },
      { level: 13, moveNumber: 22 },
      { level: 25, moveNumber: 75 },
      { level: 32, moveNumber: 76 } // Solar Beam
    ]
  },
  {
    nationalId: 4,
    name: "Charmander",
    firstType: "Fire",
    secondType: null,
    abilities: ["Blaze","Solar Power"],
    eggGroups: ["MONSTER","DRAGON"],
  
    evolutions: [5,6],
    moves: [
      { level: 1, moveNumber: 33 }, // Tackle
      { level: 1, moveNumber: 45 }, // Growl
      { level: 7, moveNumber: 52 }, // Ember
      { level: 34, moveNumber: 53 } // Flamethrower (later)
    ]
  },
  {
    nationalId: 7,
    name: "Squirtle",
    firstType: "Water",
    secondType: null,
    abilities: ["Torrent","Rain Dish"],
    eggGroups: ["WATER1","MONSTER"],
  
    evolutions: [8,9],
    moves: [
      { level: 1, moveNumber: 33 }, // Tackle
      { level: 1, moveNumber: 39 }, // Tail Whip
      { level: 7, moveNumber: 55 }, // Water Gun
      { level: 44, moveNumber: 110 } // Withdraw (learned later in some gens)
    ]
  },
  {
    nationalId: 25,
    name: "Pikachu",
    firstType: "Electric",
    secondType: null,
    abilities: ["Static","Lightning Rod"],
    eggGroups: ["FIELD","FAIRY"],
  
    evolutions: [26], // Raichu (26)
    moves: [
      { level: 1, moveNumber: 98 }, // Quick Attack
      { level: 1, moveNumber: 84 }, // Thunder Shock
      { level: 20, moveNumber: 85 }, // Thunderbolt
      { level: 50, moveNumber: 87 } // Thunder (high-level / TM/HM historically)
    ]
  },
  {
    nationalId: 133,
    name: "Eevee",
    firstType: "Normal",
    secondType: null,
    abilities: ["Run Away","Adaptability","Anticipation"],
    eggGroups: ["GROUND","FIELD"],
  
    evolutions: [134,135,136], // Vaporeon, Jolteon, Flareon
    moves: [
      { level: 1, moveNumber: 33 }, // Tackle
      { level: 1, moveNumber: 39 }, // Tail Whip
      { level: 98, moveNumber: 98 }  // Quick Attack (slot reused)
    ]
  }
];

db.species.insertMany(speciesDocs);

// ---------------------------
// Insert a few canonical Pokemon documents (persisted Pokemon instances).
// These follow the shape expected by PokemonDocument plus a speciesNationalId reference.
// ---------------------------

const samplePokemons = [
  {
    _id: UUID(),
    name: "Bulbasaur",
    speciesNationalId: 1,
    types: ["Grass","Poison"],
    abilities: ["Overgrow","Chlorophyll"],
    baseExperience: 64,
    height: 7,
    weight: 69
  },
  {
    _id: UUID(),
    name: "Charmander",
    speciesNationalId: 4,
    types: ["Fire"],
    abilities: ["Blaze","Solar Power"],
    baseExperience: 62,
    height: 6,
    weight: 85
  },
  {
    _id: UUID(),
    name: "Squirtle",
    speciesNationalId: 7,
    types: ["Water"],
    abilities: ["Torrent","Rain Dish"],
    baseExperience: 63,
    height: 5,
    weight: 90
  },
  {
    _id: UUID(),
    name: "Pikachu",
    speciesNationalId: 25,
    types: ["Electric"],
    abilities: ["Static","Lightning Rod"],
    baseExperience: 112,
    height: 4,
    weight: 60
  },
  {
    _id: UUID(),
    name: "Eevee",
    speciesNationalId: 133,
    types: ["Normal"],
    abilities: ["Run Away","Adaptability"],
    baseExperience: 65,
    height: 3,
    weight: 65
  }
];

db.pokemons.insertMany(samplePokemons);

// Done
print("Mongo init: inserted " + db.moves.count() + " moves, " + db.species.count() + " species, " + db.pokemons.count() + " pokemons.");

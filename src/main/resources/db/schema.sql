-- ============================================================
--  VetBook - структура на базата
--  Таблиците са в множествено число, колоните с малки букви и долна черта.
-- ============================================================

PRAGMA foreign_keys = ON;

-- ---------- Собственици ----------
CREATE TABLE IF NOT EXISTS owners (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    full_name  TEXT NOT NULL,
    phone      TEXT NOT NULL,
    email      TEXT,
    created_at TEXT NOT NULL DEFAULT (datetime('now'))
);

-- ---------- Животни ----------
-- gold_card_until е датата, докато важи златният картон.
-- Празно означава, че животното няма такъв.
CREATE TABLE IF NOT EXISTS animals (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    owner_id        INTEGER NOT NULL REFERENCES owners (id),
    species         TEXT    NOT NULL,
    name            TEXT    NOT NULL,
    breed           TEXT,
    birth_year      INTEGER,
    gold_card_until TEXT
);

-- ---------- Лекари ----------
CREATE TABLE IF NOT EXISTS doctors (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    full_name  TEXT    NOT NULL,
    speciality TEXT,
    active     INTEGER NOT NULL DEFAULT 1
);

-- ---------- Прегледи ----------
-- visit_at се пази като текст във вида 'YYYY-MM-DD HH:MM'.
-- Така подреждането по азбучен ред съвпада с подреждането по време.
-- Таксата се записва в самия преглед. Ако утре я променим, старите
-- прегледи трябва да си останат със сумата, която реално е била взета.
CREATE TABLE IF NOT EXISTS visits (
    id               INTEGER PRIMARY KEY AUTOINCREMENT,
    animal_id        INTEGER NOT NULL REFERENCES animals (id),
    doctor_id        INTEGER NOT NULL REFERENCES doctors (id),
    visit_at         TEXT    NOT NULL,
    complaint        TEXT,
    diagnosis        TEXT,
    status           TEXT    NOT NULL DEFAULT 'SCHEDULED',
    consultation_fee REAL    NOT NULL DEFAULT 0,
    created_at       TEXT    NOT NULL DEFAULT (datetime('now')),
    CHECK (status IN ('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'))
);

-- ---------- Процедури и медикаменти по преглед ----------
-- ON DELETE CASCADE значи: изтрие ли се преглед, редовете му си отиват с него.
CREATE TABLE IF NOT EXISTS visit_items (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    visit_id    INTEGER NOT NULL REFERENCES visits (id) ON DELETE CASCADE,
    item_type   TEXT    NOT NULL,
    description TEXT    NOT NULL,
    quantity    REAL    NOT NULL DEFAULT 1,
    unit_price  REAL    NOT NULL DEFAULT 0,
    CHECK (item_type IN ('EXAM', 'VACCINE', 'MANIPULATION', 'MEDICINE')),
    CHECK (quantity > 0),
    CHECK (unit_price >= 0)
);

-- ---------- Индекси ----------
-- Колоните, по които се търси и филтрира най-често.
CREATE INDEX IF NOT EXISTS ix_visits_visit_at   ON visits (visit_at);
CREATE INDEX IF NOT EXISTS ix_visits_status     ON visits (status);
CREATE INDEX IF NOT EXISTS ix_visits_doctor_day ON visits (doctor_id, visit_at);
CREATE INDEX IF NOT EXISTS ix_animals_owner     ON animals (owner_id);
CREATE INDEX IF NOT EXISTS ix_visit_items_visit ON visit_items (visit_id);

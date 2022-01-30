BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "pages" (
	"id"	INTEGER,
	"name"	TEXT NOT NULL UNIQUE,
	"url"	INTEGER NOT NULL,
	"instances"	INTEGER NOT NULL DEFAULT 0,
	PRIMARY KEY("id" AUTOINCREMENT)
);
INSERT INTO "pages" ("id","name","url","instances") VALUES (2,'google','google.com/',5);
COMMIT;

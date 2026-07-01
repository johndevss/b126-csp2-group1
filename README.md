# Barangay Healthcare Appointment System
---

## Prerequisites

You need these installed before touching the project:

|Tool|Check if installed|Notes|
|---|---|---|
|JDK 17+|`java -version`|We're using OpenJDK|
|MySQL Server|`mysql --version`|XAMPP's bundled MySQL works too|
|VS Code + Java Extension Pack|—|Or any IDE, but these steps assume VS Code|
|mysql-connector-j (JDBC driver)|see step 3 below|Lets Java talk to MySQL|

---

## 1. Clone the repo

```bash
git clone <repo-url> BHAS
cd BHAS
```

---

## 2. Set up your `.env` (your local config — never committed)
.

```bash
cp .env.example .env
```

Then open `.env` and fill in **your own** local values:

```
DB_HOST=127.0.0.1
DB_PORT=3306
DB_NAME=barangay_healthcare_db
DB_USER=root
DB_PASSWORD=your_password_here
```

- **XAMPP users:** leave `DB_PASSWORD` blank — XAMPP's MySQL has no password by default.
- **Native MySQL users:** use whatever password you set when you installed MySQL.

---

## 3. Install the MySQL JDBC driver

Java doesn't ship with a MySQL driver built in — you have to install one separately, similar to installing a plugin or a package with `apt`.

**If you're on Ubuntu / Debian and have the `.deb` package:**

```bash
sudo dpkg -i mysql-connector-j_*.deb
sudo apt-get install -f   # fixes any missing dependencies
```

Find where it landed:

```bash
dpkg -L mysql-connector-j | grep .jar
```

You should see something like:

```
/usr/share/java/mysql-connector-j-9.7.0.jar
```

**If you're on XAMPP / Windows / Mac**, download the jar directly from: https://dev.mysql.com/downloads/connector/j/

Place it anywhere in the project, e.g. `BHAS/lib/mysql-connector-j.jar`.

---

## 4. Create the database

Run the schema script to create all 6 tables + seed the service schedule:

```bash
mysql -u root -p < schema.sql
```

This creates the `barangay_healthcare_db` database and all tables. If you changed `DB_NAME` in your `.env`, make sure it matches what's in `schema.sql`, or edit `schema.sql`'s `CREATE DATABASE` line to match.

---

## 5. Configure VS Code

Create `.vscode/settings.json` in the project root (this file is **not** committed — like `.env`, it's specific to your machine):

```json
{
  "java.project.sourcePaths": ["src"],
  "java.project.outputPath": "bin",
  "java.project.referencedLibraries": [
    "/path/to/your/mysql-connector-j.jar"
  ]
}
```

Replace `/path/to/your/mysql-connector-j.jar` with the actual path you found in step 3.

Then reload the Java extension so it picks up the changes:

`Ctrl+Shift+P` → `Java: Clean Java Language Server Workspace` → **Reload and delete**

---

## 6. Test the connection

Before running the full app, verify your config is actually correct — same principle as testing a health check endpoint before deploying.

Open `src/com/barangay_healthcare_appointment_system/config/DBConnection.java` and run it directly (it has its own `main()` method built for this).

**Expected output:**

```
✅ Connected successfully to: jdbc:mysql://127.0.0.1:3306/barangay_healthcare_db?...
```

---

## Project Structure

```
BHAS/
├── .env                 ← your local DB credentials (git-ignored)
├── .env.example          ← template, safe to commit
├── .gitignore
├── schema.sql             ← run once to create tables
├── .vscode/
│   └── settings.json      ← your local IDE config (git-ignored)
└── src/com/barangay_healthcare_appointment_system/
    ├── Main.java
    ├── config/            ← DB connection + env loading
    ├── model/              ← one class per table
    ├── repository/         ← SQL queries, one per table
    ├── service/             ← business logic
    ├── controller/           ← ties view + service together per role
    └── view/                  ← CLI menus and input/output
```

---

## Running the app

```bash
javac -cp /path/to/mysql-connector-j.jar -d bin src/com/barangay_healthcare_appointment_system/**/*.java src/com/barangay_healthcare_appointment_system/*.java

java -cp bin:/path/to/mysql-connector-j.jar com.barangay_healthcare_appointment_system.Main
```

(On Windows, replace `:` with `;` in the `-cp` argument.)

Or just use VS Code's Run button once `Main.java` exists — it'll pick up the classpath from `.vscode/settings.json` automatically.

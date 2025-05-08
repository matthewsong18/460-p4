# 460-p4 — Ski Resort Database System

This project implements a ski resort's backend system using Oracle SQL and Java. It includes table creation, deletion, population, auditing, and trigger-based business logic.

---

##  Compilation (lectura.cs.arizona.edu)

Set the Oracle JDBC driver in your classpath:

```bash
export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:$CLASSPATH
```

Then compile all .java files under the src/ directory:
```bash
find src -name "*.java" | xargs javac
```
Table Initiaztion  
1. Create Table
   ```bash
   java tables.create_tables
   ```
    * Drops existing tables (safe FK order).
    * Creates full schema with all constraints.
    * Enables standard auditing on Equipment.
    * Adds 4 triggers for lift difficulty, SkiPass usage, and archival.
2. Populate Tables
    ```bash
    java tables.populate_tables
    ```
    * Populates tables from CSVs in the data/ directory.

3. Delete Table
      ```bash
     java tables.delete_tables
      ```
    * Drops all schema tables in FK-safe order.

## To Access the Auditng Data for Equipment Table
  ```bash
      SELECT * FROM USER_AUDIT_TRAIL WHERE OBJ_NAME = 'EQUIPMENT';
  ````

## Workload Distribution

Backend: Ethan Huang, Matthew Song
UI, Queries: Elias Zheng
Database, Triggers: Stephin Tomson

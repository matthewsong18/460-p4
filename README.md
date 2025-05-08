# 460-p4 — Ski Resort Database System

This project implements a ski resort's backend system using Oracle SQL and Java. It includes table creation, deletion, population, auditing, and trigger-based business logic.
## Group 5: Authors:
### Elias Zheng
### Stephin Tomson
### Matthew William Song
### Ethan Huang
---

##  Compilation (lectura.cs.arizona.edu)

Set the Oracle JDBC driver in your classpath:

```bash
export CLASSPATH=/usr/lib/oracle/19.8/client64/lib/ojdbc8.jar:$CLASSPATH
```
#### Compile All
Then compile all .java files under the src/ directory:
```bash
find src -name "*.java" | xargs javac
```

#### Table Initiaztion  
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

#### Run the View
Username - Oracle Username
Password - Oracle Password
java Prog4 [username] [password]

#### To Access the Auditng Data for Equipment Table
  ```bash
      SELECT * FROM USER_AUDIT_TRAIL WHERE OBJ_NAME = 'EQUIPMENT';
  ````
#### Accessing Main Menu
```bash
1 | Add, delete, or update a member.
2 | Add, delete, or update a ski pass.
3 | Add, delete, or update an equipment record.
4 | Add, update, or delete a rental record.
5 | Add, update, or delete a lesson purchase record.
Type 'query' to access the queries submenu.
Type 'exit' to close the application.
```

Typing ```bash 1-5 ``` option will give the prompt below:
```bash
1 | Add a <record type>
2 | Delete a <record type>
3 | Update a <record type>
Type 'back' to go back.
```

Typing ```query``` Option will give the prompt below:
```bash 
1  | List all ski lessons purchased for a member
2  | List all lift and rental records for a ski pass
3  | List all intermediate-level trails and associated information
4  | List the total wage expense for a given property
5  | List all members
6  | List all active ski passes
7  | List all equipment
8  | List all rentals
9  | List all lessons
10 | List all lesson orders
Type 'back' to return to the main menu.
```


## Workload Distribution

```Backend: Ethan Huang, Matthew Song```
```UI, Queries: Elias Zheng```
```Database, Triggers: Stephin Tomson```
``` Debugging and Testing: Etha Huang, Matthew Song, Stephin Tomson```


### Elias Zheng
    *  Prog4.java
    *  QueryService
    *  Rental.java
    *  RentalDatabaseAccess.java
    *  RentalService.java
    *  Equipment.java
    *  EquipmentDatabaseAccess.java
    *  EquipmentService.java
### Stephin Tomson
    *  create_tables.java 
    *  delete_tables.java
    *  populate_tables.java
    *  Prog4.java
    *  data(all csv files)
    *  Rental.java
    *  RentalDatabaseAccess.java
    *  RentalService.java
    *  Equipment.java
    *  EquipmentDatabaseAccess.java
    *  EquipmentService.java
    *  MemberService.java
    *  MemberDatabaseAccess.java
### Matthew William Song
    * DatabaseAccess.java  
    * DatabaseConnection.java
    * LessonService.java
    * Member.java
    * Prog4.java
    * MemberService.java
    * MemberDatabaseAccess.java
    * Property.java
    * PropertyDatabaseAccess.java
    * PropertyService.java
    * SkiPass.java
    * SkiPassDatabaseAccess.java
    * SkiPassService.java
### Ethan Huang
    * DatabaseAccess.java  
    * DatabaseConnection.java
    * InstructorService.java
    * LessonOrder.java
    * LessonOrderDatabaseAccess.java
    * LessonOrderService.java
    * LessonService.java
    * Prog4.java
    * PropertyService.java
    * Rental.java
    * RentalDatabaseAccess.java
    * RentalService.java
    * SkiPass.java
    * SkiPassDatabaseAccess.java
    * SkiPassService.java

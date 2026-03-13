# IPOS-SA

This is the repository for **Group 31A**. This repository stores **exclusively code**. To access other resources such as the Team Binder or Team Drive, see the **Resources** section below.

<br>

## Resources

* **Team Drive**: [Link Here](https://cityuni-my.sharepoint.com/:f:/r/personal/rached_hakkar_city_ac_uk/Documents/Team%2031%20project?csf=1&web=1&e=WZqjuq)
* **Team Binder**: [Link Here](https://www.notion.so/Team-A-Project-Binder-IPOS-SA-2fc765c10d1380aa9b22d8e9174d7ca4?source=copy_link)
* **Project Brief**: [Link Here](https://moodle4.city.ac.uk/mod/resource/view.php?id=1188148)

<br>

## Project Structure

This Project uses **Java 21**, make sure you have this version of the JDK installed

Here is a quick overview of the project file structure:

`pom.xml`
- This is a Maven file. This file defines the dependencies of this project `sqlite, JavaFX`
- IntelliJ will automatically read this file, and install all the dependencies you need for your platform. 
- Don't touch this file unless you are adding more dependencies

`mvnw`, `mvnw.cmd` and `.mvn`
- These are the Maven wrappers, needed for Maven to work. If you don't have Maven installed, run these scripts.

`.gitattributes`
- This file tells Git about how to handle differences between Windows and Mac/Linux

`.gitignore`
- Tells Git which files to ignore, IDE and OS folders should be included

`src`
- This is the folder for all our Java source code

#### Inside `src`

`java`
- This is the directory that contains our Java packages
- The IPOS subsystems `ACC`, `CAT`, `ORD` and `RPT` are contained here as packages. Work within these
- `ipos.sa` is the main system package, this contains the entry point for the code, which has
  - Login screen
  - Subsystem selection menu
- `db` this is the package that interfaces with the database, use the methods from db when making an SQL query
- **IMPORTANT:** This is the only place for SQL queries, don't use them anywhere else

`resources`
- This directory **must mirror** the java package structure.
- Inside of these directories, you can put resources that that package needs
- In JavaFX, you write the structure for the window in FXML. These files should be stored here

**Example:** FXML files for IPOS-RPT should be stored in `resources/rpt`

## Roles
* **Project Manager:** Tom
* **Deputy Manager:** Anu
* **System Analysts:** Anu, Neil, Tharun
* **Designers:** Rached, Fariha, Abdullah
* **Programmers:** Rached, Fariha, Abdullah, Tom
* **Testers:** Neil, Tharun

<br>

## Role Overviews

#### Project Manager (Tom)
- Manage communication between the team, client, and other groups
- Manage risks by reviewing and checking others work
- Authorize the final submission of work
- Delegate tasks to the appropriate people
- Maintain a general direction for the group
- Set internal deadlines for assignments

#### Deputy Manager (Anu)
- Assist the PM and fill in if the PM is unavailable.
- Manage the Project Binder and document team meetings.

#### System Analysts
- *Role to be clarified by the PM.*

#### Designers
- Create UML-compliant Use Case and Class Diagrams for each package.
- Ensure diagrams are uploaded to the Team Drive **before coding begins**.

#### Programmers
- Develop **ONLY** your assigned Java package to avoid merge conflicts.
- Ensure code matches the UML diagrams provided by Designers.
- **Pushing directly to `main` is blocked.**
    1. Clone the repo and create a feature branch (e.g., `name/feature-description`).
    2. Once a feature is complete, open a **Pull Request (PR)** on GitHub.
    3. Wait for Tester confirmation and PM approval before the code is merged.

**Assignments:**
- **Fariha:** `ACC` package
- **Rached:** `CAT` package
- **Abdullah:** `RPT` package
- **Tom:** `ORD` package

#### Testers
- For every PR, develop test cases for critical functionality (OOAD template).
- Upload test documentation to the Team Drive.
- Comment on the GitHub PR confirming testing is complete so the PM can merge.

#### Everybody
- Maintain an **Individual Diary** documenting your work. Update this weekly.

<br>

## Important Dates

- **Every Monday:** Team Meeting @ 2:00 PM
- **16th April:** Project Demo
- **19th April:** **Final Deadline**
    - Implementation Report, Individual Reports, Project Binder, and Diaries.

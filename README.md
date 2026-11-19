# Java MSE Lab Exam Solution - `com.kiet`

A complete, high-performance Java implementation containing both **Question 1 (Raw JDBC Menu-Driven Application)** and **Question 2 (Spring Boot REST API & Full-Stack Web System)**.

---

## 🛠️ Project Structure

- **Question 1 (JDBC Console App):** `src/main/java/com/kiet/jdbc/StudentJdbcApp.java`
- **Question 2 (Spring Boot Full-Stack):**
  - **Entity:** `src/main/java/com/kiet/entity/Student.java`
  - **Repository:** `src/main/java/com/kiet/repository/StudentRepository.java`
  - **Service:** `src/main/java/com/kiet/service/StudentService.java`
  - **Controller:** `src/main/java/com/kiet/controller/StudentController.java`
  - **Main Class:** `src/main/java/com/kiet/Application.java`
  - **Frontend Dashboard:** `src/main/resources/static/index.html`
  - **Configuration:** `src/main/resources/application.properties`

---

## 🚀 Question 1: Raw JDBC Menu-Driven Application (`CO4`)

### Features Implemented:
1. **Encapsulated Student Model:** Fields for Student ID, Name, Email, Course, Programming Score, Mathematics Score, and Technical Skills.
2. **Automated Schema Initialization:** Auto-executes `CREATE TABLE IF NOT EXISTS students` via JDBC `Statement`.
3. **Calculated Metrics:** Dynamic calculation of average scores and identification of stronger subjects (Programming vs. Mathematics).
4. **Grading / Performance Classification:**
   - $\ge 85.0$: **Excellent**
   - $70.0 - 84.99$: **Good**
   - $50.0 - 69.99$: **Satisfactory**
   - $< 50.0$: **Needs Improvement**
5. **Interactive Parametric Search:** Search by Student ID, Email, or Course using secure `PreparedStatement` queries.
6. **Merit List Generation:** Ranked in descending order of average score.
7. **In-Memory Grouping:** Maps and aggregates students sharing the same technical skills using `Map<String, List<String>>`.
8. **Special Filter Query:** Displays students who need programming improvement (`< 50`) while having strong mathematics scores (`>= 75`).
9. **Strict Validation:** Prevents duplicate Student IDs and validates scores strictly within $[0, 100]$.

### How to Run:
Run `src/main/java/com/kiet/jdbc/StudentJdbcApp.java` as a standard Java Application in Eclipse or terminal.

---

## 🌐 Question 2: Spring Boot REST API & Full-Stack Web Application (`CO5`)

### Tech Stack:
- **Framework:** Spring Boot 4 / Java 21+
- **Persistence:** Spring Data JPA / Hibernate
- **Database:** MySQL (`localhost:3306`, user: `root`)
- **Frontend:** Responsive HTML5 / CSS / Vanilla JavaScript Fetch API (Warm Amber & Deep Navy Enterprise Theme)

### REST Endpoints:
| Method | Endpoint | Description | Status Code |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/students` | Register a new student (validates score bounds and uniqueness) | `201 Created` |
| `GET` | `/api/students` | Retrieve all student records | `200 OK` |
| `GET` | `/api/students/{id}` | Search student record by unique ID | `200 OK` / `404 Not Found` |
| `PUT` | `/api/students/{id}` | Update student programming/math scores and technical skills | `200 OK` / `404 Not Found` |
| `DELETE` | `/api/students/{id}` | Delete student record | `200 OK` / `404 Not Found` |
| `GET` | `/api/students/merit-list` | Retrieve merit list sorted by average score descending | `200 OK` |

### Web Portal:
Access the interactive dashboard at: `http://localhost:8080/`

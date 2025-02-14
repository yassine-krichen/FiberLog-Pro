
# Agile Scrum Artifacts for FiberLog Pro

---

## 1. Product Backlog

| ID | User Story Title                | Description                                                                                                                                                   | Acceptance Criteria                                                                                                                                                                                                                                                                                                                  |
|----|---------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1  | **File Upload & Data Processing**   | As an operator, I want to upload work log files (CSV/Excel) so that the system automatically processes and stores my data.                                   | - Upload page with drag-and-drop functionality using NG-ZORRO.<br>- File is parsed (using Apache POI/Commons CSV) and validated.<br>- Success/failure notification displayed.                                                                                                                                                     |
| 2  | **Dashboard & KPI Visualization**    | As an operator, I want to view a dashboard with KPI cards and charts so that I can monitor performance metrics at a glance.                                  | - KPI cards showing total hours, average hours, etc.<br>- Interactive charts reflecting data trends.                                                                                                                                                                                                                               |
| 3  | **Work Log Table Display**             | As an operator, I want to see a detailed table of work log entries with sorting and filtering options so that I can review data entries.                     | - Table integrated into the dashboard.<br>- Basic filtering and sorting available.                                                                                                                                                                                                                                                   |
| 4  | **PDF Report Generation**              | As an operator, I want to generate a PDF report summarizing KPIs and recent work logs for offline review. (Optional for MVP)                                  | - A button to trigger PDF generation using Apache PDFBox.<br>- Downloadable PDF report provided.                                                                                                                                                                                                                                     |
| 5  | **API Documentation**                  | As a developer, I want auto-generated API documentation so that I can quickly understand the available endpoints.                                          | - Swagger UI / Springdoc OpenAPI accessible at `/swagger-ui.html`.                                                                                                                                                                                                                                                                   |

---

## 2. Sprint Backlog & Sprint Planning

**Sprint Goal:**  
*Implement the core features of FiberLog Pro including file upload & processing, a dynamic KPI dashboard, and a detailed work log table.*

**Sprint Duration:** 1 Week

| Task ID | Task Description                                                                                                                                   | Estimated Duration | Responsible         |
|---------|-----------------------------------------------------------------------------------------------------------------------------------------------------|--------------------|---------------------|
| S1      | **Setup & Environment Configuration:** Configure project repositories, setup Spring Boot and Angular projects, integrate PostgreSQL and Docker.   | 0.5 day            | All Team Members    |
| S2      | **File Upload & Data Processing:** Build the upload page (NG-ZORRO), develop the `POST /api/upload` endpoint, integrate file parsing, persist data. | 1.5 days           | Backend/Frontend    |
| S3      | **KPI Dashboard & Work Log Table:** Create the dashboard page in Angular, implement KPI cards, integrate charting library, and develop the table.    | 1.5 days           | Frontend Team       |
| S4      | **API Documentation:** Integrate Springdoc OpenAPI for auto-generated API documentation.                                                            | 0.5 day            | Backend Team        |
| S5      | **Testing & Quality Assurance:** Write unit tests (JUnit for backend, Jasmine/Karma for Angular), integration tests, and perform code reviews.       | 1 day              | QA/All              |
| S6      | **Review & Demo Preparation:** Finalize features, perform integration testing, and prepare the demo walkthrough.                                     | 1 day              | All Team Members    |

*Definition of Done:*  
- Code is merged after successful code reviews.  
- All new features are covered by unit/integration tests.  
- Functionality is demonstrated in a working demo.  
- Documentation (including API docs) is updated.

---

## 3. Testing Strategy

| Test Type          | Tools/Frameworks                   | Description                                                                                                   |
|--------------------|------------------------------------|---------------------------------------------------------------------------------------------------------------|
| **Unit Testing**       | JUnit 5 (Spring Boot Starter Test), Jasmine, Karma | Test individual service methods, components, and utilities in both backend and frontend.                       |
| **Integration Testing** | Postman, RestAssured             | Verify interactions between endpoints, file upload, data parsing, and persistence using sample files.          |
| **End-to-End Testing**   | Cypress (or Protractor)          | Simulate complete user workflows: file upload process, dashboard data refresh, and PDF report generation.       |

*Additional Setup:*  
- Use a dedicated testing database for integration tests.  
- Ensure tests run automatically via CI/CD once the pipeline is in place.

---

## 4. Architectural Decision Record (ADR)

| ADR ID    | Title                     | Context                                                                                                  | Decision                                                                                                                       | Consequences                                                                                                                                  | Status    |
|-----------|---------------------------|----------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|-----------|
| ADR-001   | Technology Stack Choice   | Need a technology stack that supports rapid development, scalability, and ease of integration.            | Use **Angular 11** for the frontend, **Spring Boot 2.5.x (Java 1.8)** for the backend, **PostgreSQL** for persistence, and **Docker** for containerization. | - Benefits: Robust ecosystem, mature libraries, and ease of integration.<br>- Future upgrades can be planned without major refactoring. | Accepted  |

---

## 5. Risk Assessment

| Risk                          | Impact | Likelihood | Mitigation Strategy                                                                                     |
|-------------------------------|--------|------------|---------------------------------------------------------------------------------------------------------|
| **Integration Issues**        | Medium | Medium     | Perform early integration tests with various sample files; add robust error handling and logging.       |
| **Scope Creep**               | High   | Low        | Strictly adhere to sprint scope and implement change control mechanisms.                               |
| **Performance Bottlenecks**   | Medium | Low        | Optimize file parsing routines; implement pagination and batch processing if necessary.                |
| **Team Coordination Issues**  | Medium | Medium     | Hold regular daily stand-ups; establish clear API contracts using OpenAPI documentation.               |
| **Testing Deficiencies**      | High   | Medium     | Establish a comprehensive testing strategy early on; automate tests; enforce rigorous code reviews.    |

---

*With these artifacts, FiberLog Pro is set on a clear path for development, ensuring we meet our goals while maintaining quality and agility. Let's move forward and build a robust solution that revolutionizes fiber deployment efficiency!*

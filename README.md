# FiberLog Pro  
*Intelligent Work Log Analyzer & KPI Dashboard for Fiber Deployment Efficiency*

---

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Architecture & Tech Stack](#architecture--tech-stack)
- [Data Model & Entities](#data-model--entities)
- [REST API Endpoints](#rest-api-endpoints)
- [Frontend Structure](#frontend-structure)
- [Workflow Example](#workflow-example)
- [Installation & Deployment](#installation--deployment)
- [Upcoming Features](#upcoming-features)
- [Contributing](#contributing)
- [License](#license)

---

## Overview

**FiberLog Pro** is a cutting-edge web application designed to streamline the deployment and maintenance of fiber optic networks. By automating data ingestion, KPI calculations, and report generation, FiberLog Pro eliminates manual errors, reduces delays, and enhances operational efficiency—all in a sleek, user-friendly interface.

Built with **Angular 11** and **Spring Boot 2.5.x** on Java 1.8, this application leverages modern technologies and libraries to deliver a robust, scalable solution that integrates seamlessly with your existing infrastructure.

---

## Features

- **Automated Data Ingestion:**  
  Upload daily work logs via Excel or CSV files using an intuitive drag-and-drop interface powered by NG-ZORRO’s Upload component.
  
- **Data Validation & Processing:**  
  Parse and validate records using **Apache POI** for Excel and **Apache Commons CSV** for CSV, ensuring high data quality.
  
- **Real-time KPI Dashboard:**  
  Visualize key metrics such as total hours logged, average hours per employee, project breakdowns, and more with interactive charts and tables.
  
- **Dynamic REST API:**  
  Access detailed work log entries and KPI summaries through well-documented REST endpoints.
  
- **PDF Reporting (Optional):**  
  Generate downloadable PDF reports summarizing project performance using **Apache PDFBox**.
  
- **Secure & Maintainable Codebase:**  
  Utilize **Lombok** for reduced boilerplate, **Hibernate Envers** for audit trails, and **Springdoc OpenAPI** for seamless API documentation.
  
- **Containerized Deployment:**  
  Leverage Docker and GitLab CI/CD for streamlined build, test, and deployment pipelines.

---

## Architecture & Tech Stack

### Backend (Spring Boot 2.5.x, Java 1.8)
- **Spring Boot 2.5.x** for building RESTful APIs.
- **Spring Data JPA** with PostgreSQL for persistence.
- **Hibernate Envers** for entity versioning and audit tracking.
- **Apache POI 5.2.3** & **Apache Commons CSV 1.8** for file parsing.
- **Lombok 1.18.22** for reducing boilerplate.
- **Springdoc OpenAPI 1.5.10** for API documentation.
- **JUnit 5.7.2** for testing.

### Frontend (Angular 11)
- **Angular 11** for the application framework.
- **NG-ZORRO 11.4.2** for a modern Ant Design UI.
- **ng2-charts 3.0.0** (or similar) for interactive data visualization.
- **Jasmine & Karma** for unit and integration testing.

### DevOps & Deployment
- **Docker** for containerization.
- **GitLab CI/CD** for continuous integration and deployment.

---

## Data Model & Entities

### 1. WorkLogEntry
Represents a single record from an uploaded work log file.

| Field            | Type         | Description                                  |
| ---------------- | ------------ | -------------------------------------------- |
| `id`             | Long         | Auto-generated primary key                   |
| `date`           | LocalDate    | Date when the work was performed             |
| `employeeName`   | String       | Name of the employee                         |
| `projectName`    | String       | Name of the project (e.g., Fiber Deployment) |
| `taskDescription`| String       | Description of the work/task                 |
| `hoursLogged`    | Double       | Number of hours worked                       |
| `createdAt`      | Timestamp    | Record creation timestamp                    |
| `updatedAt`      | Timestamp    | Last update timestamp                        |

### 2. FileUploadMetadata (Optional)
Tracks details of each file upload.

| Field           | Type      | Description                                  |
| --------------- | --------- | -------------------------------------------- |
| `id`            | Long      | Auto-generated primary key                   |
| `fileName`      | String    | Name of the uploaded file                    |
| `uploadDate`    | Timestamp | Date and time of the upload                  |
| `status`        | String    | Status (e.g., SUCCESS, FAILED)               |
| `numberOfRecords`| Integer  | Count of processed work log entries          |

---

## REST API Endpoints

### File Upload & Data Processing
- **POST `/api/upload`**  
  Accepts a multipart file (CSV/Excel) and processes its contents:
  - Detects file type and uses **Apache POI** or **Apache Commons CSV** for parsing.
  - Validates data (e.g., date formats, non-negative hours).
  - Persists validated records as **WorkLogEntry** entities.
  - Optionally logs upload metadata via **FileUploadMetadata**.

### Work Log Retrieval
- **GET `/api/worklogs`**  
  Retrieves a list (or paginated results) of all work log entries for review.

### KPI Calculation
- **GET `/api/kpis`**  
  Computes and returns key performance indicators such as:
  - Total hours logged
  - Average hours per employee
  - Number of tasks completed
  - Hours breakdown by project

### PDF Report Generation (Optional)
- **GET `/api/report/pdf`**  
  Generates a PDF report summarizing KPIs and a sample of work log entries using **Apache PDFBox**.

### API Documentation
- **GET `/v3/api-docs`** or **`/swagger-ui.html`**  
  Auto-generated API documentation via **Springdoc OpenAPI**.

---

## Frontend Structure

### Dashboard Page
- **KPI Cards:** Displays key metrics like total hours, average hours, etc.
- **Interactive Charts:** Graphs showing trends (e.g., hours per project over time).
- **Work Log Table:** Detailed table (using NG-ZORRO) for recent entries with filtering and sorting capabilities.

### File Upload Page
- **Upload Form:**  
  - Drag-and-drop file upload powered by NG-ZORRO’s Upload component.
  - Trigger button to submit the file.
- **Status Notifications:**  
  - Alerts or messages indicating the success or failure of uploads.

### Work Log Detail Modal (Optional)
- **Modal Dialog:**  
  - Opens upon selecting a table row, displaying detailed information about the chosen work log entry.

---

## Workflow Example

1. **Prepare the Data:**  
   Create an Excel or CSV file with daily work logs. For example:

   | Date       | Employee Name | Project Name      | Task Description                      | Hours Logged |
   |------------|---------------|-------------------|---------------------------------------|--------------|
   | 2025-02-10 | John Doe      | Fiber Deployment  | Installed fiber cables in District A  | 8            |
   | 2025-02-10 | Jane Smith    | Fiber Maintenance | Routine maintenance in District B     | 6            |

2. **Upload the File:**  
   - Navigate to the File Upload page.
   - Use the drag-and-drop interface to select and upload your file.
   - The file is processed via **POST `/api/upload`**, validating and persisting each record.

3. **Dashboard Refresh:**  
   - The dashboard auto-refreshes (or via a manual refresh button).
   - **GET `/api/kpis`** is invoked to fetch the latest KPI metrics.
   - The dashboard displays interactive charts and tables with up-to-date work log data.

4. **Generate Report (Optional):**  
   - Click the “Generate PDF Report” button.
   - **GET `/api/report/pdf`** is triggered, returning a downloadable PDF summary.

---

## Installation & Deployment

### Prerequisites
- **Java 1.8**
- **Node.js & npm**
- **Angular CLI**
- **PostgreSQL**
- **Docker (optional for containerization)**
- **GitLab CI/CD** (for automated deployment)

### Backend Setup
1. Clone the repository.
2. Navigate to the backend directory.
3. Build the Spring Boot application:
   ```bash
   ./mvnw clean install
   ```
4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

### Frontend Setup
1. Navigate to the frontend directory.
2. Install dependencies:
   ```bash
   npm install
   ```
3. Run the Angular application:
   ```bash
   ng serve --open
   ```

### Docker Deployment
- Use the provided `Dockerfile` and `docker-compose.yml` to containerize the app.
- Configure GitLab CI/CD for automated builds and deployments.

---

## Upcoming Features

- **Enhanced Data Analytics:**  
  Integrate advanced statistical models and machine learning for predictive insights.

- **User Authentication & Role-Based Access:**  
  Secure sensitive data with fine-grained access control.

- **Mobile-Responsive Design:**  
  Optimize the UI for mobile devices to enable field-based log submissions.

- **Advanced Workflow Automation:**  
  Incorporate BPM engines (like Camunda BPM) for complex, multi-step process automation.

- **Real-Time Notifications:**  
  Push alerts for data anomalies or upload issues.

---

## Contributing

We welcome contributions from the community! Please follow these steps:
1. Fork the repository.
2. Create a feature branch: `git checkout -b feature/my-new-feature`
3. Commit your changes: `git commit -am 'Add new feature'`
4. Push to the branch: `git push origin feature/my-new-feature`
5. Open a pull request.

---

## License

This project is licensed under the [MIT License](LICENSE).

---

*Experience the future of fiber deployment efficiency with FiberLog Pro — where automation meets precision. Join us in revolutionizing the way work logs and KPIs are managed!*

Made with ❤️‍🔥 By noel & kaos_king
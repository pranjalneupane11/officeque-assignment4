# OfficeQue - Assignment 4 Testing Prototype

**Course:** CSCI 5801 Software Engineering, Youngstown State University, Fall 2026  
**Project team:** Pranjal Neupane, Shreya Adhikari, Shreeyan Simkhada

This repository contains a **working subset** of the proposed OfficeQue faculty office-hours system. It implements two server-side features from Assignment 3:

1. Join an active virtual office-hour queue without creating duplicate active entries.
2. Request an appointment that is saved as `PENDING`, with conflicts rejected using HTTP 409.

It also implements business rules for appointment intervals, overlap detection, queue-state transitions and queue-position calculation. This is a testable backend prototype, **not** a claim that the full OfficeQue React application is finished.

## Requirements

- Java Development Kit (JDK) **21**
- Apache Maven **3.9+**
- Internet access on the first run to download Maven dependencies

No PostgreSQL server, API key, live external service or frontend installation is needed to run these tests.

## Download and run the tests

Open a terminal in the cloned repository root (the folder containing `pom.xml`):

```bash
git clone https://github.com/pranjalneupane11/officeque-assignment4.git
cd officeque-assignment4
java -version
mvn -version
mvn clean test
```

Expected after a successful run:

```text
Tests run: 27, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

The line above is the *expected outcome*, not a substitute for the real output. Use your own run and screenshots as evidence. Maven Surefire also stores XML reports under `target/surefire-reports/`.

### Run each test category separately

```bash
# Four isolated unit-test classes, 18 tests total
mvn -Dtest=AppointmentTimeValidatorTest,AppointmentConflictPolicyTest,QueueTransitionPolicyTest,QueuePositionCalculatorTest test

# Two component-test classes, 9 tests total
mvn -Dtest=QueueApiComponentTest,AppointmentApiComponentTest test
```

If using Windows PowerShell and Maven interprets the comma-separated value unexpectedly, quote the argument:

```powershell
mvn '-Dtest=QueueApiComponentTest,AppointmentApiComponentTest' test
```

## What is tested?

| Category | Class | Tests | Scope |
|---|---|---:|---|
| Unit | `AppointmentTimeValidatorTest` | 5 | Valid duration, exact 60-minute limit, over-limit seconds, zero length, past time |
| Unit | `AppointmentConflictPolicyTest` | 4 | Partial overlap, containment, touching endpoints, separate intervals |
| Unit | `QueueTransitionPolicyTest` | 5 | Faculty start/complete, student leave, forbidden transitions |
| Unit | `QueuePositionCalculatorTest` | 4 | Position 1, position 4, ignore terminal entries, unknown entry |
| Component | `QueueApiComponentTest` | 5 | Controller + security + service + repository + H2 database |
| Component | `AppointmentApiComponentTest` | 4 | Controller + security + service + repository + H2 database |
| **Total** | | **27** | **18 unit and 9 component tests** |

Each unit test instantiates the policy directly without a Spring application context or database. Each component test sends HTTP requests through Spring `MockMvc`, exercises the actual controller and service, checks the persisted H2 records, and rolls back test data with `@Transactional`. The application therefore does not modify production data.

## Optional: start the local demo API

```bash
mvn spring-boot:run
```

Demo credentials (local demonstration **only**):

- `student1` / `demo-only`
- `student2` / `demo-only`
- `faculty1` / `demo-only`

The POST endpoints are `/api/queue` and `/api/appointments`. Because this is a small testing prototype, office-hour sessions are created directly by test fixtures; the local application does not include a session creation endpoint.

Example appointment request using `curl`:

```bash
curl -u student1:demo-only -H "Content-Type: application/json" \
  -d '{"facultyId":1,"startTime":"2030-03-08T14:00:00","endTime":"2030-03-08T14:30:00"}' \
  http://localhost:8080/api/appointments
```

Successful requests return HTTP **201 Created** with `"status":"PENDING"` and an awaiting-approval message. An overlapping request returns HTTP **409 Conflict**; an invalid interval returns HTTP **400 Bad Request**.

## Relationship to Assignment 3 and honest implementation changes

The Assignment 3 architecture proposed React + Spring Boot + PostgreSQL with controller/service/repository layers. This implementation preserves the **Spring Boot layered backend** and the business rules from the request-appointment and join-queue sequence diagrams, but intentionally narrows the first working scope:

- **Implemented:** queue enrollment, duplicate-entry checks, active-session validation, derived queue position, appointment validation, sequential overlap checks, `PENDING` requests, minimal role restrictions, automated tests.
- **Local test storage:** an **H2 in-memory database** rather than PostgreSQL, allowing reproducible tests with no external database. PostgreSQL remains the planned production datastore.
- **Temporary authentication:** local HTTP Basic with predefined demonstration accounts. Replace it with the planned real identity provider and stored password hashes before production use.
- **Not implemented:** React pages, full faculty administration, messages, meeting history, appointment approval, deployment and real-time queue polling.
- **Remaining limitation:** the appointment conflict query prevents sequential overlapping requests, but a full deployment still needs concurrency-safe database locking or exclusion constraints for requests arriving simultaneously. Queue duplicate detection likewise needs concurrency protection in a multi-user deployment.

This restricted scope is intentional and consistent with Assignment 4, which requires tested **working features** rather than the entire system.

## Suggested report evidence

1. Run `mvn clean test` and capture the completed terminal output including overall result.
2. Run the unit-test command above and capture its output.
3. Run the component-test command above and capture its output.
4. Include a table for each test showing input, expected outcome, actual outcome, and pass/fail status.
5. Record actual bugs fixed and remaining limitations. Do not report planned or unexecuted tests as passing.
6. Add the **actual publicly accessible GitHub repository URL** to both the PDF and this README before submission.

## Source layout

```text
src/main/java/edu/ysu/officeque/
  domain/          Appointment, QueueEntry, OfficeHourSession, statuses
  policy/          Four isolated business-rule units
  repository/      Spring Data JPA repository interfaces
  service/         QueueService and AppointmentService
  web/             REST controllers, error mapping, demo security
src/test/java/edu/ysu/officeque/
  unit/            18 isolated automated unit tests
  component/       9 Spring Boot + H2 component tests
```

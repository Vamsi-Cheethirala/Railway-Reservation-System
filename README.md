# Railway Reservation System

A console-based **Railway Reservation System** developed using Core Java. This project simulates essential railway reservation operations, including train management, passenger registration, individual ticket booking, group booking, seat allocation, ticket cancellation, and administrative operations.

The application demonstrates practical implementation of Object-Oriented Programming, Java Collections Framework, exception handling, enums, Java Date and Time API, input validation, and transaction-style rollback mechanisms.

---

## 1. Project Overview

The Railway Reservation System provides two main interfaces:

- **Admin Module:** Manage trains, view passengers and bookings, adjust journey seat capacity, and remove trains or specific journey dates.
- **Passenger Module:** Register, log in, search for trains, check seat availability, book tickets, view bookings, and cancel reservations.

The application maintains train, passenger, ticket, group booking, and journey information in memory using Java Collections.

**Note:** This is a Core Java console application. It does not currently use a database or permanent file-based storage. Application data is initialized when the program starts and is lost when it terminates.

---

## 2. Features

### Admin Module

- Admin authentication.
- Add new trains.
- View available trains.
- Remove a train from the system.
- Remove a train for a specific journey date.
- View registered passengers.
- View individual ticket bookings.
- View group bookings.
- Increase or decrease journey seat capacity.
- Prevent removal of trains or journeys with confirmed bookings.
- Validate administrative operations before modifying application data.

### Passenger Module

- New passenger registration.
- Existing passenger login.
- Search trains by source and destination.
- Check journey-wise seat availability.
- View available seats by travel class.
- Book individual tickets.
- Book tickets for groups of travellers.
- View individual bookings.
- View group bookings.
- Cancel individual tickets.
- Cancel individual travellers within a group booking.

### Booking Management

- Automatic PNR generation.
- Automatic passenger ID generation.
- Random available seat allocation.
- Separate seat inventory for AC, Sleeper, and General classes.
- Individual and group booking status management.
- Automatic seat release after successful cancellation.
- Prevention of repeated cancellation.
- Group booking ownership validation.
- Group booking rollback when an allocation operation fails.
- Prevention of duplicate train numbers.

### Input Validation and Error Handling

- Passenger name validation.
- Age validation.
- Phone number validation.
- Email validation.
- Password validation.
- Integer input validation.
- Date and time format validation.
- Travel class and enum validation.
- Custom exceptions for invalid passengers, missing trains, unavailable seats, and missing bookings.

### Demo Data Management

- Automatic loading of sample trains and passengers.
- Preloaded individual and group bookings.
- Sample journey inventory.
- Direct route coverage validation.
- Demo booking occupancy validation.
- Group booking consistency checks.
- Duplicate booked-seat detection.

---

## 3. Technologies and Concepts Used

| Technology / Concept   | Application                                                  |
| ---------------------- | ------------------------------------------------------------ |
| Java                   | Core application development                                 |
| OOP                    | Designing models and application components                  |
| Collections Framework  | Managing trains, passengers, tickets, bookings, and journeys |
| ArrayList              | Maintaining application records                              |
| HashMap                | Managing journey inventory                                   |
| HashSet                | Tracking removed journey identifiers                         |
| Enums                  | Gender, TravelClass, SeatStatus, and BookingStatus           |
| Exception Handling     | Handling invalid operations and application errors           |
| Custom Exceptions      | Domain-specific error handling                               |
| Java Date and Time API | Managing journey dates and train timings                     |
| Java Random            | Selecting available seats                                    |
| VS Code                | Development environment                                      |
| Git and GitHub         | Version control and project hosting                          |

**Database:** Not used. The current implementation uses in-memory storage.

---

## 4. Project Structure

The project follows a package-based architecture that separates the application entry point, business logic, models, enumerations, utilities, and custom exceptions.

```text
Railway Reservation System/
│
├── .vscode/
│   └── settings.json
│
├── .gitignore
│
├── bin/
│   └── com/
│       └── railway/
│           ├── enums/
│           ├── exceptions/
│           ├── main/
│           ├── model/
│           ├── service/
│           └── util/
│
├── docs/
│   ├── class-diagram.md
│   └── mermaid-diagram.png
│
├── src/
│   └── com/
│       └── railway/
│           │
│           ├── main/
│           │   ├── Main.java
│           │   ├── TestMain.java
│           │   ├── InputValidatorTest.java
│           │   ├── TrainJourneyTest.java
│           │   ├── SeatTicketTest.java
│           │   ├── BookingIntegrationTest.java
│           │   ├── DemoDataValidatorTest.java
│           │   ├── MainInputTest.java
│           │   ├── MainMenuIntegrationTest.java
│           │   └── FinalRegressionTest.java
│           │
│           ├── model/
│           │   ├── Train.java
│           │   ├── Passenger.java
│           │   ├── Seat.java
│           │   ├── Ticket.java
│           │   ├── TrainJourney.java
│           │   ├── GroupBooking.java
│           │   └── TravellerBooking.java
│           │
│           ├── service/
│           │   ├── RailwaySystem.java
│           │   ├── DataInitializer.java
│           │   └── DemoDataValidator.java
│           │
│           ├── enums/
│           │   ├── Gender.java
│           │   ├── TravelClass.java
│           │   ├── SeatStatus.java
│           │   └── BookingStatus.java
│           │
│           ├── util/
│           │   ├── InputValidator.java
│           │   ├── PNRGenerator.java
│           │   └── PassengerIDGenerator.java
│           │
│           └── exceptions/
│               ├── BookingNotFoundException.java
│               ├── InvalidPassengerException.java
│               ├── SeatNotAvailableException.java
│               └── TrainNotFoundException.java
│
└── README.md
```

### Package Responsibilities

| Package      | Responsibility                                              |
| ------------ | ----------------------------------------------------------- |
| `main`       | Application entry point and test classes                    |
| `model`      | Represents trains, passengers, seats, tickets, and bookings |
| `service`    | Implements railway operations and demo data management      |
| `enums`      | Defines fixed application states and categories             |
| `util`       | Provides input validation and ID generation                 |
| `exceptions` | Contains custom application exceptions                      |

**Folder information:**

- `src` contains Java source files.
- `bin` contains compiled `.class` files.
- `.vscode` contains VS Code configuration.
- `docs` contains project documentation and UML diagrams.

---

## 5. UML Class Diagram

The UML class diagram represents the application's architecture, including its model classes, service layer, utility classes, enumerations, custom exceptions, and their relationships.

## UML Class Diagram

[View UML Class Diagram](docs/class-diagram.md)

The diagram covers the following major components:

- `RailwaySystem` – Central service class responsible for railway operations.
- `Train` and `TrainJourney` – Train information and journey-specific seat inventory.
- `Passenger` and `Ticket` – Passenger information and individual reservations.
- `GroupBooking` and `TravellerBooking` – Group reservation management.
- `Seat` – Seat allocation and status management.
- Enums and custom exceptions – Application states and error handling.

---

## 6. Core Classes

### Model Package

| Class              | Responsibility                                                   |
| ------------------ | ---------------------------------------------------------------- |
| `Train`            | Stores train details and its seat configuration                  |
| `Passenger`        | Stores passenger registration information                        |
| `Seat`             | Represents an individual seat and its status                     |
| `TrainJourney`     | Maintains seat inventory for a particular train and journey date |
| `Ticket`           | Represents an individual passenger reservation                   |
| `GroupBooking`     | Maintains a group reservation and its travellers                 |
| `TravellerBooking` | Stores an individual traveller's details and booking status      |

### Service Package

**RailwaySystem.java**

The main business logic class. It manages:

- Train registration and removal.
- Passenger registration and lookup.
- Individual ticket booking and cancellation.
- Group booking and traveller cancellation.
- Journey inventory creation and lookup.
- Seat availability and capacity adjustment.
- Administrative operations and authorization.

**DataInitializer.java**

Creates the application's sample data, including trains, passengers, and bookings.

**DemoDataValidator.java**

Checks the consistency of initialized demo data, including route coverage, occupancy, group bookings, and duplicate seat assignments.

### Utility Package

- `InputValidator.java` – Validates passenger and user input.
- `PNRGenerator.java` – Generates ticket and group booking reference numbers.
- `PassengerIDGenerator.java` – Generates passenger identifiers.

### Exceptions Package

- `TrainNotFoundException`
- `InvalidPassengerException`
- `SeatNotAvailableException`
- `BookingNotFoundException`

These exceptions represent common application-level error conditions.

---

## 7. Booking Workflows

### Individual Ticket Booking

1. Passenger logs into the application.
2. Passenger searches for a train using source and destination.
3. Passenger selects a train, journey date, and travel class.
4. System checks the journey's available seat inventory.
5. Passenger details are validated.
6. An available seat is selected and booked.
7. A unique PNR is generated.
8. A confirmed ticket is created and displayed.

### Group Ticket Booking

1. Passenger selects a train, journey date, and travel class.
2. Passenger enters the details of the travellers.
3. System validates the traveller list.
4. System checks whether sufficient seats are available.
5. Seats are allocated to the travellers.
6. Individual traveller booking records are created.
7. A group booking record is maintained with its own PNR.

The system supports group bookings of **2 to 6 travellers**.

If an operation fails after seat allocation begins, previously allocated seats are released to restore the journey inventory.

### Ticket Cancellation

1. Passenger selects an existing booking.
2. System validates the booking and ownership where applicable.
3. The booking status changes from `CONFIRMED` to `CANCELLED`.
4. The corresponding seat is released.
5. Repeated cancellation attempts are rejected.

For group bookings, individual travellers can be cancelled separately. The group booking status is updated when all travellers have been cancelled.

---

## 8. Testing and Validation

The project includes separate test classes for unit testing, integration testing, input validation, demo data validation, and console menu navigation.

### Test Suite Results

| Test Suite              |  Passed | Failed |
| ----------------------- | ------: | -----: |
| TestMain                |      63 |      0 |
| InputValidatorTest      |      23 |      0 |
| TrainJourneyTest        |      30 |      0 |
| SeatTicketTest          |      16 |      0 |
| BookingIntegrationTest  |      16 |      0 |
| DemoDataValidatorTest   |       3 |      0 |
| MainInputTest           |      13 |      0 |
| MainMenuIntegrationTest |       8 |      0 |
| **Total**               | **172** |  **0** |

### Final Regression Test

| Metric                 |     Result |
| ---------------------- | ---------: |
| Test suites executed   |          8 |
| Passed test cases      |        172 |
| Failed test cases      |          0 |
| Suite execution errors |          0 |
| Overall result         | **PASSED** |

### Important Test Scenarios

- Admin authentication and authorization.
- Train addition, removal, and duplicate train rejection.
- Passenger registration and validation.
- Individual ticket booking and cancellation.
- Group booking with multiple travellers.
- Rejection of invalid group booking requests.
- Insufficient seat availability.
- Repeated cancellation prevention.
- Seat inventory consistency.
- Group booking rollback after simulated failure.
- Journey capacity adjustment.
- Invalid console inputs.
- Passenger and admin menu navigation.
- Demo data consistency and duplicate seat detection.

**Result: All 8 regression test suites passed, with 172 reported test cases passed and zero failures.**

---

## 9. How to Run the Project

### Prerequisites

- Java Development Kit (JDK 17 or later recommended).
- Visual Studio Code.
- Java Extension Pack for VS Code (recommended).

### Run Using VS Code

1. Extract the project ZIP file.
2. Open the `Railway Reservation System` folder in VS Code.
3. Ensure the `src` directory is configured as the Java source folder.
4. Open `src/com/railway/main/Main.java`.
5. Run the `main()` method.

The application initializes demo data and displays the demo data validation report before opening the main menu.

### Run Using Command Line

Open a terminal in the project root directory.

**Step 1: Compile the source files**

For Windows PowerShell:

```powershell
New-Item -ItemType Directory -Force -Path bin

$files = Get-ChildItem -Path src -Recurse -Filter *.java |
    ForEach-Object { $_.FullName }

javac -d bin $files
```

**Step 2: Run the application**

```bash
java -cp bin com.railway.main.Main
```

**Step 3: Run the final regression test**

```bash
java -cp bin com.railway.main.FinalRegressionTest
```

The compiled classes are generated inside the `bin` directory.

---

## 10. Learning Outcomes

Through this project, the following concepts were implemented and practised:

- Object-Oriented Programming using Java.
- Designing and organizing a multi-class application.
- Encapsulation and separation of responsibilities.
- Java Collections Framework.
- Working with enums and custom exceptions.
- Managing object relationships.
- Using the Java Date and Time API.
- Implementing booking and cancellation workflows.
- Maintaining journey-wise seat inventory.
- Input validation and exception handling.
- Implementing rollback logic for failed group bookings.
- Writing unit and integration test cases.
- Building a consolidated regression test runner.

---

## 11. Future Enhancements

The following features can be considered for future versions:

- Oracle Database integration using JDBC.
- Persistent passenger, train, and booking records.
- Spring Boot REST API development.
- Web-based frontend using React.
- Payment gateway integration.
- Email and SMS ticket notifications.
- Concurrent booking management.
- Improved authentication and password security.

---

## Author

**Vamsi Vivek**

**Project:** Railway Reservation System  
**Language:** Java  
**Category:** Core Java | Console-Based Application

**Developed as a Core Java portfolio project.**

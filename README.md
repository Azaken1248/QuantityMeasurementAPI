# Quantity Measurement API
This documentation serves as a comprehensive `README` for the Quantity Measurement API and a detailed, step-by-step guide to understanding Spring Boot architecture. The project is structured into distinct layers: Application Entry, Data Model (Entity), Data Access (Repository), Business Logic (Service), API (Controller), Exception Handling, and Testing.

Each section below corresponds to a specific file in the repository, explaining the annotations used, providing examples of their implementation, and detailing the internal logic of the classes and methods.

-----

## 1\. Application Entry Point

**File:** `QuantityMeasurementApplication.java`

This file is the foundation of the Spring Boot application. It initializes the Spring application context, triggers component scanning, auto-configures necessary beans, and starts the embedded web server (Tomcat).

### Annotations Used

| Annotation | Purpose & Example Usage |
| :--- | :--- |
| `@SpringBootApplication` | Combines `@Configuration` (marks class as a bean source), `@EnableAutoConfiguration` (tells Spring to guess configuration based on classpath), and `@ComponentScan` (looks for other Spring components). <br><br>**Example:** `@SpringBootApplication` placed directly above the class declaration. |
| `@OpenAPIDefinition` | Used for Swagger/OpenAPI documentation to provide global metadata for the API, such as title and version. <br><br>**Example:** `@OpenAPIDefinition(info = @Info(title = "Quantity Measurement API", version = "1.0.0"))` |
| `@Info` | Nested within `@OpenAPIDefinition`, it structures the specific API details. |

### Class and Method Details

  * **Class `QuantityMeasurementApplication`**: Serves as the bootstrapping class. It does not contain business logic; its sole purpose is to configure and launch the framework.
  * **Method `main(String[] args)`**: The standard Java entry point. It calls `SpringApplication.run(QuantityMeasurementApplication.class, args);`. Under the hood, this sets up the default configuration, starts the application context, performs the classpath scan, and launches the embedded HTTP server on the default port (usually 8080).

-----

## 2\. Data Model (Entity Layer)

**File:** `QuantityMeasurementEntity.java`

This class defines the database schema. Spring Data JPA (Java Persistence API) uses this class to automatically generate and manage the underlying database table. It represents a single record of a quantity measurement operation.

### Annotations Used

| Annotation | Purpose & Example Usage |
| :--- | :--- |
| `@Entity` | Marks the class as a JPA entity, instructing Hibernate to map it to a table. |
| `@Table` | Specifies the table name and defines database indexes to optimize query performance. <br><br>**Example:** `@Table(name = "quantity_measurement_entity", indexes = {@Index(name = "idx_operation", columnList = "operation")})` |
| `@Data` | A Lombok annotation that automatically generates getters, setters, `toString`, `equals`, and `hashCode` methods at compile time. |
| `@NoArgsConstructor` | Lombok annotation that generates an empty constructor, which JPA requires to instantiate entities via reflection. |
| `@AllArgsConstructor` | Lombok annotation that generates a constructor accepting all fields. |
| `@Id` & `@GeneratedValue` | Defines the primary key and its generation strategy. <br><br>**Example:** `@Id @GeneratedValue(strategy = GenerationType.IDENTITY)` creates an auto-incrementing primary key. |
| `@Column` | Maps a class field to a specific database column and defines properties like nullability. <br><br>**Example:** `@Column(name = "this_value", nullable = false)` |
| `@PrePersist` | A JPA lifecycle callback executed right before the entity is saved to the database for the first time. |
| `@PreUpdate` | A JPA lifecycle callback executed right before an existing entity is updated. |

### Class and Method Details

  * **Class `QuantityMeasurementEntity`**: Contains fields for both operands (`thisValue`, `thisUnit`, `thatValue`, `thatUnit`), the operation performed (`COMPARE`, `ADD`, etc.), the result (`resultValue`, `resultUnit`, `resultString`), and error tracking (`isError`, `errorMessage`).
  * **Method `onCreate()`**: Annotated with `@PrePersist`, this method automatically sets the `createdAt` and `updatedAt` properties to `LocalDateTime.now()` exactly when the record is initially saved.
  * **Method `onUpdate()`**: Annotated with `@PreUpdate`, this method refreshes the `updatedAt` property whenever the record is modified.
  * **Constructors**:
      * **Single Operand Constructor**: Used for `COMPARE` and `CONVERT` operations where an operation is performed and a string or double result is produced.
      * **Double Operand Constructor**: Used for arithmetic like `ADD` and `SUBTRACT` where two `QuantityDTO` objects yield a resulting `QuantityDTO`.
      * **Error Constructor**: Instantiates the entity specifically to log failed operations (e.g., passing `isError = true` and an `errorMessage`).

-----

## 3\. Data Access (Repository Layer)

**File:** `QuantityMeasurementRepository.java`

This interface handles all database interactions. By extending `JpaRepository`, Spring provides standard CRUD operations automatically, completely eliminating the need to write standard SQL queries or JDBC boilerplate.

### Annotations Used

| Annotation | Purpose & Example Usage |
| :--- | :--- |
| `@Repository` | Marks the interface as a Spring Data Repository. This allows Spring to detect it during classpath scanning and translates database-specific exceptions into Spring's unified `DataAccessException` hierarchy. |
| `@Query` | Allows defining custom JPQL (Java Persistence Query Language) queries for complex operations. <br><br>**Example:** `@Query("SELECT e FROM QuantityMeasurementEntity e WHERE e.operation = :operation AND e.isError = false")` |
| `@Param` | Binds method parameters to named parameters inside the `@Query` string. <br><br>**Example:** `(@Param("operation") String operation)` |

### Class and Method Details

  * **Interface `QuantityMeasurementRepository`**: Extends `JpaRepository<QuantityMeasurementEntity, Long>`. At runtime, Spring automatically generates a proxy class that implements this interface.
  * **Derived Query Methods**: Methods like `findByOperation(String operation)` and `findByIsErrorTrue()`. Spring parses the method name and automatically writes the exact SQL query needed. For instance, `findByThisMeasurementType` translates to `SELECT * FROM table WHERE this_measurement_type = ?`.
  * **Method `findSuccessfulOperations`**: Uses the `@Query` annotation to perform a more specific fetch, ensuring that only records where `isError = false` are retrieved for a given operation type.
  * **Method `countByOperationAndIsErrorFalse`**: Another derived query that returns a `long` representing the total number of successful operations of a specific type.

-----

## 4\. Business Logic (Service Layer)

**File:** `QuantityMeasurementServiceImpl.java`

The service layer contains the application's core business logic. It performs validations, conversions, mathematical calculations, and coordinates saving the results to the database via the repository.

### Annotations Used

| Annotation | Purpose & Example Usage |
| :--- | :--- |
| `@Service` | Marks the class as a Spring Service component. Spring creates a single instance (singleton bean) of this class at startup. |
| `@Autowired` | Instructs Spring to automatically inject dependencies. <br><br>**Example:** `@Autowired private QuantityMeasurementRepository repository;` injects the database repository into the service so it can save data. |

### Class and Method Details

  * **Class `QuantityMeasurementServiceImpl`**: Implements the `IQuantityMeasurementService` interface. It isolates the mathematical logic from the web requests.
  * **Method `compare()` and `convert()`**: These methods take input DTOs, validate that their measurement types match using `validateMeasurementTypes()`, resolve the specific units (e.g., Inches, Litres), perform the math, construct a `QuantityMeasurementEntity`, save it to the database, and return a result DTO.
  * **Method `divide(QuantityDTO q1, QuantityDTO q2)`**: Highlights robust error handling. It calculates the base value of the denominator (`q2`). If it is zero, it invokes `saveAndReturnError()` to log the failure in the database, and then explicitly throws an `ArithmeticException` to trigger a 500 error response.
  * **Method `executeArithmetic(...)`**: A centralized helper method for `ADD`, `SUBTRACT`, `MULTIPLY`, and `DIVIDE`. It converts both operands to a common base factor (e.g., converting all lengths to inches), performs the mathematical switch case, and then converts the result back into the requested target unit.
  * **Method `getBaseConversionFactor()`**: Houses the core conversion logic. It evaluates the `IMeasurableUnit` and returns its multiplier against a base unit (e.g., returning `12.0` for `FEET` where the base is `INCHES`, or `3.78541` for `GALLON` where the base is `LITRE`).
  * **History Methods**: Methods like `getOperationHistory()` and `getErrorHistory()` simply call the respective repository methods and convert the returned `QuantityMeasurementEntity` lists into `QuantityMeasurementDTO` lists to send back to the user safely.

-----

## 5\. API Endpoints (Controller Layer)

**File:** `QuantityMeasurementController.java`

The controller acts as the API gateway. It receives incoming HTTP requests, extracts the JSON payloads, delegates the processing to the Service layer, and formats the outgoing HTTP response.

### Annotations Used

| Annotation | Purpose & Example Usage |
| :--- | :--- |
| `@RestController` | Combines `@Controller` and `@ResponseBody`. It tells Spring that this class handles web requests and that all returned objects should be serialized directly into JSON. |
| `@RequestMapping` | Defines the base URL path for the entire controller. <br><br>**Example:** `@RequestMapping("/api/v1/quantities")` |
| `@Tag` | Swagger annotation to group and describe the endpoints in the generated UI. |
| `@PostMapping` & `@GetMapping` | Maps specific HTTP methods and sub-paths to functions. <br><br>**Example:** `@PostMapping("/compare")` |
| `@Operation`, `@Content`, `@ExampleObject` | Swagger annotations used to generate rich API documentation, providing interactive examples of expected JSON payloads. |
| `@Valid` | Instructs Spring to trigger validation constraints defined inside the incoming DTO before executing the method body. |
| `@RequestBody` | Extracts the JSON body of the HTTP request and deserializes it into a Java object. <br><br>**Example:** `(@Valid @RequestBody QuantityInputDTO quantityInputDTO)` |
| `@PathVariable` | Extracts dynamic values from the URL path. <br><br>**Example:** `(@PathVariable String operation)` extracts "ADD" from `/history/operation/ADD`. |
| `@ExceptionHandler` | Catches exceptions thrown specifically within this controller and allows formatting a custom response. |
| `@ResponseStatus` | Hardcodes the HTTP status code returned by an exception handler. <br><br>**Example:** `@ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)` |

### Class and Method Details

  * **Class `QuantityMeasurementController`**: Contains static string definitions of JSON examples (`EX_FEET_INCH`, `EX_TEMP`) used purely to populate the Swagger UI, making the API easy for developers to test.
  * **Method `performComparison()`**: Maps to `/compare`. It receives a `QuantityInputDTO`, passes the nested `thisQuantity` and `thatQuantity` to `service.compare()`, and wraps the result in `ResponseEntity.ok()`, which translates to an HTTP 200 OK status.
  * **Method `performAddition()` vs `performAdditionWithTargetUnit()`**: Demonstrates endpoint overloading. `/add` checks if a target unit was provided in the JSON; if not, it returns the result in the unit of the first operand. `/add-with-target-unit` explicitly requires and utilizes the target unit formatting.
  * **Method `getOperationHistory(String operation)`**: A simple GET endpoint that passes the URL path variable directly to the service layer to fetch an array of past operations, returning a JSON list.
  * **Method `handleValidationExceptions()`**: A local exception handler. If the service layer throws an `IllegalArgumentException` (like passing a bad unit string), this method catches it, builds a `QuantityMeasurementDTO` flagged as an error, and returns it with a 400 Bad Request status.

-----

## 6\. Global Exception Handling

**File:** `GlobalExceptionHandler.java`

To prevent repetitive try-catch blocks in the controller layer, Spring Boot utilizes a global exception handler. This ensures that no matter where an error occurs, the client receives a predictable, uniformly formatted JSON error response.

### Annotations Used

| Annotation | Purpose & Example Usage |
| :--- | :--- |
| `@ControllerAdvice` | A global interceptor that listens for exceptions thrown by any `@RestController` in the entire application. |
| `@ExceptionHandler` | Specifies the exact exception class a method should intercept. <br><br>**Example:** `@ExceptionHandler(MethodArgumentNotValidException.class)` |

### Class and Method Details

  * **Class `GlobalExceptionHandler`**: Contains an internal POJO `ErrorResponse` detailing the `timestamp`, `status`, `error`, `message`, and `path`.
  * **Method `handleMethodArgumentNotValidException()`**: Intercepts errors thrown when a `@Valid` annotation fails (e.g., missing a required JSON field). It iterates through the `BindingResult` to collect all error messages, joins them into a single string, extracts the request URI, and returns a 400 Bad Request `ErrorResponse`.
  * **Method `handleQuantityException()`**: Intercepts custom `QuantityMeasurementException` errors thrown by business logic validation, mapping them directly to a 400 Bad Request with the custom error message.
  * **Method `handleGlobalException()`**: A final safety net that catches the base `Exception.class`. If a null pointer or unexpected database failure occurs, this method traps it, logs it as a `severe` error, and safely returns a 500 Internal Server Error without leaking sensitive stack traces to the client.

-----

## 7\. Testing Layer

**File:** `QuantityMeasurementControllerTest.java`

This file contains automated unit tests specifically for the Controller layer. It ensures the web endpoints map correctly, accept the right data, and return the correct HTTP statuses, completely isolated from the database and service logic.

### Annotations Used

| Annotation | Purpose & Example Usage |
| :--- | :--- |
| `@WebMvcTest` | Instructs Spring to only load the web layer (Controllers, Exception Handlers) into the test context. It is configured to exclude JPA auto-configuration (`excludeAutoConfiguration = {...}`) to prevent the application from trying to connect to a database during web testing. |
| `@AutoConfigureMockMvc` | Wires up the `MockMvc` bean, allowing the test to simulate incoming HTTP requests without needing to start a real Tomcat server. |
| `@MockBean` | Replaces a real Spring bean with a Mockito mock. <br><br>**Example:** `@MockBean private IQuantityMeasurementService service;` ensures the controller talks to a fake service during the test. |
| `@BeforeEach` | A JUnit 5 annotation indicating a method that should run before every single `@Test` method to reset state. |
| `@Test` | Marks a method as an executable JUnit test case. |

### Class and Method Details

  * **Class `QuantityMeasurementControllerTest`**: Uses Mockito to stub service responses and `MockMvc` to trigger endpoints.
  * **Method `setUp()`**: Runs before each test to instantiate dummy data objects (`quantity1` for input, `measurementResult` for output), ensuring a clean state.
  * **Method `testCompareQuantities_Success()`**:
    1.  **Mocking**: Configures the fake service using `Mockito.when(service.compare(...)).thenReturn(measurementResult);`.
    2.  **Execution**: Uses `mockMvc.perform(post("/api/v1/quantities/compare")...)` to send a simulated JSON POST request.
    3.  **Assertion**: Chains `.andExpect(status().isOk())` to verify a 200 status, and `.andExpect(jsonPath("$.resultString").value("true"))` to verify the JSON payload was serialized correctly.
  * **Method `testGetOperationHistory_Success()`**: Simulates a GET request, mocks the service to return an empty list, and uses `jsonPath("$.length()").value(0)` to assert that the returned JSON array is empty.

-----

## API Endpoint Documentation

| HTTP Method | Endpoint Path | Description |
| :--- | :--- | :--- |
| **POST** | `/api/v1/quantities/compare` | Evaluates if two quantities are equal (e.g., 1 Foot == 12 Inches). Returns a boolean result string. |
| **POST** | `/api/v1/quantities/convert` | Converts the base quantity into the exact target quantity unit. |
| **POST** | `/api/v1/quantities/add` | Adds two quantities. Can optionally return the result in a specified target unit if provided in the JSON payload. |
| **POST** | `/api/v1/quantities/add-with-target-unit` | Explicit endpoint for adding quantities where the user strictly dictates the output format unit. |
| **POST** | `/api/v1/quantities/subtract` | Subtracts the second quantity from the first, handling unit conversions automatically. |
| **POST** | `/api/v1/quantities/subtract-with-target-unit`| Explicit endpoint for subtracting quantities with a strictly defined target unit. |
| **POST** | `/api/v1/quantities/divide` | Divides the first quantity by the second quantity. Includes internal safety checks to prevent and log division by zero errors. |
| **GET** | `/api/v1/quantities/history/operation/{operation}`| Retrieves a comprehensive list of past database records filtered by the exact operation type (e.g., ADD, COMPARE). |
| **GET** | `/api/v1/quantities/history/type/{type}` | Retrieves database history filtered by measurement category (e.g., LengthUnit, TemperatureUnit). |
| **GET** | `/api/v1/quantities/count/{operation}` | Returns a single numerical integer representing the total count of *successful* executions for a requested operation. |
| **GET** | `/api/v1/quantities/history/errored` | Retrieves a specialized log of all operations that failed and generated an internal error flag. |

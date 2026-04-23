# Smart Campus Sensor & Room Management API
## Project Overview
This project is a RESTful Web Service built using Java JAX-RS (Jersey). It is designed to manage a "Smart Campus" ecosystem, allowing administrators to track rooms and their associated IoT sensors (Temperature, CO2, etc.).

### Key Architectural Choices
1. **Singleton DAO Pattern:** Since the system uses in-memory storage, I implemented the Data Access Object (DAO) pattern with Singletons to ensure data persistence across different HTTP requests.

2. **HATEOAS Discovery:** The API features a root discovery endpoint (/api/v1) to provide metadata and resource links, making the API self-documenting.

3. **Data Integrity:** The system enforces business rules, such as preventing the deletion of rooms that still contain active sensors and ensuring unique IDs for all resources.

### How to Build and Run
**Prerequisites:** ***JDK 8+, Maven 3.6+, Apache Tomcat 9.x.***
1. **Clone the Repository**
   ```
   https://github.com/sherpeus/SmartCampus.git
   ```
2. **Build:** Run ```mvn clean install``` or right-click in NetBeans and select Clean and Build.
3. **Deploy:** Deploy the generated ```.war``` file to your Tomcat server.
4. **Access:**
   
   Base URL ```http://localhost:8080/SmartCampus/api/v1```

   ***API Endpoints***
   | | Method | Endpoint | Usage |
   | - | - | - | - |
   |**Discovery** | GET | ```/api/v1```| Retrieves the Meadata|
   |**Rooms** | GET | ```/rooms```| Retrieves the stored rooms|
   ||POST|```/rooms```| Creates a new room|
   ||DELETE|```/rooms/{id}```| Deletes the room based on the id|
   |**Sensors**| GET | ```/sensors```| Retrieves all the sensors |
   || POST | ```/sensors``` | Adds a new sensor |
   |**Sensor Readings** | GET | ```/sensors/{id}/readings``` | Retrieves the logged readings for the given sensor|
   || POST | ```/sensors/{id}/readings``` | Loggs a new reading for the given sensor|
   
   
### Sample curl Commands
1. **Create a New Room.**
   
   ```python
   curl -X POST http://localhost:8080/SmartCampus/api/v1/rooms -H "Content-Type: application/json" -H "Accept:application/json" -d "{\"id\":\"ROOM-001\",\"name\":\"LIBRARY\",\"capacity\":100}"
   ```

3. **Retrieve the Stored Rooms.**

   ```python
   curl -X GET http://localhost:8080/SmartCampus/api/v1/rooms -H "Accept: application/json"
   ```
5. **Retrieve a specific room via its ID.**

   ```pyrhon
   curl -X GET http://localhost:8080/SmartCampus/api/v1/rooms/ROOM-001 -H "Accept: application/json"
   ```
7. **Registering a new Sensor.**

   ```python
   curl -X POST http://localhost:8080/SmartCampus/api/v1/sensors -H "Content-Type: application/json" -d "{\"id\":\"TEMP-001\",\"type\":\"Temperature\",\"status\":\"ACTIVE\",\"currentValue\":111,\"roomId\":\"ROOM-001\"}"
   ```

9. **Filter Sensors Using Query Parameters**

   ```python
   curl -X GET "http://localhost:8080/SmartCampus/api/v1/sensors?type=Temperature" -H "Accept: application/json"
   ```
### Report Answers
Q1)

##### Explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures (maps/lists) to prevent data loss or race conditions.

By default, the JAX-RS runtime adopts a per-request lifecycle for resource classes. This means that for every incoming HTTP request, the container instantiates a new object of the resource class. Once the response is generated and sent back to the client, this instance is discarded. This architectural decision was  made in order to keep web services stateless. By creating a fresh instance for every request, JAX-RS ensures that no residual state from a previous user leak into a subsequent request. 
Because the resource instance is short-lived, any data stored in instance variables will be lost immediately after the request cycle ends. To maintain a permanent collection of data in an in-memory system, the focus needs to be shifted towards a static context.
However, shifting data to a static context introduces significant complexity regarding concurrency.
- Modern servlet containers e.g.: Tomcat use a thread-pool approach. While each request gets its own Resource Object, multiple Threads are executing simultaneously, all trying to access the same static Map or List.
- If two clients attempt to POST a new sensor at the exact same time, a standard HashMap could experience a race condition. This might lead to data corruption, where one sensor overwrites another.
In order to mitigate this, the following measures could be taken.
- Using atomic methods like putIfAbsent() to prevent uninterrupted operations.

- Using classes from the java.util.concurrent package, which allows concurrent reading and bucket-level locking for writes, significantly improving performance under high load.


Q2)

##### Why is the provision of ”Hypermedia” (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?

HATEOAS stands for Hypermedia as the Engine of Application State and is a constraint of the REST architectural style. The reason for using HATEOAS is mainly due to its capability of loose coupling, which means that the clients do not have to hardcode the URLs and can discover them at runtime. Additionally, this allows the server to change its internal URI structure, versioning, or hosting logic without breaking the client. On the other hand, static documentation does not allow this and needs the developers to hard code the URLS in the client side. This becomes a problem when a given endpoint gets restructured as it will break the client application. Moreover, the HATEOAS approach is state-aware, meaning that it knows what actions are allowed based on the current state. This allows for the delegation of business logic; instead of the client needing to know complex rules about when a "delete" or "update" is valid, the server simply includes or omits the relevant links. This transforms the API from a static data provider into a dynamic state machine where the client developer acts more like a "user agent," navigating the application based on live opportunities provided by the server rather than following a rigid, potentially outdated manual.

Q3)
##### When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client-side processing.

Returning only IDs creates a highly lightweight initial payload, which significantly reduces network bandwidth consumption. This is particularly beneficial for mobile clients or low-latency environments where minimizing  retrieval time is critical. However, this approach forces the client to make a separate API call for every single room to get its details. This increases the total overhead due to repeated HTTP headers and handshake latency, which can ultimately degrade the user experience if the client needs to display a full dashboard.

In contrast, returning full room objects increases the payload overhead per request but provides much higher efficiency for client-side processing. By delivering all metadata in a single response, the client can render the entire UI immediately without further network interaction. While this consumes more bandwidth upfront, it reduces the total number of calls to the server. For large collections, this can lead to over-fetching, where the client downloads data it never actually displays. Therefore, the choice depends on the expected size of the collection and the actions the client is expected to perform. 

Q4)
##### Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.

The DELETE operation is idempotent in the implementation because multiple identical requests result in the same final state of the server. When the first DELETE request is received, the server locates the room, verifies there are no active sensors, and removes it from the underlying data structure. At this point, the resource is gone, and the system state has changed.
If a client mistakenly sends the exact same request a second or third time, the server will attempt to find the room and fail, as the resource no longer exists. It will then return an HTTP 404 Not Found. Despite the difference in the HTTP status code (204 vs. 404), the server state remains identical: the room is still non-existent, and no data corruption has occurred. Since the effects of one request are the same as the effects of multiple requests, the operation fulfills the definition of idempotency.

Q5)
##### We explicitly use the @Consumes (MediaType.APPLICATION_JSON) annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?

By applying the @Consumes(MediaType.APPLICATION_JSON) annotation, a strict contract is defined for the incoming request's Content-Type header. If a client attempts to send data in a different format, e.g. : text/plain or application/xml, the JAX-RS runtime performs a validation check for the request.
The technical consequence of a mismatch is that the server will automatically reject the request and return an HTTP 415 Unsupported Media Type status code to the client. This ensures that the underlying request parser is not invoked with incompatible data, which would otherwise lead to parsing errors. This behavior protects the integrity of the service by ensuring the API only attempts to process data formats it is configured to handle.

Q6)
##### You implemented this filtering using @QueryParam. Contrast this with an alternative design where the type is part of the URL path (e.g., /api/vl/sensors/type/CO2). Why is the query parameter approach generally considered superior for filtering and searching collections?

The use of @QueryParam is fundamentally different from Path Parameters in terms of resource modeling and API flexibility. Path parameters are used to identify a specific resource or a sub-resource within a hierarchy. e.g. :  /sensors/123 points to a unique entity. In contrast, query parameters are intended to provide instructions to the server on how to process that resource, like filtering. For instance, using a path like /sensors/type/CO2 implies that the "type" is a fixed structural category, which becomes unmanageable as the API evolves. For instance, if a client wants to filter by a new type, which is not registered yet, problems will arise. The query parameter approach handles this naturally and in any order. Furthermore, query parameters are inherently optional. If no parameters are provided, the API naturally defaults to returning the entire collection unlike the path parameters, which are mandatory parts of the URL structure, making them less suited for optional search criteria.

Q7)
##### Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class?

The Sub-Resource Locator pattern is an architectural tool in JAX-RS designed to prevent the emergence of saturated controllers that attempt to manage every operation for a parent. By using a locator method to return a separate resource class (e.g., SensorReadingResource), the API achieves modularity.
Defining every nested path like sensors/{id}/readings/{id} in a single class forces that class to manage multiple domain responsibilities, leading to a file that is difficult to navigate, test, and maintain. In contrast, the locator pattern enables child classes to be domain specific without them ever needing to perform tasks that are out of their realm. E.g. : SensorResource is only responsible for the high-level sensor lifecycle, while SensorReadingResource is exclusively focused on historical data points. This makes every class cohesive and concise. Furthermore, this pattern improves code reusability and state management. In large APIs, this reduces redundant code and allows development teams to work on distinct nested resources (e.g., one team on sensors, another on the reading logs)

Q8)
##### Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?

A 404 Not Found status code is semantically intended to communicate that the URI itself does not exist, essentially telling the client that the server could not find a resource at the specific address requested.
In the case of a POST request to register a sensor, the URI (e.g., /api/v1/sensors) is logically valid, and the server is ready to accept data. The failure occurs because a reference within the payload ( roomId ) points to a non-existent entity. Using a 404 here can be highly confusing for a client developer, as it implies the endpoint they are posting to is missing, rather than the data they sent being logically inconsistent.
HTTP 422 is superior because it adds semantic value to the response. It conveys that the request itself is correct, but it has some logical issues  and makes debugging way easier for client developers. 

Q9)
##### From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?

Exposing internal Java stack traces to external API consumers pose numerous threats to the system for they disclose valuable information to external parties. This removes the layer of obscurity from the system and makes it vulnerable to targeted attacks on its weak spots.

Information that an attacker could extract from a stack trace,

•	Technology Stack & Versioning: Stack traces name the libraries and frameworks in use (e.g., org.hibernate, com.fasterxml.jackson, org.glassfish.jersey). By identifying specific version numbers often included in these paths, an attacker can cross-reference them against known Common Vulnerabilities and Exposures databases and exploit their weaknesses.

•	Internal Code Structure & Logic: The trace reveals the exact sequence of method calls, class names, and package structures. This allows an attacker to map out the application's business logic and identify potential weak links in the code.

•	Server Environment Details: Traces frequently leak absolute file paths (e.g., /home/java/deploy/api/v1/...). This informs an attacker about the operating system, the directory structure, and the presence of specific environment variables or configuration files.

By implementing a "Global Safety Net" via an ExceptionMapper<Throwable>, the API prevents this leakage, replacing detailed technical failures with a generic, safe HTTP 500 error, thereby denying attackers the intelligence needed to escalate a minor error into a full-scale system compromise.

Q10)
##### Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?

When logging is manually inserted into every resource method, the codebase becomes saturated with boilerplate code that is unrelated to the actual business logic. This makes the methods harder to read and maintain. By centralizing this logic in a filter, the developer writes the code once, and it is automatically applied to every endpoint in the system. This ensures global consistency. For instance, you can guarantee that every single request's HTTP method, URI, and final status code are recorded, whereas manual logging is prone to errors. Furthermore, filters provide better observability for the entire request-response lifecycle. A ContainerRequestFilter can log an incoming request before it even reaches the resource method, and a ContainerResponseFilter can capture the final status code even if the request was terminated early by an exception or a security check. If logging were handled manually inside the resource methods, any failure that occurred during the JAX-RS provider's processing (like a JSON parsing error) might never be logged at all, as the execution would never reach the method body. Utilizing filters ensures the API error proof in terms of its monitoring.


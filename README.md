# Smart Campus Sensor & Room Management API
## Project Overview
This project is a RESTful Web Service built using Java JAX-RS (Jersey). It is designed to manage a "Smart Campus" ecosystem, allowing administrators to track rooms and their associated IoT sensors (Temperature, CO2, etc.).

### Key Architectural Choices
1. Singleton DAO Pattern: Since the system uses in-memory storage, I implemented the Data Access Object (DAO) pattern with Singletons to ensure data persistence across different HTTP requests.

2. HATEOAS Discovery: The API features a root discovery endpoint (/api/v1) to provide metadata and resource links, making the API self-documenting.

3. Data Integrity: The system enforces business rules, such as preventing the deletion of rooms that still contain active sensors and ensuring unique IDs for all resources.

# Load Balancer

---

## Project Overview

This project is a deep dive into the brain of a distributed system: the **load balancer**. It showcases a powerful service architecture where a central hub intelligently manages traffic to prevent system overload and ensure peak performance. By exploring this project, you will see how service discovery and a smart **least connections** algorithm work together to create a resilient and highly available system. This is where requests meet their perfect destination.

---

## Architecture

The system is composed of five distinct applications that work together seamlessly to demonstrate the principles of service discovery and load balancing.

* **Load Balancer (Port 9000):** The entry point for all requests. It dynamically retrieves the list of available services from the registry and forwards requests based on the **Least Connections** strategy.
* **Service Registry (Port 9004):** A central registry where all backend services automatically register themselves, acting as the single source of truth for the load balancer.
* **Backend Services (3 instances):** Simple RESTful services running on ports 9001, 9002, and 9003. They handle incoming requests and represent the core business logic.
* **Test Client:** A simple application that generates **100 concurrent requests** to fully test the load balancer's performance under load.

---

## Load Balancing Algorithm

This project's load balancer uses a **Least Connections** algorithm. Unlike a simple round-robin approach that sends requests sequentially, this algorithm is intelligent and adaptive.

It works by tracking the number of active connections to each backend service. When a new request arrives, the load balancer checks each service's connection count and forwards the request to the one with the **fewest active connections**. This ensures that the workload is always balanced, preventing any single service from becoming a bottleneck and maximizing overall system throughput.

---

## Getting Started

To run this project, you need to set up a multi-module Maven project in your preferred IDE (e.g., IntelliJ IDEA).

### Prerequisites

* Java Development Kit (JDK) 17 or higher
* An IDE with Maven support (e.g., IntelliJ IDEA)

### How to Run

1.  **Clone the Repository:**
    ```bash
    git clone https://github.com/awkward-student/load-balancer.git
    cd load-balancer
    ```

2.  **Run the Services (in this specific order):**

    * **Service Registry:** Run `main` in `ServiceRegistry.java`. Wait for it to confirm it's running on port 9004.
    * **Backend Services:** Run `main` in `BackendService1.java`, `BackendService2.java`, and `BackendService3.java` in separate terminals.
    * **Load Balancer:** Run `main` in `LoadBalancer.java`. It will start on port 9000 and establish a connection with the registry.
    * **Test Client:** Run `main` in `TestClient.java`. Observe the output in all consoles as the requests are distributed by the **Least Connections** algorithm.

---

## Author

* **awkward-student** - [GitHub Profile](https://github.com/awkward-student)
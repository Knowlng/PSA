# PSA Project - Setup Instructions

## 0) Prerequisites

Ensure you have the following installed:
- Java 21.0.2 or higher
- Node.js 23.8.0 or higher

---

## 1) Clone the Repository

```bash
git clone git@github.com:Knowlng/PSA.git
cd PSA
```

---

## 2) Set Environment Variables

Set the necessary environment variables to establish database connectivity.

---

## 3) Launch Frontend

From the root folder:

```bash
cd frontend
npm install
npm run dev
```

If it works, you should see output like:

```
Local: http://localhost:XXXX
```

---

## 4) Launch Backend

From the root folder, run:

```bash
cd backend
./gradlew build
./gradlew bootRun
```

If it works, you should see the following message in the terminal:

```
Spring Boot application started successfully!
```

## Note

The database is hosted on faculties cloud environment, that means that admins might turn off the VM I'm using to host it to save resources. 
If by any chance it might be down (./gradlew bootRun fails) when you are trying to access it, send me a message and I will turn it on again.

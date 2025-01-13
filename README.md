# Personal Finance Tracker - Microservices Architecture
The **Personal Finance Tracker** project is a full-stack microservices application that allows users to track their financial transactions, generate reports, and predict future spending trends using AI/ML models. It is built using **Java**, **Spring Boot**, **Hibernate**, **MySQL**, **Docker**, and **Feign Client**, with JWT-based authentication for security.
---

## 📂 Project Structure

The project is structured into two main microservices:

1. **Transaction Service (Port: 8080)**  
   Manages user transactions (income, expenses) and provides REST APIs to interact with the transaction data.

2. **Reporting Service (Port: 8081)**  
   Generates PDF and Excel reports of financial transactions and integrates with an ML service to predict future spending.
---

## ⚙️ Technologies Used

- Java
- Spring Boot
- Hibernate
- MySQL
- Docker
- JWT (JSON Web Token) Authentication
- Feign Client
- Apache PDFBox (for generating PDF reports)
- Apache POI (for generating Excel reports)
- JFreeChart (for chart visualization)
- OpenFeign (for inter-service communication)
- Docker Compose
---

 🏗️ Project Setup
 1. *Clone the Repository*
"Bash"
git clone https://github.com/YourGithubUsername/PersonalFinanceTracker.git
cd PersonalFinanceTracker

2. Build the Project with Maven
mvn clean install

3. Run the Docker Containers
docker-compose up

4. Access the Services
Service	URL
Transaction API	http://localhost:8080/api/transactions
Reporting API	http://localhost:8081/api/reports/pdf-report

🔐 Authentication
This project uses JWT-based authentication for secure API access.

Login API Endpoint:
POST http://localhost:8080/api/auth/login

Example JWT Token:
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

📄 API Endpoints

Transaction Service:
Method	Endpoint	Description
GET	/api/transactions	Retrieve all transactions
POST	/api/transactions	Add a new transaction

Reporting Service:
Method	Endpoint	Description
GET	/api/reports/pdf-report	Generate a PDF report
GET	/api/reports/excel-report	Generate an Excel report

🐳 Docker Configuration
The project includes a docker-compose.yml file to set up and run the microservices with MySQL and Docker containers.

Docker Services:
	•	transaction-service (Port: 8080)
	•	reporting-service (Port: 8081)
  •	ml-service (Port: 5000)
	•	MySQL Database (Port: 3306)

📘 License

This project is licensed under the MIT License. See the LICENSE file for details.

📧 Contact

For any queries, feel free to contact Dharmik Chhatbar at dpchhatbar@gmail.com.

💻 Contributor

Dharmik Chhatbar
B.Sc. in Computer Science | Full-Stack Developer

---

### ✅ **Git Commands to Commit and Push:**

Run the following commands to commit your changes and push to your GitHub repository:

"Bash"
# Step 1: Add all modified files
git add .

# Step 2: Commit the changes with a message
git commit -m "Updated JWT authentication, Docker configuration, and Reporting Service with PDF/Excel generation"

# Step 3: Push the changes to the GitHub repository
git push origin main

# Clothify Store - POS & Inventory Management System 👗👕

Welcome to **Clothify Store**, a specialized Point of Sale (POS) and Inventory Management system designed for clothing retail businesses. This system helps manage sales, track inventory, and handle customer transactions efficiently.

---

## ✨ Features

* **Inventory Tracking:** Manage stock levels, clothing categories (Shirts, Pants, etc.), and sizes.
* **Sales Management:** Seamlessly process customer orders and generate invoices.
* **Customer Loyalty:** Maintain a database of customers for better service and tracking.
* **Supplier Management:** Track incoming stock and supplier details.
* **Reports & Analytics:** View daily sales reports and stock movement.

---

## 🛠️ Tech Stack

* **Language:** Java (JDK 17+)
* **GUI Framework:** JavaFX / Swing
* **Architecture:** Layered Architecture (Controller, Service, Repository, DTO/Entity)
* **Build Tool:** Maven
* **Database:** MySQL
* **Reporting:** JasperReports (Optional - if you're using it)

---

## 📂 Project Structure

```text
Pos/
├── src/main/java/          # Source code following Layered Architecture
│   ├── controller/         # Handle UI logic
│   ├── dto/                # Data Transfer Objects
│   ├── entity/             # Database Entities
│   ├── repository/         # Data Access logic
│   └── service/            # Business Logic
├── src/main/resources/     # FXML files, CSS, and Images
├── pom.xml                 # Project dependencies
└── README.md

Simple Expense Tracker
This repository contains a straightforward expense tracking application built with Spring Boot (back end) and React (front end). The database used is MySQL.

Features
User Authentication (JWT)

Register and log in to obtain a JWT token.
Authenticate requests with Authorization: Bearer <token> headers.
Expense Management

Create new expenses.
Read all or specific expenses (supports optional pagination via query parameters).
Update existing expenses.
Delete expenses.
Tech Stack
Backend: Spring Boot (Java)
Database: MySQL
Frontend: React (TypeScript)
Usage
Log In / Register: Obtain a JWT token after successful login.
CRUD Operations: Use the token to call secured endpoints for creating, reading, updating, and deleting expenses.
API Endpoints 
Method	Endpoint	Description	Auth?
POST	/auth/register	Register a new user	No
POST	/auth/login	Log in (get JWT)	No
GET	/expenses	Get expenses (paged)	Yes (JWT)
GET	/expenses/{expenseId}	Get a single expense	Yes (JWT)
POST	/expenses	Create new expense	Yes (JWT)
PUT	/expenses/{expenseId}	Update an existing expense	Yes (JWT)
DELETE	/expenses/{expenseId}	Delete an expense	Yes (JWT)

# Real-Time Chat App - Backend

This is the **backend** of the Real-Time Chat Application built using **Spring Boot** and **MongoDB**.  
It handles user management, chat room management, and real-time messaging via WebSockets.

---

## **Tech Stack**
- **Backend Framework:** Spring Boot
- **Database:** MongoDB / MongoDB Atlas
- **WebSocket:** Spring WebSocket / STOMP
- **Build Tool:** Maven
- **Java Version:** 17+

---

## **Features**
- Create, join, and manage chat rooms
- Real-time messaging between users
- Store chat history in MongoDB
- REST APIs for user and room management
- Basic authentication (optional for now)

---

## **Folder Structure**
chat-app-backend/
├── src/
│ ├── main/
│ │ ├── java/com/example/chatapp/ # Java source code
│ │ │ ├── controller/ # REST and WebSocket controllers
│ │ │ ├── model/ # Data models (User, Room, Message)
│ │ │ ├── repository/ # MongoDB repositories
│ │ │ └── service/ # Service layer
│ │ └── resources/
│ │ ├── application.properties # Configuration files
│ │ └── static/ # Optional static files
├── pom.xml # Maven dependencies
└── README.md # Project documentation


---

## **Getting Started**
### **1. Clone the repository**
```bash
git clone https://github.com/<your-username>/real-time-chat-backend.git
cd real-time-chat-backend


Set up MongoDB
  Either install MongoDB locally or use MongoDB Atlas.
  Update application.properties with your MongoDB connection URL: spring.data.mongodb.uri=mongodb+srv://<username>:<password>@cluster0.mongodb.net/chatapp?retryWrites=true&w=majority

Build and run the application
  mvn clean install
  mvn spring-boot:run
  The backend server runs at http://localhost:8080 by default

API Endpoints (Examples)

POST /api/rooms → Create a new chat room
GET /api/rooms → Get list of chat rooms
POST /api/users → Register a new user
WebSocket endpoint: /ws → For real-time messaging

Contributing

Fork the repository
Create a feature branch: git checkout -b feature/my-feature
Commit your changes: git commit -m "Add some feature"
Push to the branch: git push origin feature/my-feature
Open a Pull Request

# 📡 Multi-User Chat Application (Java) – Setup & Usage Guide

## 📌 Project Overview

This is a **Client-Server Multi-user Chat Application** built using:

* Java (Sockets, Multithreading)
* JavaFX (UI)
* MySQL (Database)

### ✅ Features Implemented

* User Registration & Login
* Real-time Messaging (Broadcast + Private)
* File Transfer (Broadcast + Private)
* Active Users List
* Message & File Storage in Database
* Multithreaded Server & Client

---

## 📁 Project Structure
if it is on netBeans create FX project in the sorce package create a new package called chatapp and inside it create client, server, ui, database and util packages and then paste the file in each folder as shown below or if you want you can run in what ever structure you want by modifing the import inside each file according to your structure

```
chatapp/
│
├── client/
│   └── ChatClient.java        # Handles socket connection, sending/receiving messages & files
│
├── server/
│   ├── Server.java            # Main server, accepts clients, manages active users
│   └── ClientHandler.java     # Handles each client in a separate thread
│
├── ui/
│   ├── ChatView.java          # Main chat interface (messages, users, file sending)
│   ├── LoginView.java         # Login UI
│   ├── MainApp.java           # main application the UI dynamically displaying
│   └── RegisterView.java      # Registration UI
│
├── database/
│   ├── DBConnection.java      # MySQL connection setup
│   ├── UserDAO.java           # User login/register logic
│   ├── MessageDAO.java        # Save messages to DB
│   └── FileDAO.java           # Save file metadata to DB
│
├── util/
│   └── Config.java            # Server port configuration 
```

---

## ⚙️ Requirements

* Java JDK 8+
* MySQL Server
* JavaFX (if not bundled with JDK)
* IDE (NetBeans or any editor can run java with fx)

---

## 🗄️ Database Setup

### 1. Create Database

```sql
CREATE DATABASE chatapp_db;
USE chatapp_db;
```

---

### 2. Create Tables

#### 👤 Users Table

```sql
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    password VARCHAR(100)
);
```

#### 💬 Messages Table

```sql
CREATE TABLE messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sender_email VARCHAR(100),
    receiver_email VARCHAR(100),
    message TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### 📎 Files Table

```sql
CREATE TABLE files (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sender_email VARCHAR(100),
    receiver_email VARCHAR(100),
    file_name VARCHAR(255),
    file_size BIGINT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

### 3. Configure Database Connection

In `DBConnection.java`, update:

```java
String url = "jdbc:mysql://localhost:3306/chatapp_db";
String user = "root";
String password = "your_password";
```

---

## ▶️ How to Run the Project

### 1. Start the Server

Run:

```
Server.java
```

Expected Output:

```
Server started on port XXXX
Waiting for clients...
```

---

### 2. Start Clients

Run:

```
MainApp.java (The main UI inside ui package)
```

Open multiple instances then register and login to simulate multiple users.

---

## 💡 How the System Works

### 🔐 Authentication

* Users register via `RegisterView`
* Login verified using `UserDAO`
* Successful login connects to server

---

### 💬 Messaging

#### Message Format (Internal)

```
TO:receiver_email:message
```

#### Types:

* **Broadcast:** `TO:ALL:Hello`
* **Private:** `TO:user@gmail.com:Hello`

#### Flow:

1. Client sends message → Server
2. Server processes → saves to DB
3. Server sends to:

   * All users (broadcast)
   * Specific user + sender (private)

---

### 📎 File Transfer

#### File Format:

```
FILE:TO:receiver_email:filename
```

#### Flow:

1. Client sends:

   * Header
   * File size
   * File bytes
2. Server:

   * Saves metadata to DB
   * Sends to correct users
3. Client:

   * Receives file
   * Saves as `received_filename`

---

### 👥 Active Users

* Server maintains:

```java
Map<String, DataOutputStream> clients
```

* Sends updates:

```
USERLIST:user1,user2,user3
```

* UI updates ListView

---

## ⚡ Multithreading

### 🖥️ Server

* One thread per client (`ClientHandler`)
* Handles:

  * Messages
  * Files
  * Disconnection

### 💻 Client

* UI Thread → JavaFX
* Listener Thread → receives messages
* File Thread → sends files without freezing UI

---

## ⚠️ Important Notes

### ✅ Prevent Empty Inputs

* Empty messages are ignored
* Registration should validate:

  * Email not empty
  * Password not empty

---

### 📁 File Storage

* Files are saved locally as:

```
received_filename.ext
```
inside your project folder location you can check it after sending.

---

### 🔌 Port Configuration

In `Config.java`:

```java
public static final int PORT = 5000;
```

---

## 🧪 Testing Checklist

* [ ] Register new user
* [ ] Login with user
* [ ] Send broadcast message
* [ ] Send private message
* [ ] Send broadcast file
* [ ] Send private file
* [ ] See active users update
* [ ] Check database entries

---

## 🚀 Future Improvements (Optional)

* Chat UI (bubbles, timestamps)
* Online/offline status
* File download location selection
* Message history loading
* Email validation & encryption

---

## 👨‍💻 Contributors

* NIBRETU MENGAW
* YONAS ABATE
* ABEBE WORKINEH 
* HUSSNIA MOHAMMED
* HILINA YINAGER

---

## 📌 Final Notes

* Always start the server first
* Use multiple clients to test features
* Ensure MySQL is running before starting

---

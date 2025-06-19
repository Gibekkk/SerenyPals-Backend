# SerenyPals-Backend
---
Frontend Repo:
https://github.com/Gibekkk/SerenyPals-Frontend

ADR Repo:
https://github.com/Gibekkk/SerenyPals-ADR

---
<h1 align="left">SERENYPALS-BACKEND</h1>
<p><em>Empowering Growth Through Seamless Mental Wellness Solutions</em></p>

<img alt="last-commit" src="https://img.shields.io/github/last-commit/Gibekkk/SerenyPals-Backend?style=flat&logo=git&logoColor=white&color=0080ff" style="margin: 0px 2px; display: inline">
<img alt="repo-top-language" src="https://img.shields.io/github/languages/top/Gibekkk/SerenyPals-Backend?style=flat&color=0080ff" style="margin: 0px 2px; display: inline">
<img alt="repo-language-count" src="https://img.shields.io/github/languages/count/Gibekkk/SerenyPals-Backend?style=flat&color=0080ff" style="margin: 0px 2px; display: inline">

<p><em>Built with the tools and technologies:</em></p>
<img alt="Markdown" src="https://img.shields.io/badge/Markdown-000000.svg?style=flat&logo=Markdown&logoColor=white" style="margin: 0px 2px;">
<img alt="Spring" src="https://img.shields.io/badge/Spring-000000.svg?style=flat&logo=Spring&logoColor=white" style="margin: 0px 2px;">
<img alt="Docker" src="https://img.shields.io/badge/Docker-2496ED.svg?style=flat&logo=Docker&logoColor=white" style="margin: 0px 2px;">
<img alt="XML" src="https://img.shields.io/badge/XML-005FAD.svg?style=flat&logo=XML&logoColor=white" style="margin: 0px 2px;">
<img alt="phpMyAdmin" src="https://img.shields.io/badge/phpMyAdmin-6C78AF.svg?style=flat&logo=phpMyAdmin&logoColor=white" style="margin: 0px 2px;">

<br>
<hr>

## Table of Contents
- [Overview](#overview)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Usage](#usage)

<hr>

## Overview
SerenyPals-Backend is a versatile backend framework crafted to support social, mental health, and community-driven applications. It emphasizes maintainability, scalability, and security, enabling developers to build feature-rich APIs efficiently. The core features include:

- 🧩 **Modular Architecture:** Well-structured models, controllers, and services for diverse features like forums, chat, bookings, and user management.
- 🐳 **Containerized Deployment:** Docker Compose setup ensures consistent environments for development, testing, and production.
- 🔒 **Secure Authentication:** Integrated Firebase, password hashing, and session management for robust user security.
- 📊 **Rich Data Models:** Entities for tips, diaries, forums, and user interactions support engaging social and mental health features.
- 🛠️ **Comprehensive API Layer:** REST controllers and utility classes facilitate seamless client-server communication.
- 🚀 **Scalable & Maintainable:** Clear decision-making guides and dependency management promote long-term growth and ease of updates.

<hr>

## Getting Started

### Prerequisites
This project requires the following dependencies:
- **Programming Language:** Java
- **Package Manager:** Maven
- **Container Runtime:** Docker

### Installation
Build SerenyPals-Backend from the source and install dependencies:

1. **Clone the repository:**
   ```sh
   git clone https://github.com/Gibekkk/SerenyPals-Backend
2. **Navigate to the project directory:**
   ```sh
   cd SerenyPals-Backend
3. **Install the dependencies:**
 Using docker:
   ```sh
   docker build -t Gibekkk/SerenyPals-Backend .
 Using maven:
   ```sh
   mvn clean install
   ```

## Usage:
 Run the project with:
   Using docker:
   ```sh
   docker run -it {image_name}
   ```
   Using maven:
   ```sh
   mvn exec:java
   ```

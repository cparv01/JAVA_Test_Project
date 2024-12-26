# TestProject

## Overview
**TestProject** is a Spring Boot-based application that implements a secure login system using OTP (One-Time Password). The application allows users to log in using their registered mobile number or email ID. For email-based OTPs, the project uses **Spring Mail**, while for mobile-based OTPs, **Twilio** is utilized. All authentication and session management are handled through **JWT (JSON Web Token)**, ensuring secure access.

The project also includes a **React.js** frontend that handles the user input, validates OTP, and interacts with the backend Spring Boot APIs for a seamless user experience.

## Features
- **User Login**:
  - Login via **Email ID** or **Mobile Number**.
  - For email, OTP is sent using Spring Mail.
  - For mobile, OTP is sent via Twilio SMS service.

- **OTP Authentication**:
  - OTP is generated and sent to the user’s registered email or mobile number.
  - The user is required to enter the OTP to authenticate their session.

- **JWT Authentication**:
  - Once the OTP is verified, a **JWT token** is issued to the user for secure access to the application.
  - JWT ensures that the user remains authenticated without requiring repeated OTP verifications.

- **Frontend with React**:
  - A React.js frontend that communicates with the Spring Boot backend to manage login and OTP verification.

## Technologies Used
- **Spring Boot**: Framework used to build the backend of the application.
- **Spring Mail**: For sending OTPs via email.
- **Twilio**: For sending OTPs via SMS to the registered mobile number.
- **JWT (JSON Web Token)**: For secure user authentication and maintaining sessions.
- **React.js**: Frontend framework for creating the user interface.
- **Axios**: For making API requests from the React frontend to the Spring Boot backend.

## Setup & Installation

### Prerequisites
Before you begin, ensure you have the following tools installed:

#### Backend (Spring Boot):
- **Java** (JDK 11 or higher)
- **Maven** (for building the project)
- **Twilio Account** (for SMS-based OTP)
- **Spring Mail Configuration** (for email-based OTP)

#### Frontend (React):
- **Node.js** (version 14 or higher)
- **npm** or **yarn** (for managing frontend dependencies)

### Backend (Spring Boot) Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/TestProject.git
   cd TestProject

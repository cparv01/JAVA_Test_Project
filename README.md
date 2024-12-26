﻿# JAVA_Test_Project

# TestProject

## Overview
**TestProject** is a Spring Boot-based application that implements a secure login system using OTP (One-Time Password). The application allows users to log in using their registered mobile number or email ID. For email-based OTPs, the project uses **Spring Mail**, while for mobile-based OTPs, **Twilio** is utilized. All authentication and session management are handled through **JWT (JSON Web Token)**, ensuring secure access.

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

## Technologies Used
- **Spring Boot**: Framework used to build the backend of the application.
- **Spring Mail**: For sending OTPs via email.
- **Twilio**: For sending OTPs via SMS to the registered mobile number.
- **JWT (JSON Web Token)**: For secure user authentication and maintaining sessions.
  
## Setup & Installation

### Prerequisites
Before you begin, ensure you have the following tools installed:
- **Java** (JDK 11 or higher)
- **Maven** (for building the project)
- **Twilio Account** (for SMS-based OTP)
- **Spring Mail Configuration** (for email-based OTP)

### Clone the Repository
Clone the repository to your local machine:
```bash
git clone https://github.com/your-username/TestProject.git

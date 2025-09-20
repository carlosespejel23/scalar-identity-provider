# Scalar

<img src="frontend/public/logo/logo-single.png" width="200">

## What is Scalar?

Scalar is a web-based application that allows SBM's to create and manage their own data and proceses. Within Scalar, users can interact with AI agents to create and manage their own data and processes. Scalar is designed to be user-friendly and intuitive, making it easy for users to get started quickly.

Scalar is built using the latest web technologies, including React, Laravel, and PostgreSQL. It is designed to be scalable and flexible, allowing users to customize their experience to meet their specific needs. Scalar is also designed to be secure, with built-in authentication and authorization features to protect user data.

Scalar provides a powerful and flexible platform for managing data and processes, making it an ideal solution for businesses of all sizes in any niche. Whether you're a small startup or a large enterprise, Scalar can help you streamline your operations and improve your productivity.

## Software Stack
Scalar is built using the following technologies:
- **Frontend**: React, Mui UI, Vite, TypeScript
- **Backend**: Laravel API, PostgreSQL, PSR-12 Standard Coding
- **Deployment**: Docker, Docker Compose, Digital Ocean
- **DevOps**: GitLab

## Installation
To get started with Scalar, you need to set up both the frontend and backend environments. Follow the instructions below to install the necessary dependencies and run the application locally.

Clone the repository and install the dependencies for both the frontend and backend.
```sh
git clone https://gitlab.com/scalar-workspace/scalar-v1.git
cd scalar-v1
```

**Frontend Intallation**:
- **Prerequisites**: Node.js >=20 (Recommended)
- **Installation**:
```sh
npm i
npm run dev
```

**Backend Installation**:
After cloning the repository, navigate to the backend directory and install the dependencies using ```composer install ```. You must install Xampp and Composer.
- **Prerequisites**: PHP >=8.0, PostgreSQL >=15
- **Important**: 
Uncomment this extensions from php.ini
```
extension=pdo_pgsql
extension=pgsql
extension=zip
```
- **Installation**:
```sh
cd backend

py -m venv venv
pip install -e .

py app/backend_pre_start.py
py app/initial_data.py

fastapi dev app/main.py
```

## Roadmap
Soon...

## Authors and acknowledgment
The authors of Scalar are:
- **Carlos Espejel Sanchez**: **CEO** and **Founder** of Scalar.

## License
Privately licensed. Please contact the author for more information.

## Project status
In development. The project is currently in the alpha stage, and we are actively working in the backend and frontend. We are also working on the deployment process and DevOps pipeline. The project is not yet ready for production use, but we are making progress and hope to have a stable release soon.
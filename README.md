# RESEARCH.UF by GatorApps

The Common Application for campus research opportunities.

## Use RESEARCH.UF

[https://research.dev.gatorapps.org](https://research.dev.gatorapps.org)


## Features

- Sign in with GatorLink
- Student and faculty dashboards
- Discover, view, and apply to resarch opportinities
- Track application statuses
- Maintain lab profile, post and edit open positions
- Receive and review applications online

<!-- ## Getting Started

### Prerequisites
- [Git](https://git-scm.com/)
- [Node.js](https://nodejs.org/)
- [npm (comes with Node.js)](http://npmjs.com/)

### Installation and Setup
From the command line:
  ```bash
  # Clone the repository
  git clone https://github.com/lukexlii/templateapp.gatorapps.org.git

  # Change working directory into the repository
  cd templateapp.gatorapps.org
  ```

#### **Backend**
1. **Navigate to the `server` directory and install dependencies**

  ```bash
  npm i
  ```

2. **Create a `.env` file under the server directory with the following variables**

  **MongoDB connection string**

  ```
  DATABASE_URI='your_mongodb_connection_string'
  ```

  **JSON array of secrets for `express-session` ID cookies**

  Only the secret at index 0 will be used to sign new sessions, but sessions signed using other secrets in array are considered valid (generally used for rotating secrets).

  ```
  SESSION_COOKIE_SECRET='["your_session_secret","your_opetional_session_secret","your_opetional_session_secret"]'
  ```
  
  **ES256 public and private keys for auth tokens**

  ```
  USR_AUTH_TOKEN_PUBLIC_KEY='your_public_key'
  USR_AUTH_TOKEN_PRIVATE_KEY='your_private_key'
  ```

  You may use `openssl` to generate a key pair
  ```bash
  openssl ecparam -genkey -name prime256v1 -noout -out private.pem
  openssl ec -in ec_private.pem -pubout -out public.pem
  ```

  **Google OAuth 2 client secret**
  ```
  GOOGLE_CLIENT_SECRET='your_google_client_secret'
  ```

  **Backend port number**

  The default backend port is 8000. You may change it by setting the optional PORT variable.

  ```
  PORT=your_backend_port
  ```

3. **Setup additional configurations in `config` directory**

The `config` directory contains non-sensitive global configurations. Most variables have default values. You may adjust them according to the comments as you like.

The only variable required to populate is `GOOGLE_CLIENT_ID` in `config/config.js`. Change `GOOGLE_CLIENT_ID_HERE` to your Google Oauth 2 client ID.

#### **Frontend**
1. **Navigate to the `client` directory and install dependencies**

In the `server` directory:

  ```bash
  npm i
  ```

2. **Create a `.env` file under the client directory with the following variables**

  ```
    REACT_APP_APP_NAME='templateapp'
    REACT_APP_FRONTEND_HOST=''
    REACT_APP_SERVER_HOST=''
    REACT_APP_SERVER_API_PATH='/appApi/templateapp'
    REACT_APP_IDP_HOST='https://account.dev.gatorapps.org'
    REACT_APP_IDP_API_PATH='/globalApi/account'
  ```

#### **Simulating .gatorapps.org with SSL on localhost**

TO ADD

### Running the project

1. **Start the backend**

In the `server` directory:

  ``` bash
  npm run dev
  ```

Your backend should now be running on [https://templateapp.dev-local.gatorapp.org:8300](http://localhost:8300) or your specified port.


2. **In a new terminal, start the frontend**

In the `client` directory:

  ``` bash
  npm start
  ```

Your frontend should now be running on [https://templateapp.dev-local.gatorapp.org:3300](http://localhost:3300) or your specified port. -->

<!-- ## Testing

[Instructions on how to run tests for your project, if applicable.]

## Deployment

[Notes about how to deploy this on a live system. Could include steps for VPS, Docker, cloud services, etc.] -->

## Built With

- [React](https://react.dev/) - Frontend library
- [Material-UI](https://mui.com/) - UI framework for React
- [Redux](https://redux.js.org/) - State management
- [Java](https://www.oracle.com/java/) – Programming language
- [Spring Boot](https://spring.io/projects/spring-boot) – Backend framework
- [Maven](https://maven.apache.org/) – Project management and build tool
- [MongoDB](https://www.mongodb.com/) - NoSQL database

<!-- ## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us. -->

## Authors

- **Luke Li** - [luke.li@ufl.edu](mailto:luke.li@ufl.edu) - [@lukexlii](https://github.com/lukexlii)
- **Avary McCormack** - [a.mccormack@ufl.edu](mailto:a.mccormack@ufl.edu) - [@avary8](https://github.com/avary8)
- **Eric Zhou** - [ericzhou@ufl.edu](mailto:ericzhou@ufl.edu) - [@ercz16](https://github.com/ercz16)

<!-- - See also the list of [contributors](https://github.com/your_username/project_name/contributors) who participated in this project. -->

## License

Copyright 2025 GatorApps. All rights reserved.

All logos and trademarks are the property of their respective owners.

<!-- ## Acknowledgments

- UI Design recreated from ONE.UF -->
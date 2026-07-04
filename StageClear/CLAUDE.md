# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a minimal Spring Boot web application. The project structure follows standard Maven layout:

```
src/main/java/io/stageclear/demo/    # Application source code
src/main/resources/                   # Configuration files
src/test/java/io/stageclear/demo/    # Test source code
```

## Build & Run Commands

```bash
# Run the application
mvn spring-boot:run

# Run tests
mvn test

# Package as JAR
mvn package

# Clean build
mvn clean
```

## Application Details

- **Java Version**: 17
- **Default Port**: 8080
- **Main Class**: `io.stageclear.demo.StageClearApplication`
- **Context Path**: `/`

## Architecture

This is a standard Spring Boot MVC application using `spring-boot-starter-web`. Routes are defined via `@RestController` and `@Controller` annotations using Spring's request mapping annotations (`@GetMapping`, `@PostMapping`, etc.).

The application uses Spring Boot's auto-configuration for an embedded Tomcat server. No custom server configuration is present in `application.properties`.

## Notes

- The `pom.xml` uses `spring-boot-starter-web` (not `webmvc`). Ensure dependencies are corrected if changing.
- The parent POM version `4.1.0` is non-standard; verify the actual Spring Boot version in use.
- No database, security, or additional middleware is configured — this is a bare-bones web application skeleton.

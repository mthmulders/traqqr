---
description: 'Project-specific Java coding style for Traqqr'
applyTo: '**/*.java'
---

# Project-specific Java coding style for Traqqr

## General Instructions

## Applied practices

- **Records**: For classes primarily intended to store data (e.g., DTOs, immutable data structures), use Java records instead of traditional classes.
- **Pattern Matching**: Use pattern matching for `instanceof` and `switch` expressions to simplify conditional logic and type casting.
- **Type Inference**: Use `var` for local variable declarations.
- **Immutability**: Prefer immutable objects. Make classes and fields `final` where possible. Use collections from `List.of()`/`Map.of()` for fixed data. Use `Stream.toList()` to create immutable lists.
- **Streams and Lambdas**: Use the Streams API and lambda expressions for collection processing. Prefer method references over lambdas that pass all its parameters to another function.
- **Null Handling**: Avoid returning or accepting `null`. Use `Optional<T>` for possibly-absent values and `Objects` utility methods like `equals()` and `requireNonNull()`.

### Naming Conventions

- Follow Google's Java style guide:
  - `UpperCamelCase` for class and interface names.
  - `lowerCamelCase` for method and variable names.
  - `UPPER_SNAKE_CASE` for constants.
  - `lowercase` for package names.
- Use nouns for classes (`UserService`) and verbs for methods (`getUserById`).
- Avoid abbreviations and Hungarian notation.

### Bug Patterns


| Rule ID | Description | Example / Notes |
| --- | --- | --- |
| `S2095` | Resources should be closed | Use try-with-resources when working with streams, files, sockets, etc. |
| `S1698` | Objects should be compared with `.equals()` instead of `==` | Especially important for Strings and boxed primitives. |
| `S1905` | Redundant casts should be removed | Clean up unnecessary or unsafe casts. |
| `S3518` | Conditions should not always evaluate to true or false | Watch for infinite loops or if-conditions that never change.                                     |
| `S108` | Unreachable code should be removed | Code after `return`, `throw`, etc., must be cleaned up. |

## Code Smells

| Rule ID | Description | Example / Notes |
| --- | --- | --- |
| `S107` | Methods should not have too many parameters | Refactor into helper classes or use builder pattern. |
| `S121` | Duplicated blocks of code should be removed | Consolidate logic into shared methods. |
| `S138` | Methods should not be too long | Break complex logic into smaller, testable units. |
| `S3776` | Cognitive complexity should be reduced | Simplify nested logic, extract methods, avoid deep `if` trees. |
| `S1192` | String literals should not be duplicated | Replace with constants or enums. |
| `S1854` | Unused assignments should be removed | Avoid dead variables: remove or refactor. |
| `S109` | Magic numbers should be replaced with constants | Improves readability and maintainability. |
| `S1188` | Catch blocks should not be empty | Always log or handle exceptions meaningfully. |

## Build and Verification

- After adding or modifying code, verify the project continues to build successfully.
- Run `mvn spotless:apply` to ensure code formatting adheres to the project standards.
- Run `mvn verify` to ensure all tests pass.

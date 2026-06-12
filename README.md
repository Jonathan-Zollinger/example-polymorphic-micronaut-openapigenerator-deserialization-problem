# Polymorphic DTO 

This project is a minimal reproduction demonstrating limitations in OpenAPI Generator when modeling polymorphic primitive types.

## Overview

The [OpenAPI schema in this build] defines a `CustomField` object with a `value` property that uses `oneOf` to represent multiple primitive types (string, number, boolean, integer, and string array).

> NOTE: 
> The goal is to model a typical polymorphic field (similar to Zendesk custom fields) using standard OpenAPI constructs.

The [test] attempts to deserialize JSON payloads that match the schema in the oas file.

Tests fail with 
> No bean introspection available for type [interface com.example.model.CustomFieldValue]. Ensure the class is annotated with io.micronaut.core.annotation.Introspected

For comparison, [this branch employs a working solution without a generator](https://github.com/Jonathan-Zollinger/example-polymorphic-micronaut-openapigenerator-deserialization-problem/tree/not-generated-version).

[OpenAPI schema in this build]:src/main/resources/schema.yml
[test]:src/test/groovy/com/example/DemoSpec.groovy

## orm-benchmark

JMH benchmarks for popular Java ORM-ish frameworks.

## Motivation

All ORM-ish frameworks introduce some kind of "processing" overhead, either by parsing their own DSL or implementing custom object mappers. How exactly do these overheads affect your application when compared to plain JDBC (with simple static mappers)? The aim of this project is to study those overheads.
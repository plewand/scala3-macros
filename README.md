## Scala 3 macros

This project demonstrates usage of Scala 3 macros in a simple use case.
The program generates type safe prepared statements on compilation time aligned with externally loaded SQL script.

Also, it contains some other macro examples explaining macros features. 

The program is described in the blog [here](https://pawel7.medium.com/scala-3-macros-without-pain-ce54d116880a).

### Examples

There are following separate examples, each in a separate package:
* [Simple macro](src/main/scala/scalamacros/simple)
* [Inlining](src/main/scala/scalamacros/inline)
* [Log AST](src/main/scala/scalamacros/logast)
* [Symbols](src/main/scala/scalamacros/symbol)
* [Prepared statement - the main project](src/main/scala/scalamacros/statements)

### Usage

This is a normal sbt project. 

* Compile the code with 
```
sbt compile
``` 
During compilation many messages are printed related to the macros.

* Run
```
sbt run
```
The list of the entry points of the examples with appear, choose one.

### Tips
* Try to use Visual Studio code with Metals if IntelliJ fails to work (still pending work on the time of writing).
* In case of the SQL schema file cannot be found, use its absolute path.

More information on Scala 3 metaprogramming can be found 
[here](https://docs.scala-lang.org/scala3/reference/metaprogramming.html).

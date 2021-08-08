## Scala 3 macros

This project demonstrates usage of Scala 3 macros in a simple use case.
The program generates type safe prepared statements on compilation time aligned with externally loaded SQL script.

Also, it contains some other macro examples explaining macro features. 

The program is described in the blog here <>.

### Examples

There are following separate examples, each in separate package:
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
The list of entry points of examples with appear, choose one.

### Tips
* Try to use Visual Studio code with Metals if IntelliJ fails to work (still pending work on the time of writing).
* In case of the schema sql file cannot be found, use its absolute path.

For more information on the Dotty and features used in the project, please visit:
[dotty-reference](https://dotty.epfl.ch/docs/reference/overview.html).

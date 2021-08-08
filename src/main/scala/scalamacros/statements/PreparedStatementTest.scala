package scalamacros.statements

import scalamacros.logast.logAST

@main def preparedStatemntTest(): Unit = {

  // Type given explicitly - better error messages than if the type is inferred.
  // Replace the name of the table or the argument with a variable - an error reported by the compiler
  // due to the value isn't know on compile time.
  // Change the type or the name of the column to be different that in sql/schema.sql - a compilation error.
  val statement: PreparedStatement[(Int, String)] =
    StatementGenerator.createPreparedStatement("user")(ColDef[Int]("id"), ColDef[String]("username"))

  statement.insert(1, "John")
  
  // Error will be reported as the type of the first column doesn't match.
  // statement.insert("a", "Brad")
  
  // Logged AST that was used to find out the structure of the code that was matched in the macro.
  logAST {
    (ColDef[Int]("id"), ColDef[String]("lastName"))
  }  
}

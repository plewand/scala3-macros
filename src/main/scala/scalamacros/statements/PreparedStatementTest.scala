package scalamacros.statements

import scalamacros.logast.logAST

@main def preparedStatementTest(): Unit = {

  // Type given explicitly - better error messages than if the type is inferred.
  // Replace the name of the table or the argument with a variable - an error reported by the compiler
  // due to the value isn't know on compile time.
  // Change the type or the name of the column to be different that in sql/schema.sql - a compilation error.
  // In case of error messages related to schema (possible in IDE), set StatementGenerator.SchemaPath to an absolute path.
  val userStatement: PreparedStatement[(Int, String)] =
    StatementGenerator.createPreparedStatement("user")(ColDef[Int]("id"), ColDef[String]("username"))

  userStatement.insert(1, "John")
  
  // Error will be reported as the type of the first column doesn't match.
  // userStatement.insert("a", "Brad")

  // Single colume case - tuple is not used in this case.
  val userIdStatement: PreparedStatement[Int] =
    StatementGenerator.createPreparedStatement("user_id")(ColDef[Int]("id"))

  userIdStatement.insert(2)
  // Uncomment to get compilation time check error.
  // userIdStatement.insert(2, "John")

  // Logged AST that was used to find out the structure of the code that was matched in the macro.
  logAST {
    (ColDef[Int]("id"), ColDef[String]("lastName"))
  }  
}

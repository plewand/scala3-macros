package scalamacros.logast

@main def logASTTest(): Unit = {

  class A

  // Logged AST that was used to find out the structure of the code that was matched in the macro.
  val a: A = logAST {
    new A
  }  
}

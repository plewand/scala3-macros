package scalamacros.simple

@main def simpleMacroTest() = {
  
  def printConstant = SimpleMacro.printlnForConstant()
  // It will print "user1"
  printConstant

  def printUppercase = SimpleMacro.printlnUppercase("user2")
  // It will print "USER2"
  printUppercase
}

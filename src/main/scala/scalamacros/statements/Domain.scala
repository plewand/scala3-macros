package scalamacros.statements

// Type matching - a new Scala 3 feature - is used to construct the type that will be used
// to pass runtime arguments to the generated prepared statement.
type CallArgs[Xs]  = Xs match
  case EmptyTuple   => Xs
  case ColDef[b] *: xs => b *: CallArgs[xs]
  case ColDef[b] => b

// A class to simulate an unsafe statement.
class UsafeStatement(sql: String) {
  def insert(rowItems: Any*) = {
    println(s"Executing SQL: '${sql}' for args: [${rowItems.mkString(", ")}]")
  }
}

// Type safe prepared statemnet.
class PreparedStatement[A](statement: UsafeStatement) {
  def insert(rowItems: A) : Unit = rowItems match {
    case tuple: Tuple => statement.insert(tuple.toList:_*)
    case single => statement.insert(single)
  }
}

// Definintion of the column passed to the macro - a name with the bound type.
case class ColDef[A](name: String)


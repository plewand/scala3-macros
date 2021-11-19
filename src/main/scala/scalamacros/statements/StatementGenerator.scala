package scalamacros.statements

import scala.quoted.*

// Creating a class with Quotes argument allows to share quotes and reflect import across all the
// methods of the class and easily split the logic.
class StatementGenerator(using Quotes) {
  // In case of problems with finding the file, set it to absolute path.
  val SchemaPath = "sql/schema.sql"

  // This import is dependent on Quotes that are visible in the scope.
  import quotes.reflect.*

  // This class will create DbType enum based on TypeRepr of the type.
  // Note that the type is matched, unknown is captured (any lowercase name) and used to print its name.
  private def getDbType(typeRepr: TypeRepr): DbType = typeRepr.asType match {
    case '[Int]    => DbType.DbInt
    case '[String] => DbType.DbString
    case '[unknown] =>
      report.throwError("Unsupported type as DB column " + Type.show[unknown])
  }

  // Validation with the schema that is present in sql/schema.sql. In case of mismatch
  // a compilation error is returned.
  private def validateWithSchema(
      tableName: String,
      columnInfo: Seq[ColumnInfo]
  ): Unit = {
    val parsed = SqlParser.parse(SchemaPath)
    
    val schemaColumns = parsed.getOrElse(
      tableName,
      report.throwError(
        "Schema doesn't have table definded with name: " + tableName
      )
    )
    
    columnInfo.foreach(ci => {
      val schemaColumn = schemaColumns
        .find(_.name == ci.name)
        .getOrElse(
          report.throwError("Table doesn't have column with name: " + ci.name)
        )
      if (schemaColumn.dbType != ci.dbType) {
        report.throwError(
          s"Invalid type for $tableName.${ci.name}: ${ci.dbType} != ${schemaColumn.dbType}"
        )
      }
    })
  }

  private def parseColumDef(columnDefTerm: Term) : ColumnInfo = {

    // The second argument of Apply is a list of terms representing a single call argument.
    // This time quoted expression is matched as it is simpler than full AST.
    // Parameter 'a' captures the type and $name expression repesenting the name argument of the contructor.
    columnDefTerm.asExprOf[ColDef[_]] match {

      case '{ ColDef[a]($name) } =>
        val paramType = TypeRepr.of[a]

        // It is possible (and for this case required) to obtain compile time values. It
        // will fail in case of the name is a variable.
        val nameValue = name.valueOrError
        println(
          s"Type: ${Type.show[a]} , value: $nameValue, name AST: ${name.asTerm
            .show(using Printer.TreeStructure)}"
        )

        ColumnInfo(getDbType(paramType), name.valueOrError)

      case _ =>
        report.throwError(
          "Statically parsed ColDef items needed e.g. ColDef[Int](\"a\")"
        )
    }
  }

  private def parseColumnInfo(paramMapping: Term): Seq[ColumnInfo] = {

    // Low level AST is matched in the case below, the structure can be seen with show(using Printer.TreeStructure).
    paramMapping match {
      // Case with a single parameter
      case Inlined(None, Nil, Apply(TypeApply(Select(Ident("ColDef"), "apply"), _), columnDefs)) =>
        println(
            "Single parameter term: " + paramMapping.show(using Printer.TreeStructure)
        )

        Seq(parseColumDef(paramMapping))


      // Multiple parameters case
      case Inlined(None, Nil, Apply(TypeApply(_, _), columnDefs)) =>
        columnDefs.map(columnDefsTerm =>
          println(
            "Parameters term: " + columnDefsTerm.show(using Printer.TreeStructure)
          )

          parseColumDef(columnDefsTerm)
        )

      case _ =>
        report.throwError(
          "Provide ColDef list e.g.: StatementGenerator.createPreparedStatement(ColDef[Int](\"c1\"), ColDefl[String](\"c2\"))"
        )
    }
  }

  private def buildInsertSql(
      tableName: String,
      columns: Seq[ColumnInfo]
  ): String = {
    val columnNames = columns.map(_.name).mkString(", ")
    val placeholders = "?".repeat(columns.length).mkString(", ")
    s"INSERT INTO $tableName ($columnNames) VALUES ($placeholders)"
  }

  def createPreparedStatementImpl[A: Type](
      table: Expr[String],
      columnMapping: Expr[A]
  ): Expr[PreparedStatement[CallArgs[A]]] = {

    // There are utilies for output, but println and throwing custom errors also work.
    println(
      "Generating prepared statement for arguments: " + columnMapping.asTerm
        .show(using Printer.TreeStructure)
    )

    val tableName = table.valueOrError

    val columns = parseColumnInfo(columnMapping.asTerm)

    println("Columns: " + columns)

    validateWithSchema(tableName, columns)

    val sql = Expr(buildInsertSql(tableName, columns))

    // As the types was verified, unsafe statement can be safefy created and passed to the
    // Prepared statemnt cont
    '{
      new PreparedStatement[CallArgs[A]](UsafeStatement($sql))
    }.asExprOf[PreparedStatement[CallArgs[A]]]
  }
}

object StatementGenerator {

  // Auxilirary function to create the class as the inlined main maro function needs a single expression.
  private def callImplementation[A: Type](
      tableName: Expr[String],
      columnMapping: Expr[A]
  )(using
      Quotes
  ): Expr[PreparedStatement[CallArgs[A]]] =
    new StatementGenerator()
      .createPreparedStatementImpl[A](tableName, columnMapping)

  inline def createPreparedStatement[A](inline tableName: String)(
      inline columnMapping: A
  ): PreparedStatement[CallArgs[A]] = ${
    callImplementation[A]('tableName, 'columnMapping)
  }
}

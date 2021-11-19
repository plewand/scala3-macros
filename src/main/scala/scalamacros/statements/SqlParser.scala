package scalamacros.statements

// All supported database column types, in this example limited to String and Int.
enum DbType {
  case DbInt
  case DbString
}

// Auxiliary class to keep parsed column information.
case class ColumnInfo(val dbType: DbType, name: String)

// Very simple parser just to work with the example. Programmer perfection assumed - no errors in sql script.
object SqlParser {
  def parse(path: String): Map[String, Seq[ColumnInfo]] = {
    val content = scala.io.Source.fromFile(path).mkString
    val statements = content.split(";").filterNot(_.isBlank)
    statements.map(parseStatement).toMap
  }

  private def parseStatement(statement: String) ={
    val splatStatement = statement.split("[\\s,:]").filterNot(_.isBlank).map(_.toLowerCase)
    println(s"Statement: ${splatStatement.mkString(",")}")
    val tableName = splatStatement(2)
    val columns = splatStatement.drop(4).dropRight(1)
    val colInfo = columns.toSeq
      .sliding(2, 2)
      .map(col => ColumnInfo(typeByName(col(1)), col(0)))
      .toSeq

    tableName -> colInfo
  }

  private def typeByName(name: String) = {
    if(name == "int") {
      DbType.DbInt
    } else if(name.startsWith("varchar")) {
      DbType.DbString
    } else {
      throw RuntimeException("Sql parsing error, wrong type: " + name)
    }
  }
}

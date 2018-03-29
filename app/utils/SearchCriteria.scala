package utils

import anorm.NamedParameter

abstract class SearchCriteria(
                               val limit: Long = 0,
                               val offset: Long = 0) {

  protected val whereParams: Seq[(String, NamedParameter)]

  final def whereClause: String =
    if (whereParams.nonEmpty)
      whereParams.map(_._1).mkString(" WHERE ", " AND ", "")
    else ""

  final def limitClause: String =
    " LIMIT {limit} OFFSET {offset}"

  final def namedWhereParamsList: Seq[NamedParameter] = whereParams.map(_._2)

  final def namedLimitParamsList: Seq[NamedParameter] = Seq(
    NamedParameter("limit", limit), NamedParameter("offset", offset))

}

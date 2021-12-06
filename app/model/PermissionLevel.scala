package model

import reactivemongo.api.bson._

object PermissionLevel extends Enumeration {
  type PermissionLevel = Value

  val Edit = Value("Edit")
  val View = Value("View")
  val Restricted = Value("Restricted")

  implicit val toBson = BSONHandler[PermissionLevel](
    a => PermissionLevel.withName(a.asOpt[BSONString].map(_.value).getOrElse(Restricted.toString)),
    b => BSONString(b.toString)
  )
}

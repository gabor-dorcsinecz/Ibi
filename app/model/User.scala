package model

import play.api.libs.json.{Json, OFormat}
import reactivemongo.api.bson._
import reactivemongo.play.json._
import model.PermissionLevel._

final case class User(
                 username: String,
                 permission: PermissionLevel.PermissionLevel,
                 _id: BSONObjectID = BSONObjectID.generate()
               )

object User {

  //implicit val userFormat: OFormat[User] = Json.format[User]

  //implicit val personHandler: BSONHandler[User] = Macros.handler[User]
  implicit val userWriter: BSONDocumentWriter[User] = Macros.writer[User]
  implicit val userReader: BSONDocumentReader[User] = Macros.reader[User]
}




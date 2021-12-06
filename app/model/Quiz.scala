package model

import play.api.libs.json.{Format, JsError, JsString, JsSuccess, JsValue, Json, OFormat}
import reactivemongo.api.bson.{BSONDocumentReader, BSONDocumentWriter, BSONObjectID, Macros}
import reactivemongo.play.json._

final case class Quiz(
                       question: String,
                       options: List[String],
                       correctAnswer:Int,   //one element from the above list starting from zero
                       _id: BSONObjectID = BSONObjectID.generate()
                     )

object Quiz {

  implicit val bsonObjectIDFormat = new Format[BSONObjectID] {
        def reads(json: JsValue) =   BSONObjectID.parse(json.as[String]).map(JsSuccess(_)).getOrElse(JsError("Cannot Parse BSONObjectID"))//JsSuccess(RequestFrequency.withName(json.as[String]))
        def writes(rf: BSONObjectID) = JsString(rf.stringify)
      }

    implicit val format: OFormat[Quiz] = Json.format[Quiz]

  implicit val userWriter: BSONDocumentWriter[Quiz] = Macros.writer[Quiz]
  implicit val userReader: BSONDocumentReader[Quiz] = Macros.reader[Quiz]

}

//final case class QuizAnswer(
//                              user: User,
//                              quiz: Quiz,
//                              answer: Int
//                            )
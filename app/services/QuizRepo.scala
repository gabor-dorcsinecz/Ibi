package services

import model.Quiz
import model.Quiz._
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONObjectID, Macros}
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.{Cursor, ReadPreference}
import reactivemongo.api.commands.WriteResult

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class QuizRepo @Inject()(
                          implicit executionContext: ExecutionContext,
                          reactiveMongoApi: ReactiveMongoApi
                        ) {

  def collection: Future[BSONCollection] = reactiveMongoApi.database.map(_.collection("quizes"))

  def findAll(limit: Int = 100): Future[Seq[Quiz]] = {

    collection.flatMap(
      _.find(BSONDocument(), Option.empty[Quiz])
        .cursor[Quiz](ReadPreference.Primary)
        .collect[Seq](limit, Cursor.FailOnError[Seq[Quiz]]())
    )
  }

  def findOne(id: BSONObjectID): Future[Option[Quiz]] = {
    collection.flatMap(_.find(BSONDocument("_id" -> id), Option.empty[Quiz]).one[Quiz])
  }

  def create(quiz: Quiz): Future[WriteResult] = {
    collection
      .flatMap(_.insert(ordered = false)
        .one(quiz))
  }

  def update(quiz: Quiz) = {
    //collection.flatMap(_.update(ordered = false).one(BSONDocument("_id" -> quiz._id), quiz))
    //collection.flatMap(_.findAndUpdate(BSONDocument("_id" -> quiz._id), quiz))
    //collection.flatMap(_.update(ordered = false).one(BSONDocumentWriter(BSONDocument.quiz)))
      //collection.flatMap(_.update(ordered = false).element()one(BSONDocument("_id" -> quiz._id), BSONDocument("$set" -> BSONDocumentWriter(quiz))))
//    collection.flatMap(_.findAndUpdate(
//      selector = Json.obj("_id" -> quiz._id.stringify),
//      update = Json.obj("$set" -> Json.toJson(quiz)),
//      upsert = true
//    ))
//    collection.flatMap(_.findAndUpdate(
//      BSONDocument("_id" -> quiz._id),
//      BSONDocument("$set" -> BSONDocument(
//        "question" -> quiz.question,
//        "options" -> quiz.options,
//        "correctAnswer" -> 99)),
//      upsert = true
//    ))
    collection.flatMap(_.findAndUpdate(BSONDocument("_id" -> quiz._id), quiz))
  }

  def delete(id: BSONObjectID):Future[WriteResult] = {
    collection.flatMap(
      _.delete().one(BSONDocument("_id" -> id), Some(1))
    )
  }
}
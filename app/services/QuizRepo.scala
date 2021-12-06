package services

import model.Quiz
import model.Quiz._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.bson.{BSONDocumentReader, BSONDocumentWriter, Macros, BSONDocument, BSONObjectID}
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

  def update(id: BSONObjectID, quiz: Quiz):Future[WriteResult] = {

    collection.flatMap(
      _.update(ordered = false)
        .one(BSONDocument("_id" -> id), quiz)
    )
  }

  def delete(id: BSONObjectID):Future[WriteResult] = {
    collection.flatMap(
      _.delete().one(BSONDocument("_id" -> id), Some(1))
    )
  }
}
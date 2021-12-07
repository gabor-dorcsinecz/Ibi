package controllers

import actions.{SecuredController, SecuredControllerComponents}
import model.Quiz
import play.api.data.Forms.{mapping, number, seq, text}
import play.api.data._
import reactivemongo.api.bson.BSONObjectID
import services.QuizRepo
import views.html._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class QuizController @Inject()(
                                val quizRepo: QuizRepo,
                                val scc: SecuredControllerComponents,
                              )(
                                implicit val executionContext: ExecutionContext
                              ) extends SecuredController(scc: SecuredControllerComponents) with play.api.i18n.I18nSupport {

  def list() = AuthenticatedAction.async { implicit request =>
    quizRepo
      .findAll()
      .map(quizes => Ok(quizlist(quizes, request.user.permission)))
  }

  def insert() = AuthenticatedAction { implicit request =>
    val newQuiz = Quiz("", List("", "", "", "", ""), 0)
    val newForm = QuizController.quizForm.fill(newQuiz)


    Ok(quizedit(newForm, false))
  }

  def update(id: String) = AuthenticatedAction.async { implicit request =>
    BSONObjectID.parse(id).map(bsonId =>
      quizRepo
        .findOne(bsonId)
        .map(optobj => optobj match {
          case Some(obj) =>
            val filledForm = QuizController.quizForm.fill(obj)
            Ok(quizedit(filledForm, true))
          case None =>
            BadRequest("Could not find the object with the id given")
        }
        )
    ).getOrElse(Future.successful(BadRequest("Could Not Parse BSONId")))
  }

  def submit(isUpdate: Boolean) = AuthenticatedAction.async { implicit request =>
    QuizController.quizForm
      .bindFromRequest()
      .fold(
        withErrors =>
          Future.successful(BadRequest("Failed")),
        formOk =>
          (isUpdate match {
            case true => quizRepo.update(formOk)
            case false => quizRepo.create(formOk)
          }).map { _ => Redirect(routes.QuizController.list()) }
      )
  }


}

object QuizController {

  val quizForm: Form[Quiz] = Form(
    mapping(
      "question" -> text(maxLength = 500),
      "options" -> seq(text),
      "correct" -> number.verifying(a => a >= 0 && a < 5),
      "_id" -> text
    )((q, o, c, id) => {
      Quiz(q, o.toList, c, BSONObjectID.parse(id).getOrElse(BSONObjectID.generate()))
    })(
      q => Option((q.question, q.options, q.correctAnswer, q._id.toString()))
    )
  )
}
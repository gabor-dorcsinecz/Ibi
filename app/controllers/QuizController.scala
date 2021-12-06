package controllers

import model.Quiz
import play.api.mvc.{AbstractController, ControllerComponents}
import reactivemongo.api.bson.BSONObjectID
import services.{QuizRepo, UserRepo}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import views.html._
import play.api.data.Forms.{mapping, number, seq, text}
import play.api.data._


@Singleton
class QuizController @Inject()(
                                val quizRepo: QuizRepo,
                                components: ControllerComponents
                              )(
                                implicit val executionContext: ExecutionContext
                              ) extends AbstractController(components) with play.api.i18n.I18nSupport {

  def list() = Action.async { implicit request =>
    quizRepo
      .findAll()
      .map(quizes => Ok(quizlist(quizes)))
  }

  def addShow() = Action { implicit request =>
    val newQuiz = Quiz("", List.empty, 0)
    val newForm = QuizController.quizForm.fill(newQuiz)
    Ok(quizedit(newForm))
  }

    def edit(id:String) = Action.async {implicit request =>
      BSONObjectID.parse(id).map(bsonId =>
        quizRepo
          .findOne(bsonId)
          .map(optobj => optobj match {
            case Some(obj) =>
              val filledForm = QuizController.quizForm.fill(obj)
              Ok(quizedit(filledForm))
            case None =>
              BadRequest("Could not find the object with the id given")
          }
      )
      ).getOrElse(Future.successful(BadRequest("Could Not Parse BSONId")))
    }

  def submit() = Action.async { implicit request =>
    QuizController.quizForm
      .bindFromRequest()
      .fold(
        withErrors => {
          println(s"Form Errors: $withErrors")
          Future.successful(BadRequest("Failed"))
        } ,
        ok =>
          quizRepo
            .create(ok)
            .map(_ => Redirect(routes.QuizController.list()))
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
      println("Form apply options: " + o)
      Quiz(q, o.toList, c, BSONObjectID.parse(id).getOrElse(BSONObjectID.generate()))
    })(
      q => Option((q.question, q.options, q.correctAnswer, q._id.toString()))
    )
  )
}
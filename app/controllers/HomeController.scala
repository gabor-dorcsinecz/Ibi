package controllers

import model._
import model.PermissionLevel._

import javax.inject._
import play.api._
import play.api.data.Form
import play.api.mvc._
import play.modules.reactivemongo._

import scala.concurrent.{ExecutionContext, Future}
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.libs.json._
import reactivemongo.api.bson.BSONObjectID
import reactivemongo.api.bson.collection.BSONCollection
import services.UserRepo
import play.api.data.Forms._
import play.api.data._
import play.api.http.FileMimeTypes
import play.api.i18n.{Langs, MessagesApi}


@Singleton
class HomeController @Inject()(
                                val userRepo: UserRepo,
                                components: ControllerComponents
                              )(
                                implicit val executionContext: ExecutionContext
                              ) extends AbstractController(components) {

  def initialize() = Action.async { implicit request =>
    for {
      _ <- userRepo.create(User("Admin", "1234", PermissionLevel.Edit))
      _ <- userRepo.create(User("Viewer","1234", PermissionLevel.View))
      _ <- userRepo.create(User("Restricted", "1234",PermissionLevel.Restricted))
    } yield Redirect(routes.HomeController.index())
  }

  def index() = Action.async { implicit request =>
    userRepo.findAll().map(users => Ok(views.html.index(users)))
  }

  def login(message:String) = Action {
    Ok(views.html.login(message))
  }

  def loginSubmit() = Action.async {implicit request =>
    HomeController.loginForm
      .bindFromRequest()
      .fold(
        formWithError =>
          Future.successful(Redirect(routes.HomeController.login("Login Failed"))),
        formOk => {
          userRepo.findUser(formOk).map{
            case Some(user) =>
              Redirect(routes.QuizController.list()).withSession(HomeController.sessionId -> user._id.stringify)
            case None =>
              Redirect(routes.HomeController.login("Login Failed"))
          }
        }
      )
  }

}

object HomeController {
  val loginForm:Form[User] = Form(
    mapping(
      "username" -> text,
      "password" -> text
    )((u,p) => User(u,p,Restricted))(u => Some((u.username,u.password)))
  )

  val sessionId = "userSession"
//  def isLoggedIn(request:Request):Call = {
//    request.session.get(sessionId)
//  }
}


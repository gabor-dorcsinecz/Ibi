package controllers

import model._
import model.PermissionLevel._

import javax.inject._
import play.api._
import play.api.mvc._
import play.modules.reactivemongo._

import scala.concurrent.{ExecutionContext, Future}
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.libs.json._
import reactivemongo.api.bson.BSONObjectID
import reactivemongo.api.bson.collection.BSONCollection
import services.UserRepo

@Singleton
class HomeController @Inject()(
                                val userRepo: UserRepo,
                                components: ControllerComponents
                              )(
                                implicit val executionContext: ExecutionContext
                              ) extends AbstractController(components) {

  def initialize() = Action.async { implicit request =>
    for {
      _ <- userRepo.create(User("Admin", PermissionLevel.Edit))
      _ <- userRepo.create(User("Game Controller", PermissionLevel.View))
      _ <- userRepo.create(User("Quiz Participant", PermissionLevel.Restricted))
    } yield Redirect(routes.HomeController.index())
  }

  def index() = Action.async { implicit request =>
    userRepo.findAll().map(users => Ok(views.html.index(users)))
  }

  def login() = Action {
    Ok(views.html.login())
  }

}
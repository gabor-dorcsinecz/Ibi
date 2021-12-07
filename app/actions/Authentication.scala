package actions

import controllers.{HomeController, routes}
import model.User
import play.api.http.FileMimeTypes
import play.api.i18n.{Langs, MessagesApi}
import play.api.mvc.{AbstractController, ActionBuilder, ActionBuilderImpl, ActionRefiner, AnyContent, BodyParser, BodyParsers, ControllerComponents, DefaultActionBuilder, MessagesControllerComponents, PlayBodyParsers, Request, Result, Results, WrappedRequest}
import reactivemongo.api.bson.BSONObjectID
import services.UserRepo

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AuthenticatedRequest[A](val user: User, request: Request[A]) extends WrappedRequest[A](request)

class AuthenticatedActionBuilder @Inject()(parser: BodyParsers.Default, userRepo:UserRepo)(implicit ec: ExecutionContext, cc: MessagesControllerComponents)
  extends ActionBuilder[AuthenticatedRequest,AnyContent] with ActionRefiner[Request, AuthenticatedRequest]  {

  override protected def executionContext = ec
  override def parser = cc.parsers.defaultBodyParser

//  extends ActionBuilderImpl(parser) {
  override protected def refine[A](request: Request[A]): Future[Either[Result, AuthenticatedRequest[A]]] = {

  //override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    val objId = (for {
      sessionId <- request.session.get(HomeController.sessionId)
      bsonId <- BSONObjectID.parse(sessionId).toOption
    } yield bsonId)

    objId match {
      case Some(id) =>
        userRepo.findOne(id).map{
          case Some(user) => Right(new AuthenticatedRequest[A](user, request))
          case None => Left(Results.Redirect(routes.HomeController.login()))
        }
      case _ =>
        Future.successful(Left(Results.Redirect(routes.HomeController.login())))
    }
  }
}

case class SecuredControllerComponents @Inject()(authenticatedActionBuilder: AuthenticatedActionBuilder,
                                                 actionBuilder: DefaultActionBuilder,
                                                 parsers: PlayBodyParsers,
                                                 messagesApi: MessagesApi,
                                                 langs: Langs,
                                                 fileMimeTypes: FileMimeTypes,
                                                 executionContext: scala.concurrent.ExecutionContext
                                                ) extends ControllerComponents

class SecuredController @Inject()(scc: SecuredControllerComponents) extends AbstractController(scc) {
  def AuthenticatedAction: AuthenticatedActionBuilder = scc.authenticatedActionBuilder
}


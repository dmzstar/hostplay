package controllers

import javax.inject._
import jtools.AntPathMatcher
import play.api.http.{DefaultHttpErrorHandler, DefaultHttpRequestHandler, HttpConfiguration, HttpErrorHandler, HttpFilters}
import play.api._
import play.api.i18n.{I18nSupport, MessagesProvider}
import play.api.mvc._
import play.api.mvc.Results.{NotFound, _}
import play.api.routing.Router
import play.core.WebCommands

import scala.concurrent._

class ForbiddenException extends RuntimeException

@Singleton
class ErrorHandler @Inject()(
                              env: Environment,
                              config: Configuration,
                              sourceMapper: OptionalSourceMapper,
                              router: Provider[Router],
                            ) extends DefaultHttpErrorHandler(env, config, sourceMapper, router) {


  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> onServerError")
    super.onServerError(request,exception)
    /**
    if(exception.isInstanceOf[ForbiddenException]) {
      Future.successful {
        Results.Ok(views.html.secs.login())
      }
    }else{
      super.onServerError(request,exception)
    }*/
  }

  override protected def onDevServerError(request: RequestHeader, exception: UsefulException): Future[Result] = {
    println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> onDevServerError")
    super.onDevServerError(request, exception)
  }

  override def onProdServerError(request: RequestHeader, exception: UsefulException) = {
    Future.successful(
      InternalServerError("A server error occurred: " + exception.getMessage)
    )
  }

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> onClientError")
    super.onClientError(request, statusCode, message)
  }

  override protected def onOtherClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> onOtherClientError")
    super.onOtherClientError(request, statusCode, message)
  }

  override protected def onBadRequest(request: RequestHeader, message: String): Future[Result] = {
    println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> onBadRequest")
    Future.successful(
      BadRequest("Got a bad request.")
    )
  }

  override def onForbidden(request: RequestHeader, message: String) = {
    println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> onForbidden")
    Future.successful(
      Forbidden("You're not allowed to access this resource.")
    )
  }

  override protected def onNotFound(request: RequestHeader, message: String): Future[Result] = Future.successful(
    NotFound(views.html.errors._404())
  )

}


@Singleton
class Errors @Inject()(cc: ControllerComponents) extends AbstractController(cc){

  def _404 = Action { implicit request =>
    NotFound(views.html.errors._404())
  }

}

@Singleton
class SecurityComponents2 @Inject()(config:Configuration){

  import jtools.AntPathMatcher

  val securityConfig = config.get[Configuration]("hostplay.security")

  val matcher = new AntPathMatcher.Builder().build


  import play.api.routing.Router.Routes
  import play.api.routing.SimpleRouter
  import play.api.routing.sird._

  class SecurityRouter @Inject() (controller: ArticleCategories) extends SimpleRouter {
    override def routes: Routes = {
      case GET(p"/") => controller.list
      case GET(p"/create") => controller.create
      case GET(p"/edit/$id") => controller.edit(id.toLong)
      case DELETE(p"/edit/$id") => controller.delete(id.toLong)
    }
  }


  case class MatchResult(name:String){

  }

  def uriCheck(uri:String) = {

    val list = securityConfig.get[Map[String,Seq[String]]]("auth.urls")


    val result = for(expr <- list;if expr._2.exists( matcher.isMatch(_,uri))) yield MatchResult(expr._1)

    result

    /**
      println(s">>>>>>>>>>>>>>>>>> Permit - expr : $expr, uri : $uri ")
      val matched = matcher.isMatch(expr,uri)
      println(s">>>>>>>>>>>>>>>>>> Permit - matched : $matched")
      matched
    */
  }

  def permitCheck(uri:String):Boolean = {
    val list = securityConfig.get[Seq[String]]("auth.urls.permit")
    list.exists{ expr =>
      println(s">>>>>>>>>>>>>>>>>> Permit - expr : $expr, uri : $uri ")
      val matched = matcher.isMatch(expr,uri)
      println(s">>>>>>>>>>>>>>>>>> Permit - matched : $matched")
      matched
    }
  }

  def uriAuthedCheck(uri:String):Boolean = {
    val list = securityConfig.get[Seq[String]]("auth.urls.authed")
    list.exists{ expr =>
      println(s">>>>>>>>>>>>>>>>>> AuthCheck - expr : $expr, uri : $uri ")
      val matched = matcher.isMatch(expr,uri)
      println(s">>>>>>>>>>>>>>>>>> AuthCheck - matched : $matched")
      matched
    }
  }

  def loginUriCheck(uri:String):Boolean = {
    true
  }

}


@Singleton
class DefaultLoginController @Inject()(cc: ControllerComponents) extends AbstractController(cc) with I18nSupport{

  def onForbidden = Action { implicit request =>
    Redirect(routes.Users.loginPage())
  }

  def login = Action{ implicit request =>
      Ok("Login")
  }

}




class MyRequestHandler @Inject()(
                                           webCommands: WebCommands,
                                           optionalDevContext: OptionalDevContext,
                                           router: Router,
                                           errorHandler: HttpErrorHandler,
                                           configuration: HttpConfiguration,
                                           filters: HttpFilters,
                                           action:DefaultActionBuilder
                                         ) extends DefaultHttpRequestHandler(
  webCommands,
  optionalDevContext,
  router: Router,
  errorHandler,
  configuration,
  filters
) {

  override def handlerForRequest(request: RequestHeader): (RequestHeader, Handler) = {
      super.handlerForRequest(request)
    }

}





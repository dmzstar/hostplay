package controllers

import javax.inject._
import play.api.http.{DefaultHttpErrorHandler, DefaultHttpRequestHandler, HttpConfiguration, HttpErrorHandler, HttpFilters}
import play.api._
import play.api.mvc._
import play.api.mvc.Results.{NotFound, _}
import play.api.routing.Router
import play.core.WebCommands

import scala.concurrent._

@Singleton
class ErrorHandler @Inject()(
                              env: Environment,
                              config: Configuration,
                              sourceMapper: OptionalSourceMapper,
                              router: Provider[Router]
                            ) extends DefaultHttpErrorHandler(env, config, sourceMapper, router) {

  override def onProdServerError(request: RequestHeader, exception: UsefulException) = {
    Future.successful(
      InternalServerError("A server error occurred: " + exception.getMessage)
    )
  }

  override def onForbidden(request: RequestHeader, message: String) = {
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
    request.uri match {
      case s if(s.startsWith("/admin") || s.startsWith("/member")) => {
        request.session.get("loginUser").map(u => super.handlerForRequest(request)).getOrElse(request, action(Results.Unauthorized))
      }
      case _                 => super.handlerForRequest(request)
    }
  }

}

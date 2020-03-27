package controllers

import javax.inject._
import play.api.mvc._


/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  import views.html.blogs.{articles => Views}

  def index() = Action { implicit request =>
    Ok(Views.list())
  }

  def index2() = Action { implicit request =>
    Ok(Views.list())
  }

}
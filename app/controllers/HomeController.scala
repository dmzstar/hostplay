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






import scala.concurrent.Future
import javax.inject._
import play.api.inject.ApplicationLifecycle

// This creates an `ApplicationStart` object once at start-up and registers hook for shut-down.
@Singleton
class ApplicationStart @Inject()(lifecycle: ApplicationLifecycle) {

  println("==================>>> onApplicationStart")

  // Shut-down hook
  lifecycle.addStopHook { () =>
    Future.successful(())
  }
  //...
}


import com.google.inject.AbstractModule

class StartModule extends AbstractModule {
  override def configure() = {
    bind(classOf[ApplicationStart]).asEagerSingleton()
  }
}

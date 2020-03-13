package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}

@Singleton
class PageController @Inject()(cc: ControllerComponents) extends AbstractController(cc) with play.api.i18n.I18nSupport {

  def loadPage(url:String) = Action{
    val page:Option[String] = Option.empty
    page.map( p => {
      Ok("Load by database")
    }).getOrElse(
      NotFound
    )

  }

}


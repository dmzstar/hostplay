package controllers.admin

import controllers.ArticleCategories
import javax.inject.Inject
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

class AdminRouter @Inject() (index:IndexController,articleCategories: ArticleCategories) extends SimpleRouter {
  override def routes: Routes = {
    case GET(p"/") => index.index()
    case GET(p"/article-categories") => articleCategories.list
    case GET(p"/article-categories/create") => articleCategories.create
    case GET(p"/article-categories/edit/$id") => articleCategories.edit(id.toLong)
    case DELETE(p"/delete/$id") => articleCategories.delete(id.toLong)
  }
}

package controllers.admin

import controllers.ArticleCategories
import controllers.admin.articles.ArticleController
import controllers.admin.users.UsersController
import javax.inject.Inject
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

class AdminRouter @Inject() (index:IndexController,
                             articles:ArticleController,
                             articleCategories: ArticleCategories,
            users: UsersController) extends SimpleRouter {

  override def routes: Routes = {

    case GET(p"/articles/create") => articles.create

    case GET(p"/") => index.index()
    case GET(p"/article-categories") => articleCategories.list
    case GET(p"/article-categories/create") => articleCategories.create
    case GET(p"/article-categories/edit/$id") => articleCategories.edit(id.toLong)
    case DELETE(p"/delete/$id") => articleCategories.delete(id.toLong)

    case GET(p"/users") => users.index

  }
  
}

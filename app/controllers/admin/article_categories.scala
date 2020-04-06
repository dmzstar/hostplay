package controllers.admin

import controllers.FlashingCache
import controllers.secs.CheckLoginAction
import javax.inject.{Inject, Singleton}
import models.blogs.ArticleCategory
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import hostplay.mvc._
import play.core.routing.ReverseRouteContext


@Singleton
class ArticleCategoriesController @Inject()(implicit flashingCache:FlashingCache,
                         checkLoginAction: CheckLoginAction,
                         cc: ControllerComponents) extends AbstractController(cc) with play.api.i18n.I18nSupport with JavaConvertersSupport {

  import controllers.{routes => routes}
  import controllers.admin.{routes => Routes}
  import views.html.{admin => Views}
  import views.html.{blogs => BlogViews}

  object FormModel{

    case class Data(parent:Option[String],code:String,name:String)

    def bind(implicit request:Request[_]) = form.bindFromRequest

    def form = Form(
      mapping(
              "parent" -> optional(text),
        "code" -> nonEmptyText,
        "name" -> nonEmptyText,
      )(Data.apply)(Data.unapply))

  }

  def list = Action  { implicit request =>
    Ok(BlogViews.article_categories.list())
  }

  def create = Action  { implicit request =>
    val parents = ArticleCategory.all
    val form = FormModel.form

    val fdata = flashingCache.get[Map[String,String]]("formMapping").getOrElse(Map.empty)
    println("======================= fdata " + fdata)
    form.bind(fdata)

    val parentOptions = parents
    val options = parents.map[(String,String)]( a => (a.code -> a.name)).toSeq
    Ok(Views.article_categories.create(form,parents,options))
  }

  def edit(id:Long) = Action  { implicit request =>
    val parents = ArticleCategory.all
    val form = FormModel.form
    val parentOptions = parents
    val options = parents.map[(String,String)]( a => (a.code -> a.name)).toSeq
    Ok(Views.article_categories.create(form,parents,options))
  }

  def save = Action  { implicit request =>

    FormModel.bind.fold(
      errorForm => {
        val flashingMap = errorForm.data + ("error" -> "验证错误！")
        flashingCache.set("formMapping",errorForm.data)
        Redirect(Routes.ArticleCategoriesController.create()).flashing(flashingMap.toArray: _*)
      },
      data => {

        val entity = new ArticleCategory()
        entity.name = data.name
        //data.code.map(entity.code = _)
        entity.code = data.code
        entity.save()
        Redirect(Routes.ArticleCategoriesController.create()).flashing("success" -> "创建成功！")

      }
    )

  }

  def update(id:Long) = Action  { implicit request =>

    var i = 0

    FormModel.bind.fold(
      errorForm => Redirect(routes.ArticleCategories.edit(id)).flashing("error" -> "验证错误！"),
      data => {

        ArticleCategory.findById(id).map{ entity =>
          entity.name = data.name
          //data.code.map(entity.code = _)
          entity.code = data.code
          entity.save()
          Redirect(routes.ArticleCategories.edit(entity.id)).flashing("success" -> "编辑成功！")
        }.getOrElse(NotFound)


      }
    )

  }

  def delete(id:java.lang.Long) = Action{ implicit request =>
    ArticleCategory.query.where().idEq(id).delete()
    Redirect(routes.ArticleCategories.list())
  }

}

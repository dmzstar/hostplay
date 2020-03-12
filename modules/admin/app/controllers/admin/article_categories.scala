package controllers.admin


import javax.inject.{Inject, Singleton}
import models.blogs.ArticleCategory
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{AbstractController, ControllerComponents, Request}

@Singleton
class ArticleCategories @Inject()(
                         cc: ControllerComponents) extends AbstractController(cc) with play.api.i18n.I18nSupport with JavaConvertersSupport {

  object FormModel{

    case class Data(name:String,code:Option[String])

    def bind(implicit request:Request[_]) = form.bindFromRequest

    def form = Form(
      mapping(
        "name" -> nonEmptyText,
        "code" -> optional(text)
      )(Data.apply)(Data.unapply))

  }

  def list = Action  { implicit request =>
    Ok(views.html.article_categories.list())
  }

  def create = Action  { implicit request =>
    Ok(views.html.article_categories.create())
  }

  def edit(id:Long) = Action  { implicit request =>
    Ok(views.html.article_categories.edit(ArticleCategory.findById(id).get))
  }

  def save = Action  { implicit request =>

    FormModel.bind.fold(
      errorForm => Redirect(routes.admin.ArticleCategories.create()).flashing("error" -> "验证错误！"),
      data => {

        val entity = new ArticleCategory()
        entity.name = data.name
        data.code.map(entity.code = _)
        entity.save()
        Redirect(routes.ArticleCategories.create()).flashing("success" -> "创建成功！")

      }
    )

  }

  def update(id:Long) = Action  { implicit request =>

    FormModel.bind.fold(
      errorForm => Redirect(routes.ArticleCategories.edit(id)).flashing("error" -> "验证错误！"),
      data => {

        ArticleCategory.findById(id).map{ entity =>
          entity.name = data.name
          data.code.map(entity.code = _)
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

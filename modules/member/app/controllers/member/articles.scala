package controllers.member

import java.nio.file.Paths
import java.util.Date

import javax.inject.{Inject, Singleton}
import models.blogs.{Article, ArticleCategory, ArticleComment, ArticleDraft}
import models.users.User
import common.uploads.UploadComponents
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText, optional, text}
import play.api.libs.json.Json
import play.api.mvc._
import common.utils.ActionMode



trait JavaConvertersSupport{

  import collection.JavaConverters._
  implicit def jlistToScalaList[T](list:java.util.List[T]):List[T] = list.asScala.toList

  class DataList[T](val list:List[T])
  object DataList{
    implicit def to[T](list:java.util.List[T]) = new DataList(list)
  }

}

object Articles{

  case class ViewModel(articles:List[Article],category:ArticleCategory=null)

}

@Singleton
class Articles @Inject()(uploadComponents: UploadComponents,
                         cc: ControllerComponents) extends AbstractController(cc) with play.api.i18n.I18nSupport with JavaConvertersSupport {


  case class DraftData(articleId:Option[String],category:Option[String],title:Option[String],details:String)

  case class ArticleData(code:Option[String],
                         url:Option[String],
                         category:Option[String],
                         title:String,
                         remark:Option[String],
                         details:String){

    def bindEntity:Article = {
      bindEntity(new Article())
    }

    def bindEntity(article:Article):Article = {
      article.title = title
      remark.map(article.remark = _)
      code.map(article.code = _)
      url.map(article.url = _)
      category.map( c => article.category  = ArticleCategory.query.where().idEq(c).findOne())
      article.details = details
      article
    }

  }

  def articleForm = Form(
    mapping(
      "code" -> optional(text),
          "url" -> optional(text),
      "category" -> optional(text),
      "title"  -> nonEmptyText(maxLength = 200),
            "remark" -> optional(text),
      "details" -> nonEmptyText
    )(ArticleData.apply)(ArticleData.unapply))

  def draftForm = Form(
    mapping(
      "articleId" -> optional(text),
            "category" -> optional(text),
            "title" -> optional(text),
      "details" -> text
    )(DraftData.apply)(DraftData.unapply))


  object Conversation{

    def cleanLast(result:Result) = result.discardingCookies(DiscardingCookie("articleId"))
    def setCurrent(id:String,result:Result) = result.withCookies(Cookie("articleId",id))

  }

  def list = Action { implicit request =>
    val articles:List[Article] = Article.allPageList().getList
    Ok("")
  }


  /**
  def list = Action { implicit request =>
    val articles:List[Article] = Article.allPageList().getList
    Ok(articles.list())
  }

  def listByCategory(categoryId:Long) = Action { implicit request =>
    ArticleCategory.findById(categoryId).map{ category =>
      val articles:List[Article] = Article.listPublishByCategory(categoryId).getList
      val viewModel = Articles.ViewModel(articles,category)
      Ok(views.html.member.articles.list_by_category(viewModel))
    }.getOrElse(NotFound)

  }

  def create()(implicit form:Form[ArticleData] = articleForm) = Action { implicit request =>

    val result = Ok(views.html.member.articles.create(form,ArticleCategory.query.findList()))
    Conversation.cleanLast(result)

  }

  private def draftToArticle(draft:ArticleDraft,article:Article=new Article()):Article = {
    article.title = draft.title
    article.details = draft.details
    article
  }

  def edit(q:String) = Action { implicit request =>

    Option.apply(Article.findOne(q)) match {
      case Some(article) => {

        article.writeLock()
        article.lastDraft().map{ draft =>

          val _article = draft.copyNewArticle
          val result = Ok(views.html.member.articles.editormd(_article,ActionMode.edit))
          Conversation.setCurrent(article.id.toString,result)

        }.getOrElse{

          article.saveNewDraft
          val result = Ok(views.html.member.articles.editormd(article,ActionMode.edit))
          Conversation.setCurrent(article.id.toString,result)

        }


      }
      case None => NotFound
    }

  }


  def show(q:String) = Action { implicit request =>

    Article.findPublish(q) match {
      case Some(article)  => {
        Ok(views.html.member.articles.show(article))
      }
      case None => NotFound
    }

  }

  def delete(id:java.lang.Long) = Action { implicit request =>

    Option(Article.query.where().idEq(id).findOne).map( article => {
      ArticleDraft.findByArticle(article).map( _.delete())
      article.delete()
    })
    Redirect(routes.Articles.list())

  }

  /**
    * 发布文章
    * @return
    */
  def publishNew() = Action { implicit request =>

    println("========================publishNew")

    articleForm.bindFromRequest.fold(
      errorsForm => {

        render {
          case Accepts.Json() => {
            Ok(Json.obj("success" -> 0))
          }
        }
      },
      model => {
        val entity = model.bindEntity
        entity.createdBy = loginUser.get
        entity.publishNew
        render {
          case Accepts.Json() => {
            val url = routes.Articles.show(entity.id.toString).url
            Ok(Json.obj("success" -> 1,"url" -> url))
          }
        }
      }
    )

  }

  /**
    * 发布文章更新
    * @return
    */
  def publishUpdate() = Action { implicit request =>

    println("========================publishUpdate")

    articleForm.bindFromRequest.fold(
      errorsForm => {

        render {
          case Accepts.Json() => {
            Ok(Json.obj("error" -> 1))
          }
        }
      },
      data => {
        findArticle.map{ article => {


          data.bindEntity(article)
          article.updatedBy = loginUser.get
          article.publishUpdate


          render {
            case Accepts.Json() => {
              val url = routes.Articles.show(article.id.toString).url
              Ok(Json.obj("success" -> 1,"url" -> url))
            }
          }

        }}.getOrElse(NotFound)


      }
    )
  }

  def draftSave() = Action { implicit request =>

    println("========================saveDraft")


    draftForm.bindFromRequest.fold(
      errorsForm => {
        Ok("error")
      },
      data => {

        findArticle.map( article => {

          //草稿对应的文章
          ArticleDraft.findByArticle(article).map{

            draft => {
              draft.title = data.title.getOrElse("")
              draft.details = data.details
              draft.save()
              Ok("draftUpdate")
            }

          }.getOrElse(
            NotFound
          )


        }).getOrElse{

          newDraftAndArticle(data).map{
            draft => Ok(draft.details).withCookies(Cookie("articleId",draft.article.id.toString))
          }.getOrElse(NotFound)

        }


      }
    )

  }

  private def newDraftAndArticle(data: DraftData):Option[ArticleDraft] = {
    //新建文章
    val article = new Article
    article.title = if(data.details.length < 5) data.details else data.details.substring(0,4)
    article.details = data.details
    article.save()

    val  draft = new ArticleDraft

    draft.details = data.details
    draft.article = article
    draft.save()
    Option(draft)
  }

  def findArticle(implicit request:RequestHeader):Option[Article] = {
    request.cookies.get("articleId").map(cookie => {
      Option.apply(Article.findOne(cookie.value))
    }).getOrElse(None)
  }

  def editormd = Action { implicit request =>
    Ok(views.html.member.articles.editormd()).discardingCookies(DiscardingCookie("articleId"))
  }

  def editormdSave() = Action { implicit request =>

    articleForm.bindFromRequest.fold(
      errorsForm => {

        render {
          case Accepts.Json() => {
            Ok(Json.obj("success" -> 0))
          }
        }
        //Redirect(routes.Articles.create()).flashing("errors" -> errorsView(errorsForm,"article").body)
      },
      model => {
        val entity = model.bindEntity
        entity.createdBy = loginUser.get
        entity.createdOn = new Date()
        entity.save()
        render {
          case Accepts.Json() => {
            val url = routes.Articles.show(entity.id.toString).url
            Ok(Json.obj("success" -> 1,"url" -> url))
          }
        }

      }
    )

  }

  private def loginUser(implicit request:RequestHeader) = {
    request.session.get("loginUser").map(User.findOne(_)).get

  }


  def editormdUpload = Action(parse.multipartFormData) { request =>
    request.body
      .file("editormd-image-file")
      .map { picture =>

        val filename    = Paths.get(picture.filename).getFileName
        val fileSize    = picture.fileSize
        val contentType = picture.contentType

        val rndName = System.nanoTime() + "" + filename

        picture.ref.copyTo(Paths.get(uploadComponents.baseDir + "/" + rndName),false)

        Ok(Json.obj("success" -> 1,"message" -> "上传成功","url" -> s"/uploads/$rndName"))

      }.getOrElse {
        Ok(Json.obj("error" -> 1))
      }
  }
    */

}

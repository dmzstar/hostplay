package models.blogs

import java.util.{Date, Optional}

import javax.persistence._
import models.users.User
import models.{ModelTrait, QO, QueryObject, EntityObject}

trait DetailsTrait

@Entity
class Article extends ModelTrait{

  @Column(unique = true,nullable = true)
  var code:String = _
  @Column(unique = true)
  var url:String = _ //相对
  var title:String = _
  @Lob
  var remark:String = _
  @ManyToOne
  @JoinColumn(nullable = true)
  var category:ArticleCategory = _
  @Lob
  var details:String = _
  @ManyToOne
  var createdBy:User = _
  @ManyToOne
  var updatedBy:User = _
  @Temporal(TemporalType.TIMESTAMP)
  var createdOn:Date = _
  @Temporal(TemporalType.TIMESTAMP)
  var updatedOn:Date = _
  @OneToMany(mappedBy = "article",cascade = Array(CascadeType.ALL))
  var comments:JList[ArticleComment] = _
  var visited:Int = 0
  var status:Int = 0 //0 -> init 1 -> published
  var writeFlag:Boolean = false
  var single = true


  def writeLock() = {
    writeFlag = true
    save()
  }

  def publishNew = {

      if(status != 0) throw new RuntimeException
      createdOn = new Date()
      status = 1
      save()

  }

  def publishUpdate = {

      if(!writeFlag) throw new RuntimeException("没有正确锁定")

      lastDraft().map( draft => {
        updatedOn = new Date()
        status = 1
        save()
        this
      }).orElse(throw new RuntimeException("状态不一致"))

  }

  def lastDraft() = {
    ArticleDraft.findByArticle(this)
  }

  def copyFromDraft = {
    lastDraft().map { draft =>
      val article = new Article
      article.title = draft.title
      article.details = draft.details
      article.category = this.category
      article.remark = this.remark
      article
    }
  }

  def saveNewDraft = {

    val draft = new ArticleDraft
    draft.title = this.title
    draft.remark = this.remark
    draft.details = this.details
    draft.article = this
    draft.save()

  }

  def listComments = {
      ArticleComment.query.where().eq("article",this).findList()
  }

}

object Article extends EntityObject[Article] {

  val pat = "\\d+".r

  def findPublish(q:String):Option[Article] = {
    q match {
      case pat() => query.where().idEq(q).eq("status",1)
        .findOneOrEmpty()
      case _  if(q!= null) => query.where().eq("code",q).and().eq("status",1).findOneOrEmpty()
      case _ => null
    }
  }

  def findOne(q:String):Article = {
    q match {
      case pat() => query.where().idEq(q).findOne()
      case _  if(q!= null) => query.where().eq("code",q).findOne()
      case _ => null
    }
  }

  def listPublish(first:Int=0,max:Int=100) = {
    query.where().eq("status",1)
      .orderBy("id desc")
      .setFirstRow(first).setMaxRows(max).findPagedList()
  }

  def listPublishByCategory(categoryId:Long,first:Int=0,max:Int=100) = {
    query.where().eq("status",1)
      .eq("category.id",categoryId)
      .orderBy("id desc")
      .setFirstRow(first).setMaxRows(max).findPagedList()
  }

  def allPageList(first:Int=0,max:Int=10) = {
    query.setFirstRow(first).setMaxRows(max).findPagedList()
  }

}



object ArticleDraft extends EntityObject[ArticleDraft] {

  def findByArticle(article:Article):Option[ArticleDraft] = query.where().eq("article",article).findOneOrEmpty()

}

@Entity
class ArticleDraft extends ModelTrait{

  @OneToOne
  @JoinColumn(nullable = false)
  var article:Article = _
  @ManyToOne
  var category:ArticleCategory = _
  var title:String = _
  @Lob
  var remark:String = _
  @Lob
  var details:String = _

  def copyNewArticle = {

    val article = new Article
    article.title = this.title
    article.details = this.details
    article.category = this.article.category
    article.remark = remark
    article

  }

  def updateArticle = {

    article.title = this.title
    article.details = this.details
    article.category = this.category
    article.remark = this.remark
    article

  }

}

@Entity
class ArticleCategory extends ModelTrait {

  @Column(unique = true)
  var code:String = _
  var name:String = _
  var remark:String = _
  @ManyToOne
  var createdBy:User = _
  var createdOn:String = _

}

object ArticleCategory extends EntityObject[ArticleCategory]{

  def all = query.findList()

  def findById(id:Long):Option[ArticleCategory] = query.where().idEq(id).findOneOrEmpty()

  def saveNew(name:String) = {
      val a = new ArticleCategory
      a.name = name
      a.save()
  }

}

@Entity
class ArticleComment extends ModelTrait{

  @Column(unique = true,nullable = true)
  var code:String = _
  @ManyToOne
  //@JoinColumn(nullable = false)
  var article:Article = _
  @ManyToOne(cascade = Array(CascadeType.ALL))
  @JoinColumn(name="parent_id")
  var parent:ArticleComment = _
  @OneToMany(cascade=Array(CascadeType.ALL),fetch=FetchType.LAZY)
  @JoinColumn(name="parent_id")
  var childs:java.util.Set[ArticleComment] = _

  @Lob
  var details:String = _
  var createdBy:String = _
  var createdOn:java.util.Date = _

  def pubilsh = {
    createdOn = new Date
    save()
  }

  def addReplay(replay:ArticleComment) = {
    replay.parent = this
    this.childs.add(replay)
    save()
    replay.save();
  }

  def replays(first:Int=0,max:Int=100) = {
    ArticleComment.query.where().eq("parent",this).setFirstRow(first).setMaxRows(max).findPagedList()
  }

}

object ArticleComment{
  def query = QO[ArticleComment]().create
}

@Entity
class Blog extends ModelTrait {

  @Column(unique = true,nullable = true)
  var code:String = _
  var title:String = _
  var remark:String = _
  @ManyToOne
  var category:BlogCategory = _
  @Lob
  var details:String = _
  var createdBy:String = _
  var createdOn:String = _

  def url = if(code == "") id.toString else code

}


object Blog {

  val find = QueryObject[Blog].finder

  var list = List[Blog]()

  def findOne(q:String):Blog = {
      val pat = "\\d".r
      q match {
        case pat() => find.byId(q.toLong)
        case  _ => findOneByCode(q)
      }
  }

  def findOneByCode(code:String) = {
    find.query().where().eq("code", code).findOne()
  }


  def initData = {

    for(i <- 0 to 10){

      val blog = new Blog
      blog.code = s"code$i"
      blog.title = s"Post title $i"
      blog.remark = s"Blog remark $i"
      blog.createdBy = "dmstar"
      blog.createdOn = "January 1, 2017"
      blog.save()

    }
  }

}

@Entity
class BlogCategory extends ModelTrait {

  @Column(unique = true)
  var code:String = _
  var name:String = _
  var remark:String = _
  var createdBy:String = _
  var createdOn:String = _

}



object BlogCategory {

  case class Eq(name:String,value:String)

  implicit def toScalaOpt(blog:BlogCategory) = if(blog == null) None else Option.apply(blog)

  def findBy(name:String,value:String):Option[BlogCategory] = {
    find.query().where().eq(name,value).findOne
  }

  //implicit def toBlog(eq:Eq) = find.query().where().eq(eq.name,eq.value).findOne()

  val find = QueryObject[BlogCategory].finder

  def all = {
      find.all()
  }

  def initData = {
    if(find.query().findCount() == 0) {
      saveNew("spring", "Spring")
      saveNew("jsf", "JSF")
      saveNew("grails", "Grails")
      saveNew("playframework", "Playframeowrk")
    }
  }

  def saveNew(code:String,name:String) = {
    val c = new BlogCategory
    c.code = code
    c.name = name
    c.save()
  }

}

package ipct

import java.util.Date
import models.blogs.Article

object date {

  val format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  implicit class RichDate(val date:Date){
    def pretty:String = date match {
      case null => ""
      case _ => format.format(date)
    }
  }

}


object content{

  implicit class RichContent(val article:Article){
    def formatRemark:String = article.remark match {
      case null | "" => if(article.details.length < 50) article.details else article.details.substring(0,50)
      case _ => article.remark
    }
  }

}

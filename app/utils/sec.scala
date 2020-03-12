package utils.secs

import models.blogs.Article
import play.api.mvc.RequestHeader

object Permission {

  def has(article:Article,permission:String)(implicit request:RequestHeader):Boolean = {

    request.session.get("loginUser").map( user => {

      permission match {

        case "edit" => article.createdBy == user
        case "delete" => article.createdBy == user
        case _ => false

      }

    }).getOrElse(false)


  }

}

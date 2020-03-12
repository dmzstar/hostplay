package utils

import play.api.data.FormError
import play.api.i18n.MessagesProvider


object MsgUtils{

  def /(key:String)(implicit messagesProvider: MessagesProvider) = {
    messagesProvider.messages(key)
  }

  def v(key:String)(implicit messagesProvider: MessagesProvider) = messagesProvider.messages(key)

}


object FormErrorUtils {

  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val formErrorWrites = new Writes[FormError] {
    def writes(error: FormError) = Json.obj(
      "key" -> error.key,
      "message" -> error.message
    )
  }

  implicit val formErrorReads: Reads[Error] = (
    (JsPath \ "key").read[String] and
      (JsPath \ "message").read[String]
    )(Error.apply _)

  def toJson(errors:Seq[FormError]) = Json.toJson(errors).toString()

  def fromJson(value:String) = Json.parse(value).validate[Seq[Error]].get

  case class Error(key:String,message:String)

}

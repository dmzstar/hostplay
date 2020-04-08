package ui

import javax.inject.{Inject, Singleton}
import org.webjars.play.WebJarsUtil
import views.html

object HelperSymbolFix {

  implicit def tupStringToSym(tuple: Tuple2[String,String]) = (Symbol(tuple._1) , tuple._2)
  implicit def tupAnyValToSym(tuple: Tuple2[String,AnyVal]) = (Symbol(tuple._1) , tuple._2)

}


object Boostrap4Helper{

  import views.html.helper.inputText

  import views.html.helper.FieldConstructor
  implicit val myFields = FieldConstructor(html.bv4.formGroup.f)

}



object WebUtil{
  var webjars:WebJarsUtil = _
}

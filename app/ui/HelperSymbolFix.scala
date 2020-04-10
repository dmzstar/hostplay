package ui

import javax.inject.{Inject, Singleton}
import org.webjars.play.WebJarsUtil
import play.api
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

object Boostrap4StyleHelper{


    def styleClass(field:play.api.data.Field,args: (Symbol,Any)*):String = {

      var _class = args.toMap.get(Symbol("class")).map(_.toString).getOrElse("form-control")
      if(field.hasErrors){
        _class += " is-invalid"
        _class
      }else{
        _class
      }

  }

}


object b4components{

  object Alert{

    import play.api.data.FormError
    import play.api.data.Form

    implicit def errorsToAlertMessages(errors: Seq[FormError]):Seq[String] = errors.map( error => error.message )

  }

}



object WebUtil{
  var webjars:WebJarsUtil = _
}

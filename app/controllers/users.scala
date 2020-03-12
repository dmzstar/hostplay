package controllers

import javax.inject.{Inject, Singleton}
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText, optional, text}
import play.api.libs.mailer.{Email, MailerClient}
import play.api.mvc.{AbstractController, ControllerComponents}
import views.html

@Singleton
class Users @Inject()(cc: ControllerComponents,
                      mailClient:MailerClient) extends AbstractController(cc) with play.api.i18n.I18nSupport{

    case class LoginData(username:String,password:String)

    case class SiginData(username:String,password:String,passwordConfirm:String)

    def loginForm = Form(
      mapping(
      "username" -> text,
      "password" -> text
    )(LoginData.apply)(LoginData.unapply))

  def siginForm = Form(
    mapping(
      "username" -> text,
      "password" -> text,
           "passwordConfirm" -> text
    )(SiginData.apply)(SiginData.unapply))


    def login = Action{ implicit request =>
      loginForm.bindFromRequest().fold(
        errorForm => {
          Ok("login fail")
        },
        data => {
          if(data.username == "dmstar" && data.password == "7777722848zdm") {
            Ok("success").withSession("loginUser" -> "dmstar")
          }else{
            Ok("error")
          }
        }
      )
    }

  def loginPage = Action{ implicit request =>
    Ok(views.html.secs.login())
  }

  def signinPage = Action{ implicit request =>
    Ok(views.html.secs.signin())
  }

  def sendSiginValidateMail = Action{ implicit request =>
    val code = "1234"
    val email = Email(
      "Play测试 --- 激活用户",
      "zhouweifx@126.com",
      Seq("zhouweifx@126.com"),
      /**
      // adds attachment
      attachments = Seq(
        AttachmentFile("attachment.pdf", new File("/some/path/attachment.pdf")),
        // adds inline attachment from byte array
        AttachmentData("data.txt", "data".getBytes, "text/plain", Some("Simple data"), Some(EmailAttachment.INLINE)),
        // adds cid attachment
        AttachmentFile("image.jpg", new File("/some/path/image.jpg"), contentId = Some(cid))
      ),
        */
      // sends text, HTML or both...
      bodyText = Some("Play测试 -- 激活用户"),
      bodyHtml = Some(s"""<html><body><p>验证码:$code</p></body></html>""")
    )
    mailClient.send(email)
    Ok("success")
  }

  def sendResetPasswordMail = Action{ implicit request =>
    val code = "1234"
    val email = Email(
      "Play测试 -- 重置密码",
      "zhouweifx@126.com",
      Seq("zhouweifx@126.com"),
      /**
      // adds attachment
      attachments = Seq(
        AttachmentFile("attachment.pdf", new File("/some/path/attachment.pdf")),
        // adds inline attachment from byte array
        AttachmentData("data.txt", "data".getBytes, "text/plain", Some("Simple data"), Some(EmailAttachment.INLINE)),
        // adds cid attachment
        AttachmentFile("image.jpg", new File("/some/path/image.jpg"), contentId = Some(cid))
      ),
        */
      // sends text, HTML or both...
      bodyText = Some("Play测试 -- 重置密码"),
      bodyHtml = Some(s"""<html><body><p>验证码:$code</p></body></html>""")
    )
    mailClient.send(email)
    Ok("success")
  }

  def passwordForgotPage = Action{ implicit request =>
    Ok(views.html.secs.password_forget())
  }

  def passwordResetPage = Action{ implicit request =>
    Ok(views.html.secs.password_reset())
  }

  def logout = Action{ implicit request =>
    Ok("success").withNewSession
  }

}

@Singleton
class UserPasswordController @Inject()(cc: ControllerComponents,
                                       mailClient:MailerClient) extends AbstractController(cc) with play.api.i18n.I18nSupport{



}

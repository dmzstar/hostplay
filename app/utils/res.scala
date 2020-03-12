package utils.res

import java.util.Date

import play.twirl.api.Html

object Jquery {

  def core = Html("""<script src="/assets/blog/vendor/jquery/jquery.min.js"></script>""")

}

object DateUtils {

  val format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  def pretty(date: Date):String = {
    format.format(date)
  }

}

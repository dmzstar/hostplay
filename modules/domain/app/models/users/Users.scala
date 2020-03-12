package models.users

import io.ebean.Query
import javax.persistence.{Column, Entity, Table}
import models.blogs.Article
import models.{EntityObject, ModelTrait, QO}

@Entity
@Table(name="users")
class User extends ModelTrait{

  @Column(unique = true)
  var username:String = _
  var password:String = _
  @Column(unique = true)
  var email:String = _
  @Column(unique = true)
  var nickname:String = _

}

object User extends EntityObject[User]{

  //override def query = QO[User].create

  def findOne(q:String):Option[User] = {
      query.where().eq("username",q).or().eq("email",q).findOneOrEmpty()
  }

  def findById(id:Long):Option[User] = {
    query.where().idEq(id).findOneOrEmpty()
  }

}


@Entity
@Table(name="roles")
class Role extends ModelTrait{

  @Column(unique = true)
  var name:String = _
  var code:String = _

}

package models.content

import javax.persistence.{Entity, OneToOne, Table}
import models.ModelTrait

@Entity
@Table
class PageTemplate extends ModelTrait{
   var name:String = _
}

@Entity
@Table
class Page extends ModelTrait {

  @OneToOne
  var template:PageTemplate = _

}

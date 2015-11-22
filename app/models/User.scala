package models

import utils.silhouette.IdentitySilhouette
import com.mohiva.play.silhouette.impl.util.BCryptPasswordHasher
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class User(
    id: Option[Long],
    email: String,
    emailConfirmed: Boolean,
    password: String,
    nick: String,
    firstName: String,
    lastName: String,

		services: List[String]) extends IdentitySilhouette {
  def key = email
  def fullName: String = firstName + " " + lastName
}

object User {
	
	val services = Seq("Notifyr")

  val users = scala.collection.mutable.HashMap[Long, User](
    1L -> User(Some(1L), "demo@email.com", true, (new BCryptPasswordHasher()).hash("1234").password, "DemoAcc", "Demo", "Edgy", List("Notifyr")),
    2L -> User(Some(2L), "tom@email.com", true, (new BCryptPasswordHasher()).hash("1234").password, "Tom", "Thomas", "Boyle", List("Notifyr"))
  )

  def findByEmail(email: String): Future[Option[User]] = Future.successful(users.find(_._2.email == email).map(_._2))
  //	def findByEmailMap[A] (email: String)(f: User => A): Future[Option[A]] = findByEmail(email).map(_.map(f))

  def save(user: User): Future[User] = {
    // A rudimentary auto-increment feature...
    def nextId: Long = users.maxBy(_._1)._1 + 1

    val theUser = if (user.id.isDefined) user else user.copy(id = Some(nextId))
    users += (theUser.id.get -> theUser)
    Future.successful(theUser)
  }

  def remove(email: String): Future[Unit] = findByEmail(email).map(_.map(u => users.remove(u.id.get)))
}
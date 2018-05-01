package controllers

import play.api.libs.json.{JsResult, JsValue}
import play.api.mvc.{AbstractController, ControllerComponents, Result}
import repositories.Repository

import scala.concurrent.{ExecutionContext, Future}

abstract class ModelController[A](
                                   val repository: Repository[A],
                                   val cc: ControllerComponents)
                                 (implicit val ec: ExecutionContext)
  extends AbstractController(cc) {

  def selectAll(toJson: (Seq[A]) => JsValue): Future[Result] =
    repository.selectAll map { elements => Ok(toJson(elements)) }

  def select(id: Long, toJson: (A) => JsValue): Future[Result] =
    repository.select(id) map {
      case Some(element) => Ok(toJson(element))
      case None => NotFound
    }

  def insert(jsResult: JsResult[A]): Future[Result] =
    jsResult.fold(
      invalid => Future.successful(BadRequest),
      element => repository.insert(element) map {
        case Some(_) => Ok
        case None => UnprocessableEntity
      }
    )

  def deleteModel(id: Long): Future[Result] =
    repository.delete(id) map {
      case Some(deleted) => if (deleted > 0) Ok else NotFound
      case None => BadRequest
    }

  def update(id: Long, jsResult: JsResult[A]): Future[Result] =
    jsResult.fold(
      invalid => Future.successful(BadRequest),
      item => repository.update(id, item) map {
        case Some(updated) => if (updated > 0) Ok else NotFound
        case None => UnprocessableEntity
      }
    )

}

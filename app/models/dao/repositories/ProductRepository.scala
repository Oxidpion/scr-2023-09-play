package models.dao.repositories

import com.google.inject.{ImplementedBy, Singleton}
import db.SlickDatabase
import models.dao.entities.{Product, ProductItem}
import models.dao.schema.{ProductItemSlickSchema, ProductSlickSchema}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

import scala.collection._
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

@ImplementedBy(classOf[ProductRepositoryPostgres])
trait ProductRepository {
  def findByTitle(title: String): Future[List[(Product, List[ProductItem])]]

  def all(): Future[List[(Product, List[ProductItem])]]

  def insert(product: Product, items: List[ProductItem]): Future[Unit]

  def update(product: Product, items: List[ProductItem]): Future[Unit]

  def delete(id: String): Future[Unit]
}


@Singleton
class ProductRepositoryMemory extends ProductRepository {
  implicit val ec: ExecutionContextExecutor = ExecutionContext.global

  private val products = mutable.HashMap.empty[String, Product]
  private val productItems = mutable.HashMap.empty[String, ProductItem]

  override def insert(product: Product, items: List[ProductItem]): Future[Unit] = {
    products.put(product.id, product)
    items.foreach(it => productItems.put(it.id, it))
    Future.successful(())
  }

  def findByTitle(title: String): Future[List[(Product, List[ProductItem])]] =
    Future.apply(products.values.filter(_.title == title).map(
      p => (p, productItems.values.filter(pi => pi.productId == p.id).toList)
    ).toList)

  def all(): Future[List[(Product, List[ProductItem])]] =
    Future.apply(products.values.map(
      p => (p, productItems.values.filter(pi => pi.productId == p.id).toList)
    ).toList)

  def update(product: Product, items: List[ProductItem]): Future[Unit] = {
    products.put(product.id, product)
    items.foreach(it => productItems.put(it.id, it))
    Future.successful(())
  }

  override def delete(productId: String): Future[Unit] = Future.apply(products.remove(productId))
}

@Singleton
class ProductRepositoryPostgres extends ProductRepository with SlickDatabase {
  implicit val ec: ExecutionContextExecutor = ExecutionContext.global

  val products = TableQuery[ProductSlickSchema]
  val productItems = TableQuery[ProductItemSlickSchema]

  override def findByTitle(title: String): Future[List[(Product, List[ProductItem])]] = {
    val q1 = products.join(productItems).on((p, pi) => p.id === pi.productId)
      .filter(p => p._1.title === title)
      .result
    db.run(q1).map(groupByProduct)
  }

  override def all(): Future[List[(Product, List[ProductItem])]] = {
    val q1 = products.join(productItems).on((p, pi) => p.id === pi.productId).result
    db.run(q1).map(groupByProduct)
  }

  private def groupByProduct(items: Seq[(Product, ProductItem)]): List[(Product, List[ProductItem])] = {
    items.groupBy(p => p._1).map(p => (p._1, p._2.map(_._2).toList)).toList
  }

  override def insert(product: Product, items: List[ProductItem]): Future[Unit] = {
    val q1 = products += product
    val q2 = productItems ++= items
    val q3 = (q1 andThen q2 andThen DBIO.successful(())).transactionally
    db.run(q3)
  }

  override def update(product: Product, items: List[ProductItem]): Future[Unit] = {
    val q1 = products.filter(_.id === product.id).update(product)
    val q2 = DBIO.sequence(items.map(it => productItems.filter(_.id === it.id).update(it)))
    val q3 = (q1 andThen q2 andThen DBIO.successful(())).transactionally
    db.run(q3)
  }

  override def delete(id: String): Future[Unit] = db.run(for {
    _ <- productItems.filter(_.productId === id).delete
    _ <- products.filter(_.id === id).delete
  } yield ())
}
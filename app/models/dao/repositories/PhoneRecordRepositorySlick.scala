package models.dao.repositories

import db.SlickDatabase
import models.dao.entities.{Address, PhoneRecord}
import models.dao.schema.{AddressSlickSchema, PhoneRecordSlickSchema}
import slick.dbio.Effect
import slick.lifted.TableQuery
import slick.jdbc.PostgresProfile.api._
import slick.sql.SqlAction

import scala.concurrent.Future

trait PhoneRecordRepositorySlick {
  def find(phone: String): Future[Option[PhoneRecord]]
  def findWithAddress(phone: String): Future[Option[(PhoneRecord, Address)]]
}


class PhoneRecordRepositorySlickImpl extends PhoneRecordRepositorySlick with SlickDatabase{

  val phoneRecords = TableQuery[PhoneRecordSlickSchema]
  val addresses = TableQuery[AddressSlickSchema]

  override def find(phone: String): Future[Option[PhoneRecord]] = {
    val q: SqlAction[Option[PhoneRecord], NoStream, Effect.Read] =
      phoneRecords.filter(_.phone === phone).take(1).result.headOption // select from where limit 1
    db.run(q)
  }

  override def findWithAddress(phone: String): Future[Option[(PhoneRecord, Address)]] = {
    val q1 = for{
      r <- phoneRecords if r.phone === phone
      a <- addresses if a.id === r.addressId
    } yield (r, a)

    val q2 = q1.sortBy(_._1.phone.asc.nullsLast)
    val q3 = q2.take(1)
    db.run(q3.result.headOption)
  }

  def insert(address: Address, phoneRecord: PhoneRecord): Future[Int] = {
    val q1 = phoneRecords += phoneRecord
    val q2 = addresses += address

    val q3 = (q2 andThen q1).transactionally
    db.run(q3)
  }

}

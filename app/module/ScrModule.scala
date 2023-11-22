package module

import models.dao.repositories.{PhoneRecordRepository, PhoneRecordRepositoryImpl}
import models.services.{LoginService, LoginServiceImpl}

class ScrModule extends AppModule {
  override def configure(): Unit = {
    bindSingleton[LoginService, LoginServiceImpl]
    bindSingleton[PhoneRecordRepository, PhoneRecordRepositoryImpl]
  }
}

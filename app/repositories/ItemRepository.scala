package repositories

import com.google.inject.ImplementedBy
import models.Item
import repositories.impl.ItemRepositoryImpl

@ImplementedBy(classOf[ItemRepositoryImpl])
trait ItemRepository extends Repository[Item] {
}

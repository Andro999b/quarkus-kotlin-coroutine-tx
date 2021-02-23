package io.hatis

import io.quarkus.test.common.QuarkusTestResource
import io.quarkus.test.junit.QuarkusTest
import io.smallrye.mutiny.coroutines.awaitSuspending
import io.vertx.mutiny.pgclient.PgPool
import io.vertx.mutiny.sqlclient.Tuple
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import javax.inject.Inject

class BreakTxException: RuntimeException()

@QuarkusTest
@QuarkusTestResource(PgResource::class)
class CoroutineTxTest {

    @Inject
    lateinit var pool: PgPool

    @Test
    fun testCommit() {
        CoroutineTxActions(pool).withTxUni {
            withContext(Dispatchers.IO) {
                CoroutineTxActions.inTransaction {
                    it.preparedQuery("insert into test(value) values($1)")
                        .execute(Tuple.tuple().addString("test"))
                        .awaitSuspending()
                }
            }
        }.await().indefinitely()

        val rs = CoroutineTxActions(pool).withTxUni {
            CoroutineTxActions.inTransaction {
                it.preparedQuery("select value from test limit 1")
                    .execute()
                    .awaitSuspending()
                    .firstOrNull()
            }
        }.await().indefinitely()

        Assertions.assertNotNull(rs?.getString("value"))
    }

    @Test
    fun testRollback() {
        Assertions.assertThrows(BreakTxException::class.java) {
            CoroutineTxActions(pool).withTxUni {
                withContext(Dispatchers.IO) {
                    CoroutineTxActions.inTransaction {

                        it.preparedQuery("insert into test(value) values($1)")
                            .execute(Tuple.tuple().addString("test"))
                            .awaitSuspending()
                    }
                }
                
                throw BreakTxException()
            }.await().indefinitely()
        }

        val rs = CoroutineTxActions(pool).withTxUni {
            CoroutineTxActions.inTransaction {
                it.preparedQuery("select value from test limit 1")
                    .execute()
                    .awaitSuspending()
                    .firstOrNull()
            }
        }.await().indefinitely()

        Assertions.assertNull(rs?.getString("value"))
    }
}
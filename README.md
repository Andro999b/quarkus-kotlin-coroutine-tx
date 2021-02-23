# quarkus-kotlin-coroutine-tx project

This project adds quarkus reactive transactional client to coroutine context.

Examples:

1. Wrap you logic with transaction
```kotlin
CoroutineTxActions(pool).withTxUni {
    service.someLogic()
}
```

2. Retrieve transactional client from context 
```kotlin
//some repo method
CoroutineTxActions.inTransaction {
    it.preparedQuery("...")
        .execute()
        .awaitSuspending()
}
```
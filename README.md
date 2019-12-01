### Example 1

In this example we can see that if we use improper type of exception or don't declare it as rollback cause specifiably we can have our transaction not being rolled back.

### Example 2

Here we will see that multiple schedulers and singleton don't go well together. 

### Example 3

Third example will show us that is not that easy to properly handle `DataIntegrityException`. 
We will see that it's not only hard to catch exception cause is thrown not on a save but on a transaction end. 
You also have to remember that it requires new transaction to save changes after recovery.

### Example 4

This example shows us simple mistake we can make while using spring transactional services when injecting.

NOTE: This mechanism works differently in Spring 5+ so you cannot reproduce this error there. 

### Example 5

Here we will see that often it's beneficial to know some sql basics and use them to avoid orm defaults which may cause some unnecessary queries.
 
### Example 6

Example showing us that prototype is not always a prototype if we don't use it properly.
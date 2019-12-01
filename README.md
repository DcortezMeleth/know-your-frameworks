### Example 1

In this example we can see that if we use improper type of exception or don't declare it as rollback cause specifiably we can have our transaction not being rolled back.

### Example 2

In this example we will see that 
```markdown
javax.transaction.Transactional
```   
is not always equal to
```markdown
org.springframework.transaction.annotation.Transactional
```   

### Example 3

Third example will show us that is not that easy to properly handle `DataIntegrityException`. 
We will see that it's not only hard to catch exception cause is thrown not on a save but on a transaction end. 
You also have to remember that it requires new transaction to save changes after recovery.
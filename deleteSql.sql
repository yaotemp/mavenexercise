-- Start a transaction
BEGIN;

-- Declare a variable to track the number of rows deleted
DECLARE rows_deleted INT DEFAULT 1;

-- Loop to delete in batches
WHILE rows_deleted > 0 DO
    -- Perform the delete operation
    DELETE FROM your_table
    WHERE your_condition
    FETCH FIRST 1000 ROWS ONLY;

    -- Get the number of rows deleted
    GET DIAGNOSTICS rows_deleted = ROW_COUNT;

    -- Commit the transaction after each batch
    COMMIT;
END WHILE;

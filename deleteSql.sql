BEGIN
    DECLARE rows_deleted INT DEFAULT 0;

    -- Loop to delete in batches
    LOOP
        DELETE FROM your_table
        WHERE your_condition
        FETCH FIRST 1000 ROWS ONLY;

        -- Get the number of rows deleted
        GET DIAGNOSTICS rows_deleted = ROW_COUNT;

        -- Exit loop if no more rows are deleted
        IF rows_deleted = 0 THEN
            LEAVE;
        END IF;

        -- Commit after each batch
        COMMIT;
    END LOOP;
END;

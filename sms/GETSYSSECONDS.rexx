/* Enable TRACE to show the execution process and return codes */
TRACE R  /* Enable tracing */

/* Parse argument to get the dataset name */
PARSE ARG datasetName

/* Check if the dataset name is provided */
IF datasetName = "" THEN DO
    SAY 'Error: Dataset name must be provided.'
    EXIT(-1)  /* Exit with an error code of -1 if no dataset name is given */
END

SAY 'Dataset name provided: ' datasetName  /* Debug: Output the dataset name */

/* Use ADDRESS TSO to call LISTDSI */
ADDRESS TSO "LISTDSI '"datasetName"'"

/* Debug: Output the return code from LISTDSI */
SAY 'Return code from LISTDSI is: ' RC

/* Turn off TRACE after debugging steps */
TRACE O  /* Turn off tracing */

/* Check if LISTDSI was successful */
IF RC = 0 THEN DO
    /* Debug: Output system variable SYSSECONDS */
    SAY 'SYSSECONDS value is: ' SYSSECONDS
    RETURN SYSSECONDS  /* Return SYSSECONDS value if successful */
END
ELSE DO
    /* Debug: Output error message when LISTDSI fails */
    SAY 'Error: LISTDSI failed for dataset ' datasetName
    RETURN -1  /* Return -1 to indicate an error */
END

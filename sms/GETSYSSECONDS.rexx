/* REXX script named GETSYSSECONDS */

/* Parse argument to get the dataset name */
PARSE ARG datasetName

/* Check if the dataset name is provided */
IF datasetName = "" THEN DO
    SAY 'Error: Dataset name must be provided.'
    EXIT(-1)  /* Exit with an error code of -1 if no dataset name is given */
END

/* Call LISTDSI function to retrieve dataset information */
X = LISTDSI("'"datasetName"'")

/* Check the return code of LISTDSI function */
IF X = 0 THEN DO
    /* If LISTDSI is successful, return the secondary allocation size (SYSSECONDS) */
    RETURN SYSSECONDS
END
ELSE DO
    /* If LISTDSI fails, display an error message and return -1 */
    SAY 'Error: Failed to retrieve information for dataset ' datasetName
    RETURN -1
END

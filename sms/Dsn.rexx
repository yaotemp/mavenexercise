/* REXX script to get dataset information using DSINFO */

/* Parse argument to get the dataset name */
PARSE ARG datasetName

/* Check if the dataset name is provided */
IF datasetName = "" THEN DO
    SAY 'Error: Dataset name must be provided.'
    EXIT(-1)  /* Exit with an error code of -1 if no dataset name is given */
END

/* Initialize ISPF environment */
ADDRESS ISPEXEC "CONTROL ERRORS RETURN"  /* ISPF Error handling */

/* Allocate a new variable pool to store dataset information */
ADDRESS ISPEXEC "LMINIT DATAID(DID) DSNAME('"datasetName"')" 
IF RC <> 0 THEN DO
    SAY 'Error: Failed to initialize the dataset information for ' datasetName
    EXIT(RC)
END

/* Call the DSINFO service to get dataset information */
ADDRESS ISPEXEC "DSINFO DATAID("DID")" 

/* Check if DSINFO succeeded */
IF RC = 0 THEN DO
    /* Print dataset information */
    SAY "Dataset Name: " datasetName
    SAY "Volume Serial: " SYSVOL  /* Volume Serial Number */
    SAY "Dataset Organization: " SYSDORG
    SAY "Record Format: " SYSRECFM
    SAY "Logical Record Length: " SYSLRECL
    SAY "Block Size: " SYSBLKSIZE
    SAY "Allocated Tracks: " SYSALLOC
    SAY "Used Tracks: " SYSUSED
    SAY "Number of Extents: " SYSEXT
    SAY "Creation Date: " SYSCREAT
    SAY "Expiration Date: " SYSEXPD
END
ELSE DO
    SAY 'Error: DSINFO failed for dataset' datasetName ', RC=' RC
    EXIT(-1)
END

/* Free the dataset information */
ADDRESS ISPEXEC "LMFREE DATAID("DID")"
EXIT 0

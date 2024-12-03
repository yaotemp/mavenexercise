/* REXX script to list datasets from a catalog and process only those containing '@' */

arg hlq  /* Get the high-level qualifier (HLQ) from the arguments */

address ISPEXEC

/* Initialize the dataset list for the given HLQ */
if ispexec("LMDINIT LISTID(LID) LEVEL("hlq")") >= 8 then do
    say "Error: Could not initialize dataset list for HLQ" hlq
    exit 8
end

/* Iterate over each dataset and process only those that contain '@' */
do while ispexec("LMDLIST LISTID(&LID) DATASET(DSNAME) STATS(YES)") = 0
    /* If the dataset name does not contain '@', skip to the next iteration */
    if POS('@', dsname) = 0 then do
        iterate
    end

    /* Only datasets that contain '@' will reach here */
    say "Dataset name contains '@':" dsname

    /* Call the function to get volumes for this dataset */
    volserList = getVolumes(dsname)
    
    /* Display the volumes if found */
    if volserList <> "" then do
        say "VOLSER entries for dataset" dsname ":"
        say volserList
    end
    else do
        say "No VOLSER entries found for dataset" dsname "."
    end
end

/* Free the dataset list */
call ispexec "LMDFREE LISTID(&LID)"

exit 0

/* Function to Get Volumes for a Dataset */
getVolumes: procedure
    parse arg datasetName
    volserList = ""

    /* Allocate a temporary file to hold LISTCAT output */
    ddname = "LISTCATOUT"
    address TSO "ALLOCATE FILE("ddname") NEW REUSE LRECL(255) TRACKS SPACE(5,5) UNIT(SYSDA)"

    /* Run LISTCAT command and direct output to the allocated DDNAME */
    address TSO "SUBCOM IDCAMS"
    if RC <> 0 then address TSO "SUBCOM IDCAMS LOAD"  /* Ensure IDCAMS is loaded */
    address IDCAMS "LISTCAT ENT("datasetName") ALL -",
                   "OUTFILE("ddname")"

    /* Open the LISTCAT output for reading */
    address TSO "EXECIO * DISKR "ddname" (STEM outlines. FINIS"

    /* Check if we have output from LISTCAT */
    if RC <> 0 then do
        say "Error: Unable to read LISTCAT output for dataset:" datasetName
        address TSO "FREE FILE("ddname")"
        return ""
    end

    /* Loop through LISTCAT output and parse for VOLSER */
    do i = 1 to outlines.0
        line = outlines.i
        if pos("VOLSER", line) > 0 then do
            parse var line . "VOLSER" volser .
            volserList = volserList volser
        end
    end

    /* Free the temporary file after reading */
    address TSO "FREE FILE("ddname")"

    return volserList

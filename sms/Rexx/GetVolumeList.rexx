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

/* Function to Get Volumes for a Dataset Using OUTTRAP */
getVolumes: procedure
    parse arg datasetName
    volserList = ""

    /* Use OUTTRAP to capture LISTCAT output into a stem variable */
    call outtrap 'OUT.'
    address TSO "LISTCAT ENT("datasetName") ALL"
    call outtrap 'OFF'

    /* Parse the captured output for VOLSER information */
    collect = 0
    do i = 1 to OUT.0
        /* If the line contains NONVSAM, capture the dataset name */
        if index(OUT.i, 'NONVSAM') > 0 then do
            dsn = word(OUT.i, words(OUT.i))
            if collect then leave
            if dsn = datasetName then do
                collect = 1
                say "Listing datasets for" dsn
            end
            else collect = 0
        end

        /* If the line contains VOLSER, extract the volume */
        if index(OUT.i, 'VOLSER-') > 0 & collect then do
            parse var OUT.i 'VOLSER------------' volume .
            volserList = volserList volume
            say "Found VOLSER:" volume
        end
    end

    return volserList

/* REXX */

arg volume output_file .

address ISPEXEC                                                         

/* Initialize the dataset list */
if ispexec("LMDINIT LISTID(LID) VOLUME("volume")") >= 8 then exit 8    

/* Open the output dataset for writing */
address TSO "ALLOCATE FILE(OUTFILE) DATASET('"output_file"') OLD REUSE"
if RC <> 0 then do
    say "Error: Could not allocate output file '"output_file"'"
    exit 8
end

/* Iterate over each dataset found on the volume and write to output file */
do while ispexec("LMDLIST LISTID(&LID) DATASET(DSNAME) STATS(YES)") = 0
    /* Get the dataset name */
    dataset = dsname

    /* Use DSINFO to get dataset extent information */
    address ISPEXEC "DSINFO DATASET('"dataset"')"
    if RC <> 0 then do
        /* If DSINFO fails, skip to the next dataset */
        say "Warning: Could not retrieve DSINFO for dataset '"dataset"', skipping."
        iterate
    end

    /* Extract the values of ZDS1EX and ZDS2EX */
    primary_extent = ZDS1EX      /* Primary allocation size */
    secondary_extent = ZDS2EX    /* Secondary allocation size */

    /* Write the dataset name and allocation information to the output file */
    outLine.1 = "Dataset: " dataset ", Primary Extent: " primary_extent ", Secondary Extent: " secondary_extent
    address TSO "EXECIO 1 DISKW OUTFILE (STEM outLine. FINIS"
    if RC <> 0 then do
        say "Error: Could not write to output file"
        exit 8
    end
end    

/* Release the dataset list */
call ispexec "LMDFREE LISTID(&LID)"

/* Close the output file */
address TSO "FREE FILE(OUTFILE)"

exit 0                                                                 

/* Subroutine ispexec for executing ISPF commands with error handling */
ispexec:                                                                 
  arg cmd                                                                
  "CONTROL ERRORS RETURN"                                                
  cmd                                                                   
  res = rc                                                              
  if res >= 8 then do                                                   
     "SETMSG MSG(ISRZ002)"                                              
  end                                                                   
  "CONTROL ERRORS CANCEL"                                                
  return res  

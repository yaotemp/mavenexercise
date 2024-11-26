/* REXX: Get dataset details and parse secondary allocation information */
parse arg dataset_name

/* Check if the user provided a dataset name */
if dataset_name = '' then do
  say 'Please provide a dataset name as an argument.'
  exit 1
end

/* Allocate output dataset to capture LISTCAT output */
address TSO "ALLOCATE FILE(SYSPRINT) SYSOUT(*) RC(ALLOC_RC)"

/* Check if allocation was successful */
if ALLOC_RC <> 0 then do
  say 'Failed to allocate SYSPRINT. Please check your allocation settings.'
  exit 1
end

/* Invoke IDCAMS LISTCAT ENT command */
address TSO "CALL *(IDCAMS)"
address TSO "LISTCAT ENT('"dataset_name"') ALL"

/* Set default value for result.0 */
result.0 = 0

/* Read SYSPRINT file contents */
address TSO "EXECIO * DISKR SYSPRINT (STEM result. FINIS RC(EXECIO_RC)"

/* Check if EXECIO was successful */
if EXECIO_RC <> 0 then do
  say 'Failed to read SYSPRINT output. Please check your dataset and commands.'
  address TSO "FREE FILE(SYSPRINT)"
  exit 1
end

/* Release the SYSPRINT file */
address TSO "FREE FILE(SYSPRINT)"

/* Parse IDCAMS output to find Secondary Allocation information */
found = 0
do i = 1 to result.0
  if pos('SECEXT', strip(result.i)) > 0 then do
    say 'Secondary Allocation (SECEXT):' result.i
    found = 1
    leave
  end
end

/* If secondary allocation information was not found */
if found = 0 then
  say 'Unable to find Secondary Allocation information.'

exit 0

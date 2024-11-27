/* REXX: Get KEYLEN value for a dataset passed as a parameter and store it in an output dataset */
parse arg dataset_name output_dataset_name

/* Check if the user provided a dataset name and output dataset */
if dataset_name = '' | output_dataset_name = '' then do
    say 'Please provide both the dataset name and the output dataset name as arguments.'
    exit 1
end

/* Allocate output dataset to capture LISTCAT output */
Call OutTrap "OUTPUT."
Address TSO "LISTCAT ENTRIES('"dataset_name"') CLUSTER DATA INDEX ALL"
Call OutTrap "OFF"

/* Initialize Keylen */
Keylen = ""

/* Check if there is any output */
If Output.0 = 0 Then Do
    Say "No output captured from LISTCAT. Please check the dataset name."
    Exit 1
End

/* Loop through captured output to find KEYLEN */
Do I = 1 To Output.0 Until Keylen ¬= ""
    Line = Strip(Output.I)  /* Remove leading and trailing spaces */
    Locate = Pos("KEYLEN--", Line) 
    If Locate > 0 Then Do
        /* Extract the part after KEYLEN-- */
        KeylenPart = Strip(Substr(Line, Locate + Length("KEYLEN--")))
        
        /* Extract numeric part from KeylenPart */
        numericPart = ""
        Do j = 1 To Length(KeylenPart)
            char = Substr(KeylenPart, j, 1)
            If DataType(char, "N") Then
                numericPart = numericPart || char
        End

        /* If we found a valid numeric part, assign it to Keylen */
        If numericPart ¬= "" Then
            Keylen = numericPart
            Leave
    End
End

/* Allocate output dataset for writing the key length */
If Keylen = "" Then Do
    Say "Unable to find KEYLEN in the LISTCAT output."
    Exit 1  /* Exit with an error code if no valid key length found */
End

/* Allocate the output dataset */
Address TSO "ALLOCATE DATASET('"output_dataset_name"') WRITE REUSE"

/* Write the key length to the dataset */
outputData.1 = Keylen
outputData.0 = 1
Address TSO "EXECIO 1 DISKW "output_dataset_name" (STEM outputData. FINIS"

/* Free the output dataset */
Address TSO "FREE DATASET('"output_dataset_name"')"

Say "The key length (KEYLEN) has been written to the dataset:" output_dataset_name

Exit 0  /* Exit successfully */

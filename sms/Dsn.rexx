/* REXX: Get KEYLEN value for a dataset passed as a parameter */
parse arg dataset_name

/* Check if the user provided a dataset name */
if dataset_name = '' then do
    say 'Please provide a dataset name as an argument.'
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

/* If KEYLEN is found, return the numeric value */
If Keylen = "" Then
    Exit 1  /* Exit with an error code if no valid key length found */
Else
    Return Keylen  /* Return the numeric key length */

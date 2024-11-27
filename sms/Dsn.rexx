Call OutTrap "OUTPUT." 
Address TSO "LISTCAT ENTRIES(VSAMDS) CLUSTER DATA INDEX ALL"
Call OutTrap "OFF" 

Keylen = ""                                                            
Do   I = 1 To Output.0 Until Keylen ¬= ""
     Locate = Pos(" KEYLEN--",Output.I) 
     If   Locate > 0 Then 
          Keylen = Word(Substr(Output.I,Locate),1) 
End 
                                                            
Say Keylen 
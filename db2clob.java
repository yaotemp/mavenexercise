public class db2clob {
    
    CLOB dbClob=(CLOB) (rslt.getClob(1));
    char[] buffer =new char[dbClob.getBuffersize()];
    out=dbClob.getCharacterOutputStream();
    int length =0;
    while (length=isClob.read(buffer))!=-1){
        out.write(buffer,0,length);
    }
}

//DCOLLECT JOB (ACCOUNT),'DCOLLECT EXAMPLE',CLASS=A,MSGCLASS=A
//STEP1    EXEC PGM=IDCAMS
//SYSPRINT DD SYSOUT=*
//DCOUT    DD DSN=USER.DCOLLECT.OUTPUT,      /* Output dataset */
//             DISP=(NEW,CATLG,DELETE),       /* Create new dataset, catalog if successful */
//             SPACE=(CYL,(10,5),RLSE),       /* Primary and secondary space allocation */
//             DCB=(RECFM=VB,LRECL=644,BLKSIZE=27998),
//             UNIT=SYSDA
//SYSIN    DD *
     DCOLLECT OFILE(DCOUT) VOLUME(VOL001)
/*

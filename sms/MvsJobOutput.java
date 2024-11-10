/*
 * %Z%%W% %I%
 *
 * =========================================================================
 * Licensed Materials - Property of IBM
 * "Restricted Materials of IBM"
 * (C) Copyright IBM Corp. 2011. All Rights Reserved
 * 
 * DISCLAIMER: 
 * The following [enclosed] code is sample code created by IBM 
 * Corporation.  This sample code is not part of any standard IBM product 
 * and is provided to you solely for the purpose of assisting you in the 
 * development of your applications.  The code is provided 'AS IS', 
 * without warranty of any kind.  IBM shall not be liable for any damages 
 * arising out of your use of the sample code, even if they have been 
 * advised of the possibility of such damages.
 * =========================================================================
 */
package com.ibm.jzos.sample;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

import com.ibm.jzos.Exec;
import com.ibm.jzos.RcException;
import com.ibm.jzos.ZUtil;

/**
 * Sample program which reads all sysout data for a MvsJob (jobname and jobid),
 * and writes the output to a specified Writer.
 * 
 * The class relies on the sample REXX script "jobOutput", spawned as a child process
 * via the {@link com.ibm.jzos.Exec} class.
 * 
 * @since 2.1.0
 */
public class MvsJobOutput {

    public static final String JOB_STATUS_CMD = "jobStatus";
    public static final String JOB_OUTPUT_CMD = "jobOutput";

    /**
     * A sample main method that writes sysout output for
     * a job to System.out (STDOUT).
     * 
     * The first argument is the jobname, and the second argument
     * is the jobid (JOBnnnnn).
     */
    public static void main(String[] args) {
        try {
            if (args.length < 2) {
                throw new IllegalArgumentException("Missing arguments: jobname jobid");
            }

            MvsJob mvsJob = new MvsJob(args[0], args[1]);

            // Print out the status of the job
            System.out.println("JOB " + mvsJob + " " + getStatus(mvsJob));

            // Write job output to System.out
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
            writeJobOutput(mvsJob, writer);
            writer.close();

        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the job status using the TSO "status" command, which
     * is invoked via the "jobStatus" REXX USS script.
     * 
     * @return String the TSO "STATUS" command status
     * @throws IOException if there was an error communicating with the child REXX script process
     */
    public static String getStatus(MvsJob job) throws IOException, URISyntaxException {
        Exec exec = new Exec(getStatusCommand(job), getEnvironment());
        exec.run();

        String line = exec.readLine();
        if (line == null) throw new IOException("No output from jobStatus child process");

        // Close the stream connected to the stdin of the external process
        BufferedWriter wdr = exec.getStdinWriter();
        wdr.close();

        // Slurp other output
        while (exec.readLine() != null) {};

        int rc = exec.getReturnCode();
        if (rc != 0) {
            throw new RcException("REXX 'jobStatus' process failed: " + line, rc);
        }

        StringTokenizer tok = new StringTokenizer(line);
        if (tok.countTokens() < 3) {
            throw new IOException("Invalid output from jobStatus child process: " + line);
        }

        String next = tok.nextToken();

        // Skip over message id
        if (next.startsWith("IKJ")) {
            next = tok.nextToken();
        }

        if (!next.equalsIgnoreCase("JOB")) {
            throw new IOException("Invalid output from jobStatus child process: " + line);
        }

        // Skip jobname(jobid)
        tok.nextToken();
        String answer = "";

        // Concat remaining words
        while (tok.hasMoreTokens()) {
            answer += tok.nextToken();

            if (tok.hasMoreTokens()) {
                answer += " ";
            }
        }

        return answer;
    }

    /**
     * Returns the command to be executed via Runtime.exec().
     * This is the REXX script 'jobStatus' followed by the jobname and jobid.
     */
    protected static String getStatusCommand(MvsJob job) throws URISyntaxException {
        // Get the directory of the JAR file
        String jarDir = new File(MvsJobOutput.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        String cmdPath = jarDir + "/" + JOB_STATUS_CMD + " " + job.getJobname();

        if (job.getJobid() != null) {
            cmdPath += " " + job.getJobid();
        }

        return cmdPath;
    }

    /**
     * Writes all of the output for a given job to a writer.
     * Note: this method flushes the writer, but does not close it.
     * It is the caller's responsibility to close the writer.
     */
    public static void writeJobOutput(MvsJob mvsJob, Writer writer) throws IOException, URISyntaxException {
        Exec exec = new Exec(getJobOutputCommand(mvsJob), getEnvironment());
        exec.run();

        try {
            String line;
            while ((line = exec.readLine()) != null) {
                writer.write(line);
                writer.write('\n');
            }

            writer.flush();
        } finally {
            int rc = exec.getReturnCode();
            if (rc != 0) {
                throw new RcException("REXX 'jobOutput' process failed", rc);
            }
        }
    }

    /**
     * Returns the command to be executed via Runtime.exec().
     * This is the REXX script 'jobOutput' followed by the jobname and jobid.
     */
    protected static String getJobOutputCommand(MvsJob mvsJob) throws URISyntaxException {
        // Get the directory of the JAR file
        String jarDir = new File(MvsJobOutput.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        return jarDir + "/" + JOB_OUTPUT_CMD + " " + mvsJob.getJobname() + " " + mvsJob.getJobid();
    }

    /**
     * Returns the environment to use for the child process.
     * This is the current environment with _BPX_SHAREAS and _BPX_SPAWN_SCRIPT
     * set to "YES", so that the child process will execute in the same
     * address space.
     */
    protected static String[] getEnvironment() {
        Properties p = ZUtil.getEnvironment();
        p.put("_BPX_SHAREAS", "YES");
        p.put("_BPX_SPAWN_SCRIPT", "YES");

        String[] environ = new String[p.size()];
        int i = 0;

        for (Iterator<Object> iter = p.keySet().iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            environ[i++] = key + "=" + p.getProperty(key);
        }

        return environ;
    }
}

#!/bin/bash

# Path to your Java application
JAVA_CMD="/u/java/bin/java -jar /u/your_user/yourapp.jar"

# Log file to store output
LOG_FILE="/u/your_user/app.log"

# Schedule interval (e.g., run every 5 minutes)
SCHEDULE_TIME="now + 5 minutes"

# 1. Run the Java application and log output
echo "Running job at $(date)" >> "$LOG_FILE"
$JAVA_CMD >> "$LOG_FILE" 2>&1

# 2. Reschedule this script using 'at'
echo "Rescheduling job at $(date)" >> "$LOG_FILE"
echo "/u/your_user/run_repeated_job.sh" | at "$SCHEDULE_TIME"

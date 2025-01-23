-- Table 1: DASD_FREESPACE_RECORD
CREATE TABLE DASD_FREESPACE_RECORD (
    ID                    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- Auto-incrementing ID
    VOLUME_SERIAL         CHAR(10) NOT NULL,                               -- Volume Serial
    MOUNT_ATTR            CHAR(50),                                        -- Mount Attributes
    DEVICE_TYPE           CHAR(20),                                        -- Device Type
    FREE_EXTS             INTEGER NOT NULL,                                -- Free Extents
    FREE_TRACKS           INTEGER NOT NULL,                                -- Free Tracks
    FREE_CYLS             INTEGER NOT NULL,                                -- Free Cylinders
    LRG_FREE_TRACKS       INTEGER NOT NULL,                                -- Largest Free Tracks
    LG_FREE_CYLS          INTEGER NOT NULL,                                -- Largest Free Cylinders
    DASD_NMBR             CHAR(10),                                        -- DASD Number
    VTOC                  CHAR(20),                                        -- VTOC
    SMS_INDX              CHAR(20),                                        -- SMS Index
    SMS_IND               CHAR(5),                                         -- SMS Indicator
    DENSITY               CHAR(10),                                        -- Density
    STORAGE_GROUP         CHAR(50),                                        -- Storage Group
    VOLUME_FREE_PERCENT   DECIMAL(5, 2),                                   -- Volume Free Percentage
    DSCB_FREE_PERCENT     DECIMAL(5, 2),                                   -- DSCB Free Percentage
    VIRS_FREE_PERCENT     DECIMAL(5, 2),                                   -- Virtual Space Free Percentage
    CYLS_ON_VOL           INTEGER NOT NULL,                                -- Cylinders on Volume
    SMS_VOL_STATUS        CHAR(20),                                        -- SMS Volume Status
    ALLOC_CNTR            CHAR(10),                                        -- Allocation Counter
    FC_DS_FLAG            CHAR(5),                                         -- Feature Dataset Flag
    CREATED_AT            TIMESTAMP DEFAULT CURRENT TIMESTAMP,             -- Record creation timestamp
    UPDATED_AT            TIMESTAMP DEFAULT CURRENT TIMESTAMP ON UPDATE AS ROW CHANGE TIMESTAMP
)
IN DATABASE_NAME.TABLESPACE_NAME;

-- Table 2: LOB_DATA_FILE_INFO
CREATE TABLE LOB_DATA_FILE_INFO (
    ID                     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- Auto-incrementing ID
    HLQ                    VARCHAR(50) NOT NULL,                           -- High-Level Qualifier
    FILE_NAME              VARCHAR(100) NOT NULL,                          -- File Name
    PRIMARY_ALLOCATION     INTEGER NOT NULL,                               -- Primary Allocation (Tracks)
    SECONDARY_ALLOCATION   INTEGER NOT NULL,                               -- Secondary Allocation (Tracks)
    CREATION_DATE          DATE NOT NULL,                                  -- File Creation Date
    USED_TRACKS            INTEGER NOT NULL,                               -- Used Tracks
    EXTENTS                INTEGER NOT NULL,                               -- Extents
    VOLSERS                VARCHAR(512) NOT NULL,                          -- Volume Serial Numbers
    CREATED_AT             TIMESTAMP DEFAULT CURRENT TIMESTAMP,            -- Record Creation Timestamp
    UPDATED_AT             TIMESTAMP DEFAULT CURRENT TIMESTAMP ON UPDATE AS ROW CHANGE TIMESTAMP
)
IN DATABASE_NAME.TABLESPACE_NAME;

-- Table 3: LOB_DATA_DAILY_TRACK_USAGE
CREATE TABLE LOB_DATA_DAILY_TRACK_USAGE (
    ID             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- Auto-incrementing ID
    TRACK_DATE     DATE NOT NULL,                                   -- Date of track usage
    USED_TRACKS    INTEGER NOT NULL,                               -- Number of tracks used
    CREATED_AT     TIMESTAMP DEFAULT CURRENT TIMESTAMP,            -- Record creation timestamp
    UPDATED_AT     TIMESTAMP DEFAULT CURRENT TIMESTAMP ON UPDATE AS ROW CHANGE TIMESTAMP
)
IN DATABASE_NAME.TABLESPACE_NAME;

ALTER TABLE LOB_DATA_DAILY_TRACK_USAGE ADD CONSTRAINT UNIQUE_TRACK_DATE UNIQUE (TRACK_DATE);
CREATE INDEX IDX_TRACK_DATE ON LOB_DATA_DAILY_TRACK_USAGE (TRACK_DATE);

-- Table 4: LOB_DATA_TRACK_USAGE_SUMMARY
CREATE TABLE LOB_DATA_TRACK_USAGE_SUMMARY (
    ID                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- Auto-incrementing ID
    SUMMARY_DATE      DATE NOT NULL,                                   -- Base date for the summary
    NEXT_1_DAY        INTEGER NOT NULL,                               -- Tracks summary for next 1 day
    NEXT_7_DAYS       INTEGER NOT NULL,                               -- Tracks summary for next 7 days
    NEXT_30_DAYS      INTEGER NOT NULL,                               -- Tracks summary for next 30 days
    NEXT_60_DAYS      INTEGER NOT NULL,                               -- Tracks summary for next 60 days
    NEXT_90_DAYS      INTEGER NOT NULL,                               -- Tracks summary for next 90 days
    NEXT_180_DAYS     INTEGER NOT NULL,                               -- Tracks summary for next 180 days
    CREATED_AT        TIMESTAMP DEFAULT CURRENT TIMESTAMP,            -- Record creation timestamp
    UPDATED_AT        TIMESTAMP DEFAULT CURRENT TIMESTAMP ON UPDATE AS ROW CHANGE TIMESTAMP
)
IN DATABASE_NAME.TABLESPACE_NAME;

CREATE INDEX IDX_SUMMARY_DATE ON LOB_DATA_TRACK_USAGE_SUMMARY (SUMMARY_DATE);

-- Table 5: CYLINDER_CHUNK_DAILY_RECORDS
CREATE TABLE CYLINDER_CHUNK_DAILY_RECORDS (
    ID                        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, -- Auto-incrementing ID
    VOLUME_SERIAL             VARCHAR(10) NOT NULL,                           -- Volume Serial
    RECORD_DATE               DATE NOT NULL,                                  -- Date
    RECORD_TIME               TIME NOT NULL,                                  -- Time
    FREE_PERCENTAGE           DECIMAL(5, 2) NOT NULL,                         -- Free Percentage
    FREE_CYLINDER             INTEGER NOT NULL,                               -- Free Cylinder
    CYLINDER_THRESHOLD        INTEGER NOT NULL,                               -- Cylinder Threshold
    MATCHED_VOLUMES           INTEGER NOT NULL,                               -- Matched Volumes
    VOLUMES_BELOW_THRESHOLD   INTEGER NOT NULL,                               -- Volumes Below Threshold
    CURRENT_AVAILABLE_CHUNKS  INTEGER NOT NULL,                               -- Current Available Chunks
    NEXT_1_DAY                INTEGER NOT NULL,                               -- Next 1 Day
    NEXT_7_DAYS               INTEGER NOT NULL,                               -- Next 7 Days
    NEXT_30_DAYS              INTEGER NOT NULL,                               -- Next 30 Days
    NEXT_60_DAYS              INTEGER NOT NULL,                               -- Next 60 Days
    NEXT_90_DAYS              INTEGER NOT NULL,                               -- Next 90 Days
    NEXT_180_DAYS             INTEGER NOT NULL,                               -- Next 180 Days
    CREATED_AT                TIMESTAMP DEFAULT CURRENT TIMESTAMP,            -- Record Creation Time
    UPDATED_AT                TIMESTAMP DEFAULT CURRENT TIMESTAMP ON UPDATE AS ROW CHANGE TIMESTAMP
)
IN DATABASE_NAME.TABLESPACE_NAME;

CREATE INDEX IDX_CYLINDER_CHUNK_DATE ON CYLINDER_CHUNK_DAILY_RECORDS (RECORD_DATE);

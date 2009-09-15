CREATE TABLE PROTOCOL_ACTIONS (
    PROTOCOL_ACTION_ID NUMBER (12, 0) NOT NULL, 
    PROTOCOL_NUMBER VARCHAR2 (20) NOT NULL, 
    SEQUENCE_NUMBER NUMBER (4) NOT NULL, 
    SUBMISSION_NUMBER NUMBER (4) , 
    ACTION_ID NUMBER (6) NOT NULL, 
    PROTOCOL_ACTION_TYPE_CODE VARCHAR2 (3) NOT NULL, 
    PROTOCOL_ID NUMBER (12, 0) NOT NULL, 
    SUBMISSION_ID_FK NUMBER (12, 0) , 
    COMMENTS VARCHAR2 (2000) , 
    ACTION_DATE DATE, 
    ACTUAL_ACTION_DATE DATE DEFAULT SYSDATE NOT NULL, 
    UPDATE_TIMESTAMP DATE, 
    UPDATE_USER VARCHAR2 (60) , 
    VER_NBR NUMBER (8, 0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR2 (36) DEFAULT SYS_GUID () NOT NULL) ;

ALTER TABLE PROTOCOL_ACTIONS 
    ADD CONSTRAINT PK_PROTOCOL_ACTIONS 
            PRIMARY KEY (PROTOCOL_ACTION_ID) ;


DELIMITER /

CREATE TABLE S2S_USER_ATTACHED_FORM ( 
    S2S_USER_ATTACHED_FORM_ID DECIMAL(12,0) NOT NULL, 
    PROPOSAL_NUMBER VARCHAR(12) NOT NULL, 
    NAMESPACE VARCHAR(200), 
    FORM_NAME VARCHAR(100), 
    FORM_FILE_NAME VARCHAR(100), 
    DESCRIPTION VARCHAR(200), 
    UPDATE_USER VARCHAR(60) NOT NULL, 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    VER_NBR DECIMAL(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR(36)  NOT NULL)
/
ALTER TABLE S2S_USER_ATTACHED_FORM 
ADD CONSTRAINT PK_S2S_USER_ATTACHED_FORM 
PRIMARY KEY (S2S_USER_ATTACHED_FORM_ID)
/
ALTER TABLE S2S_USER_ATTACHED_FORM 
ADD CONSTRAINT UQ_S2S_USER_ATTACHED_FORM 
UNIQUE (PROPOSAL_NUMBER, NAMESPACE)
/
CREATE TABLE S2S_USER_ATTACHED_FORM_FILE ( 
    S2S_USER_ATTACHED_FORM_FILE_ID DECIMAL(12,0) NOT NULL, 
    S2S_USER_ATTACHED_FORM_ID DECIMAL(12,0) NOT NULL, 
    FORM_FILE LONGBLOB, 
    XML_FILE LONGTEXT, 
    UPDATE_USER VARCHAR(60) NOT NULL, 
    UPDATE_TIMESTAMP DATE NOT NULL, 
    VER_NBR DECIMAL(8,0) DEFAULT 1 NOT NULL, 
    OBJ_ID VARCHAR(36) NOT NULL)
/
ALTER TABLE S2S_USER_ATTACHED_FORM_FILE 
ADD CONSTRAINT PK_S2S_USER_ATTACHED_FORM_FILE 
PRIMARY KEY (S2S_USER_ATTACHED_FORM_FILE_ID)
/
ALTER TABLE S2S_USER_ATTACHED_FORM_FILE 
ADD CONSTRAINT FK1_S2S_USER_ATTACHED_FORM 
FOREIGN KEY (S2S_USER_ATTACHED_FORM_ID) 
REFERENCES S2S_USER_ATTACHED_FORM (S2S_USER_ATTACHED_FORM_ID) ON DELETE CASCADE
/
ALTER TABLE S2S_OPP_FORMS ADD USER_ATTACHED_FORM VARCHAR(1) DEFAULT 'N'
/
DELIMITER ;

DELIMITER /

CREATE TABLE SUBAWARD_REPORT_TYPE  
(
     REPORT_TYPE_CODE      DECIMAL(3,0) NOT NULL
   , DESCRIPTION           VARCHAR(50) NOT NULL
   , UPDATE_TIMESTAMP      DATE NOT NULL
   , UPDATE_USER           VARCHAR(60) NOT NULL
   , VER_NBR               DECIMAL(8,0) DEFAULT 1 NOT NULL
   , OBJ_ID                VARCHAR(36) NOT NULL
)
/

ALTER TABLE SUBAWARD_REPORT_TYPE 
ADD CONSTRAINT REPORT_TYPE_CODE_PK 
PRIMARY KEY (REPORT_TYPE_CODE) 
/

ALTER TABLE SUBAWARD_REPORT_TYPE ADD SORT_ID DECIMAL(2,0)
/

DELIMITER ;
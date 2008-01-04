 CREATE TABLE JOB_CODE 
   (	"JOB_CODE" VARCHAR2(6) constraint JOB_CODEN1 NOT NULL ENABLE, 
	"JOB_TITLE" VARCHAR2(50) constraint JOB_CODEN2 NOT NULL ENABLE, 
	"UPDATE_TIMESTAMP" DATE constraint JOB_CODEN3 NOT NULL ENABLE, 
	"UPDATE_USER" VARCHAR2(8) constraint JOB_CODEN4 NOT NULL ENABLE, 
	 "VER_NBR" NUMBER(8,0) DEFAULT 1  constraint  JOB_CODEN5  NOT NULL ENABLE,
	"OBJ_ID" VARCHAR2(36) DEFAULT SYS_GUID()  constraint  JOB_CODEN6  NOT NULL ENABLE,
	CONSTRAINT "PK_JOB_CODE_KRA" PRIMARY KEY ("JOB_CODE") ENABLE
);
 
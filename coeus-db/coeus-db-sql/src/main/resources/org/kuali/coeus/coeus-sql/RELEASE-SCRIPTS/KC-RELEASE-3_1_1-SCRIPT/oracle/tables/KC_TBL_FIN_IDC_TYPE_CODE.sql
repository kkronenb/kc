CREATE TABLE FIN_IDC_TYPE_CODE
  ( 
    FIN_IDC_CODE varchar2(5) not null,
    RATE_TYPE_CODE varchar2(3) not null,
    OBJ_ID VARCHAR2(36),
    VER_NBR NUMBER(8,0) NOT NULL,
    RATE_CLASS_CODE VARCHAR2(3) not null
  )
/
ALTER TABLE FIN_IDC_TYPE_CODE
    ADD CONSTRAINT PK_IDC_CODE
    PRIMARY KEY (RATE_CLASS_CODE, RATE_TYPE_CODE)
/

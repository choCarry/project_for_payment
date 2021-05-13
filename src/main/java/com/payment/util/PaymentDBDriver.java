package com.payment.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.h2.tools.DeleteDbFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.payment.vo.PaymentInfoVo;

public class PaymentDBDriver {
	
	private static final String DB_DRIVER = "org.h2.Driver";
	private static final String DB_CONNECTION = "jdbc:h2:~/kakaopay_pj";
	private static final String DB_USER = "sa";
	private static final String DB_PWD = "";
	
	private static final Logger logger = LoggerFactory.getLogger(PaymentDBDriver.class);
	
    public static void main(String[] args) {
        try {
            DeleteDbFiles.execute("~", "kakaopay_pj", true); // drop db if exist 'kakaopay_pj'
            initDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getDBConnection() {
        Connection dbConnection = null;
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PWD);
            return dbConnection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return dbConnection;
    }

    /**
     * DB START (TABLE CREATE / SEQUENCE CREATE)
     * @throws SQLException
     */
    private static void initDB() throws SQLException {
        Connection connection = getDBConnection();
        Statement stmt = null;
        try {
            connection.setAutoCommit(false);
            stmt = connection.createStatement();
            StringBuffer createTable = new StringBuffer();
            // 카드 결제 히스토리 적재 테이블 생성
            createTable.append(" CREATE TABLE IF NOT EXISTS TB_PAYMENT  ")
            		   .append(" (                                      ")
            		   .append("   UID VARCHAR2(20) PRIMARY KEY         ")
            		   .append("   ,PAY_CLSS VARCHAR(2)                 ")
            		   .append("   ,REQ_AMT INT                         ")
            		   .append("   ,REQ_VAT INT                         ")
            		   .append("   ,RESCD INT                           ")
            		   .append("   ,APPR_AMT INT                        ")
            		   .append("   ,APPR_VAT INT                        ")
            		   .append("   ,INS_MTHS VARCHAR2(2)                ")
            		   .append("   ,SEQ INT                             ")
            		   .append("   ,CARDNO VARCHAR2(100)                ")
            		   .append("   ,REL_UID VARCHAR2(20)                ")
            		   .append("   ,ACS_YN VARCHAR2(2)                  ")
            		   .append("   ,TRNS_STR VARCHAR2(450)              ")
            		   .append("   ,INP_USR VARCHAR2(20)                ")
            		   .append("   ,INP_DTHMS DATE                      ")
            		   .append("   ,UDP_USR VARCHAR2(20)                ")
            		   .append("   ,UDP_DTHMS DATE                      ")
            		   .append("   )                                    ");
            
            stmt.execute(createTable.toString());

            // 카드 결제 관리번호 생성 관련 시퀀스 생성
            StringBuffer uIDSeq = new StringBuffer();
            uIDSeq.append("CREATE SEQUENCE SEQ_UID ")
                  .append("START WITH 1            ")
                  .append("INCREMENT BY 1          ")
                  .append("MAXVALUE 9999           ")
                  .append("MINVALUE 1              ")
                  .append("CYCLE                   ");
            
            stmt.execute(uIDSeq.toString());
            
            stmt.close();
            connection.commit();
            
        } catch (SQLException e) {
            System.out.println("Exception Message " + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }
    

}

package com.payment.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.payment.util.PaymentDBDriver;
import com.payment.vo.PaymentInfoVo;

public class PaymentDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(PaymentDAO.class);

    /**
     * 관리번호생성
     * @param vo
     * @return
     */
    public static String getUsefulCardNoYn(PaymentInfoVo vo)  throws SQLException {
        Connection connection = PaymentDBDriver.getDBConnection();
        Statement stmt = null;
        PreparedStatement pstmt = null;
        String result = "N";
        
        try {
            stmt = connection.createStatement();
            StringBuffer sql = new StringBuffer();
            
            sql.append(" SELECT COUNT(1) AS CNT        ");
            sql.append(" FROM TB_PAYMENT               ");
            sql.append(" WHERE CARDNO = ?		         ");
            sql.append(" AND ACS_YN = 'N'              ");
            
            //UID 생성 : SYSTIMESTATMP (YYYYMMDDHHMISSFF2) & 1~99999까지 CYCLE되는 SEQ의 조합
            pstmt = connection.prepareStatement(sql.toString());
            
            pstmt.setString(1, vo.getEncCardNo());
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
            	logger.info("CNT:" + rs.getInt("CNT"));
            	if(rs.getInt("CNT")>0) {
            		result = "N";
            	}else {
            		result = "Y";
            	}
            }
            
            
            stmt.close();
            
        } catch (SQLException e) {
            System.out.println("Exception Message: " + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
        
    	return result;
    }
    
    /**
     * 관리번호생성
     * @param vo
     * @return
     */
    public static PaymentInfoVo getUId(PaymentInfoVo vo)  throws SQLException {
        Connection connection = PaymentDBDriver.getDBConnection();
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            
            //UID 생성 : SYSTIMESTATMP (YYYYMMDDHHMISSFF2) & 1~99999까지 CYCLE되는 SEQ의 조합
            ResultSet rs = stmt.executeQuery(" SELECT (TO_CHAR(SYSTIMESTAMP, 'YYYYMMDDHHMISSFF2')||LPAD(SEQ_UID.NEXTVAL, 4, '0')) AS UID FROM DUAL ");
            
            while (rs.next()) {
            	logger.info("UID:" + rs.getString("UID"));
                vo.setuID(rs.getString("UID"));
            }
            
            stmt.close();
            
        } catch (SQLException e) {
            System.out.println("Exception Message " + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
        
    	return vo;
    }
    
    /**
     * 결제정보 적재
     * @param vo
     * @return
     */
    public static void insertPaymentInfo(PaymentInfoVo vo, int seq)  throws SQLException {
        Connection connection = PaymentDBDriver.getDBConnection();
        PreparedStatement pstmt = null;
        
        try {
        	connection.setAutoCommit(false);
            StringBuffer sql = new StringBuffer();
            
            sql.append("  INSERT INTO TB_PAYMENT (    									");
            sql.append("  UID, PAY_CLSS, REQ_AMT, REQ_VAT, RESCD, APPR_AMT, APPR_VAT,   ");
            sql.append("  INS_MTHS, SEQ, CARDNO, REL_UID, ACS_YN, TRNS_STR,     		");
            sql.append("  INP_USR, INP_DTHMS, UDP_USR, UDP_DTHMS    					");
			sql.append("  )VALUES(    													");
            sql.append("  ?, ?, ?, ?, ? , ? , ?,     									");
            sql.append("  ?, ?, ?, ?, ?, ?,     										");
            sql.append("  '9999999', SYSDATE, '9999999', SYSDATE)    					");

            
            //UID 생성 : SYSTIMESTATMP (YYYYMMDDHHMISSFF2) & 1~99999까지 CYCLE되는 SEQ의 조합
            pstmt = connection.prepareStatement(sql.toString());
            String clss = "00";
            if("PAYMENT".equals(vo.getClss())) {
            	clss = "00";
            	seq = 0;
            }else if ("CANCEL".equals(vo.getClss())) {
            	clss = "01";
            }
            
            int idx = 1;
            pstmt.setString(idx++, vo.getuID());
            pstmt.setString(idx++, clss);
            pstmt.setLong  (idx++, vo.getlReqAwamt());
            pstmt.setLong  (idx++, vo.getlReqVat());
            pstmt.setString(idx++, vo.getResCd());
            pstmt.setLong  (idx++, vo.getlApprAwamt());
            pstmt.setLong  (idx++, vo.getlApprVat());
            
            pstmt.setString(idx++, vo.getInsMths());
            pstmt.setInt   (idx++, seq);
            pstmt.setString(idx++, vo.getEncCardNo());
            pstmt.setString(idx++, vo.getRelUID());
            pstmt.setString(idx++, "Y");
            pstmt.setString(idx++, vo.getTrnsStr());
            
            pstmt.executeUpdate();
            
            pstmt.close();
            connection.commit();
            
        } catch (SQLException e) {
            System.out.println("Exception Message: " + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
        
    }
    
    /**
     * 결제정보조회
     * @param vo
     * @return
     */
    public static PaymentInfoVo getPayInfo(PaymentInfoVo vo)  throws SQLException {
        Connection connection = PaymentDBDriver.getDBConnection();
        PreparedStatement pstmt = null;
        
        try {
            StringBuffer sql = new StringBuffer();
            
            sql.append(" SELECT UID, TRNS_STR, PAY_CLSS, REQ_AMT, REQ_VAT, APPR_AMT, APPR_VAT, ACS_YN       ");
            sql.append(" FROM TB_PAYMENT               ");
            sql.append(" WHERE UID = ?		         ");
            
            pstmt = connection.prepareStatement(sql.toString());
            
            pstmt.setString(1, vo.getuID());
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
            	vo.setuID(rs.getString("UID"));
            	vo.setTrnsStr(rs.getString("TRNS_STR"));
            	vo.setClss(rs.getString("PAY_CLSS"));
            	vo.setlReqAwamt(rs.getLong("REQ_AMT"));
            	vo.setlReqVat(rs.getLong("REQ_VAT"));
            	vo.setlApprAwamt(rs.getLong("APPR_AMT"));
            	vo.setlApprVat(rs.getLong("APPR_VAT"));
            	if("Y".equals(rs.getString("ACS_YN"))) {
            		vo.setResCd("00");
            	}else {
            		vo.setResCd("04");
            	}
            }
            
            pstmt.close();
            
        } catch (SQLException e) {
            System.out.println("Exception Message: " + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
        
    	return vo;
    }
    
    /**
     * 결제정보 잠금/해제
     * @param vo
     * @return
     */
    public static void lockPaymentInfo(PaymentInfoVo vo, String clss)  throws SQLException {
        Connection connection = PaymentDBDriver.getDBConnection();
        PreparedStatement pstmt = null;
        
        try {
        	connection.setAutoCommit(false);
            StringBuffer sql = new StringBuffer();
            
            sql.append("  UPDATE TB_PAYMENT     			");
            sql.append("  SET ACS_YN = ?                    ");
            sql.append("  , UDP_USR = ?                     ");
            sql.append("  , UDP_DTHMS = SYSDATE             ");
            sql.append("  WHERE UID = ?                     ");

            
            pstmt = connection.prepareStatement(sql.toString());
            
            int idx = 1;
            pstmt.setString(idx++, clss);
            pstmt.setString(idx++, "9999999");
            pstmt.setString(idx++, vo.getuID());
            
            pstmt.executeUpdate();
            
            pstmt.close();
            connection.commit();
            
        } catch (SQLException e) {
            System.out.println("Exception Message: " + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
        
    }
    
    /**
     * 취소시퀀스 생성
     * @param vo
     * @return
     */
    public static int getCanRelSeq(PaymentInfoVo vo)  throws SQLException {
        Connection connection = PaymentDBDriver.getDBConnection();
        PreparedStatement pstmt = null;
        int seq = 0;

        try {
            StringBuffer sql = new StringBuffer();
            
            sql.append(" SELECT NVL(MAX(SEQ), 0)+1 AS SEQ  FROM TB_PAYMENT WHERE REL_UID = ?  ");
            
            pstmt = connection.prepareStatement(sql.toString());
            
            pstmt.setString(1, vo.getRelUID());
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
            	logger.info("UID:" + rs.getInt("SEQ"));
                seq = rs.getInt("SEQ");
            }
            
            pstmt.close();
            
        } catch (SQLException e) {
            System.out.println("Exception Message " + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
        
    	return seq;
    }
    
    /**
     * 원결제건 취소정보 업데이트
     * @param vo
     * @return
     */
    public static void updateCancelPaymentInfo(PaymentInfoVo vo)  throws SQLException {
        Connection connection = PaymentDBDriver.getDBConnection();
        PreparedStatement pstmt = null;
        
        try {
        	connection.setAutoCommit(false);
            StringBuffer sql = new StringBuffer();
            
            sql.append("  UPDATE TB_PAYMENT     			");
            sql.append("  SET ACS_YN = ?                    ");
            sql.append("  , APPR_AMT = ?                    ");
            sql.append("  , APPR_VAT = ?                    ");
            sql.append("  , UDP_USR = ?                     ");
            sql.append("  , UDP_DTHMS = SYSDATE             ");
            sql.append("  WHERE UID = ?                     ");

            
            pstmt = connection.prepareStatement(sql.toString());
            
            int idx = 1;
            pstmt.setString(idx++, "Y");
            pstmt.setLong(idx++, vo.getlApprAwamt());
            pstmt.setLong(idx++, vo.getlApprVat());
            pstmt.setString(idx++, "9999999");
            pstmt.setString(idx++, vo.getuID());
            
            pstmt.executeUpdate();
            
            pstmt.close();
            connection.commit();
            
        } catch (SQLException e) {
            System.out.println("Exception Message: " + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
        
    }
}

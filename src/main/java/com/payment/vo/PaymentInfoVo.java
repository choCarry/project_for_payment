package com.payment.vo;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

import com.payment.util.EncryptUtil;
import com.payment.util.StringUtil;

public class PaymentInfoVo {
	
	private String uID;
	private String relUID;
	private String clss;
	private String cardNo;
	private String insMths;
	private String cvc;
	private String validYm;
	private String sAwamt;
	private String sVat;
	private Long lReqAwamt;
	private Long lApprAwamt;
	private Long lReqVat;
	private Long lApprVat;
	private String encCardNo;
	private String encCardInfo;
	private String trnsStr;
	private String resCd;
	
	
	public String getuID() {
		return uID;
	}
	public void setuID(String uID) {
		this.uID = uID;
	}
	public String getRelUID() {
		return relUID;
	}
	public void setRelUID(String relUID) {
		this.relUID = relUID;
	}
	public String getClss() {
		return clss;
	}
	public void setClss(String clss) {
		this.clss = clss;
	}
	public String getCardNo() {
		return cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}
	public String getInsMths() {
		return insMths;
	}
	public void setInsMths(String insMths) {
		this.insMths = insMths;
	}
	public String getCvc() {
		return cvc;
	}
	public void setCvc(String cvc) {
		this.cvc = cvc;
	}
	public String getValidYm() {
		return validYm;
	}
	public void setValidYm(String validYm) {
		this.validYm = validYm;
	}
	public String getsAwamt() {
		return sAwamt;
	}
	public void setsAwamt(String sAwamt) {
		this.sAwamt = sAwamt;
	}
	public String getsVat() {
		return sVat;
	}
	public void setsVat(String sVat) {
		this.sVat = sVat;
	}
	
	public Long getlReqAwamt() {
		return lReqAwamt;
	}
	public void setlReqAwamt(Long lReqAwamt) {
		this.lReqAwamt = lReqAwamt;
	}
	public Long getlApprAwamt() {
		return lApprAwamt;
	}
	public void setlApprAwamt(Long lApprAwamt) {
		this.lApprAwamt = lApprAwamt;
	}
	public Long getlReqVat() {
		return lReqVat;
	}
	public void setlReqVat(Long lReqVat) {
		this.lReqVat = lReqVat;
	}
	public Long getlApprVat() {
		return lApprVat;
	}
	public void setlApprVat(Long lApprVat) {
		this.lApprVat = lApprVat;
	}
	public String getEncCardNo() {
		return encCardNo;
	}
	public void setEncCardNo(String encCardNo) {
		this.encCardNo = encCardNo;
	}
	public String getEncCardInfo() {
		return encCardInfo;
	}
	public void setEncCardInfo(String encCardInfo) {
		this.encCardInfo = encCardInfo;
	}
	public void setTrnsStr(String trnsStr) {
		this.trnsStr = trnsStr;
	}
	public String getTrnsStr() {
		return trnsStr;
	}
	public String getResCd() {
		return resCd;
	}
	public void setResCd(String resCd) {
		this.resCd = resCd;
	}
	
	
	//암호화 데이터 저장
	public void setEncCardInfo(PaymentInfoVo vo) {
		//STEP0. 암호화 대상 데이터 암호화(카드번호, 유효기간, cvc)
		EncryptUtil enc = new EncryptUtil();
		
		String resultData = "";
		try {
			resultData = vo.getCardNo()+"|"+vo.getValidYm()+"|"+vo.getCvc(); 
			vo.setEncCardNo(enc.encrypt(vo.getCardNo()));
			vo.setEncCardInfo(enc.encrypt(resultData));
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	
	//카드정보데이터 vo 채워주기
	public void setPaymentInfo(String clss, PaymentInfoVo vo) {
		if(vo.getsAwamt().trim().length() != 0 ) {
			vo.setlReqAwamt(StringUtil.getMoney(vo.getsAwamt()));
//			if("payment".equals(clss)) {
				vo.setlApprAwamt(StringUtil.getMoney(vo.getsAwamt()));
//			}
		}
		if(vo.getsVat().trim().length() != 0) {
			vo.setlReqVat(StringUtil.getMoney(vo.getsVat()));
			vo.setlApprVat(StringUtil.getMoney(vo.getsVat()));
		}else {
			//계산단계에서 세금에 값이 안 들어왔을 경우 자동계산 
			if("payment".equals(clss) || ("cancel".equals(clss)&& vo.getsAwamt().trim().length() != 0 )) {
				vo.setlReqVat(0L);
				vo.setlApprVat(Long.valueOf(Math.round(vo.getlReqAwamt()/11)));
			}
		}
		if("payment".equals(clss)) {
			setEncCardInfo(vo);
		}
	}
	
	
	
	
	

}

package com.payment.service;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.payment.dao.PaymentDAO;
import com.payment.util.EncryptUtil;
import com.payment.util.StringUtil;
import com.payment.vo.PaymentInfoVo;

@Service("PaymentService")
public class PaymentServiceImpl implements PaymentService{

	private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);
	
	@Override
	public Map<String, String> payment(PaymentInfoVo vo) {
		Map<String, String> resultMap = new HashMap<String, String>();
		String resCd = "00";
		String trnsStr  = "";
		try {
			// STEP0. DB SELECT (관리번호 생성 - 결재정상여부와 상관없이 history관리를 위해 무조건 채번)
			PaymentDAO.getUId(vo);
			
			// STEP1. DB SELECT (암호화된 카드번호로 사용중인 결제건이 있는 지 확인)
			if("N".equals(PaymentDAO.getUsefulCardNoYn(vo))) {
				resCd = "04";
				vo.setTrnsStr("");
			}else {
				//STEP2. 카드사 전달 데이터 생성
				trnsStr = StringUtil.makeTrnsStr(vo);
				vo.setTrnsStr(trnsStr);
				logger.info("transStr :: \n {}", trnsStr);
			}
			
			//STEP3. DB INSERT
			vo.setResCd(resCd);
			PaymentDAO.insertPaymentInfo(vo, 0);

		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		resultMap.put("resCd", resCd);
		
		
		return resultMap;
	}

	@Override
	public PaymentInfoVo cancel(PaymentInfoVo payVo, PaymentInfoVo canVo) {
		String resCd = "00";
		String trnsStr = "";
		try {
			// 해당 결제건 잠그기
			PaymentDAO.lockPaymentInfo(payVo, "N");
			
			// 취소 관리번호 채번
			PaymentDAO.getUId(canVo);
			
			// 금액 체크
			long apprPayAwamt = payVo.getlApprAwamt();  //결제금액(중 남은금액)
			long apprPayVat = payVo.getlApprVat();		//결제부가세(중 남은부가세)
			
			long apprCanAwamt = canVo.getlApprAwamt();	//결제취소요청금액
			long apprCanVat = canVo.getlApprVat(); 		//결제취소요청부가세
			
			String vatClss = "";
			if(canVo.getsVat() == null || canVo.getsVat().trim().length() == 0) {
				vatClss = "auto"; //자동계산
			}else {
				vatClss = "man"; //자동계산
			}
			
			//취소금액이 남은금액전체이고 자동계산된 부가세가 남은부가세와 다를 경우 취소부가세를 맞춰준다
			if("auto".equals(vatClss) && (apprPayAwamt == apprCanAwamt && apprPayVat!=apprCanVat)) {
				apprCanVat = apprPayVat;
				canVo.setlApprVat(apprCanVat);
			}
			
			//인정금액이 취소금액보다 클 경우
			if(apprPayAwamt<apprCanAwamt){	
				resCd = "02";	//결제취소금액오류
			}
			// 결제취소금액이나 세금이 결제금액/세금보다 클 경우
			else if(apprPayVat<apprCanVat || (apprPayAwamt == apprCanAwamt && apprPayVat!=apprCanVat)){
				resCd = "03";	//결제취소세금오류
			}
			
			EncryptUtil encUtil = new EncryptUtil();
			// 카드사 통신 데이터 생성을 위해 기존 결제정보에서 카드정보를 가져온다 
			String cardNo  = payVo.getTrnsStr().substring(34, 54);
			String vliadYm = payVo.getTrnsStr().substring(56, 60);
			String cvc     = payVo.getTrnsStr().substring(60, 63);
			String enc     = payVo.getTrnsStr().substring(103,403);
			String encCardNo = "";
			
			try {
				encCardNo = encUtil.encrypt(cardNo);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (GeneralSecurityException e) {
				e.printStackTrace();
			}
			
			canVo.setCardNo(cardNo);
			canVo.setEncCardNo(encCardNo);
			canVo.setValidYm(vliadYm);
			canVo.setCvc(cvc);
			canVo.setEncCardInfo(enc);
			canVo.setInsMths("00");
			canVo.setResCd(resCd);
			// 카드사 통신 데이터생성
			trnsStr = StringUtil.makeTrnsStr(canVo);
			canVo.setTrnsStr(trnsStr);
			logger.info("transStr::{}\n", trnsStr);
			
			// 취소 데이터 seq 채번 
			int seq = PaymentDAO.getCanRelSeq(canVo);
			
			// 취소데이터 insert
			PaymentDAO.insertPaymentInfo(canVo, seq);
			
			
			// 기존 결제건 인정금액 update & 잠금거 풀기
			long finalAwamt = 0L;
			long finalVat = 0L;
			
			if("00".equals(resCd)) {
				finalAwamt = apprPayAwamt - apprCanAwamt; 
				finalVat = apprPayVat-apprCanVat;
				
				payVo.setlApprAwamt(finalAwamt);
				payVo.setlApprVat(finalVat);
				
				PaymentDAO.updateCancelPaymentInfo(payVo);
			}else {
				PaymentDAO.lockPaymentInfo(payVo, "Y");
			}
			
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return canVo;
	}

	@Override
	public PaymentInfoVo inquiry(PaymentInfoVo vo) {
		try {
			PaymentDAO.getPayInfo(vo);
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return vo;
	}

}

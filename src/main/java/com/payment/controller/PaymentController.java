package com.payment.controller;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.payment.service.PaymentService;
import com.payment.util.EncryptUtil;
import com.payment.vo.PaymentInfoVo;

/**
 * Handles requests for the application home page.
 */
@Controller
public class PaymentController {
	
	private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
	

	@Autowired
	private PaymentService paymentService;
	
	
	/**
	 * 결제모듈
	 * @param locale
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/payment.do", method = RequestMethod.POST ,produces = "application/json")
	public @ResponseBody Map<String, String> payment(Locale locale, Model model, HttpServletRequest request ) {

		Map<String, String> map = new HashMap<String, String>();
		String resCd = "00";
		
		PaymentInfoVo vo = new PaymentInfoVo();
		
		//파라미터 할당
		vo.setCardNo( request.getParameter("cardNo"));
		vo.setValidYm(request.getParameter("validYm"));
		vo.setCvc(request.getParameter("cvc"));
		vo.setInsMths(request.getParameter("insMths"));
		vo.setsAwamt(request.getParameter("sAwamt"));
		vo.setsVat(request.getParameter("sVat"));
		vo.setClss("PAYMENT");
		vo.setPaymentInfo("payment", vo);
		
		SimpleDateFormat dataFormat2 = new SimpleDateFormat("MMyy");
		logger.info("dataformat2 : {}", dataFormat2);
		int thisMonthYear = Integer.parseInt(dataFormat2.format(new Date()));
		
		try {
			//입력값 체크
			if(vo.getCardNo().trim().length() != 16
					|| vo.getValidYm().trim().length() != 4
					|| vo.getCvc().trim().length()!=3
					|| vo.getsAwamt().trim().length() == 0
					|| vo.getInsMths().trim().length() == 0
					|| (Integer.parseInt(vo.getInsMths()) < 0 )
					|| (Integer.parseInt(vo.getInsMths()) > 12 )
					|| Integer.parseInt(vo.getValidYm()) < thisMonthYear
					) {
				resCd = "77"; //유효성오류
			}
			//금액체크(세금이 결제금액보다 클 수 없다, 결제금액은 100~10억 사이 )
			if(vo.getlReqAwamt()< 100 || vo.getlReqAwamt() > 1000000000) {
				resCd = "02";
			}
			if(vo.getlReqVat() > vo.getlReqAwamt() || vo.getlReqAwamt()< 100 || vo.getlReqAwamt() > 1000000000) {
				resCd = "03";
			}
		}catch(Exception e) {
			logger.info(e.getMessage());
			resCd = "99";
		}
		
		//입력오류(화면단 기 유효성체크부) 외 서비스단 제어(db인입후 처리 진행)
		if("00".equals(resCd)) {
			map = paymentService.payment(vo);
		}else {
			map.put("resCd", resCd);
		}
		
		if("00".equals(map.get("resCd"))) {
			map.put("uid", vo.getuID());
			map.put("trnsStr", vo.getTrnsStr());
		}

		return map;
	}
	
	/**
	 * 결제취소
	 * @param locale
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/cancel.do", method = RequestMethod.POST)
	public @ResponseBody Map<String, String> cancel(HttpServletRequest request ) {
		
		Map<String, String> map = new HashMap<String, String>();
		
		logger.info("::::::::cancel::::::::::");
		String uid = request.getParameter("uid");			// 관리번호
		String cAwmat = request.getParameter("t_cawamt");	// 취소금액
		String cVat = request.getParameter("t_cvat");		// 부가가치세
		
		PaymentInfoVo payVo = new PaymentInfoVo();				// 결제건 vo
		PaymentInfoVo canVo = new PaymentInfoVo();			// 결제취소건 vo
		payVo.setuID(uid);
		
		canVo.setRelUID(uid);
		canVo.setsAwamt(cAwmat);
		canVo.setsVat(cVat);
		canVo.setClss("CANCEL");
		canVo.setPaymentInfo("cancel", canVo);
		
		// 사용 데이터 조회(결제vo)
		paymentService.inquiry(payVo);
		
		if(canVo.getsAwamt().trim().length() == 0) {
			canVo.setlReqAwamt(payVo.getlApprAwamt());
			canVo.setlApprAwamt(payVo.getlApprAwamt());
			canVo.setlReqVat(0L);
			canVo.setlApprVat(payVo.getlApprVat());
		}
		
		// 사용데이터가 있다면 해당 데이터 잠금(이중접근제한)
		if(payVo.getlReqAwamt()==null) {
			map.put("resCd", "05");
		}else if(!"00".equals(payVo.getResCd())){
			map.put("resCd", payVo.getResCd());
		}else {
			paymentService.cancel(payVo, canVo);
			map.put("resCd", canVo.getResCd());
		}
		
		map.put("uid", canVo.getuID());
		map.put("trnsStr", canVo.getTrnsStr());

		
		return map;
	}
	

	/**
	 * 결제조회
	 * @param locale
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/inquiry.do", method = RequestMethod.POST)
	public @ResponseBody Map<String, String> inquiry(HttpServletRequest request ) {
		
		EncryptUtil enc = new EncryptUtil();

		String uid = request.getParameter("uid");
		
		Map<String, String> map = new HashMap<String, String>();
		
		PaymentInfoVo vo = new PaymentInfoVo();
		vo.setuID(uid);
		
		paymentService.inquiry(vo);

		if(vo.getlReqAwamt()==null) {
			map.put("inquiryYn", "N");
		}else {
			String trnsStr = vo.getTrnsStr();
			String encCardInfo = trnsStr.substring(103,403);
			String decCardInfo = "";
			String cardNo = "";
			String validYm = "";
			String cvc = "";
			String clss = "";
					
			try {
				decCardInfo = enc.decrypt(encCardInfo);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (GeneralSecurityException e) {
				e.printStackTrace();
			}
			logger.info("encCardInfo:{}", encCardInfo);
		
			String[] cardInfos = decCardInfo.split("\\|");
			cardNo = cardInfos[0];
			validYm= cardInfos[1];
			cvc = cardInfos[2];
			
			String cardNo1 = cardNo.substring(0,4);
			String cardNo2 = cardNo.substring(4,6);
			String cardNo4 = cardNo.substring(13,16);
			cardNo = cardNo1 + "-" + cardNo2 + "**-"+"****-*"+cardNo4;
			
			if("00".equals(vo.getClss())) {
				clss = "결제";
			}else if("01".equals(vo.getClss())) {
				clss = "결제취소";
			}
			

			map.put("uid", vo.getuID());
			map.put("cardNo", cardNo);
			map.put("validYm", validYm.substring(0,2)+"/"+validYm.substring(2,4));
			map.put("cvc", cvc);
			map.put("clss", clss);
			map.put("reqAwamt", Long.toString(vo.getlReqAwamt()));
			map.put("reqvat", Long.toString(vo.getlReqVat()));
			map.put("apprAwamt", Long.toString(vo.getlApprAwamt()));
			map.put("apprvat", Long.toString(vo.getlApprVat()));
			map.put("inquiryYn", "Y");			
		}
		
		
		return map;
	}
}

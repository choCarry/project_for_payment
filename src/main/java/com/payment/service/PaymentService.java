package com.payment.service;

import java.util.Map;

import com.payment.vo.PaymentInfoVo;

public interface PaymentService {

	/**
	 * 결제
	 * @param vo
	 * @return
	 */
	Map<String, String> payment(PaymentInfoVo vo);

	/**
	 * 결제취소 
	 * @param vo 결제정보 vo
	 * @param vo2 결제취소정보 vo
	 * @return
	 */
	PaymentInfoVo cancel(PaymentInfoVo vo, PaymentInfoVo vo2);

	/**
	 * 결제정보조회
	 * @param vo
	 * @return
	 */
	PaymentInfoVo inquiry(PaymentInfoVo vo);
}

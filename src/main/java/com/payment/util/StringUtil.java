package com.payment.util;

import com.payment.vo.PaymentInfoVo;

public class StringUtil {

	
	public static String nvl(String str1, String str2) {
		if(str1 == null || str1.trim() == null || "".equals(str1)) {
			return str2;
		}
		return str1;
	}
	
	
	public static long getMoney(String sAwamt) {
		long result = 0L;
		sAwamt = sAwamt.replaceAll(",", "");
		try {
			result = Long.valueOf(sAwamt);
		}catch(NumberFormatException e) {
			e.printStackTrace();
			return 0L;
		}
		
		return result;		
	}
	
	//우정렬
	public static String lpad(String value, int length, String prefix) {
		try {
			StringBuilder sb = new StringBuilder();
			String castValue = value;

			for (int i = castValue.length(); i< length; i++) {
				sb.append(prefix);
			}
			sb.append(castValue);
			return sb.toString();
		} catch (Exception e) {
			return "";
		}
	}
	
	//좌정렬
	public static String rpad(String value, int length, String prefix) {
		try {
			StringBuilder sb = new StringBuilder();
			String castValue = value;
			sb.append(castValue);
			for (int i = castValue.length(); i< length; i++) {
				sb.append(prefix);
			}
			return sb.toString();
		} catch (Exception e) {
			return "";
		}
	}


	//카드사 전달용 전달 데이터 만들기 
	public static String makeTrnsStr(PaymentInfoVo vo) {
		String result = "";
		
		//공통헤더부분
		int totLength = 446; 		//0: 본 컬럼을 제외한 총길이(4) 	(우정렬 빈자리 공백)
//		String clss = "";			//1: 데이터구분 (10)			(좌정렬 빈자리 공백)
//		String uID = ""; 			//2: uniqe id(20)			(좌정렬 빈자리 공백)
//		
//		//데이터부분
//		String cardNo = "";			//0: 카드번호(20)				(좌정렬 빈자리 공백)
//		String insMths = "";		//1: 할부개월(2)				(우정렬 빈자리 0)
//		String validYm = "";		//2: 카드유효기간(4)			(좌정렬 빈자리 공백)
//		String cvc = "";			//3: cvc(3)					(좌정렬 빈자리 공백)
//		String awamt = "";			//4: 거래금액(10)				(우정렬 빈자리 공백)
//		String vat = "";			//5: 부가가치세(10)				(우정렬 빈자리 0)
//		String originUID = "";		//6: 원래 관리번호(20)			(좌정렬 빈자리 공백)
//		String endCardInfo = "";	//7: 암호화된카드번호(300)		(좌정렬 빈자리 공백 구분자|)
//		String tmp = "";			//8: 예비필드(47)				(좌정렬 빈자리 공백)
		
		StringBuffer trns = new StringBuffer();
		
		//헤더부 데이터 추가
		trns.append(lpad(Integer.toString(totLength),  4, " "));
		trns.append(rpad(vo.getClss()               , 10, " "));
		trns.append(rpad(vo.getuID()                , 20, " "));
		
		//데이터부 데이터 추가
		if("PAYMENT".equals(vo.getClss())) {
			trns.append(rpad(vo.getCardNo()             ,  20, " "));  //0: 카드번호(20)			
			trns.append(lpad(vo.getInsMths()            ,   2, "0"));  //1: 할부개월(2)			
			trns.append(rpad(vo.getValidYm()            ,   4, " "));  //2: 카드유효기간(4)			
			trns.append(rpad(vo.getCvc()                ,   3, " "));  //3: cvc(3)				
			trns.append(lpad(vo.getlReqAwamt()+""       ,  10, " "));  //4: 거래금액(10)			
			trns.append(lpad(vo.getlReqVat()+""         ,  10, "0"));  //5: 부가가치세(10)			
			trns.append(rpad(""                         ,  20, " "));  //6: 원래 관리번호(20)			
			trns.append(rpad(vo.getEncCardInfo()        , 300, " "));  //7: 암호화된카드번호(300)		
			trns.append(rpad(""                         ,  47, " "));  //8: 예비필드(47)			
		}else if ("CANCEL".equals(vo.getClss())) {
			trns.append(rpad(vo.getCardNo()             ,  20, " "));  //0: 카드번호(20)			
			trns.append(lpad(vo.getInsMths()            ,   2, "0"));  //1: 할부개월(2)			
			trns.append(rpad(vo.getValidYm()            ,   4, " "));  //2: 카드유효기간(4)			
			trns.append(rpad(vo.getCvc()                ,   3, " "));  //3: cvc(3)				
			trns.append(lpad(vo.getlApprAwamt()+""      ,  10, " "));  //4: 거래금액(10)			
			trns.append(lpad(vo.getlApprVat()+""        ,  10, "0"));  //5: 부가가치세(10)			
			trns.append(rpad(vo.getRelUID()             ,  20, " "));  //6: 원래 관리번호(20)			
			trns.append(rpad(vo.getEncCardInfo()        , 300, " "));  //7: 암호화된카드번호(300)		
			trns.append(rpad(""                         ,  47, " "));  //8: 예비필드(47)			
		}
		
		result = trns.toString();
		
		return result;
	}
	
	
	
	
}

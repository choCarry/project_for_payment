<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<link rel="stylesheet" href="<c:url value='/resources/css/table.css'/>">
<link rel="shortcut icon" href="#">
<head>
<meta charset="UTF-8">
	<title>결제시스템</title>
<script src="https://code.jquery.com/jquery-3.4.1.js" integrity="sha256-WpOohJOqMqqyKL9FccASB9O0KwACQJpFTUBLTYOVvVU=" crossorigin="anonymous"></script>	
<script type="text/javascript">
$(function() {
	for(var i = 1; i<13; i++){
		var t = "0";
		if(i<10){
			t = "0"+i
		}else{
			t= i
		}
		var option = "<option>"+t+"</option>";
		$("#s_insmths").append(option);
		$("#s_validmm").append(option);
	}
	
	var thisYy = Number(String(new Date().getFullYear()).substr(2,2));
	var thisMm = String(new Date().getMonth() + 1);
	if(thisMm.length==1){
		thisMm = "0"+thisMm;
	}
	$("#s_validmm").val(thisMm);
	
	for (var j = 0 ; j < 6; j++){
		var option = $("<option>"+(thisYy+j)+"</option>");
		$("#s_validyy").append(option);		
	}

	//키를 누르거나 떼었을때 이벤트 발생
	$(".money").bind('keyup keydown',function(){
	    inputNumberFormat(this);
	});
	
	//maxlength 를 채웠을 때 다음 text칸으로 이동
	$("input").bind('keyup', function(){
		if(this.value.length == this.maxLength && this.nextElementSibling != null){
			this.nextElementSibling.focus();
		}
	});
		
	setTab('1');
	
	
});


//화면 관련 메서드들
function checkInputNum(){
  if ((event.keyCode < 48) || (event.keyCode > 57)){
      event.returnValue = false;
  }
}

function setTab(clss){
	if(clss == '1'){
		$("#t_payment").addClass("sel");
		$("#t_cancel").removeClass("sel");
		$(".payment").show();
		$(".cancel").hide();
	}else if(clss = '2'){
		$("#t_payment").removeClass("sel");
		$("#t_cancel").addClass("sel");
		$(".payment").hide();
		$(".cancel").show();		
	}
}

//nvl
function nvl(a,b){
	if(a == '' || a == 'undefined'){
		return b;
	} else {
		return a;
	}
}
	
//입력한 문자열 전달
function inputNumberFormat(obj) {
    obj.value = addComma(uncomma(obj.value));
}
  
//콤마찍기
function addComma(str) {
    str = String(str);
    return str.replace(/(\d)(?=(?:\d{3})+(?!\d))/g, '$1,');
}

//콤마풀기
function uncomma(str) {
    str = String(str);
    return str.replace(/[^\d]+/g, '');
}


//결제 
function payment(){
 	if(payment_input_check()){
 		var param = {
 				cardNo : $("#t_cardno1").val()+$("#t_cardno2").val()+$("#t_cardno3").val()+$("#t_cardno4").val()
 				,validYm : $("#s_validmm").val()+$("#s_validyy").val()
 				,cvc : $("#t_cvc").val()
 				,insMths: $("#s_insmths").val()
 				,sVat: $("#t_vat").val()
 				,sAwamt:$("#t_awamt").val()
 		}
	 	$.ajax({
			type: "POST",
			url : "<c:url value='/payment.do' />",
			data: param,
			async: true,
			success : function(data, status, xhr) {
				if(data.resCd == '00'){
					alert("결제되었습니다.");
					$("#resultMsg1").html("관리번호:" + data.uid);
					$("#resultMsg2").html("카드사 전달코드 </br>" + data.trnsStr);
				}else{
					alert("결제가 실패하였습니다["+data.resCd+"]");
				}
			},
			error: function(jqXHR, textStatus, errorThrown) {
				alert(jqXHR.responseText);
			}
		});
	} 
}

// 결제 유효성 체크
function payment_input_check(){
	
	var c = true;
 	$(".nes").each(function(index, item){
 		if(item.value.trim() == null || item.value.trim() == ''){
 			alert(item.alt + "은(는) 필수 입력 요소입니다. 값을 확인해주세요.");
 			c = false;
 			return false;
 		}
 	});
 	if(!c){
 		return false;
 	}
 	
 	var validYymm = "20" + $("#s_validyy").val() + $("#s_validmm").val();
 	var today = new Date();
 	var year = String(today.getFullYear()); // 년도
 	var month = String(today.getMonth() + 1);  // 월
 	if(month.length==1){
 		month = "0"+month;
 	}
 	
 	if(Number(validYymm)< Number(year+month)){
		alert("유효기간을 확인해주세요.");
		return false;
 	}
 	
  	
 	var awamt = Number(uncomma($("#t_awamt").val())); //결제금액
 	var vat = Number(nvl(uncomma($("#t_vat").val()), 0)); //부가가치세
 	if(awamt < 100 || awamt > 1000000000){
 		alert("결제금액을 확인해주세요.(100원이상 , 10억이하)");
 		$("#t_awamt").focus();
 		return false;
 	}
 	if($("#t_vat").val() != '' && (vat > awamt )){
 		alert("부가가치세를 확인하세요. 결제금액보다 클 수 없습니다.");
 		$("#t_vat").focus();
 		return false;
 	}
 	

 	return true;	
}
//결제취소
function cancel(){
	var prog = true;
	if($("#t_uid").val().trim() == '' ){
		alert("관리번호를 확인해주세요.");
		return;
	}
	if($("#t_cawamt").val().trim() == ''){
		prog = confirm("취소금액을 적지 않을 시 전체 금액이 취소됩니다. 진행하시겠습니까?");
	}
	
	if(prog){
 		var param = {
 				uid : $("#t_uid").val()
 				,t_cawamt : $("#t_cawamt").val()
 				,t_cvat: $("#t_cvat").val()
 		}
	 	$.ajax({
			type: "POST",
			url : "<c:url value='/cancel.do' />",
			data: param,
			async: true,
			success : function(data, status, xhr) {
				if(data.resCd == '00'){
					alert("결제가 취소되었습니다.");
					$("#resultMsg1").html("관리번호:" + data.uid);
					$("#resultMsg2").html("카드사 전달코드 </br>" + data.trnsStr);
				}else{
					alert("결제취소가 실패하였습니다["+data.resCd+"]");
				}
			},
			error: function(jqXHR, textStatus, errorThrown) {
				alert(jqXHR.responseText);
			}
		});
	}
	
}
//조회
function inquiry(){
	if($("#t_uid").val().trim() == '' ){
		alert("관리번호를 확인해주세요.");
		return;
	}
	var param = {
		uid : $("#t_uid").val()
	}	
 	$.ajax({
		type: "POST",
		url : "<c:url value='/inquiry.do' />",
		data: param,
		async: true,
		success : function(data, status, xhr) {
			if(data.inquiryYn == 'Y'){
				console.log(data);
				$("#resultMsg1").html("관리번호:" + data.uid + "</br>");
				$("#resultMsg1").append("카드번호:" + data.cardNo + "</br>");
				$("#resultMsg1").append("유효기간:" + data.validYm + "</br>");
				$("#resultMsg1").append("cvc   :" + data.cvc + "</br>");
				$("#resultMsg1").append("결제구분:" + data.clss + "</br>");
				$("#resultMsg1").append("요청금액:" + addComma(data.reqAwamt) + "원</br>");
				$("#resultMsg1").append("요청부가세:" + addComma(data.reqvat) + "원</br>");
				$("#resultMsg1").append("인정금액:" + addComma(data.apprAwamt) + "원</br>");
				$("#resultMsg1").append("인정부가세:" + addComma(data.apprvat) + "원</br>");
				
				$("#resultMsg2").html("");
			}else{
				alert("조회된 건이 없습니다.");
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			alert(jqXHR.responseText);
		}
	});
}


</script> 	
</head>
<body>
<table class="type09">
	<thead>
	<tr>
		<th scope="cols" id="t_payment" onclick="javascript:setTab('1');">카드결제</th>
		<th scope="cols" id="t_cancel" onclick="javascript:setTab('2');">카드취소/조회</th>
		<th scope="cols" ></th>
	</tr>
	</thead>
	<tbody class="payment">
	<tr>
		<th scope="row">카드번호</th>
		<td colspan = "2">
			<input type="text" name="t_cardno1" id="t_cardno1" style="width:18%" maxlength="4" class="nes" onkeyPress="javascript:checkInputNum();"  alt="카드번호1"> -
			<input type="text" name="t_cardno2" id="t_cardno2" style="width:18%" maxlength="4" class="nes" onkeyPress="javascript:checkInputNum();"  alt="카드번호2"> -
			<input type="text" name="t_cardno3" id="t_cardno3" style="width:18%" maxlength="4" class="nes" onkeyPress="javascript:checkInputNum();"  alt="카드번호3"> -
			<input type="text" name="t_cardno4" id="t_cardno4" style="width:18%" maxlength="4" class="nes" onkeyPress="javascript:checkInputNum();"  alt="카드번호4">
		</td>
	</tr>
	<tr>
		<th scope="row">유효기간</th>
		<td colspan = "2">
			<select id="s_validmm" name="s_validnm"></select> / 
			<select id="s_validyy" name="s_validyy"></select> MM/YY
		</td>
	</tr>
	<tr>
		<th scope="row">cvc</th>
		<td colspan = "2">
			<input type="text" name="t_cvc" id="t_cvc" style="width:18%" maxlength="3" class="nes" onkeyPress="javascript:checkInputNum();" alt="cvc">	
		</td>
	</tr>
	<tr>
		<th scope="row">할부개월수</th>
		<td colspan = "2">
			<select id="s_insmths">
				<option value="0">일시불</option>
			</select>
			개월
		</td>
	</tr>
	<tr>
		<th scope="row">결제금액</th>
		<td colspan = "2">
			<input type="text" name="t_awamt" id="t_awamt" style="width:50%" maxlength="12" class="money nes" onkeyPress="javascript:checkInputNum();" alt="결제금액"> 원
		</td>
	</tr>
	<tr>
		<th scope="row">부가가치세</th>
		<td colspan = "2">
			<input type="text" name="t_vat" id="t_vat" style="width:50%" maxlength="12" class="money" onkeyPress="javascript:checkInputNum();" alt="부가가치세"> 원
		</td>
	</tr>
	</tbody>
	<tbody class="cancel">
	<tr>
		<th scope="row">관리번호</th>
		<td colspan = "2">
			<input type="text" name="t_uid" id="t_uid" style="width:55%" maxlength="20" onkeyPress="javascript:checkInputNum();">
		</td>
	</tr>
	<tr>
		<th scope="row">결제취소금액</th>
		<td colspan = "2">
			<input type="text" name="t_cawamt" id="t_cawamt" style="width:50%" maxlength="12" class="money" onkeyPress="javascript:checkInputNum();"> 원 (*조회 시 무시)
		</td>
	</tr>
	<tr>
		<th scope="row">결제취소부가가치세</th>
		<td colspan = "2">
			<input type="text" name="t_cvat" id="t_cvat" style="width:50%" maxlength="12" class="money" onkeyPress="javascript:checkInputNum();"> 원 (*조회 시 무시)
		</td>
	</tr>
	</tbody>
</table>
	<a href="#" class="button payment" name="test1" id="test1" onclick="javascript:payment();">결제</a>
	<a href="#" class="button cancel" name="test2" id="test2" onclick="javascript:cancel();">결제취소</a>
	<a href="#" class="button cancel" name="test3" id="test3" onclick="javascript:inquiry();">조회</a>
	</br>
	<b id="resultMsg1"></b></br>
	<b id="resultMsg2"></b>
</body>
</html>

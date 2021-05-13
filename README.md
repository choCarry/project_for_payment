# project_for_payment
payment pj 

<h4 >주제 : 결제요청을 받아 카드사와 통신하는  인터페이스를 제공하는 결제시스템</h4>
<hr />
<ul>
<li><h5 >사용 프레임워크 : String Framework</h5>
</li>
<li><h5 >사용 DB : H2</h5>
</li>

</ul>
<hr />
<h5 >■ 테이블 설계</h5>
<h6 >TABLE NAME : TB_PAYMENT</h6>
<figure><table>
<thead>
<tr><th>COLUMN</th><th>NAME</th><th>TYPE</th><th>LEN</th><th>DESCRIPTION</th></tr></thead>
<tbody><tr><td>UID</td><td>관리번호</td><td>String</td><td>20</td><td>관리번호</td></tr><tr><td>PAY_CLSS</td><td>결제구분</td><td>String</td><td>2</td><td>01:  PAYMENT     02: CENCEL   </td></tr><tr><td>REQ_AMT</td><td>요청금액</td><td>long</td><td>10</td><td>&nbsp;</td></tr><tr><td>REQ_VAT</td><td>요청부가가치세</td><td>long</td><td>10</td><td>&nbsp;</td></tr><tr><td>RESCD</td><td>응답코드</td><td>int</td><td>2</td><td>00: 정상<br>01: 서비스호출실패<br>02: 결제금액오류<br>03: 결제세금오류<br>04: 동시작업오류<br>05: 결제id오류<br>77: 유효성오류<br>81: TIMEOUT<br>99: 기타오류</td></tr><tr><td>APPR_AMT</td><td>인정금액</td><td>long</td><td>10</td><td>&nbsp;</td></tr><tr><td>APPR_VAT</td><td>인정부가가치세</td><td>long</td><td>10</td><td>&nbsp;</td></tr><tr><td>INS_MTHS</td><td>할부개월</td><td>String</td><td>2</td><td>0~12</td></tr><tr><td>SEQ</td><td>순서</td><td>&nbsp;</td><td>&nbsp;</td><td>0  : 결제      1~ : 취소 순서 ESC</td></tr><tr><td>CARDNO</td><td>카드번호</td><td>String</td><td>40</td><td>암호화된카드번호</td></tr><tr><td>REL_UID</td><td>관련관리번호</td><td>String</td><td>20</td><td>취소  시 사용      결제 관리번호</td></tr><tr><td>ACS_YN</td><td>접근가능여부</td><td>String</td><td>1</td><td>Y:  접근가능      N: 접근불가</td></tr><tr><td>TRNS_STR</td><td>전달문자열</td><td>String</td><td>450</td><td>카드사 전달 문자열</td></tr><tr><td>INP_USR</td><td>입력인</td><td>String</td><td>20</td><td>입력  CLIENT IP</td></tr><tr><td>INP_DTHMS</td><td>입력일시</td><td>DATE</td><td>&nbsp;</td><td>SYSDATE</td></tr><tr><td>UDP_USR</td><td>수정인</td><td>String</td><td>20</td><td>수정  CLIENT IP</td></tr><tr><td>UDP_DTHMS</td><td>수정일시</td><td>DATE</td><td>&nbsp;</td><td>SYSDATE</td></tr></tbody>
</table></figure>
<p>&nbsp;</p>
<h5 >■ 문제해결전략 </h5>
<ul>
<li><p>주 이슈 </p>
<ul>
<li><p>카드정보가 db에 평문으로 저장되는 부분이 존재.  카드사에 전달되는 평문형태의 데이터가 필요한 것으로 다른 데이터들 사이에 숨어있고 무엇보다 니즈사항이기 때문에 감안하고 작업하였습니다.</p>
</li>
<li><p>결제금액이 1,000원 일 때, 부가가치세는 0원일 수 있다고 안내되어있는데, 나머지 사항에 대한 안내가 없어 협의를 하지 못하고 개인 상상력으로 진행한 점이 아쉬웠습니다.</p>
</li>
<li><p>시간분배를 잘못하여 충분한 안내주석을 남기지 못했습니다. </p>
<p>&nbsp;</p>
</li>

</ul>
</li>

</ul>
<h5 >■ 빌드 및 실행방법</h5>
<h6 >1. 이클립스 설치 </h6>
<h6 >2. 서버 내 톰캣 추가</h6>
<h6 >3. 사용가능한 tomcat을 누르고 next -&gt; 프로젝트를 선택 후 Add버튼을 눌러 오른쪽으로 이동한 것을 확인하고 finish 버튼을 누릅니다</h6>
<h6 >4. 만들어진 서버를 더블클릭 후 왼쪽 하단에 module탭을 누릅니다. paymentPj row를 선택 후 오른쪽 Edit버튼을 눌러줍니다 </h6>
<h6 >5. Path부분을 / 로 바꾼 후 확인을 눌러줍니다</h6>
<h6 >6. 서버 탭 오른쪽 위의 플레이버튼을 눌러줍니다</h6>
<h6 >7. 인터넷창에서 <a href='http://localhost:8080' target='_blank' class='url'>http://localhost:8080</a> 을 통해 화면을 확인합니다. </h6>

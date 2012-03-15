package kr.ac.hansung;

//Card Information Class
public class SmsInfo {
	private String cardName;		//카드 이름
	private String approvalType;	//결재 종류 (체크승인, 신용승인 등)
	private String price;			//결재 가격
	private String cardNumber;		//카드 번호
	private String approvalTime;	//결재 일시
	private String place;			//결재 장소
	
	public String getCardName() { return cardName; }
	public String getApprovalType() { return approvalType; }
	public String getPrice() { return price; }
	public String getCardNumber() { return cardNumber; }
	public String getApprovalTime() { return approvalTime; }
	public String getPlace() { return place; }
	
	public void setCardName(String cName) { cardName = cName; }
	public void setApprovalType(String aType) { approvalType = aType; }
	public void setPrice(String _price) { price = _price; }
	public void setCardNumber(String cNum) { cardNumber = cNum; }
	public void setApprovalTime(String aTime) { approvalTime = aTime; }
	public void setPlace(String _place) { place = _place; }

	public SmsInfo(String cardName) {
		setCardName(cardName);
	}
	
	public SmsInfo(String cardName, String cardNumber) {
		setCardName(cardName);
		setCardNumber(cardNumber);
	}
}

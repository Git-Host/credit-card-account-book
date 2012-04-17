package kr.ac.hansung;

import java.util.Date;


//Card Information Class
public class SmsInfo {
	private String cardName;		//카드 이름
	private String approvalType;	//결재 종류 (체크승인, 신용승인 등)
	private int price;				//결재 가격
	private String cardNumber;		//카드 번호
	private String approvalTime;	//결재 일시
	private String place;			//결재 장소
	private String category;
	
	public String getCardName() { return cardName; }
	public String getApprovalType() { return approvalType; }
	public int getPrice() { return price; }
	public String getCardNumber() { return cardNumber; }
	public String getApprovalTime() { return approvalTime; }
	public String getPlace() { return place; }
	public String getCategory(){ return category; }
	
	public void setCardName(String cName) { cardName = cName; }
	public void setApprovalType(String aType) { approvalType = aType; }
	public void setPrice(int _price) { price = _price; }
	public void setCardNumber(String cNum) { cardNumber = cNum; }
	public void setApprovalTime(String aTime) { approvalTime = aTime; }
	public void setPlace(String _place) { place = _place; }
	public void setCategory(String _category){category = _category;}

	public SmsInfo() {}
	
	public SmsInfo(String cardName) {
		setCardName(cardName);
	}
	
	public SmsInfo(String approvalTime, String cardName, String place, int price) {
		this.approvalTime = approvalTime;
		this.cardName = cardName;
		this.place = place;
		this.price = price;
	}
	
	public SmsInfo splitSMSAddToSmsInfo(String sms) {
		String[] tmpSMS = sms.split("\n");
		SmsInfo tmpSmsObj = new SmsInfo();
		String[] tmpSplitCardNameNumber = splitCardNameNumber(tmpSMS[2]);

		tmpSmsObj.setCardName(tmpSplitCardNameNumber[0]);
		tmpSmsObj.setApprovalType(tmpSMS[0]);
		tmpSmsObj.setPrice(convertToIntPrice(tmpSMS[1]));
		tmpSmsObj.setCardNumber(tmpSplitCardNameNumber[1]);
		tmpSmsObj.setApprovalTime(splitApprovalTime(tmpSMS[4]));
		tmpSmsObj.setPlace(tmpSMS[5]);

		return tmpSmsObj;
	}

	public int convertToIntPrice(String price) {
		String tmpPrice;

		tmpPrice = price.replace("원", "");
		tmpPrice = tmpPrice.replace(",", "");

		return Integer.parseInt(tmpPrice);
	}
	
	public String[] splitCardNameNumber(String cardNameNumber) {
		String tmpCardName = cardNameNumber.substring(0, cardNameNumber.indexOf("("));
		String tmpCardNumber = cardNameNumber.substring(cardNameNumber.indexOf("(") + 1, cardNameNumber.indexOf(")"));
		
		String[] tmpNameNumber = { tmpCardName, tmpCardNumber };
		return tmpNameNumber;
	}
	
	public String splitApprovalTime(String approvalTime) {
		String[] tmpApprovalTime = approvalTime.split(" ");
		
		return tmpApprovalTime[0]; 
	}
}

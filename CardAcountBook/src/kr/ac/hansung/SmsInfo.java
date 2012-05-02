package kr.ac.hansung;

import java.util.Calendar;

import android.content.res.Resources;

//SMS Information Class
public class SmsInfo {
	private String cardName;		//카드 이름
	private String approvalType;	//결재 종류 (체크승인, 신용승인 등)
	private int price;				//결재 가격
	private String cardNumber;		//카드 번호
	private String approvalTime;	//결재 일시
	private String place;			//결재 장소
	private String category;
	
	static int primaryKey = 1010;
	
	
	
	final static int NH_PNUM = 15881600;
	final static int KB_PNUM = 15881788;
	
	
	
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

	// Month, Day로 나누기
	public static String[] splitMonthDay(String monthDay) {
		String[] tmpMonthDay = monthDay.substring(0, monthDay.indexOf(" ")).split("/");
		return tmpMonthDay;
	}
	
	public static String scatterMessage(String smsAddress, String smsBody) {
		int tmpAddress = Integer.parseInt(smsAddress);
		String tmpInsertQuery = null;
		String[] tmpSplitBody;
		Calendar c = Calendar.getInstance();
		String tmpAType, tmpPrice, tmpCardName, tmpCardNum, tmpYear, tmpMonth, tmpDay, tmpPlace;
		String[] tmpApproval;
		
		tmpYear = String.valueOf(c.get(Calendar.YEAR));
		
		switch (tmpAddress) {
		case KB_PNUM :
			tmpSplitBody = smsBody.split("\n");
			tmpCardName = tmpSplitBody[0].substring(0, tmpSplitBody[0].indexOf("("));
			tmpCardNum = tmpSplitBody[0].substring(tmpSplitBody[0].indexOf("(") + 1, tmpSplitBody[0].indexOf(")"));
			tmpApproval = splitMonthDay(tmpSplitBody[2]);
			tmpMonth = tmpApproval[0];
			tmpDay = tmpApproval[1];
			tmpPrice = tmpSplitBody[3].replace(",", "").replace("원", "");
			tmpPlace = tmpSplitBody[4].substring(0, tmpSplitBody[4].length() - 3);
			
			tmpInsertQuery =  "INSERT INTO breakdowstats VALUES("
					+ primaryKey++ + ", '" + tmpCardName
					+ "', " + tmpYear + ", " + tmpMonth + ", "
					+ tmpDay + ", '" + tmpPlace
					+ "', " + Integer.parseInt(tmpPrice) + ", '기타', '" + tmpCardNum + "');";
			
			break;
		
		case NH_PNUM :
			tmpSplitBody = smsBody.split("\n");
			tmpAType = tmpSplitBody[0].substring(tmpSplitBody[0].indexOf("[") + 1, tmpSplitBody[0].indexOf("]"));
			tmpPrice = tmpSplitBody[1].replace(",", "").replace("원", "");
			tmpCardName = tmpSplitBody[2].substring(0, tmpSplitBody[2].indexOf("("));
			tmpCardNum = tmpSplitBody[2].substring(tmpSplitBody[2].indexOf("(") + 1, tmpSplitBody[2].indexOf(")"));
			String[] tmpAprvl = tmpSplitBody[4].split(" ");
			String[] tmpApprovalSplit = tmpAprvl[0].split("/");
			tmpMonth = tmpApprovalSplit[0];
			tmpDay = tmpApprovalSplit[1];
			
			tmpInsertQuery =  "INSERT INTO breakdowstats VALUES("
					+"null, '" + tmpCardName
					+ "', " + tmpYear + ", " + tmpMonth + ", "
					+ tmpDay + ", '" + tmpSplitBody[5]
					+ "', " + Integer.parseInt(tmpPrice) + ", '기타', '" + tmpCardNum + "');";
			
			break;
		}
		
		return tmpInsertQuery; 
	}
}

package kr.ac.hansung;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.ContextWrapper;
import android.util.Log;

//SMS Information Class
public class SmsInfo implements CategoryList{	
	private int breakKey;
	private String cardName;		//카드 이름
	private String approvalType;	//결재 종류 (체크승인, 신용승인 등)
	private int price;				//결재 금액
	private String cardNumber;		//카드 번호
	private String approvalTime;	//결재 일시
	private String place;			//결재 장소
	private String category;
	
	static int primaryKey = 1010;
	
	
	
	final static int NH_PNUM = 15881600;
	final static int KB_PNUM = 15881788; 
	
	
	public int getBreakKey() { return breakKey; }
	public String getCardName() { return cardName; }
	public String getApprovalType() { return approvalType; }
	public int getPrice() { return price; }
	public String getCardNumber() { return cardNumber; }
	public String getApprovalTime() { return approvalTime; }
	public String getPlace() { return place; }
	public String getCategory(){ return category; }
	
	public void setBreakKey(int key) { breakKey = key; }
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
	
	// 소수점 3자리마다 ,찍고 끝에 "원"을 붙이는 메소드
	public static String decimalPointToString(int price) {
		DecimalFormat df = new DecimalFormat("#,##0");
		String deciamlPoint = df.format(price) + "원";
		
		return deciamlPoint;
	}

	// Month, Day로 나누기
	public static String[] splitMonthDay(String monthDay) {
		String[] tmpMonthDay = monthDay.substring(0, monthDay.indexOf(" ")).split("/");
		return tmpMonthDay;
	}
	
	public static String scatterMessage(String smsAddress, String smsBody) {
		int tmpAddress = Integer.parseInt(smsAddress);
		int inDate;
		String tmpInsertQuery = null;
		String tmpAType, tmpPrice, tmpCardName, tmpCardNum, tmpYear, tmpMonth, tmpDay, tmpPlace, tmpCategory;
		String[] tmpSplitBody;
		Calendar c = Calendar.getInstance();
		Date date;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
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
			tmpCategory = SearchCategory(tmpPlace);
			
			date = new Date();
			date.setYear(Integer.parseInt(tmpYear) - 1900);
			date.setMonth(Integer.parseInt(tmpMonth) - 1);
			date.setDate(Integer.parseInt(tmpDay));
			inDate = Integer.parseInt(dateFormat.format(date));
			
			tmpInsertQuery =  "INSERT INTO breakdowstats VALUES("
					+ primaryKey++ + ", '" + tmpCardName
					+ "', " + tmpYear + ", " + tmpMonth + ", "
					+ tmpDay + ", '" + tmpPlace
					+ "', " + Integer.parseInt(tmpPrice) + ", '" + tmpCategory
					+ "', '" + tmpCardNum + "'," + inDate + ");";
			
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
			tmpCategory = SearchCategory(tmpSplitBody[5]);
			
			date = new Date();
			date.setYear(Integer.parseInt(tmpYear) - 1900);
			date.setMonth(Integer.parseInt(tmpMonth) - 1);
			date.setDate(Integer.parseInt(tmpDay));
			inDate = Integer.parseInt(dateFormat.format(date));
			
			tmpInsertQuery =  "INSERT INTO breakdowstats VALUES("
					+"null, '" + tmpCardName
					+ "', " + tmpYear + ", " + tmpMonth + ", "
					+ tmpDay + ", '" + tmpSplitBody[5]
					+ "', " + Integer.parseInt(tmpPrice) + ", '" + tmpCategory
					+ "', '" + tmpCardNum + "'," + inDate  + ");";
			
			break;
		}
		
		return tmpInsertQuery; 
	}
	
	
	public static String SearchCategory(String place){
		
		String Category = "기타";
			
		for(int i=0;i<High_Category.length;i++){
			for(int j=0;j<KeyWord[i].length;j++){
				if(place.matches(".*"+KeyWord[i][j]+".*")){
					Category = new String(High_Category[i]);
				}
			}
		}
				
		return Category;
	}
}

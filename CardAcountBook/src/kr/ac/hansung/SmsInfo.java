package kr.ac.hansung;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

/**
 * SmsInfo.java
 * SMS정보를 담는 Class
 * @author Junu Kim
 */
public class SmsInfo implements CategoryList {
	private Context context;
	private int breakKey;
	private String cardName;						//카드 이름
	private String approvalType;					//결재 종류 (체크승인, 신용승인 등)
	private int price;								//결재 금액
	private String cardNumber;						//카드 번호
	private String approvalTime;					//결재 일시
	private String place;							//결재 장소
	private String category;
	private static Resources res;
	
	private final static int NH_PNUM = 15881600;			// 농협
	private final static int KB_PNUM = 15881788; 			// 국민은행
	private final static int CITY_PNUM = 15661000;			// 시티은행
	private final static int KEB_PNUM = 15886700;			// 외환은행
	private final static int SAVING_BANK_PNUM = 15886622;	// 저축은행
	private final static int SHINHAN_PNUM = 15447200;		// 신한은행
	private final static int HYUNDAI_PNUM = 15776200;
	
	// getter
	public Context getContext() { return context; }
	public int getBreakKey() { return breakKey; }
	public String getCardName() { return cardName; }
	public String getApprovalType() { return approvalType; }
	public int getPrice() { return price; }
	public String getCardNumber() { return cardNumber; }
	public String getApprovalTime() { return approvalTime; }
	public String getPlace() { return place; }
	public String getCategory(){ return category; }
	public String getSmsFormErrorString() { return res.getString(R.string.sms_form_error); }
	
	// setter
	public void setBreakKey(int key) { breakKey = key; }
	public void setCardName(String cName) { cardName = cName; }
	public void setApprovalType(String aType) { approvalType = aType; }
	public void setPrice(int _price) { price = _price; }
	public void setCardNumber(String cNum) { cardNumber = cNum; }
	public void setApprovalTime(String aTime) { approvalTime = aTime; }
	public void setPlace(String _place) { place = _place; }
	public void setCategory(String _category){category = _category;}

	
	// Constructor
	public SmsInfo() {}
	
	public SmsInfo(Context context) {
		this.context = context;
		res = context.getResources();
	}
	
	public SmsInfo(String cardName) {
		setCardName(cardName);
	}
	
	public SmsInfo(String approvalTime, String cardName, String place, int price) {
		this.approvalTime = approvalTime;
		this.cardName = cardName;
		this.place = place;
		this.price = price;
	}

	public SmsInfo(int breakKey, String cardName, String cardNumber, String approvalTime, String place, int price, String category) {
		this.breakKey = breakKey;
		this.cardName = cardName;
		this.cardNumber = cardNumber;
		this.approvalTime = approvalTime;
		this.place = place;
		this.price = price;
		this.category = category;
	}
	
	/**
	 * Method decimalPiontToString 세자리 마다 ','를 찍고 '원'을 붙인다.
	 * @param price ','와 '원'을  붙일 정수
	 * @return String 결과
	 */
	public static String decimalPointToString(int price) {
		DecimalFormat df = new DecimalFormat("#,##0");
		String deciamlPoint = df.format(price) + "원";
		
		return deciamlPoint;
	}

	/**
	 * Method splitMonthDay 'MM/dd'형식의 Month와 Day를 분할한다.
	 * @param monthDay 'MM/dd'형식의 String
	 * @return String[] 결과
	 */
	public static String[] splitMonthDay(String monthDay) {
		String[] tmpMonthDay = monthDay.substring(0, monthDay.indexOf(" ")).split("/");
		return tmpMonthDay;
	}
	
	/**
	 * Method scatterMessage SMS의 발신처 Address와 수신 MessageBody를 카드사의 SMS Format에  
	 * 에 맞게 적절히 분할하여 각 정보를 DB에 Insert하기 위한 Query문 리턴 
	 * @param smsAddress SMS 발신처 Address
	 * @param smsBody SMS Message Body
	 * @return String 각정보를 DB에 넣는 INSERT QUERY
	 */
	public String scatterMessage(String smsAddress, String smsBody) {
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
		
		try {
			switch (tmpAddress) {
			
			case KB_PNUM :
				tmpSplitBody = smsBody.split("\n");
				tmpCardName = tmpSplitBody[0].substring(0, tmpSplitBody[0].indexOf("("));
				tmpCardNum = tmpSplitBody[0].substring(tmpSplitBody[0].indexOf("(") + 1, tmpSplitBody[0].indexOf(")"));
				tmpApproval = splitMonthDay(tmpSplitBody[2]);
				tmpMonth = tmpApproval[0];
				tmpDay = tmpApproval[1];
				tmpPrice = tmpSplitBody[3].replace(",", "").replace(res.getString(R.string.no_space_won), "");
				tmpPlace = tmpSplitBody[4].substring(0, tmpSplitBody[4].length() - 3);
				tmpCategory = SearchCategory(tmpPlace);
				
				date = new Date();
				date.setYear(Integer.parseInt(tmpYear) - 1900);
				date.setMonth(Integer.parseInt(tmpMonth) - 1);
				date.setDate(Integer.parseInt(tmpDay));
				inDate = Integer.parseInt(dateFormat.format(date));
				
				tmpInsertQuery =  "INSERT INTO breakdowstats VALUES(null, '" + tmpCardName	+ "', "
								  + tmpYear + ", " + tmpMonth + ", " + tmpDay + ", '" + tmpPlace
								  + "', " + Integer.parseInt(tmpPrice) + ", '" + tmpCategory + "', '" 
								  + tmpCardNum + "'," + inDate + ", 0);";
				
				break;
			
			case NH_PNUM :
				tmpSplitBody = smsBody.split("\n");
				tmpAType = tmpSplitBody[0].substring(tmpSplitBody[0].indexOf("[") + 1, tmpSplitBody[0].indexOf("]"));
				tmpPrice = tmpSplitBody[1].replace(",", "").replace(res.getString(R.string.no_space_won), "");
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
						+ "', '" + tmpCardNum + "'," + inDate  + ", 0);";
				
				break;
			
			case SHINHAN_PNUM :
				tmpSplitBody = smsBody.split(" ");
				
				Vector<String> stringVector = new Vector<String>();
						
				for (int i=0; i<tmpSplitBody.length; i++) {
					if (!tmpSplitBody[i].equals("")) {
						stringVector.add(tmpSplitBody[i]);
					}
				}
				
				if (stringVector.get(1).contains(res.getString(R.string.refusal_card_payment))
						|| stringVector.get(4).contains(res.getString(R.string.refusal_cause))) {
					return getSmsFormErrorString();
				}
				
				tmpAType = stringVector.get(0);
				tmpPrice = stringVector.get(3).replace(",", "").replace(res.getString(R.string.no_space_won), "");
				tmpCardName = res.getString(R.string.SHINHAN_card);
				tmpCardNum = res.getString(R.string.SHINHAN_blank_card_number);
				String[] _tmpApproval = stringVector.get(1).split("/");
				tmpMonth = _tmpApproval[0];
				tmpDay = _tmpApproval[1];
				tmpCategory = SearchCategory(stringVector.get(4));
	
				date = new Date();
				date.setYear(Integer.parseInt(tmpYear) - 1900);
				date.setMonth(Integer.parseInt(tmpMonth) - 1);
				date.setDate(Integer.parseInt(tmpDay));
				inDate = Integer.parseInt(dateFormat.format(date));
				
				tmpInsertQuery =  "INSERT INTO breakdowstats VALUES("
						+"null, '" + tmpCardName
						+ "', " + tmpYear + ", " + tmpMonth + ", "
						+ tmpDay + ", '" + stringVector.get(4)
						+ "', " + Integer.parseInt(tmpPrice) + ", '" + tmpCategory
						+ "', '" + tmpCardNum + "'," + inDate  + ", 0);";
				break;
				
			case CITY_PNUM :
				break;
	
			case KEB_PNUM :
				tmpSplitBody = smsBody.split(" ");
				
				Vector<String> kebSmsBody = new Vector<String>();
				
				for (int i=0; i<tmpSplitBody.length; i++) {
					if (!tmpSplitBody[i].equals("")) {
						kebSmsBody.add(tmpSplitBody[i]);
					}
				}

				tmpCardName = kebSmsBody.get(0).substring(kebSmsBody.get(0).indexOf("[") + 1, kebSmsBody.get(0).indexOf("]"));
				tmpPrice = kebSmsBody.get(1).replace(",", "").replace(res.getString(R.string.no_space_won), "");
				tmpPlace = kebSmsBody.get(3);
				tmpApproval = kebSmsBody.get(4).split("/");
				tmpMonth = tmpApproval[0];
				tmpDay = tmpApproval[1];
				tmpCardNum = res.getString(R.string.SHINHAN_blank_card_number);
				tmpCategory = SearchCategory(tmpPlace);
				
				tmpInsertQuery =  "INSERT INTO breakdowstats VALUES("
						+"null, '" + tmpCardName
						+ "', " + tmpYear + ", " + tmpMonth + ", "
						+ tmpDay + ", '" + tmpPlace
						+ "', " + tmpPrice + ", '" + tmpCategory
						+ "', '" + tmpCardNum + "'," + tmpYear + tmpMonth + tmpDay + ", 0);";
				
				break;
			
			case SAVING_BANK_PNUM :
				break;
			
			case HYUNDAI_PNUM :
				tmpSplitBody = smsBody.split("\n");
				
				if (!tmpSplitBody[0].contains(res.getString(R.string.approval_string))) {
					return getSmsFormErrorString();
				}
				
				String[] cardNameApproval = tmpSplitBody[0].split("-");
				String[] tmpApprovalTime = tmpSplitBody[2].split(" ");
				tmpApproval = tmpApprovalTime[0].split("/");
				tmpPrice = tmpSplitBody[3].substring(0, tmpSplitBody[3].indexOf("(")).replace(",", "").replace(res.getString(R.string.no_space_won), "");
				tmpCardNum = res.getString(R.string.SHINHAN_blank_card_number);
				tmpPlace = tmpSplitBody[4];
				
				Vector<String> hyundaiSmsBody = new Vector<String>();
				
				hyundaiSmsBody.add(cardNameApproval[0].replace("[", "").replace("]", ""));
				hyundaiSmsBody.add(tmpYear);
				hyundaiSmsBody.add(tmpApproval[0]);
				hyundaiSmsBody.add(tmpApproval[1]);
				hyundaiSmsBody.add(tmpPlace);
				hyundaiSmsBody.add(tmpPrice);
				hyundaiSmsBody.add(SearchCategory(tmpPlace));
				hyundaiSmsBody.add(tmpCardNum);
				hyundaiSmsBody.add(tmpYear + tmpApproval[0] + tmpApproval[1]);
				
				tmpInsertQuery =  "INSERT INTO breakdowstats VALUES("
								+"null, '" + hyundaiSmsBody.get(0)
								+ "', " + hyundaiSmsBody.get(1) + ", " + hyundaiSmsBody.get(2) + ", "
								+ hyundaiSmsBody.get(3) + ", '" + hyundaiSmsBody.get(4)
								+ "', " + hyundaiSmsBody.get(5) + ", '" + hyundaiSmsBody.get(6)
								+ "', '" + hyundaiSmsBody.get(7) + "'," + hyundaiSmsBody.get(8) + ", 0);";
				break;
				
			default : 
				break;
			}
		} catch (Exception e) {
			return getSmsFormErrorString();
		}
		return tmpInsertQuery; 
	}
	
	
	/**
	 * Method SearchCategory place의 적절한 카테고리 자동분류. 
	 * @param place Place
	 * @return String Category
	 */
	public static String SearchCategory(String place){
		
		String Category = "기타";
			
		for(int i=0;i<sCategory.length;i++){
			for(int j=0;j<KeyWord[i].length;j++){
				if(place.matches(".*"+KeyWord[i][j]+".*")){
					Category = sCategory[i];
				}
			}
		}
		return Category;
	}
}

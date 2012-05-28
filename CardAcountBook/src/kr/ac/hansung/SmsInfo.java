package kr.ac.hansung;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * SmsInfo.java
 * SMS������ ��� Class
 * @author Junu Kim
 */
public class SmsInfo implements CategoryList{	
	private int breakKey;
	private String cardName;						//ī�� �̸�
	private String approvalType;					//���� ���� (üũ����, �ſ���� ��)
	private int price;								//���� �ݾ�
	private String cardNumber;						//ī�� ��ȣ
	private String approvalTime;					//���� �Ͻ�
	private String place;							//���� ���
	private String category;
	
	final static int NH_PNUM = 15881600;			// ����
	final static int KB_PNUM = 15881788; 			// ��������
	final static int CITY_PNUM = 15661000;			// ��Ƽ����
	final static int KEB_PNUM = 15886700;			// ��ȯ����
	final static int SAVING_BANK_PNUM = 15886622;	// ��������
	final static int SHINHAN_PNUM = 15447200;		// ��������
	
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

	public SmsInfo(String cardName, String cardNumber, String approvalTime, String place, int price, String category) {
		this.cardName = cardName;
		this.cardNumber = cardNumber;
		this.approvalTime = approvalTime;
		this.place = place;
		this.price = price;
		this.category = category;
	}
	
	/**
	 * Method decimalPiontToString ���ڸ� ���� ','�� ��� '��'�� ���δ�.
	 * @param price ','�� '��'��  ���� ����
	 * @return String ���
	 */
	public static String decimalPointToString(int price) {
		DecimalFormat df = new DecimalFormat("#,##0");
		String deciamlPoint = df.format(price) + "��";
		
		return deciamlPoint;
	}

	/**
	 * Method splitMonthDay 'MM/dd'������ Month�� Day�� �����Ѵ�.
	 * @param monthDay 'MM/dd'������ String
	 * @return String[] ���
	 */
	public static String[] splitMonthDay(String monthDay) {
		String[] tmpMonthDay = monthDay.substring(0, monthDay.indexOf(" ")).split("/");
		return tmpMonthDay;
	}
	
	/**
	 * Method scatterMessage SMS�� �߽�ó Address�� ���� MessageBody�� ī����� SMS Format��  
	 * �� �°� ������ �����Ͽ� �� ������ DB�� Insert�ϱ� ���� Query�� ���� 
	 * @param smsAddress SMS �߽�ó Address
	 * @param smsBody SMS Message Body
	 * @return String �������� DB�� �ִ� INSERT QUERY
	 */
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
			tmpPrice = tmpSplitBody[3].replace(",", "").replace("��", "");
			tmpPlace = tmpSplitBody[4].substring(0, tmpSplitBody[4].length() - 3);
			tmpCategory = SearchCategory(tmpPlace);
			
			date = new Date();
			date.setYear(Integer.parseInt(tmpYear) - 1900);
			date.setMonth(Integer.parseInt(tmpMonth) - 1);
			date.setDate(Integer.parseInt(tmpDay));
			inDate = Integer.parseInt(dateFormat.format(date));
			
//			db.execSQL("CREATE TABLE breakdowstats (breakKey INTEGER PRIMARY KEY, cardName TEXT, pYear INTEGER, pMonth INTEGER," +
//					" pDay INTEGER, pPlace TEXT"
//					+ ", price INTEGER, category TEXT, cardNumber TEXT, combineDate INTEGER);");
			
			
			tmpInsertQuery =  "INSERT INTO breakdowstats VALUES(null, '" + tmpCardName	+ "', "
							  + tmpYear + ", " + tmpMonth + ", " + tmpDay + ", '" + tmpPlace
							  + "', " + Integer.parseInt(tmpPrice) + ", '" + tmpCategory + "', '" 
							  + tmpCardNum + "'," + inDate + ");";
			
			break;
		
		case NH_PNUM :
			tmpSplitBody = smsBody.split("\n");
			tmpAType = tmpSplitBody[0].substring(tmpSplitBody[0].indexOf("[") + 1, tmpSplitBody[0].indexOf("]"));
			tmpPrice = tmpSplitBody[1].replace(",", "").replace("��", "");
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
			
		case CITY_PNUM :
			break;

		case KEB_PNUM :
			break;
		
		case SAVING_BANK_PNUM :
			break;
		
		case SHINHAN_PNUM :
			break;
		}
		
		return tmpInsertQuery; 
	}
	
	
	/**
	 * Method SearchCategory place�� ������ ī�װ� �ڵ��з�. 
	 * @param place Place
	 * @return String Category
	 */
	public static String SearchCategory(String place){
		
		String Category = "��Ÿ";
			
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

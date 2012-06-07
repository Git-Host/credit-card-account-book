package kr.ac.hansung;


public interface CategoryList {
	int[] categoryList = { 
			R.string.c_etc, 				R.string.c_food,					R.string.c_side_dish, 		R.string.c_snack, 		R.string.c_eat_out,
			R.string.c_alcohol, 			R.string.c_maintenance_cost,		R.string.c_utility_bills,	R.string.c_mobile, 		R.string.c_internet,
			R.string.c_monthly_rent, 		R.string.c_furniture, 				R.string.c_kitchen,			R.string.c_goods, 		R.string.c_consumables,
			R.string.c_clothing,			R.string.c_fashion, 				R.string.c_hair, 			R.string.c_wash_repair,	R.string.c_leisure,
			R.string.c_culture, 			R.string.c_travle,					R.string.c_medical, 		R.string.c_tuition,		R.string.c_teaching_materials,
			R.string.c_baby_goods,			R.string.c_public_transit, 			R.string.c_oiling,			R.string.c_dating, 		R.string.c_gift,		
			R.string.c_family_event,		R.string.c_meeting_fee,				R.string.c_card_pay };
	
	final static String[] High_Category = {"기타","식비","주거/통신","생활용품","의복/미용","건강/문화","교육/육아","교통/차량","경조사비/회비","카드대금"};
	
	public String[][] KeyWord = 
	/*기타*/			{{},
	/*식비*/			{"마트","정육","슈퍼"},{"베이커리","바게트","파닭","치킨","BBQ","피자","피치홈","분식","버무리"},{"탐앤탐스","스타벅스","카페베네","엔제리너스","파스꾸찌","베스킨","빈스앤와플"},
					{"밥앤죽","맥도날드","베니건스","아웃백","빕스","KFC","TGIF","레스토랑","순대","본죽","윤가네","용우동","닐리리맘보","해장국"},{"스타우트","옛고을","족발","호프","거품","사과"},
	/*주거/통신*/		{},{},{"핸드폰"},{"브로드"},{},
	/*생활용품*/		{},{},{"세븐일레븐","GS25","훼미리마트","미니스톱","델리후레쉬"},{"다이소"},
	/*의복/미용*/		{"마리오"},{"뉴발란스","나이키","프로스펙스","현대쇼핑"},{"헤어샵","미용실"},{},
	/*건강/문화*/		{},{"티켓"},{"호텔","모텔","펜션"},{"치과","약국","병원","의원","외과","이빈후과","외과","산부인과"},
	/*교육/육아*/		{"대학"},{"학원","독서실","서점","문고","서적"},{},
	/*교통/차량*/		{"택시","코레일"},{"주유소","도로공사","GS칼텍스","오일뱅크",},
	/*경조사비/회비*/	{},{},{},{},
	/*카드대금*/		{},};
	public String[] sCategory = {
			"기타",
			"주식","부식","간식","외식","술/유흥",
			"관리비","공과금","이동통신","인터넷","월세",
			"가구/가전","주방/욕실","잡화","소모",
			"의류비","패션/잡화","헤어/뷰티","세탁/수선",
			"운동/레져","문화생활","여행","병원비",
			"등록금","학원/교재비","육아용품",
			"대중교통","주유비",
			"데이트","선물","경조사비","모임회비",
			"카드대금"
			};
	 
	    
	int[][] i_category = {{R.string.c_etc},
			{R.string.c_alcohol,R.string.c_food,R.string.c_side_dish,R.string.c_snack,R.string.c_eat_out},
			{R.string.c_maintenance_cost,R.string.c_utility_bills,R.string.c_mobile,R.string.c_internet,R.string.c_monthly_rent},
			{R.string.c_furniture,R.string.c_kitchen,R.string.c_goods,R.string.c_consumables},
			{R.string.c_clothing,R.string.c_fashion,R.string.c_hair,R.string.c_wash_repair},
			{R.string.c_leisure,R.string.c_culture,R.string.c_travle,R.string.c_medical},
			{R.string.c_tuition,R.string.c_teaching_materials,R.string.c_baby_goods},
			{R.string.c_public_transit,R.string.c_oiling},
			{R.string.c_family_event,R.string.c_dating,R.string.c_gift,R.string.c_meeting_fee},
			{R.string.c_card_pay}};
	
	

}

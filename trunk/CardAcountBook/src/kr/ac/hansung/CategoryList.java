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
	/*기타*/		{{},
	/*식비*/		{"마트","정육"},{},{},{"맥도날드","베니건스","아웃백","빕스"},{},
	/**/		{},{},{"핸드폰"},{},{},
	/**/		{},{},{},{},
	/**/		{},{},{"헤어샵","미용실"},{},
	/**/		{},{},{},{"약국","병원","한의원"},
	/**/		{},{"학원"},{},
	/**/		{"택시"},{"주유소","도로공사"},
	/**/		{},{},{},{},
	/**/		{},};
	public String[] sCategory = {"주식","부식","간식","외식","술/유통","관리비","공과금","이동통신","인터넷","월세","가구/가전","주방/욕실","잡화","소모","의류비","패션/잡화","헤어/뷰티",
			"세탁/수선","운동/레져","문화생활","여행","병원비","등록금","학원/교재비","육아용품","대중교통","주유비","데이트","선물","경조사비","모임회비","카드대금"};
	 
	    
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

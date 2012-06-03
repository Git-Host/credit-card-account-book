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
	
	final static String[] High_Category = {"��Ÿ","�ĺ�","�ְ�/���","��Ȱ��ǰ","�Ǻ�/�̿�","�ǰ�/��ȭ","����/����","����/����","�������/ȸ��","ī����"};
	
	public String[][] KeyWord = 
	/*��Ÿ*/			{{},
	/*�ĺ�*/			{"��Ʈ","����","����"},{"����Ŀ��","�ٰ�Ʈ"},{"Ž��Ž��","��Ÿ����","ī�亣��","�������ʽ�","�Ľ�����","����","������","��쵿","����Ų"},{"�Ƶ�����","���ϰǽ�","�ƿ���","����","KFC","TGIF"},{},
	/*�ְ�/���*/		{},{},{"�ڵ���"},{"��ε���"},{},
	/*��Ȱ��ǰ*/		{},{},{"�����Ϸ���","GS25","�ѹ̸���Ʈ"},{},
	/*�Ǻ�/�̿�*/		{},{},{"��","�̿��"},{},
	/*�ǰ�/��ȭ*/		{},{},{"ȣ��"},{"ġ��","�౹","����","���ǿ�","�ܰ�","�̺��İ�","�ܰ�","����ΰ�"},
	/*����/����*/		{},{"�п�","������"},{},
	/*����/����*/		{"�ý�","�ڷ���"},{"������","���ΰ���","GSĮ�ؽ�","���Ϲ�ũ"},
	/*�������/ȸ��*/	{},{},{},{},
	/*ī����*/		{},};
	public String[] sCategory = {"�ֽ�","�ν�","����","�ܽ�","��/����",
			"������","������","�̵����","���ͳ�","����",
			"����/����","�ֹ�/���","��ȭ","�Ҹ�",
			"�Ƿ���","�м�/��ȭ","���/��Ƽ","��Ź/����",
			"�/����","��ȭ��Ȱ","����","������","��ϱ�","�п�/�����","���ƿ�ǰ","���߱���","������","����Ʈ","����","�������","����ȸ��","ī����"};
	 
	    
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

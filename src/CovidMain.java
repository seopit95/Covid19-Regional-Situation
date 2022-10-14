package covid19;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;


public class CovidMain {
	public static final int DATAINPUT = 1, DATAUPDATE = 2, DATADELETE = 3, DATASEARCH = 4;
	public static final int DATAOUTPUT = 5, DATASORT = 6, DATASTATS = 7, INOCULATION = 8, EXIT = 9 ;
	public static Scanner sc = new Scanner(System.in);
	
	public static void main(String[] args) {
		List<Covid> list = new ArrayList<Covid>();
		
		boolean loopflag = false;
		
		while(!loopflag) {
			int num = displayMenu();
			switch(num) {
			case DATAINPUT:
				covidDataInput();
				break;
			case DATAUPDATE:
				covidDataUpdate();
				break;
			case DATADELETE:
				covidDataDelete();
				break;
			case DATASEARCH :
				covidDataSearch();
				break;
			case DATAOUTPUT:
				covidDataOutput();
				break;
			case DATASORT :
				covidDataSort();
				break;
			case DATASTATS :
				covidDataStats();
				break;
//			case INOCULATION :
//				covidInoculation();
//				break;
			case EXIT :
				System.out.println("Covid 시스템 종료");
				loopflag = true;
				break;
			default :
				return;
			}
		}
	}

//	private static void covidInoculation() {
//		try {
//			DBConnection dbConnection = new DBConnection();
//			dbConnection.connect();
//			
//			System.out.println("4차 예방접종자 수 >>");
//			int inoculNum = sc.nextInt(); 
//			
//			String inoculationReturnValue = dbConnection.selectInoculation(inoculNum);
//			
//			dbConnection.close();
//		}catch(InputMismatchException e) {
//			System.out.println("Inoculation Error. " + e.getMessage() );
//		}
//	}


	//통계
	private static void covidDataStats() {
		List<Covid> list = new ArrayList<Covid>();
		try {
			DBConnection dbConnection = new DBConnection();
			dbConnection.connect();
			
			System.out.print("통계) 1.확진률이 가장 높은 지역 2.확진률 가장 낮은 지역\n>>");
			int type = sc.nextInt();
			boolean value = checkInputPattern(String.valueOf(type), 4);
			if(!value) {
				return;
			}
			
			list = dbConnection.selectMaxMin(type);
			
			if (list.size() <= 0) {
				System.out.println("보여줄 리스트가 없습니다." );
				return;
			}
			for (Covid covid : list) {
				System.out.println("지역\t확진자 수(명)\t사망자 수(명)\t확진률(전국기준)\t사망률(전국기준)\t위험도\t위험 순위");
				System.out.println(covid);
			}
			dbConnection.close();
			
		}catch(InputMismatchException e) {
			System.out.println("올바르지 않은 입력입니다. 다시 입력해주세요" + e.getMessage());
			return;
		}catch(Exception e) {
			System.out.println("Database Stats Error" + e.getMessage());
		}
		
	}

	//정렬
	private static void covidDataSort() {
		List<Covid> list = new ArrayList<Covid>();
		try {
			DBConnection dbConnection = new DBConnection();
			dbConnection.connect();
			System.out.print("1.지역별 확진률 높은 순\t2.지역별 사망률 낮은 순\n>>");
			int type = sc.nextInt();
			boolean value = checkInputPattern(String.valueOf(type), 4);
			if(!value) {
				return;
			}
			list = dbConnection.seletOrderBy(type);
			
			if (list.size() <= 0) {
				System.out.println("보여줄 리스트가 없습니다." );
				return;
			}
			
			for (Covid covid : list) {
				System.out.println("지역\t확진자 수(명)\t사망자 수(명)\t확진률(전국기준)\t사망률(전국기준)\t위험도\t위험 순위");
				System.out.println(covid);
			}
			dbConnection.close();
		}catch(Exception e) {
			System.out.println("Database Sort Error " + e.getMessage());
		}
		return;
		
	}

	//삭제
	private static void covidDataDelete() {
		try {
			System.out.print("삭제할 지역을 입력해주세요 >> ");
			String region = sc.nextLine();
			
			//데이터 연결
			DBConnection dbConnection = new DBConnection();
			dbConnection.connect();
			
			int deleteReturnValue = dbConnection.delete(region);
			if(deleteReturnValue == -1) {
				System.out.println("삭제 실패입니다.");
			}else if(deleteReturnValue == 0) {
				System.out.println("입력하신 지역에 대한 정보가 없습니다.");
			}else{
				System.out.println("삭제되었습니다");
			}
			dbConnection.close();
		}catch(InputMismatchException e) {
			System.out.println("올바르지 않은 입력입니다. 다시 입력해주세요");
			return;
		}catch(Exception e) {
			System.out.println("Database Delete Error"  + e.getMessage());
		}
	}

	//수정
	private static void covidDataUpdate() {
		List<Covid> list = new ArrayList<Covid>();
		try {
		System.out.print("수정할 지역을 입력해주세요. (3~5자 이내) >> ");
		String region = sc.nextLine();
		boolean value = checkInputPattern(region, 2);
		if(!value) {
			return;
		}
		System.out.println("지역\t확진자 수(명)\t사망자 수(명)\t확진률(전국기준)\t사망률(전국기준)\t위험도\t위험 순위");
		DBConnection dbConnection = new DBConnection();
		dbConnection.connect();
		
		list = dbConnection.selectSearch(region);
		if(list.size() <= 0) {
			System.out.println("수정할 리스트가 없습니다.");
		}
		
		for (Covid covid : list) {
			if(!(covid.getRegion().equals(region))) {
				System.out.println("해당 지역 정보가 없습니다.");
			}else {
			
			System.out.println(covid);
			}
		}
		
		Covid savedCovid = list.get(0);
		System.out.println("수정할 확진자 수 : " + savedCovid.getConfirmed() + ">>");
		int confirmed = sc.nextInt();
		value = checkInputPattern(String.valueOf(confirmed), 3);
		if(!value) {
			return;
		}
		savedCovid.setConfirmed(confirmed);
		
		System.out.println("수정할 사망자 수 : " + savedCovid.getDeath() + ">>");
		int death = sc.nextInt();
		value = checkInputPattern(String.valueOf(death), 3);
		if(!value) {
			return;
		}
		savedCovid.setDeath(death);
		
		int returnUpdateValue = dbConnection.update(savedCovid);
		if(returnUpdateValue == -1) {
			System.out.println("지역정보 수정실패");
			return;
		}
		System.out.println("지역정보 수정완료");
		dbConnection.close();
		}catch(Exception e) {
			System.out.println("Update Error. " + e.getMessage());
		}
	}

	//검색
	private static void covidDataSearch() {
		List<Covid> list = new ArrayList<Covid>();
		
		try {
			//검색할 지역
			System.out.print("검색할 지역을 입력해주세요. (3~5자 이내) >> ");
			String region = sc.nextLine();
			boolean value = checkInputPattern(region, 2);
			if(!value) {
				return;
			}
			System.out.println("지역\t확진자 수(명)\t사망자 수(명)\t확진률(전국기준)\t사망률(전국기준)\t위험도\t위험 순위");
			DBConnection dbConnection = new DBConnection();
			dbConnection.connect();
			
			list = dbConnection.selectSearch(region);
			if(list.size() <= 0) {
				System.out.println("검색할 리스트가 없습니다.");
			}
			
			for (Covid covid : list) {
				System.out.println(covid);
			}
			dbConnection.close();
		}catch(InputMismatchException e) {
			System.out.println("올바르지 않은 입력입니다. 다시 입력해주세요");
			return;
		}catch(Exception e) {
			System.out.println("Database Search Error" + e.getMessage());
		}
	}

	//출력
	private static void covidDataOutput() {
		List<Covid> list = new ArrayList<Covid>();
		System.out.println("지역\t확진자 수(명)\t사망자 수(명)\t확진률(전국기준)\t사망률(전국기준)\t위험도\t위험 순위");
		try {
			DBConnection dbConnection = new DBConnection();
			dbConnection.connect();
			
			list = dbConnection.select();
			if (list.size() <= 0) {
				System.out.println("보여줄 리스트가 없습니다." );
				return;
			}
			
			for (Covid covid : list) {
				System.out.println(covid);
			}
			dbConnection.close();
		}catch(Exception e) {
			System.out.println("select Error. " + e.getMessage());
		}
	}

	//입력
	//삽입
	private static void covidDataInput() {
		try {
			System.out.print("지역을 입력해주세요 (3~5자 이내) >> ");
			String region = sc.nextLine();
			boolean value = checkInputPattern(region, 2);
			if(!value) {
				return;
			}
			System.out.print("확진자 수를 입력해주세요 >> ");
			int confirmed = sc.nextInt();
			boolean value2 = checkInputPattern(String.valueOf(confirmed), 3);
			if(!value2) {
				return;
			}
			System.out.print("사망자 수를 입력해주세요 >> ");
			int death = sc.nextInt();
			boolean value3 = checkInputPattern(String.valueOf(death), 3);
			if(!value3) {
				return;
			}
			
			//코로나 객체 생성
			Covid covid = new Covid(region, confirmed, death);
			//데이터베이스 연결
			DBConnection dbConnection = new DBConnection();
			dbConnection.connect();
			
			int insertReturnValue = dbConnection.insert(covid);
			if(insertReturnValue == -1) {
				System.out.println("입력 실패입니다.");
			}else {
				System.out.println("입력되었습니다. (리턴값: " + insertReturnValue + ")");
			}
			dbConnection.close();
		}catch(InputMismatchException e) {
			System.out.println("올바르지 않은 입력입니다. 다시 입력해주세요");
		}
	}

	//메뉴
	//메뉴
	private static int displayMenu() {
		int num = -1;
		try {
			System.out.println(" ---------------------------------------------------");
			System.out.println("| 1.입력 2.수정 3.삭제 4.검색 5.출력 6.정렬 7.통계 8.4차 예방접종률 9.종료 |");
			System.out.print(" ---------------------------------------------------\n>>");
			num = sc.nextInt();
			//패턴적용
			boolean value = checkInputPattern(String.valueOf(num), 1);
			if(!value) {
				return num;
			}
		}catch(Exception e) {
			System.out.println("DisplayMenu Error" + e.getMessage());
		}finally {
			sc.nextLine();
		}
		return num;
	}

	//패턴체크
	private static boolean checkInputPattern(String data, int patternType) {
		final int MENU = 1, REGION = 2, PERSON = 3, SORT = 4;
		String pattern = null;
		boolean regex = false;
		String message = null;
		
		switch(patternType) {
		case MENU:
			pattern = "^[1-9]$";
			message = "번호를 다시 입력해주세요 (1~9)";
			break;
		case REGION:
			pattern = "^[가-힣]{2,5}$";
			message = "지역을 다시 입력해주세요 (3~5자 이내)";
			break;
		case PERSON:
			pattern = "^[0-9]{1,8}$";
			message = "사람 수를 다시 입력해주세요 ";
			break;
		case SORT:
			pattern = "^[1-2]$";
			message = "번호를 다시 입력해주세요 ";
			break;
		}
		
		regex = Pattern.matches(pattern, data);
		if(!regex) {
			System.out.println(message);
			return false;
		}
		return regex;
	}

}

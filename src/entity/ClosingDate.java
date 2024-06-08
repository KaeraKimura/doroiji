package entity;

public enum ClosingDate {
	D_5("5"),
	D_10("10"),
	D_15("15"),
	D_20("20"),
	D_25("25"),
	D_LAST("末");
	
	private String dateStr;
	
	private ClosingDate(String dateStr) {
		this.dateStr = dateStr;
	}
	
	
	public static ClosingDate getTypeByValue(String dateStr) {
		for(ClosingDate c: ClosingDate.values()) {
			if(c.dateStr.equals(dateStr)) {
				return c;
			}
		}
		//どれにも当てはまらない場合は例外
		throw new IllegalArgumentException("列挙型に定義されてません:" + dateStr);
	}
	
	public String toString() {
		return this.dateStr;
	}
}

package energy;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "source", "dataset", "areaName", "areaTypeCode", "mapCode", "resolutionCode", "year", "month",
		"actualTotalLoadByMonthValue" })
public class ActualTotalLoadByMonth {
	private final String Source = "entso-e";
	private final String Dataset = "ActualTotalLoad";
	private String AreaName;
	private String AreaTypeCode;
	private String MapCode;
	private String ResolutionCode;
	private int Year;
	private int Month;
	private float ActualTotalLoadByMonthValue;

	public ActualTotalLoadByMonth(String areaName, String areaTypeCode, String mapCode, String resolutionCode, int year,
			int month, float actualTotalLoadByMonthValue) {
		super();
		AreaName = areaName;
		AreaTypeCode = areaTypeCode;
		MapCode = mapCode;
		ResolutionCode = resolutionCode;
		Year = year;
		Month = month;
		ActualTotalLoadByMonthValue = actualTotalLoadByMonthValue;
	}

	public String getAreaName() {
		return AreaName;
	}

	public void setAreaName(String areaName) {
		AreaName = areaName;
	}

	public String getAreaTypeCode() {
		return AreaTypeCode;
	}

	public void setAreaTypeCode(String areaTypeCode) {
		AreaTypeCode = areaTypeCode;
	}

	public String getMapCode() {
		return MapCode;
	}

	public void setMapCode(String mapCode) {
		MapCode = mapCode;
	}

	public String getResolutionCode() {
		return ResolutionCode;
	}

	public void setResolutionCode(String resolutionCode) {
		ResolutionCode = resolutionCode;
	}

	public int getYear() {
		return Year;
	}

	public void setYear(int year) {
		Year = year;
	}

	public int getMonth() {
		return Month;
	}

	public void setMonth(int month) {
		Month = month;
	}

	public float getActualTotalLoadByMonthValue() {
		return ActualTotalLoadByMonthValue;
	}

	public void setActualTotalLoadByMonthValue(float actualTotalLoadByMonthValue) {
		ActualTotalLoadByMonthValue = actualTotalLoadByMonthValue;
	}

	public String getSource() {
		return Source;
	}

	public String getDataset() {
		return Dataset;
	}

	@Override
	public String toString() {
		return Source + "," + Dataset + "," + AreaName + "," + AreaTypeCode + "," + MapCode + "," + ResolutionCode + ","
				+ Year + "," + Month + "," + "," + ActualTotalLoadByMonthValue;
	}
}

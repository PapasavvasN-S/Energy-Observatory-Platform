package energy;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "source", "dataset", "areaName", "areaTypeCode", "mapCode", "resolutionCode", "year", "month",
		"day", "dateTimeUTC", "actualTotalLoadValue", "updateTimeUTC" })
public class ActualTotalLoad {

	private final String Source = "entso-e";
	private final String Dataset = "ActualTotalLoad";
	private String AreaName;
	private String AreaTypeCode;
	private String MapCode;
	private String ResolutionCode;
	private int Year;
	private int Month;
	private int Day;
	private String DateTimeUTC;
	private float ActualTotalLoadValue;
	private String UpdateTimeUTC;

	public ActualTotalLoad(String areaName, String areaTypeCode, String mapCode, String resolutionCode, int year,
			int month, int day, String dateTimeUTC, float actualTotalLoadValue, String updateTimeUTC) {
		super();
		AreaName = areaName;
		AreaTypeCode = areaTypeCode;
		MapCode = mapCode;
		ResolutionCode = resolutionCode;
		Year = year;
		Month = month;
		Day = day;
		DateTimeUTC = dateTimeUTC;
		ActualTotalLoadValue = actualTotalLoadValue;
		UpdateTimeUTC = updateTimeUTC;
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

	public int getDay() {
		return Day;
	}

	public void setDay(int day) {
		Day = day;
	}

	public String getDateTimeUTC() {
		return DateTimeUTC;
	}

	public void setDateTimeUTC(String dateTimeUTC) {
		DateTimeUTC = dateTimeUTC;
	}

	public float getActualTotalLoadValue() {
		return ActualTotalLoadValue;
	}

	public void setActualTotalLoadValue(float actualTotalLoadValue) {
		ActualTotalLoadValue = actualTotalLoadValue;
	}

	public String getUpdateTimeUTC() {
		return UpdateTimeUTC;
	}

	public void setUpdateTimeUTC(String updateTimeUTC) {
		UpdateTimeUTC = updateTimeUTC;
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
				+ Year + "," + Month + "," + Day + "," + DateTimeUTC + "," + ActualTotalLoadValue + "," + UpdateTimeUTC;
	}
}
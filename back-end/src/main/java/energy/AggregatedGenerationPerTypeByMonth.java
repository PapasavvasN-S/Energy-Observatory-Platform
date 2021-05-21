package energy;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "source", "dataset", "areaName", "areaTypeCode", "mapCode", "resolutionCode", "year", "month",
		"productionType", "actualGenerationOutputByMonthValue" })
public class AggregatedGenerationPerTypeByMonth {
	private final String Source = "entso-e";
	private final String Dataset = "AggregatedGenerationPerType";
	private String AreaName;
	private String AreaTypeCode;
	private String MapCode;
	private String ResolutionCode;
	private int Year;
	private int Month;
	private String ProductionType;
	private float ActualGenerationOutputByMonthValue;

	public AggregatedGenerationPerTypeByMonth(String areaName, String areaTypeCode, String mapCode,
			String resolutionCode, int year, int month, String productionType,
			float actualGenerationOutputByMonthValue) {
		super();
		AreaName = areaName;
		AreaTypeCode = areaTypeCode;
		MapCode = mapCode;
		ResolutionCode = resolutionCode;
		Year = year;
		Month = month;
		ProductionType = productionType;
		ActualGenerationOutputByMonthValue = actualGenerationOutputByMonthValue;
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

	public String getProductionType() {
		return ProductionType;
	}

	public void setProductionType(String productionType) {
		ProductionType = productionType;
	}

	public float getActualGenerationOutputByMonthValue() {
		return ActualGenerationOutputByMonthValue;
	}

	public void setActualGenerationOutputByMonthValue(float actualGenerationOutputByMonthValue) {
		ActualGenerationOutputByMonthValue = actualGenerationOutputByMonthValue;
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
				+ Year + "," + Month + "," + ActualGenerationOutputByMonthValue;
	}
}
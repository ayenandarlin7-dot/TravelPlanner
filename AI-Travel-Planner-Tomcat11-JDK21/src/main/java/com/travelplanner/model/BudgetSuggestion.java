package com.travelplanner.model;

import java.math.BigDecimal;

/**
 * A single over-budget suggestion for a recommended trip plan.
 *
 * <p>
 * The {@link #applyType} / {@link #applyValue} pair tells the recommendation
 * page how to re-run the plan search with the suggestion applied:
 * <ul>
 * <li>transportation -> post {@code transportationId}=applyValue</li>
 * <li>hotel -> post {@code hotelCategory}=applyValue</li>
 * <li>duration -> post {@code returnDate}=applyValue (new return date)</li>
 * <li>attractions -> post {@code excludeAttractionIds}=applyValue</li>
 * </ul>
 */
public class BudgetSuggestion {

	private String applyType;

	private String applyValue;

	private String title;

	private String description;

	private BigDecimal potentialSavings;

	public BudgetSuggestion() {
	}

	public BudgetSuggestion(String applyType, String applyValue, String title, String description,
			BigDecimal potentialSavings) {

		this.applyType = applyType;
		this.applyValue = applyValue;
		this.title = title;
		this.description = description;
		this.potentialSavings = potentialSavings;
	}

	public String getApplyType() {
		return applyType;
	}

	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}

	public String getApplyValue() {
		return applyValue;
	}

	public void setApplyValue(String applyValue) {
		this.applyValue = applyValue;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getPotentialSavings() {
		return potentialSavings;
	}

	public void setPotentialSavings(BigDecimal potentialSavings) {
		this.potentialSavings = potentialSavings;
	}
}

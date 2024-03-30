package fr.ceri.ceriplanning.model;

public class Event {
  String categories;
  String dtStamp;
  String lastModified;
  String uid;
  String dtStart;
  String dtEnd;
  String summary = ""; // Initialize summary to handle accumulation of lines

  DescriptionDetails descriptionDetails;

  @Override
  public String toString() {
    return "Event{" +
      "categories='" + categories + '\'' +
      ", dtStamp='" + dtStamp + '\'' +
      ", lastModified='" + lastModified + '\'' +
      ", uid='" + uid + '\'' +
      ", dtStart='" + dtStart + '\'' +
      ", dtEnd='" + dtEnd + '\'' +
      ", description='" + descriptionDetails + '\'' + // Trim the summary to remove any leading/trailing whitespace
      '}';
  }

  public String getCategories() {
    return categories;
  }

  public void setCategories(String categories) {
    this.categories = categories;
  }

  public String getDtStamp() {
    return dtStamp;
  }

  public void setDtStamp(String dtStamp) {
    this.dtStamp = dtStamp;
  }

  public String getLastModified() {
    return lastModified;
  }

  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getDtStart() {
    return dtStart;
  }

  public void setDtStart(String dtStart) {
    this.dtStart = dtStart;
  }

  public String getDtEnd() {
    return dtEnd;
  }

  public void setDtEnd(String dtEnd) {
    this.dtEnd = dtEnd;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public DescriptionDetails getDescriptionDetails() {
    return descriptionDetails;
  }

  public void setDescriptionDetails(DescriptionDetails descriptionDetails) {
    this.descriptionDetails = descriptionDetails;
  }
}

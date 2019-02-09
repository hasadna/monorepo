package projects.stackoverflow;

public class ExtractedRow {
  // stackoverflow's posts.xml file is primarily a data bace table written as
  // xml file.
  // we are interested only in the row tag. other tags will be written as they
  // are to the output file.
  // this tag however includes unnecessary data we don't want.
  // this class will store only the data of the row after extracting the
  // relevant data from it, so it can be written to the output file.
  private String id;
  private String postTypeId;
  private String creationDate;
  private String ownerUserId;
  private String tags;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPostTypeId() {
    return this.postTypeId;
  }

  public void setPostTypeId(String postTypeId) {
    this.postTypeId = postTypeId;
  }

  public String getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }

  public String getOwnerUserId() {
    return ownerUserId;
  }

  public void setOwnerUserId(String ownerUserId) {
    this.ownerUserId = ownerUserId;
  }

  public String getTags() {
    return tags;
  }

  public void setTags(String tags) {
    this.tags = tags;
  }

}

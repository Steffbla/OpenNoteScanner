package com.todobom.opennotescanner.network.models.dracoon;

public class CreateShareUploadChannelResponse {

  private String uploadId;
  private String uploadUrl;
  private String token;

  public CreateShareUploadChannelResponse(String uploadId, String uploadUrl, String token) {
    this.uploadId = uploadId;
    this.uploadUrl = uploadUrl;
    this.token = token;
  }

  public String getUploadId() {
    return uploadId;
  }

  public void setUploadId(String uploadId) {
    this.uploadId = uploadId;
  }

  public String getUploadUrl() {
    return uploadUrl;
  }

  public void setUploadUrl(String uploadUrl) {
    this.uploadUrl = uploadUrl;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}

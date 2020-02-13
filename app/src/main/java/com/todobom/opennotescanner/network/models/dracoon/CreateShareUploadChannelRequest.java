package com.todobom.opennotescanner.network.models.dracoon;

public class CreateShareUploadChannelRequest {

  private String name;
  private long size;
  private String password;
  private boolean directS3Upload;

  public CreateShareUploadChannelRequest(String name, long size, String password,
      boolean directS3Upload) {
    this.name = name;
    this.size = size;
    this.password = password;
    this.directS3Upload = directS3Upload;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isDirectS3Upload() {
    return directS3Upload;
  }

  public void setDirectS3Upload(boolean directS3Upload) {
    this.directS3Upload = directS3Upload;
  }
}

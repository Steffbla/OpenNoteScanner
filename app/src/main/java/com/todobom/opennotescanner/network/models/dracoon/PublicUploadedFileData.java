package com.todobom.opennotescanner.network.models.dracoon;

import java.util.Date;

public class PublicUploadedFileData {

  private String name;
  private long size;
  private Date createdAt;
  private String hash;

  public PublicUploadedFileData(String name, long size, Date createdAt, String hash) {
    this.name = name;
    this.size = size;
    this.createdAt = createdAt;
    this.hash = hash;
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

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }
}

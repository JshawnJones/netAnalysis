package com.jshawn.netanalysis.secureweb;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity 
public class users {
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Integer id;
  
  private String username;

  private String pass;
  
  private String role;
  
  public Integer getId() {
	  return id;
  }

  public void setId(Integer id) {
	  this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPass() {
    return pass;
  }

  public void setPass(String pass) {
    this.pass = pass;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }
  
}
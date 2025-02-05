package com.smart.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="CONTACT")
public class Contact {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cId;
	
	@NotBlank(message = "Name should be not Empty..!!")
	@Size(min=3,max=25,message = "Characters should be between 3 - 10")
	private String name;
	
	@NotBlank(message = "Nick Name not be Empty..! Please fill ")
	private String secondName;
	
	@NotBlank(message = "work hould be not empty..!!!")
	private String work;
	
	@Email(regexp = "^(.+)@(\\S+)$",message = "invalid Email .!")
	private String email;
	
	@NotBlank(message = "phone number must be fill..!!!")
	@Size(max=10,message = "Numbers should be 10 numbers only..!!!")
	private String phone;
	
	private String image;
	
	@Column(length = 1000)
	private String description;
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return this.cId==((Contact)obj).getcId();
	}

	@ManyToOne
	@JsonIgnore
	private User user;
	
	
	
	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public int getcId() {
		return cId;
	}


	public void setcId(int cId) {
		this.cId = cId;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getSecondName() {
		return secondName;
	}


	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}


	public String getWork() {
		return work;
	}


	public void setWork(String work) {
		this.work = work;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public String getImage() {
		return image;
	}


	public void setImage(String image) {
		this.image = image;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public Contact() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}

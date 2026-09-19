package com.practice.samplespringboot.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @Column(name = "customer_id")
    private String customerId;

    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate dateOfBirth;
    private Integer age;
    private String email;
    private String phoneNumber;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String occupation;
    private BigDecimal annualIncome;
    private String maritalStatus;
    private String educationLevel;
    private String employmentStatus;
    private LocalDate customerSince;
    private String customerSegment;
    private String kycStatus;
    private String riskRating; // LOW, MEDIUM, HIGH
    private Integer isPoliticallyExposed;
    private String preferredChannel;
    private String emailVerified;
    private String phoneVerified;
    private Integer numComplaintsLastYear;

    public Customer() {}

    // Getters and Setters
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }

    public BigDecimal getAnnualIncome() { return annualIncome; }
    public void setAnnualIncome(BigDecimal annualIncome) { this.annualIncome = annualIncome; }

    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }

    public String getEducationLevel() { return educationLevel; }
    public void setEducationLevel(String educationLevel) { this.educationLevel = educationLevel; }

    public String getEmploymentStatus() { return employmentStatus; }
    public void setEmploymentStatus(String employmentStatus) { this.employmentStatus = employmentStatus; }

    public LocalDate getCustomerSince() { return customerSince; }
    public void setCustomerSince(LocalDate customerSince) { this.customerSince = customerSince; }

    public String getCustomerSegment() { return customerSegment; }
    public void setCustomerSegment(String customerSegment) { this.customerSegment = customerSegment; }

    public String getKycStatus() { return kycStatus; }
    public void setKycStatus(String kycStatus) { this.kycStatus = kycStatus; }

    public String getRiskRating() { return riskRating; }
    public void setRiskRating(String riskRating) { this.riskRating = riskRating; }

    public Integer getIsPoliticallyExposed() { return isPoliticallyExposed; }
    public void setIsPoliticallyExposed(Integer isPoliticallyExposed) { this.isPoliticallyExposed = isPoliticallyExposed; }

    public String getPreferredChannel() { return preferredChannel; }
    public void setPreferredChannel(String preferredChannel) { this.preferredChannel = preferredChannel; }

    public String getEmailVerified() { return emailVerified; }
    public void setEmailVerified(String emailVerified) { this.emailVerified = emailVerified; }

    public String getPhoneVerified() { return phoneVerified; }
    public void setPhoneVerified(String phoneVerified) { this.phoneVerified = phoneVerified; }

    public Integer getNumComplaintsLastYear() { return numComplaintsLastYear; }
    public void setNumComplaintsLastYear(Integer numComplaintsLastYear) { this.numComplaintsLastYear = numComplaintsLastYear; }
}

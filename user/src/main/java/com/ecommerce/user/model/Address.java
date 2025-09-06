package com.ecommerce.user.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Address {

    @Id
    private String id;
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipcode;
}

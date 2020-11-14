package com.example.accessingdatamysql;

import javax.persistence.*;


@Entity
public class Car {

    public void setId(Integer id) {
        this.id = id;
    }

    @Id
    @Column(name = "user_id")
    private Integer id;

    /*@OneToOne(mappedBy = "car")
    private User user;*/

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }



    public Car(){




    }


    public void setModel(String model) {
        this.model = model;
    }

    private String brand;
    private String model;
    private String serial;
    private String pic;

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }





    public Integer getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public String getBrand() {
        return brand;
    }



}

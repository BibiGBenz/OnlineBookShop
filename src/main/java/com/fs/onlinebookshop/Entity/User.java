package com.fs.onlinebookshop.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Scope;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String userName;
    @Email
    @Column(unique = true,nullable = false)
    @Pattern(regexp = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")
    private String email;
    @Column(unique = true,length = 10)
//    @Pattern(regexp = "^[6-9]\\d{9}$")
//    @Size(min = 10,max = 10)
    private long phoneNumber;
    private String address;
//    @Column(nullable = false)
//    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\\\d).*$")
    private String password;
    private String roles;



    @JsonIgnore
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Booking> bookings;
    @JsonIgnore
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Token> tokens;

}

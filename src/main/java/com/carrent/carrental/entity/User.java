package com.carrent.carrental.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.carrent.carrental.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 15) // just length restriction, validation will be in DTO
    private String contactNo;

    @Column(nullable = false, length = 200)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();// initialized

    // Used by Spring Security to decide if the user is authorized to access a
    // resource.

    // userdetails implements ----------------------------------

    // Returns the roles/permissions of the user.
    // Used by Spring Security to decide if the user is authorized to access a
    // resource.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userRole.name()));
    }

    // Returns the user’s username (unique identifier).
    @Override
    public String getUsername() {
        return email;
    }

    // Returns true if the account is not expired.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Returns true if the account is not locked.
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Returns true if the password is still valid.
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Returns true if the account is active.
    @Override
    public boolean isEnabled() {
        return true;
    }

}

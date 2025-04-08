package org.ntnu.idi.idatt2105.fant.org.fantorg.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Represents a user of the system, encapsulating all personal and authentication details.
 * This class implements the {@link UserDetails} interface from Spring Security to integrate
 * authentication and authorization functionalities seamlessly.
 *
 * @author Harry Xu
 * @since 1.0
 */
@Builder
@NoArgsConstructor
@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "\"USER\"")
public class User implements UserDetails {

    /**
     * The unique identifier for the user, automatically generated by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    /**
     * First name of the user; cannot be null.
     */
    @NotNull
    @Column(nullable = false)
    private String firstName;

    /**
     * Last name of the user; cannot be null.
     */
    @NotNull
    @Column(nullable = false)
    private String lastName;


    /**
     * Encrypted password for user authentication; cannot be null.
     */
    @NotNull
    @Column(nullable = false)
    private String password;

    /**
     * User's email address, must be valid as per the @Email constraint; unique across users.
     */
    @NotNull
    @Column(nullable = false, unique = true)
    @Email
    private String email;

    private String address;

    private String number;

    /**
     * A user's role. See {@link Role}
     */
    @NotNull
    @Column(nullable = false)
    @Enumerated
    private Role role;

    /**
     * Granted authorities for user
     * @return List of granted authorities given the role of the user.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
    @OneToMany(mappedBy = "seller", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Item> items;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Bookmark> bookmarks = new HashSet<>();
    @OneToOne
    @JoinColumn(name = "profile_image_id")
    private Image profileImage;
    // Let email behave like username
    @Override
    public String getUsername() {
        return this.email; //We would rather
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPassword(){
        return password;
    }
}

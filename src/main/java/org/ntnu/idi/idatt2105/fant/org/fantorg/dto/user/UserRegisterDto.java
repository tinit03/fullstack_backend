package org.ntnu.idi.idatt2105.fant.org.fantorg.dto.user;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRegisterDto {
  private String email;
  private String firstName;
  private String lastName;
  private String password;
  private String address;
  private String phoneNumber; //Maybe we should make it optional for the user...
}

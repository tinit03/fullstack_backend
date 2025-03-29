package org.ntnu.idi.idatt2105.fant.org.fantorg.controller;


import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ntnu.idi.idatt2105.fant.org.fantorg.model.User;
import org.ntnu.idi.idatt2105.fant.org.fantorg.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor
public class UserController {

  private final UserRepository repository;

  @GetMapping("/users")
  public ResponseEntity<List<User>> findConnectedUsers() {
    return ResponseEntity.ok(repository.findAll());
  }
}
package com.clebsonsantos.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.clebsonsantos.todolist.user.IUserRepository;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

  @Autowired
  private IUserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    var authorization = request.getHeader("Authorization");
    var base64Encoder = authorization.substring("Basic".length()).trim();

    var authDecode = Base64.getDecoder().decode(base64Encoder);
    var auth = new String(authDecode);
    var credentials = auth.split(":");
    var username = credentials[0];
    var password = credentials[0];

    var user = this.userRepository.findByUsername(username);

    if (user == null) {
      response.sendError(401, "User doesn't authorized");
      return;
    }

    var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());

    if (!passwordVerify.verified) {
      response.sendError(401);
    }

    filterChain.doFilter(request, response);

  }

}
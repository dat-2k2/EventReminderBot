package utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;
import repos.UserRepository;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {
    private JwtTokenizer jwtTokenizer;
    private UserRepository userRepository;
    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // get header which specifies authorization
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header.isEmpty() || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Get jwt token and validate
        final String token = header.split(" ")[1].trim();
        if (!jwtTokenizer.validate(token)) {
            filterChain.doFilter(request, response);
            return;
        }

//;
    }
}

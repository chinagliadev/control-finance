package dev.chinaglia.control_finance.controllers;

import java.net.URI;
import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import dev.chinaglia.control_finance.config.TokenConfig;
import dev.chinaglia.control_finance.dto.request.LoginRequest;
import dev.chinaglia.control_finance.dto.request.RegistrarUsuarioRequest;
import dev.chinaglia.control_finance.dto.response.RegistrarUsuarioResponse;
import dev.chinaglia.control_finance.dto.response.UsuarioDTO;
import dev.chinaglia.control_finance.entitdades.Usuario;
import dev.chinaglia.control_finance.repository.UsuarioRepository;
import dev.chinaglia.control_finance.response.ApiResponse;
import dev.chinaglia.control_finance.response.ResponseUtil;
import dev.chinaglia.control_finance.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Endpoints responsáveis pela autenticação e gerenciamento da sessão do usuário.")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;
    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;

    public AuthController(AuthenticationManager authenticationManager, TokenConfig tokenConfig, AuthService authService, UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenConfig = tokenConfig;
        this.authService = authService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    @Operation(summary = "Realizar login", description = "Autentica o usuário utilizando e-mail e senha e cria um cookie de autenticação contendo o token JWT.")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {

        var authToken = new UsernamePasswordAuthenticationToken(request.email(), request.senha());
        authenticationManager.authenticate(authToken);

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow();

        String token = tokenConfig.generateToken(usuario);

        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ofHours(24))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/registrar")
    @Operation(summary = "Registrar usuário", description = "Realiza o cadastro de um novo usuário no sistema.")
    public ResponseEntity<RegistrarUsuarioResponse> login(@Valid @RequestBody RegistrarUsuarioRequest registrarUsuarioRequest) {

        RegistrarUsuarioResponse registrarUsuarioResponse = authService.registrar(registrarUsuarioRequest);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(registrarUsuarioResponse.id()).toUri();

        return ResponseEntity.created(location).body(registrarUsuarioResponse);
    }

    @GetMapping("/me")
    @Operation(summary = "Buscar usuário autenticado", description = "Retorna os dados do usuário atualmente autenticado.")
    public ResponseEntity<ApiResponse<UsuarioDTO>> usuario(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();

        return ResponseEntity.ok(ResponseUtil.sucesso(new UsuarioDTO(usuario.getNome(), usuario.getEmail()), "Usuario encontrado", ""));
    }

    @PostMapping("/logout")
    @Operation(summary = "Realizar logout", description = "Encerra a sessão do usuário removendo o cookie de autenticação.")
    public ResponseEntity<?> logout(HttpServletResponse response) {

        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ZERO)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok().build();
    }
}
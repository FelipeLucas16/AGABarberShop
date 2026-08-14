package com.devffl.babershop.security.config;

import com.devffl.babershop.dto.ProdutoDto;
import com.devffl.babershop.entities.Role;
import com.devffl.babershop.entities.User;
import com.devffl.babershop.enums.RoleName;
import com.devffl.babershop.repositories.UserRepository;
import com.devffl.babershop.security.authentication.JwtTokenService;
import com.devffl.babershop.security.userDetails.UserDetailsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testa de ponta a ponta (JwtTokenService real + UserAuthenticationFilter real + regras
 * de authorizeHttpRequests) que cada papel só acessa o que a SecurityConfiguration permite.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SecurityConfigurationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenService jwtTokenService;
    @Autowired
    private ObjectMapper objectMapper;

    private String tokenComum;
    private String tokenAdmin;

    @BeforeEach
    void setUp() {
        User comum = userRepository.save(User.builder()
                .email("comum-it@teste.com")
                .password(passwordEncoder.encode("123456"))
                .nome("Comum IT")
                .telefone("11999999999")
                .roles(List.of(Role.builder().name(RoleName.ROLE_COMUM).build()))
                .build());

        User admin = userRepository.save(User.builder()
                .email("admin-it@teste.com")
                .password(passwordEncoder.encode("123456"))
                .nome("Admin IT")
                .telefone("11999999999")
                .roles(List.of(Role.builder().name(RoleName.ROLE_ADMINISTRADOR).build()))
                .build());

        tokenComum = jwtTokenService.generateToken(new UserDetailsImpl(comum));
        tokenAdmin = jwtTokenService.generateToken(new UserDetailsImpl(admin));
    }

    @Test
    void comumNaoPodeCriarProduto() throws Exception {
        mockMvc.perform(post("/produtos")
                        .header("Authorization", "Bearer " + tokenComum)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produtoDto())))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminPodeCriarProduto() throws Exception {
        mockMvc.perform(post("/produtos")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(produtoDto())))
                .andExpect(status().isOk());
    }

    @Test
    void comumConsegueListarProdutos() throws Exception {
        mockMvc.perform(get("/produtos")
                        .header("Authorization", "Bearer " + tokenComum))
                .andExpect(status().isOk());
    }

    @Test
    void comumNaoAcessaOrdemServico() throws Exception {
        mockMvc.perform(get("/ordemservico")
                        .header("Authorization", "Bearer " + tokenComum))
                .andExpect(status().isForbidden());
    }

    @Test
    void semTokenRetorna401EmEndpointProtegido() throws Exception {
        mockMvc.perform(get("/produtos"))
                .andExpect(status().isUnauthorized());
    }

    private ProdutoDto produtoDto() {
        return ProdutoDto.builder().nome("Pomada").preco(30.0).descricao("Pomada modeladora").build();
    }
}

package oficinaflow;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import oficinaflow.web.LoginController;
import oficinaflow.web.LoginFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LoginController.class)
@Import(LoginFilter.class)
class LoginTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void semLoginAPaginaRedirecionaParaATelaDeLogin() throws Exception {
        mvc.perform(get("/os")).andExpect(redirectedUrl("/login"));
    }

    @Test
    void senhaErradaNaoEntra() throws Exception {
        mvc.perform(post("/login").param("usuario", "utfpr").param("senha", "errada"))
            .andExpect(redirectedUrl("/login"));
    }

    @Test
    void senhaCertaLiberaAsPaginas() throws Exception {
        MockHttpSession sessao = (MockHttpSession) mvc.perform(post("/login").param("usuario", "utfpr").param("senha", "12345"))
            .andExpect(redirectedUrl("/os")).andReturn().getRequest().getSession();
        // 404 aqui prova que o filtro deixou passar (a página /os não existe neste teste de fatia)
        mvc.perform(get("/os").session(sessao)).andExpect(status().isNotFound());
        mvc.perform(post("/logout").session(sessao)).andExpect(redirectedUrl("/login"));
    }
}
